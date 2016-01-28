package org.umn.visualization;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import java.io.OutputStreamWriter;

public class QuadTreeIO {

	OutputStreamWriter writer;
	OutputStreamWriter writerWKT;
	OutputStreamWriter writerLevel; 
	BufferedReader reader; 
	int counter = 0;
	QuadTree tree; 
	
	public QuadTreeIO() {
		//tree = new QuadTree(new Rectangle(-180, -90, 180, 90), 1);
		
	}
	
	public void setTree(QuadTree tree) {
		this.tree = tree;
	}
	
	
	private String toWKT(RectangleQ polygon) {
		return "\tPOLYGON ((" + polygon.x2 + " " + polygon.y1
				+ ", " + polygon.x2 + " " + polygon.y2 + ", " + polygon.x1
				+ " " + polygon.y2 + ", " + polygon.x1 + " " + polygon.y1
				+ ", " + polygon.x2 + " " + polygon.y1 + "))";
	}
	
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		for(int level=0; level< 17; level++){
			System.out.println("Starting level "+level);
			QuadTreeIO quadIO = new QuadTreeIO();
			String file = System.getProperty("user.dir") + "/quadtree_"+level+".txt";
			quadIO.reader = new BufferedReader(new FileReader(
						file));
			quadIO.writer = new OutputStreamWriter(new FileOutputStream(
					System.getProperty("user.dir") + "/quadtree_"+(level+1)+".txt", true),
					"UTF-8");
			quadIO.writerWKT = new OutputStreamWriter(new FileOutputStream(
					System.getProperty("user.dir") + "/quadtree_"+(level+1)+".WKT", true),
					"UTF-8");
			
			String line = null;
			while ((line = quadIO.reader.readLine()) != null) {
				String[] rec = line.split(",");
				RectangleQ rectangle = new RectangleQ(Double.parseDouble(rec[0]),Double.parseDouble(rec[1])
						,Double.parseDouble(rec[2]),Double.parseDouble(rec[3]));
				quadIO.setTree(new QuadTree(rectangle, 1));
//				RectangleQ[] children = quadIO.tree.packInRectangles(1, quadIO.writer);
//				for(RectangleQ r : children){
//					quadIO.writer.write(r.x1+","+r.y1+","+r.x2+","+r.y2+"\n");
//					quadIO.writerWKT.write(quadIO.toWKT(r)+"\n");
//				}
				

			}
			quadIO.reader.close();
			quadIO.writer.close();
			quadIO.writerWKT.close();
			
			
		}
		System.out.println("Finish");

	}

}
