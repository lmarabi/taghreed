package org.gistic.taghreed.diskBaseQueryOptimizer.Solution1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.net.ftp.parser.MVSFTPEntryParser;
import org.gistic.taghreed.basicgeom.MBR;
import org.gistic.taghreed.basicgeom.Point;
import org.gistic.taghreed.collections.Week;
import org.gistic.taghreed.diskBaseQuery.query.Lookup;
import org.gistic.taghreed.diskBaseQuery.server.ServerRequest;
import org.gistic.taghreed.diskBaseQuery.server.ServerRequest.queryIndex;
import org.gistic.taghreed.diskBaseQuery.server.ServerRequest.queryLevel;
import org.gistic.taghreed.diskBaseQueryOptimizer.Cluster;
import org.gistic.taghreed.diskBaseQueryOptimizer.DayCardinality;
import org.gistic.taghreed.diskBaseQueryOptimizer.GridCell;
import org.gistic.taghreed.diskBaseQueryOptimizer.HistogramCluster;

/**
 * The idea of this solution as follow ,
 * 
 * we list all the days with the volume, and construct the histogram based only
 * on the the present ----------- | day-volume-clusterid | , | histogram |
 * 
 * the clusters constains a list of histogram each buckets cell in histogram has
 * a ratio reflects the DayVolume/Cardinality
 * 
 * 
 * the algorithm as follow: 1- find the max and the min (x,y) 2- construct a
 * fixd withd bucket width divide the max-min\10,000 this way we construct grid
 * like structure for each day 3- fill the buckets of each day with the persent
 * of the data in that day. 4- find histogram that look similar to each other.
 * this we will end by haveing n number of clusters 5- combine buckets in the
 * histogram to save more space in the histogram. the same idea we discuss.
 * 
 * @author louai
 *
 */

public class MemoryHistogram {

	private List<HistogramCluster> cluster = new ArrayList<HistogramCluster>();

	String startDay;
	String endDay;

	HashMap<String, HistogramCluster> dayHistogram;
	HashMap<String, HistogramCluster> weekHistogram;
	HashMap<String, HistogramCluster> monthHistogram;
	// queryLevel level;
	Lookup lookup;

	// MBR of the grid cell
	Point maxPoint;
	Point minPoint;
	Map<String, List<Bucket>> initHistogram;
	List<Bucket> histogramBackets;
	List<Bucket> histogramBacketsExact;
	// Map<String,List<String>> clusters;
	// Day
	List<TemporalLookupTable> dayLookup;
	List<TemporalClusterTable> dayClusters;
	// Week
	List<TemporalLookupTable> weekLookup;
	List<TemporalClusterTable> weekClusters;
	// Month
	List<TemporalLookupTable> monthLookup;
	List<TemporalClusterTable> monthClusters;
	// time
	double starttime, endtime;
	double confidenceThreshold = 0.85;

	private void startLog() {
		starttime = System.currentTimeMillis();
	}

	private void endLog(String log) {
		endtime = System.currentTimeMillis();
		System.out.println(log + "\ttooks :" + (endtime - starttime) + " ms");
	}

	public MemoryHistogram(boolean initiate) throws IOException, ParseException {
		// init in memory histograms
		ServerRequest server = new ServerRequest();
		server.setIndex(queryIndex.rtree);
		this.lookup = server.getLookup();
		this.dayHistogram = new HashMap<String, HistogramCluster>();
		this.weekHistogram = new HashMap<String, HistogramCluster>();
		this.monthHistogram = new HashMap<String, HistogramCluster>();
		// Offline processing
		this.histogramBackets = new ArrayList<Bucket>();
		this.histogramBacketsExact = new ArrayList<Bucket>();
		this.initHistogram = new HashMap<String, List<Bucket>>();
		this.dayClusters = new ArrayList<TemporalClusterTable>();
		this.dayLookup = new ArrayList<TemporalLookupTable>();
	}

