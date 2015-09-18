package com.cesi.infinitetorrent.service;

import com.cesi.infinitetorrent.repository.TorrentRepository;
import com.turn.ttorrent.tracker.TrackedTorrent;
import com.turn.ttorrent.tracker.Tracker;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;

/**
 * Service class for managing trackers
 */
@Service
public class InfiniteTrackerService {

    @Inject
    private TorrentRepository torrentRepository;

    @Inject
    public static Tracker tracker = null;

    public void startTracker() {
        try {
            if (null == tracker) {
                tracker = new Tracker(new InetSocketAddress(6969));
            }

            torrentRepository.findAll().stream()
                .map(torrent -> {
                    try {
                        File torrentFile = File.createTempFile("IF-", ".torrent");
                        FileUtils.writeByteArrayToFile(torrentFile, torrent.getFile());
                        return TrackedTorrent.load(torrentFile);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                })
                .forEach(tracker::announce);

            tracker.start();
        } catch (IOException e) {

        }
    }

    public void stopTracker() {
        tracker.stop();
    }

    public Tracker getTracker() {
        return tracker;
    }
}
