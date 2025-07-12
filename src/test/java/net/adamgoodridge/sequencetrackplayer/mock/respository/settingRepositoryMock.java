package net.adamgoodridge.sequencetrackplayer.mock.respository;

import com.google.gson.*;
import net.adamgoodridge.sequencetrackplayer.bookmark.*;
import net.adamgoodridge.sequencetrackplayer.settings.Setting;
import net.adamgoodridge.sequencetrackplayer.settings.SettingRepository;
import org.bson.types.*;

import static org.mockito.Mockito.when;

public class SettingRepositoryMock  extends AbstractMockRepository<Setting, SettingRepository> {
    public SettingRepositoryMock() {
        super(Setting.class);
    }

    protected Setting deserializeObject(JsonElement json) {
        JsonObject jsonObject = json.getAsJsonObject();
        Setting setting = new Setting();
        setting.setId(String.valueOf(new ObjectId(jsonObject.getAsJsonObject("_id").get("$oid").getAsString())));
        setting.setName(jsonObject.get("name").getAsString());
        setting.setValue(jsonObject.get("value").getAsString());
        return setting;
    }

    @Override
    protected String getFileJsonPath() {
        return "src/test/resources/TestData/radioDB.settings.json";
    }
}
