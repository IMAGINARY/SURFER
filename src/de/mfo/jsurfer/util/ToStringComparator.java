package de.mfo.jsurfer.util;

import java.util.Comparator;

public class ToStringComparator implements Comparator {
    public int compare( java.lang.Object o1, java.lang.Object o2) {
        return o1.toString().compareTo(o2.toString());
    }
}
