package com.cesi.infinitetorrent.repository;

import com.cesi.infinitetorrent.domain.Torrent;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the Torrent entity.
 */
public interface TorrentRepository extends MongoRepository<Torrent,String> {

}
