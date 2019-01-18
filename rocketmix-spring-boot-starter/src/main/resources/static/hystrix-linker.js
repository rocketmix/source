var baseUrl = window.location.protocol + "//" + window.location.host;
var streamUrl = window.location.protocol + "//" + window.location.host + "/actuator/hystrix.stream";
var linkUrl = "/hystrix/monitor?stream=" + encodeURIComponent(streamUrl) + "";
var a = document.createElement('a');
var linkText = document.createTextNode("View Hystrix Dashboard");
a.appendChild(linkText);
a.title = "Hystrix Dashboard link";
a.href = linkUrl;
a.className = "swagger-ui";
a.style = "float: right;";
a.target = "_blank";
var div = document.createElement('div');
div.appendChild(a);
div.style = "position:absolute;top:60px;width:100%;padding-right:5px;";
document.body.appendChild(div);	

