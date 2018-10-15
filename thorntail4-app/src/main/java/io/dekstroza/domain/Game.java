package io.dekstroza.domain;

import javax.persistence.*;

@Entity
public class Game implements BaseEntity<Long> {

    private Long id;
    private Band band;
    private Song song;
    private Long votes;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "game")
    @TableGenerator(name = "game", table = "sequences", pkColumnName = "key", pkColumnValue = "game", valueColumnName = "seed")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    public Band getBand() {
        return band;
    }

    public void setBand(Band band) {
        this.band = band;
    }

    @ManyToOne
    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public Long getVotes() {
        return votes;
    }

    public void setVotes(Long votes) {
        this.votes = votes;
    }
}
