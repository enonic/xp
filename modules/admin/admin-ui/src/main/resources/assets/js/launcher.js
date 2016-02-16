(function () {
    var adminUrl = "/admin/tool/com.enonic.xp.admin.ui/launcher";
    var cssPath = window.CONFIG.portalAssetsUrl + "/styles/_launcher.css";
    var launcherPanel, bodyMask, launcherButton;
    var isHomeApp = (window.CONFIG && window.CONFIG.appId == "home");
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

    function appendCssReference() {
        var fileref = document.createElement("link");
        fileref.setAttribute("rel", "stylesheet");
        fileref.setAttribute("type", "text/css");
        fileref.setAttribute("href", cssPath);

        document.getElementsByTagName("body")[0].appendChild(fileref);
    }

    function createLauncherLink(container) {
        var link = document.createElement("link");

        link.setAttribute("rel", "import");
        link.setAttribute("href", adminUrl);

        link.onload = function () {
            var clonedDiv = link.import.querySelector('.launcher-main-container').cloneNode(true);
            while (clonedDiv.childNodes.length > 0) {
                container.appendChild(clonedDiv.childNodes[0]);
            }

            if (window.CONFIG.autoOpenLauncher) {
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
        listenToKeyboardEvents();
        toggleButton();
        showBodyMask();
        launcherPanel.classList.remove("hidden", "slideout");
        launcherPanel.classList.add("visible");
    }

    function closeLauncherPanel(skipTransition) {
        setTipVisibility("none");
        unlistenToKeyboardEvents();
        disableKeyboardNavigation();
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

    function listenToMouseMove() {
        window.addEventListener("mousemove", disableKeyboardNavigation, true);
    }

    function disableKeyboardNavigation() {
        launcherPanel.classList.remove("keyboard-navigation");
        unselectCurrentApp();
        window.removeEventListener("mousemove", disableKeyboardNavigation, true);
    }

    function getSelectedApp() {
        return launcherPanel.querySelector('.app-row.selected');
    }

    function getSelectedAppIndex() {
        var apps = launcherPanel.querySelectorAll('.app-row');
        for (var i=0; i<apps.length; i++) {
            if (apps[i].classList.contains("selected")) {
                return i;
            }
        }
        return -1;
    }

    function selectNextApp() {
        var selectedIndex = getSelectedAppIndex();
        var apps = launcherPanel.querySelectorAll('.app-row');

        selectApp((selectedIndex + 1) == apps.length ? 0 : selectedIndex + 1);
    }

    function selectPreviousApp() {
        var selectedIndex = getSelectedAppIndex();
        var nextIndex;
        if (selectedIndex == -1) {
            nextIndex = 0;
        }
        else if (selectedIndex == 0) {
            nextIndex = launcherPanel.querySelectorAll('.app-row').length - 1;
        }
        else {
            nextIndex = selectedIndex - 1;
        }

        selectApp(nextIndex);
    }

    function selectApp(index) {
        unselectCurrentApp();
        getAppByIndex(index).classList.add("selected");
    }

    function unselectCurrentApp() {
        var selectedApp = getSelectedApp();
        if (selectedApp) {
            selectedApp.classList.remove("selected");
        }
    }

    function getAppByIndex(index) {
        var apps = launcherPanel.querySelectorAll('.app-row');
        for (var i=0; i<apps.length; i++) {
            if (i == index) {
                return apps[i];
            }
        }
        return null;
    }

    function startApp(app) {
        var anchorEl = app.parentElement;
        if (anchorEl && anchorEl.tagName == 'A' && anchorEl.click) {
            anchorEl.click();
        }
    }

    function highlightActiveApp() {
        if (!window.CONFIG.appId) {
            return;
        }
        var appRows = launcherPanel.querySelectorAll('.app-row');
        for (var i = 0; i < appRows.length; i++) {
            if (appRows[i].id == window.CONFIG.appId ) {
                appRows[i].classList.add("active");
            }
        }

    }

    function initKeyboardNavigation() {
        if (!launcherPanel.classList.contains("keyboard-navigation")) {
            listenToMouseMove();
            launcherButton.blur();
            launcherPanel.classList.add("keyboard-navigation");
        }
    }

    function onKeyPressed(e) {
        e.stopPropagation();
        switch(e.keyCode) {
            case 38:
                // up key pressed
                initKeyboardNavigation();
                selectPreviousApp();
                break;
            case 40:
                // down key pressed
                initKeyboardNavigation();
                selectNextApp();
                break;
            case 13:
                // enter key pressed
                var selectedApp = getSelectedApp();
                if (selectedApp) {
                    setTipVisibility("none");
                    startApp(selectedApp);
                }
                break;
            case 27:
                // esc key pressed
                closeLauncherPanel();
                break;
        }
    }

    function init() {
        initBodyMask();

        appendCssReference();
        appendLauncherButton();
        appendLauncherPanel();
    }

    init();
}());