	private void readExactDayHistogram() throws ParseException, IOException {
		double temp, maxlon = 0, maxlat = 0;
		double minlat = Double.MAX_VALUE, minlon = Double.MAX_VALUE;
		MBR mbr = new MBR(new Point(90, 180), new Point(-90, -180));
		String startDate = "2000-01-01";
		String endDate = "3000-01-01";
		Map<String, String> days = this.lookup.getTweetsDayIndex(startDate,
				endDate);
		Iterator it = days.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> obj = (Entry<String, String>) it.next();
			dayHistogram
					.put(obj.getKey(), new HistogramCluster(obj.getValue()));
			// Get the maximum and the Minimum MBR over all paritions
			temp = dayHistogram.get(obj.getKey()).getMinLon();
			minlon = temp < minlon ? temp : minlon;
			temp = dayHistogram.get(obj.getKey()).getMinLat();
			minlat = temp < minlat ? temp : minlat;
			temp = dayHistogram.get(obj.getKey()).getMaxlat();
			maxlat = temp > maxlat ? temp : maxlat;
			temp = dayHistogram.get(obj.getKey()).getMaxLon();
			maxlon = temp > maxlon ? temp : maxlon;

		}
		// We partition the space of the histograms into buckets of equal width
		// size.
		maxPoint = new Point(maxlat, maxlon);
		minPoint = new Point(minlat, minlon);
		startLog();
		histogramBackets = creatHistogramBuckets(maxPoint, minPoint);
	}

	private void readExactWeekHistogram() throws ParseException, IOException {
		double temp, maxlon = 0, maxlat = 0;
		double minlat = Double.MAX_VALUE, minlon = Double.MAX_VALUE;
		MBR mbr = new MBR(new Point(90, 180), new Point(-90, -180));
		String startDate = "2000-01-01";
		String endDate = "3000-01-01";
		Map<Week, String> week = this.lookup.getAllTweetsWeekIndex(startDate,
				endDate);
		Iterator it = week.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Week, String> obj = (Entry<Week, String>) it.next();
			weekHistogram.put(obj.getKey().getWeekName(), new HistogramCluster(
					obj.getValue()));
			// Get the maximum and the Minimum MBR over all paritions
			temp = weekHistogram.get(obj.getKey().getWeekName()).getMinLon();
			minlon = temp < minlon ? temp : minlon;
			temp = weekHistogram.get(obj.getKey().getWeekName()).getMinLat();
			minlat = temp < minlat ? temp : minlat;
			temp = weekHistogram.get(obj.getKey().getWeekName()).getMaxlat();
			maxlat = temp > maxlat ? temp : maxlat;
			temp = weekHistogram.get(obj.getKey().getWeekName()).getMaxLon();
			maxlon = temp > maxlon ? temp : maxlon;

		}
		// We partition the space of the histograms into buckets of equal width
		// size.
		maxPoint = new Point(maxlat, maxlon);
		minPoint = new Point(minlat, minlon);
		startLog();
		this.histogramBackets.clear();
		histogramBackets = creatHistogramBuckets(maxPoint, minPoint);
	}

	private void readExactMonthkHistogram() throws ParseException, IOException {
		double temp, maxlon = 0, maxlat = 0;
		double minlat = Double.MAX_VALUE, minlon = Double.MAX_VALUE;
		MBR mbr = new MBR(new Point(90, 180), new Point(-90, -180));
		String startDate = "2000-01-01";
		String endDate = "3000-01-01";
		Map<String, String> month = this.lookup.getTweetsMonthsIndex(startDate,
				endDate);
		Iterator it = month.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> obj = (Entry<String, String>) it.next();
			monthHistogram.put(obj.getKey(),
					new HistogramCluster(obj.getValue()));
			// Get the maximum and the Minimum MBR over all paritions
			temp = monthHistogram.get(obj.getKey()).getMinLon();
			minlon = temp < minlon ? temp : minlon;
			temp = monthHistogram.get(obj.getKey()).getMinLat();
			minlat = temp < minlat ? temp : minlat;
			temp = monthHistogram.get(obj.getKey()).getMaxlat();
			maxlat = temp > maxlat ? temp : maxlat;
			temp = monthHistogram.get(obj.getKey()).getMaxLon();
			maxlon = temp > maxlon ? temp : maxlon;

		}
		// We partition the space of the histograms into buckets of equal width
		// size.
		maxPoint = new Point(maxlat, maxlon);
		minPoint = new Point(minlat, minlon);
		startLog();
		this.histogramBackets.clear();
		histogramBackets = creatHistogramBuckets(maxPoint, minPoint);
	}

	private void reset() {
		this.histogramBackets.clear();
		this.dayHistogram.clear();
		this.weekHistogram.clear();
		this.monthHistogram.clear();

	}

	/**
	 * This method is offline phase of to prepare the clusters of the histogram.
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	private void OfflinePhase(queryLevel q) throws IOException, ParseException {

		endLog("Create the buckets mbr");
		startLog();
		initHistogramo(q);
		System.out.println("Start writing to disk");
		writeHistogramToDisk("xxxxWorld_Histogram" + q.toString() + ".txt");
		writeWKTHistogramToDisk("xxxxWorld_Histogram" + q.toString() + ".WKT");
		System.out.println("End writing to disk");
		// ReadHistogramFromDisk("xxxxWorld_Histogram" + q.toString()+ ".txt");
		matchBuckets(q);
		endLog("ini Histogram");
		writeHistogramToDisk("xxxxWorld_Histogram_Cluster" + q.toString()
				+ "_CI(" + this.confidenceThreshold + ").txt");
		writeWKTHistogramToDisk("xxxxWorld_Histogram_Cluster" + q.toString()
				+ "_CI(" + this.confidenceThreshold + ").WKT");
		reset();

		// for(Bucket d : this.histogramBackets){
		// System.out.println(d.toString());
		// }

		// startLog();
		// matchDays(queryLevel.Day);
		// endLog("Creating clusters");
		//
		// System.out.println("**** Day level**** with size:"+
		// dayLookup.size());
		// for(TemporalLookupTable day : dayLookup){
		// System.out.println(day.toString());
		// }
		//
		// System.out.println("\n*****\nNumber of clusters:  "+dayClusters.size());
		// System.out.println("\n*****\ninit histogram left:  "+initHistogram.size());

		// Map<Week,String> week = this.lookup.getAllTweetsWeekIndex(startDate,
		// endDate);
		// it = week.entrySet().iterator();
		// while(it.hasNext()){
		// Map.Entry<Week, String> obj = (Entry<Week, String>) it.next();
		// weekHistogram.put(obj.getKey().toString(), new
		// HistogramCluster(obj.getValue()));
		// }
		//
		// Map<String,String> month =
		// this.lookup.getTweetsMonthsIndex(startDate, endDate);
		// it = month.entrySet().iterator();
		// while(it.hasNext()){
		// Map.Entry<String, String> obj = (Entry<String, String>) it.next();
		// monthHistogram.put(obj.getKey(), new
		// HistogramCluster(obj.getValue()));
		// }

	}

	/**
	 * Given the maxlat, maxlon, minlat, and minlon this method create equal
	 * width size bucket of Histogram.
	 * 
	 * @param max
	 * @param min
	 * @return list of partitions each is considered as bucket in histogram.
	 * @throws IOException
	 */
	private List<Bucket> creatHistogramBuckets(Point max, Point min)
			throws IOException {
		List<Bucket> result = new ArrayList<Bucket>();
		double lonBucketSize = Math.abs((max.getLon() - min.getLon()) / 100);
		double latBucketSize = Math.abs((max.getLat() - min.getLon()) / 100);
		Bucket part;
		// variables
		double prelat = min.getLat();
		double preLon = min.getLon();
		double maxlat;
		double maxlon;
		int inc = 0;
		for (int x = 0; x < 100; x++) {
			maxlat = prelat + latBucketSize;
			for (int y = 0; y < 100; y++) {
				maxlon = preLon + lonBucketSize;
				// new mbr
				MBR mbr = new MBR(new Point(maxlat, maxlon), new Point(prelat,
						preLon));
				part = new Bucket(mbr, inc++);
				result.add(part);
				// change the prelon to the next value.
				preLon = maxlon;
			}
			prelat = maxlat;
			preLon = min.getLon();
		}
		return result;
	}

	private void initHistogramo(queryLevel level) throws ParseException,
			IOException {
		if (level.equals(queryLevel.Day)) {
			readExactDayHistogram();
			initDayHistogram();
		} else if (level.equals(queryLevel.Week)) {
			readExactWeekHistogram();
			initWeekHistogram();
		} else {
			readExactMonthkHistogram();
			initMonthHistogram();
		}
	}

	private void writeHistogramToDisk(String fileName) throws IOException {
		OutputStreamWriter writer = new OutputStreamWriter(
				new FileOutputStream(System.getProperty("user.dir") + "/"
						+ fileName, false), "UTF-8");
		for (Bucket b : histogramBackets)
			writer.write(b.toString() + "\n");
		writer.close();
	}

	private void writeWKTHistogramToDisk(String fileName) throws IOException {
		OutputStreamWriter writer = new OutputStreamWriter(
				new FileOutputStream(System.getProperty("user.dir") + "/"
						+ fileName, false), "UTF-8");
		for (Bucket b : histogramBackets)
			writer.write(b.getId() + "\t" + b.getArea().toWKT() + "\n");
		writer.close();
	}

	private void initDayHistogram() throws ParseException, IOException {
		GridCell worldCell;
		MBR mbr;
		for (Bucket bucket : this.histogramBackets) {
			mbr = bucket.getArea();
			worldCell = new GridCell(mbr, this.lookup);
			// add the days in this mbr to the cell
			Iterator it = dayHistogram.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, HistogramCluster> obj = (Entry<String, HistogramCluster>) it
						.next();
				worldCell.add(obj.getKey(), obj.getValue().getCardinality(mbr));
			}
			// crate the cluster
			worldCell.initCluster(this.confidenceThreshold);
			// get the clusters created Then add in this bucket the days with
			// their mean only
			List<Cluster> cluster = worldCell.getCluster();
			for (Cluster c : cluster) {
				List<DayCardinality> temporalSegment = c.getDays();
				for (DayCardinality segment : temporalSegment) {
					// System.out.println("Set day cardinality in bucket"+segment.getDay()+"- mean "+c.getMean());
					bucket.setCardinality(segment.getDay(), c.getMean());
				}
			}
			System.out.println(cluster.size());

		}

	}

	private void ReadHistogramFromDisk(String fileName) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(new File(
				System.getProperty("user.dir") + "/" + fileName)));
		// OutputStreamWriter writer = new OutputStreamWriter(
		// new FileOutputStream(System.getProperty("user.dir") +
		// "/HistogramInit.WKT", false), "UTF-8");
		String line;
		Bucket temp;
		while ((line = reader.readLine()) != null) {
			temp = new Bucket();
			temp.parseFromText(line);
			histogramBackets.add(temp);
			// writer.write(temp.getId()+"\t"+temp.getArea().toWKT()+"\n");
		}
		reader.close();
		// writer.close();
	}
	
	private void ReadHistogramFromDiskExact(String fileName) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(new File(
				System.getProperty("user.dir") + "/" + fileName)));
		// OutputStreamWriter writer = new OutputStreamWriter(
		// new FileOutputStream(System.getProperty("user.dir") +
		// "/HistogramInit.WKT", false), "UTF-8");
		String line;
		Bucket temp;
		while ((line = reader.readLine()) != null) {
			temp = new Bucket();
			temp.parseFromText(line);
			histogramBacketsExact.add(temp);
			// writer.write(temp.getId()+"\t"+temp.getArea().toWKT()+"\n");
		}
		reader.close();
		// writer.close();
	}
	
	private void ReadHistogramFromDiskMVHistogram(String fileName) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(new File(
				System.getProperty("user.dir") + "/" + fileName)));
		// OutputStreamWriter writer = new OutputStreamWriter(
		// new FileOutputStream(System.getProperty("user.dir") +
		// "/HistogramInit.WKT", false), "UTF-8");
		String line;
		Bucket temp;
		while ((line = reader.readLine()) != null) {
			temp = new Bucket();
			temp.parseFromText(line);
			histogramBackets.add(temp);
			// writer.write(temp.getId()+"\t"+temp.getArea().toWKT()+"\n");
		}
		reader.close();
		// writer.close();
	}

	private void initWeekHistogram() throws ParseException {
		GridCell worldCell;
		MBR mbr;
		for (Bucket bucket : this.histogramBackets) {
			mbr = bucket.getArea();
			worldCell = new GridCell(mbr, this.lookup);
			// add the days in this mbr to the cell
			Iterator it = weekHistogram.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, HistogramCluster> obj = (Entry<String, HistogramCluster>) it
						.next();
				worldCell.add(obj.getKey(), obj.getValue().getCardinality(mbr));
			}
			// crate the cluster
			worldCell.initCluster(this.confidenceThreshold);
			// get the clusters created Then add in this bucket the days with
			// their mean only
			List<Cluster> cluster = worldCell.getCluster();
			for (Cluster c : cluster) {
				List<DayCardinality> temporalSegment = c.getDays();
				for (DayCardinality segment : temporalSegment) {
					// System.out.println("Set day cardinality in bucket"+segment.getDay()+"- mean "+c.getMean());
					bucket.setCardinality(segment.getDay(), c.getMean());
				}
			}
			System.out.println(cluster.size());

		}
	}

	private void initMonthHistogram() throws ParseException {
		GridCell worldCell;
		MBR mbr;
		for (Bucket bucket : this.histogramBackets) {
			mbr = bucket.getArea();
			worldCell = new GridCell(mbr, this.lookup);
			// add the days in this mbr to the cell
			Iterator it = monthHistogram.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, HistogramCluster> obj = (Entry<String, HistogramCluster>) it
						.next();
				worldCell.add(obj.getKey(), obj.getValue().getCardinality(mbr));
			}
			// crate the cluster
			worldCell.initCluster(this.confidenceThreshold);
			// get the clusters created Then add in this bucket the days with
			// their mean only
			List<Cluster> cluster = worldCell.getCluster();
			for (Cluster c : cluster) {
				List<DayCardinality> temporalSegment = c.getDays();
				for (DayCardinality segment : temporalSegment) {
					// System.out.println("Set day cardinality in bucket"+segment.getDay()+"- mean "+c.getMean());
					bucket.setCardinality(segment.getDay(), c.getMean());
				}
			}
			System.out.println(cluster.size());

		}
	}

	private void matchBuckets(queryLevel level) {
		System.out.println("Histogram size before: " + histogramBackets.size());
		for (int i = 0; i < histogramBackets.size(); i++) {
			while (intersect_Combine(histogramBackets.get(i))) {
				if (i > histogramBackets.size()) {
					i = histogramBackets.size() - 1;
				}
			}
		}
		System.out.println("Histogram size after: " + histogramBackets.size());
	}

	private boolean intersect_Combine(Bucket a) {
		List<Integer> bucketsIds = new ArrayList<Integer>();
		double maxlat = a.getArea().getMax().getLat();
		double maxlon = a.getArea().getMax().getLon();
		double minlat = a.getArea().getMin().getLat();
		double minlon = a.getArea().getMin().getLon();
		for (Bucket b : histogramBackets) {
			if (a.getArea().Intersect(b.getArea())) {
				if (a.getId() != b.getId()) {
					if (bucketMatched(a, b)) {
						// get the maximum Point
						maxlat = maxlat < b.getArea().getMax().getLat() ? b
								.getArea().getMax().getLat() : maxlat;
						maxlon = maxlon < b.getArea().getMax().getLon() ? b
								.getArea().getMax().getLon() : maxlon;
						// get the min point
						minlat = b.getArea().getMin().getLat() < minlat ? b
								.getArea().getMin().getLat() : minlat;
						minlon = b.getArea().getMin().getLon() < minlon ? b
								.getArea().getMin().getLon() : minlon;
						bucketsIds.add(b.getId());
					} else {
						return false;
					}
				}
			}
		}
		// delete old buckets and create the new one.
		if (bucketsIds.isEmpty())
			return false;
		bucketsIds.add(a.getId());
		Bucket combinedBucket = new Bucket(new MBR(new Point(maxlat, maxlon),
				new Point(minlat, minlon)), a.getId());
		combinedBucket.setDayCardinality(a.getDayCardinality());
		reconstructBuckets(bucketsIds);
		histogramBackets.add(combinedBucket);
		return true;
	}

	private void reconstructBuckets(List<Integer> bucketsIds) {
		List<Integer> index = new ArrayList<Integer>();
		// search
		for (Integer i : bucketsIds) {
			for (int pos = 0; pos < histogramBackets.size(); pos++) {
				if (histogramBackets.get(pos).getId() == i) {
					// System.out.println("delete "+
					// histogramBackets.get(pos).getId()+ " status"+
					// histogramBackets.remove(histogramBackets.get(pos)));
					histogramBackets.remove(histogramBackets.get(pos));
					index.add(pos);
					break;
				}
			}
		}

		// delete
		// for(Integer in : index){
		// histogramBackets.remove(in);
		// }
	}

	/**
	 * This method match the bucket if they have the same number of median
	 * cardinality then the are similar
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private boolean bucketMatched(Bucket a, Bucket b) {
		List<DayCardinality> a_card = a.getDayCardinality();
		List<DayCardinality> b_card = b.getDayCardinality();
		Collections.sort(a_card);
		Collections.sort(b_card);
		for (int i = 0; i < a_card.size(); i++) {
			if (a_card.get(i).getCardinality() != b_card.get(i)
					.getCardinality()) {
				return false;
			}
		}
		return true;
	}

	private int addNewCluster(String temporalSegment, queryLevel level) {
		System.out.println("Found cluster");
		int clusterId = 0;
		if (level.equals(queryLevel.Day)) {
			clusterId = dayClusters.size() + 1;
			dayClusters.add(new TemporalClusterTable(clusterId, initHistogram
					.get(temporalSegment)));
		} else if (level.equals(queryLevel.Week)) {
			clusterId = weekClusters.size() + 1;
			weekClusters.add(new TemporalClusterTable(clusterId, initHistogram
					.get(temporalSegment)));
		} else {
			clusterId = monthClusters.size() + 1;
			monthClusters.add(new TemporalClusterTable(clusterId, initHistogram
					.get(temporalSegment)));
		}
		return clusterId;
	}

	private void addToLookupTable(String temporal, int clusterId,
			queryLevel level) {
		if (level.equals(queryLevel.Day)) {
			dayLookup.add(new TemporalLookupTable(temporal, dayHistogram.get(
					temporal).getHistogramVolume(), clusterId));
		} else if (level.equals(queryLevel.Week)) {
			weekLookup.add(new TemporalLookupTable(temporal, weekHistogram.get(
					temporal).getHistogramVolume(), clusterId));
		} else {
			monthLookup.add(new TemporalLookupTable(temporal, monthHistogram
					.get(temporal).getHistogramVolume(), clusterId));
		}
	}

	public queryLevel getQueryPlan(String startDay, String endDay, MBR queryMBR)
			throws ParseException {
		queryLevel queryFrom = queryLevel.Day;
		double minPlan = Double.MAX_VALUE;
		for (queryLevel q : queryLevel.values()) {
			long plancost = this.getQueryCostExact(startDay, endDay, q,
					queryMBR);
			System.out.println("Histogram Statistics: \n" + q.toString()
					+ "\tCardinality Cost: " + plancost);
			if (plancost <= minPlan && plancost != -1) {
				minPlan = plancost;
				queryFrom = q;
			}
		}
		return queryFrom;

	}

	/**
	 * This method measure the storage and the accuracy of the histogram.
	 * 
	 * @param startDay
	 * @param endDay
	 * @param queryMBR
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public void StorageVsAccuracy(queryLevel level) throws ParseException,
			IOException {
		String start_date = "2000-01-01";
		String end_date = "3000-01-01";
		long sizeMVhistogram = 0, sizeExact = 0;
		List<Double> accuracy = new ArrayList<Double>();
		OutputStreamWriter writer = new OutputStreamWriter(
				new FileOutputStream(System.getProperty("user.dir") + "/"
						+ "storageVSaccuracy.csv", true), "UTF-8");
		writer.write("CI,Storage,Accuracy,ExactStorage\n");
		// Compute the Accuracy of the histogram.
		String[] fixed_point_list = { "-3.295467,47.704601999999994",
				"-0.590735,47.562259", "2.026395,47.893619",
				"2.400914,48.790124500000005",
				"2.8557474999999997,44.447964999999996",
				"3.1929321,45.2862325", "3.2034215,46.025104999999996",
				"4.3816565,45.4606697", "4.6679226,45.54895215",
				"9.60464035,48.71361715", "11.01279895,45.911180650000006",
				"18.17329695,48.56453115", "18.08580155,48.333007949999995",
				"22.09410785,49.99443585", "36.298819449999996,50.05773405",
				"55.43924685,-21.2713249", "54.9923851,51.1565279",
				"116.2924235,-8.3199363", "81.6726242,7.75050505",
				"76.840953,43.2144594" };
		for (int ci = 90; ci >=10; ci = ci - 10) {
			this.confidenceThreshold = (double) ci / (double) 100;
			OfflinePhase(level);
			reset();
			ReadHistogramFromDiskExact("xxxxWorld_Histogram"+level+".txt");
			// each bucket contains 4 float --- > MBR 32bits/float
			// list of integers represents days --- 32bit/integer
			System.out.println("Calculate statistics about the histogram");
			sizeExact = getHistogramSize(true);
			ReadHistogramFromDisk("xxxxWorld_Histogram_Cluster" + level
					+ "_CI(" + this.confidenceThreshold + ").txt");
			sizeMVhistogram = getHistogramSize(false);
			for (String point : fixed_point_list) {
				MBR temp = getQueryMBR(point, 0.0001);
				System.out.println(temp.toWKT());
				// readExactMonthkHistogram();
				// long exactCost = getQueryCostExact(start_date, end_date,
				// queryLevel.Month, temp);
				// System.out.println("Query Exact cardinality = "+ exactCost);
				// reset(queryLevel.Month);
				long exactCost = getEstimateCardinality(start_date, end_date,
						temp,level,true);
				System.out.println("Query Exact cardinality = " + exactCost);
				long estimateCost = getEstimateCardinality(start_date,
						end_date, temp,level,false);
				System.out.println("Query Estimate cardinality = "
						+ estimateCost);
				if (exactCost != 0) {
					accuracy.add(((double) estimateCost / (double) exactCost));
					System.out.println("The Accuracy of the histogram = "
							+ accuracy.get(accuracy.size() - 1));
				}
			}

			double aggregatedAccuracy = 0.0;
			for (double i : accuracy) {
				aggregatedAccuracy += i;
			}
			aggregatedAccuracy = (double) (aggregatedAccuracy / accuracy.size());
			System.out.println("The exact size = " + sizeExact
					+ "- The MV-size = " + sizeMVhistogram);
			System.out.println("The Accuracy of the histogram = "
					+ aggregatedAccuracy);
			
			writer.write(ci+","+sizeMVhistogram+","
			+aggregatedAccuracy+","+sizeExact+"\n");
			writer.flush();
			

		}
		writer.close();
	}

	private long getHistogramSize(boolean IsExact) {
		List<Bucket> temp = null;
		if(IsExact){
			temp = histogramBacketsExact;
		}else{
			temp = histogramBackets;//MV-histogram
		}
		
		long sizeInBits = (long) 0;
		for (Bucket bucket : temp) {
			// the mbr size in bits.
			sizeInBits += (32 * 4);
			// size of the temporal dimension (days, months, weeks).
			sizeInBits += (32 * 4 * bucket.getDayCardinality().size());

		}
		return (sizeInBits / 8);
	}

	/**
	 * This method return the mbr of a fixed points for used for test the
	 * accuracy of the histogram.
	 * 
	 * @param point
	 * @param area_ratio
	 * @return
	 */
	private MBR getQueryMBR(String point, double area_ratio) {
		String[] coordinate = point.split(",");
		double x = Double.parseDouble(coordinate[0]);
		double y = Double.parseDouble(coordinate[1]);
		int total_width = 360;
		int total_height = 180;
		double w = (double) (Math.sqrt(area_ratio) * total_width);
		double h = (double) Math.sqrt(area_ratio) * total_height;
		double max_x = x + w;
		double max_y = y + h;
		return new MBR(new Point(max_x, max_y), new Point(x, y));

	}

	/**
	 * This get the exact cardinality cost in the disk
	 * 
	 * @param startDay
	 * @param endDay
	 * @param q
	 * @param queryMBR
	 * @return
	 * @throws ParseException
	 */
	private long getQueryCostExact(String startDay, String endDay,
			queryLevel q, MBR queryMBR) throws ParseException {
		if (q.equals(queryLevel.Day)) {
			return this.getDayLevelCardinality(startDay, endDay, queryMBR);
		} else if (q.equals(queryLevel.Week)) {
			return this.getWeekLevelCardinality(startDay, endDay, queryMBR);
		} else if (q.equals(queryLevel.Month)) {
			return this.getMonthLevelCardinality(startDay, endDay, queryMBR);
		}
		return 0;
	}

	/**
	 * This get estimate cardinality from histogrambuckets
	 * 
	 * @param startDay
	 * @param endDay
	 * @param q
	 * @param queryMBR
	 * @return
	 * @throws ParseException
	 */
	private long getQueryCostEstimate(String startDay, String endDay,
			queryLevel q, MBR queryMBR) throws ParseException {
		if (q.equals(queryLevel.Day)) {
			return this.getDayLevelCardinality(startDay, endDay, queryMBR);
		} else if (q.equals(queryLevel.Week)) {
			return this.getWeekLevelCardinality(startDay, endDay, queryMBR);
		} else if (q.equals(queryLevel.Month)) {
			return this.getMonthLevelCardinality(startDay, endDay, queryMBR);
		}
		return 0;
	}

	private long getDayLevelCardinality(String startDay, String endDay,
			MBR queryMBR) throws ParseException {
		// clusterCalculator<clusterid,numberofdays>
		// temporalRange<day,directorypath>
		long result = -1;
		HashMap<Integer, Integer> clusterCalculator = new HashMap<Integer, Integer>();
		Map<String, String> temporalRange = this.lookup.getTweetsDayIndex(
				startDay, endDay);
		Iterator it = temporalRange.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> day = (Entry<String, String>) it.next();
			HistogramCluster histogram = dayHistogram.get(day.getKey());
			try {
				result += histogram.getCardinality(queryMBR);
			} catch (NullPointerException e) {

			}
		}
		return result;
	}

	private long getMonthLevelCardinality(String startDay, String endDay,
			MBR queryMBR) throws ParseException {
		// clusterCalculator<clusterid,numberofdays>
		// temporalRange<day,directorypath>
		long result = -1;
		Map<String, String> temporalRange = this.lookup.getTweetsMonthsIndex(
				startDay, endDay);
		Iterator it = temporalRange.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> m = (Entry<String, String>) it.next();
			HistogramCluster histogram = monthHistogram.get(m.getKey());
			try {
				result += histogram.getCardinality(queryMBR);
			} catch (NullPointerException e) {

			}
		}
		return result;
	}

	private long getEstimateCardinality(String startDay, String endDay,
			MBR queryMBR,queryLevel level,boolean IsExact) throws ParseException {
		
		List<Bucket> histogram = null; 
		if(IsExact){
			histogram = histogramBacketsExact;
		}else{
			histogram = histogramBackets;//MV-histogram
		}
		long result = 0;
		Map<String, String> temporalRange = null;
		if(level.equals(queryLevel.Day)){
			temporalRange = this.lookup.getTweetsDayIndex(
					startDay, endDay);
		}else if(level.equals(queryLevel.Week)){
			Map<Week, String> temporalRangeweek = this.lookup.getTweetsWeekIndex(
					startDay, endDay);
			Iterator it = temporalRangeweek.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<Week, String> week = (Entry<Week, String>) it.next();
				for (Bucket bucket : histogram) {
					if (queryMBR.Intersect(bucket.getArea())) {
						result += bucket.getCardinality(week.getValue());
					}
				}
			}
			return result;
		}else{
			temporalRange = this.lookup.getTweetsMonthsIndex(
					startDay, endDay);
		}
		
		Iterator it = temporalRange.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> m = (Entry<String, String>) it.next();
			for (Bucket bucket : histogram) {
				if (queryMBR.Intersect(bucket.getArea())) {
					result += bucket.getCardinality(m.getKey());
				}
			}

		}
		return result;
	}

	private long getWeekLevelCardinality(String startDay, String endDay,
			MBR queryMBR) throws ParseException {
		// clusterCalculator<clusterid,numberofdays>
		// temporalRange<day,directorypath>
		long result = -1;
		HashMap<Integer, Integer> clusterCalculator = new HashMap<Integer, Integer>();
		Map<Week, String> temporalRange = this.lookup.getTweetsWeekIndex(
				startDay, endDay);
		Iterator it = temporalRange.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Week, String> week = (Entry<Week, String>) it.next();
			HistogramCluster histogram = weekHistogram.get(week.getKey()
					.toString());
			try {
				result += histogram.getCardinality(queryMBR);
			} catch (NullPointerException e) {
				System.out.println(week.getValue());
			}
		}
		return result;
	}

	public static void main(String[] args) throws FileNotFoundException,
			IOException, ParseException {

		// MBR mbr = new MBR(new
		// Point(40.694961541009995,118.07045041992582),new
		// Point(38.98904106170265,114.92561399414794) );
		// 40.06978032458496 21.29929525830078, 40.06978032458496
		// 21.56846029736328, 39.55651280749512 21.56846029736328,
		// 39.55651280749512 21.29929525830078, 40.06978032458496
		// 21.29929525830078
		MBR mbr = new MBR(new Point(21.56846029736328, 40.06978032458496),
				new Point(21.29929525830078, 39.55651280749512));

		MemoryHistogram planner2 = new MemoryHistogram(false);

		planner2.StorageVsAccuracy(queryLevel.Month);
		// System.err.println("Done");
		// System.out.println("Num of day histogram: "+planner2.dayHistogram.size());
		// System.out.println("Num of week histogram: "+planner2.weekHistogram.size());
		// System.out.println("Num of month histogram: "+planner2.monthHistogram.size());
		// queryLevel result = planner2.getQueryPlan("2014-05-01", "2014-05-10",
		// mbr);
		// System.out.print(result);

	}

}
