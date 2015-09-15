package com.cesi.infinitetorrent.repository;

import com.cesi.infinitetorrent.domain.InfiniteTracker;
import com.cesi.infinitetorrent.domain.Torrent;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Spring Data MongoDB repository for the InfiniteTracker entity.
 */
public interface InfiniteTrackerRepository extends MongoRepository<InfiniteTracker, String> {

    InfiniteTracker findByUrl(String url);

    List<InfiniteTracker> findTop5ByOrderByDateLastSyncAsc();

}
