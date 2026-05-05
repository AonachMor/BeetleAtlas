				/* beetle2.js 21.03.2026 */

// Writes start of (hidden) form, setting values of options
function startForm()
{	
	var form = document.createElement("form");
	form.setAttribute("method", "get");
	form.setAttribute("action", "");
	form.setAttribute("accept-charset", "UTF-8");
	
	// show SDs if user-selected
	if (document.getElementById('errors_0') 
			&& document.getElementById('errors_0').checked)
	{
		var hiddenErrorsField = document.createElement("input");
		hiddenErrorsField.setAttribute("type", "hidden");
		hiddenErrorsField.setAttribute("name", "errors");
		hiddenErrorsField.setAttribute("value", "errors");		// Because we have to send a String to set a boolean
		form.appendChild(hiddenErrorsField);
	}
	
	// show Whole Fly data, if user-selected
	if (document.getElementById('whole_0') 
			&& document.getElementById('whole_0').checked)
	{
		var hiddenWholeField = document.createElement("input");
		hiddenWholeField.setAttribute("type", "hidden");
		hiddenWholeField.setAttribute("name", "whole");
		hiddenWholeField.setAttribute("value", "whole");		// Because we have to send a String to set a boolean
		form.appendChild(hiddenWholeField);
	}
		
	return form;
}

		// Create hidden submission forms for different queries and appropriate pages //
		
function sendSearchGeneForm2() 
{
	var gene = document.getElementById('inputField').value;
	// idtype handled separately because of smart check of gene value

	if(gene=="")
	{
		alert("Please enter a gene identifier");
	}
	else
	{ 
		var form = startForm();			
			// identifier field for gene search form (will be null at start)
		var hiddenSearchField = document.createElement("input");
		hiddenSearchField.setAttribute("type", "hidden");
		hiddenSearchField.setAttribute("name", "search");
		hiddenSearchField.setAttribute("value", "gene");		
		form.appendChild(hiddenSearchField);		
			// gene id etc (from and for) gene text field
		var hiddenGeneField = document.createElement("input");	
		hiddenGeneField.setAttribute("type", "hidden");
		hiddenGeneField.setAttribute("name", "gene");
		hiddenGeneField.setAttribute("value", gene);		
		form.appendChild(hiddenGeneField);		
			// idtype from (and for) radio button choice
		var idtype = getIDType2();
		if (idtype=="tcID")
		{
			var ogs3url = "https://motif.mvls.gla.ac.uk/BeetleAtlas/?search=gene&gene=" + gene + "&idtype=geneID";
			window.open(ogs3url,'_self');
		}
		else
		{
			var hiddenIDField = document.createElement("input");	
			hiddenIDField.setAttribute("type", "hidden");
			hiddenIDField.setAttribute("name", "idtype");
			hiddenIDField.setAttribute("value", idtype);	
			form.appendChild(hiddenIDField);
			
			document.body.appendChild(form);
			form.submit();
		}
	}
}

function sendSearchGoForm() 
{	
	var form = startForm();
		// identifier field for go (category) search form		
	var hiddenSearchField = document.createElement("input");
	hiddenSearchField.setAttribute("type", "hidden");
	hiddenSearchField.setAttribute("name", "search");
	hiddenSearchField.setAttribute("value", "go");
	form.appendChild(hiddenSearchField);
		// stage
	var hiddenStageField = document.createElement("input");
	var stage = document.getElementById('stage').value;
	hiddenStageField.setAttribute("type", "hidden");
	hiddenStageField.setAttribute("name", "stage");
	hiddenStageField.setAttribute("value", stage);
	form.appendChild(hiddenStageField);
		// tissue ID 
	var hiddenTissueField = document.createElement("input");
	var tissue = document.getElementById('tissue').value;
	hiddenTissueField.setAttribute("type", "hidden");
	hiddenTissueField.setAttribute("name", "tissue");
	hiddenTissueField.setAttribute("value", tissue);
	form.appendChild(hiddenTissueField);
		// order term (enrichment/abundance)
	var hiddenOrderField = document.createElement("input");
	var order = document.getElementById('order').value;
	hiddenOrderField.setAttribute("type", "hidden");
	hiddenOrderField.setAttribute("name", "order");
	hiddenOrderField.setAttribute("value", order);
	form.appendChild(hiddenOrderField);		
		// keyword
	var hiddenKeywordField = document.createElement("input");
	var keyword = document.getElementById('keyword').value;
	hiddenKeywordField.setAttribute("type", "hidden");
	hiddenKeywordField.setAttribute("name", "keyword");
	hiddenKeywordField.setAttribute("value", keyword);
	form.appendChild(hiddenKeywordField);	
		// Max No. of results to display (maxdisplayed)
	var hiddenMaxField = document.createElement("input");
	var maxdisplayed = document.getElementById('maxdisplayed').value;
	hiddenMaxField.setAttribute("type", "hidden");
	hiddenMaxField.setAttribute("name", "maxdisplayed");
	hiddenMaxField.setAttribute("value", maxdisplayed);
	form.appendChild(hiddenMaxField);
	
	document.body.appendChild(form);
	form.submit();
}

