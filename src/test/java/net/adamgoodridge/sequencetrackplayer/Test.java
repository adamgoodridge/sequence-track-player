package net.adamgoodridge.sequencetrackplayer;

import net.adamgoodridge.sequencetrackplayer.exceptions.*;
import net.adamgoodridge.sequencetrackplayer.feeder.*;
import net.adamgoodridge.sequencetrackplayer.filesystem.*;
import net.adamgoodridge.sequencetrackplayer.settings.*;
import org.springframework.boot.test.context.*;

import java.util.*;

@SpringBootTest
public class Test {
    public static void main(String[] args) throws GetFeedException {
        String hi = "hi";
        String[] args1 = new String[]{hi};
        List<String> argsList = new ArrayList<>(Arrays.asList(args1));
        System.out.println("argsList = " + argsList);


    }
}
