     /* Beetle Browse JS DPL 17.05.2025 */

	function openLinkWindow(url) 
	{ 
	   var args = 'width=1500,'
	   + 'height=800,'
	   + 'toolbar=1,'
	   + 'location=1,'
	   + 'directories=1,'
	   + 'status=1,'
	   + 'menubar=1,'
	   + 'scrollbars=yes,'
	   + 'resizable=yes';
	
	   window.open(url, 'new', args);
	}
	
	// link to iBeetle for geme with no liftover
	function linkToiBeetle(geneID)	
	{
		var url = "http://ibeetle-base.uni-goettingen.de/details/" + geneID;
		var args = "width=850,height=800,toolbar=1,scrollbars=1,resizable=yes";
		var name = "iBeetle page for " + geneID;
		window.open(url, name, args);
	}
	