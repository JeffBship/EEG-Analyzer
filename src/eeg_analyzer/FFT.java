/**
 *   INSERT LICENSE INFO HERE.  MIT OR GNU?
 * 
 * This project makes use of various sources.
 * 
 * XChart charting library components are open source and attributed 
 * to various contributors listed here:  https://github.com/knowm/XChart/tree/master
 * 
 * The FFT algorithm is modified from code provided at
 * https://introcs.cs.princeton.edu/java/97data/FFT.java.html
 * by Robert Sedgewick and Kevin Wayne, based upon the work of 
 * J.W. Cooley and John Tukey.
 * 
 */

package eeg_analyzer;




import static java.lang.Math.exp;
import static java.lang.Math.sqrt;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JFrame;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;



public class FFT {
    
    /*
    private static ArrayList<Double[]> graphData;
    private static String graphName;
    
    public static Iterator graphDataIterator(){
        return graphData.iterator();
    }
    */
    
    // compute the FFT of x[], assuming its length is a power of 2
    public static ArrayList<Double> fftRealList ( ArrayList<Double> signal ){
                
        //pad signal to be size of a power of 2.
        double sizeLog2 = Math.log(signal.size())/Math.log(2);
        int intLog2 = (int) sizeLog2;
        if (sizeLog2-intLog2==0){
            //do nothing, size is a power of 2
        } else {
            //pad up to the next power of 2
            int pads = (int) Math.pow(2,intLog2+1) - signal.size();
            while (pads>0){
                //adds zeros to signal, until sized up to next power of 2.
                signal.add(0.0);
                pads--;
            }
        }
        // signal.size() is now a power of 2
        int n = signal.size();
        
        
        //build a complex array from the real input
        Complex test = new Complex(0,0);
        Complex[] signalComplex = new Complex[n];
        for (int a=0; a<n; a++){
            signalComplex[a] = new Complex(signal.get(a),0);
        }
        
        Complex[] resultComplex = fftComplex(signalComplex);
        
        //get magnitude of each frequency back into a List
        //only take first half, it's a mirror image
        ArrayList<Double> resultReal = new ArrayList<>();
        for (int a=0; a<n/2; a++){
            resultReal.add( resultComplex[a].abs()  );
        }
        
        
        return resultReal;
    }
        
    // compute the FFT of x[], assuming its length is a power of 2
    // (this method is the code from Sedgewick and Wayne)
    public static Complex[] fftComplex(Complex[] x) {
        int n = x.length;

        // base case
        if (n == 1) return new Complex[] { x[0] };

        // radix 2 Cooley-Tukey FFT
        if (n % 2 != 0) {
            throw new IllegalArgumentException("n is not a power of 2");
        }

        // fft of even terms
        Complex[] even = new Complex[n/2];
        for (int k = 0; k < n/2; k++) {
            even[k] = x[2*k];
        }
        Complex[] q = fftComplex(even);

        // fft of odd terms
        Complex[] odd  = even;  // reuse the array
        for (int k = 0; k < n/2; k++) {
            odd[k] = x[2*k + 1];
        }
        Complex[] r = fftComplex(odd);

        // combine
        Complex[] y = new Complex[n];
        for (int k = 0; k < n/2; k++) {
            double kth = -2 * k * Math.PI / n;
            Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
            y[k]       = q[k].plus(wk.times(r[k]));
            y[k + n/2] = q[k].minus(wk.times(r[k]));
        }
        return y;
    }
   

    
    // compute the FFT of x[], of any length
    public static ArrayList<Double> fft(ArrayList<Double> signal) {
        
        //pad signal to be size of a power of 2.
        double sizeLog2 = Math.log(signal.size())/Math.log(2);
        int intLog2 = (int) sizeLog2;
        if (sizeLog2-intLog2==0){
            //do nothing, size is a power of 2
        } else {
            //pad up to the next power of 2
            int pads = (int) Math.pow(2,intLog2+1) - signal.size();
            while (pads>0){
                //adds zeros to signal, until sized up to next power of 2.
                signal.add(0.0);
                pads--;
            }
        }
        // signal.size() is now a power of 2
        
        
        //create empty result list of full size, allowing direct
        //access via index to upper half.
        ArrayList<Double> result = new ArrayList<>(signal.size());
        //populate result, so set method works later 
        for (int k=0; k<signal.size(); k++){
            result.add(0.0);
            }
        //System.out.println("Signal size: "+signal.size());
        //System.out.println("Result size: "+result.size());
        
        // base case.  returns a single element list, with a zero.
        if (signal.size() == 1) {
            result.add(0.0);
            return result;
        } 
        
        //  Cooley-Tukey FFT...
        
        int halfSize = signal.size()/2;  //saves a lot of caclulation time
        
        // fft of even terms
        ArrayList<Double> even = new ArrayList<>();
        for (int i = 0; i < halfSize; i++) {
            even.add( signal.get(2*i) );
        }
        even = fft(even);

        // fft of odd terms
        ArrayList<Double> odd = new ArrayList<>();
        for (int i = 0; i < halfSize; i++) {
            odd.add( signal.get(2*i+1) );
        }
        odd = fft(odd);
        
        
        // combine odds and evens per the summation for fft
        for (int n=0; n < halfSize-1; n++){
            //twiddle factor.  Consider factoring out part to reduce computation time.
            double twiddle = exp( -2 * Math.PI * n / signal.size());
            //System.out.println("n: "+n+" size:"+signal.size()+ " halfSize:" + halfSize);
            //System.out.println("signal element n: " + signal.get(n));
            //System.out.println("Result size: "+result.size());
            
            result.set(n, signal.get(n) + twiddle );
            result.set(n+halfSize, signal.get(n+halfSize) - twiddle);
        }
        
        return result;
    }
    
