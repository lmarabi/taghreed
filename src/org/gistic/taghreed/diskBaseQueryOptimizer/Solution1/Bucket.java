package org.gistic.taghreed.diskBaseQueryOptimizer.Solution1;

import java.util.ArrayList;
import java.util.List;

import org.gistic.taghreed.basicgeom.MBR;
import org.gistic.taghreed.diskBaseQueryOptimizer.Cluster;
import org.gistic.taghreed.diskBaseQueryOptimizer.DayCardinality;

public class Bucket implements Comparable<Bucket>{
	private int id;
    private MBR area;
    private List<DayCardinality> dayCardinality;
    private List<Cluster> cluster;//to be implemented so a bucket contains clusters, for now the implementation as follow each bucket contains a list of days only. 
    private double persent;
    

    public Bucket() {
    	this.dayCardinality = new ArrayList<DayCardinality>();
    	this.area = new MBR();
    }
    
    public Bucket(MBR mbr,int id){
    	this.area = mbr;
    	this.id = id;
    	this.dayCardinality = new ArrayList<DayCardinality>();
    }
    
    public void setId(int id) {
		this.id = id;
	}
    
    public int getId() {
		return id;
	}

    public MBR getArea() {
        return area;
    }

    public void setArea(MBR area) {
        this.area = area;
    }

    public void setCardinality(String day,long cardinality) {
    	this.dayCardinality.add(new DayCardinality(day, cardinality));
    }
    
    public void setCardinality(String day,double cardinality) {
    	this.dayCardinality.add(new DayCardinality(day, Math.round(cardinality) ));
    }
    
    public void setPersent(double persent) {
		this.persent = persent;
	}
    
    public double getPersent() {
		return persent;
	}
    
    public List<Cluster> getCluster() {
		return cluster;
	}
    
    public void setCluster(List<Cluster> cluster) {
		this.cluster = cluster;
	}
    
    

    /**
     * Cardinality of the partition is the number of rows in this partition
     * @return 
     */
    public long getCardinality(String day) {
       for(DayCardinality items : this.dayCardinality){
        	if(items.getDay().equals(day)){
        		return items.getCardinality();
        	}
        }
       return 0;
    }
    
    public List<DayCardinality> getDayCardinality() {
		return dayCardinality;
	}
    
    public void setDayCardinality(List<DayCardinality> dayCardinality) {
		this.dayCardinality = dayCardinality;
	}
    
    public void parseFromText(String fromText){
    	DayCardinality day;
    	String[] token = fromText.split("#");
    	this.id = Integer.parseInt(token[0]);
    	this.area.parse(token[1]);
    	String[] days = token[2].split(",");
    	for(String d : days){    	
    		day = new DayCardinality();
    		day.parse(d);
    		this.dayCardinality.add(day);
    	}
    }
    
    
    
    @Override
    public String toString() {
    	// TODO Auto-generated method stub
    	StringBuilder temp = new StringBuilder();
    	temp.append(this.id+"#"+this.area.toString()+"#");
    	for(DayCardinality day : this.dayCardinality){
    		temp.append(day.toString()+",");
    	}
    	return temp.toString();
    }
    
    
    public String partitionToWKT(){
		return this.id + "\tPOLYGON (("
    +this.area.getMax().getLon() + " "+ this.area.getMin().getLat() 
    +", "+ this.area.getMax().getLon() + " "+ this.area.getMax().getLat()
    +", "+ this.area.getMin().getLon() + " "+ this.area.getMax().getLat()
    +", "+ this.area.getMin().getLon() + " "+ this.area.getMin().getLat()
    +", "+ this.area.getMax().getLon() + " "+ this.area.getMin().getLat()
    + "))\t" + this.persent;
	}

	@Override
	public int compareTo(Bucket arg0) {
		return arg0.id - id ;
	}
}
