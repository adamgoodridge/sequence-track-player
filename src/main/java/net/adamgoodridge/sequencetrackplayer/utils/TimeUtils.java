package net.adamgoodridge.sequencetrackplayer.utils;


import net.adamgoodridge.sequencetrackplayer.exceptions.errors.*;

import java.text.*;
import java.util.*;

public class TimeUtils {

    public static String getDay(String inputDate) {
        //gives a day of the day when given a date
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        Date dt1;
        try {
            dt1 = format1.parse(inputDate);
        } catch (ParseException e) {
            throw new ServerError("The date of" + inputDate + "cannot be parse to a day of week, please try again");
        }
        DateFormat format2 = new SimpleDateFormat("EEEE");
        return format2.format(dt1);
    }
}
