package com.cesi.infinitetorrent.domain;

import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.net.URI;
import java.util.Objects;


/**
 * A Tracker.
 */
@Document(collection = "TRACKER")
public class InfiniteTracker implements Serializable {

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
        if (this == o) {
            return true;

        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        InfiniteTracker tag = (InfiniteTracker) o;

        if (!Objects.equals(id, tag.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Tag{" +
            "id=" + id +
            ", host='" + host + "'" +
            ", url='" + url + "'" +
            '}';
    }
}
