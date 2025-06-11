package net.adamgoodridge.sequencetrackplayer;


import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

@Controller("/")
@SuppressWarnings("unused")
public class HomeController {
    @RequestMapping("/")
    public String  home() {
        return "redirect:/feed/list";
    }
    //for menu bar
    @RequestMapping("/home")
    public String  homePage() {
        return "redirect:/feed/list";
    }

}
