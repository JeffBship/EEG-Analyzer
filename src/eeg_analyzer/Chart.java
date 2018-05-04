package eeg_analyzer;

/**
 * 
 * 
 * 
 * This code based on example 
 * "Using JFreechart to draw XY line chart with XYDataset"
 * http://www.codejava.net/java-se/graphics/using-jfreechart-to-draw-xy-line-chart-with-xydataset
 * @author No author name provided
 *
 */

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Chart extends JFrame {
    
    
    //this constructor shows a dataset from start to finish
    public Chart(XYDataset dataset, int start, int finish,String chartTitle, String xAxisLabel,
           String yAxisLabel, boolean alarm, String side) {
        super("EEG Analysis");
        JPanel chartPanel = createChartPanel(dataset,start,finish,chartTitle,xAxisLabel,
                                            yAxisLabel,alarm);
        
        add(chartPanel, BorderLayout.CENTER);
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();
        int xSize = (int) (width*Globals.CHART_SCALE/100);
        int ySize = (int) (height*Globals.CHART_SCALE/100);
        setSize(xSize, ySize);
        if (side.equals("left")){
            setLocation( (int) width/2-xSize, (int) height/2-ySize/2);
        }else{
            setLocation( (int) width/2, (int) height/2-ySize/2);
        }
        //setLocation(0,0);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //setLocationRelativeTo(null);
    }
    
    //this constructor shows a single channel from start to finish
    public Chart(Channel channel, int start, int finish,
           String chartTitle, String xAxisLabel,String yAxisLabel) {
        super("EEG Analysis");
        JPanel chartPanel = createChartPanel(channel,start,finish);
        add(chartPanel, BorderLayout.CENTER);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();
        int xSize = (int) (width*Globals.CHART_SCALE/100);
        int ySize = (int) (height*Globals.CHART_SCALE/100);
        setSize(xSize, ySize);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
 
    
    private JPanel createChartPanel(XYDataset dataset, int start, int finish,
        String chartTitle, String xAxisLabel, String yAxisLabel, boolean alarm) {
        
        JFreeChart chart = ChartFactory.createXYLineChart(chartTitle,
                xAxisLabel, yAxisLabel, dataset);
        //Font titleFont = new Font("SansSerif", Font.PLAIN, 10);
        //chart.getTitle().setFont(titleFont);
        XYPlot plot = chart.getXYPlot();
        if (alarm){
            plot.setOutlinePaint(Color.RED);
            plot.setOutlineStroke(new BasicStroke(4.0f));
        } else {
            plot.setOutlinePaint(Color.GRAY);
            
        }
        
        
        return new ChartPanel(chart);
    }
    
    private JPanel createChartPanel(Channel channel, int start, int finish) {
        String chartTitle = "Amplitude in time domain";
        String xAxisLabel = "time";
        String yAxisLabel = "Amplitude";

        XYDataset dataset = createDataset(channel, start, finish);

        JFreeChart chart = ChartFactory.createXYLineChart(chartTitle,
                xAxisLabel, yAxisLabel, dataset);
        //Font titleFont = new Font("SansSerif", Font.PLAIN, 10);
        //chart.getTitle().setFont(titleFont);

        return new ChartPanel(chart);
    }
 
    private XYDataset createDataset(Channel channel, int start, int finish) {
    XYSeriesCollection dataset = new XYSeriesCollection();
    XYSeries series = new XYSeries(channel.getName());
 
    for (int sample=start;sample<=finish;sample++){
        double time = sample / channel.getSampleRate();
        double amplitude = channel.getElement(sample);
        series.add(time,amplitude);
        //System.out.println("Added " + time + ":" + amplitude);
    }
    dataset.addSeries(series);
    return dataset;
}
 
    //main provided for stand alone testing during development
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //new XYLineChartExample().setVisible(true);
            }
        });
    }
}