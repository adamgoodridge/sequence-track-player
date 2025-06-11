package net.adamgoodridge.sequencetrackplayer.shortcut;

import org.springframework.data.mongodb.core.mapping.*;

import javax.persistence.*;

@Entity
@Document("shortcuts")
public class Shortcut {
    @Id
    private String id;

    private String feedName;

    private String logoPath;
    @Transient
    private String title;

    public Shortcut(String feedName, String logoPath) {
        this.feedName = feedName;
        this.logoPath = logoPath;
    }

    public Shortcut() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFeedName() {
        return feedName;
    }

    public void setFeedName(String name) {
        this.feedName = name;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String feedTitle) {
        this.title = feedTitle;
    }
}
