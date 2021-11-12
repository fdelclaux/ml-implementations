  import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/*
 * Utils.java
 * Copyright (c) 2018 Mark Maloof.  All Rights Reserved.  See LICENSE.
 */

public class Utils{

  /**
   * http://alvinalexander.com/java/java-deep-clone-example-source-code
   */

  public static Object deepClone( Object object ) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream( baos );
      oos.writeObject( object );
      ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
      ObjectInputStream ois = new ObjectInputStream( bais );
      return ois.readObject();
    }
    catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  } // Utils::deepClone

  public static int maxIndex( double[] p )
  {
    int max = 0;
    for(int i = 1; i < p.length;i++)
    {
      if(p[max] < p[i])
        max = i;
    }

    return max;
    
  }
  
  public static double dotProduct(double[] v, Example x)
  {
    double product =0.0;
    for(int i = 0; i < v.length;i++)
    {
      // System.out.println(v[i] + " " + x.get(i));
      product += v[i]*x.get(i);
    }
    // System.out.println(product);
    return product;
  }

  public static double dotProduct(double[] w, double[] h)
  {
    double product =0.0;
    for(int i = 0; i < w.length;i++)
    {
      // System.out.println(w[i] + " " + h[i]);
      product += w[i]*h[i];
    }
    // System.out.println(product);
    return product;
  }

  public static double[] matrixMultiplication(double x, double [] w)
  {
    double [] y = new double[w.length];

    for(int i = 0; i < y.length; i++)
    {
      y[i] = w[i]*x;
    }

    return y;
  }
}
