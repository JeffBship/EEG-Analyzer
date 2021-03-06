/*
 *   INSERT LICENSE INFO HERE.  MIT OR GNU?
 */
package eeg_analyzer;

/**
 * Global variables.  These variables are used in many places 
 * and are maintained here for easy adjusting in one location.
 * @author Jeff Blankenship
 */
public class Globals {
    
    //RULE 1: AMPLITUDE OF SPIKE
    //Sets difference between average and any given sample
    //to designate a spike, expressed as a percentage of the average.
    //i.e. 200.0 would be twice the average.
    public static final Double SPIKE_DIF = 50.0;
    
    //RULE 2: MAX DURATION OF SPIKE, msec
    //Time in millisec longer than which a high amplitude is not a spike
    public static final Double SPIKE_MAX_DURATION = 200.0;
    
    //RULE 3: MIN DURATION OF SPIKE, msec
    //Time in millisec shorter than which a high amplitude is not a spike
    public static final Double SPIKE_MIN_DURATION = 30.0;
    
    //RULE 4: MIN FREQ THETA WAVE, Hz
    public static final Double THETA_MIN = 3.5;
    
    //RULE 5: MAX FREQ THETA WAVE, Hz
    public static final Double THETA_MAX = 7.5;
    
    //RULE 6: THETA CONTRIBUTION FOR hasTheta()
    //unitless, but roughly equivalent to percent
    public static final Double THETA_CON = 10.0;
    
    //RULE 7: MIN FREQ DELTA WAVE, Hz
    public static final Double DELTA_MIN = 1.0;
    
    //RULE 8: MAX FREQ DELTA WAVE, Hz
    public static final Double DELTA_MAX = 3.0;
    
    //RULE 9: DELTA CONTRIBUTION FOR hasDelta() 
    //unitless, but roughly equivalent to percent
    public static final Double DELTA_CON = 10.0;
    
    //Sets size of chart display, as percentage of total screen size.
    public static final Double CHART_SCALE = 40.0;
    
    //Minimum frequency to display on spectrograph (filters DC component)
    public static final Double MIN_FREQ = 1.0;
    
    //Maximum frequency to display on spectrograph (filters main line noise)
    public static final Double MAX_FREQ = 50.0;
    
}
