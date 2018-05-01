/*
 *   INSERT LICENSE INFO HERE.  MIT OR GNU?
 */
package eeg_analyzer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import static java.lang.Double.parseDouble;
import static java.lang.Math.abs;
import static java.lang.Math.sqrt;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Data structure for one channel of EEG data.
 * 
 * @author Jeff Blankenship
 * @author Taylor Somma
 */
public class Channel {
    
    private String name;
    private String filename;
    private ArrayList<Double> data;  //raw amplitude data, time domain
    private ArrayList<Double[]> bins;   //frequency domain [frequency,percent]
    private Double max, min;
    private Integer count;
    private Double sampleRate, digMax, digMin, physMin, physMax;

    
    public Channel(String name, ArrayList<Double> data){
        this.name = name;
        this.data = data;
        this.max = this.getMax();
        this.min = this.getMin();
    }
    
    public Channel(String name){
        this.name = name;
        this.data = new ArrayList<>();
        this.max = null;
        this.min = null;
    }
    
    /**
     * Add passed integer parameter to data arrayList.
     * @param value 
     */
    public void addElement(int value){
        this.data.add( (double) value);
    }
    
    /**
     * Retrieve value at passed index position in data arrayList.
     * Returns null if index is larger than size of data arrayList.
     * @param index
     * @return Integer or Null
     */
    public Double getElement(int index){
        Double result = null;
        if (this.data.size()>index){
            result = this.data.get(index);
        }
        return result;
    }
    
