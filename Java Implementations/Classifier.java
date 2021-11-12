/*
 * Classifier.java
 * Copyright (c) 2018 Mark Maloof.  All Rights Reserved.  See LICENSE.
 */

public abstract class Classifier extends Object implements OptionHandler{

  public Classifier()
  {

  }

  public Classifier( String[] options ) throws Exception
  {
    setOptions(options);
  }

  abstract public Performance classify( DataSet dataset ) throws Exception;

  abstract public int classify( Example example ) throws Exception;

  // public abstract Classifier clone()
  // {
    
  // }

  public void setOptions( String[] options ) throws Exception
  {

  }

  abstract public String toString();

  abstract public void train( DataSet dataset ) throws Exception;

}
