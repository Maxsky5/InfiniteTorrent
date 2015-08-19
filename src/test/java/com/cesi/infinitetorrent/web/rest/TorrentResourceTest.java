package com.cesi.infinitetorrent.web.rest;

import com.cesi.infinitetorrent.Application;
import com.cesi.infinitetorrent.domain.Torrent;
import com.cesi.infinitetorrent.repository.TorrentRepository;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Base64Utils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the TorrentResource REST controller.
 *
 * @see TorrentResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class TorrentResourceTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";
    private static final String DEFAULT_COMMENT = "SAMPLE_TEXT";
    private static final String UPDATED_COMMENT = "UPDATED_TEXT";

    private static final DateTime DEFAULT_CREATED = new DateTime(0L, DateTimeZone.UTC);
    private static final DateTime UPDATED_CREATED = new DateTime(DateTimeZone.UTC).withMillisOfSecond(0);
    private static final String DEFAULT_CREATED_STR = dateTimeFormatter.print(DEFAULT_CREATED);
    private static final String DEFAULT_CREATED_BY = "SAMPLE_TEXT";
    private static final String UPDATED_CREATED_BY = "UPDATED_TEXT";
    private static final Long DEFAULT_TOTAL_SIZE = 0L;
    private static final Long UPDATED_TOTAL_SIZE = 0L;

    private static final byte[] DEFAULT_FILE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_FILE = TestUtil.createByteArray(2, "1");

    @Inject
    private TorrentRepository torrentRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restTorrentMockMvc;

    private Torrent torrent;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TorrentResource torrentResource = new TorrentResource();
        ReflectionTestUtils.setField(torrentResource, "torrentRepository", torrentRepository);
        this.restTorrentMockMvc = MockMvcBuilders.standaloneSetup(torrentResource).setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        torrentRepository.deleteAll();
        torrent = new Torrent();
        torrent.setName(DEFAULT_NAME);
        torrent.setComment(DEFAULT_COMMENT);
        torrent.setCreated(DEFAULT_CREATED);
        torrent.setCreatedBy(DEFAULT_CREATED_BY);
        torrent.setTotalSize(DEFAULT_TOTAL_SIZE);
        torrent.setFile(DEFAULT_FILE);
    }

    @Test
    public void getNonExistingTorrent() throws Exception {
        // Get the torrent
        restTorrentMockMvc.perform(get("/api/torrents/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteTorrent() throws Exception {
        // Initialize the database
        torrentRepository.save(torrent);

		int databaseSizeBeforeDelete = torrentRepository.findAll().size();

        // Get the torrent
        restTorrentMockMvc.perform(delete("/api/torrents/{id}", torrent.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Torrent> torrents = torrentRepository.findAll();
        assertThat(torrents).hasSize(databaseSizeBeforeDelete - 1);
    }
}
