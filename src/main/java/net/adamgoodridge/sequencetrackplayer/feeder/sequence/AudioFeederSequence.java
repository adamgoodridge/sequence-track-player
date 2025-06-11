package net.adamgoodridge.sequencetrackplayer.feeder.sequence;

import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.*;


//https://www.javaprogramto.com/2019/05/spring-boot-mongodb-auto-generated-field.html
@Document(collection = "current_audio_feeder_sequences")
public class AudioFeederSequence {

    @Id
    private String id;

    private long seq;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getSeq() {
        return seq;
    }
}
