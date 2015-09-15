package com.cesi.infinitetorrent.repository;

import com.cesi.infinitetorrent.domain.Tag;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Spring Data MongoDB repository for the Tag entity.
 */
public interface TagRepository extends MongoRepository<Tag,String> {

    List<Tag> findByNameLike(String name);

}
