import java.io.Serializable;

public class Boosting extends Classifier implements Serializable, OptionHandler{

    protected Attributes attributes;

    protected int ensembleSize =10;
    protected Classifier baseClassifier;
    protected Classifier[] classifiers;
    protected double[] alpha;
    protected DataSet[] weightedDS;

    public Boosting()
    {

    }

    public Boosting(String[] options) throws Exception
    {
        setOptions(options);
    }

    public Performance classify(DataSet dataset) throws Exception
    {
      Performance p = new Performance(dataset.attributes);

      int classIndex = dataset.attributes.getClassIndex();

      for(int i = 0; i < dataset.examples.size(); i++)
      {
        int prediction = classify(dataset.examples.get(i));

        int actual = dataset.examples.get(i).get(classIndex).intValue();

        p.add(actual, prediction);
      }

      return p;

    }

    public int classify(Example example) throws Exception
    {

      double sum = 0.0;

      for(int i = 0 ; i < ensembleSize; i++)
      {
        int cl = classifiers[i].classify(example);

        if(cl == 0)
        {
          cl = -1;
        }
        sum += alpha[i]*cl;
      }

      sum = sign(sum);

      if((int)sum == 1)
      {
        return 1;
      }

      return 0;
    
    }

    public void setOptions( String[] options ) throws Exception
    {
        char c;
        int a = 0;
        while ( a < options.length && options[a].charAt( 0 ) == '-' ) {
            c = options[a].charAt( 1 );
            switch ( c ) {
                case 'b': // set baseClassifier
                    Class<?> cl = Class.forName(options[++a]);
                    this.baseClassifier = (Classifier) cl.getDeclaredConstructor().newInstance();
                    a++;
                    break;
                default:
                    a++;
                    if ( a < options.length && options[a].charAt( 0 ) != '-' )
                        a++;
                    break;
            } // switch
        } // while
    }


    private void update_weights(int i,double y[], double[][] predictions)
    {
      DataSet current = weightedDS[i];
      DataSet next = new DataSet(current.attributes);
      next.examples = current.examples;

      double sum = 0.0;

      for(int j = 0; j < next.examples.size();j++)
      {
        double oldW = current.examples.get(j).getWeight();

        double newW = oldW * Math.exp((-1)*alpha[i]*y[j]*predictions[i][j]);

        next.examples.get(j).setWeight(newW);
        sum += newW;
      }

      for(int j = 0; j < next.examples.size();j++)
      {
        next.examples.get(j).setWeight(next.examples.get(j).getWeight()/sum);
      }

      weightedDS[i+1] = next;

    }

    private double sign(double x)
    {
      if( x >= 0)
        return 1.0;
      return -1.0;
    }

    public String toString()
    {
      return "";

    }

    public void train(DataSet dataset) throws Exception
    {
      //initializations
      int size = dataset.examples.size();

      classifiers = new Classifier[ensembleSize];
      alpha = new double[ensembleSize];
      double[] y = new double[size];
      double [][] predictions = new double[ensembleSize][size];

      int classIndex = dataset.attributes.getClassIndex();
      weightedDS = new DataSet[ensembleSize+1];

      //first weighted DataSet
      DataSet init = new DataSet(dataset.attributes);
      init.examples = dataset.examples;

      init._init_weights();
      weightedDS[0] = init;

      //loop through ensemble
      for(int i = 0; i < ensembleSize; i++)
      {
        double error = 0.0;

        DataSet ds = weightedDS[i];
        Classifier copyClassifier = (Classifier) Utils.deepClone(baseClassifier);
        copyClassifier.train(ds);

        double sumWeights = 0.0;

        //loop through examples
        for(int j = 0; j < ds.examples.size();j++)
        {

          if(i==0)
          {
            int actual = ds.examples.get(j).get(classIndex).intValue();

            if(actual == 0)
              y[j] = -1.0;
            else
              y[j] = actual;
          }

          int prediction = copyClassifier.classify(ds.examples.get(j));

          if(prediction == 0)
          {
            predictions[i][j] = -1.0;
          }
          else
          {
            predictions[i][j] = 1.0;
          }
          
          sumWeights += ds.examples.get(j).getWeight();
          // System.out.println(predictions[i][j] + " " + y[j]);

          if(predictions[i][j] != y[j])
            error += ds.examples.get(j).getWeight();
        }//end for

        error /= sumWeights;

        // if(error >= 0.5)
        // {
        //   i--;
        //   break;
        // }

        if(error == 0.0)
          error = 0.00001;

        alpha[i] = 0.5*Math.log((1-error)/error);

        // System.out.println(error + "\n");
          
        update_weights(i, y, predictions);

        classifiers[i] = copyClassifier;

      }//end for

    }//end train

    public static void main( String[] args ) {
      try {
        String [] args2 = {"-t", "mushroom.mff", "-T","mushroom.mff","-b","WDS"};
        Evaluator evaluator = new Evaluator( new Boosting(), args );
        Performance performance = evaluator.evaluate();
        System.out.println( performance );
      } // try
      catch ( Exception e ) {
        System.out.println( e.getMessage() );
        e.printStackTrace();
      } // catch
    } // NaiveBayes::main
}