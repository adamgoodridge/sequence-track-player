package net.adamgoodridge.sequencetrackplayer.mock.respository;

import net.adamgoodridge.sequencetrackplayer.bookmark.BookmarkedAudio;
import net.adamgoodridge.sequencetrackplayer.bookmark.BookmarkedAudioRepository;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bson.types.ObjectId;

public class BookmarkRepositoryMock extends AbstractMockRepository<BookmarkedAudio, BookmarkedAudioRepository> {
    public BookmarkRepositoryMock() {
        super(BookmarkedAudio.class);
    }

    protected BookmarkedAudio deserializeObject(JsonElement json) {
        JsonObject jsonObject = json.getAsJsonObject();
        BookmarkedAudio audio = new BookmarkedAudio();
        audio.setBookmarkId(String.valueOf(new ObjectId(jsonObject.getAsJsonObject("_id").get("$oid").getAsString())));
        audio.setPath(jsonObject.get("path").getAsString());
        return audio;
    }

    @Override
    protected String getFileJsonPath() {
        return "src/test/resources/TestData/radioDB.bookmarkedAudio.json";
    }

}
