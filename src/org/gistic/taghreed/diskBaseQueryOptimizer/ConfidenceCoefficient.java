package org.gistic.taghreed.diskBaseQueryOptimizer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;



public class ConfidenceCoefficient {
	private static double  confidencecoefficient;
	private static double confidenceLevel;
	private static int sampleSize;
	
	
	public static double calcMeanCI(List<Integer> list,int Confidence) {
	    confidenceLevel = Confidence/100.0f;
		sampleSize = list.size();
		 SummaryStatistics stats = new SummaryStatistics();
	        for (Integer val : list) {
	            stats.addValue(val);
	        }
		
        try {
            // Create T Distribution with N-1 degrees of freedom
            TDistribution tDist = new TDistribution(stats.getN() - 1);
            // Calculate critical value
            double critVal = tDist.inverseCumulativeProbability(1.0 - (1 - confidenceLevel) / 2);
            // Calculate confidence interval
            double se = stats.getStandardDeviation() / Math.sqrt(stats.getN());
            
            critVal *= se;
            return critVal;
        } catch (MathIllegalArgumentException e) {
            return Double.NaN;
        }
    }
	
//	public static double getConfidence(List<Long> list){
//		double result =0 ;
//		double confidenceLevel =0;
//		sampleSize = list.size();
//		 SummaryStatistics stats = new SummaryStatistics();
//	        for (Long val : list) {
//	            stats.addValue(val);
//	        }
//	        if(stats.getN() == 1)
//	        	return 1.0;
//		
//        try {
//            // Create T Distribution with N-1 degrees of freedom
//            TDistribution tDist = new TDistribution(stats.getN() - 1);
//            // Calculate critical value
//			for (int i = 99; i > 0; i--) {
//				confidenceLevel = i/100.0f;
//				double alpha = 1- confidenceLevel;
//				alpha /= 2;
//				double z  = 1 - alpha;
//				double critVal = tDist
//						.inverseCumulativeProbability(z);
//
//				// Calculate confidence interval
//				double se = stats.getStandardDeviation()
//						/ Math.sqrt(stats.getN());
//
//				critVal *= se;
//				double maxValue = stats.getMax();
//				double minValue = stats.getMin();
//				double lowerBound = stats.getMean() - critVal;
//				double upperBound = stats.getMean() + critVal;
//				if(lowerBound <= minValue && upperBound >= maxValue){
//					result = i/100.0f;
//					 return result;
//				}
//			}
//			 return result;
//        } catch (MathIllegalArgumentException e) {
//            return result;
//        }
//	}
	
	
	public static double getConfidence(List<Long> list){
		double result =0 ;
		double confidenceLevel =0;
		sampleSize = list.size();
		 SummaryStatistics stats = new SummaryStatistics();
	        for (Long val : list) {
	            stats.addValue(val);
	        }
	        if(stats.getN() == 1)
	        	return 1.0;
	        
        try {
            // Create T Distribution with N-1 degrees of freedom
            TDistribution tDist = new TDistribution(stats.getN() - 1);
            // Calculate critical value
            double se = stats.getStandardDeviation() / Math.sqrt(stats.getN());
            double sd = stats.getStandardDeviation();
            System.out.println("sd: "+sd);
            System.out.println("SE: "+se);
            System.out.println("mean: "+stats.getMean());
            System.out.println("max: "+stats.getMax());
            System.out.println("min: "+stats.getMin());
            int variance =0;
            int max = 0; 
            for(long element : list){
            	if((int)element< (int)stats.getMean()){
            		variance = (int) (stats.getMean()- element);
            	}else{
            		variance = (int) (element - stats.getMean());
            	}
            	if(variance > max){
            		max = variance;
            	}
            }
            System.out.println("variance confi: "+(100f-(variance/stats.getMean())));
//            confidenceLevel = (double)100 - se;
            confidenceLevel = 100f-(max/stats.getMean());
            System.out.println("confidnece: "+confidenceLevel);
            return confidenceLevel;
          
        } catch (MathIllegalArgumentException e) {
            return result;
        }
	}
	
	// Gives some values but not accurate - 
//	public static double getConfidence(List<Long> list){
//		double confidenceLevel =0;
//		sampleSize = list.size();
//		 SummaryStatistics stats = new SummaryStatistics();
//	        for (Long val : list) {
//	            stats.addValue(val);
//	        }
//	        if(stats.getN() == 1)
//	        	return 1.0;
//	        
//	        try {
//	            // Create T Distribution with N-1 degrees of freedom
//	            TDistribution tDist = new TDistribution(stats.getN() - 1);
//	            // Calculate critical value
//	            int critVal = (int) Math.ceil(calcMeanCI(stats,0.99));
//				for (int i = 95; i > 0; i=i-5) {
//					critVal += (int) Math.ceil(calcMeanCI(stats,0.99));;
//					int maxValue = (int) stats.getMax();
//					int minValue = (int) stats.getMin();
//					int lowerBound = (int) (stats.getMean() - critVal);
//					int upperBound = (int) (stats.getMean() + critVal);
//					if(lowerBound <= minValue && upperBound >= maxValue){
//						confidenceLevel = (double)i/(double)100.0f;
//						 return confidenceLevel;
//					}
//				}
//				 return confidenceLevel;
//	        } catch (MathIllegalArgumentException e) {
//	            return confidenceLevel;
//	        }
//	}
	
