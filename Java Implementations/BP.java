import java.io.Serializable;
import java.util.Random;
import java.lang.RuntimeException;
import java.text.DecimalFormat;


public class BP extends Classifier implements Serializable, OptionHandler
{
    Attributes attributes;
    
    Random random;

    private int J = 10;

    private double learningRate = 0.9;
    private double minE = 0.1;

    private double [][] v; //hidden layer weights
    private double [][] w; //output layer weights 


    //sigmoid function
    
    //calculate error

    //calculate error signal vectors

    //adjust weights
    
    public BP()
    {

    }

    public BP(String options[]) throws Exception
    {
        setOptions(options);
    }

    public Performance classify( DataSet ds ) throws Exception
    {
        ds = ds.encode(1);
        Performance p = new Performance(ds.attributes);
        Examples examples = ds.examples;

        int classIndex = ds.attributes.getClassIndex();

        for(int i = 0; i < examples.size();i++)
        {
            double actual = ds.examples.get(i).get(classIndex);
            double prediction = classify(examples.get(i));

            p.add(actual,prediction);
        }

        
        return p;
    }

    public int classify( Example example ) throws Exception
    {
        double [] h = new double[J]; // hidden layer
        h[J-1] = -1.0;
        double [] o = new double[attributes.get(attributes.getClassIndex()).size()];

        int classIndex = attributes.getClassIndex();
        double [] y = new double[attributes.get(classIndex).size()];

        for(int j = 0; j < h.length - 1;j++)
        {
            h[j] = 1/(1+Math.exp(Utils.dotProduct(v[j], example)));
        }

        for(int j = 0; j < o.length;j++)
        {
            o[j] = 1/(1+Math.exp(Utils.dotProduct(w[j],h)));
        }

        return Utils.maxIndex(o);
        
    }

    public void setOptions( String[] options ) throws Exception
    {
        char c;
        int a = 0;
        while ( a < options.length && options[a].charAt( 0 ) == '-' ) {
            c = options[a].charAt( 1 );
            switch ( c ) {
                case 'J': // setK
                    J = Integer.parseInt(options[++a]) + 1;
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

    public void _init_weights()
    {
        random = new Random(); 
        //v
        for(int i = 0; i < J;i++)
        {
            for(int z = 0; z < attributes.size()-1; z++)
            {
                v[i][z] = random.nextDouble();
            }
        }

        //w
        for(int i = 0; i < attributes.get(attributes.getClassIndex()).size(); i++)
        {
            for(int z = 0; z < J; z++)
            {
                w[i][z] = random.nextDouble();
            }
        }

        // //v
        // for(int i = 0; i < J;i++)
        // {
        //     for(int z = 0; z < attributes.size()-1; z++)
        //     {
        //         v[i][z] =  0.1+i*((double)1/10);
        //     }
        // }

        // //w
        // for(int i = 0; i < attributes.get(attributes.getClassIndex()).size(); i++)
        // {
        //     for(int z = 0; z < J; z++)
        //     {
        //         w[i][z] = 0.1+z*((double)1/10) + 0.3*i;
        //     }
        // }


    }

    public String toString()
    {
        DecimalFormat numberFormat = new DecimalFormat("#.00");
        StringBuilder s = new StringBuilder();
        for(int i = 0; i < v.length-1; i++)
        {
            for(int j = 0; j < v[0].length;j++)
            {
                s.append(numberFormat.format(v[i][j]) + " ");
            }
            s.append("\n");
        }

        s.append("\n\n");

        for(int i = 0; i < w.length; i++)
        {
            for(int j = 0; j < w[0].length;j++)
            {
                s.append(numberFormat.format(w[i][j]) + " ");
            }
            s.append("\n");
        }


        return s.toString();
    }

    public void train( DataSet ds ) throws Exception
    {
        DataSet dataset = ds.encode(1);
        attributes = dataset.attributes;

        v = new double[J][attributes.size()-1];
        w = new double[attributes.get(attributes.getClassIndex()).size()][J];

        _init_weights();
        // System.out.println(this.toString() + "\n");

    
        double [] h = new double[J]; // hidden layer
        h[J-1] = -1.0;
        double [] o = new double[attributes.get(attributes.getClassIndex()).size()]; // output layer

        int epoch = 0;
        double e = 0;
        do
        {
            e = 0; //error
            int m = 1; // current example
            
            for(int i = 0; i < dataset.examples.size();i++)
            {
                int classIndex = dataset.attributes.getClassIndex();
                double [] y = new double[dataset.attributes.get(classIndex).size()];

                // System.out.println(dataset.examples.get(i).toString());

                //hot one encoding
                for(int j = 0;j < y.length;j++)
                {
                    if(j == dataset.examples.get(i).get(classIndex))
                    {
                        y[j] = 1;
                    }
                    else{
                        y[j] = 0;
                    }
                }

                //forward feed examples
                for(int j = 0; j < h.length-1;j++)
                {
                    h[j] = 1/(1+Math.exp(-Utils.dotProduct(v[j], dataset.examples.get(i))));
                }

                for(int j = 0; j < o.length;j++)
                {
                    o[j] = 1/(1+Math.exp(-Utils.dotProduct(w[j],h)));
                }

                //compute error
                for(int j = 0; j < o.length; j++)
                {
                    e += (0.5)*Math.pow( y[j] - o[j] , 2.0);
                }


                //compute error signal vectors
                double [] deltao = new double[attributes.get(classIndex).size()]; // error signal vector output
                double [] deltah = new double[J]; // error signal vector hidden

                for(int j = 0; j < deltao.length; j ++)
                {
                    deltao[j] = (y[j] - o[j])*(1-o[j])*o[j];
                }

                for(int j = 0; j < deltah.length;j++)
                {
                    deltah[j] = h[j]*(1-h[j]);

                    double sumOk = 0.0;
                
                    for(int k = 0; k < deltao.length; k++)
                    {
                        sumOk += deltao[k] * w[k][j];
                    }

                    deltah[j] *= sumOk;
                }

                //adjust weights

                for(int k = 0; k <deltao.length;k++ )
                {
                    for(int j = 0; j < J;j++)
                    {
                        w[k][j] += learningRate *deltao[k]*h[j];
                    }
                }

                for(int j = 0 ; j < J;j++)
                {
                    for(int k = 0; k < dataset.examples.get(i).size()-1;k++)
                    {
                        v[j][k] += learningRate*deltah[j]*dataset.examples.get(i).get(k);
                    }
                }

                m++;

            }


            // System.out.println("Error: "+e+"\n");

            epoch++;
            
            if(epoch > 50000)
            {
                throw new FailedToConvergeException(); 
            }
           
        }while(e > minE);

        // System.out.println(this.toString());

    }

    public static void main( String[] args )
    {
        try{

            Evaluator evaluator = new Evaluator( new BP(), args );
            Performance performance = evaluator.evaluate();
            System.out.println( performance );
        } // try
        catch ( Exception e ) {
            System.out.println( e.getMessage() );
            e.printStackTrace();
        } // catch

    }

}