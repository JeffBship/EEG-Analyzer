/*
 *   INSERT LICENSE INFO HERE.  MIT OR GNU?
 */
package eeg_analyzer;

/**
 * Global variables.  These variables are provided used in many places 
 * and are maintained here for easy adjusting.
 * @author Jeff Blankenship
 */
public class Globals {
    
    //Sets difference between average and any given sample
    //to designate a spike, expressed as a percentage of the average.
    //i.e. 200.0 would be twice the average.
    public static final Double SPIKE_DIF = 100.0;
    
    //Time in millisec longer than which a high amplitude is not a spike
    public static final Double SPIKE_MAX_DURATION = 1000.0;
    
    //Time in millisec shorter than which a high amplitude is not a spike
    public static final Double SPIKE_MIN_DURATION = 0.0;
    
    
    
    //Sets size of chart display, as percentage of total screen size.
    public static final Double CHART_SCALE = 50.0;
    
}
