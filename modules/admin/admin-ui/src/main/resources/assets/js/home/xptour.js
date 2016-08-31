var tourDialog;
var tourSteps = [];
var canInstallDemoApps = false;
var demoAppsNames = ["com.enonic.app.superhero", "com.enonic.app.xphoot", "com.enonic.app.googlemaps"];
var marketDemoApps = [];
var isInstallingNow = false;

exports.init = function () {
    initDialog();
    initTourSteps();
    setTourStep(1);
    api.dom.Body.get().appendChild(tourDialog);
    
    return tourDialog;
};

function initDialog() {
    tourDialog = new api.ui.dialog.ModalDialog({
        title: new api.ui.dialog.ModalDialogHeader("Welcome Tour - Step 1 of 5"),
        ignoreClickOutside: true
    });
    tourDialog.addClass("xp-tour-dialog");

    initNavigation();
}

function initNavigation() {
    var previousStepAction = new api.ui.Action("Skip Tour");
    var previousStepActionButton = tourDialog.addAction(previousStepAction);

    var nextStepAction = new api.ui.Action("Next");
    var nextStepActionButton = tourDialog.addAction(nextStepAction);

    var currentStep = 1;
    previousStepAction.onExecuted(function () {
        if (currentStep === 1) {
            tourDialog.close();
        }
        else {
            currentStep--;
            if (currentStep === 1) {
                previousStepActionButton.setLabel("Skip Tour");
            }
            nextStepActionButton.setEnabled(true);
            nextStepActionButton.setLabel("Next");
            nextStepActionButton.removeClass("last-step");
            setTourStep(currentStep);
        }
    });

    nextStepAction.onExecuted(function () {
        if (currentStep === tourSteps.length) {
            if (canInstallDemoApps) { // if install is hit
                nextStepActionButton.setLabel("Installing...");
                nextStepActionButton.setEnabled(false);
                isInstallingNow = true;
                
                wemQ.all(loadDemoApps()).spread(function () {
                    if (currentStep === tourSteps.length) { //if still on install apps page of xp tour
                        nextStepActionButton.setLabel("Finish");
                        nextStepActionButton.addClass("last-step");
                        nextStepActionButton.setEnabled(true);
                    }
                    isInstallingNow = false;
                    canInstallDemoApps = false;
                });
            } else { // Finish button was hit
                tourDialog.close();
                nextStepActionButton.setLabel("Next");
                nextStepActionButton.removeClass("last-step");
                previousStepActionButton.setLabel("Skip Tour");
                currentStep = 1;
                setTourStep(currentStep);
            }

        }
        else {
            currentStep++;
            if (currentStep === tourSteps.length) {
                if (isInstallingNow) {
                    nextStepActionButton.setLabel("Installing...");
                    nextStepActionButton.setEnabled(false);
                } else if (canInstallDemoApps) {
                    nextStepActionButton.setLabel("Install Apps");
                } else {
                    nextStepActionButton.setLabel("Finish");
                    nextStepActionButton.addClass("last-step");
                }

            }
            previousStepActionButton.setLabel("Previous");
            setTourStep(currentStep);
        }
    });
}

function initTourSteps() {
    tourSteps = [createStep1(), createStep2(), createStep3(), createStep4()];

    fetchDemoAppsFromMarket().then(function () {
        canInstallDemoApps = marketDemoApps.some(function (marketDemoApp) {
            return marketDemoApp.getStatus() !== api.application.MarketAppStatus.INSTALLED;
        });

        tourSteps.push(createStep5());
    }).catch(function (err) {
        api.DefaultErrorHandler.handle(err);
    });

}

function createStep1() {
    var html = '<div class="xp-tour-step step-1">' +
               '    <div class="subtitle">' +
               '        <div class="subtitle-part-1">Welcome to Enonic XP! </div>' +
               '        <div class="subtitle-part-2">Complete this tour to get started with Enonic XP</div>' +
               '    </div>' +
               '    <div class="caption">Enonic XP - The Web Operating System</div>' +
               '    <img src="/admin/common/images/app-icon.svg">' +
               '    <div class="text">' +
               '        <div class="paragraph1">Enonic XP is a powerful platform for building highly scalable, customer tailored applications and sites. It is four systems in one: </div>' +
               '        <ul>' +
               '            <li>Database/Storage</li>' +
               '            <li>Search</li>' +
               '            <li>Application Engine</li>' +
               '            <li>Identity/Role system</li>' +
               '            <li>Topped off with a powerful CMS.</li>' +
               '        </ul>' +
               '        <div class="paragraph2">Similar to a regular operating system - with XP installed you have everything you need to deploy and run custom applications. This is why we call it a ‘Web Operating System’.</div>' +
               '    </div>' +
               '</div>';
    var element = api.dom.Element.fromString(html);
    return element;
}