function sendSearchTopForm()	// no text capture — values of selected options only
{
	var form = startForm();
		// identifier field for top search form
	var hiddenSearchField = document.createElement("input");
	hiddenSearchField.setAttribute("type", "hidden");
	hiddenSearchField.setAttribute("name", "search");
	hiddenSearchField.setAttribute("value", "top");
	form.appendChild(hiddenSearchField);
		// stage (was sex for fly)
	var hiddenTissueField = document.createElement("input");
	var stage = document.getElementById('stage').value;
	hiddenTissueField.setAttribute("type", "hidden");
	hiddenTissueField.setAttribute("name", "stage");
	hiddenTissueField.setAttribute("value", stage);
	form.appendChild(hiddenTissueField);
		// tissue ID 
	var hiddenTissueField = document.createElement("input");
	var tissue = document.getElementById('tissue').value;
	hiddenTissueField.setAttribute("type", "hidden");
	hiddenTissueField.setAttribute("name", "tissue");
	hiddenTissueField.setAttribute("value", tissue);
	form.appendChild(hiddenTissueField);
		// order term (enrichment/abundance)
	var hiddenOrderField = document.createElement("input");
	var order = document.getElementById('order').value;
	hiddenOrderField.setAttribute("type", "hidden");
	hiddenOrderField.setAttribute("name", "order");
	hiddenOrderField.setAttribute("value", order);
	form.appendChild(hiddenOrderField);
		// Max No. of results to display (maxdisplayed)
	var hiddenMaxField = document.createElement("input");
	var maxdisplayed = document.getElementById('maxdisplayed').value;
	hiddenMaxField.setAttribute("type", "hidden");
	hiddenMaxField.setAttribute("name", "maxdisplayed");
	hiddenMaxField.setAttribute("value", maxdisplayed);
	form.appendChild(hiddenMaxField);

	document.body.appendChild(form);
	form.submit();
}

function sendSearchDevelForm()	// no text capture for this one
{
	var form = startForm();	
		// identifier field for development search form
	var hiddenSearchField = document.createElement("input");
	hiddenSearchField.setAttribute("type", "hidden");
	hiddenSearchField.setAttribute("name", "search");
	hiddenSearchField.setAttribute("value", "devel");
	form.appendChild(hiddenSearchField);
		// radio buttons for stage (adult/larval) to be dominant
	var radioDev = getRadioDev();
	var hiddenRadioField = document.createElement("input");
	hiddenRadioField.setAttribute("type", "hidden");
	hiddenRadioField.setAttribute("name", "radioDev");
	hiddenRadioField.setAttribute("value", radioDev);		
	form.appendChild(hiddenRadioField);
		// uniTissue
	var hiddenUniTissueField = document.createElement("input");
	var uniTissue = document.getElementById('uniTissue').value;
	hiddenUniTissueField.setAttribute("type", "hidden");
	hiddenUniTissueField.setAttribute("name", "uniTissue");
	hiddenUniTissueField.setAttribute("value", uniTissue);
	form.appendChild(hiddenUniTissueField);	
	// Max No. of results to display (maxdisplayed)
	var hiddenMaxField = document.createElement("input");
	var maxdisplayed = document.getElementById('maxdisplayed').value;
	hiddenMaxField.setAttribute("type", "hidden");
	hiddenMaxField.setAttribute("name", "maxdisplayed");
	hiddenMaxField.setAttribute("value", maxdisplayed);
	form.appendChild(hiddenMaxField);
	
	document.body.appendChild(form);
	form.submit();
}

