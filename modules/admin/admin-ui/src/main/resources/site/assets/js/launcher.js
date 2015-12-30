(function () {
    function injectCss() {
        var cssRef = document.createElement("link");
        cssRef.setAttribute("rel", "stylesheet");
        cssRef.setAttribute("type", "text/css");
        cssRef.setAttribute("href", CONFIG.portalAssetsUrl + "/css/launcher.css");

        document.getElementsByTagName("head")[0].appendChild(cssRef);
    }

    function appendToolbar() {
        var div = document.createElement("div");
        div.setAttribute("class", "launcher-bar");

        var button = document.createElement("button");
        button.setAttribute("class", "launcher-button");

        document.getElementsByTagName("body")[0].appendChild(div);
        div.appendChild(button);
    }

    function init() {
        injectCss();
        appendToolbar();
    }

    init();
}());