import java.util.Scanner;

/*
 * AttributeFactory.java
 * Copyright (c) 2018 Mark Maloof.  All Rights Reserved.  See LICENSE.
 */

public class AttributeFactory extends Object{
  
	public static Attribute make( Scanner scanner ) throws Exception
	{
		scanner.next(); // takes @attribute tag

		String name = scanner.next(); // takes in name

		boolean isNumeric = scanner.hasNext("numeric");

		if(isNumeric)
		{
			scanner.next(); // skip 'numeric'
			return new NumericAttribute(name);
		}
		else{
			NominalAttribute newAt = new NominalAttribute(name);
			while(scanner.hasNext() && !scanner.hasNext("@attribute") && !scanner.hasNext("@examples"))
			{
				newAt.addValue(scanner.next());
			}

			return newAt;
		}
		
	}
}
