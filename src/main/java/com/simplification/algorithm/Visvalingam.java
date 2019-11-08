package com.simplification.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import com.simplification.algorithm.model.Pair;
import com.simplification.algorithm.model.SortableWeighted;

/**
 * Visvalingam-Whyatt algorithm
 * @author fab
 *
 */
public class Visvalingam {

	public static class VWPoint extends Pair<Double, Double> implements Comparable<VWPoint> {
		/**
		     *
		     */
		private static final long serialVersionUID = -2174680269775101563L;

		private String id;
		private Double vwArea;

		public VWPoint(Double key, Double value) {
			super(key, value);
		}

		public VWPoint(Double key, Double value, String id) {
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
		public int compareTo(VWPoint o) {
			return getKey().compareTo(o.getKey());
		}

		public Double getVwArea() {
			return vwArea;
		}

		public void setVwArea(Double vwArea) {
			this.vwArea = vwArea;
		}
	}

	private static class Triangle implements Comparable<Triangle> {

		private VWPoint[] VWPoints = new VWPoint[3];
		private Triangle previous;
		private Triangle next;

		public Triangle(VWPoint p1, VWPoint p2, VWPoint p3) {
			super();
			VWPoints[0] = p1;
			VWPoints[1] = p2;
			VWPoints[2] = p3;
		}

		public double area() {
			return 0.5 * Math.abs(
					(VWPoints[2].getKey() - VWPoints[0].getKey()) * (VWPoints[2].getValue() - VWPoints[1].getValue())
							- (VWPoints[2].getKey() - VWPoints[1].getKey())
									* (VWPoints[2].getValue() - VWPoints[1].getValue()));
		}

		@Override
		public int compareTo(Triangle arg0) {
			return VWPoints[1].getVwArea().compareTo(arg0.VWPoints[1].getVwArea());
		}
	}

	private static void update(Triangle triangle, PriorityQueue<Triangle> heap) {
		heap.remove(triangle);
		triangle.VWPoints[1].vwArea = triangle.area();
		heap.add(triangle);
	}

	public static void simplifiedVisvalingam(List<VWPoint> pointList, int pointsToKeep, List<VWPoint> out) {
		int len = pointList.size();
		List<Triangle> triangles = new ArrayList<>();
		PriorityQueue<Triangle> heap = new PriorityQueue<>();
		double maxArea = 0.0;

		Triangle triangle;
		for (int i = 1; i < pointList.size() - 1; ++i) {
			triangle = new Triangle(pointList.get(i - 1), pointList.get(i), pointList.get(i + 1));

			triangle.VWPoints[1].vwArea = triangle.area();
			triangles.add(triangle);
			heap.add(triangle);
		}

		for (int i = 0, n = triangles.size(); i < n; ++i) {
			triangle = triangles.get(i);
			if (i > 0)
				triangle.previous = triangles.get(i - 1);
			if (i < n - 1)
				triangle.next = triangles.get(i + 1);
		}

		while ((triangle = heap.poll()) != null) {
			if (triangle.VWPoints[1].vwArea < maxArea)
				triangle.VWPoints[1].vwArea = maxArea;
			else
				maxArea = triangle.VWPoints[1].vwArea;

			if (triangle.previous != null) {
				triangle.previous.next = triangle.next;
				triangle.previous.VWPoints[2] = triangle.VWPoints[2];
				update(triangle.previous, heap);
			} else {
				triangle.VWPoints[0].vwArea = triangle.VWPoints[1].vwArea;
			}

			if (triangle.next != null) {
				triangle.next.previous = triangle.previous;
				triangle.next.VWPoints[0] = triangle.VWPoints[0];
				update(triangle.next, heap);
			} else {
				triangle.VWPoints[0].vwArea = triangle.VWPoints[1].vwArea;
			}
		}

		Double[] weights = new Double[len];
		for (int i = 0; i < len; i++) {
			if (pointList.get(i).vwArea == null)
				weights[i] = Double.MAX_VALUE;
			else
				weights[i] = pointList.get(i).vwArea = Math.random();
		}

		Comparator<SortableWeighted> cmp = Collections.reverseOrder();

		List<SortableWeighted> weightsDescending = new ArrayList<>();
		for (int i = 0; i < weights.length; i++)
			weightsDescending.add(new SortableWeighted(i, weights[i]));
		Collections.sort(weightsDescending, cmp);

		out.clear();
		for (int i = 0; i < pointsToKeep; i++) {
			out.add(pointList.get(weightsDescending.get(i).getIndex()));
		}
		Collections.sort(out);
	}

}