function sendSearchEmbryoForm()	// no text capture for this one
{
	var form = startForm();
	
		// identifier field for development search form
	var hiddenSearchField = document.createElement("input");
	hiddenSearchField.setAttribute("type", "hidden");
	hiddenSearchField.setAttribute("name", "search");
	hiddenSearchField.setAttribute("value", "embryo");
	form.appendChild(hiddenSearchField);
		// radio buttons for embryo stage (0,1,2,3) to be dominant
	var radioEmbryo = getRadioEmbryo();
	var hiddenRadioField = document.createElement("input");
	hiddenRadioField.setAttribute("type", "hidden");
	hiddenRadioField.setAttribute("name", "radioEmbryo");
	hiddenRadioField.setAttribute("value", radioEmbryo);		
	form.appendChild(hiddenRadioField);
		// radio buttons for exclusive embryo (yes/no)
	var embryoOnly = getRadioEmbryoOnly();
	var hiddenEmbryoField = document.createElement("input");
	hiddenEmbryoField.setAttribute("type", "hidden");
	hiddenEmbryoField.setAttribute("name", "embryoOnly");
	hiddenEmbryoField.setAttribute("value", embryoOnly);	
	form.appendChild(hiddenEmbryoField);
	// Max No. of results to display (maxdisplayed)
	var hiddenMaxField = document.createElement("input");
	var maxdisplayed = document.getElementById('maxdisplayed').value;
	hiddenMaxField.setAttribute("type", "hidden");
	hiddenMaxField.setAttribute("name", "maxdisplayed");
	hiddenMaxField.setAttribute("value", maxdisplayed);
	form.appendChild(hiddenMaxField);
	
	document.body.appendChild(form);
	form.submit();
}

function sendSearchProfileForm2() 
{
	var gene = document.getElementById('inputField').value;
	if(gene=="")
	{
		alert("Please enter a gene identifier.");
	}
	else
	{
		var form = startForm();
			// identifier field for profile search form
		var hiddenSearchField = document.createElement("input");
		hiddenSearchField.setAttribute("type", "hidden");
		hiddenSearchField.setAttribute("name", "search");
		hiddenSearchField.setAttribute("value", "profile");
		form.appendChild(hiddenSearchField);
			// name of gene entered in text field
		var hiddenGeneField = document.createElement("input");
		hiddenGeneField.setAttribute("type", "hidden");
		hiddenGeneField.setAttribute("name", "gene");
		hiddenGeneField.setAttribute("value", gene);
		form.appendChild(hiddenGeneField);
			// idtype from (and for) radio button choice
		var idtype = getProfileIDType2();
		if (idtype=="tcID")
		{
			var ogs3url = "https://motif.mvls.gla.ac.uk/BeetleAtlas/?search=profile&gene=" + gene + "&idtype=geneID";
			window.open(ogs3url,'_self');
		}	
		else
		{
			var hiddenIDField = document.createElement("input");	
			hiddenIDField.setAttribute("type", "hidden");
			hiddenIDField.setAttribute("name", "idtype");
			hiddenIDField.setAttribute("value", idtype);	
			form.appendChild(hiddenIDField);		
				// Pearson or Spearman
			var hiddenPearsonField = document.createElement("input");
			hiddenPearsonField.setAttribute("type", "hidden");
			hiddenPearsonField.setAttribute("name", "correlation");
			if(document.getElementById('pearson').checked)
			{
				hiddenPearsonField.setAttribute("value", "pearson");
			}
			else
			{
				hiddenPearsonField.setAttribute("value", "spearman");
			}
			form.appendChild(hiddenPearsonField);
				// r statistic cutoff
			var rcut = document.getElementById('rcut').value;
			var rCutHidden = document.createElement("input");
			rCutHidden.setAttribute("type", "hidden");
			rCutHidden.setAttribute("name", "rcut");
			rCutHidden.setAttribute("value", rcut);
			form.appendChild(rCutHidden);
			// Max No. of results to display (maxdisplayed)
			var hiddenMaxField = document.createElement("input");
			var maxdisplayed = document.getElementById('maxdisplayed').value;
			hiddenMaxField.setAttribute("type", "hidden");
			hiddenMaxField.setAttribute("name", "maxdisplayed");
			hiddenMaxField.setAttribute("value", maxdisplayed);
			form.appendChild(hiddenMaxField);
	
			document.body.appendChild(form);
			form.submit();
		}
	}
}

		// Create hidden submission forms for links to different sections //
