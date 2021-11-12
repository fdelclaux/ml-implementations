import java.io.Serializable;
import java.util.ArrayList;

/*
 * NaiveBayes.java
 * Copyright (c) 2018 Mark Maloof.  All Rights Reserved.  See LICENSE.
 */

public class NaiveBayes extends Classifier implements Serializable, OptionHandler{

  protected Attributes attributes;
  protected CategoricalEstimator classDistribution;
  protected ArrayList< ArrayList<Estimator> > classConditionalDistributions;

  public NaiveBayes()
  {

  }

  public NaiveBayes( String[] options ) throws Exception
  {
    setOptions(options);
  }

  public Performance classify( DataSet dataSet ) throws Exception
  {
    Performance p = new Performance(attributes);
    Examples examples = dataSet.getExamples();

    int classIndex = attributes.getClassIndex();

    for(int i = 0; i < examples.size(); i++)
    {
      p.add(examples.get(i).get(classIndex).intValue(), classify(examples.get(i)));
    }

    return p;
    //evaluates performance of model on dataset and returns performance
  }

  public int classify( Example example ) throws Exception
  {
    int classIndex = attributes.getClassIndex();
  
    double probabilities[] = new double[attributes.get(classIndex).size()];

    for(int i = 0; i < attributes.get(classIndex).size(); i++)
    {
      probabilities[i] = classDistribution.getProbability(i);
      for(int j = 0; j < example.size(); j++)
      {
        Estimator e = classConditionalDistributions.get(i).get(j);
        probabilities[i] *= e.getProbability(example.get(j));
      }

    }

    int index = Utils.maxIndex(probabilities);

    return index;
    
    //classifies classIndex for this Example
  }

  // public Classifier clone()
  // {
  //
  // }

  public void setOptions( String[] options ) throws Exception
  {
    super.setOptions(options);
    //no options for naive bayes
  }

  private void init()
  {
    int classIndex = attributes.getClassIndex();

    int nAttr = attributes.size();
    int nDomain = attributes.get(classIndex).size();

    classConditionalDistributions = new ArrayList< ArrayList<Estimator> >(nDomain);
    classDistribution = new CategoricalEstimator(nDomain);


    for(int i = 0; i < nDomain; i++)
    {
      classConditionalDistributions.add(new ArrayList<Estimator>(nAttr));
      for(int j = 0; j < nAttr; j++)
      {
        classConditionalDistributions.get(i).add(new CategoricalEstimator(attributes.get(j).size()));
      }
    }

  }

  public void train( DataSet dataset ) throws Exception
  {
    //builds model: populate classConditionalDistribution
    attributes = dataset.getAttributes();
    Examples examples = dataset.getExamples();

    int classIndex = attributes.getClassIndex();

    this.init();  

    for(int i = 0; i < examples.size(); i++)
    {
      
      classDistribution.add(dataset.examples.get(i).get(classIndex)); //add values to CategoricalEstimator to compute prior probabilities

      for(int j = 0; j < examples.get(i).size();j++)
      {
        
        if(j != classIndex)
        {
          Estimator e = classConditionalDistributions.get(examples.get(i).get(classIndex).intValue()).get(j);
          e.add(examples.get(i).get(j));
        }
      }

    }

  }

  public String toString()
  {
    return attributes.toString();
  }

  public static void main( String[] args ) {
    try {
      Evaluator evaluator = new Evaluator( new NaiveBayes(), args );
      Performance performance = evaluator.evaluate();
      System.out.println( performance );
    } // try
    catch ( Exception e ) {
      System.out.println( e.getMessage() );
      e.printStackTrace();
    } // catch
  } // NaiveBayes::main
}
