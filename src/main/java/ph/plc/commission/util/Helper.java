package ph.plc.commission.util;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.Scene;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Helper {
    public static void disableResize(ReadOnlyObjectProperty<Scene> sceneReadOnlyObjectProperty) {
        sceneReadOnlyObjectProperty.addListener((observableScene, oldScene, newScene) -> {
            if (oldScene == null && newScene != null) {
                newScene.windowProperty().addListener((observableWindow, oldWindow, newWindow) -> {
                    if (oldWindow == null && newWindow != null) {
                        ((Stage) newWindow).setResizable(false);
                    }
                });
            }
        });
    }

    public static List<Integer> generateYear() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int startInclusive = year - 80;
        return IntStream.range(startInclusive, year)
                .map(i -> year - i + startInclusive).boxed().collect(Collectors.toList());
    }

    public static Date toUtilDate(LocalDate date) {
        return java.sql.Date.valueOf(date);
    }

    public static LocalDate toLocalDate(Date date) {
        return new java.sql.Date(new Date().getTime()).toLocalDate();
    }

    public static TextFormatter<Double> generateDoubleTextFormatter() {
        Pattern validEditingState = Pattern.compile("-?(([1-9][0-9]*)|0)?(\\.[0-9]*)?");
        UnaryOperator<TextFormatter.Change> filter = c -> {
            String text = c.getControlNewText();
            if (validEditingState.matcher(text).matches()) {
                return c;
            } else {
                return null;
            }
        };
        StringConverter<Double> converter = new StringConverter<Double>() {
            @Override
            public Double fromString(String s) {
                if (s.isEmpty() || "-".equals(s) || ".".equals(s) || "-.".equals(s)) {
                    return 0.0;
                } else {
                    return Double.valueOf(s);
                }
            }

            @Override
            public String toString(Double d) {
                return d.toString();
            }
        };
        return new TextFormatter<>(converter, 0.0, filter);
    }

    public static TextFormatter<Integer> generateIntegerTextFormatter() {
        Pattern validEditingState = Pattern.compile("-?(([1-9][0-9]*)|0)?");
        UnaryOperator<TextFormatter.Change> filter = c -> {
            String text = c.getControlNewText();
            if (validEditingState.matcher(text).matches()) {
                return c;
            } else {
                return null;
            }
        };
        StringConverter<Integer> converter = new StringConverter<Integer>() {
            @Override
            public Integer fromString(String s) {
                if (s.isEmpty() || "-".equals(s) || ".".equals(s) || "-.".equals(s)) {
                    return 0;
                } else {
                    return Integer.valueOf(s);
                }
            }

            @Override
            public String toString(Integer d) {
                return d.toString();
            }
        };
        return new TextFormatter<>(converter, 0, filter);
    }
}