// repeated stuff — only parameters are SDs, Whole Body for initial pages
function startToForm()
{
	var form = document.createElement("form");
	form.setAttribute("method", "get");
	form.setAttribute("action", "");
	form.setAttribute("accept-charset", "UTF-8");			
	
	// show SDs
	if (document.getElementById('errors_0') 
			&& document.getElementById('errors_0').checked)
	{
		var hiddenErrorsField = document.createElement("input");
		hiddenErrorsField.setAttribute("type", "hidden");
		hiddenErrorsField.setAttribute("name", "errors");
		hiddenErrorsField.setAttribute("value", "errors");		// Because we have to send a String to set a boolean
		form.appendChild(hiddenErrorsField);
	}	
	// show Whole Beetle data
	if (document.getElementById('whole_0') 
			&& document.getElementById('whole_0').checked)
	{
		var hiddenWholeField = document.createElement("input");
		hiddenWholeField.setAttribute("type", "hidden");
		hiddenWholeField.setAttribute("name", "whole");
		hiddenWholeField.setAttribute("value", "whole");		// Because we have to send a String to set a boolean
		form.appendChild(hiddenWholeField);
	}
	
	return form;
}

function toGeneForm() 
{
	var form = startToForm();
	var hiddenField = document.createElement("input");
	hiddenField.setAttribute("type", "hidden");
	hiddenField.setAttribute("name", "page");
	hiddenField.setAttribute("value", "gene");		// gene page
	form.appendChild(hiddenField);
	document.body.appendChild(form);
	form.submit();
}

function toGOForm() 
{
	var form = startToForm();
	var hiddenField = document.createElement("input");
	hiddenField.setAttribute("type", "hidden");
	hiddenField.setAttribute("name", "page");	
	hiddenField.setAttribute("value", "go");		// Category or GO page (note use of "go") NOT YET IMPLEMENTED
	form.appendChild(hiddenField);
	document.body.appendChild(form);
	form.submit();
}

function toTopForm() 
{
	var form = startToForm();
	var hiddenField = document.createElement("input");
	hiddenField.setAttribute("type", "hidden");
	hiddenField.setAttribute("name", "page");
	hiddenField.setAttribute("value", "top");		// Tissue page (note use of "top") 
	form.appendChild(hiddenField);
	document.body.appendChild(form);
	form.submit();
}

function toDevelForm() 
{
	var form = startToForm();
	var hiddenField = document.createElement("input");
	hiddenField.setAttribute("type", "hidden");
	hiddenField.setAttribute("name", "page");
	hiddenField.setAttribute("value", "devel");		// Devel(opment) page (note use of "devel") — compares larval and adult
	form.appendChild(hiddenField);
	document.body.appendChild(form);
	form.submit();
}

function toEmbryoForm() 
{
	var form = startToForm();
	var hiddenField = document.createElement("input");
	hiddenField.setAttribute("type", "hidden");
	hiddenField.setAttribute("name", "page");
	hiddenField.setAttribute("value", "embryo");		// Embryo
	form.appendChild(hiddenField);
	document.body.appendChild(form);
	form.submit();
}

function toProfileForm() 
{
	var form = startToForm();
	var hiddenField = document.createElement("input");
	hiddenField.setAttribute("type", "hidden");
	hiddenField.setAttribute("name", "page");
	hiddenField.setAttribute("value", "profile");		// Profile
	form.appendChild(hiddenField);
	document.body.appendChild(form);
	form.submit();
}

