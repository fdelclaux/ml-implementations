import pandas as pd
from sklearn.model_selection import train_test_split
from typing import Optional
import numpy as np

from statistics import mean, mode


class DataSet:

    def __init__(
        self,
        df: Optional[pd.DataFrame] = None,
        description: Optional[str] = 'raw', 
        attributes: Optional[dict] = None
    ):
        """
            CLASS CONSTRUCTOR
        """
        self.df = df
        self.description = description
        self.attributes = attributes

        # if self.attributes == None:

        #     self.attributes = dict()

        #     self.update_attributes()



    def update_attributes(self):

        for col in self.df.columns:

            uniques = self.df[col].unique()

            # if '?'.encode() in uniques:
            #     np.delete(uniques, np.where(uniques == '?'.encode() ) )

            self.attributes[col] = uniques
    

    def traintestsets(self, train_size = 0.9, test_size = 0.1):
        """
            Returns: Two dataframes, a train set containing 90% of values, a test set with 10% of the values
        """

        total_size = self.df.shape[0]

        splice = total_size * ( test_size / (train_size + test_size))

        folds = 1 / (test_size / (train_size + test_size))

        sets = []

        for i in range(int(folds)):

            sets.append(self.df[int(i*splice):int((i+1)*splice)])

        return sets

    def decode(self):

          for i in self.df.columns:
            if not pd.api.types.is_numeric_dtype(self.df[i]):
                self.df[i] = self.df[i].str.decode('utf-8')

    def fill_missing_values(self, byClass = True):
        """
            Definition: Fills in missing numeric values and missing categorical variables for DataFrame with mean and mode for each attribute respectively
        """
        for i in self.df.columns:
            if pd.api.types.is_numeric_dtype(self.df[i]):
                if byClass:
                    class_means = self.df.groupby('class')[i].mean()
                    for index, value in class_means.items():
                        self.df[i].mask(self.df[i] == '?',class_means[index], inplace = True)
                else:
                    mean = self.df[i].mean()
                    self.df[i].mask(self.df[i] == '?', mean, inplace = True)
            else:
                if byClass:
                    class_modes= self.df.groupby('class')[i].agg( lambda x: x.value_counts().idxmax())
                    for index, value in class_modes.items():
                        self.df[i].mask(self.df[i] == '?', class_modes[index], inplace = True)
                else:
                    
                    mode = self.df[i].mode()[0]
                
                    self.df[i].mask(self.df[i] == '?', mode, inplace = True)




    def map_discretization(self, discretized_df):

        for column in discretized_df.columns:

            if discretized_df[column].dtypes == 'category':

                bins = pd.IntervalIndex.from_tuples(discretized_df[column].unique().categories.to_tuples())

                self.df.loc[:,(column)] = pd.cut(self.df[column], bins = bins)

        # self.update_attributes()


    def entropy(self, sorted_values):
        """
            Definition: Calculates entropy of given sorted list and returns it.
        """

        counts = pd.Series(sorted_values).value_counts(normalize= True)

        entropy = -(counts * np.log(counts)).sum()

        return entropy


    def entropy_discretization_helper(self, attribute, sorted_values, threshold):
        """
            Definition: Recursive discretization helper. Calculates entropies for all intermediate points and selects the minimum. Calls recursively until threshold is met
        """
        breaks = []
        total = sorted_values[1].size

        values = sorted_values[0]
        class_labels = sorted_values[1]

        if total == 1:
            breaks.append(values[0])

        minimum_entropy = 1
        minimum_entropy_index = 0

        for index, row in enumerate(values, start=1):
           
            entropy_value = ((index)/total)* ( self.entropy( class_labels[:index] ) ) + ( ( (total-index) / total) * (self.entropy(class_labels[index:])))

            if entropy_value < minimum_entropy:
                minimum_entropy = entropy_value
                minimum_entropy_index = index

            if minimum_entropy < threshold:
                breaks.append(values[minimum_entropy_index])
                return breaks


        breaks.extend(self.entropy_discretization_helper(attribute, np.array([values[minimum_entropy_index:], class_labels[minimum_entropy_index:]]), threshold))
        breaks.extend(self.entropy_discretization_helper(attribute, np.array([values[:minimum_entropy_index], class_labels[:minimum_entropy_index]]), threshold))

        return breaks
    


    def discretize(self, attribute,  method, threshold = 0.3):
        """
            Definition: Convert continous variables into categorical ones
        """

        if not pd.api.types.is_numeric_dtype( self.df[ attribute ] ):
            raise Exception("The attribute you are trying to discretize is not numeric. Please try another attribute.")

        column = self.df[ [attribute, 'class'] ]

        sorted_values = column.sort_values( by = [ attribute ], ignore_index = True )

        sorted_values = sorted_values.to_numpy().T

        breaks = []

        if method == 'entropy':

            breaks.extend( self.entropy_discretization_helper(attribute, sorted_values, threshold) )

            breaks.sort()

            breaks.insert(0,0)

            breaks.append(float("inf"))
        
            self.df[attribute] = pd.cut(self.df[attribute], bins = breaks)


        elif method == 'binning':

            self.df[attribute] = pd.qcut(self.df[attribute], q = 10, duplicates = 'drop')

        # self.update_attributes()

    
    def normalize(self, attribute, method = 'z-score'):
        """
            Description: Normalize values for numeric attribute of dataframe

            Parameters:
                attribute: the attribute you want to normalize
                method: [`z-score`, `min-max`, `decimal`] Select method of normalization for this attribute

        """

        if not pd.api.types.is_numeric_dtype(self.df[attribute]):
            raise Exception("The attribute you are trying to discretize is not numeric. Please try another attribute.")

        if method == 'z-score':

            mean = self.df[attribute].mean()
            std = self.df[attribute].std()

            if std == 0:
                std = 1

            self.df[attribute] = self.df[attribute].apply(lambda x: (x - mean) / std)
        
        else:

            raise ValueError('The provided method does not exist.')


    def encode(self):
        """
            Definition: Encodes dataframe by transforming categorical attributes into hot one encoded representation of values as columns

        """

        self.df = self.df.reset_index(drop=True)

        df_dict = self.df.to_dict('records')

        columns = []

        for attribute in self.df.columns:

            if not pd.api.types.is_numeric_dtype( self.df[ attribute ] ):

                if attribute == 'class':
                    continue

                for value in self.attributes[attribute]:
                    columns.append(value)
            else:
                columns.append(attribute)

        encoded_df = pd.DataFrame(columns = columns)

        uniques = self.attributes['class']

        for row in df_dict:

            encoded_row = dict()

            for attribute in self.df.columns:

                if attribute == 'class':
                    encoded_row['class'] = uniques.index(row['class'])
                    continue

                if not pd.api.types.is_numeric_dtype( self.df[ attribute ] ):

                    encoded_row[row[attribute]] = 1

                else:
                    encoded_row[attribute] = row[attribute]

            # print(encoded_row)

            encoded_df = encoded_df.append(encoded_row, ignore_index=True)

        class_col = encoded_df['class']
        encoded_df.drop('class', axis = 1, inplace=True)
        encoded_df['bias']= -1.0
        encoded_df = pd.concat([encoded_df, class_col], axis = 1)

        encoded_df.fillna(0.0, inplace=True)

        # print(encoded_df)

        self.df = encoded_df

               


        


                







        
