/*
 * Performance.java
 * Copyright (c) 2018 Mark Maloof.  All Rights Reserved.  See LICENSE.
 */

public class Performance extends Object{

  private Attributes attributes;
  private int[][] confusionMatrix; // optional
  private int corrects = 0;
  private double sum = 0.0;
  private double sumSqr = 0.0;
  private int c;                // number of classes
  private int n = 0;            // number of predictions
  private int m = 0;            // number of additions

  public Performance( Attributes attributes ) throws Exception
  {
    this.attributes = attributes;
    c = attributes.get(attributes.getClassIndex()).size();
  }

  public void add( double actual, double prediction )
  {
    n++;
    if(actual == prediction)
    {
      corrects++;
    }

  }

  public void add( Performance p ) throws Exception
  {
    m++;
    sum += p.getAccuracy();
    sumSqr += p.getAccuracy()*p.getAccuracy(); 
    n += p.n;

  }

  public double getAccuracy()
  {
    if(m > 0)
      return sum/m;
    else
      return (double) corrects/n;
  }

  public double getSDAcc()
  {
    double variance = sumSqr - ((sum*sum)/n);
    variance /= n-1;
    return Math.sqrt(variance);
  }

  public String toString()
  {
    StringBuilder s = new StringBuilder();
    s.append("Accuracy: " + this.getAccuracy());
    s.append(" +- " + getSDAcc());

    return s.toString();
  }

}