function createStep2() {
    var html = '<div class="xp-tour-step step-2">' +
               '    <div class="subtitle">' +
               '        <div class="subtitle-part-1">Did you know? </div>' +
               '        <div class="subtitle-part-2">You can build your own tools and add them to this menu!</div>' +
               '    </div>' +
               '    <div class="caption">Tools and Navigation</div>' +
               '    <img src="/admin/common/images/launcher.svg">' +
               '    <div class="text">' +
               '        <div class="paragraph1">XP admin is all about tools. You are currently in the ‘Home’ tool. Navigate to different tools using the menu icon which is available at the top right. The current tool is always highlighted.</div>' +
               '        <div class="paragraph2">Tools are launched in separate browser tabs so you can easily navigate between them later. Tools can also be bookmarked for direct access.</div>' +
               '    </div>' +
               '</div>';
    var element = api.dom.Element.fromString(html);
    return element;
}

function createStep3() {
    var html = '<div class="xp-tour-step step-3">' +
               '    <div class="subtitle">' +
               '        <div class="subtitle-part-1">Did you know? </div>' +
               '        <div class="subtitle-part-2">You can contribute to Enonic Market by submitting your apps!</div>' +
               '    </div>' +
               '    <div class="caption">Applications and Enonic Market</div>' +
               '    <img src="/admin/common/images/market.svg">' +
               '    <div class="text">' +
               '        <div class="paragraph1">Enonic XP is all about applications. Using the <a href="/admin/tool/com.enonic.xp.admin.ui/applications" target="_blank">Applications tool</a> you can create your own apps or install ready-2-run applications from Enonic Market.</div>' +
               '        <div class="paragraph2"><a href="https://market.enonic.com/" target="_blank">Enonic Market</a> also features libraries and starters for developers to get going quickly. Applications are primarily built with Javascript - but can also include Java since XP is running on the Java Virtual Machine.</div>' +
               '        <div class="paragraph3">Learn more about building applications from our <a href="http://docs.enonic.com/en/latest/" target="_blank">documentation</a>.</div>' +
               '    </div>' +
               '</div>';
    var element = api.dom.Element.fromString(html);
    return element;
}

function createStep4() {
    var html = '<div class="xp-tour-step step-4">' +
               '    <div class="subtitle">' +
               '        <div class="subtitle-part-1">Did you know? </div>' +
               '        <div class="subtitle-part-2">You can easily add 3rd party services like Google Analytics to the CMS</div>' +
               '    </div>' +
               '    <div class="caption">Embedded CMS - Content Studio</div>' +
               '    <img src="/admin/common/images/studio.svg">' +
               '    <div class="text">' +
               '        <div class="paragraph1">A valuable part of XP is the <a href="/admin/tool/com.enonic.xp.admin.ui/content-studio" target="_blank">Content Studio tool</a> - its an awesome CMS that can be used to compose websites of any kind - or simply to make your applications more dynamic.</div>' +
               '        <div class="paragraph2">The interface is praised by users as it and combines simplicity of use with powerful features - and it’s fully responsive too! It can also be extended with 3rd party services like the <a href="https://market.enonic.com/vendors/enonic/com.enonic.app.ga" target="_blank">Google Analytics app</a> for the enjoyment of your site’s editors.</div>' +
               '    </div>' +
               '</div>';
    var element = api.dom.Element.fromString(html);
    return element;
}

function createStep5() {
    var html = '<div class="xp-tour-step step-5">' +
               '    <div class="subtitle">' +
               '        <div class="subtitle-part-1">Custom apps you say? </div>' +
               '        <div class="subtitle-part-2">Simply choose Install + Upload in the Applications Tool</div>' +
               '    </div>' +
               '    <div class="caption">Install Demo Applications</div>' +
               '    <div class="text">' +
               '        <div class="paragraph1">If you are evaluating or just testing Enonic XP, let’s install some sample applications from Enonic Market - showing you some of Enonic XP’s capabilities.</div>' +
               '    </div>' +
               '    <div class="demo-apps">' +
               getDemoAppsHtml() +
               '    </div>'
    '</div>';

    var element = api.dom.Element.fromString(html);
    return element;
}

