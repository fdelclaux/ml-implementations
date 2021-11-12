/*
 * DataSet.java
 * Copyright (c) 2018 Mark Maloof.  All Rights Reserved.  See LICENSE.
 */


 /*
 * DataSet.java
 * Copyright (c) 2018 Mark Maloof.  All Rights Reserved.  See LICENSE.
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

/*
 * DataSet.java
 * Copyright (c) 2018 Mark Maloof.  All Rights Reserved.  See LICENSE.
 */

public class DataSet
{

  protected String name;
  protected Attributes attributes = null;
  protected Examples examples = null;
  protected Random random;

  protected int folds = 10;
  protected int[] partitions = null;

  public DataSet()
  {
    attributes = new Attributes();
    random = new Random();

  }

  public DataSet( Attributes attributes )
  {
    this.attributes = attributes;
    examples = new Examples(attributes);
    random = new Random();

  }


  public void add( Example example )
  {
    examples.add(example);
  }


  public Attributes getAttributes()
  {
    return attributes;
  }


  public Examples getExamples()
  {
    return examples;
  }


  public boolean getHasNumericAttributes()
  {
    return attributes.getHasNumericAttributes();
  }


  public void load( String filename ) throws Exception
  {
    Scanner scanner = new Scanner( new BufferedReader( new FileReader( filename ) ) );

    parse(scanner);
    
  }


  private void parse( Scanner scanner ) throws Exception
  {
    scanner.next(); // gets rid of @dataset tag
    name = scanner.next();
    attributes.parse(scanner);

    examples = new Examples(attributes);

    examples.parse(scanner);
  }

  public void setRandom( Random random )
  {
    this.random = random;
  }


  public String toString()
  {

    StringBuilder s = new StringBuilder();
    s.append("@dataset " + name + "\n\n");
    s.append(attributes.toString());
    s.append(examples.toString());

    return s.toString();

  }

  public TrainTestSets getCVSets( int p ) throws Exception
  {
    if(p > folds)
    {
      throw new Exception();
    }

    if(p == 0)
    {
      partitions = new int[examples.size()];
      for(int i = 0; i < examples.size(); i++)
      {
          partitions[i] = this.random.nextInt(this.folds);
      }
    }

    TrainTestSets tts = new TrainTestSets();
    DataSet train = new DataSet(attributes);
    DataSet test = new DataSet(attributes);


    for(int i = 0; i < partitions.length;i++)
    {
      if(partitions[i] != p)
      {
        train.add(examples.get(i));
      }
      else{
        test.add(examples.get(i));
      }
    }
    tts.setTestingSet(test);
    tts.setTrainingSet(train);
    
    return tts;
    //partition dataset
  }

  public int getFolds()
  {
    return folds;
  }

  public void setFolds( int folds ) throws Exception
  {
    this.folds = folds;
    //populate Partitions
  }

    
  public boolean isEmpty()
  {
    return (examples.isEmpty());
  }

  public void _init_weights()
  {
    for(int i = 0; i < examples.size();i++)
    {
      Example e = examples.get(i);

      e.setWeight((double)1/examples.size());
    }
  }

  public double [] getCounts( int index)
  {

    double [] counts = new double[attributes.get(index).size()];

    for(int i = 0;i < examples.size();i++)
    {
      counts[examples.get(i).get(index).intValue()] += examples.get(i).getWeight();
    }

    return counts;
  }

  public double calculateEntropy(double[] counts,double size)
  {
    double entropyS = 0.0;

    for(int i = 0; i < counts.length;i++)
    {
      if(counts[i] != 0)
        entropyS += ((double)counts[i]/size) * (Math.log(  ((double)counts[i] / size ))/Math.log(2));
    }

    entropyS *= -1;

    return entropyS;
  }

  public double gainRatio( int attribute ) throws Exception
  {
    ArrayList<DataSet> branches = splitOnAttribute(attribute);
    double gainRatio = 0.0;

    int classIndex = attributes.getClassIndex();

    double [] classCounts = getCounts(classIndex);
    double [] attCounts = getCounts(attribute);

    //calculate sum of weights
    double size = 0;
    for(int i = 0; i < examples.size();i++)
    {
      size += examples.get(i).getWeight();
    }

    
    //calculate gain
    double entropyS = calculateEntropy(classCounts,size);

    double attEntropies = 0.0;

    for(int i = 0; i < branches.size();i++)
    {
      attEntropies += (attCounts[i]/size)*calculateEntropy(branches.get(i).getCounts(classIndex),attCounts[i]);
    }

    double gain = entropyS - attEntropies;

    //calculate split info
    double splitInfo = 0.0;

    for(int i = 0; i < branches.size(); i++)
    {
      if(attCounts[i]!= 0)
        splitInfo += (attCounts[i]/size) * ( Math.log(  (attCounts[i]/size) )/Math.log(2));
    }

    splitInfo *= -1;


    //calculate and return gain ratio
    if(splitInfo != 0)
      gainRatio = gain/splitInfo;

    return gainRatio;
  }

  public int getBestSplittingAttribute() throws Exception
  {
    double maxGain = 0.0;
    boolean maxExists = false;
    double [] gainRatios = new double[attributes.size()];

    int classIndex = attributes.getClassIndex();

    for(int i = 0; i < attributes.size();i++)
    {
      if(i != classIndex)
      {
        gainRatios[i] = gainRatio(i);

        if(gainRatios[i] > maxGain)
        {
          maxGain = gainRatios[i];
          maxExists = true;
        }
      }
    }

    if (!maxExists)
      return -1;

    return Utils.maxIndex(gainRatios);

  }

