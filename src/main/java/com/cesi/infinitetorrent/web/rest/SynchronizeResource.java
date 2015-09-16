package com.cesi.infinitetorrent.web.rest;

import com.cesi.infinitetorrent.domain.InfiniteTracker;
import com.cesi.infinitetorrent.domain.Torrent;
import com.cesi.infinitetorrent.repository.InfiniteTrackerRepository;
import com.cesi.infinitetorrent.repository.TorrentRepository;
import com.codahale.metrics.annotation.Timed;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public List<Map<Long, InfiniteTracker>> synchronize() {
        log.debug("REST request to synchronize the tracker : {}");
        List<Map<Long, InfiniteTracker>> result = new ArrayList<>();

        List<InfiniteTracker> trackers = infiniteTrackerRepository.findTop5ByOrderByDateLastSyncAsc();

        for (InfiniteTracker tracker : trackers) {
            tracker.setDateLastSync(new DateTime());
            infiniteTrackerRepository.save(tracker);
            List<Torrent> torrents = new ArrayList<>();
            RestTemplate restTemplate = new RestTemplate();
            Torrent[] trackerTorrents = restTemplate.getForObject(tracker.getUrl() + "?file=true", Torrent[].class);

            for (Torrent torrent : trackerTorrents) {
                torrents.add(torrent);
            }

            Long nbTorrents = torrents.stream()
                .filter(torrent -> torrentRepository.findOne(torrent.getId()) == null)
                .peek(torrent -> {
                    torrentRepository.save(torrent);
                })
                .count();

            result.add(new HashMap<Long, InfiniteTracker>() {{
                put(nbTorrents, tracker);
            }});
        }

        return result;
    }

}
