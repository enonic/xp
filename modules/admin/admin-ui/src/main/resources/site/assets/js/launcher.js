var cssRef = document.createElement("link");
cssRef.setAttribute("rel", "stylesheet");
cssRef.setAttribute("type", "text/css");
cssRef.setAttribute("href", "{{portalAssetsUrl}}/css/launcher.css");

document.getElementsByTagName("head")[0].appendChild(cssRef);

var button = document.createElement("button");
button.setAttribute("class", "launcher-button");

document.getElementsByTagName("body")[0].appendChild(button);