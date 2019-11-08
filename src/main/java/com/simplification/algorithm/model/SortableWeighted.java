package com.simplification.algorithm.model;

public class SortableWeighted implements Comparable<SortableWeighted> {
	private Integer index;
	private Double val;

	public SortableWeighted(Integer index, Double val) {
		this.index = index;
		this.val = val;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public Double getVal() {
		return val;
	}

	public void setVal(Double val) {
		this.val = val;
	}

	@Override
	public int compareTo(SortableWeighted o) {
		return val.compareTo(o.getVal());
	}
}