    // compute the DFT of x[], of any length
    public static ArrayList<Double> dft(ArrayList<Double> signal) {
        
        ArrayList<Double> result = new ArrayList<>();
        int N = signal.size();  //using capitolization scheme of Fourier equation
        double Xk;
        for (int k=0; k<N; k++){
            Xk = 0;
            for (int n=0; n<N; n++){
                Xk += signal.get(n) * Math.cos( 2 * Math.PI*k*n/N  );
            }
            result.add(Xk);
        }
        return result;
    }
    
    /**
     * Print the entire fft to the screen.  This is not very useful for
     * analysis, but is provided for testing and debugging during development.
     * Since it prints an entire ArrayList, it  used in many steps.
     * @param signal 
     */
    static void displayList(ArrayList<Double> signal){
        for (int i=0; i<signal.size(); i++){
            System.out.println(i + "\t" + signal.get(i));
        }
    }
    
    static void displayBins(ArrayList<Double[]> bins){
        System.out.println("There are " + bins.size() + " bins as follows");
        System.out.println("Freq:Amplitude");
        Double[] bin = new Double[2];
        for (int b=0; b<bins.size(); b++){
            bin = bins.get(b);
            System.out.println(bin[0] + " : " + bin[1]);
        }
        
    }
    
    /**
     * Extract frequencies from an fft result.  
     * Returns ArrayList<Double[]> where each element of the list 
     * is a freq:Amplitude pair
     * @param fft entire fft, will be divided internally
     * @param sampleRate in samples/sec
     * @return 
     */
    public static ArrayList<Double[]> getBins(ArrayList<Double> fft, double sampleRate){
        ArrayList<Double[]> result = new ArrayList<>();
        Double nyquist = sampleRate /2;
        int bins = fft.size()/2; //all information is in the first half
        Double[] bin = new Double[2];
        Double binFreq, binAmplitude;
        //bin[0] is the center frequency of the bin
        //bin[1] is the amplitude of that frequency 
        for(int b=0; b<bins; b++){
            binFreq = b * nyquist / bins;
            
            //System.out.println("Adding bin freq " + bin[0]);
            //Note that the maximum this reaches is half the sampleRate
            //so the frequency is capped at the Nyquist cutoff with no further
            //converstions required.
            binAmplitude =   sqrt( fft.get(b)*fft.get(b) + fft.get(b+bins)*fft.get(b+bins) );
            //remove dc line
            if (binFreq<1.0) binAmplitude=0.0;
            bin = new Double[]{binFreq,binAmplitude};
            result.add(bin);
            //System.out.println("Just added " + result.get(b)[0] +":"+result.get(b)[1]);
        }
        
        return result;
    }
    
    
    
    
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
        boolean windowSpike = false;        
        //LineChartSample.main(new String[0]);
        
        while (!finished) {
            windowSpike = false;        
            //new dataset for this window
            XYSeriesCollection dataset = new XYSeriesCollection();
            
            
            //add data from each channel
            for (int c=0;c<record.getSize();c++){
                Channel chan = record.getChannel(c);
                XYSeries series = new XYSeries(chan.getName());
                for (int sample=start;sample<=finish;sample++){
                    double time = sample / chan.getSampleRate();
                    double amplitude = chan.getElement(sample);
                    series.add(time,amplitude);
                    //System.out.println("Added " + time + ":" + amplitude);
                }
                
                //activate to show all channels
                //dataset.addSeries(series);   
                
                //activate to show one channel by name
                if (chan.getName().contains("EEG FP2-REF")) dataset.addSeries(series);  //
                
                
                //check for spike while iterating
                if (chan.hasSpikeAmp(start,finish)) {
                    windowSpike = true;
                    spikeCount++;
                    System.out.println("SPIKE FOUND: channel:"+chan.getName()
                        +"  window:"+ start/chan.getSampleRate());
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
            
            String title = "Amplitude spike detected"; 
            String xLabel = "Time (seconds)";
            String yLabel = "Amplitude (units?)";
            String side = "left";
            Chart chart = new Chart(dataset,start,finish,title,xLabel,yLabel,windowSpike,side);
            chart.setVisible(true);
            Thread.sleep(1000);
            //chart.setVisible(false);
            
            start = start + windowSamples;
            finish = finish + windowSamples;
            
            //continue until window is past the samples available
            if (finish>samples) finished = true;
        }  //while (!finished)
        System.out.println("Total windows with spike: " + spikeCount);
        
        
        
        
    }
}

