/*
	Class to create SVG Image of Graph, 
	DPL 10.12.2013
	modified from BufferedImage/png version 07.08.2014
	Modified for BeetleAtlas 22.01.2021
	Renamed ImageCreator 23.02.2021
	Last modified 07.09.2021
*/		

import java.awt.*;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.dom.GenericDOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DOMImplementation;

public class ImageCreator
{
	String gene;			// name of gene
	int numTissues;			// number of tissues (embryo stages)
	String[] names;			// array of names of tissues (embryo stages)
	double[] fpkms;			// array of FPKM values
	double[] errors;		// array of associated SDs
	
	boolean hasTitle;		// flag for whether to include title (gene name) above graphic
	
	// fonts (apologies for specific names, but needed to ensure SVG can be loaded into Adobe Illustrator)
	private Font titleFt = new Font("arial", Font.BOLD,16);
	private Font axisFt = new Font("arial", Font.PLAIN,12);	
	
	// overall dimensions - these are currently hard coded - could code for number of conds.
	private int imgWidth = 500;
	private int imgHeight = 350;
	
	// layout gutters and spacings
	private int gutter = 20;	// minimum stand-off of graphics from edge of overall immage
	
	private int topSpace;		// space between top gutter and graph
	private int botSpace;		// space between bottom gutter and graph
	private int lSpace;			// space between left gutter and graph
	private int rSpace;			// space between right gutter and graph
	
	// graph dimensions
	int oriX;
	int oriY;
	int graphWidth;
	int graphHeight;
	
	int tickWidth = 5;			// hard-code width of tick on y axis
	
	// barchart bar dimensions
	int barWidth;		// width of bar
	int spaceWidth;		// width of space between bars - half of this at start and (poss) end
	
	// value variables
	int scaleMax;		// actual value of max number on scale. e.g = 7000 for maxFPKM of 5081.2 ± 1722 (TC015275)
	int scaleMaxDigit;	// first digit in scaleMax. e.g. = 7 for scaleMax of 7000
	int scalePower;		// power of 10 scaleMaxDigit must be multiplied by to get scaleMax. e.g. = 3 for scaleMaxDigit of 7 and scaleMax of 7000 

	double pxFPKMfactor;		// factor to convert FPKMs to pixels
	
	// Misc.
	Color barColour;	
	String htmlSVG;					// string with untrimmed SVG

	public ImageCreator(String gene, TissueCatalogue  tCat, double[] fpkms, double[] errors, boolean hasTitle)
	{	
		this.gene = gene;
		this.fpkms = fpkms;
		this.errors = errors;
		this.hasTitle = hasTitle;
		
		numTissues = tCat.getEmbryoListSize();
		names = new String[numTissues];	
		for(int i=0; i<numTissues; i++)
		{
			names[i] = tCat.getEmbryoTissue(i).getAge(); 	// Use age for name as all have TissueName "WholeBody"
		}
		
		// barColour = new Color(73, 136, 212);	// lighter blue to show error bars
		 barColour = new Color(169, 28, 34);
		
		// Get a DOMImplementation.
		DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
		
		// Create an instance of org.w3c.dom.Document.
		String svgNS = "http://www.w3.org/2000/svg";
		Document document = domImpl.createDocument(svgNS, "svg", null);
		
		// Create an instance of the SVG Generator.
		SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
		svgGenerator.setSVGCanvasSize(new Dimension(imgWidth, imgHeight));		// Should match image size 
		
		// Ask the class to render into the SVG Graphics2D implementation. 
		this.paint(svgGenerator);
		
		boolean useCSS = true;		// we want to use CSS style attributes
		Writer sw = new StringWriter();
		try
		{
			svgGenerator.stream(sw, useCSS);
		}
		catch (IOException io)
		{
			System.out.println(io.toString());
		}
		
		StringBuilder sb = new StringBuilder(sw.toString());
		htmlSVG = sb.toString();
	}
	
	// accessor method for final SVG text
	public String getSVG()
	{
		String trimmedSVG = trimSVG(htmlSVG);	// remove repeat of xml pragma
		String cleanSVG = cleanSVG(trimmedSVG);	// Dialog font to arial for export
		return cleanSVG;
	}
	
	// removes (repeat of) xml version and doctype from SVG text
	private String trimSVG(String untrimmedSVG)
	{
		int i = untrimmedSVG.indexOf("<svg xmlns");
		return untrimmedSVG.substring(i, untrimmedSVG.length());
	}
	
