package net.adamgoodridge.sequencetrackplayer.mock;

import com.google.gson.*;
import net.adamgoodridge.sequencetrackplayer.filesystem.*;

import java.io.*;
import java.util.*;

import static org.mockito.Mockito.*;

public class FileSystemMock {

    public static void setupFileMock(FileListSubFileWrapper fileWrapper) {
        HashMap<String, String[]> mockedFolder = getDataFromJson();
        //when(fileWrapper.wrap(anyString())).thenReturn(new String[0]);
            for(Map.Entry<String, String[]> entry : mockedFolder.entrySet()) {
                when(fileWrapper.wrap(entry.getKey())).thenReturn(entry.getValue());
            }
        }


    private static HashMap<String, String[]>  getDataFromJson(){
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
        } catch (IOException e) {
			throw new RuntimeException(e);
		}
		return result;
    }
}
