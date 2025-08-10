package com.example.finalproject;

import java.util.Comparator;

public class Run {
    private String name;
    private double distanceInMiles;
    private double timeInMinutes;

    public Run(String name, double distanceInMiles, double timeInMinutes) {
        if (name.isEmpty()) throw new IllegalArgumentException("Name cannot be blank or null");
        if (distanceInMiles <= 0) throw new IllegalArgumentException("Distance must be greater than 0 miles");
        if (timeInMinutes <= 0) throw new IllegalArgumentException("Time must be greater than 0 minutes");

        this.name = name;
        this.distanceInMiles = distanceInMiles;
        this.timeInMinutes = timeInMinutes;
    }

    public String getName() {
        return name;
    }
    public double getDistanceInMiles() {
        return distanceInMiles;
    }
    public double getTimeInMinutes() {
        return timeInMinutes;
    }

    public double paceInMinPerMile() {
        return timeInMinutes / distanceInMiles;
    }

    public String formattedToHMS(double timeInMinutes) {
        long totalSeconds = Math.round(timeInMinutes * 60);
        long h = totalSeconds / 3600;
        long m = (totalSeconds % 3600) / 60;
        long s = totalSeconds % 60;
        if (h > 0) return String.format("%d:%02d:%02d", h, m, s);
        return String.format("%d:%02d", m, s); // mm:ss when under an hour
    }

    public String getFormattedTime() {
        return formattedToHMS(timeInMinutes);
    }
    public String getFormattedPace() {
        return formattedToHMS(paceInMinPerMile());
    }

    public static final Comparator<Run> BY_NAME = Comparator.comparing(Run::getName, String.CASE_INSENSITIVE_ORDER);
    public static final Comparator<Run> BY_DISTANCE_ASC = Comparator.comparingDouble(Run::getDistanceInMiles);
    public static final Comparator<Run> BY_TIME_ASC = Comparator.comparingDouble(Run::getTimeInMinutes);
    public static final Comparator<Run> BY_PACE_ASC = Comparator.comparingDouble(Run::paceInMinPerMile);
}
