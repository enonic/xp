(function () {
    var adminUrl = "/admin/tool/com.enonic.xp.admin.ui/launcher";
    var launcherPanel, bodyMask, launcherButton;
    var isHomeApp = (CONFIG && CONFIG.autoOpenLauncher);

    function appendLauncherToolbar() {
        launcherButton = document.createElement("button");
        launcherButton.setAttribute("class", "launcher-button");

        var span = document.createElement("span");
        span.setAttribute("class", "lines");
        launcherButton.appendChild(span);

        launcherButton.addEventListener("click", togglePanelState);

        document.getElementsByTagName("body")[0].appendChild(launcherButton);
    }

    function togglePanelState() {
        if (isPanelExpanded()) {
            closeLauncherPanel();
        }
        else {
            openLauncherPanel();
        }
    }

    function toggleButton() {
        launcherButton.classList.toggle("toggled");
    }

    function appendLauncherPanel() {
        var div = document.createElement("div");
        div.setAttribute("class", "launcher-panel");
        div.classList.add("hidden");
        div.appendChild(createLauncherLink(div));

        document.getElementsByTagName("body")[0].appendChild(div);

        launcherPanel = div;
    }

    function createLauncherLink(container) {
        var link = document.createElement("link");

        link.setAttribute("rel", "import");
        link.setAttribute("href", adminUrl);

        link.onload = function() {
            var clonedDiv = link.import.querySelector('.launcher-main-container').cloneNode(true);
            while (clonedDiv.childNodes.length > 0) {
                container.appendChild(clonedDiv.childNodes[0]);
            }

            if (isHomeApp) {
                openLauncherPanel();
            }
            else {
                var appTiles = container.querySelector('.launcher-app-container').querySelectorAll("a");
                for (var i = 0; i < appTiles.length; i++) {
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
        if (isHomeApp) {
            div.classList.add("app-home");
        }
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

    function isPanelExpanded() {
        return launcherPanel.classList.contains("visible");
    }

    function openLauncherPanel() {
        toggleButton();
        showBodyMask();
        launcherPanel.classList.remove("hidden", "slideout");
        launcherPanel.classList.add("visible");
    }

    function closeLauncherPanel(skipTransition) {
        launcherPanel.classList.remove("visible");
        launcherPanel.classList.add((skipTransition == true) ? "hidden" : "slideout");
        hideBodyMask();
        toggleButton();
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
}());