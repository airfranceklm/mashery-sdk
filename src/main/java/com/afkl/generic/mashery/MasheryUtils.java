package com.afkl.generic.mashery;

/**
 * Utils class
 */
public class MasheryUtils {


    /**
     * <p>Compares two Strings, returning true if they represent equal strings of characters.</p>
     *
     * @param o1 the first String, may be null
     * @param o2 the second String, may be null
     * @return true, if the Strings are equal
     */
    public static boolean isEqual(String o1, String o2) {
        return o1 == o2 || (o1 != null && o1.equals(o2));
    }
}
