import java.io.Serializable;
import java.util.Random;

public class Perceptron extends Classifier implements Serializable, OptionHandler
{

    Attributes attributes;
    private double learningRate = 0.9;
    private double[] weights;  

    public Perceptron()
    {

    }

    public Perceptron(String options[]) throws Exception
    {
        setOptions(options);
    }

    public double sign(double x)
    {
        if(x >= 0 )
            return 1.0;
        return -1.0;
    }

    public Performance classify( DataSet ds ) throws Exception
    {
        DataSet dataset = ds.encode(0);
        Performance p = new Performance(dataset.attributes);
        Examples examples = dataset.examples;

        int classIndex = dataset.attributes.getClassIndex();

        for(int i = 0; i < examples.size();i++)
        {

            double actual = dataset.examples.get(i).get(classIndex);
            if(actual == 0.0)
                    actual = -1.0;
            int prediction = classify(dataset.examples.get(i));

            p.add(actual, (double)prediction);


        }

        return p;
    }

    public int classify( Example example ) throws Exception
    {
        return((int)sign(Utils.dotProduct(weights, example)));
    }

    public void setOptions( String[] options ) throws Exception
    {
      super.setOptions(options);
    }

    public String toString()
    {
        StringBuilder s = new StringBuilder();
        for(int i = 0; i < weights.length; i++)
        {
            s.append(attributes.get(i).getName() + ": " + weights[i]+"\n");
        }

        return s.toString();
    }

    public void train( DataSet ds ) throws Exception
    {
        DataSet dataset = ds.encode(0);
        attributes = dataset.attributes;
        weights = new double[dataset.attributes.size()-1];
        // System.out.println(dataset.attributes.size());

        for(int i = 0; i < weights.length;i++)
        {
            weights[i] = 0.0;
        }

        boolean converged = false;

        int epochs = 0;

        while( !converged )
        {
            converged = true;
            for(int i = 0; i < dataset.examples.size(); i++)
            {
                
                int classIndex = dataset.attributes.getClassIndex();
                // System.out.println(dataset.examples.get(i).size());
                double actual = dataset.examples.get(i).get(classIndex);
                double y;
                if(actual == 0.0)
                    y = -1.0;
                else
                    y = 1.0;

                // System.out.println("here: " + actual + " "+ y);
                
                if(y*Utils.dotProduct(weights,dataset.examples.get(i)) <= 0)
                {
                    // System.out.println("readjusting weights");
                    for(int j = 0; j < weights.length;j++)
                    {
                        weights[j] += learningRate*y*dataset.examples.get(i).get(j);
                    }
                    converged = false;
                }
            }
            epochs++;

        }

    }



    public static void main( String[] args )
    {
        try{

            Evaluator evaluator = new Evaluator( new Perceptron(), args );
            Performance performance = evaluator.evaluate();
            System.out.println( performance );
        } // try
        catch ( Exception e ) {
            System.out.println( e.getMessage() );
            e.printStackTrace();
        } // catch

    }

}