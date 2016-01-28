package org.umn.visualization;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

import org.apache.hadoop.io.Text;




public class PointQ implements Serializable,Comparable<PointQ>{
	public double x;
	public double y;

	public PointQ () {
		this(0, 0);
	}
	
	public PointQ (double x, double y) {
	  set(x, y);
	}
	

	/**
	 * A copy constructor from any shape of type Point (or subclass of Point)
	 * @param s
	 */
	public PointQ (PointQ s) {
	  this.x = s.x;
	  this.y = s.y;
  }

  public void set(double x, double y) {
		this.x = x;
		this.y = y;
	}

	
	public boolean equals(Object obj) {
		PointQ r2 = (PointQ) obj;
		return this.x == r2.x && this.y == r2.y;
	}
	
	public double distanceTo(PointQ s) {
		double dx = s.x - this.x;
		double dy = s.y - this.y;
		return Math.sqrt(dx*dx+dy*dy);
	}
	
	@Override
	public PointQ clone() {
	  return new PointQ(this.x, this.y);
	}

	/**
	 * Returns the minimal bounding rectangle of this point. This method returns
	 * the smallest rectangle that contains this point. For consistency with
	 * other methods such as {@link Rectangle#isIntersected(Shape)}, the rectangle
	 * cannot have a zero width or height. Thus, we use the method
	 * {@link Math#ulp(double)} to compute the smallest non-zero rectangle that
	 * contains this point. In other words, for a point <code>p</code> the
	 * following statement should return true.
	 * <code>p.getMBR().isIntersected(p);</code>
	 */
  
  public RectangleQ getMBR() {
    return new RectangleQ(x, y, x + Math.ulp(x), y + Math.ulp(y));
  }

  
  public double distanceTo(double px, double py) {
    double dx = x - px;
    double dy = y - py;
    return Math.sqrt(dx * dx + dy * dy);
  }



  public boolean isIntersected(RectangleQ s) {
    return getMBR().isIntersected(s);
  }
  
  @Override
  public String toString() {
    return "Point: ("+x+","+y+")";
  }
  

  @Override
  public int compareTo(PointQ  o) {
    if (x < o.x)
      return -1;
    if (x > o.x)
      return 1;
    if (y < o.y)
      return -1;
    if (y > o.y)
      return 1;
    return 0;
  }

 
}
