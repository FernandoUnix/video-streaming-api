package com.video.streaming.api.util;

public class Time {

    public static String getRunningTime(Integer totalSeconds) {

        if (totalSeconds != null) {
            int hours = totalSeconds / 3600;
            int minutes = (totalSeconds % 3600) / 60;
            int seconds = totalSeconds % 60;

            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }

        return "N/A";
    }
}
