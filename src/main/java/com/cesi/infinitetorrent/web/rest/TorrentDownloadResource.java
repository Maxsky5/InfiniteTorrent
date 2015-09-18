package com.cesi.infinitetorrent.web.rest;

import com.cesi.infinitetorrent.domain.Torrent;
import com.cesi.infinitetorrent.repository.TorrentRepository;
import com.codahale.metrics.annotation.Timed;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
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
    @ResponseBody
    public void download(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) {
        log.debug("REST request to download Torrent : {}", id);

        Torrent torrent = torrentRepository.findOne(id);

        InputStream inputStream = new ByteArrayInputStream(torrent.getFile());

        HttpHeaders headers = new HttpHeaders();

        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader("Content-Disposition", "attachment; filename=" + torrent.getName() + ".torrent");

        try {
            OutputStream outStream = response.getOutputStream();
            IOUtils.copy(inputStream, outStream);

            inputStream.close();
            outStream.close();
        } catch (IOException e) {

        }
    }
}
