package reachability;

import java.util.Comparator;

public class StringBasedComparator<T> implements Comparator<T> {
	public int compare(T o1, T o2) {
		return o1.toString().compareTo(o2.toString());
	}
}
