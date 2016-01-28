package org.umn.visualization;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;


import java.io.Serializable;

public class QuadTree implements Serializable {
	RectangleQ spaceMbr;
	int nodeCapacity;
	int level;
	//VisoBucket elements;
	List<PointQ> elements;
	boolean hasChild;
	QuadTree NW, NE, SE, SW; // four subtrees
	//
	//OutputStreamWriter writer;
	List<RectangleQ> result;
	int counter = 0;

	public QuadTree(RectangleQ mbr, int capacity) {
		spaceMbr = mbr;
		int level = 0;
		this.nodeCapacity = capacity;
		this.elements = new ArrayList<PointQ>();
		this.hasChild = false;
	}

	// Split the tree into 4 quadrants
	private void split() {
		double subWidth = (this.spaceMbr.getWidth() / 2);
		double subHeight = (this.spaceMbr.getHeight() / 2);
		PointQ midWidth;
		PointQ midHeight;
		midWidth = new PointQ((this.spaceMbr.x1 + subWidth), this.spaceMbr.y1);
		midHeight = new PointQ(this.spaceMbr.x1, (this.spaceMbr.y1 + subHeight));

		this.SW = new QuadTree(new RectangleQ(this.spaceMbr.x1,
				this.spaceMbr.y1, midWidth.x, midHeight.y), this.nodeCapacity);
		this.NW = new QuadTree(new RectangleQ(midHeight.x, midHeight.y,
				midWidth.x, this.spaceMbr.y2), this.nodeCapacity);
		this.NE = new QuadTree(new RectangleQ(midWidth.x, midHeight.y,
				this.spaceMbr.x2, this.spaceMbr.y2), this.nodeCapacity);
		this.SE = new QuadTree(new RectangleQ(midWidth.x, midWidth.y,
				this.spaceMbr.x2, midHeight.y), this.nodeCapacity);
	}

	/**
	 * Insert an object into this tree
	 */
	public void insert(PointQ p) {
		// check if there is chiled or not before insert
		// First case if node doesn't have child
		if (!this.hasChild) {
			/*
			 * if the elements in the node less than the capacity insert
			 * otherwise split the node and redistribute the nodes between the
			 * children.
			 */
			if (this.elements.size() <= this.nodeCapacity) {
				this.elements.add(p);
				this.hasChild = false;
			} else {
				// Number of node exceed the capacity split and then reqrrange
				// the Points
				this.split();
				reArrangePointsinChildren(this.elements);
				this.elements.clear();
				this.hasChild = true;
			}
		}
		/*
		 * Else Case if the node has child we need to trace the place where the
		 * point belong to
		 */
		else {
			 if(p.isIntersected(this.SW.spaceMbr)){
			this.SW.insert(p);
			 }else if(p.isIntersected(this.NW.spaceMbr)){
			this.NW.insert(p);
			 }else if(p.isIntersected(this.NE.spaceMbr)){
			this.NE.insert(p);
			 }else if(p.isIntersected(this.SE.spaceMbr)){
			this.SE.insert(p);
			 }
		}

	}
	
	/**
	 * This method get the visualize buckets 
	 * @param queryMBR
	 * @param values
	 * @return
	 */
	public ArrayList<PointQ> get(RectangleQ queryMBR, ArrayList<PointQ> values) {
		if (this.hasChild) {
			if (this.NW.spaceMbr.contains(queryMBR)) {
				this.NW.get(queryMBR, values);
			}
			if (this.NE.spaceMbr.contains(queryMBR)) {
				this.NE.get(queryMBR, values);
			}
			if (this.SE.spaceMbr.contains(queryMBR)) {
				this.SE.get(queryMBR, values);
			}
			if (this.SW.spaceMbr.contains(queryMBR)) {
				this.SW.get(queryMBR, values);
			}
			return values;
		}
		if (this.hasChild == false && this.elements.size() != 0) {
			values.addAll(this.elements);
		}
		return values;
	}
	
//	public void aggregateStatistics(QuadTree node){
//		if(!node.hasChild){
//			if(node.elements.size() >0){
//				
//			}
//		}else{// node has children
//			List<VisoBucket> temp = new ArrayList<VisoBucket>();
//			int[] aggregate = new int[365];
//			for(int i =0; i< 365;i++){
//				node.SE
//			}
//		}
//	}

