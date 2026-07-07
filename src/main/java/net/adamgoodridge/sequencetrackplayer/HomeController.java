package net.adamgoodridge.sequencetrackplayer;

import com.google.gson.*;
import net.adamgoodridge.sequencetrackplayer.constanttext.*;
import net.adamgoodridge.sequencetrackplayer.exceptions.errors.*;
import net.adamgoodridge.sequencetrackplayer.feeder.*;
import net.adamgoodridge.sequencetrackplayer.utils.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;
@Controller("/")
@SuppressWarnings("unused")
public class HomeController {

    private final Gson gson = new Gson();

    @RequestMapping("/")
    public String home() {
        return "redirect:/feed/list";
    }

    //for menu bar
    @RequestMapping("/home")
    public String homePage() {
        return "redirect:/feed/list";
    }

    @RequestMapping("/date")
    @ResponseBody
    public String datePage() {
        Map<String, String> output = new HashMap<>();
        output.put("currentDate", TimeUtils.getInstance().getCurrentDate().toString());
        return gson.toJson(output);
    }
}
