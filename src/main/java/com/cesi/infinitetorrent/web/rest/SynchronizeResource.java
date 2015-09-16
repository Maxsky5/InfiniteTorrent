package com.cesi.infinitetorrent.web.rest;

import com.cesi.infinitetorrent.domain.InfiniteTracker;
import com.cesi.infinitetorrent.domain.Torrent;
import com.cesi.infinitetorrent.repository.InfiniteTrackerRepository;
import com.cesi.infinitetorrent.repository.TorrentRepository;
import com.codahale.metrics.annotation.Timed;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Controller
public class SynchronizeResource {

    private final Logger log = LoggerFactory.getLogger(SynchronizeResource.class);

    @Inject
    private TorrentRepository torrentRepository;

    @Inject
    private InfiniteTrackerRepository infiniteTrackerRepository;

    /**
     * SYNCHRONIZE
     */
    @RequestMapping(value = "/api/synchronize",
        method = RequestMethod.GET)
    @Timed
    @ResponseBody
    public List<InfiniteTracker> synchronize() {
        log.debug("REST request to synchronize the tracker : {}");

        List<InfiniteTracker> trackers = infiniteTrackerRepository.findTop5ByOrderByDateLastSyncAsc();
        List<Torrent> torrents = new ArrayList<>();

        for (InfiniteTracker tracker : trackers) {
            System.out.println(tracker.getHost());
//            tracker.setDateLastSync(new DateTime());
//            infiniteTrackerRepository.save(tracker);
            RestTemplate restTemplate = new RestTemplate();
            Torrent[] trackerTorrents = restTemplate.getForObject(tracker.getUrl() + "?file=true", Torrent[].class);

            for (Torrent torrent : trackerTorrents) {
                torrents.add(torrent);
            }
        }

        torrents.stream()
            .filter(torrent -> torrentRepository.findOne(torrent.getId()) != null)
            .peek(torrent -> {
                System.out.println(torrent.getName());
                torrentRepository.save(torrent);
            });

        return trackers;
    }

}
