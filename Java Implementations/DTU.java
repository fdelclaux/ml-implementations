import java.io.Serializable;
import java.util.ArrayList;

/*
 * DTU.java
 * Copyright (c) 2018 Mark Maloof.  All Rights Reserved.  See LICENSE.
 */

public class DTU extends Classifier implements Serializable, OptionHandler
{

  protected Attributes attributes;
  protected Node root;

  public DTU()
  {

  }

  public DTU( String[] options ) throws Exception
  {
    setOptions(options);
  }

  public Performance classify( DataSet ds ) throws Exception
  {
    Performance p = new Performance(ds.getAttributes());

    Examples e = ds.getExamples();

    int classIndex = ds.getAttributes().getClassIndex();

    for(int i = 0; i < e.size();i++)
    {
      int prediction = classify(e.get(i));
      int actual = e.get(i).get(classIndex).intValue();

      // System.out.println(actual + " " + prediction);

      p.add(actual, prediction);
    }

    return p;

  }

  public int classify( Example example ) throws Exception
  {
    Node current = root;

    while(!current.isLeaf())
    {
      current = current.children.get(example.get(current.attribute).intValue());
    }
    return current.label;
  }
  
  public void setOptions( String[] options ) throws Exception
  {
    super.setOptions(options);
  }

  public String toString()
  {
    return toString_aux(root);
    
  } // optional

  public String toString_aux(Node n)
  {
    StringBuilder s = new StringBuilder();
    if (n.isLeaf())
    {
      s.append(n.label+"\n\n");
      return s.toString();
    }
    s.append(n.attribute+"\n");
    for(int  i = 0; i < n.children.size();i++)
    {
      s.append(n.children.get(i).attribute+"\n");
      toString_aux(n.children.get(i));
    }

    return s.toString();
    
  } // optional

  public void train( DataSet ds ) throws Exception
  {
    root = train_aux(ds);

  }

  // private recursive methods

  private Node train_aux( DataSet ds ) throws Exception
  {
    // System.out.println(ds.toString()+"\n");
    Node n = new Node();

    if (ds.homogeneous() || ds.examples.size() < 4 )
    {
      n.label = ds.getMajorityClassLabel();
      // System.out.println(n.label+"\n");
      return n;
    }

    int att = ds.getBestSplittingAttribute();

    int count = 0;

    //deal with ambiguous attributes
    for(int i = 0; i < ds.examples.size()-1;i++)
    {
      if(ds.examples.get(i).get(att) == ds.examples.get(i+1).get(att))
        count++;
    }

    if(count == ds.examples.size()-1)
    {
      n.label = ds.getMajorityClassLabel();
      return n;
    }
    else{
      ArrayList<DataSet> branches;
      branches = ds.splitOnAttribute(att);
      for(int i = 0; i < branches.size(); i++)
      {
        n.children.add(new Node());

        if(branches.get(i).isEmpty())
        {
          n.children.get(i).label = ds.getMajorityClassLabel();
        }
        else
        {
          n.children.set(i ,train_aux(branches.get(i)));
        }// end if
      }//end for
      n.attribute = att;

      return n;
    }//end if

  }

  public static void main( String[] args ) {
    try {
      String [] args2 = {"-t", "bikes-nominal.mff"};
      Evaluator evaluator = new Evaluator( new DTU(), args );
      Performance performance = evaluator.evaluate();
      System.out.println( performance );
    } // try
    catch ( Exception e ) {
      System.out.println( e.getMessage() );
      e.printStackTrace();
    } // catch
  } // NaiveBayes::main

}