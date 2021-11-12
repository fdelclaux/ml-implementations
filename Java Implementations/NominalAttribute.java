import java.util.ArrayList;

/*
 * NominalAttribute.java
 * Copyright (c) 2018 Mark Maloof.  All Rights Reserved.  See LICENSE.
 */

public class NominalAttribute extends Attribute{

  protected ArrayList<String> domain = new ArrayList<String>();

  public NominalAttribute()
  { 

  }

  public NominalAttribute( String name )
  {
    super(name);
  }

  public void addValue( String value )
  {
    domain.add(value);
  }


  public int size()
  {
    return domain.size();
  }


  public String getValue( int index )
  {
    return domain.get(index);
  }


  public int getIndex( String value ) throws Exception
  {
    int index = domain.indexOf(value);

    if (index == -1)
      throw new Exception("Specified value not found in this attributes domain.");
  
    return index;
  }

  @Override
  public String toString()
  {
    StringBuilder s = new StringBuilder();

    s.append(super.toString());

    for(int i = 0; i < domain.size(); i++)
    {
      s.append(" " + domain.get(i));
    }

    return s.toString();
  }

  public boolean validValue( String value )
  {
    return domain.contains(value);
  }
}
