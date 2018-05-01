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

/**
 *
 * @author Jeff Blankenship
 */
public class Record {
    
    private String name;
    private String filename;
    private ArrayList<Channel> channelList = new ArrayList<>();
    private Double sampleRate, digMax, digMin, physMin, physMax;
    
    
    
    
    /**
     * Constructor that builds a record from a file.
     * @param filename
     */
    public Record (String filename){
        Channel newChannel = null;
        
        char ch = '\u0000'; //null
        int value;
        FileReader fileReader = null;
        
        try {
            fileReader = new FileReader("data/" + filename);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            
            while (true){
                //stop when there's nothing left in the file
                if (! bufferedReader.ready()) break;
                
                //advance until a single quote is found
                while ( ( (value=bufferedReader.read())!= (int) '\'' )){}
                //advance to next single quote, adding characters to name
                String channelName="";
                while ( ( (value=bufferedReader.read())!= (int) '\'' )){
                    channelName += (char) value;
                }
                //construct the new channel
                
                this.channelList.add( new Channel(channelName));
                int channelNum = this.channelList.size()-1;
                this.channelList.get(channelNum).setFilename(filename);
                System.out.print("channelNum: " + channelNum + " ");
                System.out.println("channelName: " + channelName);
                //advance past left bracket
                while ( ( (value=bufferedReader.read())!= (int) '[' )){}
                
                //add comma separated values until right bracket is found
                //This is the data for this channel.
                ArrayList<Double> data = new ArrayList<>();
                String numString="";
                Double dataPoint;
                while ( ( (value=bufferedReader.read())!= (int) ']' )){
                    if (value != (int) ','){
                        numString += (char) value;
                    } else {
                        dataPoint = parseDouble(numString);
                        data.add(dataPoint);
                        numString="";
                    }
                }
                this.channelList.get(channelNum).setData(data);
                
                //Parse SampleRate
                String sampleRateString="";
                //go past two spaces
                while ( ( (value=bufferedReader.read())!= (int) ' ' )){}
                while ( ( (value=bufferedReader.read())!= (int) ' ' )){}
                //add digits until a comma
                while ( ( (value=bufferedReader.read())!= (int) ',' )){
                    sampleRateString += (char) value;
                }
                this.sampleRate = parseDouble(sampleRateString);
                this.channelList.get(channelNum).setSampleRate(this.sampleRate);
            
                //Parse digMax
                String digMaxString="";
                //go past two spaces
                while ( ( (value=bufferedReader.read())!= (int) ' ' )){}
                while ( ( (value=bufferedReader.read())!= (int) ' ' )){}
                //add digits until a comma
                while ( ( (value=bufferedReader.read())!= (int) ',' )){
                    digMaxString += (char) value;
                }
                this.digMax = parseDouble(digMaxString);
                this.channelList.get(channelNum).setDigMax(this.digMax);
                
                //Parse digMin
                String digMinString="";
                //go past two spaces
                while ( ( (value=bufferedReader.read())!= (int) ' ' )){}
                while ( ( (value=bufferedReader.read())!= (int) ' ' )){}
                //add digits until a comma
                while ( ( (value=bufferedReader.read())!= (int) ',' )){
                    digMinString += (char) value;
                }
                this.digMin = parseDouble(digMinString);
                this.channelList.get(channelNum).setDigMin(this.digMin);
                
                //Parse physMin
                String physMinString="";
                //go past two spaces
                while ( ( (value=bufferedReader.read())!= (int) ' ' )){}
                while ( ( (value=bufferedReader.read())!= (int) ' ' )){}
                //add digits until a comma
                while ( ( (value=bufferedReader.read())!= (int) ',' )){
                    physMinString += (char) value;
                }
                this.physMin = parseDouble(physMinString);
                this.channelList.get(channelNum).setPhysMin(this.physMin);
                
                //Parse physMax
                String physMaxString="";
                //go past two spaces
                while ( ( (value=bufferedReader.read())!= (int) ' ' )){}
                while ( ( (value=bufferedReader.read())!= (int) ' ' )){}
                //add digits until a right curly bracket
                while ( ( (value=bufferedReader.read())!= (int) '}' )){
                    physMaxString += (char) value;
                }
                this.physMax = parseDouble(physMaxString);
                this.channelList.get(channelNum).setPhysMax(this.physMax);
                
                //skip one character after each channel
                bufferedReader.read();
            
            }
                        
            
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
        
    System.out.println("Parsed file" + filename);
    System.out.println("Found " + this.channelList.size() + " channels.");
        
        
    }
    
    public Channel getChannel(int channelNum){
        Channel result = null;
        if (channelNum> this.channelList.size()-1){
            System.out.println("error, attempting to exceed channelList size.");
        } else {
            result = this.channelList.get(channelNum);
        }
        return result;
    }
    
    /**
     * Returns the size of the record.  
     * @return number of channels in the record
     */
    public int getSize(){
        return this.channelList.size();
    }
    
    public Double getSampleRate(){
        return this.sampleRate;
    }
    
    
    
}
