/*
 *   INSERT LICENSE INFO HERE.  MIT OR GNU?
 */
package eeg_analyzer;

/**
 *
 * @author Jeff Blankenship
 * @author Taylor Somma
 */
public class FileTester {
    
    public static void main (String Args[]){
        
        String filename = "data/00004087_s003_t004.txt";
        
        //Channel channel=null;
        //channel = Channel.fromFile(fileName);
        
        //System.out.println(channel);
        
        Record record = new Record(filename);
        
        Channel channel = record.getChannel(2);
        //System.out.println(channel);
        
        
        
    }
    
    
}
