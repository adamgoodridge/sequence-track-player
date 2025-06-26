package net.adamgoodridge.sequencetrackplayer.feeder;

import com.fasterxml.jackson.annotation.*;
import com.google.gson.annotations.*;
import net.adamgoodridge.sequencetrackplayer.*;
import net.adamgoodridge.sequencetrackplayer.browser.*;
import net.adamgoodridge.sequencetrackplayer.exceptions.*;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.*;

import java.util.*;
import java.util.concurrent.*;

@SuppressWarnings("unused")
@Document(collection = "currentAudioFeeder")
//old field in db or _class in json for tesṯing
@JsonIgnoreProperties(ignoreUnknown = true)
public class AudioFeeder {
    @Transient
    public static final String SEQUENCE_NAME = "audio_feeders";
    @Expose
    @Field
    private AudioIOFileManager audioInfo;
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
    private transient CompletableFuture<AudioIOFileManager> completableFuture;
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
        this.audioInfo = audioIOFileManager;
        //e.g /ACT/2020/2020-08_August/2020-08-29_Saturday/ACT_2020-08-29_Saturday_08-13_TO_08-43_011.mp3
        this.feedName = feedName;
    }

    public AudioIOFileManager getAudioIOFileManager() {
        return audioInfo;
    }

    public boolean isAudioIOFileManager() {
        return audioInfo != null;
    }

    public void setAudioInfo(AudioIOFileManager audioInfo) {
        this.audioInfo = audioInfo;
    }
    public void setAudioIOFileManager(AudioIOFileManager audioIOFileManager) {
        this.audioInfo = audioIOFileManager;
    }

    public AudioIOFileManager getPreviousAudioPlayed() {
        return previousAudioPlayed;
    }

    public String getFeedName() {
        return feedName;
    }

    public void setPreviousAudioPlayed(AudioIOFileManager previousAudioPlayed) {
        this.previousAudioPlayed = previousAudioPlayed;
    }

    public void setIncludeInFullScreenShuffle(boolean includeInFullScreenShuffle) {
        isIncludeInFullScreenShuffle = includeInFullScreenShuffle;
    }


    public void setErrorMessage(String completionErrorMessage) {
        this.errorMessage = completionErrorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }


    public CompletableFuture<AudioIOFileManager> getCompletableFuture() {
        return completableFuture;
    }

    public void setCompletableFuture(CompletableFuture<AudioIOFileManager> completableFuture) {
        this.completableFuture = completableFuture;
    }

    public void setFeedName(String feedName) {
        this.feedName = feedName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    // FEED/2021/2021-03_March/2021-03-25_Thursday if input FEED/2021/2021-03_March it will return 2021-03-25_Thursday if input
    public String fileInFolder(String currentPath) {
        //increase the audio file
        List<BreadCrumb> fileItems = BrowserUtils.generateFileItems(audioInfo.getFile().getHref().replace(ConstantText.DEFAULT_SERVER_URL, ""));

        int childIndex = fileItems.stream().map(BreadCrumb::path).toList().indexOf(currentPath) + 1;
        return fileItems.get(childIndex).name();
    }

    public boolean isIncludeInFullScreenShuffle() {
        return isIncludeInFullScreenShuffle;
    }

    public int getCurrentTrackCount() {
        return currentTrackCount;
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

    public boolean ableToPlay() {
        return audioInfo != null && this.getAudioIOFileManager().getFile().getFileName() != null;
    }

    public void setStartPath(String startPath) {
        this.startPath = startPath;
    }

    public String getStartPath() {
        return startPath;
    }

    public boolean isLoaded() {
        return audioInfo != null;
    }

    public void setIsIncludeInFullScreenShuffle(boolean isIncludeInFullScreenShuffle) {
        this.isIncludeInFullScreenShuffle = isIncludeInFullScreenShuffle;
    }

    public String displayText() {
        if(isLoaded())
            return audioInfo.getFile().getFullPathLocalFileSystem();
       return getFeedName() + " is loading";
    }

    public String displayName() {
        if(getFeedName().startsWith("/"))
            return getFeedName().split("/")[1];
        return feedName;
    }
    public void throwIfError() throws GetFeedException {
        if(errorMessage != null && !errorMessage.isBlank())
            throw new GetFeedException(errorMessage);
    }
}

