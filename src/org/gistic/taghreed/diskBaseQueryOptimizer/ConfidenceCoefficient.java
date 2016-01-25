package org.gistic.taghreed.diskBaseQueryOptimizer;

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
			for (int i = 99; i > 0; i--) {
				confidenceLevel = i/100.0f;
				double critVal = calcMeanCI(stats,confidenceLevel);
				double maxValue = stats.getMax();
				double minValue = stats.getMin();
				double lowerBound = stats.getMean() - critVal;
				double upperBound = stats.getMean() + critVal;
				if(lowerBound <= minValue && upperBound >= maxValue){
					result = i/100.0f;
					 return result;
				}
			}
			 return result;
        } catch (MathIllegalArgumentException e) {
            return result;
        }
	}
	
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
            return critVal * stats.getStandardDeviation() / Math.sqrt(stats.getN());
        } catch (MathIllegalArgumentException e) {
            return Double.NaN;
        }
    }
	
	// The rest for Taghreed implementation
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
			for (int i = 99; i > 0; i--) {
				confidenceLevel = i/100.0f;
				double alpha = 1- confidenceLevel;
				alpha /= 2;
				double z  = 1 - alpha;
				double critVal = tDist
						.inverseCumulativeProbability(z);

				// Calculate confidence interval
				double se = stats.getStandardDeviation()
						/ Math.sqrt(stats.getN());

				critVal *= se;
				double maxValue = stats.getMax();
				double minValue = stats.getMin();
				double lowerBound = stats.getMean() - critVal;
				double upperBound = stats.getMean() + critVal;
				if(lowerBound <= minValue && upperBound >= maxValue){
					result = i/100.0f;
				}
			}
            return result;
        } catch (MathIllegalArgumentException e) {
            return result;
        }
	}
	
	public static double getMeanOfCluster(Cluster cluster){
		SummaryStatistics stats = new SummaryStatistics();
        for (DayCardinality val : cluster.getDays()) {
            stats.addValue(val.getCardinality());
        }
        return stats.getMean();
	}
	
	


}
