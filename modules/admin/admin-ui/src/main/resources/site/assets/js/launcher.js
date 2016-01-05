(function () {
    var adminUrl = "/admin/tool";
    var launcherPanel;

    function appendLauncherToolbar() {
        var div = document.createElement("div");
        div.setAttribute("class", "launcher-bar");

        var button = document.createElement("button");
        button.setAttribute("class", "launcher-button");

        button.addEventListener("click", onLauncherClick);

        document.getElementsByTagName("body")[0].appendChild(div);
        div.appendChild(button);
    }

    function appendLauncherPanel() {
        var div = document.createElement("div");
        div.setAttribute("class", "launcher-panel");
        div.appendChild(createLauncherLink(div));

        document.getElementsByTagName("body")[0].appendChild(div);

        launcherPanel = div;
    }

    function createLauncherLink(container) {
        var link = document.createElement("link");

        link.setAttribute("rel", "import");
        link.setAttribute("href", adminUrl);

        link.onload = function() {
            var clonedDiv = link.import.querySelector('.home-main-container').cloneNode(true);
            while (clonedDiv.childNodes.length > 0) {
                container.appendChild(clonedDiv.childNodes[0]);
            }
        };

        return link;
    }

    function setLauncherPanelDisplay(display) {
        launcherPanel.style.display = display;
    }


    function isLauncherPanelVisible() {
        return launcherPanel.style.display == "block";
    }

    function onLauncherClick() {
        setLauncherPanelDisplay('block');
    }

    function onBodyClicked(e) {
        if (launcherPanel && isLauncherPanelVisible() && e.target != getLauncherButton() && !launcherPanel.contains(e.target)) {
            setLauncherPanelDisplay('none');
        }
    }

    function getLauncherButton() {
        return document.querySelector('.launcher-button');
    }

    function init() {
        document.getElementsByTagName("body")[0].addEventListener("click", onBodyClicked);
        appendLauncherToolbar();
        appendLauncherPanel();
    }

    init();
}());