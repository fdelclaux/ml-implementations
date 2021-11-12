/*
 * Attribute.java
 * Copyright (c) 2018 Mark Maloof.  All Rights Reserved.  See LICENSE.
 */

abstract class Attribute extends Object{
  
  protected String name;

  abstract int size();

  public Attribute()
  {
    super();
  }


  public Attribute( String name )
  {
    this.name = name;
  }

  public  String getName()
  {
    return name;
  }


  public void setName( String name )
  {
    this.name = name;
  }


  public String toString()
  {
    return "@attribute " + name;
  }
}
