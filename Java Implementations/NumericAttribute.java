/*
 * NumericAttribute.java
 * Copyright (c) 2018 Mark Maloof.  All Rights Reserved.  See LICENSE.
 */

public class NumericAttribute extends Attribute{

  public NumericAttribute()
  {

  }
  public NumericAttribute( String name )
  {
    super(name);
  }

  public int size(){

    return Integer.MAX_VALUE;
  }

  @Override
  public String toString()
  {
    StringBuilder s = new StringBuilder();

    s.append(super.toString());
    s.append( " numeric");

    return s.toString();
  }

  public boolean validValue( Double value )
  {
    return true;
  }
}