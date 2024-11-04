package com.JIRSend.ui;

public class Log {
    public static int ERROR_ONLY = 0;
    public static int ERROR_AND_WARNING = 1;
    public static int ALL = 2;
    public static int ERROR = 0;
    public static int WARNING = 1;
    public static int LOG = 2;
    private static boolean verbose = false;
    private static int displayTreshold = Integer.MAX_VALUE;
    
    /**
     * Set wether debug & errors are printed to console
     * If they are, there will be printed only if their priority is <= threshold (default INT_MAX)
     * 
     * @param on
     * @param threshold
     */
    public static void setVerbose(boolean on, int threshold) {
        verbose = on;
        displayTreshold = threshold;
    }

    /**
     * Set wether debug & errors are printed to console
     * @param on
     */
    public static void setVerbose(boolean on) {
        verbose = on;
    }

    public static void setThreshold(int threshold) {
        displayTreshold = threshold;
    }

    /**
     * Will println out string if verbose is active and if indicationLevel <= displayThreshold = Integer.MAX_VALUE
     * @param string
     * @param indicationLevel (default 0)
     */
    public static void l(String string, int indicationLevel) {
        printWithStack(string, indicationLevel, Thread.currentThread().getStackTrace()[2]);
        //if (verbose && indicationLevel <= displayTreshold) System.out.println("["+caller.getClassName() + "." + caller.getMethodName() +"] "+string);
    }

    /**
     * Will println out string if displayThreshold >= 0
     * @param string
     */
    public static void l(String string) {
        printWithStack(string, 0, Thread.currentThread().getStackTrace()[2]);
    }

    /**
     * @deprecated
     * Will print out string if displayThreshold >= 0
     * @param string
     */
    public static void log(String string) {
        l(string, 0);
    }

    /**
     * Will println err string if verbose is active and if indicationLevel <= displayThreshold = Integer.MAX_VALUE
     * @param string
     * @param indicationLevel (default 0)
     */
    public static void e(String err, int indicationLevel) {
        if (verbose && indicationLevel <= displayTreshold) System.err.println(err);
    }

    /**
     * Will println err string if displayThreshold >= 0
     * @param string
     */
    public static void e(String err) {
        e(err, 0);
    }

    /**
     * @deprecated
     * Will println err string if displayThreshold >= 0
     * @param string
     */
    public static void err(String err) {
        e(err, 0);
    }

    private static String levelToString(int indicationLevel) {
        switch (indicationLevel) {
            case 0: return "ERROR";
            case 1: return "WARNING";
            case 2: return "LOG";
            default: return "OTHER";
        }
    }

    private static void printWithStack(String string, int indicationLevel, StackTraceElement stack) {
        if (verbose && indicationLevel <= displayTreshold)
            System.out.println("["+levelToString(indicationLevel)+"] ("+stack.getClassName()+"->"+stack.getMethodName()+") "+string);
    }
}
