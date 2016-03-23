(function () {
    var adminUrl = window.CONFIG && window.CONFIG.adminUrl || "/admin";
    var launcherUrl = adminUrl + (adminUrl.slice(-1) == '/' ? "" : "/" ) + "tool/com.enonic.xp.admin.ui/launcher";
    var launcherPanel, bodyMask, launcherButton, launcherMainContainer;
    var isHomeApp = window.CONFIG && window.CONFIG.appId == "home";
    var autoOpenLauncher = window.CONFIG && window.CONFIG.autoOpenLauncher;
    var appId = window.CONFIG ? window.CONFIG.appId : "";
    var minWidthForTip = 900;

    function appendLauncherButton() {
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
        launcherButton.focus();
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
        link.setAttribute("href", launcherUrl);

        link.onload = function () {
            launcherMainContainer = link.import.querySelector('.launcher-main-container');
            launcherMainContainer.setAttribute("hidden", "true");
            container.appendChild(launcherMainContainer);
            addLongClickHandler(container);

            if (autoOpenLauncher) {
                openLauncherPanel();
                launcherButton.focus();
                if (getBodyWidth() > minWidthForTip) {
                    setTipVisibility("table");
                }
            }
            else {
                var appTiles = container.querySelector('.launcher-app-container').querySelectorAll("a");
                for (var i = 0; i < appTiles.length; i++) {
                    appTiles[i].addEventListener("click", closeLauncherPanel.bind(this, true));
                }
            }
            highlightActiveApp();
        };

        return link;
    }

    function addLongClickHandler(container) {
        var longpress = false;
        var starttime, endtime;

        var appTiles = container.querySelector('.launcher-app-container').querySelectorAll("a");
        for (var i = 0; i < appTiles.length; i++) {
            appTiles[i].addEventListener("click", function(e) {
                if (longpress) {
                    e.preventDefault();
                    document.location.href = this.href;
                }
            });
            appTiles[i].addEventListener("mousedown", function() {
                startTime = new Date().getTime();
            });
            appTiles[i].addEventListener("mouseup", function() {
                endTime = new Date().getTime();
                longpress = (endTime - startTime < 500) ? false : true;
            });
        }
    }

    function getBodyWidth() {
        return document.getElementsByTagName("body")[0].clientWidth;
    }

    function setTipVisibility(display) {
        var launcherTip = document.querySelector('.launcher-tip');
        if (launcherTip) {
            launcherTip.style.display = display;
        }
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
        bodyMask.classList.add("launcher");
    }

    function hideBodyMask() {
        bodyMask.style.display = "none";
        bodyMask.classList.remove("launcher");
    }

    function isPanelExpanded() {
        return launcherPanel.classList.contains("visible");
    }

    function openLauncherPanel() {
        launcherMainContainer.removeAttribute("hidden");
        listenToKeyboardEvents();
        toggleButton();
        showBodyMask();
        launcherPanel.classList.remove("hidden", "slideout");
        launcherPanel.classList.add("visible");
    }

    function closeLauncherPanel(skipTransition) {
        launcherMainContainer.setAttribute("hidden", "true");
        setTipVisibility("none");
        unlistenToKeyboardEvents();
        launcherPanel.classList.remove("visible");
        launcherPanel.classList.add((skipTransition == true) ? "hidden" : "slideout");
        hideBodyMask();
        toggleButton();
        unselectCurrentApp();
    }

    function initBodyMask() {
        bodyMask = getBodyMask();
        if (!bodyMask) {
            bodyMask = createBodyMaskDiv();
        }
        bodyMask.addEventListener("click", closeLauncherPanel);
    }

    function listenToKeyboardEvents() {
        window.addEventListener("keydown", onKeyPressed, true);
    }

    function unlistenToKeyboardEvents() {
        window.removeEventListener("keydown", onKeyPressed, true);
    }

    function getSelectedApp() {
        return launcherPanel.querySelector('.app-row.selected');
    }

    function unselectCurrentApp() {
        var selectedApp = getSelectedApp();
        if (selectedApp) {
            selectedApp.classList.remove("selected");
        }
    }

    function highlightActiveApp() {
        if (!appId) {
            return;
        }
        var appRows = launcherPanel.querySelectorAll('.app-row');
        for (var i = 0; i < appRows.length; i++) {
            if (appRows[i].id == appId) {
                appRows[i].classList.add("active");
            }
        }
    }

    function onKeyPressed(e) {
        if (!isPanelExpanded()) {
            return;
        }

        e.stopPropagation();

        switch (e.keyCode) {
        case 27:
            // esc key pressed
            closeLauncherPanel();
            break;
        case 13:
            // enter key pressed
            var selectedApp = getSelectedApp();
            if (selectedApp) {
                setTipVisibility("none");
            }
            break;
        }
    }

    function init() {
        initBodyMask();

        appendLauncherButton();
        appendLauncherPanel();
    }

    window.addEventListener("load", function () {
        init();
    });
}());