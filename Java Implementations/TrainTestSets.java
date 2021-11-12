import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;

/*
 * TrainTestSets.java
 * Copyright (c) 2018 Mark Maloof.  All Rights Reserved.  See LICENSE.
 */

public class TrainTestSets implements OptionHandler{

  protected DataSet train;
  protected DataSet test;

  public TrainTestSets()
  {
    train = null;
    test = null;

  }

  public TrainTestSets( String [] options ) throws Exception {
    this();
    setOptions( options );
  }


  public TrainTestSets( DataSet train, DataSet test )
  {
    setTrainingSet(train);
    setTestingSet(test);

  }


  public DataSet getTrainingSet()
  {
    return train;

  }


  public DataSet getTestingSet()
  {
    return test;
  }


  public void setTrainingSet( DataSet train )
  {
    this.train = train;

  }


  public void setTestingSet( DataSet test )
  {
    this.test = test;
  }


  public void setOptions( String[] options ) throws Exception {
    char c;
    int a = 0;
    while ( a < options.length && options[a].charAt( 0 ) == '-' ) {
      c = options[a].charAt( 1 );
      switch ( c ) {
        case 't': // training set
          train = new DataSet();
          train.load( options[++a] );
          a++;
          break;
        case 'T': // testing set
          test = new DataSet();
          test.load( options[++a] );
          a++;
          break;
        default:
          a++;
          if ( a < options.length && options[a].charAt( 0 ) != '-' )
            a++;
          break;
      } // switch
    } // while
  } // TrainTestSets::setOptions


  public String toString()
  {
    StringBuilder s = new StringBuilder();

    if(train == null)
    {
      s.append("No training set available: \n\n");
    }
    else{
      s.append("Training Set: \n\n");
      s.append(train.toString());
    }

    
    if(test == null)
    {
      s.append("\n\nNo testing set available.");
    }
    else{
      s.append("\n\nTesting set: \n");
      s.append(test.toString());
    }

    return s.toString();
  }

  public static void main( String args[] ) throws Exception {
    

    DataSet d1 = new DataSet();

    d1.load("bikes-nominal.mff");
    
    System.out.println(d1.gainRatio(0));

  }


}
