import pandas as pd
import numpy as np
from typing import Optional 
import math

from dataset import DataSet
from evaluator import Evaluator

class hiddenLayer:

    def __init__(
        self,
        size = 10,
        previousLayerSize = 10,
        nodes: Optional[np.array] = None,
        weights: Optional[np.array] = None
    ):
        """
            Definition: Class Constructor
        """

        self.size = size
        self.previousLayerSize = previousLayerSize
        self.nodes = nodes
        self.weights = weights

        self.init_weights()

    def init_weights(self):

        self.weights = np.random.uniform(low = -0.5, high = 0.5, size = ( self.size, self.previousLayerSize))

        

class BackProp:

    def __init__(
        self,
        hiddenLayersCount: Optional[int] = 1,
        hiddenLayerSizes: Optional[list] = None, 
        learningRate: Optional[float] = 0.9,
        hiddenLayers: Optional[list] = None,
        outputLayerWeights: Optional[np.array] = None,
        minimumError: Optional[float] = 0.10,
        maxEpochs: Optional[int] = 50000,
    ):
        """
            Defintion: Class Constructor
        """

        self.hiddenLayerSizes = hiddenLayerSizes
        self.hiddenLayersCount = hiddenLayersCount
        self.learningRate = learningRate
        self.hiddenLayers = hiddenLayers
        self.outputLayerWeights = outputLayerWeights
        self.minimumError = minimumError
        self.maxEpochs = maxEpochs


    def init_weights(self, inputLayerNodes, outputLayerNodes):
        """
            Definition: Initializes the weights of all matrices between input, hidden and output layers.

            Parameters:
                - inputLayerNodes: Number of nodes in the input layer
                - outputLayerNodes: Number of nodes in the output layer

        """
        for i in range(self.hiddenLayersCount):

            if i == 0:
                self.hiddenLayers.append( 
                    hiddenLayer(
                        size = self.hiddenLayerSizes[i], 
                        previousLayerSize = inputLayerNodes, 
                        nodes = np.zeros(self.hiddenLayerSizes[i]),
                        weights = np.random.rand(self.hiddenLayerSizes[i], inputLayerNodes)
                    ) 
                )
                self.hiddenLayers[i].nodes[ self.hiddenLayerSizes[i] - 1 ] = -1.0 #bias

            else:
                self.hiddenLayers.append(
                    hiddenLayer(
                        size = self.hiddenLayerSize[i],
                        previousLayerSize = self.hiddenLayerSize[i-1],
                        nodes = np.zeros(self.hiddenLayerSize[i]),
                        weights = np.random.rand( self.hiddenLayerSize[i], self.hiddenLayerSize[i-1] )
                    )
                )
                self.hiddenLayers[ i ].nodes[ self.hiddenLayerSize[i] - 1 ] = -1.0 #bias

        self.outputLayerWeights = np.random.uniform(low = -0.5, high = 0.5, size = ( outputLayerNodes, self.hiddenLayerSizes[ self.hiddenLayersCount - 1 ] ) )



    def feed_forward(self, row, outputLayerNodes):
        """
            Definition: Feeds an example (row) through all layers of Neural Network and returns the output

            Parameters:
                - row: example to be fed forward
                - outputLayerNodes: Number of nodes in the output layer

            Returns:
                - output: Output produced by network

        """
        layerCount = 0

        output = np.zeros( outputLayerNodes )

        for hiddenLayer in self.hiddenLayers:

            if layerCount == 0:

                for i in range(hiddenLayer.size - 1):

                    hiddenLayer.nodes[i] = 1 / (1 + math.exp(-1 * np.dot( hiddenLayer.weights[i], row.to_numpy() ) ) )

            else:
                    for i in range(hiddenLayer.size - 1):

                        hiddenLayer.nodes[i] = 1 / (1 + math.exp(-1 * np.dot(hiddenLayer.weights[i], self.hiddenLayers[layerCount - 1].nodes)))

                    layerCount = layerCount + 1

        for index, node in enumerate(output):
                    
            output[index] = 1 / (1 + math.exp(-1 * np.dot( self.outputLayerWeights[index], self.hiddenLayers[ self.hiddenLayersCount - 1 ].nodes) ) )

        return output



    def calcualte_error_vectors(self, y, output):
        """
            Definition: Calculates error vecors between y and output

            Parameters:
                - y: encoded value of y
                - output: output from network

            Returns: 
                - deltah: list of error vectors, one per hidden layer
                - deltao: error vector for output layer
        """

        deltao = np.zeros( len(output) )
        deltah = []

        for index, value in enumerate(deltao):
                    
            deltao[index] = (y[index] - output[index]) * ( 1 - output[index] ) * output[index]

        for index, hiddenLayer in enumerate(reversed(self.hiddenLayers)):

            delta = np.zeros( self.hiddenLayerSizes[ self.hiddenLayersCount - 1 - index ] )

            for j, node in enumerate(delta):

                delta[j] = hiddenLayer.nodes[j] * (1 - hiddenLayer.nodes[j])

                sumOk = 0.0

                if index == 0:
                    for k, deltaValue in enumerate(deltao):
                        sumOk = sumOk + deltaValue * self.outputLayerWeights[k][j]
                            
                else:
                    for k, deltaValue in enumerate(deltah[0]):
                        sumOk = sumOk + deltaValue * hiddenLayer.weights[k][j]

                delta[j] = delta[j]*sumOk

            deltah.insert(0,delta)

        return deltah, deltao


    def adjust_weights(self, row, deltah, deltao):
        """
            Definition: Adjust's models weights 

            Parameters:
                - row: example from this epoch
                - deltah: hidden layers error vectors
                - deltao: output layer error vector

        """
        
        for k in range(len(deltao)):

            for j in  range( self.hiddenLayerSizes[self.hiddenLayersCount - 1] ):

                self.outputLayerWeights[k][j] = self.outputLayerWeights[k][j] + self.learningRate * deltao[k] * self.hiddenLayers[self.hiddenLayersCount - 1].nodes[j]

        for index, hiddenLayer in enumerate(self.hiddenLayers):

            for j in range(self.hiddenLayerSizes[index]):

                for k in range(row.size - 1):
 
                    if index == 0:

                        weight = self.learningRate * deltah[index][j] * row[k]

                    else:

                        weight = self.learningRate * deltah[index][j] * self.hiddenLayers[index-1].nodes[k]

                    hiddenLayer.weights[j][k] = hiddenLayer.weights[j][k] + weight

        

    def train(self, ds: DataSet):
        """
            Defintion: Trains model using Feed-Forward, Back-Propagation Neural Network on provided DataSet

            Parameters:
                - ds: Training DataSet

        """

        ds.encode()

        # print('hi')

        uniqueClasses = len(ds.attributes['class'])
        
        self.init_weights(inputLayerNodes = len(ds.df.columns) - 1, outputLayerNodes =  uniqueClasses )

        epoch = 0

        while True:

            error = 0
            example = 1

            df_dict = ds.df.to_dict('records')

            for row in df_dict:

                y = np.zeros( uniqueClasses, dtype = int )

                y[ int(row['class']) ] = 1
 
                output = self.feed_forward( pd.Series(row).drop('class'), uniqueClasses )

                # print(f'prediction: {output} , actual: {y}')

                for index, node in enumerate(output):

                    error = error + (0.5)*pow(y[index] - output[index], 2.0)

                deltah, deltao = self.calcualte_error_vectors(y, output)

                self.adjust_weights( pd.Series(row), deltah, deltao )

                example = example + 1

            epoch = epoch + 1

            if epoch == 1:
                print(f'Error after first epoch: {error}')
                print(f'Error at output nodes: {deltao}')

            if error < self.minimumError or epoch > self.maxEpochs:
                print(f'Error after last epoch: {error}')
                print(f'Error at output nodes: {deltao}')
                print(f'Epoch: {epoch}')
                break



    def classify(self, ds: DataSet):
        """
            Definition: Classifies a DataSet

            Parameters:
                - ds: test data

            Returns:
                - evaluator: Evaluator object holding confusion matrices for model's performance

        """

        ds.encode()

        evaluator = Evaluator( class_labels = ds.df['class'].unique() )

        outputLayerNodes = len(ds.attributes['class'])

        for index, row in ds.df.iterrows():

            output = self.feed_forward(row.drop('class'), outputLayerNodes)

            result = np.argmax(output)
            
            evaluator.add_prediction(result, row['class'])

        return evaluator
