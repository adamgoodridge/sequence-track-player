package net.adamgoodridge.sequencetrackplayer.exceptions;

import com.google.gson.*;
import net.adamgoodridge.sequencetrackplayer.exceptions.errors.*;
import org.springframework.http.*;
import org.springframework.ui.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static net.adamgoodridge.sequencetrackplayer.constanttext.ConstantText.*;

@SuppressWarnings({"SameReturnValue","unused"})
@ControllerAdvice
public class ExceptionHandingController {
    private static final String PAGE_ERROR = "custom-error";
    @ExceptionHandler(NoSuchElementException.class)
    public String notFoundBookmark(Model model) {
        model.addAttribute(ERROR_PAGE_ATTRIBUTE_HEADING,"Not found bookmark");
        model.addAttribute(ERROR_PAGE_ATTRIBUTE_INFO , "The request bookmark couldn't be found in the database");
        return PAGE_ERROR;
    }
    @ExceptionHandler(NotFoundError.class)
    public String fileNotFoundBookmark(Model model) {
        model.addAttribute(ERROR_PAGE_ATTRIBUTE_HEADING,"Not found bookmark");
        model.addAttribute(ERROR_PAGE_ATTRIBUTE_INFO , "The request bookmark couldn't be found in the file system");
        return PAGE_ERROR;
    }
    @ExceptionHandler(ServerFailConnectionError.class)
    public String serverFailConnection(Model model, ServerFailConnectionError serverFailConnectionError) {
        model.addAttribute(ERROR_PAGE_ATTRIBUTE_HEADING,"File Server Connection");
        model.addAttribute(ERROR_PAGE_ATTRIBUTE_INFO ,serverFailConnectionError.getMessage());
        return PAGE_ERROR;
    }

    @ExceptionHandler(ServerError.class)
    public String serverError(Model model, ServerError serverError) {
        model.addAttribute(ERROR_PAGE_ATTRIBUTE_HEADING,"Server Error");
        model.addAttribute(ERROR_PAGE_ATTRIBUTE_INFO , serverError.getMessage());
        return PAGE_ERROR;
    }
    @ExceptionHandler(ServerFilePathFeedError.class)
    public String serverError(Model model, ServerFilePathFeedError error) {
        model.addAttribute(ERROR_PAGE_ATTRIBUTE_HEADING,"Server loading Error");
        model.addAttribute(ERROR_PAGE_ATTRIBUTE_INFO , error.getMessage());
        model.addAttribute("feedId",error.getFeedId());
        return "feed-error";
    }

    @ExceptionHandler(JsonReturnError.class)
    public @ResponseBody String jsonReturnError(JsonReturnError error) {
        Map<String, String> output = new HashMap<>();
        Gson gson = new Gson();
        output.put("error", error.getMessage());
        //return the feed id if we know
        if(error.getFeedName() != null) {
            output.put("feedName", error.getFeedName());
        }
        return gson.toJson(output);
    }
    @ExceptionHandler(JsonNotFoundError.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody String jsonNotFoundError(JsonNotFoundError error) {
        Map<String, String> output = new HashMap<>();
        Gson gson = new Gson();
        output.put("error", error.getMessage());
        return gson.toJson(output);
    }
    @ExceptionHandler(GetFeedError.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody String getFeedError(JsonNotFoundError error) {
        Map<String, String> output = new HashMap<>();
        Gson gson = new Gson();
        output.put("error", error.getMessage());
        return gson.toJson(output);
    }
}