function fetchDemoAppsFromMarket() {
    var appPromises = [];

    appPromises.push(new api.application.ListApplicationsRequest().sendAndParse(), new api.application.ListMarketApplicationsRequest()
        .setStart(0)
        .setCount(40)
        .setVersion(CONFIG.xpVersion)
        .sendAndParse()
        .then(function (response) {
            return response.getApplications().filter(function (marketApp) {
                return demoAppsNames.some(function (demoAppName) {
                    if (marketApp.getName() === demoAppName) {
                        marketDemoApps.push(marketApp);
                        return true;
                    }
                })
            });
        }));

    return wemQ.all(appPromises).spread(function (installedApplications, marketDemoApps) {
        marketDemoApps.forEach(function (marketDemoApp) {
            for (var i = 0; i < installedApplications.length; i++) {
                if (marketDemoApp.getAppKey().equals(installedApplications[i].getApplicationKey())) {
                    if (api.application.MarketApplicationsFetcher.installedAppCanBeUpdated(marketDemoApp, installedApplications[i])) {
                        marketDemoApp.setStatus(api.application.MarketAppStatus.OLDER_VERSION_INSTALLED);
                    } else {
                        marketDemoApp.setStatus(api.application.MarketAppStatus.INSTALLED);
                    }
                    break;
                }
            }
        });
    }).catch(function (err) {
        api.DefaultErrorHandler.handle(err);
    });
}

function getDemoAppsHtml() {
    var html = "";
    marketDemoApps.forEach(function (marketDemoApp) {
        var status = api.application.MarketAppStatusFormatter.formatStatus(marketDemoApp.getStatus());

        html += '<div class="demo-app" id="' + marketDemoApp.getName() + '">' +
                '    <a href="' + marketDemoApp.getUrl() + '" target="_blank">' +
                '    <img class="demo-app-icon" src="' + marketDemoApp.getIconUrl() + '">' +
                '    <div class="demo-app-title">' + marketDemoApp.getDisplayName() + '</div>' +
                '    </a>' +
                '    <div class="demo-app-status ' + status.toLowerCase() + '">' + status + '</div>' +
                '</div>'
    });

    return html;
}

function loadDemoApps() {
    enableApplicationServerEventsListener();

    var loadingAppsPromises = [];

    marketDemoApps.forEach(function (marketDemoApp) {
        if (marketDemoApp.getStatus() !== api.application.MarketAppStatus.INSTALLED) {
            loadingAppsPromises.push(loadApp(marketDemoApp));
        }
    });

    return loadingAppsPromises;
}

// Required to update progress bar
function enableApplicationServerEventsListener() {
    var application = new api.app.Application('applications', 'Applications', 'AM', 'applications');
    application.setPath(api.rest.Path.fromString("/"));
    application.setWindow(window);
    var serverEventsListener = new api.app.ServerEventsListener([application]);
    serverEventsListener.start();
}

function loadApp(marketDemoApp) {
    var url = marketDemoApp.getLatestVersionDownloadUrl();
    var demoAppContainer = document.getElementById(marketDemoApp.getName());

    var progressBar = new api.ui.ProgressBar(0);
    var progressHandler = function (event) {
        if (event.getApplicationUrl() == url &&
            event.getEventType() == api.application.ApplicationEventType.PROGRESS) {

            progressBar.setValue(event.getProgress());
        }
    };

    api.application.ApplicationEvent.on(progressHandler);
    demoAppContainer.appendChild(progressBar.getHTMLElement());

    return new api.application.InstallUrlApplicationRequest(url).sendAndParse().then(function (result) {
        api.application.ApplicationEvent.un(progressHandler);
        progressBar.remove();

        var statusContainer = tourSteps[tourSteps.length - 1].findChildById(marketDemoApp.getName(), true).getHTMLElement().querySelector(
            ".demo-app-status");
        if (!result.getFailure()) {
            statusContainer.className = "demo-app-status installed";
            statusContainer.textContent = "Installed";
        }
        else {
            statusContainer.className = "demo-app-status failure";
            statusContainer.textContent = "Failed";
        }
    }).catch(function (err) {
        api.DefaultErrorHandler.handle(err);
    });

}

function updateHeaderStep(step) {
    tourDialog.setTitle("Welcome Tour - Step " + step + " of 5");
}

function setTourStep(step) {
    updateHeaderStep(step);
    tourDialog.getContentPanel().removeChildren();
    tourDialog.appendChildToContentPanel(tourSteps[step - 1]);
}