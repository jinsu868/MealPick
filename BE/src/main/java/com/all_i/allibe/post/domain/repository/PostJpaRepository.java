package com.all_i.allibe.post.domain.repository;

import com.all_i.allibe.post.domain.Post;
import java.util.List;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostJpaRepository extends JpaRepository<Post, Long>, PostJpaRepositoryCustom {

    @Query(
        """
        SELECT p FROM Post p
        WHERE ST_COntains(ST_Buffer(:currentMemberLocation, :meterRange), p.point)
        ORDER BY p.id DESC LIMIT :size
        """
    )
    List<Post> getNearByPosts(
            Point currentMemberLocation,
            Double meterRange,
            int size
    );
}