	/**
	 * This method redistribute the points between the 4 new quadrant child
	 * 
	 * @param list
	 */
	private void reArrangePointsinChildren(List<PointQ> list) {
		for(PointQ p : list){
    		if(p.isIntersected(this.SW.spaceMbr)){
    			this.SW.elements.add(p);
    		}else if(p.isIntersected(this.NW.spaceMbr)){
    			this.NW.elements.add(p);
    		}else if(p.isIntersected(this.NE.spaceMbr)){
    			this.NE.elements.add(p);
    		}else if(p.isIntersected(this.SE.spaceMbr)){
    			this.SE.elements.add(p);
    		}
    	}
	}

	private void printLeafNodes(QuadTree node,OutputStreamWriter writer) throws IOException {
		if (!node.hasChild) {
			result.add(node.spaceMbr);
			writer.write(toWKT(node.spaceMbr)+
					"\t"+node.level+ "\n");
//			System.out.println(counter + "\t" + node.spaceMbr.toString());
		} else {
			printLeafNodes(node.SW,writer);
			printLeafNodes(node.NW,writer);
			printLeafNodes(node.NE,writer);
			printLeafNodes(node.SE,writer);
		}
	}
	
	private void printAllNodes(QuadTree node, OutputStreamWriter writer) throws IOException{
		result.add(node.spaceMbr);
		writer.write(toWKT(node.spaceMbr)+ "\n");
//		System.out.println(counter + "\t" + node.spaceMbr.toString());
		printLeafNodes(node.SW,writer);
		printLeafNodes(node.NW,writer);
		printLeafNodes(node.NE,writer);
		printLeafNodes(node.SE,writer);
	}

	public String toWKT(RectangleQ polygon) {
		return (counter++) + "\tPOLYGON ((" + polygon.x2 + " " + polygon.y1
				+ ", " + polygon.x2 + " " + polygon.y2 + ", " + polygon.x1
				+ " " + polygon.y2 + ", " + polygon.x1 + " " + polygon.y1
				+ ", " + polygon.x2 + " " + polygon.y1 + "))";
	}

	/**
	 * Store quad tree to disk
	 * 
	 * @throws IOException
	 */
	public boolean storeQuadToDisk(File f) throws IOException {
		try {
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(spaceMbr);
            oos.writeObject(nodeCapacity);
            oos.writeObject(level);
            oos.writeObject(elements);
            oos.writeObject(hasChild);
            oos.writeObject(NW);
            oos.writeObject(NE);
            oos.writeObject(SE);
            oos.writeObject(SW);
            oos.close();
            fos.close();
        } catch (IOException ex) {
            return false;
        }
        return true;

	}

	/**
	 * Restore quad tree to memory
	 */
	public boolean loadQuadToMemory(File f) {
		 try {
	            FileInputStream fis = new FileInputStream(f);
	            ObjectInputStream ois = new ObjectInputStream(fis);
	            this.spaceMbr = (RectangleQ)ois.readObject();
	            this.nodeCapacity = (int)ois.readObject();
	            this.level = (int)ois.readObject();
	            this.elements = (List<PointQ>)ois.readObject();
	            this.hasChild = (boolean)ois.readObject();
	            this.NW = (QuadTree)ois.readObject();
	            this.NE = (QuadTree)ois.readObject();
	            this.SE = (QuadTree)ois.readObject();
	            this.SW = (QuadTree)ois.readObject();
	            ois.close();
	            fis.close();
	        } catch (IOException e) {
	            return false;
	        } catch (ClassNotFoundException e) {
	            return false;
	        }
	        return true;
	}

	public void StoreRectanglesWKT() throws IOException {
//		for (int i = 0; i < LevelNumbers; i++) {
//			this.insert(1);
//		}
		 OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(
				System.getProperty("user.dir") + "/viso_quad.WKT", false),
				"UTF-8");
		result = new ArrayList<RectangleQ>();
		//printAllNodes(this);
		printLeafNodes(this,writer);
		writer.close();
//		System.out.println("number of buckets in the leaves:"+counter+
//				"estimated Size = "+((1.47*counter)/1024)+" MB");
	}
	


	public static void main(String[] args) throws IOException {
		QuadTree test = new QuadTree(new RectangleQ(-180, -90, 180, 90), 1);
		//test.packInRectangleQs(17);

	}

}