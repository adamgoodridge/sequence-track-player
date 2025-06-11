package net.adamgoodridge.sequencetrackplayer.mock.respository;

import net.adamgoodridge.sequencetrackplayer.shortcut.Shortcut;
import net.adamgoodridge.sequencetrackplayer.shortcut.ShortcutRepository;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class ShortcutRepositoryMock extends AbstractMockRepository<Shortcut, ShortcutRepository> {

    public ShortcutRepositoryMock() {
        super(Shortcut.class);
    }

    protected Shortcut deserializeObject(JsonElement json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Shortcut.class);
    }

    @Override
    protected String getFileJsonPath() {
        return "src/test/resources/TestData/radioDB.currentShortcut.json";
    }

}