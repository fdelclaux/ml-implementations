import java.util.ArrayList;

/*
 * CategoricalEstimator.java
 * Copyright (c) 2018 Mark Maloof.  All Rights Reserved.  See LICENSE.
 */

public class CategoricalEstimator extends Estimator{

  protected ArrayList<Integer> counts;

  public CategoricalEstimator()
  {
    super();
  }
  public CategoricalEstimator( Integer k )
  {
    counts = new ArrayList<Integer>(k);
    for(int i = 0; i < k;i++) 
    {
      counts.add(0);
    }
  } // number of categories

  public void add( Number x ) throws Exception
  {
    counts.set(x.intValue(), counts.get(x.intValue()) + 1);
    n++;
  }

  public Double getProbability( Number x )
  {
    Double probability;

    probability = (double)(counts.get(x.intValue()) + 1)/(n+counts.size());

    return probability;

    //add-one smoothing
  }

}