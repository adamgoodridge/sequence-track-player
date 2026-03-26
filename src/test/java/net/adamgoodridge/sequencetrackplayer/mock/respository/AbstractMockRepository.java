package net.adamgoodridge.sequencetrackplayer.mock.respository;

import com.google.gson.*;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public abstract class AbstractMockRepository<T, R extends MongoRepository> {
    private final Class<T> typeParameterClass;

    protected AbstractMockRepository(Class<T> typeParameterClass) {
        this.typeParameterClass = typeParameterClass;
    }


    protected List<T> getList() throws IOException {
        String jsonContent = new String(Files.readAllBytes(Paths.get(getFileJsonPath())));
        JsonArray array = JsonParser.parseString(jsonContent).getAsJsonArray();
        List<T> result = new ArrayList<>();
        for (JsonElement elem : array) {
            result.add(deserializeObject(elem));
        }
        return result;
    }
    protected abstract T deserializeObject(JsonElement jsonElement);

    protected abstract String getFileJsonPath();

    public void fillWithMockData(R repo) throws IOException {
        repo.deleteAll();
        List<T> mockData = getList();
        repo.saveAll(mockData);
    }
}
