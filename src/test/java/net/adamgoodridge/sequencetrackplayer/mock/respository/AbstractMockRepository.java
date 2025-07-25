package net.adamgoodridge.sequencetrackplayer.mock.respository;

import net.adamgoodridge.sequencetrackplayer.bookmark.BookmarkedAudio;
import net.adamgoodridge.sequencetrackplayer.GsonConfig;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public abstract class AbstractMockRepository<T, R extends MongoRepository> {
    private final Class<T> typeParameterClass;

    protected AbstractMockRepository(Class<T> typeParameterClass) {
        this.typeParameterClass = typeParameterClass;
    }


    protected List<T> getList() throws IOException {
        String jsonContent = new String(Files.readAllBytes(Paths.get(getFileJsonPath())));

        Gson gson = new GsonConfig().gson().newBuilder()
                .registerTypeAdapter(ObjectId.class, (JsonDeserializer<ObjectId>) (JsonElement json, Type typeOfT, JsonDeserializationContext context) ->{
                        if (json.isJsonObject() && json.getAsJsonObject().has("$oid")) {
                            //convert the JSON object's attribute to an MongoDB ObjectId
                            return new ObjectId(json.getAsJsonObject().get("$oid").getAsString());
                        }
                        throw new JsonParseException("Invalid ObjectId format");
                    })
                .registerTypeAdapter(BookmarkedAudio.class, (JsonDeserializer<T>) (JsonElement json, Type typeOfT, JsonDeserializationContext context) ->
                        deserializeObject(json)
                ).create();
        
        Type listType = TypeToken.getParameterized(List.class, typeParameterClass).getType();
        return gson.fromJson(jsonContent, listType);
    }
    protected abstract T deserializeObject(JsonElement jsonElement);

    protected abstract String getFileJsonPath();

    public void fillWithMockData(R repo) throws IOException {
        repo.deleteAll();
        List<T> mockData = getList();
        repo.saveAll(mockData);
    }
}
