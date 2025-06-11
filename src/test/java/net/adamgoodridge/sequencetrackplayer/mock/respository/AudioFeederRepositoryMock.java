package net.adamgoodridge.sequencetrackplayer.mock.respository;

import net.adamgoodridge.sequencetrackplayer.feeder.AudioFeeder;
import net.adamgoodridge.sequencetrackplayer.feeder.repository.AudioFeederRepository;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class AudioFeederRepositoryMock extends AbstractMockRepository<AudioFeeder, AudioFeederRepository> {

    public AudioFeederRepositoryMock() {
        super(AudioFeeder.class);
    }

    protected AudioFeeder deserializeObject(JsonElement json) {
        Gson gson = new Gson();
        return gson.fromJson(json, AudioFeeder.class);
    }

    @Override
    protected String getFileJsonPath() {
        return "src/test/resources/TestData/radioDB.currentAudioFeeder.json";
    }

}