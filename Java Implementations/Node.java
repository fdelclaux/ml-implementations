import java.util.ArrayList;

/*
 * Node.java
 * Copyright (c) 2018 Mark Maloof.  All Rights Reserved.  See LICENSE.
 */

public class Node
{

  public int attribute = -1;
  public int label = -1;
  public ArrayList<Node> children = new ArrayList<Node>();

  Node()
  {

  }
  
  public boolean isLeaf()
  {
    return (children.size() == 0);
  }

  public boolean isEmpty()
  {
    return children.isEmpty();
  }

}
