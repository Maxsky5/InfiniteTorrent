package com.cesi.infinitetorrent.repository;

import com.cesi.infinitetorrent.domain.Torrent;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Spring Data MongoDB repository for the Torrent entity.
 */
public interface TorrentRepository extends MongoRepository<Torrent, String> {

    List<Torrent> findAllByUpdatedAfter(DateTime date);

}
