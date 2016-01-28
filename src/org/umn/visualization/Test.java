package org.umn.visualization;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.gistic.taghreed.basicgeom.MBR;
import org.gistic.taghreed.collections.TopTweetResult;
import org.gistic.taghreed.collections.Tweet;
import org.gistic.taghreed.diskBaseQuery.server.ServerRequest;
import org.gistic.taghreed.diskBaseQuery.server.ServerRequest.queryIndex;
import org.gistic.taghreed.diskBaseQuery.server.ServerRequest.queryLevel;
import org.gistic.taghreed.diskBaseQuery.server.ServerRequest.queryType;



public class Test {

	public static void buildQuadTRee() throws FileNotFoundException,
			IOException, ParseException, InterruptedException {
		QuadTree quadtree = new QuadTree(new RectangleQ(-180, -90, 180, 90),
				1000);
		ServerRequest req = new ServerRequest();
		req.setType(queryType.tweet);
		req.setIndex(queryIndex.rtree);
		MBR mbr = new MBR("maxlon 180 maxlat 90 minlon -180 minlat -90");
		req.setMBR(mbr);
		req.setQueryResolution(queryLevel.Day);
		req.setStartDate("2014-05-01");
		req.setEndDate("2014-05-2");
		TopTweetResult temp = req.getTweetsRtreeDays();
		for (Tweet object : temp.getTweet()) {
			quadtree.insert(new PointQ(object.lon,object.lat));
		}
		
		quadtree.StoreRectanglesWKT();

		File file = new File(System.getProperty("user.dir") + "/quadtree.dat");
		boolean stored = quadtree.storeQuadToDisk(file);
		if(stored){
			System.out.println("Stored Successfully");
		}else{
			System.out.println("Error while Storing ");
		}

	}

	public static void QueryQuadTree() throws IOException {
		QuadTree quadtree = new QuadTree(new RectangleQ(-180, -90, 180, 90),
				1000);
		File file = new File(System.getProperty("user.dir") + "/quadtree.dat");
		boolean loadQuadToMemory = quadtree.loadQuadToMemory(file);
		if (loadQuadToMemory) {
			ArrayList<PointQ> result = new ArrayList<PointQ>();
			quadtree.get(new RectangleQ(-180, -90, 180, 90), result);
			System.out.println("Result size = " + result.size());
			quadtree.StoreRectanglesWKT();
		}else{
			System.out.println("Could not load to memory");
		}
	}

	public static void main(String[] args) throws FileNotFoundException,
			IOException, ParseException, InterruptedException {

		 //buildQuadTRee();
		QueryQuadTree();

	}

}
