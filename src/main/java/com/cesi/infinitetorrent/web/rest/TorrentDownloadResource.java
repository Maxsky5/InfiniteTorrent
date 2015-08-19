package com.cesi.infinitetorrent.web.rest;

import com.cesi.infinitetorrent.domain.Torrent;
import com.cesi.infinitetorrent.repository.TorrentRepository;
import com.codahale.metrics.annotation.Timed;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
public class TorrentDownloadResource {

    private final Logger log = LoggerFactory.getLogger(TorrentDownloadResource.class);

    @Inject
    private TorrentRepository torrentRepository;

    /**
     * DOWNLOAD  /torrents/download:id -> download the "id" torrent.
     */
    @RequestMapping(value = "/api/torrents/download/{id}",
        method = RequestMethod.GET)
    @Timed
    public void download(@PathVariable String id, HttpServletResponse response) {
        log.debug("REST request to download Torrent : {}", id);
        Torrent torrent = torrentRepository.findOne(id);

        InputStream inputStream = new ByteArrayInputStream(torrent.getFile());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + torrent.getName() + ".torrent");

        try {
            IOUtils.copy(inputStream, response.getOutputStream());
            response.getOutputStream().write(torrent.getFile());
            response.flushBuffer();
            inputStream.close();
        } catch (IOException e) {

        }
    }
}