  public ArrayList<DataSet> splitOnAttribute( int attribute ) throws Exception
  {
    ArrayList<DataSet> children = new ArrayList<DataSet>(attributes.get(attribute).size());

    for(int i = 0; i < attributes.get(attribute).size(); i++)
    {
      children.add(new DataSet(attributes));
    }

    for(int i = 0; i < examples.size();i++)
    {
      children.get(examples.get(i).get(attribute).intValue()).add(examples.get(i));
    }

    return children;
  }

  public boolean homogeneous() throws Exception
  {

    int classIndex = attributes.getClassIndex();

    for(int i = 0; i < examples.size() - 1;i++)
    {
      if(!examples.get(i).get(classIndex).equals(examples.get(i+1).get(classIndex)))
      {
        return false;
      }
    }

    return true;

  }

  public int getMajorityClassLabel() throws Exception
  {
    int classIndex = attributes.getClassIndex();

    double [] counts = new double[attributes.get(classIndex).size()];

    for(int i = 0; i < examples.size();i++)
    {
      counts[examples.get(i).get(classIndex).intValue()] += examples.get(i).getWeight();
    }

    return Utils.maxIndex(counts);

  }

  public TrainTestSets getHoldOutSets( double p ) throws Exception
  {
    if(p > 1.0)
    {
      throw new Exception();
    }

    Collections.shuffle(examples, random);

    TrainTestSets tts = new TrainTestSets();
    DataSet train = new DataSet(attributes);
    DataSet test = new DataSet(attributes);

    for(int i = 0; i < examples.size();i++)
    {
      if(i < examples.size() * p )
      {
        train.add(examples.get(i));
      }
      else
      {
        test.add(examples.get(i));
      }
    }

    tts.setTestingSet(test);
    tts.setTrainingSet(train);
    
    return tts;
  }

  public DataSet getBootstrapSample()
  {

    DataSet ds = new DataSet(attributes);

    Collections.shuffle(examples, this.random);

    for(int j = 0; j < examples.size(); j++)
    {
      int position = this.random.nextInt(examples.size());
      Example e = examples.get(this.random.nextInt(examples.size()));
      ds.add(e);
    }
    return ds;

  }

  public DataSet encode(int option) throws Exception
  {
    DataSet ds = new DataSet();
    ds.name = name;

    Attributes att = new Attributes();


    if(!attributes.getHasNumericAttributes())
    {
        
        for(int i = 0; i < attributes.size();i++)
        {
          if(i != attributes.getClassIndex())
          {
            int necessaryAtts = (int) Math.ceil(Math.log(attributes.get(i).size())/Math.log(2.0));
            for(int j = 0; j < necessaryAtts; j++)
            {
              NumericAttribute newAtt = new NumericAttribute(new String(attributes.get(i).getName()+j));
              att.add(newAtt);
            }
          }
        }
        att.add(new NumericAttribute("bias"));
        att.add(attributes.get(attributes.getClassIndex()));
        att.setClassIndex(att.size()-1);

        
    }
    else{
      for(int i = 0; i < attributes.size();i++)
      {
        if(i != attributes.getClassIndex())
        {
          att.add(new NumericAttribute(attributes.get(i).getName()));
        }
        
      }
      att.add(new NumericAttribute("bias"));
      att.add(attributes.get(attributes.getClassIndex()));
      att.setClassIndex(att.size()-1);
    }

    ds.attributes = att;

    // System.out.println(att.toString());
    // System.out.println(att.size());

    Examples exs = new Examples(att);

    if(!attributes.getHasNumericAttributes())
    {
      for(int i = 0; i < examples.size();i++)
      {
        Example e = new Example();
        for(int j = 0; j < attributes.size()-1;j++)
        {
          int num = examples.get(i).get(j).intValue();
          // System.out.println(i + " " + j + " " + num);

          int necessaryAtts = (int) Math.ceil(Math.log(attributes.get(j).size())/Math.log(2.0));
          // System.out.println(necessaryAtts);
          // System.out.println(attributes.get(j).size());

          for(int z = 0; z < necessaryAtts;z++)
          {
            if(num == 0)
            {
              if(option == 1)
              {
                e.add(0.0);
              }
              else{
                e.add(-1.0);
              }
            }
            else if(num % 2 == 0)
            {
              if(option == 1)
              {
                e.add(0.0);
              }
              else{
                e.add(-1.0);
              }
            }
            else{
              e.add(1.0);
            }
            num/=2;

            // System.out.println(e.get(e.size()-1));

          }// end for

        } //end for2
        e.add(-1.0);
        e.add(examples.get(i).get(attributes.getClassIndex()));
        exs.add(e);
        
      }//end for1
    }
    else{

      for(int i = 0; i < examples.size();i++)
      {
        Example e = new Example();
        for(int j = 0; j < examples.get(i).size()-1;j++)
        {
          e.add(examples.get(i).get(j));
        }
        e.add(-1.0);
        e.add(examples.get(i).get(attributes.getClassIndex()));

        exs.add(e);
      }

      // System.out.println(exs.toString());

    }

    // System.out.println(exs.get(0).size());

    ds.examples = exs;

    return ds;
  
  }

  public DataSet hotOneEncoding() throws Exception
  {
    DataSet ds = (DataSet) Utils.deepClone(this);
    
    
    //encode

    //add input bias

    return ds;
  
  }




  public static void main( String args[] ) throws Exception {
  
    Attributes attr  = new Attributes();

    Scanner scanner = new Scanner (new BufferedReader( new FileReader( "bikes_nominal.mff" ) ));

    attr.parse(scanner);

    Examples examples = new Examples(attr);
    examples.parse(scanner);
  

    scanner.close();

  }
}