    public String getName(){
        return this.name;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public String getFilename(){
        return this.filename;
    }
    
    public void setFilename(String filename){
        this.filename = filename;
    }
    
    public ArrayList<Double> getData(){
        return this.data;
    }
    
    public void setData(ArrayList<Double> data){
        this.data = data;
    }
    
    public ArrayList<Double[]> getBins(){
        return this.bins;
    }
    
    public void setBins(ArrayList<Double[]> bins){
        this.bins = bins;
    }
    
    
    public Double getSampleRate(){
        return this.sampleRate;
    }
    
    public void setSampleRate(Double sampleRate){
        this.sampleRate = sampleRate;
    }
    
    public Double getDigMax(){
        return this.digMax;
    }
    
    public void setDigMax(Double digMax){
        this.digMax = digMax;
    }
    
    public Double getDigMin(){
        return this.digMin;
    }
    
    public void setDigMin(Double digMin){
        this.digMin = digMin;
    }
    
    public Double getPhysMax(){
        return this.physMax;
    }
    
    public void setPhysMax(Double physMax){
        this.physMax = physMax;
    }
    
    public Double getPhysMin(){
        return this.physMin;
    }
    
    public void setPhysMin(Double physMin){
        this.physMin = physMin;
    }
        
    
    /**
     * Recalculates and returns the maximum value of the channel.
     * @return Integer
     */
    public Double getMax(){
        if (this.data==null){
            return null;
        }else{
            double result = this.data.get(0);
            Iterator iterator = this.data.iterator();
            while (iterator.hasNext()){
                Double next = (double) iterator.next();
                if (next>result){
                    result = next;
                }
            }
            return result;
        }
    }
    
    /**
     * Recalculates and returns the minimum value of the channel.
     * @return Integer
     */
    public Double getMin(){
        if (this.data==null){
            return null;
        }else{
            double result = this.data.get(0);
            Iterator iterator = this.data.iterator();
            while (iterator.hasNext()){
                Double next = (double) iterator.next();
                if (next<result){
                    result = next;
                }
            }
            return result;
        }
    }
    
    /**
     * Returns the minimum value of samples in the 
     * channel from start to finish.
     * Minimum is the most negative.
     * @param start
     * @param finish
     * @return Integer
     */
    public Double getMin(int start, int finish){
        Double result=0.0;
        if (start<0) start=0;
        if (finish>this.count()) finish = this.count();
        if (start>finish) return null;
        
        if (this.data==null){
            return null;
        }else{
            result = this.data.get(start);
            for(int i = start;i<=finish;i++){
                if (this.data.get(i)<result) result=this.data.get(i);
            }
            return result;
        }
        
    }
    
    /**
     * Returns the maximum value of samples in the 
     * channel from start to finish.
     * Minimum is the most negative.
     * @param start
     * @param finish
     * @return Integer
     */
    public Double getMax(int start, int finish){
        Double result=0.0;
        if (start<0) start=0;
        if (finish>this.count()) finish = this.count();
        if (start>finish) return null;
        
        if (this.data==null){
            return null;
        }else{
            result = this.data.get(start);
            for(int i = start;i<=finish;i++){
                if (this.data.get(i)>result) result=this.data.get(i);
            }
            return result;
        }
        
    }
    
    /**
     * Returns the Average using RMS method.  The average returned
     * is relative to zero.
     * @param start
     * @param finish
     * @return 
     */
    public Double getAveRms(int start, int finish){
        Double result=0.0;
        if (start<0) start=0;
        if (finish>this.count()) finish = this.count();
        if (start>finish) return null;
        
        Double squares = 0.0;
        for (int i=start; i<=finish;i++){
            squares += this.getElement(i) * this.getElement(i);
        }
        result = sqrt(squares) / (finish-start);
        return result;
    }
    
    /**
     * Returns the average relative to the pure average.
     * For signals where the average is not zero, this provides the
     * average difference between the signal value and the average
     * value.  This method does not use RMS.
     * @param start
     * @param finish
     * @return 
     */
    public Double getShiftedAve(int start, int finish){
        Double result=0.0;
        if (start<0) start=0;
        if (finish>this.count()) finish = this.count();
        if (start>finish) return null;
        
        //get the average
        Double sum = 0.0;
        for (int i=start; i<=finish;i++){
            sum += this.getElement(i);
        }
        Double average = sum/(finish-start);
        //get the sum of the differences from sample to average
        Double difference = 0.0;
        for (int i=start; i<=finish;i++){
            difference = abs(this.getElement(i) - average);
        }
        //average difference
        result = difference / (finish-start);
        
        return result;
    }
    
    
    
    
    /**
     * Checks for any amplitude more than Globals.SPIKE_DIF percentage
     * from the average.  Looks within the start-finish section of the
     * channel.
     * The time duration must be less than Globals.SPIKE_MAX_DURATION. (msec)
     * and more than Globals.SPIKE_MIN_DURATION (msec)
     * @param start
     * @param finish
     * @return boolean
     */
    public boolean hasSpikeAmp(int start, int finish){
        double aveAmplitude = this.aveAmplitude(start,finish);
        boolean result = false;
        
        double top=0.0;
        double bottom=0.0;
        int firstTop = -1;
        int topi = -1;
        int lastBottom = -1;
        
        for (int i=start; i<=finish; i++){
            //check for min/max
            if ( this.isLocalMax(i) ) {
                top = this.getElement(i);
                topi = i;
            }
            if (isLocalMin(i)){
                bottom = this.getElement(i);
            }
            if (top-bottom > aveAmplitude * Globals.SPIKE_DIF){
                result = true;
                //System.out.print("Spike at " + topi/this.sampleRate);
                if (firstTop==-1) firstTop = topi;
                lastBottom = i;
            }  
        }
        //calculate spike duration
        if (result){
            double duration = (lastBottom-firstTop)/this.sampleRate*1000;
            if (duration<Globals.SPIKE_MIN_DURATION || duration>Globals.SPIKE_MAX_DURATION){
                //duration rule fail
                result=false;
            }else{
                System.out.println("Spike at: " + firstTop/this.sampleRate + " duration:"+duration);
            }
        }
        return result;
    }
    
    /**
     * Checks for any amplitude more than Globals.SPIKE_DIF percentage
     * from the average.  Without parameters, checks entire channel.
     * @return boolean
     */
    public boolean hasSpikeAmp(){
        return hasSpikeAmp(0,this.count());
    }
    
    /**
     * Tests to see if element at passed index is a local maximum.
     * @param sample index of element to be tested
     * @return boolean
     */
    private boolean isLocalMax(int sample){
        //some basic input validation
        if (this.count<=1) return false;  //need at least 2 elements to compare
        if (sample<0) sample=0;
        if (sample>this.count()) sample = this.count();
        
        //left boundary
        if ( sample==0 && this.getElement(sample) > this.getElement(sample+1) ) {
            return true;
        }
        //right boundary    
        if (sample==this.count() && this.getElement(sample)>this.getElement(sample-1) ){
            return true;
        }
        //mid points    
        if ( sample>0 && sample<this.count()
            && (this.getElement(sample)>this.getElement(sample-1))
            && (this.getElement(sample)>this.getElement(sample+1))){
            return true;
        }       
        return false;    
    }
      
    
    /**
     * Tests to see if element at passed index is a local minimum.
     * @param sample index of element to be tested
     * @return boolean
     */
    private boolean isLocalMin(int sample){
        boolean result = false;
        //some basic input validation
        if (this.count<=1) return false;  //need at least 2 elements to compare
        if (sample<0) sample=0;
        if (sample>this.count()) sample = this.count();
        //left boundary
        if ( sample==0 && this.getElement(sample) < this.getElement(sample+1) ) {
            result = true;
        }
        //right boundary
        if (sample==this.count() && this.getElement(sample)<this.getElement(sample-1) ){
            result = true;
        } 
        //mid points
        if ( sample>0 && sample<this.count()
            && this.getElement(sample)<this.getElement(sample-1)
            && this.getElement(sample)<this.getElement(sample+1) ){
            result = true;
        }
        return result;
    }
    
    
    
    
    
    public Double aveAmplitude(int start, int finish){
        Double result = null;
        if (start<0) start=0;
        if (finish>this.count()) finish = this.count();
        if (start>finish) return null;  
        
        double top=0.0;
        double bottom=0.0;
        double total=0.0;
        int cycles=0;
        for(int i=start; i<=finish; i++){
            if (isLocalMax(i)){
                top = this.getElement(i);
                cycles++;
            }else if (isLocalMin(i)){
                bottom = this.getElement(i);
                total += top-bottom;
            }
        //System.out.println("maxcount:" + maxCount + " minCount:" + minCount);
        result = total/cycles;
        
        }
        return result;
    }
    
    /**
     * Counts and reports the number of elements in the data ArrayList.
     * @return Integer
     */
    public Integer count(){
        this.count = this.data.size();
        return this.count;
    }
    
    public Iterator dataIterator(){
        Iterator iterator = this.data.iterator();
        return iterator;
    }
    
    public Iterator binIterator(){
        Iterator iterator = this.bins.iterator();
        return iterator;
    }
    
   
    
    @Override
    public String toString(){
        String result = this.name + ": ";
        Iterator iterator = this.data.iterator();
        while ( iterator.hasNext() ) {
            result += iterator.next() + ", ";
        }
        result += "Max: " + this.getMax();
        result += " Min: " + this.getMin();
        result += " Count: " + this.count();
        result += " Name: " + this.name;
        return result;
    }
    
    /**
     * Constructor that builds channel data from a file.  This constructor
     * parses only the first channel to appear in the file, without metadata.
     * @param filename
     * @return Channel
     */
    public static Channel fromFile (String filename){
        Channel newChannel = null;
        
        char ch = '\u0000'; //null
        int value;
        FileReader fileReader = null;
        
        try {
            fileReader = new FileReader(filename);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            
            //read the file until the first ' is found.
            while ( ( (value=bufferedReader.read())!= -1)
                && (value != (int) '\'' )){
                //System.out.print( (char) value );
                //go to the next character in the file
            }
            
            //read the file until the second ' is found.
            //This is the channel name.
            String name = "";
            while ( ( (value=bufferedReader.read())!= -1)
                && (value != (int) '\'' )){
                name += (char) value;
            }
            newChannel = new Channel(name);
        
                        
            //parse comma separated integers until 'b' is found.
            //This is the data for this channel.
            String numString="";
            double dataPoint;
            while ( ( (value=bufferedReader.read())!= -1)
                && (value != (int) 'b' )){
                if (value != (int) ','){
                    numString += (char) value;
                } else {
                    dataPoint = parseDouble(numString);
                    newChannel.data.add(dataPoint);
                    numString="";
                }
            }
            //System.out.println();
            //System.out.println(channel);
            
            
        } catch (FileNotFoundException ex) {
            System.out.println("File not found.");
        } catch (IOException ex) {
            System.out.println("IO Exception on opening.");
        } finally {
            try {
                fileReader.close();
            } catch (IOException ex) {
                System.out.println("IO Exception on closing.");
            }
        }
        return newChannel;
    }
    
}
