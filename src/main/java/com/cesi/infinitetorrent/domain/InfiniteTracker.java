package com.cesi.infinitetorrent.domain;

import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.net.URI;


/**
 * A Tracker.
 */
@Document(collection = "TRACKER")
public class InfiniteTracker {

    @Id
    private String id;

    @NotNull
    @Field("host")
    private String host;

    @NotNull
    @Field("url")
    private String url;

    @Field("date_last_sync")
    private DateTime dateLastSync;

    public InfiniteTracker() {
    }

    public InfiniteTracker(URI uri) {
        this.setHost(uri.getHost());
        this.setUrl(uri.toString());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public DateTime getDateLastSync() {
        return dateLastSync;
    }

    public void setDateLastSync(DateTime dateLastSync) {
        this.dateLastSync = dateLastSync;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InfiniteTracker that = (InfiniteTracker) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (host != null ? !host.equals(that.host) : that.host != null) return false;
        return !(url != null ? !url.equals(that.url) : that.url != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (host != null ? host.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }
}
