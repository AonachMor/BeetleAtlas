 /*
Class to hold HTML code and write specific HTML menus 
for different pages of BeetleAtlas
Also contains various other utilities, most of which can be accessed in a static manner
DPL 10.10.2019
Updated for BeetleDirect2 to remove unneeded methods etc.
*/

import java.text.NumberFormat;

public class PageUtility
{		
	// Constants to allow safer comparisons
	final static String ADULT = "Adult";
	final static String LARVAL = "Larval";
	
	////////////////// GENERAL UTILITY METHODS /////////////////////////////
	
	// Legacy method for Enrichments — calls two parameter method with decDigits = 1
	public static String formatValues(double value)
	{	
		return formatValues(value, 1);
	}
	
	// returns a String value with visually appropriate number of decimal places (not strict num sig figs)
	public static String formatValues(double value, int decDigits)
	{
		NumberFormat N = NumberFormat.getInstance();
		N.setGroupingUsed(false);		// no comma separators for thousands (mainly single digit thous which shouldn't have them)
		if(value < 1.0)
		{
				N.setMaximumFractionDigits(decDigits);
				N.setMinimumFractionDigits(decDigits);
		}
		else if(value < 2.0)
		{
				N.setMaximumFractionDigits(decDigits);
				N.setMinimumFractionDigits(decDigits);
		}
		else if(value < 10.0)
		{
				N.setMaximumFractionDigits(1);
				N.setMinimumFractionDigits(1);
		}
		else
		{
			N.setMaximumFractionDigits(0); 
			N.setMinimumFractionDigits(0);
		}	
		return N.format(value);
	}
	
	// Checks for [+] indication of superscript. If present, marks up for HTML
	public static String checkSuper(String name)
	{
		if(name.indexOf("[+]") != -1)
		{
			int start = name.indexOf("[+]");
			name = name.substring(0, start) + "<sup>+</sup>" + name.substring(start+3);
			// check for second case as in Na[+]/H[+]
			if(name.indexOf("[+]") != -1)
			{
				start = name.indexOf("[+]");
				name = name.substring(0, start) + "<sup>+</sup>" + name.substring(start+3);
			}
		}
		else if(name.indexOf("[2+]") != -1)
		{
			int start = name.indexOf("[2+]");
			name = name.substring(0, start) + "<sup>2+</sup>" + name.substring(start+4);			
		}
		return name;
	}
	
}
