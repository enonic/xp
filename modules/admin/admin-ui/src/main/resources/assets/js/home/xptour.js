var tourDialog;
var tourSteps = [];
var demoAppsInstalled = false;
var demoAppsLoadMask;
var canInstallDemoApps = false;
var isInstallingDemoAppsNow = false;
var demoAppsNames = ["com.enonic.app.superhero", "com.enonic.app.wireframe", "com.enonic.app.imagexpert"];
var marketDemoApps = [];
var isSystemAdmin = false;
var i18n = api.util.i18n;

exports.init = function () {
    initDialog();
    initTourSteps();

    checkAdminRights().then(function () {
        if (isSystemAdmin) {
            appendInstalAppStep();
        }
        setTourStep(1);
        api.dom.Body.get().appendChild(tourDialog);
        // Hack: Make sure the correct size is set on first-time run.
        api.ui.responsive.ResponsiveManager.fireResizeEvent();
    });
    
    return tourDialog;
};

function appendInstalAppStep() {
    tourSteps.push(createStep5());
    tourDialog.setTitle(i18n('tour.title.stepXofY', 1, 5));
}

function checkAdminRights() {
    return new api.security.auth.IsAuthenticatedRequest().sendAndParse().then(function (loginResult) {
        isSystemAdmin = loginResult.getPrincipals().some(function (principal) {
            return principal.equals(api.security.RoleKeys.ADMIN);
        });
    });
}

function initDialog() {
    tourDialog = new api.ui.dialog.ModalDialog(i18n('tour.title.stepXofY', 1, 4));
    tourDialog.addClass("xp-tour-dialog");

    initNavigation();
}

function initNavigation() {
    var previousStepAction = new api.ui.Action(i18n('tour.action.skip'));
    var previousStepActionButton = tourDialog.addAction(previousStepAction);

    var nextStepAction = new api.ui.Action(i18n('action.next'));
    var nextStepActionButton = tourDialog.addAction(nextStepAction);

    var currentStep = 1;
    previousStepAction.onExecuted(function () {
        if (currentStep === 1) {
            tourDialog.close();
        }
        else {
            currentStep--;
            if (currentStep === 1) {
                previousStepActionButton.setLabel(i18n('tour.action.skip'));
            }
            nextStepActionButton.setEnabled(true);
            nextStepActionButton.setLabel(i18n('action.next'));
            nextStepActionButton.removeClass("last-step");
            setTourStep(currentStep);

            if (demoAppsLoadMask) {
                demoAppsLoadMask.hide();
            }
        }
    });

    nextStepAction.onExecuted(function () {
        if (currentStep === tourSteps.length) {
            if (canInstallDemoApps) { // if install is hit
                nextStepActionButton.setLabel(i18n('tour.action.installing'));
                nextStepActionButton.setEnabled(false);
                isInstallingDemoAppsNow = true;

                wemQ.all(loadDemoApps()).spread(function () {
                    if (currentStep === tourSteps.length) { //if still on install apps page of xp tour
                        nextStepActionButton.setLabel(i18n('tour.action.finish'));
                        nextStepActionButton.addClass("last-step");
                        nextStepActionButton.setEnabled(true);
                    }
                    isInstallingDemoAppsNow = false;
                    canInstallDemoApps = false;
                });
            } else { // Finish button was hit
                tourDialog.close();
                nextStepActionButton.setLabel(i18n('action.next'));
                nextStepActionButton.removeClass("last-step");
                previousStepActionButton.setLabel(i18n('tour.action.skip'));
                currentStep = 1;
                setTourStep(currentStep);
            }

        }
        else {
            currentStep++;

            previousStepActionButton.setLabel(i18n('tour.action.previous'));
            setTourStep(currentStep);

            if (currentStep === tourSteps.length) {

                if (tourSteps.length == 5 && !demoAppsInstalled) {
                    var demoAppsContainer = api.dom.Element.fromHtmlElement(document.querySelector(".demo-apps"));

                    if (!demoAppsLoadMask) {
                        demoAppsLoadMask = new api.ui.mask.LoadMask(demoAppsContainer);
                        tourDialog.onHidden(function () {
                            demoAppsLoadMask.hide();
                        });
                    }

                    demoAppsContainer.removeClass("failed");

                    demoAppsLoadMask.show();

                    nextStepActionButton.setLabel(i18n('tour.action.finish'));
                    nextStepActionButton.addClass("last-step");

                    fetchDemoAppsFromMarket().then(function (apps) {

                        marketDemoApps = apps || [];
                        canInstallDemoApps = marketDemoApps.some(function (marketDemoApp) {
                            return marketDemoApp.getStatus() !== api.application.MarketAppStatus.INSTALLED;
                        });

                        demoAppsInstalled = !!apps && !canInstallDemoApps;

                        tourSteps[tourSteps.length - 1] = createStep5();

                        if (currentStep === tourSteps.length) { //if still on install apps page of xp tour
                            setTourStep(currentStep);
                            demoAppsContainer = api.dom.Element.fromHtmlElement(document.querySelector(".demo-apps"));
                            if (canInstallDemoApps) {
                                nextStepActionButton.setLabel(i18n('tour.action.install'));
                                nextStepActionButton.removeClass("last-step");
                            }
                        }
                    }).catch(function (err) {
                        api.DefaultErrorHandler.handle(err);
                        //Set text in demo-apps div that failed loading
                    }).finally(function () {
                        demoAppsContainer.toggleClass("failed", marketDemoApps.length == 0);
                        demoAppsLoadMask.hide();
                    });
                } else if (isInstallingDemoAppsNow) {
                    nextStepActionButton.setLabel(i18n('tour.action.installing'));
                    nextStepActionButton.setEnabled(false);
                } else if (canInstallDemoApps) {
                    nextStepActionButton.setLabel(i18n('tour.action.install'));
                } else {
                    nextStepActionButton.setLabel(i18n('tour.action.finish'));
                    nextStepActionButton.addClass("last-step");
                }

            }

        }
    });
}

