package com.example.hanghaefinal.repository;

import com.example.hanghaefinal.model.Post;
import com.example.hanghaefinal.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByModifiedAtDesc();
    // findAll By OrderBy ModifiedAt Desc
    // 모두 찾아라 순서에따라 ModifiedAt을 기준으로 내림차순으로
    // 규칙에 맞게만 써주면 jpa가 알아서 sql문을 짜준다
    // modifiedAt 은 Timestamped 에 있다.
    Page<Post> findAllByOrderByModifiedAtDesc(Pageable pageable);

    List<Post> findByTitleContaining(String keyword);
    List<Post> findAllByUserIdOrderByModifiedAtDesc(Long userId);
    Page<Post> findAllByUserIdOrderByModifiedAtDesc(Long userId, Pageable pageable);

    //List<Post> findAllByCompleteOrderByModifiedAt(boolean complete);
    //List<Post> findAllByCompleteAndOrderByModifiedAt(boolean complete);
    //List<Post> findAllByOrderByModifiedAtDescAndCompleteTrue();
    Page<Post> findAllByCompleteTrueOrderByModifiedAtDesc(Pageable pageable);
    Page<Post> findAllByCompleteFalseOrderByModifiedAtDesc(Pageable pageable);

    List<Post> findAllByCompleteTrueOrderByModifiedAtDesc();
    List<Post> findAllByUser(User user);

}
