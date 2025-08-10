package com.example.finalproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Comparator;
import java.util.Locale;


public class RunStore {
    private final ObservableList<Run> all = FXCollections.observableArrayList();
    private final SortedList<Run> sortedList = new SortedList<>(all);

    public SortedList<Run> getSortedList() {
        return sortedList;
    }

    public void add(Run r) {
        all.add(r);
    }

    public void clear() {
        all.clear();
    }

    public void setComparator(Comparator<Run> c, boolean asc) {
        if (asc) {
            sortedList.setComparator(c);
        } else {
            sortedList.setComparator(c.reversed());
        }
    }

    public void exportCsv (Path p) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(p, StandardCharsets.UTF_8)) {
            bw.write("name,distance_miles,time_hhmmss,pace_mm_per_mile");
            bw.newLine();
            DecimalFormat decimalFormat = new DecimalFormat("0.0#", DecimalFormatSymbols.getInstance(Locale.US));
            for (Run r: all) {
                String rName = r.getName();
                String escaped = rName.replace("\"", "\"\"");
                String safeName;
                String commaLine = ",";
                boolean needsQuoting = rName.contains("\"") || rName.contains(",");
                if (needsQuoting) {
                    safeName = "\"" + escaped + "\"";
                } else {
                    safeName = rName;
                }
                String distance_miles = decimalFormat.format(r.getDistanceInMiles());
                String time_minutes = r.getFormattedTime();
                String pace_min_per_mile = r.getFormattedPace();
                bw.write(safeName);
                bw.write(commaLine);
                bw.write(distance_miles);
                bw.write(commaLine);
                bw.write(String.valueOf(time_minutes));
                bw.write(commaLine);
                bw.write(String.valueOf(pace_min_per_mile));
                bw.newLine();
            }
        }
    }
}
