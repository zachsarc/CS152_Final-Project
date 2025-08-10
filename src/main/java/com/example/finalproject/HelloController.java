package com.example.finalproject;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Comparator;
import java.util.Locale;

public class HelloController {
    @FXML
    private Label runnersNameLabel, runnersDistanceLabel, timeLabel;
    @FXML
    private TextField nameField, distanceField, timeField;
    @FXML
    private Button addRunToList, listToCsvButton;
    @FXML
    private RadioButton nameRadio, distanceRadio, timeRadio, paceRadio;
    @FXML
    private CheckBox descendingBox;
    @FXML
    private TableView<Run> tableView;
    @FXML
    private TableColumn<Run, String> nameCol;
    @FXML
    private TableColumn<Run, Number> distCol;
    @FXML
    private TableColumn<Run, String> timeCol, paceCol;
    @FXML
    private ToggleGroup toggleGroup;

    RunStore store = new RunStore();

    @FXML
    public void initialize() {
        nameRadio.setSelected(true);
        store.setComparator(Run.BY_NAME, true);
        nameCol.setCellValueFactory(new PropertyValueFactory<Run, String>("name"));

        DecimalFormat decimalFormat = new DecimalFormat("0.0#", DecimalFormatSymbols.getInstance(Locale.US));
        distCol.setCellFactory(col -> new TableCell<Run, Number>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                } else {
                    setText(decimalFormat.format(value.doubleValue()));
                    setStyle("-fx-alignment: CENTER-RIGHT;");
                }
            }
                });
        distCol.setCellValueFactory(new PropertyValueFactory<Run, Number>("distanceInMiles"));

        timeCol.setCellValueFactory(new PropertyValueFactory<Run, String>("formattedTime"));
        paceCol.setCellValueFactory(new PropertyValueFactory<Run, String>("formattedPace"));
        tableView.setItems(store.getSortedList());
    }

    public void onAdd() {
        Alert infoAlert = new Alert(Alert.AlertType.ERROR);

        String nameStr = nameField.getText().trim();
        String distanceStr = distanceField.getText().trim();
        String timeStr = timeField.getText().trim();

        if (nameStr.isEmpty()) {
            infoAlert.showAndWait();
            return;
        }

        double parsedDist;
        try {
            parsedDist = Double.parseDouble(distanceStr);
        } catch (NumberFormatException e) {
            infoAlert.showAndWait();
            return;
        }
        if (parsedDist <= 0) {
            infoAlert.showAndWait();
            return;
        }

        String[] sections = timeStr.split(":", -1);

        for (String section : sections) {
            if (section == null || section.trim().isEmpty()) {
                infoAlert.showAndWait();
                return;
            }
        }

        int len = sections.length;
        String hStr = "0", mStr = "0", sStr = "0";
        if (len == 3) {
            hStr = sections[0].trim();
            mStr = sections[1].trim();
            sStr = sections[2].trim();
        } else if (len == 2) {
            mStr = sections[0].trim();
            sStr = sections[1].trim();
        } else {
            mStr = sections[0].trim();
        }
        if (hStr.isEmpty() || mStr.isEmpty() || sStr.isEmpty()) {
            infoAlert.showAndWait();
            return;
        }

        double totalSeconds = 0;
        double minutes = 0;
        int hInt;
        int mInt;
        int sInt;
        try {
            hInt = Integer.parseInt(hStr);
            mInt = Integer.parseInt(mStr);
            sInt = Integer.parseInt(sStr);
        } catch (NumberFormatException e) {
            infoAlert.showAndWait();
            return;
        }
        if (!(hInt >= 0 && mInt >= 0 && mInt < 60 && sInt >= 0 && sInt < 60)) {
            infoAlert.showAndWait();
            return;
        }
        totalSeconds = hInt * 3600 + mInt * 60 + sInt;
        minutes = totalSeconds / 60.0;

        if (minutes <= 0) {
            infoAlert.showAndWait();
            return;
        }

        Run run = new Run(nameStr, parsedDist, minutes);
        store.add(run);
        nameField.clear();
        distanceField.clear();
        timeField.clear();
        nameField.requestFocus();
    }
    public void parseTimeToHMS(double mins) {

    }
    public void onSort() {
        Comparator<Run> rule;
        boolean ascending = !descendingBox.isSelected();
        if (distanceRadio.isSelected()) {
            rule = Run.BY_DISTANCE_ASC;
        } else if (timeRadio.isSelected()) {
            rule = Run.BY_TIME_ASC;
        } else if (paceRadio.isSelected()) {
            rule = Run.BY_PACE_ASC;
        } else {
            rule = Run.BY_NAME;
        }
        store.setComparator(rule, ascending);
    }
    public void onSave() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save runs as CSV");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Save as CSV", "*.csv");
        fileChooser.getExtensionFilters().setAll(filter);
        fileChooser.setInitialFileName("Runners.csv");
        File file = fileChooser.showSaveDialog(tableView.getScene().getWindow());
        if (file == null) return;
        Path p = file.toPath();
        store.exportCsv(p);
    }
}