function toHomeForm() 
{
	var form = startToForm();
	var hiddenField = document.createElement("input");
	hiddenField.setAttribute("type", "hidden");
	hiddenField.setAttribute("name", "page");
	hiddenField.setAttribute("value", "home");		// home page
	form.appendChild(hiddenField);
	document.body.appendChild(form);
	form.submit();
}

function toFeedbackForm() 
{
	var form = startToForm();
	var hiddenField = document.createElement("input");
	hiddenField.setAttribute("type", "hidden");
	hiddenField.setAttribute("name", "page");
	hiddenField.setAttribute("value", "contact");	// feedback page named as contact
	form.appendChild(hiddenField);
	document.body.appendChild(form);
	form.submit();
}

function toHelpForm() 
{
	var form = startToForm();
	var hiddenField = document.createElement("input");
	hiddenField.setAttribute("type", "hidden");
	hiddenField.setAttribute("name", "page");
	hiddenField.setAttribute("value", "help");		// documentation page named as help  NOT YET IMPLEMENTED
	form.appendChild(hiddenField);
	document.body.appendChild(form);
	form.submit();
}


// Smart check for correct idtype choice for Gene
// determines name of gene and auto-assigns idType if possible
function getIDType2()
{
	var idtype;		// idtype choice
	var input = document.getElementById('inputField').value;

	if(input.substring(0,4) == "FBgn")
	{
		idtype = "flyFBgn"		
	}
	else if(input.substring(0,2) == "CG")
	{
		idtype = "flyCG";
	}
	else if(document.getElementById('ncbiID').checked)
	{
		idtype = "ncbiID";
	}
	else if(document.getElementById('ncbiSymbol').checked)
	{
		idtype = "ncbiSymbol";
	}
	else if(document.getElementById('product').checked)
	{
		idtype = "product";
	}
	else if(document.getElementById('tcID').checked)
	{
		idtype = "tcID";
	}
	else if(document.getElementById('flyFBgn').checked)
	{
		idtype = "flyFBgn";
	}
	else if(document.getElementById('flyCG').checked)
	{
		idtype = "flyCG";
	}
	else if(document.getElementById('flySymbol').checked)
	{
		idtype = "flySymbol";
	}
	else
	{
		idtype = "ncbiID";
	}	
	return idtype;
}

//for Profile search — surely this could be short-circuited
function getProfileIDType2()
{
	var idtype;		// idtype choice
	// var input = document.getElementById('inputField').value;	
	if(document.getElementById('ncbiID').checked)
	{
		idtype = "ncbiID";
	}
	else if(document.getElementById('ncbiSymbol').checked)
	{
		idtype = "ncbiSymbol";
	}
	else if(document.getElementById('tcID').checked)
	{
		idtype = "tcID";
	}
	return idtype;
}

//determines checked radio button for use in hidden form fields in Development page
function getRadioDev()
{
	var radioDev;		// radio button choice
	if(document.getElementById('devAdult').checked)
	{
		radioDev = "devAdult";
	}
	else
	{
		radioDev = "devLarval";
	}	
	return radioDev;
}

//determines checked radio button for use in hidden form fields in Embryo page
function getRadioEmbryo()
{
	var radioEmbryo;		// radio button choice
	if(document.getElementById('zero').checked)
	{
		radioEmbryo = "zero";
	}
	else if (document.getElementById('one').checked)
	{
		radioEmbryo = "one";
	}
	else if (document.getElementById('two').checked)
	{
		radioEmbryo = "two";
	}
	else if (document.getElementById('three').checked)
	{
		radioEmbryo = "three";
	}
	return radioEmbryo;
}

//determines second checked radio button for use in hidden form fields in Embryo page
function getRadioEmbryoOnly()
{
	var embryoOnly;		// radio button choice
	if(document.getElementById('yes').checked)
	{
		embryoOnly = "yes";
	}	
	else
	{
		embryoOnly = "no";
	}
	return embryoOnly;
}


