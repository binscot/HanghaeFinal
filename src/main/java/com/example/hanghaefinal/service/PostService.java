package com.example.hanghaefinal.service;

import com.example.hanghaefinal.dto.requestDto.CategoryRequestDto;
import com.example.hanghaefinal.dto.requestDto.PostRequestDto;
import com.example.hanghaefinal.dto.responseDto.*;
import com.example.hanghaefinal.model.*;
import com.example.hanghaefinal.repository.*;
import com.example.hanghaefinal.security.UserDetailsImpl;
import com.example.hanghaefinal.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
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
    private final S3Uploader s3Uploader;

    public String uploadImageFile(MultipartFile multipartFile, PostRequestDto requestDto) throws IOException {
        //String originalFileName = multipartFile.getOriginalFilename();
        //String convertedFileName = UUID.randomUUID() + originalFileName;
        //requestDto.setImageUrl(convertedFileName);
        String dirName = "image";
        //s3Uploader.upload(multipartFile, convertedFileName);

        String defaultImg = "https://taeks3bucket.s3.ap-northeast-2.amazonaws.com/image/defaultPhoto.png";
        if (!Objects.equals(multipartFile.getOriginalFilename(), "foo.txt"))
            defaultImg = s3Uploader.upload(multipartFile, "image");
        //String uploadUrl =  s3Uploader.upload(multipartFile, dirName);
        //requestDto.setPostImageUrl(defaultImg);
        //requestDto.setPostImageUrl(uploadUrl);
        return defaultImg;
    }

    // 게시글 최초 생성 -> 미완성 게시글 생성
    @Transactional
    public Boolean savePost(PostRequestDto postRequestDto, User user, String defaultImg){
        Post post = new Post(postRequestDto, user, defaultImg);
        Category category = new Category(postRequestDto.getCategory(), post);
        Paragraph paragraph = new Paragraph(postRequestDto.getParagraph(), user, post);
        postRepository.save(post);
        categoryRepository.save(category);
        paragraphRepository.save(paragraph);

        return true;
    }

    // 마지막 파라그래프 작성 후 게시글 완성 버튼 누름 -> 완성 게시글로 변경 ( 완성 게시글 상세 조회)
    @Transactional
    public PostDetailResponseDto completePost(Long postId, CategoryRequestDto categoryRequestDto){
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("postId가 존재하지 않습니다.")
        );

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

        List<Comment> commentList = commentRepository.findAllByPostIdOrderByModifiedAtDesc(postId);
        List<CommentResponseDto> commentResDtoList = new ArrayList<>();

        // List<Comment>에 있는 정보를 List<CommentResponseDto> 에 담는다
        for (Comment comment:commentList ) {
            Long commentLikesCnt = commentLikesRepository.countByComment(comment);

            List<CommentLikes> commentLikesList = commentLikesRepository.findAllByCommentId(comment.getId());
            List<CommentLikeClickersResponseDto> commentLikeClickersResponseDtoList = new ArrayList<>();
            for(CommentLikes commentLikesTemp : commentLikesList){
                commentLikeClickersResponseDtoList.add(new CommentLikeClickersResponseDto(commentLikesTemp));
            }

            commentResDtoList.add(new CommentResponseDto(comment, commentLikesCnt, commentLikeClickersResponseDtoList));
        }

        Long postLikesCnt =  postLikesRepository.countByPost(post);

        String postUsername = null;
        if (post.getUser() != null) {
            postUsername = post.getUser().getUsername();
        }

        //return new PostDetailResponseDto(post, paragraphResDtoList, commentResDtoList, categoryResDtoList, postLikesCnt,postUsername);
        return new PostDetailResponseDto(post, postLikeClickersResponseDtoList, bookmarkClickUserKeyResDtoList,
                paragraphResDtoList, commentResDtoList, categoryResDtoList, postLikesCnt, postUsername);
    }

    // 게시글 상세조회 ( 완성, 미완성 둘다)
    public PostDetailResponseDto viewPostDetail(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("postId가 존재하지 않습니다.")
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

        for (Paragraph paragraph : paragraphList) {
            // a. 이거는 UserInfoResponseDto를 여기서 생성해서 ParagraphResDto로 가져가고
//            UserInfoResponseDto userInfoResDto = new UserInfoResponseDto(paragraph.getUser());
//            paragraphResDtoList.add(new ParagraphResDto(paragraph, userInfoResDto));
            
            // b. 이거는 UserInfoResponseDto를 ParagraphResDto로 가서 생성한다. ㅇㅇ
            // 각 문단에 좋아요를 한 userKey(PK)의 리스트
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

        List<Category> categoryList = categoryRepository.findAllByPostIdOrderByModifiedAtDesc(postId);
        List<CategoryResponseDto> categoryResDtoList = new ArrayList<>();

        // List<Category>에 있는 정보를 List<CategoryResponseDto> 에 담는다.
        for (Category category : categoryList) {
            categoryResDtoList.add(new CategoryResponseDto(category));
        }

        List<Comment> commentList = commentRepository.findAllByPostIdOrderByModifiedAtDesc(postId);
        List<CommentResponseDto> commentResDtoList = new ArrayList<>();

        // List<Comment>를 각각 List<CommentResponseDto> 에 담는다
        for (Comment comment : commentList) {
            Long commentLikesCnt = commentLikesRepository.countByComment(comment);

            List<CommentLikes> commentLikesList = commentLikesRepository.findAllByCommentId(comment.getId());
            List<CommentLikeClickersResponseDto> commentLikeClickersResponseDtoList = new ArrayList<>();
            for(CommentLikes commentLikesTemp : commentLikesList){
                commentLikeClickersResponseDtoList.add(new CommentLikeClickersResponseDto(commentLikesTemp));
            }

            commentResDtoList.add(new CommentResponseDto(comment, commentLikesCnt, commentLikeClickersResponseDtoList));
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


        return new PostDetailResponseDto(post, postLikeClickersResponseDtoList, bookmarkClickUserKeyResDtoList,
                paragraphResDtoList, commentResDtoList, categoryResDtoList, postLikesCnt, postUsername);
//        return new PostDetailResponseDto(post, commentList, postLikesCnt);
    }

    // 완성작 게시글 전체 조회 - 최신순
    public List<PostResponseDto> viewPostRecent(int page, int size){
        List<PostResponseDto> postResponseDtoList = new ArrayList<>();
        //List<Post> posts = postRepository.findAllByOrderByModifiedAtDesc();
        // complete 가 true이며(완성작) 최근 수정한 시간순으로 불러온다.
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.findAllByCompleteTrueOrderByModifiedAtDesc(pageable);

        int postLikeCnt;
        for (Post post: posts ) {

            List<PostLikes> postLikesList = postLikesRepository.findAllByPostId(post.getId());
            List<PostLikeClickersResponseDto> postLikeClickersResponseDtoList = new ArrayList<>();
            for (PostLikes postLikesTemp : postLikesList) {
                postLikeClickersResponseDtoList.add(new PostLikeClickersResponseDto(postLikesTemp));
            }
            //List<PostLikes> postLikesList = postLikesRepository.findAllByPostId(post.getId());
            postLikeCnt = postLikesList.size();

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

                List<ParagraphLikes> paragraphLikes = paragraphLikesRepository.findAllByParagraphId(paragraphKey);
                List<ParagraphLikesClickUserKeyResDto> paragraphLikesClickUserKeyResDtoList = new ArrayList<>();
                for(ParagraphLikes paragraphLikesTemp : paragraphLikes){
                    // paragraphLikesRepository.findAllByParagraphId(paragraphKey);
                    paragraphLikesClickUserKeyResDtoList.add(new ParagraphLikesClickUserKeyResDto(paragraphLikesTemp));
                }

                paragraphResDtoList.add(new ParagraphResDto(paragraph, paragraphLikesClickUserKeyResDtoList, paragraphLikesCnt));
            }

            List<Comment> commentList = commentRepository.findAllByPostIdOrderByModifiedAtDesc(post.getId());
            List<CommentResponseDto> commentResDtoList = new ArrayList<>();

            // List<Comment>를 각각 List<CommentResponseDto> 에 담는다
            for (Comment comment:commentList ) {
                Long commentLikesCnt = commentLikesRepository.countByComment(comment);

                List<CommentLikes> commentLikesList = commentLikesRepository.findAllByCommentId(comment.getId());
                List<CommentLikeClickersResponseDto> commentLikeClickersResponseDtoList = new ArrayList<>();
                for(CommentLikes commentLikesTemp : commentLikesList){
                    commentLikeClickersResponseDtoList.add(new CommentLikeClickersResponseDto(commentLikesTemp));
                }

                commentResDtoList.add(new CommentResponseDto(comment, commentLikesCnt, commentLikeClickersResponseDtoList));
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
        return postResponseDtoList;
    }

    // 완성작 게시글 전체 조회 - 추천순(좋아요순)
    public List<PostResponseDto> viewPostRecommend(int page, int size){
        List<PostResponseDto> postResponseDtoList = new ArrayList<>();
        //List<Post> posts = postRepository.findAllByOrderByModifiedAtDesc();
        // complete 가 true이며(완성작) 최근 수정한 시간순으로 불러온다.
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.findAllByCompleteTrueOrderByModifiedAtDesc(pageable);

        int postLikeCnt = 0;
        for (Post post: posts ) {

            List<PostLikes> postLikesList = postLikesRepository.findAllByPostId(post.getId());
            List<PostLikeClickersResponseDto> postLikeClickersResponseDtoList = new ArrayList<>();
            for (PostLikes postLikesTemp : postLikesList) {
                postLikeClickersResponseDtoList.add(new PostLikeClickersResponseDto(postLikesTemp));
            }
            //List<PostLikes> postLikesList = postLikesRepository.findAllByPostId(post.getId());
            postLikeCnt = postLikesList.size();

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

                List<ParagraphLikes> paragraphLikes = paragraphLikesRepository.findAllByParagraphId(paragraphKey);
                List<ParagraphLikesClickUserKeyResDto> paragraphLikesClickUserKeyResDtoList = new ArrayList<>();
                for(ParagraphLikes paragraphLikesTemp : paragraphLikes){
                    // paragraphLikesRepository.findAllByParagraphId(paragraphKey);
                    paragraphLikesClickUserKeyResDtoList.add(new ParagraphLikesClickUserKeyResDto(paragraphLikesTemp));
                }

                paragraphResDtoList.add(new ParagraphResDto(paragraph, paragraphLikesClickUserKeyResDtoList, paragraphLikesCnt));
            }

            List<Comment> commentList = commentRepository.findAllByPostIdOrderByModifiedAtDesc(post.getId());
            List<CommentResponseDto> commentResDtoList = new ArrayList<>();

            // List<Comment>를 각각 List<CommentResponseDto> 에 담는다
            for (Comment comment:commentList ) {
                Long commentLikesCnt = commentLikesRepository.countByComment(comment);

                List<CommentLikes> commentLikesList = commentLikesRepository.findAllByCommentId(comment.getId());
                List<CommentLikeClickersResponseDto> commentLikeClickersResponseDtoList = new ArrayList<>();
                for(CommentLikes commentLikesTemp : commentLikesList){
                    commentLikeClickersResponseDtoList.add(new CommentLikeClickersResponseDto(commentLikesTemp));
                }

                commentResDtoList.add(new CommentResponseDto(comment, commentLikesCnt, commentLikeClickersResponseDtoList));
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
            //postResponseDto.getPostLikesCnt();
            postResponseDtoList.add(postResponseDto);
        }

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

        int postLikeCnt = 0;
        for (Post post: posts ) {
            List<PostLikes> postLikesList = postLikesRepository.findAllByPostId(post.getId());
            List<PostLikeClickersResponseDto> postLikeClickersResponseDtoList = new ArrayList<>();
            for (PostLikes postLikesTemp : postLikesList) {
                postLikeClickersResponseDtoList.add(new PostLikeClickersResponseDto(postLikesTemp));
            }
            //List<PostLikes> postLikesList = postLikesRepository.findAllByPostId(post.getId());
            postLikeCnt = postLikesList.size();

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

                List<ParagraphLikes> paragraphLikes = paragraphLikesRepository.findAllByParagraphId(paragraphKey);
                List<ParagraphLikesClickUserKeyResDto> paragraphLikesClickUserKeyResDtoList = new ArrayList<>();
                for(ParagraphLikes paragraphLikesTemp : paragraphLikes){
                    // paragraphLikesRepository.findAllByParagraphId(paragraphKey);
                    paragraphLikesClickUserKeyResDtoList.add(new ParagraphLikesClickUserKeyResDto(paragraphLikesTemp));
                }

                paragraphResDtoList.add(new ParagraphResDto(paragraph, paragraphLikesClickUserKeyResDtoList, paragraphLikesCnt));
            }

            List<Comment> commentList = commentRepository.findAllByPostIdOrderByModifiedAtDesc(post.getId());
            List<CommentResponseDto> commentResDtoList = new ArrayList<>();

            // List<Comment>를 각각 List<CommentResponseDto> 에 담는다
            for (Comment comment:commentList ) {
                Long commentLikesCnt = commentLikesRepository.countByComment(comment);

                List<CommentLikes> commentLikesList = commentLikesRepository.findAllByCommentId(comment.getId());
                List<CommentLikeClickersResponseDto> commentLikeClickersResponseDtoList = new ArrayList<>();
                for(CommentLikes commentLikesTemp : commentLikesList){
                    commentLikeClickersResponseDtoList.add(new CommentLikeClickersResponseDto(commentLikesTemp));
                }

                commentResDtoList.add(new CommentResponseDto(comment, commentLikesCnt, commentLikeClickersResponseDtoList));
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
    public OtherUserResDto2 viewUserPage2(Long userKey, int page, int size){
        User user = userRepository.findById(userKey).orElseThrow(
                () -> new IllegalArgumentException("해당 유저가 존재하지 않습니다.")
        );

        Pageable pageable = PageRequest.of(page,size);

        Page<Post> postList = postRepository.findAllByUserIdOrderByModifiedAtDesc(user.getId(), pageable);
        //List<OtherUserPostListResDto> otherUserList = new ArrayList<>();
        List<PostResponseDto> postResponseDtoList = new ArrayList<>();
        //List<Post> posts = postRepository.findAllByOrderByModifiedAtDesc();
        // complete 가 false이며(미완성작품) 최근 수정한 시간순으로 불러온다.

        //List<OtherUserResDto2> otherUserResDto2List2 = new ArrayList<>();


        int postLikeCnt = 0;
        for (Post post: postList ) {
            List<PostLikes> postLikesList = postLikesRepository.findAllByPostId(post.getId());
            List<PostLikeClickersResponseDto> postLikeClickersResponseDtoList = new ArrayList<>();
            for (PostLikes postLikesTemp : postLikesList) {
                postLikeClickersResponseDtoList.add(new PostLikeClickersResponseDto(postLikesTemp));
            }
            //List<PostLikes> postLikesList = postLikesRepository.findAllByPostId(post.getId());
            postLikeCnt = postLikesList.size();

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

                List<ParagraphLikes> paragraphLikes = paragraphLikesRepository.findAllByParagraphId(paragraphKey);
                List<ParagraphLikesClickUserKeyResDto> paragraphLikesClickUserKeyResDtoList = new ArrayList<>();
                for(ParagraphLikes paragraphLikesTemp : paragraphLikes){
                    // paragraphLikesRepository.findAllByParagraphId(paragraphKey);
                    paragraphLikesClickUserKeyResDtoList.add(new ParagraphLikesClickUserKeyResDto(paragraphLikesTemp));
                }

                paragraphResDtoList.add(new ParagraphResDto(paragraph, paragraphLikesClickUserKeyResDtoList, paragraphLikesCnt));
            }

            List<Comment> commentList = commentRepository.findAllByPostIdOrderByModifiedAtDesc(post.getId());
            List<CommentResponseDto> commentResDtoList = new ArrayList<>();

            // List<Comment>를 각각 List<CommentResponseDto> 에 담는다
            for (Comment comment:commentList ) {
                Long commentLikesCnt = commentLikesRepository.countByComment(comment);

                List<CommentLikes> commentLikesList = commentLikesRepository.findAllByCommentId(comment.getId());
                List<CommentLikeClickersResponseDto> commentLikeClickersResponseDtoList = new ArrayList<>();
                for(CommentLikes commentLikesTemp : commentLikesList){
                    commentLikeClickersResponseDtoList.add(new CommentLikeClickersResponseDto(commentLikesTemp));
                }

                commentResDtoList.add(new CommentResponseDto(comment, commentLikesCnt, commentLikeClickersResponseDtoList));
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

        return new OtherUserResDto2(user, postResponseDtoList);
    }

    // '문단 시작'버튼을 눌렀을 때 writing이 true가 되고 writer의 닉네임을 준다.
    public void startWritingStatus(Long postId, User user) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("해당 게시글이 없습니다.")
        );
        post.updatePostWriting(true, user.getNickName());
    }

    // '문단 완료'버튼을 눌렀을 때 writing이 false가 되고 writer의 nickName을 null 로 한다.
    public void talkWritingStatus(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("해당 게시글이 없습니다.")
        );
        post.updatePostWriting(false, null);
    }

    public Boolean cancelIsWriting(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("해당 게시글이 없습니다.")
        );
        if (post.isWriting()){
            post.updatePostWriting(false, null);
        }
        return post.isWriting();
    }
}