	public static double getMean(List<Long> list){
		SummaryStatistics stats = new SummaryStatistics();
        for (Long val : list) {
            stats.addValue(val);
        }
        return stats.getMean();
	}
	
	
	
	

    private static double calcMeanCI(SummaryStatistics stats, double level) {
        try {
            // Create T Distribution with N-1 degrees of freedom
            TDistribution tDist = new TDistribution(stats.getN() - 1);
            // Calculate critical value
            double critVal = tDist.inverseCumulativeProbability(1.0 - (1 - level) / 2);
            // Calculate confidence interval
//            System.out.println("------- "+level+" -------");
//            System.out.println("CI = "+critVal * stats.getStandardDeviation() / Math.sqrt(stats.getN()));
//            System.out.println("SE = "+ stats.getStandardDeviation() / Math.sqrt(stats.getN()));
//            System.out.println("SD = "+ stats.getStandardDeviation());
//            System.out.println("Mean = "+ stats.getMean());
            return critVal * stats.getStandardDeviation() / Math.sqrt(stats.getN());
        } catch (MathIllegalArgumentException e) {
            return Double.NaN;
        }
    }
    
    
    private static double CalculateStandardError(SummaryStatistics stats, double level) {
        try {
            // Create T Distribution with N-1 degrees of freedom
            TDistribution tDist = new TDistribution(stats.getN() - 1);
            // Calculate critical value
            double critVal = tDist.inverseCumulativeProbability(1.0 - (1 - level) / 2);
            // Calculate confidence interval
//            System.out.println("------- "+level+" -------");
//            System.out.println("CI = "+critVal * stats.getStandardDeviation() / Math.sqrt(stats.getN()));
//            System.out.println("SE = "+ stats.getStandardDeviation() / Math.sqrt(stats.getN()));
//            System.out.println("SD = "+ stats.getStandardDeviation());
//            System.out.println("Mean = "+ stats.getMean());
            return 100 * stats.getStandardDeviation() / Math.sqrt(stats.getN());
        } catch (MathIllegalArgumentException e) {
            return Double.NaN;
        }
    }
    
    //for testing standard deviation 
	public static double getConfidenceOfCluster(Cluster cluster){
	double result =0 ;
	double confidenceLevel =0;
	
	 SummaryStatistics stats = new SummaryStatistics();
        for (DayCardinality val : cluster.getDays()) {
            stats.addValue(val.getCardinality());
        }
        if(stats.getN() == 1)
        	return 1.0;
	
        try {
            // Create T Distribution with N-1 degrees of freedom
            TDistribution tDist = new TDistribution(stats.getN() - 1);
            // Calculate critical value
            double se = stats.getStandardDeviation() / Math.sqrt(stats.getN());
            double sd = stats.getStandardDeviation();
//            System.out.println("sd: "+sd);
//            System.out.println("SE: "+se);
//            System.out.println("mean: "+stats.getMean());
//            System.out.println("max: "+stats.getMax());
//            System.out.println("min: "+stats.getMin());
            confidenceLevel = (double)100 - se;
//            System.out.println("confidnece: "+confidenceLevel);
            return confidenceLevel;
          
        } catch (MathIllegalArgumentException e) {
            return result;
        }
}
    
	
	// The rest for Taghreed implementation
//	public static double getConfidenceOfCluster(Cluster cluster){
//		double result =0 ;
//		double confidenceLevel =0;
//		
//		 SummaryStatistics stats = new SummaryStatistics();
//	        for (DayCardinality val : cluster.getDays()) {
//	            stats.addValue(val.getCardinality());
//	        }
//	        if(stats.getN() == 1)
//	        	return 1.0;
//		
//	        try {
//	            // Create T Distribution with N-1 degrees of freedom
//	            TDistribution tDist = new TDistribution(stats.getN() - 1);
//	            // Calculate critical value
//	            int critVal99 = (int) Math.ceil(calcMeanCI(stats,0.99));
//				for (int i = 98; i > 0; i--) {
//					confidenceLevel = i/100.0f;
//					int critVal = (int) Math.ceil(calcMeanCI(stats,confidenceLevel));
//					critVal += critVal99;
//					int maxValue = (int) stats.getMax();
//					int minValue = (int) stats.getMin();
//					int lowerBound = (int) (stats.getMean() - critVal);
//					int upperBound = (int) (stats.getMean() + critVal);
//					if(lowerBound <= minValue && upperBound >= maxValue){
//						result = i/100.0f;
//						 return result;
//					}
//				}
//				 return result;
//	        } catch (MathIllegalArgumentException e) {
//	            return result;
//	        }
//	}
	
	public static double getMeanOfCluster(Cluster cluster){
		SummaryStatistics stats = new SummaryStatistics();
        for (DayCardinality val : cluster.getDays()) {
            stats.addValue(val.getCardinality());
        }
        return Math.ceil(stats.getMean());
	}
	
	public static void main(String args[]){
		System.out.println("Calculate hello ");
		List<Long> testlist = new ArrayList<Long>();
		testlist.add((long) 10);
		testlist.add((long) 3330);
		testlist.add((long) 10);
		testlist.add((long) 10);
		getConfidence(testlist);
		
	}


}
