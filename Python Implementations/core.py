from scipy.io import arff
import argparse

import pandas as pd
import numpy as np
from  dataset import DataSet
from evaluator import Evaluator
from BP import BackProp

def load_arff(filename):

    raw_data, meta = arff.loadarff( filename )

    return pd.DataFrame(raw_data), meta

def main():

    parser = argparse.ArgumentParser()

    parser.add_argument('-l', '--learning-rate', dest = "learningRate", help = "learning-rate", type = float, required = True)
    parser.add_argument('-n', '--hidden-layer-nodes', dest = "hiddenLayerNodes", help = "hidden-layer-nodes", type = int, required = True)
    parser.add_argument('-m', '--minimum-error', dest = "minimumError", help = "minimum-error", type = float, required = True)
    parser.add_argument('-e', '--epochs', dest = "epochs", help = "epochs", type = int, required = True)
    parser.add_argument('-x', '--hidden-layers', dest = "hiddenLayers", help = 'hidden-layers', type = int, required = False)

    args = parser.parse_args()

    df, meta = load_arff('adult-tiny.arff')

    attributes = dict()

    for col in meta:
        attributes[col] = meta[col][1]

    dataset = DataSet(df, attributes) 

    dataset.decode()  

    traintestsets = dataset.traintestsets(train_size = 0.8, test_size = 0.2)

    evaluators = []

    # 10-fold cross validation
    # --------------------------------------------------------

    for index, partition in enumerate(traintestsets):

        # Build train and test
        # ----------------------------------------------------
        
        train_sets = traintestsets.copy()

        train_sets.pop(index)

        trainset = DataSet ( pd.concat(train_sets), 'train' , attributes = attributes )

        testset = DataSet ( partition.copy(), 'test' , attributes = attributes )


        # pre process train
        # ----------------------------------------------------

        trainset.fill_missing_values()

        for column in trainset.df.columns:

            if pd.api.types.is_numeric_dtype( trainset.df[column] ):

                trainset.normalize( attribute = column )

        # for column in trainset.df.columns:

        #     if not pd.api.types.is_numeric_dtype( trainset.df[column] ):

        #         print(trainset.df[column].value_counts())
            
        # pre process test
        # ----------------------------------------------------

        testset.fill_missing_values(byClass = False)

        for column in testset.df.columns:

            if pd.api.types.is_numeric_dtype( testset.df[column] ):

                testset.normalize( attribute = column )

        # print(trainset.df)

        # NN

        if args.hiddenLayers == None:

            hiddenLayersCount = 1

        else:

            hiddenLayersCount = args.hiddenLayers
            
            
        hiddenLayerSizes = [args.hiddenLayerNodes] * hiddenLayersCount

        # print(hiddenLayerSizes)

        bp = BackProp( 
            hiddenLayerSizes = hiddenLayerSizes, 
            hiddenLayersCount = hiddenLayersCount, 
            learningRate = args.learningRate,  
            hiddenLayers = [], 
            minimumError = args.minimumError,
            maxEpochs = args.epochs
        )

        
        bp.train(trainset)

        evaluator = bp.classify(testset)

        print( f'Accuracy: {evaluator.accuracy()}')
        print( f'Macro F1: {evaluator.macro_f1_score()}' )
        print( f'Micro F1: {evaluator.micro_f1_score()}' )

        evaluators.append(evaluator)

    average_macro_f1 = 0.0
    average_micro_f1 = 0.0

    for evaluator in evaluators:

        average_macro_f1 = average_macro_f1 + evaluator.macro_f1_score()
        average_micro_f1 = average_micro_f1 + evaluator.micro_f1_score()

    print(f"Average Macro F1: {average_macro_f1/len(evaluators)}")
    print(f"Average Micro F1: {average_micro_f1/len(evaluators)}")


if __name__ == "__main__":
    main()

