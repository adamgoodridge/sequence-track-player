package net.adamgoodridge.sequencetrackplayer.shortcut;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.*;

import javax.persistence.*;

@NoArgsConstructor
@Data
@Entity
@Document("shortcuts")
public class Shortcut {
    @Id
    private String id;

    private String feedName;

    private String logoPath;
    @Transient
    private String title;
    @Transient
    private int count;

    public Shortcut(String feedName, String logoPath) {
        this.feedName = feedName;
        this.logoPath = logoPath;
    }


}
