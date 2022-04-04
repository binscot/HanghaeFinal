package com.example.hanghaefinal.service;

import com.example.hanghaefinal.dto.requestDto.CategoryRequestDto;
import com.example.hanghaefinal.dto.requestDto.PostContinueReqDto;
import com.example.hanghaefinal.dto.requestDto.PostRequestDto;
import com.example.hanghaefinal.dto.responseDto.*;
import com.example.hanghaefinal.exception.exception.ContentNullException;
import com.example.hanghaefinal.exception.exception.PostNotFoundException;
import com.example.hanghaefinal.exception.exception.UserNotFoundException;
import com.example.hanghaefinal.model.*;
import com.example.hanghaefinal.repository.*;
import com.example.hanghaefinal.security.UserDetailsImpl;
import com.example.hanghaefinal.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final PostLikesRepository postLikesRepository;
    private final CommentRepository commentRepository;
    private final CommentLikesRepository commentLikesRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ParagraphRepository paragraphRepository;
    private final ParagraphLikesRepository paragraphLikesRepository;
    private final BookmarkRepository bookmarkRepository;
    private final AlarmService alarmService;
    private final S3Uploader s3Uploader;
    private final AlarmRepository alarmRepository;
    private final LevelService levelService;

    public String uploadImageFile(MultipartFile multipartFile, PostRequestDto requestDto) throws IOException {
        //String originalFileName = multipartFile.getOriginalFilename();
        //String convertedFileName = UUID.randomUUID() + originalFileName;
        //requestDto.setImageUrl(convertedFileName);
        //s3Uploader.upload(multipartFile, convertedFileName);

        String defaultImg = "https://binscot-bucket.s3.ap-northeast-2.amazonaws.com/default/Rectangle+6141.png";
        if (!Objects.equals(multipartFile.getOriginalFilename(), "foo.txt"))
            defaultImg = s3Uploader.upload(multipartFile, "post");
        //String uploadUrl =  s3Uploader.upload(multipartFile, dirName);
        //requestDto.setPostImageUrl(defaultImg);
        //requestDto.setPostImageUrl(uploadUrl);
        return defaultImg;
    }

    // 게시글 최초 생성 -> 미완성 게시글 생성
    @Transactional
    public Boolean savePost(PostRequestDto postRequestDto, User user, String defaultImg, BindingResult bindingResult){

        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException(
                    Objects.requireNonNull(bindingResult.getFieldError()
                    ).getDefaultMessage());
        }

        if(postRequestDto.getTitle().equals("null")){
            throw new ContentNullException("제목을 입력해주세요.");
        }
        if(postRequestDto.getParagraph().equals("null")){
            throw new ContentNullException("문단을 입력해주세요.");
        }

        Post post = new Post(postRequestDto, user, defaultImg);
        Category category = new Category(postRequestDto.getCategory(), post);
        Paragraph paragraph = new Paragraph(postRequestDto.getParagraph(), user, post);
        postRepository.save(post);
        categoryRepository.save(category);
        paragraphRepository.save(paragraph);

        //포인트 추가
        int userPoint = user.getPoint()+3;
        user.updatePoint(userPoint);
        userRepository.save(user);

        return true;
    }

    // 마지막 파라그래프 작성 후 게시글 완성 버튼 누름 -> 완성 게시글로 변경 ( 완성 게시글 상세 조회)
    @Transactional
    public PostDetailResponseDto completePost(Long postId, CategoryRequestDto categoryRequestDto, UserDetailsImpl userDetails){
        log.info("-------------------테스트로그2---------------------");
        log.info("----------- 카테고리 로그 : " + categoryRequestDto.getCategory());
        /*User user = userRepository.findById(userDetails.getUser().getId()).orElseThrow(
                () -> new UserNotFoundException("존재하지 않는 ID 입니다.")
        );*/

        if(userDetails == null){
            throw new UserNotFoundException("존재하지 않는 ID 입니다.");
        }
        User user = userDetails.getUser();

        Post post = postRepository.findById(postId).orElseThrow(
                () -> new PostNotFoundException("게시물이 존재하지 않습니다.")
        );
/*        int paragraphCnt = paragraphRepository.findAllByPostId(postId).size();

        if(post.getLimitCnt() != paragraphCnt ){
            throw new IllegalArgumentException("문단 작성이 완료되지 않았습니다.");
        }*/

        // 마지막 문단 작성자가 카테고리를 생성하면 새로운 카테고리 등록, category가 있으면 카테고리를 생성
        if(categoryRequestDto.getCategory() != null){
            Category category = new Category(categoryRequestDto.getCategory(), post);
            categoryRepository.save(category);
        } else {
            log.info("~~~ category is null");
        }

        // 백에서도 검사 해주려면 requestDto에서 limitCnt 값을 받아야 한다.
        // if(post.getLimitCnt() == requestDto에서 받아온 limitCnt)

        // 어차피 true지만  postRequestDto.isComplete() 이걸 인자로 넣어도 된다.
        post.updatePostComplete(true);
        //postRepository.save(post);

        List<PostLikes> postLikes = postLikesRepository.findAllByPostId(postId);
        List<PostLikeClickersResponseDto> postLikeClickersResponseDtoList = new ArrayList<>();
        for (PostLikes postLikesTemp : postLikes) {
            postLikeClickersResponseDtoList.add(new PostLikeClickersResponseDto(postLikesTemp));
        }

        List<Bookmark> bookmarkList = bookmarkRepository.findAllByPostId(postId);
        List<BookmarkClickUserKeyResDto> bookmarkClickUserKeyResDtoList = new ArrayList<>();

        for (Bookmark bookmark:bookmarkList){
            bookmarkClickUserKeyResDtoList.add(new BookmarkClickUserKeyResDto(bookmark));
        }


        List<Paragraph> paragraphList = paragraphRepository.findAllByPostId(postId);
        List<ParagraphResDto> paragraphResDtoList = new ArrayList<>();

        for(Paragraph paragraph: paragraphList){
            //UserInfoResponseDto userInfoResDto = new UserInfoResponseDto(paragraph.getUser());
            //paragraphResDtoList.add(new ParagraphResDto(paragraph, userInfoResDto));
            Long paragraphLikesCnt = paragraphLikesRepository.countByParagraph(paragraph);
            Long paragraphKey = paragraph.getId();

            List<ParagraphLikes> paragraphLikes = paragraphLikesRepository.findAllByParagraphId(paragraphKey);
            List<ParagraphLikesClickUserKeyResDto> paragraphLikesClickUserKeyResDtoList = new ArrayList<>();
            for(ParagraphLikes paragraphLikesTemp : paragraphLikes){
                paragraphLikesClickUserKeyResDtoList.add(new ParagraphLikesClickUserKeyResDto(paragraphLikesTemp));
            }

            paragraphResDtoList.add(new ParagraphResDto(paragraph, paragraphLikesClickUserKeyResDtoList, paragraphLikesCnt));
        }

        List<Category> categoryList = categoryRepository.findAllByPostIdOrderByModifiedAtDesc(postId);
        List<CategoryResponseDto> categoryResDtoList = new ArrayList<>();

        // List<Category>에 있는 정보를 List<CategoryResponseDto> 에 담는다.
        for(Category category: categoryList){
            categoryResDtoList.add(new CategoryResponseDto(category));
        }

        List<Comment> commentList = commentRepository.findAllByPostIdOrderByModifiedAt(postId);
        List<CommentResponseDto> commentResDtoList = new ArrayList<>();

        // List<Comment>에 있는 정보를 List<CommentResponseDto> 에 담는다
        for (Comment comment:commentList ) {
            Long commentLikesCnt = commentLikesRepository.countByComment(comment);

//            List<CommentLikes> commentLikesList = commentLikesRepository.findAllByCommentId(comment.getId());
//            List<CommentLikeClickersResponseDto> commentLikeClickersResponseDtoList = new ArrayList<>();
//            for(CommentLikes commentLikesTemp : commentLikesList){
//                commentLikeClickersResponseDtoList.add(new CommentLikeClickersResponseDto(commentLikesTemp));
//            }

            commentResDtoList.add(new CommentResponseDto(comment, commentLikesCnt));
        }

        Long postLikesCnt =  postLikesRepository.countByPost(post);

        String postUsername = null;
        if (post.getUser() != null) {
            postUsername = post.getUser().getUsername();
        }

        log.info("---------------------- 222222aaaa ----------------------");
        // 게시글이 완성 되었을 때 알림을 보낸다
        alarmService.generateCompletePostAlarm(user, post);

        //return new PostDetailResponseDto(post, paragraphResDtoList, commentResDtoList, categoryResDtoList, postLikesCnt,postUsername);
        return new PostDetailResponseDto(post, postLikeClickersResponseDtoList, bookmarkClickUserKeyResDtoList,
                paragraphResDtoList, commentResDtoList, categoryResDtoList, postLikesCnt, postUsername);
    }

    public Boolean continuePost(Long postId, PostContinueReqDto postContinueReqDto, UserDetailsImpl userDetails){
        User user = userRepository.findById(userDetails.getUser().getId()).orElseThrow(
                () -> new UserNotFoundException("존재하지 않는 ID 입니다.")
        );

        Post post = postRepository.findById(postId).orElseThrow(
                () -> new PostNotFoundException("게시물이 존재하지 않습니다.")
        );

        // 게시글 최초 생성자와 'GO'를 누른 사람이 다르면 false를 반환
        if(!Objects.equals(user.getId(), post.getUser().getId())){
            return false;
        }

        // 기존 limitCnt 에 추가할만큼의 문단 수를 더한다.
        post.updateLimitCnt(post.getLimitCnt() + postContinueReqDto.getAddParagraphSize());
        post.updatePostComplete(false); // 계속 작성중인 게시글로 변환
        postRepository.save(post);

        alarmService.generateGoAlarm(post, user);

        return true;
    }

    // 게시글 상세조회 ( 완성, 미완성 둘다)
    public PostDetailResponseDto viewPostDetail(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new PostNotFoundException("게시물이 존재하지 않습니다.")
        );
        // commentList 조회
        //List<Comment> commentList = commentRepository.findAllByPostIdOrderByModifiedAtDesc(postId);
        // 위처럼 commentList로 조회하면 안된다. Comment안에 user랑 post있고 post안에 user 가 있다.. (궁금하면 다시 해봐)

        List<PostLikes> postLikes = postLikesRepository.findAllByPostId(postId);
        List<PostLikeClickersResponseDto> postLikeClickersResponseDtoList = new ArrayList<>();
        for (PostLikes postLikesTemp : postLikes) {
            postLikeClickersResponseDtoList.add(new PostLikeClickersResponseDto(postLikesTemp));
        }

        List<Bookmark> bookmarkList = bookmarkRepository.findAllByPostId(postId);
        List<BookmarkClickUserKeyResDto> bookmarkClickUserKeyResDtoList = new ArrayList<>();
        for (Bookmark bookmark:bookmarkList){
            bookmarkClickUserKeyResDtoList.add(new BookmarkClickUserKeyResDto(bookmark));
        }


        List<Paragraph> paragraphList = paragraphRepository.findAllByPostId(postId);
        List<ParagraphResDto> paragraphResDtoList = new ArrayList<>();
