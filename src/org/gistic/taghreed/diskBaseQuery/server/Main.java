/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gistic.taghreed.diskBaseQuery.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.math3.util.MultidimensionalCounter.Iterator;
import org.gistic.taghreed.basicgeom.MBR;
import org.gistic.taghreed.basicgeom.Point;
import org.gistic.taghreed.collections.ActiveUsers;
import org.gistic.taghreed.collections.PopularHashtags;
import org.gistic.taghreed.collections.PopularUsers;
import org.gistic.taghreed.collections.Tweet;
import org.gistic.taghreed.collections.TweetVolumes;
import org.gistic.taghreed.diskBaseQuery.server.ServerRequest.queryIndex;
import org.gistic.taghreed.diskBaseQuery.server.ServerRequest.queryLevel;
import org.gistic.taghreed.diskBaseQuery.server.ServerRequest.queryType;
import org.gistic.taghreed.diskBaseQueryOptimizer.QueryPlanner2;
import org.gistic.taghreed.diskBaseQueryOptimizer.TraditionalMultiHistogram;

/**
 *
 * @author turtle
 * 
 *         To support reading rtree index do the following Fs.open stream Rtree
 *         r = new Rtree(); setstokOjbect(Shape of the Tweet "new Tweets) read
 *         Fileds(fs.open obj) search(Rectagnle , Collection<output> ) result
 *         will return in the output
 * 
 */
public class Main {
	static QueryPlanner2 queryPlan;
	static TraditionalMultiHistogram exactPlan;
	
	
	static OutputStreamWriter writer;

	public static void main(String[] args) throws FileNotFoundException,
			UnsupportedEncodingException, IOException, ParseException,
			InterruptedException {
		queryPlan = new QueryPlanner2();
		exactPlan = new TraditionalMultiHistogram();
		queryPlan.printArray();
		args = new String[2];
		args[0] = "2014-04-27";
		args[1] = "2014-04-27";
		TestCase1(args);
//		for (int month = 1; month < 11; month++) {
//			String fileString = System.getProperty("user.dir")
//					+ "/MRHistogram-"+month+"-2014.log";
//			writer = new OutputStreamWriter(new FileOutputStream(fileString,
//					false), "UTF-8");
//			args = new String[2];
//			String monthDate;
//			if(month < 10){
//				monthDate = "0"+month;
//			}else{
//				monthDate = Integer.toString(month);
//			}
//			args[0] = "2014-"+monthDate+"-01";
//			for (int i = 1; i < 31; i++) {
//				if (i < 10) {
//
//					args[1] = "2014-"+monthDate+"-0" + i;
//				} else {
//					args[1] = "2014-"+monthDate+"-" + i;
//				}
//
//				TestCase1(args);
//			}
//			writer.close();
//		}
		
		
//		experments();
//		String result1,result2;
//		long startTime, endTime;
//		String text = "BIG NEWS from CS&E! We are now accepting applications for the Data Science MS program. http://datascience.umn.edu/admissions  #UMN #CSE #BigData;";
//		String text = "Pandora is on it yet again #awwwyeah";
//		startTime = System.currentTimeMillis();
//		result1 = getHashtag1(text);
//		endTime = System.currentTimeMillis();
//		System.out.println("1 execution time:"+(endTime-startTime)+" result:"+result1);
//		
//		startTime = System.currentTimeMillis();
//		result1 = getHashtag2(text);
//		endTime = System.currentTimeMillis();
//		System.out.println("2 execution time:"+(endTime-startTime)+" result:"+result1);
		

	}
	
	private static void experments() throws IOException, ParseException{
		long startTime, endTime,queryExec_time;
		startTime = System.currentTimeMillis();
		Map<String, Integer> activeUsers = new HashMap<String, Integer>(100000000);
		BufferedReader reader = new BufferedReader(new  FileReader(new File(System.getProperty("user.dir")+ "/_tweets_" + ".txt")));
		String line;
		int count =0;
		Tweet tweet;
		while((line = reader.readLine()) != null){
//			tweet = new Tweet(line);
//			if(tweet.screenName == null){
//				System.out.println("Null pointer");
//			}
//			if(activeUsers.containsKey(tweet.screenName)){
//				count = activeUsers.get(tweet.screenName);
//				activeUsers.put(tweet.screenName, (count+1));
//			}else{
//				activeUsers.put(tweet.screenName, 1);
//			}
		}
		endTime = System.currentTimeMillis();
		queryExec_time = endTime - startTime;
//		List<ActiveUsers> list = new ArrayList<ActiveUsers>();
//		java.util.Iterator it = activeUsers.entrySet().iterator();
//		while(it.hasNext()){
//			Map.Entry<String,Integer> obj = (Entry<String, Integer>) it.next();
//			list.add(new ActiveUsers(obj.getKey(), obj.getValue()));
//		}
//		Collections.sort(list);
//		System.out.println("number of tweets: "+activeUsers.size());
//		System.out.println("fist of the list:"+ list.get(0));
//		System.out.println("last in the list:"+ list.get(list.size()-1));
		System.out.println("Execution time: "+queryExec_time+" ms");
	}
	
