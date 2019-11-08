package com.simplification.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CollectionsUtility {
	public static <T> List<T> asList(Collection<T> c) {
		List<T> list = new ArrayList<T>(c);
		return list;
	}

	public static <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
		List<T> list = new ArrayList<T>(c);
		Collections.sort(list);
		return list;
	}

	public static <T extends Comparable<? super T>> List<T> asReverseSortedList(Collection<T> c) {

		// create comparator for reverse order
		Comparator<T> cmp = Collections.reverseOrder();

		List<T> list = new ArrayList<T>(c);
		Collections.sort(list, cmp);
		return list;
	}
}
