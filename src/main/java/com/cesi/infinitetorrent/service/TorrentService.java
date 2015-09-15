package com.cesi.infinitetorrent.service;

import com.cesi.infinitetorrent.domain.InfiniteTracker;
import com.cesi.infinitetorrent.domain.Torrent;
import com.cesi.infinitetorrent.repository.InfiniteTrackerRepository;
import com.cesi.infinitetorrent.repository.TorrentRepository;
import com.turn.ttorrent.tracker.TrackedTorrent;
import com.turn.ttorrent.tracker.Tracker;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing torrents
 */
@Service
public class TorrentService {

    @Inject
    private TorrentRepository torrentRepository;

    @Inject
    private InfiniteTrackerRepository infiniteTrackerRepository;


    public Torrent add(Torrent torrent) throws URISyntaxException {
        URI requestUri = new URI(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getRequestURL().toString());
        final URI remoteUri = new URI("http://" + requestUri.getHost() + ":" + requestUri.getPort());

        try {
            File tempFile = File.createTempFile("IF-", ".torrent");
            Tracker tracker = new Tracker(new InetSocketAddress(6969));
            FileUtils.writeByteArrayToFile(tempFile, torrent.getFile());

            TrackedTorrent torrentFile = TrackedTorrent.load(tempFile);

            torrent.setCreated(new DateTime());
            torrent.setName(torrentFile.getName());
            torrent.setComment(torrentFile.getComment());
            torrent.setCreatedBy(torrentFile.getCreatedBy());
            torrent.setTotalSize(torrentFile.getSize());
            torrent.setLeechers(torrentFile.leechers());
            torrent.setSeeders(torrentFile.seeders());

            List<URI> announces = torrentFile.getAnnounceList().stream()
                .flatMap(Collection::stream)
                .distinct()
                .filter(uri -> !uri.equals(remoteUri))
                .collect(Collectors.toList());

            announces.add(remoteUri);

            announces.stream()
                .map(uri -> {
                    InfiniteTracker bddInfiniteTracker = infiniteTrackerRepository.findByUrl(uri.toString());
                    return bddInfiniteTracker != null ? bddInfiniteTracker : infiniteTrackerRepository.save(new InfiniteTracker(uri));
                })
                .collect(Collectors.collectingAndThen(Collectors.toList(), torrent.getInfiniteTrackers()::addAll));

            tempFile.delete();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return torrentRepository.save(torrent);
    }
}
