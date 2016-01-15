(function () {
    var adminUrl = "/admin/tool/com.enonic.xp.admin.ui/launcher";
    var launcherPanel, bodyMask, launcherButton;
    var isHomeApp = (window.CONFIG && window.CONFIG.autoOpenLauncher);

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
        link.setAttribute("href", adminUrl);

        link.onload = function () {
            var clonedDiv = link.import.querySelector('.launcher-main-container').cloneNode(true);
            while (clonedDiv.childNodes.length > 0) {
                container.appendChild(clonedDiv.childNodes[0]);
            }

            if (isHomeApp) {
                openLauncherPanel();
                launcherButton.focus();
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
        var anchorEl = app.querySelector("a");
        if (anchorEl && anchorEl.click) {
            anchorEl.click();
        }
    }

    function onKeyPressed(e) {
        e.stopPropagation();
        switch(e.keyCode) {
            case 38:
                // up key pressed
                launcherButton.blur();
                selectPreviousApp();
                break;
            case 40:
                // down key pressed
                launcherButton.blur();
                selectNextApp();
                break;
            case 13:
                // enter key pressed
                var selectedApp = getSelectedApp();
                if (selectedApp) {
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
        appendLauncherToolbar();
        appendLauncherPanel();
    }

    init();
}());