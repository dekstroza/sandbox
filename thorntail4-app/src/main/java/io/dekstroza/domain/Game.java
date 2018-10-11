package io.dekstroza.domain;

import javax.persistence.*;

@Entity
public class Game {

    private Long id;
    private Integer playerCount;
    private String song;
    private String performer;
    private String band;
    private String totalScore;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "game")
    @TableGenerator(name = "game", table = "sequences", pkColumnName = "key", pkColumnValue = "game", valueColumnName = "seed")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(Integer playerCount) {
        this.playerCount = playerCount;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getPerformer() {
        return performer;
    }

    public void setPerformer(String performer) {
        this.performer = performer;
    }

    public String getBand() {
        return band;
    }

    public void setBand(String band) {
        this.band = band;
    }

    public String getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(String totalScore) {
        this.totalScore = totalScore;
    }
}
