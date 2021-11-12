import numpy as np
from typing import Optional
import pandas as pd

class Evaluator:

    def __init__(
        self,
        actual: Optional[list] = None,
        predicted: Optional[list] = None,
        n: Optional[int] = 0,
        correct: Optional[int] = 0,
        class_labels: list = None,
        confusion_matrices: Optional[dict] = None
    ):
        """
            CLASS CONSTRUCTOR
        """
        self.actual = actual
        self.predicted = predicted
        self.n = n
        self.correct = correct
        self.class_labels = class_labels
        self.confusion_matrices = confusion_matrices

        self.init()

    
    def init(self):
        """
            Definition: Generate Confusion Matrix from actual and predicted values
        """

        self.actual = []
        self.predicted = []
    
        self.confusion_matrices = dict()

        for i in self.class_labels:
            self.confusion_matrices[i] = [[0,0] , [0,0]]



    def precision(self,confusion_matrix):

        try:

            return confusion_matrix[0][0] / ( confusion_matrix[0][0] + confusion_matrix[0][1])

        except Exception as e:

            return 0

    def recall(self,confusion_matrix):

        try:

            return confusion_matrix[0][0] / ( confusion_matrix[0][0] + confusion_matrix[1][0])

        except Exception as e:

            return 0

    def f1(self,confusion_matrix):

        try:

            precision = self.precision(confusion_matrix)

            recall = self.recall(confusion_matrix)

            return (2 * precision * recall ) / (precision + recall)

        except Exception as e:

            return 0


    def micro_f1_score(self):
        """ 
            Definition: Calculates and returns micro f1_score, and stores it as local variables for evaluator.
        
        """

      

        micro_precision = self.micro_precision()
        micro_recall = self.micro_recall()

        micro_f1 = (2 * micro_precision * micro_recall) / (micro_precision + micro_recall)

        return micro_f1

        

    def macro_f1_score(self):
        """ 
            Definition: Calculates and returns macro f1_score, and stores it as local variables for evaluator.
        
        """

        f1s = [] 

        for key in self.confusion_matrices:

            f1 = self.f1(self.confusion_matrices[key])

            f1s.append(f1)

        return sum(f1s)/len(f1s)

          

    def micro_precision(self):
        """ 
            Definition: Calculates and returns micro precision, and stores it as local variables for evaluator.
        
        """

        true_positives = 0
        false_positives = 0

        for key in self.confusion_matrices:

            true_positives = true_positives + self.confusion_matrices[key][0][0]
            false_positives = false_positives + self.confusion_matrices[key][0][1]

        micro_precision = true_positives / (true_positives + false_positives)

        return micro_precision


    def macro_precision(self):
        """ 
            Definition: Calculates and returns macro precision, and stores it as local variables for evaluator.
        
        """

        precisions = []

        for key in self.confusion_matrices:

            precision = self.precision(self.confusion_matrices[key])

            precisions.append(precision)

        macro_precision = sum(precisions) / len(precisions)

        return macro_precision      

    def micro_recall(self):
        """ 
            Definition: Calculates and returns micro recall, and stores it as local variables for evaluator.
        
        """

        true_positives = 0
        false_negatives = 0

        for key in self.confusion_matrices:

            true_positives = true_positives + self.confusion_matrices[key][0][0]
            false_negatives = false_negatives + self.confusion_matrices[key][1][0]

        micro_recall = true_positives / (true_positives + false_negatives)

        return micro_recall

    def macro_recall(self):
        """ 
            Definition: Calculates and returns macro recall, and stores it as local variables for evaluator.
        
        """


        recalls = []

        for key in self.confusion_matrices:

            recall = self.recall(self.confusion_matrices[key])

            recalls.append(recall)

        macro_recall = sum(recalls) / len(recalls)

        return macro_recall            


    def accuracy(self):
        """ 
            Definition: Calculates and returns macro and micro accuracy, and stores them as local variables for evaluator.
        
        """

        return self.correct/self.n


    def add_prediction( self, actual , predicted ):
        """
            Definition: Adds prediction to evaluator, and confusion matrix.
        """
        
        for key in self.confusion_matrices:


            if actual == predicted:

                self.correct = self.correct + 1

                self.n = self.n + 1

                self.confusion_matrices[predicted][0][0] = self.confusion_matrices[predicted][0][0] + 1
                
                for key in self.confusion_matrices:

                    if not key == predicted:

                        self.confusion_matrices[key][1][1] = self.confusion_matrices[key][1][1] + 1

            else:

                self.n = self.n + 1
                self.confusion_matrices[predicted][0][1] =  self.confusion_matrices[predicted][0][1] + 1
                self.confusion_matrices[actual][1][0] = self.confusion_matrices[actual][1][0] + 1

                for key in self.confusion_matrices:

                    if not (key == predicted or key == actual) :

                        self.confusion_matrices[key][1][1] = self.confusion_matrices[key][1][1] + 1 

        self.actual.append(actual)
        self.predicted.append(predicted)


    def add( self, actual: pd.Series, predicted: pd.Series):

        for index, value in enumerate(actual):
            self.add_prediction(actual[index], predicted[index])

        