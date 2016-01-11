(function () {
    var adminUrl = "/admin/tool/com.enonic.xp.admin.ui/launcher";
    var launcherPanel, bodyMask;

    function appendLauncherToolbar() {
        var div = document.createElement("div");
        div.setAttribute("class", "launcher-bar");

        var button = document.createElement("button");
        button.setAttribute("class", "launcher-button");

        button.addEventListener("click", openLauncherPanel);

        document.getElementsByTagName("body")[0].appendChild(div);
        div.appendChild(button);
    }

    function appendLauncherPanel() {
        var div = document.createElement("div");
        div.setAttribute("class", "launcher-panel");
        div.classList.add("hidden");

        var button = document.createElement("button");
        button.setAttribute("class", "launcher-panel-close");
        button.addEventListener("click", closeLauncherPanel);

        div.appendChild(button);
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

            var appTiles = container.querySelector('.app-tiles-placeholder').querySelectorAll("a");
            for (var i = 0; i < appTiles.length; i++) {
                if (appTiles[i].target == "") {
                    appTiles[i].target = "_blank";
                    appTiles[i].addEventListener("click", closeLauncherPanel.bind(this, true));
                }
            }
        };

        return link;
    }

    function getBodyMask() {
        return document.querySelector('.xp-admin-common-mask.body-mask');
    }

    function createBodyMaskDiv() {
        var div = document.createElement("div");
        div.classList.add("xp-admin-common-mask", "body-mask");
        div.style.display = "none";

        document.getElementsByTagName("body")[0].appendChild(div);

        return div;
    }

    function showBodyMask() {
        bodyMask.style.display = "block";
    }

    function hideBodyMask() {
        bodyMask.style.display = "none";
    }

    function openLauncherPanel() {
        showBodyMask();
        launcherPanel.classList.remove("hidden", "slideout");
        launcherPanel.classList.add("visible");
    }

    function closeLauncherPanel(skipTransition) {
        launcherPanel.classList.remove("visible");
        launcherPanel.classList.add((skipTransition == true) ? "hidden" : "slideout");
        hideBodyMask();
    }

    function initBodyMask() {
        bodyMask = getBodyMask();
        if (!bodyMask) {
            bodyMask = createBodyMaskDiv();
        }
        bodyMask.addEventListener("click", closeLauncherPanel);
    }

    function init() {
        initBodyMask();
        appendLauncherToolbar();
        appendLauncherPanel();
    }

    init();

    if (CONFIG && CONFIG.autoOpenLauncher) {
        openLauncherPanel();
    }
}());