package com.cesi.infinitetorrent.web.rest.dto;

import com.cesi.infinitetorrent.domain.InfiniteTracker;

public class InfiniteTrackerSynchronizeDto {
    private InfiniteTracker infiniteTracker;
    private Long number;

    public InfiniteTrackerSynchronizeDto() {
    }

    public InfiniteTrackerSynchronizeDto(InfiniteTracker infiniteTracker, Long number) {
        this.infiniteTracker = infiniteTracker;
        this.number = number;
    }

    public InfiniteTracker getInfiniteTracker() {
        return infiniteTracker;
    }

    public void setInfiniteTracker(InfiniteTracker infiniteTracker) {
        this.infiniteTracker = infiniteTracker;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }
}
