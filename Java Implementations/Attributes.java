import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

/*
 * Attributes.java
 * Copyright (c) 2018 Mark Maloof.  All Rights Reserved.  See LICENSE.
 */

public class Attributes {

  private ArrayList<Attribute> attributes = new ArrayList<Attribute>();
  private boolean hasNumericAttributes;
  private int classIndex;

  public Attributes() {
    classIndex = 0;
    hasNumericAttributes = false;
  }

  public void add(Attribute attribute) {
    attributes.add(attribute);
  }

  public int getClassIndex() {
    return classIndex;
  }

  public boolean getHasNumericAttributes() {
    return hasNumericAttributes;
  }

  public Attribute get(int i) {
    return attributes.get(i);
  }

  public Attribute getClassAttribute() {
    return attributes.get(classIndex);
  }

  public int getIndex(String name) throws Exception {

    for(int i = 0; i < attributes.size();i++)
    {
      if(attributes.get(i).name.equals(name))
        return i;
    }

    throw new Exception("No attribute with given name.");
    
  }

  public int size() {
    return attributes.size();
  }

  public void parse(Scanner scanner) throws Exception {
    while (scanner.hasNext("@attribute")) {

      Attribute addMe = AttributeFactory.make(scanner);

      if(addMe instanceof NumericAttribute)
      {
        hasNumericAttributes = true;
      }
      
      attributes.add(addMe);
    }
    classIndex = attributes.size() - 1;
  }

  public void setClassIndex(int classIndex) throws Exception {
    if (classIndex > attributes.size()) {
      throw new Exception("Index out of bounds.");
    }

    this.classIndex = classIndex;
  }

  public String toString() {
    StringBuilder s = new StringBuilder();
    for (int i = 0; i < attributes.size(); i++) {
      s.append(attributes.get(i).toString() + "\n");
    }

    return s.toString();
  }

  public static void main( String args[] ) throws Exception {

    Scanner scanner = new Scanner (new BufferedReader( new FileReader( "bikes_test.mff" ) ));

    while (scanner.hasNext("@attribute")) {

      Attribute a = AttributeFactory.make(scanner);

      System.out.println(a.toString());
    }

    scanner.close();
  }
}
