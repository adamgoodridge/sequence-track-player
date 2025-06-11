package net.adamgoodridge.sequencetrackplayer.utils;

import net.adamgoodridge.sequencetrackplayer.feeder.*;

import java.util.*;

public class GenerateMp3Index {

    private static final Random rnd = new Random();
    final List<DataItem> children;
    private final int preferTime;

    public GenerateMp3Index(List<DataItem> children, int time) {
        this.children = children;
        this.preferTime = time;
    }

    public int compute() {
        if (preferTime != -1 && !children.get(0).getFullPath().contains(" ")) {
            //find a specific time
            DataItem found = findFileByTime();
            return children.indexOf(found);
        }
         return  rnd.nextInt(children.size());
    }
    private DataItem findFileByTime() {
        int time = preferTime;
        //in case if there is no track for that exact hour
        do {
            String startTime = String.format("%02d",time);
            String regex = ".*_" + startTime +
                    (children.get(0).getFullPath().contains("\\uF03A") ? "\uF03A\\d.*": "-[0-5]\\d.*");
            DataItem found = children.stream().filter(r-> r.getFullPath().matches(regex)).findFirst().orElse(null);
            time = (time + 1) % 23;
            if(found != null)
                return found;
        } while (time != preferTime);
        //if not found, return a random track
        return children.get(rnd.nextInt(children.size()));
    }
}
