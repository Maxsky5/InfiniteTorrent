package com.cesi.infinitetorrent.service;

import com.cesi.infinitetorrent.domain.Torrent;
import com.cesi.infinitetorrent.repository.InfiniteTrackerRepository;
import com.cesi.infinitetorrent.repository.TorrentRepository;
import com.turn.ttorrent.bcodec.BDecoder;
import com.turn.ttorrent.bcodec.BEValue;
import com.turn.ttorrent.bcodec.BEncoder;
import com.turn.ttorrent.tracker.TrackedTorrent;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.*;

/**
 * Service class for managing torrents
 */
@Service
public class TorrentService {

    @Inject
    private TorrentRepository torrentRepository;

    @Inject
    private InfiniteTrackerRepository infiniteTrackerRepository;

    @Inject
    private InfiniteTrackerService infiniteTrackerService;


    public Torrent add(Torrent torrent) throws URISyntaxException {
        URI requestUri = new URI(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getRequestURL().toString());
        final URI remoteUri = new URI("http://" + requestUri.getHost() + ":" + requestUri.getPort() + "/announce");

        try {
            File tempFile = Files.createTempFile("IF-", ".torrent").toFile();
            FileUtils.writeByteArrayToFile(tempFile, torrent.getFile());

            TrackedTorrent torrentFile = TrackedTorrent.load(tempFile);

            if (torrentFile.getAnnounceList().stream()
                .flatMap(Collection::stream)
                .noneMatch(uri -> uri.equals(remoteUri))) {
                torrentFile.getAnnounceList().add(Arrays.asList(remoteUri));

                Map<String, BEValue> decoded = BDecoder.bdecode(new ByteArrayInputStream(torrent.getFile())).getMap();

                if (decoded.containsKey("announce-list")) {
                    List<BEValue> tiers = decoded.get("announce-list").getList();

                    List<BEValue> tierInfo = new LinkedList<BEValue>();
                    tierInfo.add(new BEValue(remoteUri.toString()));
                    tiers.add(new BEValue(tierInfo));

                    decoded.put("announce-list", new BEValue(tiers));
                } else if (decoded.containsKey("announce")) {
                    List<BEValue> tiers = new LinkedList<>();

                    List<BEValue> tier1Info = new LinkedList<>();
                    List<BEValue> tier2Info = new LinkedList<>();
                    tier1Info.add(new BEValue(decoded.get("announce").getString()));
                    tier2Info.add(new BEValue(remoteUri.toString()));

                    tiers.add(new BEValue(tier1Info));
                    tiers.add(new BEValue(tier2Info));

                    decoded.remove("announce");
                    decoded.put("announce-list", new BEValue(tiers));
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                BEncoder.bencode(new BEValue(decoded), baos);

                torrent.setFile(baos.toByteArray());
            }

            infiniteTrackerService.getTracker().announce(torrentFile);

            torrent.setCreated(new DateTime());
            torrent.setUpdated(new DateTime());
            torrent.setName(torrentFile.getName());
            torrent.setComment(torrentFile.getComment());
            torrent.setCreatedBy(torrentFile.getCreatedBy());
            torrent.setTotalSize(torrentFile.getSize());
            torrent.setLeechers(torrentFile.leechers());
            torrent.setSeeders(torrentFile.seeders());

//            List<URI> announces = torrentFile.getAnnounceList().stream()
//                .flatMap(Collection::stream)
//                .distinct()
//                .filter(uri -> !uri.equals(remoteUri))
//                .collect(Collectors.toList());

//            announces.add(remoteUri);

//            announces.stream()
//                .map(uri -> {
//                    InfiniteTracker bddInfiniteTracker = infiniteTrackerRepository.findByUrl(uri.toString());
//                    return bddInfiniteTracker != null ? bddInfiniteTracker : infiniteTrackerRepository.save(new InfiniteTracker(uri));
//                })
//                .collect(Collectors.collectingAndThen(Collectors.toList(), torrent.getInfiniteTrackers()::addAll));

            tempFile.delete();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return torrentRepository.save(torrent);
    }
}
