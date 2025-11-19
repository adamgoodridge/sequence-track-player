package net.adamgoodridge.sequencetrackplayer.feeder;

import com.fasterxml.jackson.annotation.*;
import com.google.gson.annotations.*;
import lombok.*;
import net.adamgoodridge.sequencetrackplayer.browser.*;
import net.adamgoodridge.sequencetrackplayer.constanttext.*;
import net.adamgoodridge.sequencetrackplayer.exceptions.errors.*;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.*;

import java.util.*;
import java.util.concurrent.*;

@SuppressWarnings("unused")
@Document(collection = "currentAudioFeeder")
//old field in db or _class in json for tesṯing
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AudioFeeder {
    @Transient
    public static final String SEQUENCE_NAME = "audio_feeders";
    @Expose
    @Field("audioInfo")
    private AudioIOFileManager audioIOFileManager;
    @Field
    private AudioIOFileManager previousAudioPlayed;
    @Field
    private String feedName;
    @Field
    private String sessionId;
    //If it will be shown in the shuffle full shuffle
    @Field
    private boolean isIncludeInFullScreenShuffle;
    @Field
    @Transient
    private CompletableFuture<AudioIOFileManager> completableFuture;
    @Field
    private String errorMessage;
    @Field
    private int currentTrackCount;

    //to find by a number
    @Id
    private long id;
    @Field
    private String startPath;

    @Field
    private DataItem dataItem;
    //needed for hibernate
    public AudioFeeder() {

    }

    public AudioFeeder(String feedName) {
        this.feedName = feedName;
    }

    public AudioFeeder(CompletableFuture<AudioIOFileManager> completableFuture) {
        this.completableFuture = completableFuture;
    }

    public AudioFeeder(String feedName, AudioIOFileManager audioIOFileManager) {
        this.audioIOFileManager = audioIOFileManager;
        //e.g /ACT/2020/2020-08_August/2020-08-29_Saturday/ACT_2020-08-29_Saturday_08-13_TO_08-43_011.mp3
        this.feedName = feedName;
    }

    // FEED/2021/2021-03_March/2021-03-25_Thursday if input FEED/2021/2021-03_March it will return 2021-03-25_Thursday if input
    public String fileInFolder(String currentPath) {
        //increase the audio file
        List<BreadCrumb> fileItems = BrowserUtils.generateFileItems(audioIOFileManager.getFile().getHref().replace(ConstantText.DEFAULT_SERVER_URL, ""));

        int childIndex = fileItems.stream().map(BreadCrumb::path).toList().indexOf(currentPath) + 1;
        return fileItems.get(childIndex).name();
    }

    public void increaseTrackCount() {
        currentTrackCount++;
    }

    public void decreaseTrackCount() {
        if (currentTrackCount != 0)
            currentTrackCount--;
    }

    public void resetTrackCount() {
        currentTrackCount = 0;
    }


    public boolean isLoaded() {
        return audioIOFileManager != null;
    }

    public String displayText() {
        try {
            if (isLoaded())
                return audioIOFileManager.getFile().getFileName();
        } catch (Exception ignored) {
            return getFeedName() + "(id:" + getId() + ")" + " has an invalid path";
        }
       return getFeedName() + " is loading";
    }

    public String displayName() {
        if(getFeedName().startsWith("/"))
            return getFeedName().split("/")[1];
        return feedName;
    }
    public void throwIfError() throws GetFeedError {
        if(errorMessage != null && !errorMessage.isBlank())
            throw new GetFeedError(errorMessage);
    }
}

