var adminUrl = window.CONFIG && window.CONFIG.adminUrl || "/admin";
var launcherUrl = adminUrl + (adminUrl.slice(-1) === '/' ? "" : "/" ) + "tool/com.enonic.xp.app.main/launcher";
var launcherPanel, launcherButton, launcherMainContainer;
var autoOpenLauncher = window.CONFIG && window.CONFIG.autoOpenLauncher;
var appId = window.CONFIG ? window.CONFIG.appId : "";

function appendLauncherButton() {
    launcherButton = document.createElement("button");
    launcherButton.setAttribute("class", "launcher-button " + getColorClass());
    launcherButton.classList.add("hidden");

    var span = document.createElement("span");
    span.setAttribute("class", "lines");
    launcherButton.appendChild(span);

    launcherButton.addEventListener("click", togglePanelState);

    setTimeout(function() {
        var container = document.querySelector(".appbar") || document.body;
        container.appendChild(launcherButton);
        launcherButton.focus();
    }, 700);
}

function getColorClass() {
    if (document.querySelector(".appbar") || document.querySelector(".home-main-container")) {
        return "";
    }

    return "dark";
}

function togglePanelState() {
    if (isPanelExpanded()) {
        closeLauncherPanel();
    } else {
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

    document.body.appendChild(div);

    launcherPanel = div;
}

function onLauncherClick(e) {
    if (!launcherPanel || !launcherMainContainer) {
        return;
    }
    var isClickOutside = !launcherPanel.contains(e.target) && !launcherButton.contains(e.target);
    if (isClickOutside && !launcherMainContainer.getAttribute("hidden") && !isModalDialogActiveOnHomePage(e.target) && !isDashboardIcon(e.target)) {
        closeLauncherPanel();
    }
}

function isDashboardIcon(element) {
    if (!window.wemjq) {
        return false;
    }
    return (wemjq(element).closest(".dashboard-item").length > 0);
}

function isModalDialogActiveOnHomePage(element) {
    if (!window.wemjq) {
        return false;
    }
    return (window.CONFIG.appId == "home") &&
           (document.body.classList.contains("modal-dialog") || (wemjq(element).closest(".xp-admin-common-modal-dialog").length > 0));
}

function createLauncherLink(container) {
    var link = document.createElement("link");
    var url = launcherUrl + "?t=" + Date.now();

    link.setAttribute("rel", "import");
    link.setAttribute("href", url);
    link.setAttribute("async", "");

    link.onload = function () {
        launcherMainContainer = link.import.querySelector('.launcher-main-container');
        launcherMainContainer.setAttribute("hidden", "true");
        if (window.CONFIG.appId == "home") {
            launcherMainContainer.classList.add("home");
        }
        container.appendChild(launcherMainContainer);
        addLongClickHandler(container);

        if (autoOpenLauncher) {
            openLauncherPanel();
            launcherButton.focus();
        } else {
            var appTiles = container.querySelector('.launcher-app-container').querySelectorAll("a");
            for (var i = 0; i < appTiles.length; i++) {
                appTiles[i].addEventListener("click", closeLauncherPanel.bind(this, true));
            }
        }
        highlightActiveApp();
    };

    return link;
}

function openWindow(windowArr, anchorEl) {
    var windowId = anchorEl.getAttribute("data-id");

    if (windowArr[windowId] && !windowArr[windowId].closed) {
        windowArr[windowId].focus();
    }
    else {
        windowArr[windowId] = window.open(anchorEl.href, windowId);
    }
}

function addLongClickHandler(container) {
    var longpress = false;
    var startTime, endTime;
    var toolWindows = [];

    var appTiles = container.querySelector('.launcher-app-container').querySelectorAll("a");
    for (var i = 0; i < appTiles.length; i++) {
        appTiles[i].addEventListener("click", function (e) {
            if (window.CONFIG.appId === e.currentTarget.getAttribute("data-id") && window.CONFIG.appId === "home") {
                e.preventDefault();
                return;
            }

            if (longpress) {
                e.preventDefault();
                document.location.href = this.href;
            }
            else if (navigator.userAgent.search("Chrome") > -1 ) {
                e.preventDefault();
                openWindow(toolWindows, e.currentTarget);
            }
        });
        appTiles[i].addEventListener("mousedown", function () {
            startTime = new Date().getTime();
        });
        appTiles[i].addEventListener("mouseup", function () {
            endTime = new Date().getTime();
            longpress = (endTime - startTime >= 500);
        });
    }
}

function isPanelExpanded() {
    return launcherPanel.classList.contains("visible");
}

function openLauncherPanel() {
    launcherMainContainer.removeAttribute("hidden");
    listenToKeyboardEvents();
    toggleButton();
    launcherPanel.classList.remove("hidden", "slideout");
    launcherPanel.classList.add("visible");
    document.addEventListener('click', onLauncherClick);
}

function closeLauncherPanel(skipTransition) {
    document.removeEventListener('click', onLauncherClick);
    launcherMainContainer.setAttribute("hidden", "true");
    unlistenToKeyboardEvents();
    launcherPanel.classList.remove("visible");
    launcherPanel.classList.add((skipTransition === true) ? "hidden" : "slideout");
    toggleButton();
    unselectCurrentApp();
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
        if (appRows[i].id === appId) {
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
    }
}

function addApplicationsListeners() {
    var triesLeft = 3;
    var intervalID = setInterval(function () {
        var initialized = initApplicationsListeners();
        if (!initialized && triesLeft > 0) {
            triesLeft -= 1;
        } else {
            clearInterval(intervalID);
        }
    }, 3000);
}

function initApplicationsListeners() {
    if (api.application.ApplicationEvent) {
        api.application.ApplicationEvent.on(function (event) {
            switch (event.getEventType()) {
            case api.application.ApplicationEventType.STARTED:
            case api.application.ApplicationEventType.STOPPED:
                reloadLauncher();
                break;
            }
        });
        return true;
    }
    return false;
}

function reloadLauncher() {
    launcherPanel.remove();
    launcherButton.remove();
    appendLauncherButton();
    appendLauncherPanel();
    addApplicationsListeners();
}

exports.init = function () {
    appendLauncherButton();
    appendLauncherPanel();
    addApplicationsListeners();
};
