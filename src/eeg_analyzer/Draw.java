package eeg_analyzer;

import java.util.Iterator;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
 
 
public abstract class Draw extends Application {
    
    //private static XYChart.Series series = new XYChart.Series();
    //public static LineChart<Number,Number> lineChart; 
               // = new LineChart<Number,Number>(xAxis,yAxis);
    
    private static Channel channel;
    private static String label="";
    //private static Stage stage = new Stage();
    
    public static void setChannel(Channel channel){
        Draw.channel = channel;
    }
     
    public static void setLabel(String label){
        Draw.label = label;
    }
 
    //public static void test(String string){
    //    lineChart.setTitle(string);
    //}
   
    
    //create stage

    /**
     *
     * @param stage
     */
    public void start(Stage stage) {
        
        //Stage stage = new Stage();
        
        System.out.println("DDDDDDDDDDDDDDDDDDDDDDDDDDDDD");
        
        stage.setTitle("EEG frequency analysis");
        //defining the axes
        final NumberAxis xAxis = new NumberAxis(0,200,10);
        xAxis.setLabel("Frequency");
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Relative Occurences");
        //creating the chart
        LineChart<Number,Number> lineChart = new LineChart<Number,Number>(xAxis,yAxis);
        lineChart = new LineChart<Number,Number>(xAxis,yAxis);
        lineChart.setTitle(channel.getFilename() + " : " + label);
        //hide the Y axis...the values are non-units
        lineChart.getYAxis().setTickLabelsVisible(false);
        lineChart.getYAxis().setOpacity(0);
        //defining a series
        XYChart.Series series = new XYChart.Series();
        series.setName("channel: " + channel.getName());
        //populating the series with data
        Iterator iterator = channel.binIterator();
        Double[] bin;
        while (iterator.hasNext()){
            bin = (Double[]) iterator.next();
            series.getData().add(new XYChart.Data(bin[0], bin[1]));
        }
        Scene scene  = new Scene(lineChart,800,600);
       
        scene.getStylesheets().add(getClass().getResource("chart.css").toExternalForm());
        
        lineChart.getData().add(series);
        stage.setScene(scene);
        //scene.getStylesheets().add(getClass().getResource
        //    ("C:\\Users\\Jeff\\Desktop\\EEG\\EEG_analyzer\\style\\chart.css").toExternalForm());
        stage.show();
        System.out.println("last line of Draw start");
    }
 
    public static void main(String[] args) {
        launch(args);
    }
}