// Submit forms by hitting 'enter' key (code 13)
function geneKey2(e)
{
	if (e.keyCode == 13) 
	{
		sendSearchGeneForm2();
	}
}
function goKey(e)
{	
	if (e.keyCode == 13) 
	{
		sendSearchGoForm();
	}
}
function topKey(e)
{
	if (e.keyCode == 13)
	{
		sendSearchTopForm();
	}
}
function develKey(e)
{
	if (e.keyCode == 13)
	{
		sendSearchDevelForm();
	}
}
function embryoKey(e)
{
	if (e.keyCode == 13)
	{
		sendSearchEmbryoForm();
	}
}
function profileKey2(e)
{
	if (e.keyCode == 13)
	{
		sendSearchProfileForm2();
	}
}

	// toggle all function (smart)
	function toggleAll(linkAllID, numResults, defText, altText)
	{
		// toggle self
		var linkEle = document.getElementById(linkAllID);	// this is the icon itself
		var oldText = linkEle.textContent;
		var newText;
		if(oldText === "▽")
		{
			newText = "▷";
		}
		else
		{
			newText = "▽";
		}
		linkEle.replaceChild(document.createTextNode(newText), linkEle.firstChild);
		
		// toggle slaves
		for (var i=0; i < numResults; i++) 
		{	
			toggleSlaves(linkAllID, ("bt_"+i), ("hs_"+i), defText, altText);
		}
	}

	// sets all according to the master button, irrespective of current state (unlike toggleConcealed)
	function toggleSlaves(linkAllID, linkID, targetID, defText, altText)
	{
		var masterText = document.getElementById(linkAllID).textContent;
		var theStyle = document.getElementById(targetID).style;
		if (masterText === "▷")
		{
			theStyle.display = "none";
			newText = defText;
		}
		else
		{
			theStyle.display = "block";
			newText = altText;
		}
		var linkEle = document.getElementById(linkID);
		linkEle.replaceChild(document.createTextNode(newText), linkEle.firstChild);
	}

	//  takes ID of link element, hide/show target div, and default and alternative text to do hide/show and text change
	function toggleConcealed(linkID, targetID, defText, altText)
	{
		var theStyle = document.getElementById(targetID).style;
		if (theStyle.display == "block")
		{
			theStyle.display = "none";
			newText = defText;
		}
		else
		{
			theStyle.display = "block";
			newText = altText;
		}
		var linkEle = document.getElementById(linkID);
		linkEle.replaceChild(document.createTextNode(newText), linkEle.firstChild);
	}	

	// takes class of target <tr> s for hide/show
	function toggleRow(rowClass)
	{
		var myClasses = document.getElementsByClassName(rowClass);
		
		for (var i=0; i < myClasses.length; i++) 
		{
			var theStyle = myClasses[i].style;
			if(theStyle.display == 'none')
			{
				theStyle.display = '';
			}
			else
			{
				theStyle.display = 'none';			
			}
		}	
	}
		
	// allows hide/show of target row to be set directly on creation of page
	function setRow(rowClass, on)
	{
		var myClasses = document.getElementsByClassName(rowClass);
		for (var i=0; i < myClasses.length; i++) 
		{
			var theStyle = myClasses[i].style;
			if(on == true)
			{
				theStyle.display = '';
			}
			else
			{
				theStyle.display = 'none';			
			}
		}		
	}
	
	// takes id of target <div> to hide show contents (e.g. for icon w. no change of text)
	function toggleDiv(target)
	{
		var targetStyle = document.getElementById(target).style;

		if (targetStyle.display == "block")
		{
			targetStyle.display = "none";
		}
		else
		{
			targetStyle.display = "block";		
		}	
	}
	
	// allows closing a hide/show div with a x box
	function closeDiv(target)
	{
		document.getElementById(target).style.display = "none";
	}
	
	// takes class of target <span> to hide show contents (e.g. for icon w. no change of text)
	function toggleSpan(spanClass) 
	{
		var myClasses = document.querySelectorAll(spanClass);

		for (var i=0; i < myClasses.length; i++) 
		{
			var theStyle = myClasses[i].style;
			if(theStyle.display == 'inline')
			{
				theStyle.display = 'none';
			}
			else
			{
				theStyle.display = 'inline';			
			}
		}
	}
	
	// allows hide/show of target span to be set directly on creation of page
	function setSpan(spanClass, on)
	{
		var myClasses = document.querySelectorAll(spanClass);
		for (var i=0; i < myClasses.length; i++) 
		{
			var myClasses = document.querySelectorAll(spanClass);	// ???
			var theStyle = myClasses[i].style;
			if(on == true)
			{
				theStyle.display = 'inline';
			}
			else
			{
				theStyle.display = 'none';			
			}
		}		
	}
	
	// synchronizes checked state checkboxes: used where checkbox makes global change (e.g. for errors)
	function synchBoxes(ckbox, theClass)
	{
		var boxes = document.getElementsByTagName("input");

		for(var i=0; i<boxes.length; i++)
		{
			if(ckbox.checked && boxes[i].classList == theClass)
			{
				boxes[i].checked = true;
			}
			else if (!ckbox.checked && boxes[i].classList == theClass)
			{
				boxes[i].checked = false;
			}
		}
	}
	
	// Sends SVG to Servlet SVGreflector for returning as downloadable file
	function sendSVG2(resultNum) 
	{
		var svgText = document.getElementById("svg_" + resultNum).innerHTML;			// id of div holding SVG
		var ncbiID = document.getElementById("graphID_" + resultNum).textContent;		// id of span holding gene name
		
		var form = document.createElement("form");
		form.setAttribute("method", "post");
		form.setAttribute("action", "/SVGreflector/image.svg");
		form.setAttribute("accept-charset", "UTF-8");	

		var hiddenSVGField = document.createElement("input");	
		hiddenSVGField.setAttribute("type", "hidden");
		hiddenSVGField.setAttribute("name", "svgText");
		hiddenSVGField.setAttribute("value", svgText);		
		form.appendChild(hiddenSVGField);
		
		var hiddenNameField = document.createElement("input");	
		hiddenNameField.setAttribute("type", "hidden");
		hiddenNameField.setAttribute("name", "graphName");
		hiddenNameField.setAttribute("value", ncbiID);		
		form.appendChild(hiddenNameField);
		
		document.body.appendChild(form);
		form.submit();
	}
	
	// link to NCBI from NCBI_ID
	function linkToNCBI(ncbiID)	
	{
		var url = "https://www.ncbi.nlm.nih.gov/gene/?term=" + ncbiID;
		var args = "width=850,height=800,toolbar=1,scrollbars=1,resizable=yes";
		var name = "iBeetle page for " + ncbiID;
		window.open(url, name, args);
	}
		
	// Send query for fly homologues to FlyAtlas2
	function linkToFlyAtlas(ortho)	
	{
		ortho = ortho.replace(/,/g, '%0D%0A');				// replace comma by EOL for FA2 batch entry
		var url = "https://motif.mvls.gla.ac.uk/FlyAtlas2/?search=bulk&geneList=" + ortho;	// flyatlas2.org doesn't work.
		var args = "width=850,height=800,toolbar=1,scrollbars=1,resizable=yes";
		var name = "Drosophila homologue(s)";
		window.open(url, name, args);
	}
	
	// opens UCSC Brower link/info page in popup – Hard-coded for BeetleAtlas2 and Tricast 1.1 genome
	function linkToUCSC2(id, locus)
	{
		var url = "/BeetleBrowse/index.html?id=" + id + "&locus=" + locus + "&version=two";
		var w = 720;
		var h = 560;
		openHelp(url, "Link to UCSC Browser", w, h);		
	}
	
