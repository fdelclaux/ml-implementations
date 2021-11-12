import java.util.ArrayList;

/*
 * Example.java
 * Copyright (c) 2018 Mark Maloof.  All Rights Reserved.  See LICENSE.
 */

public class Example extends ArrayList<Double>{

  protected double weight = 1.0;

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public Example()
  {

  }

  public Example( int n )
  {
    ensureCapacity(n);
  }

  public void setWeight(double w)
  {
    weight = w;
  }

  public double getWeight()
  {
    return weight;
  }
}