	// replaces generated Dialog font with arial to ensure readability in Adobe Illustrator
	private String cleanSVG(String dirtySVG)
	{
		return dirtySVG.replace("Dialog", "arial");
	}
	
	public void paint(Graphics2D g)
	{
        // Turn anti-aliasing on for text
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHints(hints);
        
        // fill area with white
        g.setColor(Color.white);
		g.fillRect(0, 0, imgWidth, imgHeight);
		
		drawFrame(g);
		if(hasTitle)
		{
			drawTitle(g);
		}
		drawGraph(g);
		drawYaxis(g);		
	}

	//------------------------ Subsidiary drawing methods ---------------------------//
			
	// draw rectangular frame of graph leaving gutter and text space
	private void drawFrame(Graphics g)
	{	
		calculateStandoff(g);
		
		oriX = gutter + lSpace;
		oriY = gutter + topSpace;	
		graphWidth = imgWidth - 2*gutter - lSpace - rSpace;
		graphHeight = imgHeight - 2*gutter - topSpace - botSpace;
		
		g.setColor(Color.black);
		g.drawRect(oriX, oriY, graphWidth, graphHeight);
		
		calculateFactor(graphHeight, scalePower, scaleMaxDigit);	// sep from orig calcScale
	}
	
	// Draws overall title: name of gene
	private void drawTitle(Graphics g)
	{
		g.setColor(Color.black);
		g.setFont(titleFt);
		FontMetrics fm = g.getFontMetrics(titleFt);
		String title = ( gene.substring(0,1).toUpperCase() 
						+ gene.substring(1, gene.length()));
		int xTitle = oriX + (graphWidth - fm.stringWidth(title))/2;			
		int yTitle = gutter + fm.getAscent();

		g.drawString( title, xTitle, yTitle);
	}
	
	// Draws bars for graph and x axis names
	private void drawGraph(Graphics g)
	{
		calculateBars();
			
		int barX = oriX + spaceWidth/2;
		for(int i=0; i<numTissues; i++)
		{
			// Draw rectangular bars
			g.setColor(barColour);
			g.fillRect(barX, oriY + (int) (graphHeight - fpkms[i]*pxFPKMfactor) + 1, barWidth, (int) (fpkms[i]*pxFPKMfactor) );
			g.setColor(Color.black);
			g.drawRect(barX, oriY + (int) (graphHeight - fpkms[i]*pxFPKMfactor) + 1, barWidth, (int) (fpkms[i]*pxFPKMfactor) );
			
			// Draw error markings
			g.setColor(Color.black);
			int errorMax = (int) ((fpkms[i] + errors[i])*pxFPKMfactor);
			int errorMin = (int) ((fpkms[i] - errors[i])*pxFPKMfactor);

			g.drawLine((barX + 3*barWidth/8), (oriY + graphHeight - errorMax), 
										(barX + 5*barWidth/8), (oriY + graphHeight - errorMax));		// top	
			g.drawLine((barX + 3*barWidth/8), (oriY + graphHeight - errorMin), 
										(barX + 5*barWidth/8), (oriY + graphHeight - errorMin));		// bottom
			g.drawLine((barX + barWidth/2), (oriY + graphHeight - errorMax), 
										(barX + barWidth/2), (oriY + graphHeight - errorMin));			// join
											
			// Draw legends (names)
			g.setFont(axisFt);
			FontMetrics fm = g.getFontMetrics(axisFt);
			int xName = barX + barWidth/2 - fm.stringWidth(names[i])/2;			
			int yName = imgHeight - gutter - fm.getDescent();
			
			g.setColor(Color.black);
			g.drawString( names[i], xName, yName);			
			
			// increment barX
			barX = barX + barWidth + spaceWidth;
		}
	}	
		
