package net.adamgoodridge.sequencetrackplayer.thymeleaf;

import lombok.*;
import net.adamgoodridge.sequencetrackplayer.feeder.*;

/*
This class decides what text to show in thymeleaf template
 */
@SuppressWarnings("unused")
@Getter
public class AudioFeederItemDTO {

    private final long id;
    private final String text;
    private final String feedName;
    private final Boolean isIncludeInFullScreenShuffle;
    public AudioFeederItemDTO(AudioFeeder audioFeeder) {
        id = audioFeeder.getId();
        text = audioFeeder.displayText();
        feedName = audioFeeder.displayName();
        isIncludeInFullScreenShuffle = audioFeeder.isIncludeInFullScreenShuffle();
    }


    @Override
    public String toString() {
        return "AudioFeederItem{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", feedName='" + feedName + '\'' +
                ", isIncludeInFullScreenShuffle=" + isIncludeInFullScreenShuffle +
                '}';
    }
    
    public Boolean isIncludeInFullScreenShuffle() {
        return isIncludeInFullScreenShuffle;
    }
}

/*
This replaces and adds more function to:

   <span th:each="value : ${audioFeeder}">
	   <span th:if="${value.getAudioInfo() != null}" >
		   <a th:href="@{/feed/get/{feedId}(feedId=${value.getId()})}" class="list-group-item list-group-item-action" style="padding-top: 3px" th:text="${value.getAudioInfo().getFile().getFullPath()}"></a>
		</span>
	   <span th:if="${value.getAudioInfo() == null}" >
		   <a th:href="@{/feed/get/{feedId}(feedId=${value.getId()})}" class="list-group-item list-group-item-action" style="padding-top: 3px" th:text="${value.getFeedName() + 'is loading'}"></a>
		</span>
   </span>
 */