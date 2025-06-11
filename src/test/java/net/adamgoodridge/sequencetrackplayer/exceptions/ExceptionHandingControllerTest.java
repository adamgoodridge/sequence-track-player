package net.adamgoodridge.sequencetrackplayer.exceptions;

import net.adamgoodridge.sequencetrackplayer.exceptions.errors.JsonReturnError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ExceptionHandingControllerTest {

    private ExceptionHandingController controller;
    private Gson gson;

    @BeforeEach
    void setUp() {
        controller = new ExceptionHandingController();
        gson = new Gson();
    }

    @Test
    void jsonReturnError_shouldReturnJsonWithErrorMessage_whenNoFeedName() {
        String errorMessage = "Test error message";
        JsonReturnError error = new JsonReturnError(errorMessage);

        String jsonResult = controller.jsonReturnError(error);
        Map<String, String> resultMap = gson.fromJson(jsonResult, new TypeToken<Map<String, String>>(){}.getType());

        assertEquals(errorMessage, resultMap.get("error"), "Error message should match in JSON");
        assertFalse(resultMap.containsKey("feedName"), "feedName should not be present when not provided");
    }

    @Test
    void jsonReturnError_shouldReturnJsonWithErrorMessageAndFeedName_whenFeedNameProvided() {
        String errorMessage = "Test error message";
        String feedName = "testFeed";
        JsonReturnError error = new JsonReturnError(errorMessage, feedName);

        String jsonResult = controller.jsonReturnError(error);
        Map<String, String> resultMap = gson.fromJson(jsonResult, new TypeToken<Map<String, String>>(){}.getType());

        assertEquals(errorMessage, resultMap.get("error"), "Error message should match in JSON");
        assertEquals(feedName, resultMap.get("feedName"), "Feed name should match in JSON");
    }

}
