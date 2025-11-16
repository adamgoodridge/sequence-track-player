package net.adamgoodridge.sequencetrackplayer;

import net.adamgoodridge.sequencetrackplayer.constanttext.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller("/")
@SuppressWarnings("unused")
public class HomeController {

    @RequestMapping("/")
    public String home() {
        return "redirect:/feed/list";
    }

    //for menu bar
    @RequestMapping("/home")
    public String homePage() {
        return "redirect:/feed/list";
    }
}