/*
        // Paragraph, ParagraphLikesList
        HashMap<Object, Paragraph> map = new HashMap<>();
        // ParagraphLikes, userPkList
        HashMap<Object, Object> map2 = new HashMap<>();

        List<ParagraphLikes> paragraphLikesList = new ArrayList<>();

        // 유저를 가져오는 repository에서 fetch join을 잘 걸어라
        for(int i = 0; i < paragraphList.size(); i++){
            //map.put(String.valueOf(i), paragraphList.get(i).getId());
            //map.put(String.valueOf(i), paragraphList.get(i).getParagraphLikesList());
            paragraphLikesList = paragraphList.get(i).getParagraphLikesList();
            //map.put(paragraphList.get(i), paragraphLikesList);
            map.put(i, paragraphList.get(i));
            map2.put(i, paragraphLikesList);
        }
        System.out.println("------------------------ paragraphLikesList : " + paragraphLikesList.size());

        List<ParagraphLikesClickUserKeyResDto> paragraphLikesClickUserKeyResDtoList = new ArrayList<>();
        for(int k = 0; k < paragraphLikesList.size(); k++){
//            map2.put(paragraphLikesList.get(i), paragraphLikesList.get(i).getUser().getId());
            //map2.put(k, paragraphLikesList.get(k));
            paragraphLikesClickUserKeyResDtoList.add(new ParagraphLikesClickUserKeyResDto(paragraphLikesList.get(k)));
        }

        // 이게 지금 paragraphLikesClickUserKeyResDtoList 는 마지막것만 넣어주네..
        for(int i = 0; i < paragraphList.size(); i++){
            paragraphResDtoList.add(new ParagraphResDto( map.get(i), paragraphLikesClickUserKeyResDtoList, paragraphList.get(i).getParagraphLikesList().size() ));
        }*/

        /*for( Object key : map.keySet() ){
            System.out.println( String.format("키 : %s, 값 : %s", key, map.get(key)) );
            paragraphResDtoList.add(new ParagraphResDto( key , map.get(key) ) );
        }

        HashMap<String, Long> map2 = new HashMap<>();
        for(int i = 0; i < paragraphList.size(); i++){
            List<ParagraphLikes> paragraphLikes = paragraphLikesRepository.findAllByParagraphId(map.get(String.valueOf(i)));
            map2.put(String.valueOf(i), paragraphLikes.get(i).getUser().getId());

        }*/

        for (Paragraph paragraph : paragraphList) {
            // a. 이거는 UserInfoResponseDto를 여기서 생성해서 ParagraphResDto로 가져가고
//            UserInfoResponseDto userInfoResDto = new UserInfoResponseDto(paragraph.getUser());
//            paragraphResDtoList.add(new ParagraphResDto(paragraph, userInfoResDto));

            // b. 이거는 UserInfoResponseDto를 ParagraphResDto로 가서 생성한다. ㅇㅇ
            // 각 문단에 좋아요를 한 userKey(PK)의 리스트
            Long paragraphLikesCnt = paragraphLikesRepository.countByParagraph(paragraph);
            Long paragraphKey = paragraph.getId();

            List<ParagraphLikes> paragraphLikes = paragraphLikesRepository.findAllByParagraphId(paragraphKey);  // 이거 fetch join 해봐
            List<ParagraphLikesClickUserKeyResDto> paragraphLikesClickUserKeyResDtoList = new ArrayList<>();
            for(ParagraphLikes paragraphLikesTemp : paragraphLikes){
                paragraphLikesClickUserKeyResDtoList.add(new ParagraphLikesClickUserKeyResDto(paragraphLikesTemp));
            }

            paragraphResDtoList.add(new ParagraphResDto(paragraph, paragraphLikesClickUserKeyResDtoList, paragraphLikesCnt));
        }

        List<Category> categoryList = categoryRepository.findAllByPostIdOrderByModifiedAtDesc(postId);
        List<CategoryResponseDto> categoryResDtoList = new ArrayList<>();

        // List<Category>에 있는 정보를 List<CategoryResponseDto> 에 담는다.
        for (Category category : categoryList) {
            categoryResDtoList.add(new CategoryResponseDto(category));
        }

        List<Comment> commentList = commentRepository.findAllByPostIdOrderByModifiedAt(postId);
        List<CommentResponseDto> commentResDtoList = new ArrayList<>();

        // List<Comment>를 각각 List<CommentResponseDto> 에 담는다
        for (Comment comment : commentList) {
            Long commentLikesCnt = commentLikesRepository.countByComment(comment);

//            List<CommentLikes> commentLikesList = commentLikesRepository.findAllByCommentId(comment.getId());
//            List<CommentLikeClickersResponseDto> commentLikeClickersResponseDtoList = new ArrayList<>();
//            for(CommentLikes commentLikesTemp : commentLikesList){
//                commentLikeClickersResponseDtoList.add(new CommentLikeClickersResponseDto(commentLikesTemp));
//            }

            commentResDtoList.add(new CommentResponseDto(comment, commentLikesCnt));
        }

        // postLikes 조회
        //List<PostLikes> postLikesList = postLikesRepository.findAllByPostId(post.getId());
        // postId는 이미 알고 있으니까 totalCnt만 주면된다.
        Long postLikesCnt = postLikesRepository.countByPost(post);
        // paragraph를 작성한 유저와 좋아요
        // comment를 작성한 유저와 좋아요가 필요하다.

        // limitCnt와 paragraph의 개수가 같으면 complete를 true로 반환해라

        String postUsername = null;
        if (post.getUser() != null) {
            postUsername = post.getUser().getUsername();
        }

        log.info("-----------------------PostDetailResponseDto---------:"+post.isWriting());
        return new PostDetailResponseDto(post, postLikeClickersResponseDtoList, bookmarkClickUserKeyResDtoList,
                paragraphResDtoList, commentResDtoList, categoryResDtoList, postLikesCnt, postUsername);
    }

    // 완성작 게시글 전체 조회 - 최신순
    public List<PostResponseDto> viewPostRecent(int page, int size){
        //List<Post> posts = postRepository.findAllByOrderByModifiedAtDesc();
        // complete 가 true이며(완성작) 최근 수정한 시간순으로 불러온다.
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.findAllByCompleteTrueOrderByModifiedAtDesc(pageable);

        return viewPostList(posts);
    }

    // 완성작 게시글 전체 조회 - 추천순(좋아요순)
    public List<PostResponseDto> viewPostRecommend(int page, int size){
        List<PostResponseDto> postResponseDtoList = new ArrayList<>();
        //List<Post> posts = postRepository.findAllByOrderByModifiedAtDesc();
        // complete 가 true이며(완성작) 최근 수정한 시간순으로 불러온다.
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.findAllByCompleteTrueOrderByModifiedAtDesc(pageable);

        postResponseDtoList = viewPostList(posts);

        // post좋아요 순으로 내림차순 정렬(좋아요 많은게 위에 보이게끔)
        Comparator<PostResponseDto> comparator = Comparator.comparing(PostResponseDto::getPostLikesCnt, Comparator.reverseOrder());
        List<PostResponseDto> responseDtoList = postResponseDtoList.stream().sorted(comparator).collect(Collectors.toList());
        // 결과 출력
        /*postResponseDtoList.stream().sorted(comparator)
                .forEach(o -> {
                    System.out.println("~~~ o.getPostLikesCnt() : " + o.getPostLikesCnt());
                });
        System.out.println("-----------------------------------------------");*/

        return responseDtoList;
        //return postResponseDtoList;
    }


    // 미완성 게시글 전체 조회 - 최신순
    public List<PostResponseDto> viewPostIncomplete(int page, int size){
        List<PostResponseDto> postResponseDtoList = new ArrayList<>();
        //List<Post> posts = postRepository.findAllByOrderByModifiedAtDesc();
        // complete 가 false이며(미완성작품) 최근 수정한 시간순으로 불러온다.
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.findAllByCompleteFalseOrderByModifiedAtDesc(pageable);

        postResponseDtoList = viewPostList(posts);

        return postResponseDtoList;
    }

    // 메인에 보여줄 즐겨찾기 많은 순 & 댓글 있고 & 완성작 top3
    public List<PostResponseDto> viewPostMain(){

        List<PostResponseDto> postResponseDtoList = new ArrayList<>();
        List<Post> posts = postRepository.findAll();

        int postLikeCnt = 0;
        for (Post post: posts ) {
            List<PostLikes> postLikesList = postLikesRepository.findAllByPostId(post.getId());
            List<PostLikeClickersResponseDto> postLikeClickersResponseDtoList = new ArrayList<>();
            for (PostLikes postLikesTemp : postLikesList) {
                postLikeClickersResponseDtoList.add(new PostLikeClickersResponseDto(postLikesTemp));
            }
            postLikeCnt = postLikesList.size();

            List<Bookmark> bookmarkList = bookmarkRepository.findAllByPostId(post.getId());
            List<BookmarkClickUserKeyResDto> bookmarkClickUserKeyResDtoList = new ArrayList<>();

            for (Bookmark bookmark:bookmarkList){
                bookmarkClickUserKeyResDtoList.add(new BookmarkClickUserKeyResDto(bookmark));
            }

            List<Paragraph> paragraphList = paragraphRepository.findAllByPostId(post.getId());
            List<ParagraphResDto> paragraphResDtoList = new ArrayList<>();

            for(Paragraph paragraph: paragraphList){
                Long paragraphLikesCnt = paragraphLikesRepository.countByParagraph(paragraph);
                Long paragraphKey = paragraph.getId();

                List<ParagraphLikes> paragraphLikes = paragraphLikesRepository.findAllByParagraphId(paragraphKey);
                List<ParagraphLikesClickUserKeyResDto> paragraphLikesClickUserKeyResDtoList = new ArrayList<>();
                for(ParagraphLikes paragraphLikesTemp : paragraphLikes){
                    // paragraphLikesRepository.findAllByParagraphId(paragraphKey);
                    paragraphLikesClickUserKeyResDtoList.add(new ParagraphLikesClickUserKeyResDto(paragraphLikesTemp));
                }

                paragraphResDtoList.add(new ParagraphResDto(paragraph, paragraphLikesClickUserKeyResDtoList, paragraphLikesCnt));
            }

            List<Comment> commentList = commentRepository.findAllByPostIdOrderByModifiedAt(post.getId());
            List<CommentResponseDto> commentResDtoList = new ArrayList<>();

            // List<Comment>를 각각 List<CommentResponseDto> 에 담는다
            for (Comment comment:commentList ) {
                Long commentLikesCnt = commentLikesRepository.countByComment(comment);

//                List<CommentLikes> commentLikesList = commentLikesRepository.findAllByCommentId(comment.getId());
//                List<CommentLikeClickersResponseDto> commentLikeClickersResponseDtoList = new ArrayList<>();
//                for(CommentLikes commentLikesTemp : commentLikesList){
//                    commentLikeClickersResponseDtoList.add(new CommentLikeClickersResponseDto(commentLikesTemp));
//                }

                commentResDtoList.add(new CommentResponseDto(comment, commentLikesCnt));
            }

            List<Category> categoryList = categoryRepository.findAllByPostIdOrderByModifiedAtDesc(post.getId());
            List<CategoryResponseDto> categoryResDtoList = new ArrayList<>();

            // List<Category>에 있는 정보를 List<CategoryResponseDto> 에 담는다.
            for(Category category: categoryList){
                categoryResDtoList.add(new CategoryResponseDto(category));
            }

            String postUsername = null;
            if (post.getUser() != null) {
                postUsername = post.getUser().getUsername();
            }

            PostResponseDto postResponseDto = new PostResponseDto(post, postLikeClickersResponseDtoList, bookmarkClickUserKeyResDtoList,
                    paragraphResDtoList, commentResDtoList, categoryResDtoList, postLikeCnt, postUsername);
            postResponseDtoList.add(postResponseDto);

        }

        // 댓글있고 북마크있는 것들만 추린다.
        List<PostResponseDto> postCommentAndBookmarkList = new ArrayList<>();
        for(PostResponseDto postResponseDto : postResponseDtoList){
            if(postResponseDto.getCommentList().size() > 0 && postResponseDto.getBookmarkLikesCnt() > 0){
                postCommentAndBookmarkList.add(postResponseDto);
            }
        }

        // 북마크 좋아요 순으로 내림차순 정렬(좋아요 많은게 위에 보이게끔)
        Comparator<PostResponseDto> comparator = Comparator.comparing(PostResponseDto::getBookmarkLikesCnt, Comparator.reverseOrder());
        List<PostResponseDto> responseDtoList = postCommentAndBookmarkList.stream().sorted(comparator).collect(Collectors.toList());

        List<PostResponseDto> top3ResDtoList = new ArrayList<>();
        int cnt = 0;
        for(PostResponseDto postResponseDto: responseDtoList){
            top3ResDtoList.add(postResponseDto);
            cnt++;
            if(cnt == 3)
                break;
        }

        return top3ResDtoList;  // BookMark 많이한 Top3 & 댓글 있는거 & 완성작
    }


    // 사용자가(내가) 좋아요한 게시글목록
    public List<PostResponseDto> viewMyLikesPost(int page, int size, User user){
        List<PostResponseDto> postResponseDtoList = new ArrayList<>();
        Pageable pageable = PageRequest.of(page, size);
        //Page<Post> posts = postRepository.findAllByOrderByModifiedAtDesc(pageable);
        List<PostLikes> postMyLikesList = postLikesRepository.findAllByUserId(user.getId(), pageable);

        // 지금 postLikes 에 시간이 없네.. 좋아요한 시간 순서데로 보여줘야할 것 같은데...
        int postLikeCnt = 0;
        for(PostLikes postLikes : postMyLikesList){
            Post post = postRepository.findById(postLikes.getPost().getId()).orElseThrow(
                    () -> new UserNotFoundException("존재하지 않는 ID 입니다.")
            );

            // 게시글을 좋아요한 사람의 userKey리스트를 구한다.
            List<PostLikes> postLikesList = postLikesRepository.findAllByPostId(post.getId());
            List<PostLikeClickersResponseDto> postLikeClickersResponseDtoList = new ArrayList<>();
            for (PostLikes postLikesTemp : postLikesList) {
                postLikeClickersResponseDtoList.add(new PostLikeClickersResponseDto(postLikesTemp));
            }
            //List<PostLikes> postLikesList = postLikesRepository.findAllByPostId(post.getId());
            postLikeCnt = postLikesList.size();

            // 게시글을 북마크한 사람의 userKey 리스트를 구한다.
            List<Bookmark> bookmarkList = bookmarkRepository.findAllByPostId(post.getId());
            List<BookmarkClickUserKeyResDto> bookmarkClickUserKeyResDtoList = new ArrayList<>();

            for (Bookmark bookmark:bookmarkList){
                bookmarkClickUserKeyResDtoList.add(new BookmarkClickUserKeyResDto(bookmark));
            }

            List<Paragraph> paragraphList = paragraphRepository.findAllByPostId(post.getId());
            List<ParagraphResDto> paragraphResDtoList = new ArrayList<>();

            for(Paragraph paragraph: paragraphList){
                Long paragraphLikesCnt = paragraphLikesRepository.countByParagraph(paragraph);
                Long paragraphKey = paragraph.getId();
                // 각 문단을 좋아요한 사람의 userKey 리스트를 구한다.
                List<ParagraphLikes> paragraphLikes = paragraphLikesRepository.findAllByParagraphId(paragraphKey);
                List<ParagraphLikesClickUserKeyResDto> paragraphLikesClickUserKeyResDtoList = new ArrayList<>();
                for(ParagraphLikes paragraphLikesTemp : paragraphLikes){
                    // paragraphLikesRepository.findAllByParagraphId(paragraphKey);
                    paragraphLikesClickUserKeyResDtoList.add(new ParagraphLikesClickUserKeyResDto(paragraphLikesTemp));
                }

                paragraphResDtoList.add(new ParagraphResDto(paragraph, paragraphLikesClickUserKeyResDtoList, paragraphLikesCnt));
            }

            List<Comment> commentList = commentRepository.findAllByPostIdOrderByModifiedAt(post.getId());
            List<CommentResponseDto> commentResDtoList = new ArrayList<>();

            // List<Comment>를 각각 List<CommentResponseDto> 에 담는다
            for (Comment comment:commentList ) {
                Long commentLikesCnt = commentLikesRepository.countByComment(comment);
                // 각 댓글을 좋아요한 사람의 userKey 리스트를 구한다.
//                List<CommentLikes> commentLikesList = commentLikesRepository.findAllByCommentId(comment.getId());
//                List<CommentLikeClickersResponseDto> commentLikeClickersResponseDtoList = new ArrayList<>();
//                for(CommentLikes commentLikesTemp : commentLikesList){
//                    commentLikeClickersResponseDtoList.add(new CommentLikeClickersResponseDto(commentLikesTemp));
//                }

                commentResDtoList.add(new CommentResponseDto(comment, commentLikesCnt));
            }

            List<Category> categoryList = categoryRepository.findAllByPostIdOrderByModifiedAtDesc(post.getId());
            List<CategoryResponseDto> categoryResDtoList = new ArrayList<>();
            // 게시글의 카테고리 리스트(시작 1개, 끝날 때 1개 총 2개)를 구한다.
            // List<Category>에 있는 정보를 List<CategoryResponseDto> 에 담는다.
            for(Category category: categoryList){
                categoryResDtoList.add(new CategoryResponseDto(category));
            }

            String postUsername = null;
            if (post.getUser() != null) {
                postUsername = post.getUser().getUsername();
            }

            PostResponseDto postResponseDto = new PostResponseDto(post, postLikeClickersResponseDtoList, bookmarkClickUserKeyResDtoList,
                    paragraphResDtoList, commentResDtoList, categoryResDtoList, postLikeCnt, postUsername);
            postResponseDtoList.add(postResponseDto);
        }

        return postResponseDtoList;
    }

    // 다른 유저 페이지
    /*public OtherUserResDto viewUserPage(Long userKey,int page, int size){
        User user = userRepository.findById(userKey).orElseThrow(
                () -> new IllegalArgumentException("해당 유저가 존재하지 않습니다.")
        );

        Pageable pageable = PageRequest.of(page,size);

        Page<Post> postList = postRepository.findAllByUserIdOrderByModifiedAtDesc(userKey, pageable);
        List<OtherUserPostListResDto> otherUserList = new ArrayList<>();

        for (Post post: postList ) {
            otherUserList.add(new OtherUserPostListResDto(post));
        }
        
        //OtherUserResDto otherUserResDto = new OtherUserResDto(user, postList);
        return new OtherUserResDto(user, otherUserList);
    }*/

    // 다른 유저 페이지 ( 다른 유저가 작성한 게시글들의 정보 )
    public OtherUserResDto2 viewUserCreatePost(Long userKey, int page, int size){
        User user = userRepository.findById(userKey).orElseThrow(
                () -> new UserNotFoundException("존재하지 않는 ID 입니다.")
        );

        Pageable pageable = PageRequest.of(page,size);

        Page<Post> postList = postRepository.findAllByUserIdOrderByModifiedAtDesc(user.getId(), pageable);
        //List<OtherUserPostListResDto> otherUserList = new ArrayList<>();
        List<PostResponseDto> postResponseDtoList = new ArrayList<>();
        //List<Post> posts = postRepository.findAllByOrderByModifiedAtDesc();
        // complete 가 false이며(미완성작품) 최근 수정한 시간순으로 불러온다.

        postResponseDtoList = viewPostList(postList);

        return new OtherUserResDto2(user, postResponseDtoList);
    }

    // 다른 유저 페이지 ( 다른 유저가 작성한 게시글들의 정보 )
    public OtherUserResDto2 viewUserParticipatePost(Long userKey, int page, int size){
        User user = userRepository.findById(userKey).orElseThrow(
                () -> new UserNotFoundException("존재하지 않는 ID 입니다.")
        );

        Pageable pageable = PageRequest.of(page,size);

        // paragraphList 에서 userKey와 동일한 리스트들 가져온다.
        List<Paragraph> paragraphList = paragraphRepository.findAllByUserId(userKey);
        List<Long> postKeyList = new ArrayList<>();

        // userKey와 동일한 리스트인 paragraphList 에서 postId가 중복을 없애서 postId만 들어있는 list에 넣는다.
        for(Paragraph paragraph : paragraphList){
            if( !postKeyList.contains(paragraph.getPost().getId()) ) {
                postKeyList.add(paragraph.getPost().getId());
            }
        }

        List<Post> postTempList = new ArrayList<>();
        // postList 전체 가져온거에서 postId와 postKeyList에 들어있는 postKey가 같으면 postTempList 에 Post행을 넣어준다.

        for(Long postKey : postKeyList){
            Post post = postRepository.findById(postKey).orElseThrow(
                    () -> new PostNotFoundException("게시물이 존재하지 않습니다.")
            );
            postTempList.add(post);
        }

        // List<Post>를 Page<Post>로 변환하는 코드
        final int start = (int)pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), postTempList.size());
        final Page<Post> postPageableList = new PageImpl<>(postTempList.subList(start, end), pageable, postTempList.size());


        List<PostResponseDto> postResponseDtoList = new ArrayList<>();
        postResponseDtoList = viewPostList(postPageableList);

        return new OtherUserResDto2(user, postResponseDtoList);
    }

    // '문단 시작'버튼을 눌렀을 때 writing이 true가 되고 writer의 닉네임을 준다.
    public Boolean startWritingStatus(Long postId, User user) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new PostNotFoundException("게시물이 존재하지 않습니다.")
        );
        post.updatePostByStart(true, user.getNickName(), LocalDateTime.now());

        return true;
    }

    // '문단 완료'버튼을 눌렀을 때 writing이 false가 되고 writer의 nickName을 null 로 한다.
    public Boolean talkWritingStatus(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new PostNotFoundException("게시물이 존재하지 않습니다.")
        );
        post.updatePostWriting(false, null,null);

        return true;
    }

    public Boolean cancelIsWriting(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new PostNotFoundException("게시물이 존재하지 않습니다.")
        );
        if (post.isWriting()){
            log.info("isWriting---------------------------------------false");
            post.updatePostWriting(false, null,null);
        }
        return post.isWriting();
    }


    // Page 사용하는 전체조회 메서드
    public List<PostResponseDto> viewPostList(Page<Post> posts){
        List<PostResponseDto> postResponseDtoList = new ArrayList<>();
        //List<Post> posts = postRepository.findAllByOrderByModifiedAtDesc();
        // complete 가 true이며(완성작) 최근 수정한 시간순으로 불러온다.
        /*
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.findAllByCompleteTrueOrderByModifiedAtDesc(pageable);
        */

        int postLikeCnt = 0;
        for (Post post: posts ) {
            // 게시글을 좋아요한 사람의 userKey리스트를 구한다.
            List<PostLikes> postLikesList = postLikesRepository.findAllByPostId(post.getId());
            List<PostLikeClickersResponseDto> postLikeClickersResponseDtoList = new ArrayList<>();
            for (PostLikes postLikesTemp : postLikesList) {
                postLikeClickersResponseDtoList.add(new PostLikeClickersResponseDto(postLikesTemp));
            }
            postLikeCnt = postLikesList.size();


            // 게시글을 북마크한 사람의 userKey 리스트를 구한다.
            List<Bookmark> bookmarkList = bookmarkRepository.findAllByPostId(post.getId());
            List<BookmarkClickUserKeyResDto> bookmarkClickUserKeyResDtoList = new ArrayList<>();

            for (Bookmark bookmark:bookmarkList){
                bookmarkClickUserKeyResDtoList.add(new BookmarkClickUserKeyResDto(bookmark));
            }


            List<Paragraph> paragraphList = paragraphRepository.findAllByPostId(post.getId());
            List<ParagraphResDto> paragraphResDtoList = new ArrayList<>();

            for(Paragraph paragraph: paragraphList){
//                UserInfoResponseDto userInfoResDto = new UserInfoResponseDto(paragraph.getUser());
//                paragraphResDtoList.add(new ParagraphResDto(paragraph, userInfoResDto));
                Long paragraphLikesCnt = paragraphLikesRepository.countByParagraph(paragraph);
                Long paragraphKey = paragraph.getId();
                // 각 문단을 좋아요한 사람의 userKey 리스트를 구한다.
                List<ParagraphLikes> paragraphLikes = paragraphLikesRepository.findAllByParagraphId(paragraphKey);
                List<ParagraphLikesClickUserKeyResDto> paragraphLikesClickUserKeyResDtoList = new ArrayList<>();
                for(ParagraphLikes paragraphLikesTemp : paragraphLikes){
                    // paragraphLikesRepository.findAllByParagraphId(paragraphKey);
                    paragraphLikesClickUserKeyResDtoList.add(new ParagraphLikesClickUserKeyResDto(paragraphLikesTemp));
                }

                paragraphResDtoList.add(new ParagraphResDto(paragraph, paragraphLikesClickUserKeyResDtoList, paragraphLikesCnt));
            }


            List<Comment> commentList = commentRepository.findAllByPostIdOrderByModifiedAt(post.getId());
            List<CommentResponseDto> commentResDtoList = new ArrayList<>();

            // List<Comment>를 각각 List<CommentResponseDto> 에 담는다
            for (Comment comment:commentList ) {
                Long commentLikesCnt = commentLikesRepository.countByComment(comment);
                // 각 댓글을 좋아요한 사람의 userKey 리스트를 구한다.
//                List<CommentLikes> commentLikesList = commentLikesRepository.findAllByCommentId(comment.getId());
//                List<CommentLikeClickersResponseDto> commentLikeClickersResponseDtoList = new ArrayList<>();
//                for(CommentLikes commentLikesTemp : commentLikesList){
//                    commentLikeClickersResponseDtoList.add(new CommentLikeClickersResponseDto(commentLikesTemp));
//                }

                commentResDtoList.add(new CommentResponseDto(comment, commentLikesCnt));
            }


            List<Category> categoryList = categoryRepository.findAllByPostIdOrderByModifiedAtDesc(post.getId());
            List<CategoryResponseDto> categoryResDtoList = new ArrayList<>();
            // 게시글의 카테고리 리스트(시작 1개, 끝날 때 1개 총 2개)를 구한다.
            // List<Category>에 있는 정보를 List<CategoryResponseDto> 에 담는다.
            for(Category category: categoryList){
                categoryResDtoList.add(new CategoryResponseDto(category));
            }

            String postUsername = null;
            if (post.getUser() != null) {
                postUsername = post.getUser().getUsername();
            }

            PostResponseDto postResponseDto = new PostResponseDto(post, postLikeClickersResponseDtoList, bookmarkClickUserKeyResDtoList,
                    paragraphResDtoList, commentResDtoList, categoryResDtoList, postLikeCnt, postUsername);
            postResponseDtoList.add(postResponseDto);
        }
        return postResponseDtoList;
    }

    public Boolean viewTest(Long postKey, UserDetailsImpl userDetails){

        /*if(userDetails == null){
            throw new UserNotFoundException("유저가 없네용 ㅜㅜ");
        }*/

        /*System.out.println("---------------------- 와이 ---------------------");
        List<Post> postList = postRepository.findAllByUserIdOrderByModifiedAtDesc(userDetails.getUser().getId());
        System.out.println("------------------- postList  size : " + postList.size());

        List<Bookmark> bookmarkList = bookmarkRepository.findAllByPostId(postKey);
        System.out.println("------------------- bookmarkList  size : " + bookmarkList.size());

        List<Paragraph> paragraphList = paragraphRepository.findAllByPostId(postKey);
        System.out.println("------------------- paragraphList  size : " + paragraphList.size());*/

        List<PostLikes> postLikes = postLikesRepository.findAllByPostId(postKey);
        System.out.println("------------------- postLikes  size : " + postLikes.size());


        return true;
    }
}