	private static String getHashtag1(String tweet_text){
		String result = "";
		String[] token = tweet_text.split(" ");
		for (int i = 0; i < token.length; i++) {
			// Match the hashtags with the regular expression
			if (token[i].matches("^#[\\p{L}\\p{N}\\p{M}]+")) {
			    result += token[i];
			}
		}
		return result;
	}
	
	private static String getHashtag2(String tweet_text){
		List<String> hashtags = new ArrayList<String>();
		String temp = "";
		boolean flag = false;
		if (tweet_text.contains("#")) {
			for(int index=0 ; index< tweet_text.length(); index++){
				if(tweet_text.charAt(index) == '#' && flag == false){
					flag = true; 
				}
				if(tweet_text.charAt(index) != ' ' && (index+1) != tweet_text.length()){
					if(flag){
						temp += tweet_text.charAt(index);
					}
				}else if(flag){
					temp += tweet_text.charAt(index);
					hashtags.add(temp.replace(" ", ""));
					temp ="";
					flag = false;
				}
			}
		}
			 
			String result = "";
			// iterate the list of hashtags 
			for(int i =0 ; i < hashtags.size(); i++)
				result += hashtags.get(i);
			return result;
	}

	private static void TestCase1(String[] args) throws ParseException, FileNotFoundException, IOException, InterruptedException {
		List<PopularHashtags> popularHashtags = new ArrayList<PopularHashtags>();
		List<Tweet> tweets = new ArrayList<Tweet>();
		ServerRequest req = new ServerRequest();
		req.setType(queryType.tweet);
		req.setIndex(queryIndex.rtree);
		//MBR mbr = new MBR(new Point(90, 180),new Point(-90, -180));
		double maxlon = -93.18933596240234;
		double minlat = 44.94941027490235;
		double maxlat = 45.01670153466797;
		double minlon = -93.3176528416748;
		MBR mbr = new MBR(new Point(maxlat, maxlon), new Point(minlat, minlon));
	
		req.setMBR(mbr);
		req.setStartDate(args[0]);
		req.setEndDate(args[1]);
		// Test
		long startTime, endTime, queryEst_Time, queryExec_time, exactQueryExec_time, exactQueryEst_Time;
		// Histogram estimation
		
		// Get the queryPlan
		queryLevel queryEstimated;
		queryLevel exactquery;
		startTime = System.currentTimeMillis();
		queryEstimated = queryPlan.getQueryPlan(req.getStartDate(),
				req.getEndDate(), req.getMbr());
		endTime = System.currentTimeMillis();
		queryEst_Time = endTime - startTime;
		// get the exact query plan 
		startTime = System.currentTimeMillis();
		 exactquery = exactPlan.getQueryPlan(req.getStartDate(), req.getEndDate(), req.getMbr());
		endTime = System.currentTimeMillis();
		exactQueryEst_Time = endTime - startTime;
		// Exact histogrtam execution time 
		req.setQueryResolution(exactquery);
		startTime = System.currentTimeMillis();
		req.getTweetsRtreeDays();
		endTime = System.currentTimeMillis();
		exactQueryExec_time = endTime - startTime;
		//execute the query 
				req.setQueryResolution(queryEstimated);
				startTime = System.currentTimeMillis();
				req.getTweetsRtreeDays();
				endTime = System.currentTimeMillis();
				queryExec_time = endTime - startTime;
		
		List<PopularUsers> listp = req.getPopularUsers();
		System.out.println("******\n"+"Number of popular users:"+ listp.size()+"\n*****");
		System.out.println("first of the list = "+ listp.get(0));
		System.out.println("last of the list = "+ listp.get(listp.size()-1));
//		for(PopularUsers user : listp){
//			System.out.println("User:"+ user.screenName+" follower:"+ user.followersCount);
//		}
		
		List<PopularHashtags> listh = req.getHashtags();
		System.out.println("******\n"+"Number of Hashtags:"+ listh.size()+"\n*****");
		System.out.println("first of the list = "+ listh.get(0));
		System.out.println("last of the list = "+ listh.get(listh.size()-1));
		
		for(PopularHashtags tag : listh){
			if(tag.hashtagCount >1 )
				System.out.println("hashtag:"+ tag.hashtagName+" count:"+ tag.hashtagCount);
		}
		
		
		List<TweetVolumes> daysVolume = req.getDayVolumes();
		System.out.println("******\n"+"Volume day:"+ daysVolume.size()+"\n*****");
//		for(TweetVolumes day : daysVolume)
//			System.out.println("day: "+day.dayName + "- volume:"+day.volume);
		
		System.out.println("******\n"+"# Tweets:"+ req.getTopKtweets().size()+"\n*****");
		
		System.out.println("*****Over all ******");
		//Print statistics result:
		
		int comapre = exactquery.equals(queryEstimated) ? 1 : 0;
	    
		String temp = req.getStartDate()+"\t"+req.getEndDate()+"\tMRHistogram\t" + queryEstimated + "\tEstimated Time\t"+ queryEst_Time+ "\tEexution Time\t" + queryExec_time
				+"\tExact Histogram\t" + exactquery + "\tEstimated Time\t"+ exactQueryEst_Time + "\tEexution Time\t"+ exactQueryExec_time+"\t"+comapre+"\n";
		System.out.println(temp);
		writer.write(temp);
		
	}

	

	

}
