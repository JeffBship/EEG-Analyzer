package eeg_analyzer;

import java.util.Iterator;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
 
 
public class Draw extends Application {
 
    @Override public void start(Stage stage) {
        stage.setTitle("EEG frequency analysis");
        //defining the axes
        final NumberAxis xAxis = new NumberAxis(0,200,10);
        xAxis.setLabel("Frequency");
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Relative Occurences");
        //creating the chart
        final LineChart<Number,Number> lineChart = 
                new LineChart<Number,Number>(xAxis,yAxis);
        lineChart.setTitle("EEG channel frequency spectrograph");
        //hide the Y axis...the values are non-units
        lineChart.getYAxis().setTickLabelsVisible(false);
        lineChart.getYAxis().setOpacity(0);
        //defining a series
        XYChart.Series series = new XYChart.Series();
        //series.setName("My portfolio");
        //populating the series with data
        Iterator iterator = FFT.graphDataIterator();
        Double[] bin;
        while (iterator.hasNext()){
            bin = (Double[]) iterator.next();
            series.getData().add(new XYChart.Data(bin[0], bin[1]));
        }
        Scene scene  = new Scene(lineChart,800,600);
        
        //Can't get the stylesheet to load, always null pointer whatever folder it's in
        scene.getStylesheets().add(getClass().getResource("chart.css").toExternalForm());
        
        lineChart.getData().add(series);
        stage.setScene(scene);
        //scene.getStylesheets().add(getClass().getResource
        //    ("C:\\Users\\Jeff\\Desktop\\EEG\\EEG_analyzer\\style\\chart.css").toExternalForm());
        stage.show();
    }
 
    public static void main(String[] args) {
        launch(args);
    }
}