package org.umn.visualization;

import org.gistic.taghreed.basicgeom.MBR;

import edu.umn.cs.spatialHadoop.core.Rectangle;

public class GridIndex {
	//Granularity
	int granularity;
	//Grid index
	int columns; 
	int rows;
	VisoBucket[][] gridIndex;
	
	public GridIndex(int granularity) {
		// TODO Auto-generated constructor stub
		this.granularity = granularity; 
		calculateCellDimensions(granularity);
		gridIndex = new VisoBucket[360][180];
		for(int i =0 ; i< 360; i++)
			for(int j = 0; j < 180; j++)
				gridIndex[i][j]= new VisoBucket();
		
	}
	
	
	public void calculateCellDimensions(int granularity) {
	    int gridCols = 1;
	    int gridRows = 1;
	    while (gridRows * gridCols < granularity) {
	      // (  cellWidth          >    cellHeight        )
	      //if ((x2 - x1) / gridCols > (y2 - y1) / gridRows) {
	      if ((180-(-180)) / gridCols > (90 - (-90)) / gridRows) {  
	        gridCols++;
	      } else {
	        gridRows++;
	      }
	    }
	    columns = gridCols;
	    rows = gridRows;
	    System.out.println(columns);
	    System.out.println(rows);
	}
	
	
	//Search on the index. 
	void search(MBR query){
		double minLon = query.getMin().getLon() + 180;
		double minLat = query.getMin().getLat() + 90;
		double maxLon = query.getMax().getLon() + 180; 
		double maxLat = query.getMax().getLat() + 90;
		for(int x = (int)minLon; x <= maxLon;x++){
			for(int y = (int)minLat; y <= maxLat; y++){
				System.out.println(this.gridIndex[x][y].toString());
			}
		}
		
	}
	
	// update the index values
	
	
	//read Index from disk 
	
	
	//write index to disk 

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GridIndex x = new GridIndex(100000);
		

	}

}
