package org.umn.visualization;

import java.awt.Graphics;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;


public class RectangleQ implements Serializable, Comparable<RectangleQ>{

	  public double x1;
	  public double y1;
	  public double x2;
	  public double y2;

	  public RectangleQ() {
	    this(0, 0, 0, 0);
	  }

	  /**
	   * Constructs a new <code>Rectangle</code>, initialized to match 
	   * the values of the specified <code>Rectangle</code>.
	   * @param r  the <code>Rectangle</code> from which to copy initial values
	   *           to a newly constructed <code>Rectangle</code>
	   * @since 1.1
	   */
	  public RectangleQ(RectangleQ r) {
	    this(r.x1, r.y1, r.x2, r.y2);
	  }

	  public RectangleQ(double x1, double y1, double x2, double y2) {
	    this.set(x1, y1, x2, y2);
	  }
	  
	  public void set(RectangleQ s) {
		  RectangleQ mbr = s.getMBR();
	    set(mbr.x1, mbr.y1, mbr.x2, mbr.y2);
	  }
	  
	  public void set(double x1, double y1, double x2, double y2) {
	    this.x1 = x1;
	    this.y1 = y1;
	    this.x2 = x2;
	    this.y2 = y2;
	  }

	  public void write(DataOutput out) throws IOException {
	    out.writeDouble(x1);
	    out.writeDouble(y1);
	    out.writeDouble(x2);
	    out.writeDouble(y2);
	  }

	  public void readFields(DataInput in) throws IOException {
	    this.x1 = in.readDouble();
	    this.y1 = in.readDouble();
	    this.x2 = in.readDouble();
	    this.y2 = in.readDouble();
	  }
	  
	  /**
	   * Comparison is done by lexicographic ordering of attributes
	   * < x1, y1, x2, y2>
	   */
	  public int compareTo(RectangleQ s) {
		  RectangleQ rect2 = (RectangleQ) s;
	    // Sort by x1 then y1
	    if (this.x1 < rect2.x1)
	      return -1;
	    if (this.x1 > rect2.x1)
	      return 1;
	    if (this.y1 < rect2.y1)
	      return -1;
	    if (this.y1 > rect2.y1)
	      return 1;

	    // Sort by x2 then y2
	    if (this.x2 < rect2.x2)
	      return -1;
	    if (this.x2 > rect2.x2)
	      return 1;
	    if (this.y2 < rect2.y2)
	      return -1;
	    if (this.y2 > rect2.y2)
	      return 1;
	    return 0;
	  }

	  public boolean equals(Object obj) {
		  RectangleQ r2 = (RectangleQ) obj;
	    boolean result = this.x1 == r2.x1 && this.y1 == r2.y1
	        && this.x2 == r2.x2 && this.y2 == r2.y2;
	    return result;
	  }


	  public double distanceTo(double px, double py) {
	    return this.getMaxDistanceTo(px, py);
	  }

	  /**
	   * Maximum distance to the perimeter of the Rectangle
	   * @param px
	   * @param py
	   * @return
	   */
	  public double getMaxDistanceTo(double px, double py) {
	    double dx = Math.max(px - this.x1, this.x2 - px);
	    double dy = Math.max(py - this.y1, this.y2 - py);

	    return Math.sqrt(dx*dx+dy*dy);
	  }

	  public double getMinDistanceTo(double px, double py) {
	    if (this.contains(px, py))
	      return 0;
	    
	    double dx = Math.min(Math.abs(px - this.x1), Math.abs(this.x2 - px));
	    double dy = Math.min(Math.abs(py - this.y1), Math.abs(this.y2 - py));

	    if ((px < this.x1 || px > this.x2) &&
	        (py < this.y1 || py > this.y2)) {
	      return Math.sqrt(dx * dx + dy * dy);
	    }
	    
	    return Math.min(dx, dy);
	  }
	  
	  public double getMinDistance(RectangleQ r2) {
	    // dx is the horizontal gap between the two rectangles. If their x ranges
	    // overlap, dx is zero
	    double dx = 0;
	    if (r2.x1 > this.x2)
	      dx = r2.x1 - this.x2;
	    else if (this.x1 > r2.x2)
	      dx = this.x1 - r2.x2;

	    double dy = 0;
	    if (r2.y1 > this.y2)
	      dy = r2.y1 - this.y2;
	    else if (this.y1 > r2.y2)
	      dy = this.y1 - r2.y2;

	    // Case 1: Overlapping rectangles
	    if (dx == 0 && dy == 0)
	      return 0;
	    
	    // Case 2: Overlapping in one dimension only
	    if (dx == 0 || dy == 0)
	      return dx + dy;
	    
	    // Case 3: Not overlapping in any dimension
	    return Math.sqrt(dx * dx + dy * dy);
	  }
	  
