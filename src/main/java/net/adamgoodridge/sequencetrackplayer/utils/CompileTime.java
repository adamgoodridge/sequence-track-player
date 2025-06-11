package net.adamgoodridge.sequencetrackplayer.utils;

import java.io.*;
import java.text.*;
import java.util.*;

public class CompileTime {
    private CompileTime() {
        // Prevent instantiation
    }
    /**
     * Returns the compile time of the class.
     * @return The compile time of the class.
     */
    public static String get(){
        String path = CompileTime.class.getResource(CompileTime.class.getSimpleName() + ".class").getPath();
        long timestamp = new File(path).lastModified();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, yyyy h:mm:ss a");
        return "Compile time: " + sdf.format(new Date(timestamp));
    }

}

