import java.io.Serializable;
import java.lang.reflect.*;
import java.util.Random;

public class Bagging extends Classifier implements Serializable, OptionHandler{

    protected int ensembleSize = 10;
    protected Classifier baseClassifier;
    protected Classifier[] classifiers;


    public Bagging()
    {

    }

    public Bagging(String[] options) throws Exception
    {
        setOptions(options);
    }

    public Performance classify(DataSet dataset) throws Exception
    {
        Performance p = new Performance(dataset.attributes);

        int classIndex = dataset.attributes.getClassIndex();

        for(int i = 0; i < dataset.examples.size();i++)
        {

            int prediction = classify(dataset.examples.get(i));
            int actual = dataset.examples.get(i).get(classIndex).intValue();

            p.add(actual,prediction);

        }

        return p;

    }

    public int classify(Example example) throws Exception
    {
        double [] predictions = new double[ensembleSize];
        for(int i = 0; i < ensembleSize;i++)
        {
            predictions[i] = classifiers[i].classify(example);
        }

        return Utils.maxIndex(predictions);
    
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

    public String toString()
    {
        return "";
    }

    public void train(DataSet dataset) throws Exception
    {
        classifiers = new Classifier[ensembleSize];
        for(int i = 0; i < ensembleSize;i++)
        {
            DataSet ds = dataset.getBootstrapSample();

            Classifier copyClassifier = (Classifier) Utils.deepClone(baseClassifier);

            copyClassifier.train(ds);

            classifiers[i] = copyClassifier;
        }

        //create ensembleSize dataSets and train one baseClassifier classifier into classifiers array

    }

    public static void main( String[] args ) {
        try {
          String [] args2 = {"-t", "bikes-nominal.mff"};
          Evaluator evaluator = new Evaluator( new Bagging(), args );
          Performance performance = evaluator.evaluate();
          System.out.println( performance );
        } // try
        catch ( Exception e ) {
          System.out.println( e.getMessage() );
          e.printStackTrace();
        } // catch
      } // NaiveBayes::main
}