function initTourSteps() {
    tourSteps = [createStep1(), createStep2(), createStep3(), createStep4()];
}

function createStep1() {
    var html = '<div class="xp-tour-step step-1">' +
               '    <div class="subtitle">' +
               '        <div class="subtitle-part-1">' + i18n('tour.step1.subtitle1') + '</div>' +
               '        <div class="subtitle-part-2">' + i18n('tour.step1.subtitle2') + '</div>' +
               '    </div>' +
               '    <div class="caption">' + i18n('tour.step1.caption') + '</div>' +
               '    <img src="' + CONFIG.adminUrl + '/common/images/app-icon.svg">' +
               '    <div class="text">' +
               '        <div class="paragraph1">' + i18n('tour.step1.paragraph1') + '</div>' +
               '        <ul>' +
               '            <li>' + i18n('tour.step1.system1') + '</li>' +
               '            <li>' + i18n('tour.step1.system2') + '</li>' +
               '            <li>' + i18n('tour.step1.system3') + '</li>' +
               '            <li>' + i18n('tour.step1.system4') + '</li>' +
               '            <li>' + i18n('tour.step1.system5') + '</li>' +
               '        </ul>' +
               '        <div class="paragraph2">' + i18n('tour.step1.paragraph2') + '</div>' +
               '    </div>' +
               '</div>';
    var element = api.dom.Element.fromString(html);
    return element;
}

function createStep2() {
    var html = '<div class="xp-tour-step step-2">' +
               '    <div class="subtitle">' +
               '        <div class="subtitle-part-1">' + i18n('tour.step2.subtitle1') + '</div>' +
               '        <div class="subtitle-part-2">' + i18n('tour.step2.subtitle2') + '</div>' +
               '    </div>' +
               '    <div class="caption">' + i18n('tour.step2.caption') + '</div>' +
               '    <img src="' + CONFIG.adminUrl + '/common/images/launcher.svg">' +
               '    <div class="text">' +
               '        <div class="paragraph1">' + i18n('tour.step2.paragraph1') + '</div>' +
               '        <div class="paragraph2">' + i18n('tour.step2.paragraph2') + '</div>' +
               '    </div>' +
               '</div>';
    var element = api.dom.Element.fromString(html);
    return element;
}

