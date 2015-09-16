package com.cesi.infinitetorrent.web.rest;

import com.cesi.infinitetorrent.domain.InfiniteTracker;
import com.cesi.infinitetorrent.repository.InfiniteTrackerRepository;
import com.cesi.infinitetorrent.web.rest.util.HeaderUtil;
import com.cesi.infinitetorrent.web.rest.util.PaginationUtil;
import com.codahale.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Trackers.
 */
@RestController
@RequestMapping("/api")
public class InfiniteTrackerResource {

    private final Logger log = LoggerFactory.getLogger(InfiniteTrackerResource.class);

    @Inject
    private InfiniteTrackerRepository infiniteTrackerRepository;

    /**
     * POST  /trackers -> Create a new tracker.
     */
    @RequestMapping(value = "/trackers",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<InfiniteTracker> create(@Valid @RequestBody InfiniteTracker tracker) throws URISyntaxException {
        log.debug("REST request to save InfiniteTracker : {}", tracker);
        if (tracker.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new tracker cannot already have an ID").body(null);
        }

        InfiniteTracker result = infiniteTrackerRepository.save(tracker);

        return ResponseEntity.created(new URI("/api/trackers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("tracker", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /trackers -> Updates an existing tracker.
     */
    @RequestMapping(value = "/trackers",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<InfiniteTracker> update(@Valid @RequestBody InfiniteTracker infiniteTracker) throws URISyntaxException {
        log.debug("REST request to update InfiniteTracker : {}", infiniteTracker);

        if (infiniteTracker.getId() == null ||infiniteTracker.getId().isEmpty()) {
            return ResponseEntity.badRequest().header("Failure", "A tracker cannot have an empty ID").body(null);
        }

        if (null == infiniteTrackerRepository.findOne(infiniteTracker.getId())) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        InfiniteTracker result = infiniteTrackerRepository.save(infiniteTracker);

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("tracker", infiniteTracker.getId().toString()))
            .body(result);
    }

    /**
     * GET  /trackers -> get all the trackers.
     */
    @RequestMapping(value = "/trackers",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<InfiniteTracker>> getAll(@RequestParam(value = "page", required = false) Integer offset,
                                                        @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<InfiniteTracker> page = infiniteTrackerRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/trackers", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /trackers/:id -> get the "id" tracker.
     */
    @RequestMapping(value = "/trackers/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<InfiniteTracker> get(@PathVariable String id) {
        log.debug("REST request to get InfiniteTracker : {}", id);
        return Optional.ofNullable(infiniteTrackerRepository.findOne(id))
            .map(tracker -> new ResponseEntity<>(
                tracker,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /trackers/:id -> delete the "id" tracker.
     */
    @RequestMapping(value = "/trackers/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> delete(@PathVariable String id) {
        log.debug("REST request to delete InfiniteTracker : {}", id);
        infiniteTrackerRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("tracker", id.toString())).build();
    }
}
