package io.dekstroza.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Band implements BaseEntity<String> {

    @JsonProperty("id")
    private String id;

    @JsonProperty("bandName")
    private String bandName;

    @JsonProperty("members")
    private String members;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("bandName")
    public String getBandName() {
        return bandName;
    }

    public void setBandName(String bandName) {
        this.bandName = bandName;
    }

    @JsonProperty("members")
    public String getMembers() {
        return members;
    }

    public void setMembers(String memberNames) {
        this.members = memberNames;
    }

}