function createStep3() {
    var html = '<div class="xp-tour-step step-3">' +
               '    <div class="subtitle">' +
               '        <div class="subtitle-part-1">' + i18n('tour.step3.subtitle1') + '</div>' +
               '        <div class="subtitle-part-2">' + i18n('tour.step3.subtitle2') + '</div>' +
               '    </div>' +
               '    <div class="caption">' + i18n('tour.step3.caption') + '</div>' +
               '    <img src="' + CONFIG.adminUrl + '/common/images/market.svg">' +
               '    <div class="text">' +
               '        <div class="paragraph1">' + i18n('tour.step3.paragraph1') +
               ' <a href="/admin/tool/com.enonic.xp.app.applications/main" target="_blank">' +
               i18n('tour.step3.paragraph1hreftext') + '</a>.</div>' +
               '        <div class="paragraph2"><a href="https://market.enonic.com/" target="_blank">' +
               i18n('tour.step3.paragraph2hreftext') + '</a> ' + i18n('tour.step3.paragraph2') + '</div>' +
               '        <div class="paragraph3">' + i18n('tour.step3.paragraph3') +
               ' <a href="http://docs.enonic.com/en/latest/" target="_blank">' + i18n('tour.step3.paragraph3hreftext') +
               '</a>.</div>' +
               '    </div>' +
               '</div>';
    var element = api.dom.Element.fromString(html);
    return element;
}

function createStep4() {
    var html = '<div class="xp-tour-step step-4">' +
               '    <div class="subtitle">' +
               '        <div class="subtitle-part-1">' + i18n('tour.step4.subtitle1') + '</div>' +
               '        <div class="subtitle-part-2">' + i18n('tour.step4.subtitle2') + '</div>' +
               '    </div>' +
               '    <div class="caption">' + i18n('tour.step4.caption') + '</div>' +
               '    <img src="' + CONFIG.adminUrl + '/common/images/studio.svg">' +
               '    <div class="text">' +
               '        <div class="paragraph1">' + i18n('tour.step4.paragraph1part1') +
               ' <a href="/admin/tool/com.enonic.xp.app.contentstudio/main" target="_blank">' +
               i18n('tour.step4.paragraph1hreftext') + '</a> - ' + i18n('tour.step4.paragraph1part2') + '</div>' +
               '        <div class="paragraph2">' + i18n('tour.step4.paragraph2part1') +
               ' <a href="https://market.enonic.com/vendors/enonic/com.enonic.app.ga" target="_blank">' +
               i18n('tour.step4.paragraph2hreftext') + '</a> ' + i18n('tour.step4.paragraph2part2') + '</div>' +
               '    </div>' +
               '</div>';
    var element = api.dom.Element.fromString(html);
    return element;
}

function createStep5() {
    var html = '<div class="xp-tour-step step-5">' +
               '    <div class="subtitle">' +
               '        <div class="subtitle-part-1">' + i18n('tour.step5.subtitle1') + '</div>' +
               '        <div class="subtitle-part-2">' + i18n('tour.step5.subtitle2') + '</div>' +
               '    </div>' +
               '    <div class="caption">' + i18n('tour.step5.caption') + '</div>' +
               '    <div class="text">' +
               '        <div class="paragraph1">' + i18n('tour.step5.paragraph1') + '</div>' +
               '    </div>' +
               '    <div class="demo-apps">' +

               getAppsDiv() +
               '    </div>'
                '</div>';

    var element = api.dom.Element.fromString(html);
    return element;
}

function getAppsDiv() {
    return marketDemoApps.length > 0 ?
           getDemoAppsHtml() :
           '        <div class="demo-apps-text">' + i18n('tour.apps.notavailable') + '</div>';
}

function fetchDemoAppsFromMarket() {
    var appPromises = [];

    appPromises.push(
        new api.application.ListApplicationsRequest().sendAndParse(),
        new api.application.ListMarketApplicationsRequest()
            .setStart(0)
            .setCount(40)
            .setVersion(CONFIG.xpVersion)
            .setIds(demoAppsNames)
            .sendAndParse()
    );

    return wemQ.all(appPromises).spread(function (installedApplications, marketApplications) {
        var apps = marketApplications.getApplications();
        apps.forEach(function (marketDemoApp) {
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
        return apps;
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
            statusContainer.textContent = i18n('tour.apps.status.installed');
        }
        else {
            statusContainer.className = "demo-app-status failure";
            statusContainer.textContent = i18n('tour.apps.status.failed');
        }
    }).catch(function (err) {
        api.DefaultErrorHandler.handle(err);
    });

}

function updateHeaderStep(step) {
    const totalSteps = isSystemAdmin ? "5" : "4";
    tourDialog.setTitle(i18n('tour.title.stepXofY', step, totalSteps));
}

function setTourStep(step) {
    updateHeaderStep(step);
    tourDialog.getContentPanel().removeChildren();
    tourDialog.appendChildToContentPanel(tourSteps[step - 1]);
}