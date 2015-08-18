package com.cesi.infinitetorrent.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.cesi.infinitetorrent.domain.Torrent;
import com.cesi.infinitetorrent.repository.TorrentRepository;
import com.cesi.infinitetorrent.web.rest.util.HeaderUtil;
import com.cesi.infinitetorrent.web.rest.util.PaginationUtil;
import com.turn.ttorrent.tracker.TrackedTorrent;
import com.turn.ttorrent.tracker.Tracker;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Torrent.
 */
@RestController
@RequestMapping("/api")
public class TorrentResource {

    private final Logger log = LoggerFactory.getLogger(TorrentResource.class);

    @Inject
    private TorrentRepository torrentRepository;

    /**
     * POST  /torrents -> Create a new torrent.
     */
    @RequestMapping(value = "/torrents",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Torrent> create(@RequestBody Torrent torrent) throws URISyntaxException {
        log.debug("REST request to save Torrent : {}", torrent);
        if (torrent.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new torrent cannot already have an ID").body(null);
        }

        try {
            File tempFile = File.createTempFile(null, ".torrent");
            Tracker tracker = new Tracker(new InetSocketAddress(6969));
            FileUtils.writeByteArrayToFile(tempFile, torrent.getFile());

            TrackedTorrent torrentFile = TrackedTorrent.load(tempFile);
            tracker.announce(torrentFile);

            torrent.setCreated(new DateTime());
            torrent.setName(torrentFile.getName());
            torrent.setComment(torrentFile.getComment());
            torrent.setCreatedBy(torrentFile.getCreatedBy());
            torrent.setTotalSize(torrentFile.getSize());
            torrent.setLeechers(torrentFile.leechers());
            torrent.setSeeders(torrentFile.seeders());

            tempFile.delete();
        } catch (IOException e) {

        }

        Torrent result = torrentRepository.save(torrent);

        return ResponseEntity.created(new URI("/api/torrents/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("torrent", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /torrents -> Updates an existing torrent.
     */
    @RequestMapping(value = "/torrents",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Torrent> update(@Valid @RequestBody Torrent torrent) throws URISyntaxException {
        log.debug("REST request to update Torrent : {}", torrent);
        if (torrent.getId() == null) {
            return create(torrent);
        }
        Torrent result = torrentRepository.save(torrent);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("torrent", torrent.getId().toString()))
            .body(result);
    }

    /**
     * GET  /torrents -> get all the torrents.
     */
    @RequestMapping(value = "/torrents",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Torrent>> getAll(@RequestParam(value = "page", required = false) Integer offset,
                                                @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<Torrent> page = torrentRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/torrents", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /torrents/:id -> get the "id" torrent.
     */
    @RequestMapping(value = "/torrents/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Torrent> get(@PathVariable String id) {
        log.debug("REST request to get Torrent : {}", id);
        return Optional.ofNullable(torrentRepository.findOne(id))
            .map(torrent -> new ResponseEntity<>(
                torrent,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /torrents/:id -> delete the "id" torrent.
     */
    @RequestMapping(value = "/torrents/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> delete(@PathVariable String id) {
        log.debug("REST request to delete Torrent : {}", id);
        torrentRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("torrent", id.toString())).build();
    }

    /**
     * DOWNLOAD  /torrents/download:id -> download the "id" torrent.
     */
    @RequestMapping(value = "/torrents/download/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Timed
    public void download(@PathVariable String id, HttpServletResponse response) {
        log.debug("REST request to download Torrent : {}", id);
        Torrent torrent = torrentRepository.findOne(id);

        File tempFile = null;
        InputStream inputStreamResource = null;

        try {
            tempFile = File.createTempFile("IT-", ".torrent");
            FileUtils.writeByteArrayToFile(tempFile, torrent.getFile());

            inputStreamResource = new FileInputStream(tempFile);
        } catch (IOException e) {

        }

//        return ResponseEntity
//            .ok()
//            .contentType(MediaType.APPLICATION_OCTET_STREAM)
//            .contentLength(tempFile.length())
//            .header("Content-Disposition", "attachment; filename=" + torrent.getName() + ".torrent")
//            .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + torrent.getName() + ".torrent");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentLength(tempFile.length());

        try {
            IOUtils.copy(inputStreamResource, response.getOutputStream());
            response.flushBuffer();
            inputStreamResource.close();
        } catch (IOException e) {

        }
    }
}
