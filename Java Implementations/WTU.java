import java.io.Serializable;
import java.util.ArrayList;

public class WTU extends Classifier implements Serializable, OptionHandler{

    protected Attributes attributes;
    protected Node root;
  
    public WTU()
    {
  
    }
  
    public WTU( String[] options ) throws Exception
    {
      setOptions(options);
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

    public void setOptions( String[] options ) throws Exception
    {
      super.setOptions(options);
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return null;
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

        if(att == -1)
        {
            Node leaf = new Node();
            leaf.label = ds.getMajorityClassLabel();

            return leaf;
        }

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

                if(branches.get(i).isEmpty())
                {
                    n.children.get(i).label = ds.getMajorityClassLabel();
                }
                else
                {
                    n.children.set(i, train_aux(branches.get(i)));
                }
            }
            n.attribute = att;

            return n;
        }
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
      } // WTU::main
}