(function() {

	function appendExitLink() {
		var newDiv=document.createElement("div");
		newDiv.style = "padding-left: 10px;";
		newDiv.innerHTML="<a style=\"color: #7a7a7a;\" href=/index.html><span style=\"padding-left:0px; font-size:16px; padding-right:0px;\">Exit</span><i class=\"material-icons md-24\">exit_to_app</i></a>";
		var searchResult = document.getElementsByClassName("topbar-wrapper");
		if (searchResult.length > 0) {
			var topBar = searchResult[0];
			topBar.appendChild(newDiv);
		}
	};
	

	function changeTitle() {
		var searchResult = document.getElementsByClassName("topbar-wrapper");
		if (searchResult.length < 1) {
			return;
		}
		var topBar = searchResult[0];
		searchResult = topBar.getElementsByTagName("span");
		if (searchResult.length < 1) {
			return;
		}
		var linkSpan = searchResult[0];
		linkSpan.innerHTML = "API Catalog";
		document.title = "API Catalog";
	};

	function injectIcons() {
		var link = document.createElement("link");
		link.href = "https://fonts.googleapis.com/icon?family=Material+Icons";
        link.rel= "stylesheet";
        document.getElementsByTagName("head")[0].appendChild(link);
	}
	
	function makeResponsive() {
		var meta = document.createElement("meta");
		meta.name = "viewport";
		meta.content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no"
		document.getElementsByTagName("head")[0].appendChild(meta);
	}

	
	injectIcons();
	makeResponsive();
	appendExitLink();
	changeTitle();
	
})();