/*	function openLinkWindow(url) 
	{ 
	   var args = 'width=850,'
	   + 'height=800,'
	   + 'toolbar=1,'
	   + 'location=1,'
	   + 'directories=1,'
	   + 'status=1,'
	   + 'menubar=1,'
	   + 'scrollbars=yes,'
	   + 'resizable=yes';
	
	   window.open(url, 'new', args);
	}*/
	
	function openLinkWindow(url) 
	{ 
	   var args = 'width=1200,'
	   + 'height=800,'
	   + 'toolbar=1,'
	   + 'location=1,'
	   + 'directories=1,'
	   + 'status=1,'
	   + 'menubar=1,'
	   + 'scrollbars=1,'
	   + 'resizable=1';
	
	   window.open(url, 'new', args);
	}
	
	// Send to BeetlePara2 to generate List/Button window
	function listParalogues2(id) 
	{
		var url = "/BeetlePara2/index.html?id=" + id + "&type=para";
		var w = 460;
		var h = 600;
		var name = "Beetle Paralogue(s)";
		openHelp(url, name, w, h);
	}
	
	function listOrthologues2(id)
	{
		var url = "/BeetlePara2/index.html?id=" + id + "&type=ortho";
		var w = 460;
		var h = 600;
		var name = "Drosophila Orthologue(s)";
		openHelp(url, name, w, h);
	}
	
	// Opens linked publication in separate window
	function linkToPaper(url, name)	
	{
		var args = "width=850,height=800,toolbar=1,scrollbars=1,resizable=yes";
		window.open(url, name, args);
	}
	
	function openHelp(url, name, w, h) 
	{ 
	   var args = 'width=' + w + ','
	   + 'height=' + h + ','
	   + 'toolbar=0,'
	   + 'location=0,'
	   + 'directories=0,'
	   + 'status=yes,'
	   + 'menubar=0,'
	   + 'scrollbars=1,'
	   + 'resizable=yes';	 
	   if (parseInt(navigator.appVersion) >= 4)
	   {
		   xposition = (screen.width - w)/2;
		   yposition = (screen.height - h)/2;  
		   args += ','
			   + 'screenx=' + xposition + ',' //NN
			   +  'screeny=' + yposition + ',' //NN
			   +  'left=' + xposition + ',' //IE
			   +  'top=' + yposition; //IE
	    }
	   window.open(url, name, args);
	}

	// sets focus to input field, if present
	function setFocus() 
	{
		if(document.getElementById("inputField"))
		{
			var input = document.getElementById("inputField");
			input.focus();
			return;
		}
		else
		{
			return;		
		}
	}
	
	// function to try to append a location hash to a url dynamically generated by submit button
	function setHash(locHash)
	{
		if(document.getElementById(locHash))
		{
			window.location.hash = locHash;
		}
		else
		{
			window.location.hash = "";	
		}
	}
	
	var defLinkText = "  show";	// default link text
	var altLinkText = "  hide";	// alternative link text
		// check the following ids are actually unique!
	var visDivID = "visible";		// id of div with vis text to which link ele is added
	var hidDivID = "hideme";		// id of div with hide/show text
	var linkID = "expand";			// id for link - generated by js
	
	// creates link on line with vis text if there is div with hidden text
	function createLink()
	{
		if(document.getElementById(visDivID) && document.getElementById(hidDivID))
		{
			var visDiv = document.getElementById(visDivID);		// div to add link ele to
			var hidDiv = document.getElementById(hidDivID);		// div to hide/show
			
				// create 'a' element with js link to hideShow function and append to visible div
			var hsLink = document.createElement("a");
			hsLink.id = linkID;	// provide link with id to ref for text change 
				// construct the ahref as the js hideShow()
			hsLink.href = "javascript:hideShow('" + hsLink.id + "','" + hidDiv.id + "');";
				// add linked text to element and add element to div
			hsLink.appendChild(document.createTextNode(defLinkText));
			visDiv.appendChild(hsLink);
		}
	}
	
	// takes ids of link element and hide/show target div to do hide/show and text change
	function hideShow(link, target)
	{
			// does the hide/show stuff on the target
		theStyle = document.getElementById(target).style;
			//var text;	// name of link
		if (theStyle.display == "block")
		{
			theStyle.display = "none";
			newText = defLinkText;
		}
		else
		{
			theStyle.display = "block";
			newText = altLinkText;
		}	
			// get the link element and change its text
		var linkEle = document.getElementById(link);
		linkEle.replaceChild(document.createTextNode(newText), linkEle.firstChild);
	}
	
	// When the user scrolls down 20px from the top of the document, show the button
	function scrollFunction() 
	{
		let mybutton = document.getElementById("upButton");
		if (document.body.scrollTop > 20 || document.documentElement.scrollTop > 20) 
		{
			mybutton.style.display = "block";
		} 
		else 
		{
			mybutton.style.display = "none";
		}
	}

	// When the user clicks on the button, scroll to the top of the document
	function topFunction() 
	{
		document.body.scrollTop = 0;
		document.documentElement.scrollTop = 0;
	}	
	