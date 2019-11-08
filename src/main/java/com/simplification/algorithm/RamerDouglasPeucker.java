package com.simplification.algorithm;

import java.util.ArrayList;
import java.util.List;

import com.simplification.algorithm.model.Pair;
import com.simplification.algorithm.model.SortableWeighted;
import com.simplification.util.CollectionsUtility;

/**
 * Ramer Doulgas Peucker algorithm implementation
 * @author fab
 *
 */
public class RamerDouglasPeucker {
	public static class Point extends Pair<Double, Double> implements Comparable<Point> {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2174680269775101563L;

		private String id;

		public Point(Double key, Double value) {
			super(key, value);
		}

		public Point(Double key, Double value, String id) {
			super(key, value);
			this.id = id;
		}

		@Override
		public String toString() {
			return String.format("(%f, %f)", getKey(), getValue());
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		@Override
		public int compareTo(Point o) {
			return getKey().compareTo(o.getKey());
		}
	}
	
	
	private static double perpendicularDistance(Point pt, Point lineStart, Point lineEnd) {
		double dx = lineEnd.getKey() - lineStart.getKey();
		double dy = lineEnd.getValue() - lineStart.getValue();

		// Normalize
		double mag = Math.hypot(dx, dy);
		if (mag > 0.0) {
			dx /= mag;
			dy /= mag;
		}
		double pvx = pt.getKey() - lineStart.getKey();
		double pvy = pt.getValue() - lineStart.getValue();

		// Get dot product (project pv onto normalized direction)
		double pvdot = dx * pvx + dy * pvy;

		// Scale line direction vector and subtract it from pv
		double ax = pvx - pvdot * dx;
		double ay = pvy - pvdot * dy;

		return Math.hypot(ax, ay);
	}

	public static void ramerDouglasPeucker(List<Point> pointList, double epsilon, List<Point> out) {
		if (pointList.size() < 2)
			throw new IllegalArgumentException("Not enough points to simplify");

		// Find the point with the maximum distance from line between the start
		// and end
		double dmax = 0.0;
		int index = 0;
		int end = pointList.size() - 1;
		for (int i = 1; i < end; ++i) {
			double d = perpendicularDistance(pointList.get(i), pointList.get(0), pointList.get(end));
			if (d > dmax) {
				index = i;
				dmax = d;
			}
		}

		// If max distance is greater than epsilon, recursively simplify
		if (dmax > epsilon) {
			List<Point> recResults1 = new ArrayList<>();
			List<Point> recResults2 = new ArrayList<>();
			List<Point> firstLine = pointList.subList(0, index + 1);
			List<Point> lastLine = pointList.subList(index, pointList.size());
			ramerDouglasPeucker(firstLine, epsilon, recResults1);
			ramerDouglasPeucker(lastLine, epsilon, recResults2);

			// build the result list
			out.addAll(recResults1.subList(0, recResults1.size() - 1));
			out.addAll(recResults2);
			if (out.size() < 2)
				throw new RuntimeException("Problem assembling output");
		} else {
			// Just return start and end points
			out.clear();
			out.add(pointList.get(0));
			out.add(pointList.get(pointList.size() - 1));
		}
	}

	private static void doRecursiveSimplification(List<Point> pointList, Double[] weights, int start, int end) {
		if (end > start + 1) {
			double maxDist = -1;
			int maxDistIndex = 0;
			double dist;

			for (int i = start + 1; i < end; i++) {
				dist = perpendicularDistance(pointList.get(i), pointList.get(start), pointList.get(end));
				if (dist > maxDist) {
					maxDist = dist;
					maxDistIndex = i;
				}
			}
			weights[maxDistIndex] = maxDist + Math.random();

			doRecursiveSimplification(pointList, weights, start, maxDistIndex);
			doRecursiveSimplification(pointList, weights, maxDistIndex, end);
		}
	}

	public static void simplifiedRamerDouglasPeucker(List<Point> pointList, int pointsToKeep, List<Point> out) {
		int len = pointList.size();
		Double[] weights = new Double[len];

		doRecursiveSimplification(pointList, weights, 0, len - 1);
		weights[0] = Double.MAX_VALUE;
		weights[len - 1] = Double.MAX_VALUE;

		List<SortableWeighted> list = new ArrayList<>();
		for (int i = 0; i < weights.length; i++)
			list.add(new SortableWeighted(i, weights[i]));
		List<SortableWeighted> weightsDescending = CollectionsUtility.asReverseSortedList(list);

		out.clear();
		List<Point> res = new ArrayList<>();
		for (int i = 0; i < pointsToKeep; i++) {
			res.add(pointList.get(weightsDescending.get(i).getIndex()));
		}
		out.addAll(CollectionsUtility.asSortedList(res));
	}

}
