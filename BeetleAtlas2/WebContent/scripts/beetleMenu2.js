			// beetleMenu2.js - populates tissue list - DP Leader 23.06.2018 //
	
	// called on changing state of stage list to populate tissue list
	// Version with full path to servlet for use in other servlets

	// 20.19.2024 Version for BeetleAtlas2 specifies version so can work with common BeetleMenu servlet
	
	var qSelect;			// Selection object
	var qButton;			// button object
	
	function processData()
	{
		qSelect = document.getElementById("stage");			// stage select id
		qButton = document.getElementById("runButton");		// submit button id
		
		var selection = qSelect.value;	// ea option list item must have a "value=" for IE
		fetchTissues(selection);
	}
	
	var req;		// request object
  
  	// sends asynchronous query to servlet
	function fetchTissues(stg, vers)
	{
		if(stg == " --- Select a Stage --- ")	// clear tissues if stg set to default
		{
			resetList();
			if (qButton != null)
			{
				qButton.disabled = true;	// inactivate submit button
			}
		}
		else
		{
			// construct url with menuPopup servlet mapping and parameters for both stage and Version 2
			var url = "/BeetleMenu/index.html?stg=" + stg + "&vers=two";
			
			if (window.XMLHttpRequest)
			{ 
				req = new XMLHttpRequest(); 
			} 
			else if (window.ActiveXObject)
			{ 
				req = new ActiveXObject("Microsoft.XMLHTTP"); 
			} 
		
			req.open("Get",url,true); 
			req.onreadystatechange = handleStateChange; 
			req.send(null); 
		}
	}
  
  	// NB Must use different name from that  in other script
	function handleStateChange()
	{ 
		
		if (req.readyState==4)
		{ 			
			if (req.status == 200)
			{ 
				// var headers = req.getAllResponseHeaders(); // not used
				parseResults();
			} 
		}
	}
  
  	// parses xml returned to populate tissue 'select' (list)
	function parseResults()
	{			
		var results = req.responseXML;
		var tissue = null;
		var tissid = "";
		var tissdescrip = "";
		
		clearList();
		
		var tiss = results.getElementsByTagName("tiss");
					
		for(var i = 0; i < tiss.length; i++)
		{
			tissue = tiss[i];
			tissid = tissue.getElementsByTagName("tissid")[0].firstChild.nodeValue;
			tissdescrip = tissue.getElementsByTagName("tissdescrip")[0].firstChild.nodeValue;
			addOption(tissdescrip, tissid);
		}
		if (qButton != null)
		{
			qButton.disabled = false;		// enable submit button
		}
	}
   
   	// auxiliary function for adding <option>s to 'select' (list)
	function addOption(tissdescrip, tissid)
	{
		var opt = document.createElement("option");
		var textNode = document.createTextNode(tissdescrip);
		opt.appendChild(textNode);
		opt.value = tissid;
		
		document.getElementById("tissue").appendChild(opt);	
	}
  
  	// clears all entries from tissue list (before repopulation)
	function clearList()
	{
		var tissues = document.getElementById("tissue");
		while(tissues.childNodes.length > 0)
		{
			tissues.removeChild(tissues.childNodes[0]);
		}
	}
	
	// resets tissue list to default (i.e. when no stage selected)
	function resetList()
	{
		var tissues = document.getElementById("tissue");
		while(tissues.childNodes.length > 0)
		{
			tissues.removeChild(tissues.childNodes[0]);
		}
		
		var opt = document.createElement("option");
		var textNode = document.createTextNode(" --- First select a Stage --- ");		
		opt.appendChild(textNode);
		opt.value = 0;
		tissues.appendChild(opt);	
	}
