package com.cesi.infinitetorrent.web.rest;

import com.cesi.infinitetorrent.domain.InfiniteTracker;
import com.cesi.infinitetorrent.domain.Torrent;
import com.cesi.infinitetorrent.repository.InfiniteTrackerRepository;
import com.cesi.infinitetorrent.repository.TorrentRepository;
import com.cesi.infinitetorrent.web.rest.dto.InfiniteTrackerSynchronizeDto;
import com.codahale.metrics.annotation.Timed;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class SynchronizeResource {

    private final Logger log = LoggerFactory.getLogger(SynchronizeResource.class);

    private RestTemplate restTemplate = new RestTemplate();

    @Inject
    private TorrentRepository torrentRepository;

    @Inject
    private InfiniteTrackerRepository infiniteTrackerRepository;

    /**
     * SYNCHRONIZE
     */
    @RequestMapping(value = "/api/synchronize", method = RequestMethod.GET)
    @Timed
    public List<InfiniteTrackerSynchronizeDto> synchronize() {
        log.debug("REST request to synchronize the tracker : {}");
        return infiniteTrackerRepository.findTop5ByOrderByDateLastSyncAsc().stream()
            .peek(tracker -> tracker.setDateLastSync(new DateTime()))
            .map(infiniteTrackerRepository::save)
            .map(tracker -> new InfiniteTrackerSynchronizeDto(tracker, getAllTorrentsForTracker(tracker).stream()
                .filter(torrent -> torrentRepository.findOne(torrent.getId()) == null)
                .peek(torrentRepository::save)
                .collect(Collectors.counting()))
            )
            .collect(Collectors.toList());
    }
    private List<Torrent> getAllTorrentsForTracker(InfiniteTracker tracker) {
        return restTemplate.exchange(tracker.getUrl() + "/sync/torrents", HttpMethod.GET, null, new ParameterizedTypeReference<List<Torrent>>() {
        }).getBody();
    }

}
