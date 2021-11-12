import java.util.Random;

/*
 * Evaluator.java
 * Copyright (c) 2018 Mark Maloof.  All Rights Reserved.  See LICENSE.
 */

public class Evaluator implements OptionHandler{

  private long seed = 2026875034;
  private Random random;
  private int folds = 10;
  private double holdOut = 0.0;
  private Classifier classifier; // a copy of classifier with its parameters
  private TrainTestSets tts;


  public Evaluator()
  {
    random = new Random();
    random.setSeed(seed); 

    tts = new TrainTestSets();
  }

  public Evaluator( Classifier classifier, String[] options ) throws Exception
  {
    random = new Random();
    random.setSeed(seed); 

    tts = new TrainTestSets();

    this.classifier = classifier;
    this.setOptions(options);
    classifier.setOptions(options);
    tts.setOptions(options);
    
  }

  public Performance evaluate() throws Exception
  {
    Performance overallP = new Performance(tts.getTrainingSet().getAttributes());

    if(holdOut != 0.0 )//hold-out method
    {
      DataSet d1 = tts.getTrainingSet();
      tts = d1.getHoldOutSets(holdOut);

      Classifier copyClassifier = (Classifier) Utils.deepClone(this.classifier);

      copyClassifier.train(tts.getTrainingSet());

      overallP = copyClassifier.classify(tts.getTestingSet());

    }
    else if(tts.getTestingSet() == null) //preform k-fold cross validation
    {
        DataSet d1 = tts.getTrainingSet();
        d1.setFolds(folds);
        d1.setRandom(random);
        
        //partitions

        //deep clone classifier
        for(int i = 0;i < folds; i++)
        {
      
          Classifier copyClassifier = (Classifier) Utils.deepClone(this.classifier);

          tts = d1.getCVSets(i);
        
          copyClassifier.train(tts.getTrainingSet());

          Performance p = copyClassifier.classify(tts.getTestingSet());

          overallP.add(p);

        }
    }
    else{
      Classifier copyClassifier = (Classifier) Utils.deepClone(this.classifier);

      copyClassifier.train(tts.getTrainingSet());

      overallP = copyClassifier.classify(tts.getTestingSet());
      
    }

    //kfold cross validation

    return overallP;
  }

  public long getSeed()
  {
    return seed;
  }

  public void setOptions( String[] options ) throws Exception
  {
    char c;
    int a = 0;
    while ( a < options.length && options[a].charAt( 0 ) == '-' ) {
      c = options[a].charAt( 1 );
      switch ( c ) {
        case 'x': // folds
          folds = Integer.parseInt(options[++a]);
          a++;
          break;
        case 'p': //hold-out percentage
          holdOut = Double.parseDouble(options[++a]);
          a++;
          break;
        case 's': // set seed
          this.setSeed(Long.parseLong(options[++a]));
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

  public void setSeed( long seed )
  {
    this.seed = seed;
    random.setSeed(seed);
  }

}
