package org.umn.visualization;

import java.util.HashMap;

import org.gistic.taghreed.basicgeom.MBR;

public class VisoBucket {
	MBR mbr;
	//Hash keywords
	private HashMap<String, Integer> keywords;
	// cardinality 
	private int[] cardinality; 
	
	public VisoBucket() {
		// TODO Auto-generated constructor stub
		keywords = new HashMap<String, Integer>();
		cardinality = new int[365];
	}
	
	public int[] getCardinality() {
		return cardinality;
	}
	
	
	public void setCardinality(int[] cardinality) {
		this.cardinality = cardinality;
	}
	
	public HashMap<String, Integer> getKeywords() {
		return keywords;
	}
	
	public void setKeywords(HashMap<String, Integer> keywords) {
		this.keywords = keywords;
	}
	
	public MBR getMbr() {
		return mbr;
	}
	
	public void setMbr(MBR mbr) {
		this.mbr = mbr;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

}