	// draw a suitable number of ticks along Y axis together with fpkms
	private void drawYaxis(Graphics g)
	{
		double stepSize = 0.0;	// spacing (*10-scalePower) - chosen arbitrarily
		
		if(scalePower == 0)
		{
			stepSize = 2;	// prevent too many ticks for 0–10 scale
		}
		else if(scaleMaxDigit>4)
		{
			stepSize = 1.0;
		}
		else if (scaleMaxDigit>1.5)
		{
			stepSize = 0.5;
		}
		else
		{
			stepSize = 0.2;
		}
		
		double pxFactor = graphHeight/scaleMaxDigit;
		int interval = (int) (stepSize*pxFactor);		// interval between ticks in pixels
		int numSteps = (int)	(scaleMaxDigit/stepSize) + 1;	// number of ticks (starting from 0)
		
		g.setColor(Color.black);
		for(int i=0; i< numSteps; i++)
		{
			// tick
			g.drawLine(oriX, (oriY+graphHeight - i*interval), oriX-tickWidth, (oriY+graphHeight - i*interval));

			String numString = String.valueOf((int)(stepSize*i*Math.pow(10, scalePower)));
			g.setFont(axisFt);
			FontMetrics fm = g.getFontMetrics(axisFt);
			int xNum = oriX - tickWidth*2 - fm.stringWidth(numString);
			int yNum = (oriY+graphHeight - i*interval) + fm.getAscent()/2;
			g.drawString(numString, xNum, yNum);
		}	
		
		// Y axis units text
		String fpkm = "FPKM";    		
		g.setFont(axisFt);			
		int xP = oriX - tickWidth*2 - lSpace;
		int yP = oriY + imgHeight/3;			// Arbitrary but seems to work	
		g.drawString(fpkm, xP, yP);				
	}


	//--------------------------- Utility calculation methods ------------------------------//	
		
	// calculates stand-off for frame to accommodate text
	private void calculateStandoff(Graphics g)
	{
		calculateScale();
		
		FontMetrics fmt = g.getFontMetrics(titleFt);
		topSpace = 20 + fmt.getAscent() + fmt.getDescent();
		
		FontMetrics fma = g.getFontMetrics(axisFt);
		botSpace = 20 + fma.getAscent() + fma.getDescent();	
		
		String leftNumeral = String.valueOf(scaleMax);	
		int leftWidth = fma.stringWidth(leftNumeral);
		int legendWidth = fma.stringWidth("FPKM");
		lSpace = leftWidth + legendWidth + 10 ;
		
		rSpace = 0;			// no extra space needed
	}
	
	// calculates width of bars and spaces wrt No conditions: max spacing - 2x bar width, min - 0
	private void calculateBars()
	{
		//int minBar = 50;
		int minBar = 35;	// This seems to be the determinant of bar width at the moment
		int maxBar = 75;
				
		if(numTissues*(3*maxBar) < graphWidth)	// need to use max for both
		{
			barWidth = maxBar;
			spaceWidth = 2*maxBar;
		}
		else
		{
			int testBar = graphWidth / (3*numTissues);
			if(testBar >= minBar)				// share in proportion if possible
			{
				barWidth = testBar;
				spaceWidth = 2*testBar;
			}
			else								// use minimum bar width and lower space
			{
				barWidth = minBar;
				spaceWidth = (graphWidth - numTissues*barWidth)/numTissues;
				if(spaceWidth < 0)					//	unlikely event of v.many conditions 
				{
					spaceWidth = 0;
					barWidth = graphWidth/numTissues;
				}
			}
		}
	}
	
	// Find highest bar and decide on what value to set as maximum, and hence set scale value
	private void calculateScale()
	{
		// Find maximum FPKM value of different tissues/embryo time points + error
		double maxFPKM = 0;
		for (int i=0; i<numTissues; i++)
		{
			double current = fpkms[i] + errors[i];
			if(current > maxFPKM)
			{
				maxFPKM = current;
			}
		}	
		
		// Find initial digit for max and power of 10
		boolean done = false;
		int digit = (int) maxFPKM;		// initial digit in max
		
		scalePower = 0;					// number of powers of 10 this digit is x by in max
		if(digit < 10)
		{		
			digit = 10;	// Sets minimum value for top scale
			done = true;
		}
		while (!done)
		{
			digit = digit/10;
			scalePower++;
			if(digit < 10)
			{
				done = true;
			}
		}
		
		// Make scaleMaxDigit 1 more than digit of max value
		scaleMaxDigit = digit + 1;		
		// for Y axis spacing
		scaleMax = (int) (scaleMaxDigit * Math.pow(10, scalePower));
	}	
	
	// calculates factor for converting fpkms to pixels — called after graphHeight calculated
	private void calculateFactor(int graphHeight, int scalePower, double scaleMaxDigit)
	{
		pxFPKMfactor = (double) graphHeight / (scaleMaxDigit * Math.pow(10, scalePower));
	}
	
}