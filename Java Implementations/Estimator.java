/*
 * Estimator.java
 * Copyright (c) 2018 Mark Maloof.  All Rights Reserved.  See LICENSE.
 */

public abstract class Estimator extends Object{

  protected int n = 0; // number of samples
  
  public Estimator()
  {
    
  }
  abstract public void add( Number x ) throws Exception;

  public Integer getN()
  {
    return n;
  }

  abstract public Double getProbability( Number x );

}
