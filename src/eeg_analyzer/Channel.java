/*
 *   INSERT LICENSE INFO HERE.  MIT OR GNU?
 */
package eeg_analyzer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import static java.lang.Double.parseDouble;
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
    private ArrayList<Double> data;
    private Double max,min;
    private Integer count;
    
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
    public void add(int value){
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
    
    public ArrayList<Double> getData(){
        return this.data;
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
     * Minimum is the most negative.
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
     * Counts and reports the number of elements in the data ArrayList.
     * @return Integer
     */
    public Integer count(){
        this.count = this.data.size();
        return this.count;
    }
    
    public Iterator iterator(){
        Iterator iterator = this.data.iterator();
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
        return result;
    }
    
    /**
     * Constructor that builds channel data from a file.
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
