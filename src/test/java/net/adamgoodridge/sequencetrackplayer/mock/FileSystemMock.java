package net.adamgoodridge.sequencetrackplayer.mock;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.mockito.MockedConstruction;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

public class FileSystemMock {

    private static volatile HashMap<String, String[]> dataFromFile;
    private static final Object lock = new Object();

    public static MockedConstruction<File> process(String expectedPath, String[] expectedFiles) {
        return process(new HashMap<String, String[]>() {{ put(expectedPath, expectedFiles); }});
    }
    public static MockedConstruction<File> MockFromJsonFile() {
        if(dataFromFile == null) {
            synchronized (lock) {
                if (dataFromFile == null) { // Double-checked locking
                    // Load data from JSON file only once
                    try {
                        dataFromFile = getDataFromJson();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return process(dataFromFile);
    }

    public static MockedConstruction<File> process(HashMap<String, String[]> folders) {
        return mockConstruction(File.class, (mock, context) -> {
            // Get the constructor arguments
            Object[] args = context.arguments().toArray();
            String path = args.length > 0 ? args[0].toString() : "";
            when(mock.list()).thenReturn(folders.containsKey(path) ? folders.get(path) : new String[0]);
        });
    }

    private static HashMap<String, String[]>  getDataFromJson() throws IOException {
        HashMap<String, String[]> result = new HashMap<>();

        try (InputStream inputStream = FileSystemMock.class.getResourceAsStream("/TestData/FileSystem.json");
             InputStreamReader reader = new InputStreamReader(inputStream)) {

            JsonObject jsonObject = new Gson().fromJson(reader, JsonObject.class);
            JsonArray data = jsonObject.getAsJsonArray("data");

            for (JsonElement element : data) {
                JsonObject obj = element.getAsJsonObject();
                String path = obj.get("name").getAsString();
                JsonArray items = obj.getAsJsonArray("items");
                // Convert JsonArray to String[]
                String[] itemsArray = new String[items.size()];
                for (int i = 0; i < items.size(); i++) {
                    itemsArray[i] = items.get(i).getAsString();
                }

                // Handle root directory
                result.put(path, itemsArray);
            }
        }
        return result;
    }
}