	  public double getMaxDistance(RectangleQ r2) {
	    double xmin = Math.min(this.x1, r2.x1);
	    double xmax = Math.max(this.x2, r2.x2);
	    double ymin = Math.min(this.y1, r2.y1);
	    double ymax = Math.max(this.y2, r2.y2);
	    double dx = xmax - xmin;
	    double dy = ymax - ymin;
	    return Math.sqrt(dx * dx + dy * dy);
	  }

	  @Override
	  public RectangleQ clone() {
	    return new RectangleQ(this);
	  }
	  

	  public RectangleQ getMBR() {
	    return new RectangleQ(this);
	  }
	  
	  public boolean isIntersected(RectangleQ s) {
	    RectangleQ r = s.getMBR();
	    if (r == null)
	      return false;
	    return (this.x2 > r.x1 && r.x2 > this.x1 && this.y2 > r.y1 && r.y2 > this.y1);
	  }

	  public RectangleQ getIntersection(RectangleQ s) {
	    if (!s.isIntersected(this))
	      return null;
	    RectangleQ r = s.getMBR();
	    double ix1 = Math.max(this.x1, r.x1);
	    double ix2 = Math.min(this.x2, r.x2);
	    double iy1 = Math.max(this.y1, r.y1);
	    double iy2 = Math.min(this.y2, r.y2);
	    return new RectangleQ(ix1, iy1, ix2, iy2);
	  }

	  public boolean contains(PointQ p) {
	    return contains(p.x, p.y);
	  }

	  public boolean contains(double x, double y) {
	    return x >= x1 && x < x2 && y >= y1 && y < y2;
	  }

	  public boolean contains(RectangleQ r) {
	    return contains(r.x1, r.y1, r.x2, r.y2);
	  }

	  public RectangleQ union(final RectangleQ s) {
		  RectangleQ r = s.getMBR();
	    double ux1 = Math.min(x1, r.x1);
	    double ux2 = Math.max(x2, r.x2);
	    double uy1 = Math.min(y1, r.y1);
	    double uy2 = Math.max(y2, r.y2);
	    return new RectangleQ(ux1, uy1, ux2, uy2);
	  }
	  
	  public void expand(final RectangleQ s) {
		  RectangleQ r = s.getMBR();
	    if (r.x1 < this.x1)
	      this.x1 = r.x1;
	    if (r.x2 > this.x2)
	      this.x2 = r.x2;
	    if (r.y1 < this.y1)
	      this.y1 = r.y1;
	    if (r.y2 > this.y2)
	      this.y2 = r.y2;
	  }
	  
	  public boolean contains(double rx1, double ry1, double rx2, double ry2) {
	    return rx1 >= x1 && rx2 <= x2 && ry1 >= y1 && ry2 <= y2;
	  }
	  
	  public PointQ getCenterPoint() {
	    return new PointQ((x1 + x2) / 2, (y1 + y2)/2);
	  }


	  @Override
	  public String toString() {
	    return "Rectangle: ("+x1+","+y1+")-("+x2+","+y2+")";
	  }

	  public boolean isValid() {
	    return !Double.isNaN(x1);
	  }
	  
	  public void invalidate() {
	    this.x1 = Double.NaN;
	  }

	  public double getHeight() {
	    return y2 - y1;
	  }

	  public double getWidth() {
	    return x2 - x1;
	  }

	  
	  
	  public int compareTo1(RectangleQ r2) {
	    if (this.x1 < r2.x1)
	      return -1;
	    if (this.x1 > r2.x1)
	      return 1;
	    if (this.y1 < r2.y1)
	      return -1;
	    if (this.y1 > r2.y1)
	      return 1;

	    if (this.x2 < r2.x2)
	      return -1;
	    if (this.x2 > r2.x2)
	      return 1;
	    if (this.y2 < r2.y2)
	      return -1;
	    if (this.y2 > r2.y2)
	      return 1;
	    
	    return 0;
	  }
	  
	  
	  public String toWKT() {
	    return String.format("POLYGON((%g %g, %g %g, %g %g, %g %g, %g %g))", this.x1, this.y1,
	        this.x1, this.y2,
	        this.x2, this.y2,
	        this.x2, this.y1,
	        this.x1, this.y1);
	  }
	
}
