// BeetleDirect: Servlet class of text download utility for tables in BeetleAtlas
// DPL 15.12.2025

import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BeetleDirect extends HttpServlet 
{
	private TissueCatalogue  tCat;		// stores info about all beetle tissues and stages: 
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException 
	{			
		// capture and deal with parameters
		String geneID = req.getParameter("geneID");				// Parameter to specify Beetle gene ID
		String tableOut = req.getParameter("tableOut");			// Parameter to specify whether "gene" or "transcript" table(s)	
		// handle nulls
		if(geneID == null)
		{
			geneID = "";
		}
		if(tableOut == null)
		{
			tableOut = "";
		}		
		// To prevent cross-site scripting, accept only letters or numbers
		geneID = geneID.replaceAll("[^a-zA-Z0-9]", "");
		tableOut = tableOut.replaceAll("[^a-zA-Z]", "");
		
		// Output filename
		String filename = new String();
		if(geneID != "" && tableOut.equals("gene"))
		{
			filename = geneID +  "G.txt";
		}
		else if(geneID != "" && tableOut.equals("transcriptGene"))
		{
			filename = geneID +  "T.txt";
		}
		else {filename = "error.txt";}
		
		// Set Content type
		res.setContentType("text/plain;charset=UTF-8");
		res.setCharacterEncoding("UTF-8");
		res.setHeader("Cache-Control", "no-cache");			
		// For forcing download 
		res.addHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");	
		// Set security headers
		res.setHeader("X-Frame-Options", "deny");
		res.setHeader("X-Content-Type-Options", "nosniff");
		res.setHeader("X-XSS-Protection", "1; mode=block");		
		PrintWriter writer = res.getWriter();		
			
		tCat = new TissueCatalogue();
		if(!tableOut.equals(""))
		{
    		GeneSearch search = new GeneSearch(geneID, "geneID", tCat);	
    		Gene gene = search.getGene();
    		Expression express = new Expression();
    		if(tableOut.equals("gene") || tableOut.equals("transcriptGene"))
    		{
    			express = search.getExpression();
    		}
    		
    		if(tableOut.equals("gene") && gene != null && express != null)
    		{
    			writer.println(gene.getGeneInfoText());  
       			writer.println(getGeneTable(gene, express));
    		}
    		else if(tableOut.equals("transcriptGene") && gene != null && express != null)
    		{
    			writer.println(gene.getGeneInfoText());   			
    			writer.println(getTranscriptTable(gene, express, tCat));
    		}
    		else{writer.println("An error has occurred.");}
		}
		else{writer.println("Invalid parameters.");}
		writer.close();		
	}
	
	// Construct tab-separated table of gene results: FPKMs and enhancements, including SD
	private String getGeneTable(Gene gene, Expression expression)
	{	
		GeneExpression express = (GeneExpression) expression;
		GeneTissueDataSet dataset = express.getGeneData();			
		
		StringBuilder sb = new StringBuilder();
		// Write two header lines for numerical data
		sb.append("\tAdult\t\t\tLarval\n");	
		sb.append("Tissue\tFPKM\tSD\tEnrichment\tFPKM\tSD\tEnrichment\n");	
		
		// Write each table row in order specified in tCat object's TissueDoublet.thisDoublet list
		int nonrefCount = 0; 	// counter for included rows — i.e. those other than reference pairs
		for (int i=0; i < tCat.getDoubletListSize(); i++)
		{
			TissueDoublet thisDoublet = tCat.getTissueDoublet(i);		
			String nextUniTissueName = thisDoublet.getUniTissueName();		// Cell1: next non-reference uniTissue in list
			if(nonrefCount > 0)
			{
				sb.append("\n" + nextUniTissueName);
			}
			else
			{
				sb.append(nextUniTissueName);
			}
			
			// ADULT //
			Tissue adultTissue = tCat.getTissueDoublet(i).getAdultTissue();				
			if(adultTissue == null)	// if not present write empty cells 2 and 3 
			{
				sb.append("\t-\t-\t-");
			}
			else					// Get appropriate GeneTissueData object and build table columns
			{
				GeneTissueData adultData = dataset.getGeneTissueDataByID(adultTissue.getTissueID());				
				if(adultData != null)
				{	
					sb.append("\t" + PageUtility.formatValues(adultData.getFPKM()) + "\t"  +  PageUtility.formatValues(adultData.getSD()));
					double enrich = adultData.getEnrichment();
					if(enrich > 0)
					{
						sb.append("\t" +  PageUtility.formatValues(adultData.getEnrichment()));	
					}	
					else
					{
						sb.append("\tN.A.");	
					}
				}
			}										
			
			// LARVAL //
			Tissue larvalTissue = tCat.getTissueDoublet(i).getLarvalTissue();			
			if(larvalTissue == null)	// if not present write empty cells 2 and 3 
			{
				sb.append("\t-\t-\t-");
			}
			else					// Get appropriate GeneTissueData object and build table columns
			{
				GeneTissueData larvalData = dataset.getGeneTissueDataByID(larvalTissue.getTissueID());			
				if(larvalData != null)
				{	
					sb.append("\t" +  PageUtility.formatValues(larvalData.getFPKM()) + "\t"  +   PageUtility.formatValues(larvalData.getSD()));
					sb.append("\t"  +  PageUtility.formatValues(larvalData.getEnrichment()));	
				}
			}
			nonrefCount++;
		}
		
		// EMBRYO //
		// Write blank line and then two header lines for numerical data
		sb.append("\n\n");	
		sb.append("\tEmbryo\n");	
		sb.append("Stage\tFPKM\tSD\n");	
		// Write data
		for(int i=0; i<tCat.getEmbryoListSize(); i++)
		{
			Tissue embryoTissue = tCat.getEmbryoTissue(i);
			GeneTissueData embryoData = dataset.getGeneTissueDataByID(embryoTissue.getTissueID());			
			if(embryoData != null)
			{	
				sb.append(ndashToHyph(embryoTissue.getAge()));
				sb.append("\t" +  PageUtility.formatValues(embryoData.getFPKM()) + "\t"  +   PageUtility.formatValues(embryoData.getSD()));
				sb.append("\n");
			}
		}
		
		return sb.toString();
	}
		
	private String getTranscriptTable(Gene gene, Expression expression, TissueCatalogue tCat)
    {
		GeneExpression express = (GeneExpression) expression;
		
    	StringBuilder sb = new StringBuilder(); 
    	
    	// Header with name of transcript
    	sb.append("\t\t");						// Just tab out 
		for(int w=0; w<express.getTranscriptDataSize(); w++)				// for each transcript (if >1)
		{
			String transID = express.getTranscriptData(w).getTranscriptID();
			sb.append(gene.getTranscriptByID(transID).getIDSuffix() + "/");	
			sb.append(transID + "\t\t");
		}
		sb.append("\n");
		
		// Data column headings
    	sb.append("\t\t");
		for(int z=0; z<express.getTranscriptDataSize(); z++)
		{
			sb.append("FPKM\tSD\t");
		}   
		sb.append("\n");
   	
		// Data for each tissue
		for (int i=0; i<tCat.getTissueListSize(); i++)
		{
			if(!tCat.getTissue(i).getStage().equals("Embryo"))
			{
				sb.append(tCat.getTissue(i).getStage() + "\t" + tCat.getTissue(i).getTissueName() + "\t");	//  stage and name columns
			}
			else
			{
				sb.append(tCat.getTissue(i).getStage() + "\t" + ndashToHyph(tCat.getTissue(i).getAge()) + "\t");			//  stage and age columns				
			}			
			int tissID = tCat.getTissue(i).getTissueID();
			for(int k=0; k<express.getTranscriptDataSize(); k++)
			{
				TranscriptTissueDataSet ttds = express.getTranscriptData(k);
				TranscriptTissueData ttd = ttds.getTranscriptTissueDataByID(tissID);
				if(ttd != null)
				{
					sb.append(PageUtility.formatValues(ttd.getFPKM()) + "\t" + PageUtility.formatValues(ttd.getSD()) + "\t");
				}
				else
				{
					sb.append("-\t-\t");						
				}
			}
			sb.append("\n");			
		} 
    	return sb.toString();
    }
	
	// Goes through String and converts n dashes to hyphens
	private String ndashToHyph(String instring)
	{
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<instring.length(); i++)
		{	
			if(instring.substring(i, i+1).equals("–"))
			{
				sb.append("-");
			}
			else
			{
				sb.append(instring.substring(i, i+1));
			}
		}
		return sb.toString();
	}

}
