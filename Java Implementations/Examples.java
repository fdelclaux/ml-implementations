import java.util.ArrayList;
import java.util.Scanner;

/*
 * Examples.java
 * Copyright (c) 2018 Mark Maloof.  All Rights Reserved.  See LICENSE.
 */

public class Examples extends ArrayList<Example>{

  private Attributes attributes;

  public Examples( Attributes attributes )
  {
    this.attributes = attributes;
  }


  public void parse( Scanner scanner ) throws Exception
  {
      scanner.next(); // take @examples tag

      while(scanner.hasNext())
      {
        Example obs = new Example(attributes.size());


        for(int i = 0; i<attributes.size(); i++)
        {
          String token = scanner.next();

          Attribute att = attributes.get(i);

          if(att instanceof NominalAttribute)
          {
            if(((NominalAttribute)att).validValue(token))
            {
              // System.out.print(Double.valueOf(((NominalAttribute)att).getIndex(token))+ " ");
              obs.add(Double.valueOf(((NominalAttribute)att).getIndex(token)));
            }
          }
          else{
            if(((NumericAttribute)att).validValue(Double.parseDouble(token))){
              // System.out.print(Double.parseDouble(token)+ " ");
              obs.add(Double.parseDouble(token)); 
            }
          }
        }

        // System.out.print("\n");

        add(obs);
        
      }
  }


  public String toString()
  {
    StringBuilder s = new StringBuilder();

    s.append("\n@examples\n\n");

    for(int i = 0; i <size();i++)
    {
      Example observation = get(i);

      for(int j = 0; j < attributes.size(); j++)
      {
        Attribute att = attributes.get(j);

        if(att instanceof NominalAttribute)
        {
          //System.out.print( observation.get(i) +  " ");
          s.append( ((NominalAttribute)att).getValue( observation.get(j).intValue() ) + " " );
        }
        else{
          //System.out.print( observation.get(i) +  " ");
          s.append(observation.get(j) + " ");
        }
      }
      //System.out.print("\n");
      s.append("\n");
    }


    return s.toString();
    
  }
}