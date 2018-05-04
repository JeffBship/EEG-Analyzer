/*
 *   INSERT LICENSE INFO HERE.  MIT OR GNU?
 */
package eeg_analyzer;

import static eeg_analyzer.FFT.fftRealList;
import static eeg_analyzer.FFT.getBins;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author Jeff Blankenship
 */
public class Main {
    
    //main class for testing purposes
    public static void main(String[] args) throws InterruptedException{
        
        /*
        ArrayList<Double> randomNoise = new ArrayList<>();
        int size = 500;
        for (int i=0; i<size;i++){
            randomNoise.add(  (Math.random() * 500)-250 );
        }
        display(fft(randomNoise));
        */
        
        //Generate test signal
        ArrayList<Double> testSignal = new ArrayList<>();
        double tonalFreq = 10;      //frequency in cycles/sec
        double sampleRate = 400;    // samples/sec
        double duration = 1;        // duration of signal in seconds
        double maxAmplitude = 100;
        double radiansStep =        // change in radians per sample
               2 * Math.PI * tonalFreq / sampleRate;
        int sampleCount = (int) (sampleRate * duration);    //number of samples to generate
        while (sampleCount>0){
            testSignal.add( maxAmplitude * Math.sin(sampleCount*radiansStep)   );
            sampleCount--;
        }
        
        /*
        System.out.println(" ##############   testSignal #############");
        displayList(testSignal);
        System.out.println(" #########################################");
        
        long startTime = System.currentTimeMillis();
        ArrayList<Double> resultReal = fftRealList(testSignal);
        
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println(" %%%%%%%%%%%%%%   dftResult %%%%%%%%%%%%%%%%%");
        displayList(resultReal);
        System.out.println(" %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        System.out.println("elapsedTime: " + elapsedTime + " msec");
        
        */
        
        String filename = "00004087_s003_t004.txt";
        Record record = new Record(filename);
        sampleRate = record.getSampleRate();
        
        Channel channel = record.getChannel(0);
        
        Scanner scan = new Scanner(System.in);
        
        Boolean finished = false;
        int window = 1;  //This is the time in seconds for each graph
        int samples = channel.count();
        int windowSamples = (int)sampleRate * window;
        
        /*
        for (int c=0;c<samples;c++){
            System.out.println(c +":"+channel.getElement(c));
        }
        */
        
        //for testing, starts at this window.  First window is zero.
        int startWindow = 0;
        
        int start = startWindow * windowSamples;
        int finish = start + windowSamples;
        ArrayList<Double> resultReal = null;
        ArrayList<Double[]> bins = null;
          
        //display graphs in window size slices.  End is truncated to lower
        //whole window size.
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.HALF_UP);
        int spikeCount = 0;
        boolean alarm,spikeAmp,spikeDuration,theta,delta;   
        
        //LineChartSample.main(new String[0]);
        
