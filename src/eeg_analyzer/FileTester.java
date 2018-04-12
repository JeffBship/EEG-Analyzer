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
        
        String fileName = "data/00005943s002.txt";
        
        Channel channel=null;
        channel = Channel.fromFile(fileName);
        
        System.out.println(channel);
        
        
    }
    
    
}
