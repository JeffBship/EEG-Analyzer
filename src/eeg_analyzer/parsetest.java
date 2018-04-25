/*
 *   INSERT LICENSE INFO HERE.  MIT OR GNU?
 */
package eeg_analyzer;

import static java.lang.Double.parseDouble;

/**
 *
 * @author Jeff Blankenship
 */
public class parsetest {
    
    
    public static void main(String args[]){
        String numString = " \"sampleRate\": 400";
     
        System.out.println(numString); 
        
        String [] result = numString.split(" ");
        System.out.println("result length: " + result.length);
        
        System.out.println("last is: " + result[ result.length-1 ]);
        Double num = parseDouble(  result[ result.length-1 ]  );
        System.out.println(num);
        
        
    }
     
    
}
