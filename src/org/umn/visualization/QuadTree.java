package org.umn.visualization;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import edu.umn.cs.spatialHadoop.core.Point;
import edu.umn.cs.spatialHadoop.core.Rectangle;

public class QuadTree {
	Rectangle spaceMbr;
	int nodeCapacity;
	VisoBucket elements;
	boolean hasChild;
	boolean isleaf;
	QuadTree NW, NE, SE, SW; // four subtrees
	//
	OutputStreamWriter writer;
	List<Rectangle> result;
	int counter = 0;

	public QuadTree(Rectangle mbr, int capacity) {
		spaceMbr = mbr;
		this.nodeCapacity = capacity;
		this.elements = null;
		this.hasChild = false;
	}

	// Split the tree into 4 quadrants
	private void split() {
		double subWidth = (this.spaceMbr.getWidth() / 2);
		double subHeight = (this.spaceMbr.getHeight() / 2);
		Point midWidth;
		Point midHeight;
		midWidth = new Point((this.spaceMbr.x1 + subWidth), this.spaceMbr.y1);
		midHeight = new Point(this.spaceMbr.x1, (this.spaceMbr.y1 + subHeight));

		this.SW = new QuadTree(new Rectangle(this.spaceMbr.x1,
				this.spaceMbr.y1, midWidth.x, midHeight.y), this.nodeCapacity);
		this.NW = new QuadTree(new Rectangle(midHeight.x, midHeight.y,
				midWidth.x, this.spaceMbr.y2), this.nodeCapacity);
		this.NE = new QuadTree(new Rectangle(midWidth.x, midHeight.y,
				this.spaceMbr.x2, this.spaceMbr.y2), this.nodeCapacity);
		this.SE = new QuadTree(new Rectangle(midWidth.x, midWidth.y,
				this.spaceMbr.x2, midHeight.y), this.nodeCapacity);
	}

	/**
	 * Insert an object into this tree
	 */
	public void insert(VisoBucket p) {
		// check if there is chiled or not before insert
		// First case if node doesn't have child
		if (!this.hasChild) {
			/*
			 * if the elements in the node less than the capacity insert
			 * otherwise split the node and redistribute the nodes between the
			 * children.
			 */
			if (this.elements == null) {
				this.elements = p;
				this.nodeCapacity++;
			} else {
				// Number of node exceed the capacity split and then reqrrange
				// the points
				this.split();
				reArrangePointsinChildren(this.elements);
				this.elements= null;
				this.elements = null;
				this.hasChild = true;
			}
		}
		/*
		 * Else Case if the node has child we need to trace the place where the
		 * point belong to
		 */
		else {
			// if(p.isIntersected(this.SW.spaceMbr)){
			this.SW.insert(p);
			// }else if(p.isIntersected(this.NW.spaceMbr)){
			this.NW.insert(p);
			// }else if(p.isIntersected(this.NE.spaceMbr)){
			this.NE.insert(p);
			// }else if(p.isIntersected(this.SE.spaceMbr)){
			this.SE.insert(p);
			// }
		}

	}
	
	/**
	 * This method get the visualize buckets 
	 * @param queryMBR
	 * @param values
	 * @return
	 */
	public ArrayList<VisoBucket> get(Rectangle queryMBR, ArrayList<VisoBucket> values) {
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
		if (this.hasChild == false && this.elements != null) {
			values.add(this.elements);
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
	private void reArrangePointsinChildren(VisoBucket bucket) {
//		for (VisoBucket p : list) {
			// if(p.isIntersected(this.SW.spaceMbr)){
			this.SW.elements = bucket;
			// }else if(p.isIntersected(this.NW.spaceMbr)){
			this.NW.elements = bucket;
			// }else if(p.isIntersected(this.NE.spaceMbr)){
			this.NE.elements = bucket;
			// }else if(p.isIntersected(this.SE.spaceMbr)){
			this.SE.elements = bucket;
			// }
//		}
	}

	private void printLeafNodes(QuadTree node) throws IOException {
		if (!node.hasChild) {
			result.add(node.spaceMbr);
			writer.write(toWKT(node.spaceMbr)
					+ "\n");
			System.out.println(counter + "\t" + node.spaceMbr.toString());
		} else {
			printLeafNodes(node.SW);
			printLeafNodes(node.NW);
			printLeafNodes(node.NE);
			printLeafNodes(node.SE);
		}
	}
	
	private void printAllNodes(QuadTree node) throws IOException{
		result.add(node.spaceMbr);
		writer.write(toWKT(node.spaceMbr)+ "\n");
		System.out.println(counter + "\t" + node.spaceMbr.toString());
		printLeafNodes(node.SW);
		printLeafNodes(node.NW);
		printLeafNodes(node.NE);
		printLeafNodes(node.SE);
	}

	public String toWKT(Rectangle polygon) {
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
	public void storeQuadToDisk(QuadTree node) throws IOException {
		writer = new OutputStreamWriter(new FileOutputStream(
				System.getProperty("user.dir") + "/viso_quad.dat", false),
				"UTF-8");
		printAllNodes(this);
		writer.close();

	}

	/**
	 * Restore quad tree to memory
	 */
	public void loadQuadToMemory() {

	}

	public Rectangle[] packInRectangles(int LevelNumbers) throws IOException {
		for (int i = 0; i < LevelNumbers; i++) {
			this.insert(new VisoBucket());
		}
		result = new ArrayList<Rectangle>();
		writer = new OutputStreamWriter(new FileOutputStream(
				System.getProperty("user.dir") + "/viso_quad.WKT", false),
				"UTF-8");
		//printAllNodes(this);
		printLeafNodes(this);
		writer.close();
		Rectangle[] cellinfo = new Rectangle[result.size()];
		return result.toArray(cellinfo);
	}

	public static void main(String[] args) throws IOException {
		QuadTree test = new QuadTree(new Rectangle(-180, -90, 180, 90), 1);
		test.packInRectangles(17);

	}

}