        while (!finished) {
            alarm = false;  
            spikeAmp = false;
            spikeDuration = false;
            theta = false;
            delta = false;
            //new dataset for this window
            XYSeriesCollection datasetAmp = new XYSeriesCollection();
            XYSeriesCollection datasetFreq = new XYSeriesCollection();
            
            //pick out one window
            ArrayList<Double> oneWindow = new ArrayList<>();
            for (int count=0; count<windowSamples; count++){
                oneWindow.add( channel.getElement(start+count) );
            }
            
            long startTime = System.currentTimeMillis();
            //try {
                resultReal = fftRealList(oneWindow);
                bins= getBins(resultReal,sampleRate);
            //} catch (Exception e) {
            //    System.out.println(e);
            //}
            //this takes the entire channel at once.  Activate if needed.
            //ArrayList<Double> resultReal = fftRealList(channel.getData()); 
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;
            //System.out.println("elapsedTime: " + elapsedTime + " msec");
            channel.setBins(bins);
            
            
            //add data from each channel
            for (int c=0;c<record.getSize();c++){
                oneWindow = new ArrayList<>();
                Channel chan = record.getChannel(c);
                XYSeries ampSeries = new XYSeries(chan.getName());
                for (int sample=start;sample<=finish;sample++){
                    double time = sample / chan.getSampleRate();
                    double amplitude = chan.getElement(sample);
                    oneWindow.add(amplitude);
                    ampSeries.add(time,amplitude);
                    //System.out.println("Added " + time + ":" + amplitude);
                    
                }
                resultReal = fftRealList(oneWindow);
                bins= getBins(resultReal,sampleRate);  
                
                XYSeries freqSeries = new XYSeries(chan.getName());
                double freq = 0.0;
                double percent = 0.0;
                for (int bin=0;bin<bins.size();bin++){
                    freq = bins.get(bin)[0];
                    percent = bins.get(bin)[1]/1000.0;
                    if ( freq>Globals.MIN_FREQ && freq<Globals.MAX_FREQ){
                        freqSeries.add(freq,percent);
                    }
                    //System.out.println("Added " + time + ":" + amplitude);
                }
                
                //activate to show all channels
                datasetAmp.addSeries(ampSeries);   
                datasetFreq.addSeries(freqSeries);   
                
                //activate to show one channel by name
                //if (chan.getName().contains("EEG FP2-REF")) datasetAmp.addSeries(ampSeries);  //
                //if (chan.getName().contains("EEG FP2-REF")) datasetFreq.addSeries(freqSeries);
                
                //check for spike while iterating
                if (chan.hasSpikeAmp(start,finish)) {
                    spikeCount++;
                    spikeAmp = true;
                    //System.out.println("spike: channel:"+chan.getName()
                    //    +"  window:"+ start/chan.getSampleRate());
                }
                
                //check for theta while iterating
                if (chan.hasTheta(bins)) {
                    theta = true;
                    //System.out.println("theta: channel:"+chan.getName()
                    //    +"  window:"+ start/chan.getSampleRate());
                }
                
                //check for delta while iterating
                if (chan.hasDelta(bins)) {
                    delta = true;
                    //System.out.println("delta: channel:"+chan.getName()
                    //    +"  window:"+ start/chan.getSampleRate());
                }
                
                //check for alarm conditions
                if (spikeAmp && (theta||delta)) {
                    alarm=true;
                    System.out.println("ALARM: channel:" + chan.getName() 
                        + " window:" + start/chan.getSampleRate()
                        + " spikeAmp:" + spikeAmp
                        + " theta:" + theta
                        + " delta:" + delta);
                }
            }
              
            /*
            System.out.println();
            System.out.println("Start: " + start + " Finish: " + finish);
            System.out.println("AveRms:" + df.format(channel.getAveRms(start, finish)));
            System.out.println("Min:" + df.format(channel.getMin(start, finish)));
            System.out.println("ShiftedAverage:" + df.format(channel.getShiftedAve(start, finish)));
            System.out.println("Max::" + df.format(channel.getMax(start, finish)));
            System.out.println("hasSpike: " + channel.hasSpikeAmp(start,finish) );
            System.out.println("aveAmplitude: " + df.format(channel.aveAmplitude(start,finish)));
            */
            
                   
            //if (channel.hasSpikeAmp(start,finish)) spikeCount++;
            
            
            
             
            /*
            if (channel.hasSpikeAmp(start,finish)){
                String title = "Amplitude spike detected"; 
                String xLabel = "Time (seconds)";
                String yLabel = "Amplitude (units?)";
                Chart chart = new Chart(channel,start,finish,title,xLabel,yLabel);
                chart.setVisible(true);
                Thread.sleep(1000);
            }
            */
            
            //show amplitude data
            String title = "Amplitude - time"; 
            String xLabel = "Time (seconds)";
            String yLabel = "Amplitude";
            Chart ampChart = new Chart(datasetAmp,start,finish,title,xLabel,yLabel,alarm,"left");
            ampChart.setVisible(true);
            
            //show frequency data
            title = "Spectrograph"; 
            xLabel = "frequency";
            yLabel = "contribution";
            Chart freqChart = new Chart(datasetFreq,start,finish,title,xLabel,yLabel,alarm,"right");
            freqChart.setVisible(true);
            Thread.sleep(1000);
            //chart.setVisible(false);
            
            
            
            
            start = start + windowSamples;
            finish = finish + windowSamples;
            
            //continue until window is past the samples available
            if (finish>samples) finished = true;
        }  //while (!finished)
        System.out.println("Total windows with event: " + spikeCount);
        
        
        
        
    }
    
}
