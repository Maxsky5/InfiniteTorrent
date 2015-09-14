package com.cesi.infinitetorrent.domain;

import com.cesi.infinitetorrent.domain.util.CustomDateTimeDeserializer;
import com.cesi.infinitetorrent.domain.util.CustomDateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;


/**
 * A Torrent.
 */
@Document(collection = "TORRENT")
public class Torrent implements Serializable {

    @Id
    private String id;

    @NotNull
    @Field("name")
    private String name;


    @Field("comment")
    private String comment;


    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Field("created")
    private DateTime created;


    @Field("created_by")
    private String createdBy;


    @Field("total_size")
    private Long totalSize;


    @Field("file")
    private byte[] file;

    @Field("leechers")
    private Integer leechers;

    @Field("seeders")
    private Integer seeders;

    @DBRef
    private List<Tag> tags;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public DateTime getCreated() {
        return created;
    }

    public void setCreated(DateTime created) {
        this.created = created;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(Long totalSize) {
        this.totalSize = totalSize;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public Integer getLeechers() {
        return leechers;
    }

    public void setLeechers(Integer leechers) {
        this.leechers = leechers;
    }

    public Integer getSeeders() {
        return seeders;
    }

    public void setSeeders(Integer seeders) {
        this.seeders = seeders;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Torrent torrent = (Torrent) o;

        if (!Objects.equals(id, torrent.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Torrent{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", comment='" + comment + "'" +
            ", created='" + created + "'" +
            ", createdBy='" + createdBy + "'" +
            ", totalSize='" + totalSize + "'" +
            ", file='" + file + "'" +
            '}';
    }
}
