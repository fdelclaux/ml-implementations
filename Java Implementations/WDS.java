import java.io.Serializable;
import java.util.ArrayList;

public class WDS extends Classifier implements Serializable, OptionHandler{

    protected Attributes attributes;
    protected Node root;
  
    public WDS()
    {
  
    }
  
    public WDS( String[] options ) throws Exception
    {
      super.setOptions(options);
    }

    @Override
    public Performance classify(DataSet ds) throws Exception {
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

    @Override
    public int classify(Example example) throws Exception {
        Node current = root;

        while(!current.isLeaf())
        {
            current = current.children.get(example.get(current.attribute).intValue());
        }
        return current.label;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setOptions( String[] options ) throws Exception
    {
      super.setOptions(options);
    }

    @Override
    public void train(DataSet dataset) throws Exception {
        root = train_aux(dataset);
        
    }
    
    private Node train_aux (DataSet ds) throws Exception
    {
        Node n = new Node();

        if(ds.homogeneous() || ds.examples.size() < 4)
        {
            n.label = ds.getMajorityClassLabel();
            return n;
        }

        int att = ds.getBestSplittingAttribute();

        int count = 0;

        for(int i = 0 ; i < ds.examples.size()-1;i++)
        {
            if(ds.examples.get(i).get(att) == ds.examples.get(i+1).get(att))
                count++;
        }

        if(count == ds.examples.size() - 1)
        {
            n.label = ds.getMajorityClassLabel();
            return n;
        }
        else{
            ArrayList<DataSet> branches;
            branches = ds.splitOnAttribute(att);
            for(int i = 0; i < branches.size();i++)
            {
                n.children.add(new Node());

                n.children.get(i).label = ds.getMajorityClassLabel();
            }
            n.attribute = att;

            return n;
        }
    }

    public static void main( String[] args ) {
        try {
          String [] args2 = {"-t", "bikes-nominal.mff"};
          Evaluator evaluator = new Evaluator( new WDS(), args );
          Performance performance = evaluator.evaluate();
          System.out.println( performance );
        } // try
        catch ( Exception e ) {
          System.out.println( e.getMessage() );
          e.printStackTrace();
        } // catch
      } // WDS::main
}