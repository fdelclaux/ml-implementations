import java.io.Serializable;
import java.lang.Math;

/*
 * IBk.java
 * Copyright (c) 2018 Mark Maloof.  All Rights Reserved.  See LICENSE.
 */



public class IBk extends Classifier implements Serializable, OptionHandler{

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  protected DataSet dataset;
  protected int k = 3;

  //variables for heap
  private double[] distances;
  private double[] examples;
  private int size = 0;
  boolean inOrder;

  public IBk()
  {
    inOrder = true; 
    distances = new double[k+1];
    examples = new double[k+1];
    distances[size] = -1.0;
    examples[size] = -1.0;
  }


  // //functions for heap
  // public void percolateDown(int hole)
  // {
  //   Double tempDist = distances[hole];
  //   Double tempEx = examples[hole];
    
  //   while(hole <= size/2)
  //   {
  //     int child = hole*2;
  //     if(child != size && distances[child+1] < distances[child])
  //       child++;
  //     if(distances[child] < tempDist)
  //     {
  //       distances[hole] = distances[child];
  //       examples[hole] = examples[child];
  //       hole = child;
  //     }
  //     else{
  //       break;
  //     }

  //     distances[hole] = tempDist;
  //     examples[hole] = tempEx;
  //   }


  // }

  // public void build()
  // {
  //   int i = size/2;
  //   while(i>0)
  //   {
  //     percolateDown(i);
  //     i--;
  //   }
  //   inOrder = true;

  // }

  public void insert(double d, double e)
  {

    // System.out.println("Insertion: " + d + " " + e);
    // System.out.println(size);
    if(size < k){
      size++;
      distances[size] = d;
      examples[size] = e;
      // if(size > 1 && d < distances[size/2])
      //   inOrder = false;
    }
    else{
      int maxIndex = Utils.maxIndex(distances);

      // System.out.println("Comparison: " + distances[maxIndex] + " " + d);

      if(distances[maxIndex] > d)
      {
        distances[maxIndex] = d;
        examples[maxIndex] = e;
        if(size > 1 && d < distances[size/2])
          inOrder = false;
      }
    }

  }

  // public double extractMin()
  // {
  //   if(!inOrder)
  //     build();

  //   double min = distances[1];

  //   distances[1] = distances[size];
  //   examples[1] = examples[size];
  //   size--;
  //   if(size>1)
  //     percolateDown(1);

  //   return min;
  // }

  public double calculateDistance(Example e, Example obs)
  {
    double distance = 0.0;

    int classIndex = dataset.attributes.getClassIndex();

    for(int i = 0; i < e.size();i++)
    {
      if( i != classIndex && !(e.get(i).equals( obs.get(i)))  )
      {
        distance += 1.0;
      }  
    }

    return distance;

  }


  public IBk( String[] options ) throws Exception
  {
    this.setOptions(options);
  }

  public Performance classify( DataSet dataset ) throws Exception
  {
    Performance p = new Performance(dataset.getAttributes());
    Examples e = dataset.getExamples();
    int index = dataset.getAttributes().getClassIndex();
    for(int i = 0; i < e.size(); i++)
    {
      int prediction = classify(e.get(i));
      int actual = e.get(i).get(index).intValue();

      //System.out.println(actual + " " + prediction);
      p.add(actual, prediction);
    }

    return p;
    //evaluates models performance on dataset
  }

  public int classify( Example query ) throws Exception
  {
    
    int index = dataset.attributes.getClassIndex();

    size = 0;
    for(int i = 0; i < dataset.examples.size(); i++)
    {
      double distance = calculateDistance(dataset.examples.get(i), query);
      insert(distance, dataset.examples.get(i).get(index));

    }


    //find majority vote
    
    double count[] = new double[dataset.attributes.get(index).size()];

    for(int i = 1; i < examples.length ;i++)
    {

      count[(int)examples[i]]++;
    }

    int prediction = Utils.maxIndex(count);



    return prediction;

  }

  // public Classifier clone()
  // {
  //   //return (Classifier) Utils.deepClone(this);
  // }

  public void setK( int k )
  {
    this.k = k;

    distances = new double[k+1];
    examples = new double[k+1];
    
  }

  public void setOptions( String options[] ) throws Exception
  {
    char c;
    int a = 0;
    while ( a < options.length && options[a].charAt( 0 ) == '-' ) {
      c = options[a].charAt( 1 );
      switch ( c ) {
        case 'k': // setK
          this.setK(Integer.parseInt(options[++a]));
          a++;
          break;
        default:
          a++;
          if ( a < options.length && options[a].charAt( 0 ) != '-' )
            a++;
          break;
      } // switch
    } // while
  }

  public void train( DataSet dataset ) throws Exception
  {
    this.dataset = dataset;
    //builds model
  }

  public String toString()
  {
    StringBuilder s = new StringBuilder();
    s.append( dataset.toString());
    s.append("K: " + k);
    
    return s.toString();
  }

  public static void main( String[] args ) {
    try {
      Evaluator evaluator = new Evaluator( new IBk(), args );
      Performance performance = evaluator.evaluate();
      System.out.println( performance );
    } // try
    catch ( Exception e ) {
      System.out.println( e.getMessage() );
      e.printStackTrace();
    } // catch
  } // IBk::main
  

}


