var tourDialog;
var tourSteps = [];
var canInstallDemoApps = false;
var demoApps = [];

exports.init = function () {
    initDialog();
    initDemoApps();
    initTourSteps();
    setTourStep(1);
    api.dom.Body.get().appendChild(tourDialog);
};

function initDialog() {
    tourDialog = new api.ui.dialog.ModalDialog({title: new api.ui.dialog.ModalDialogHeader("Welcome Tour - Step 1 of 5")});
    tourDialog.addClass("xp-tour-dialog")
    document.querySelector(".xp-tour").addEventListener("click", function () {
        tourDialog.open()
    });

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
            nextStepActionButton.setLabel("Next");
            nextStepActionButton.removeClass("last-step");
            setTourStep(currentStep);
        }
    });

    nextStepAction.onExecuted(function () {
        if (currentStep === tourSteps.length) {
            tourDialog.close();
            nextStepActionButton.setLabel("Next");
            nextStepActionButton.removeClass("last-step");
            previousStepActionButton.setLabel("Skip Tour");
            currentStep = 1;
            setTourStep(currentStep);
        }
        else {
            currentStep++;
            if (currentStep === tourSteps.length) {
                nextStepActionButton.setLabel("Finish");
                nextStepActionButton.addClass("last-step");
            }
            previousStepActionButton.setLabel("Previous");
            setTourStep(currentStep);
        }
    });
}

function initTourSteps() {
    tourSteps = [createStep1(), createStep2(), createStep3(), createStep4()];

    checkAllDemoAppsInstalled().then(function () {
        tourSteps.push(createStep5());
    });

}

function initDemoApps() {
    var demoApp1 = {
        id: "com.enonic.app.superhero",
        name: "Superhero Blog",
        url: "https://market.enonic.com/vendors/enonic/com.enonic.app.superhero",
        installUrl: "http://repo.enonic.com/public/com/enonic/app/superhero/1.5.0/superhero-1.5.0.jar",
        iconUrl: "https://market.enonic.com/applications/_/attachment/inline/0e282996-27df-4301-a1ba-29959f55595d:44e64fe5aea828f71c92b0d2d9ce96aa021b446b/superhero-blog_cleaned.svg"
    }

    var demoApp2 = {
        id: "com.enonic.app.xphoot",
        name: "XPHOOT",
        url: "https://market.enonic.com/vendors/enonic/com.enonic.app.xphoot",
        installUrl: "http://repo.enonic.com/public/com/enonic/app/xphoot/1.0.0/xphoot-1.0.0.jar",
        iconUrl: "https://market.enonic.com/applications/_/attachment/inline/92c3d127-161d-45ac-bea1-c288875b27df:ba063aabcb55d5ae88ef1a469c2c60477b0e4d3a/xphoot.svg"
    }

    var demoApp3 = {
        id: "com.enonic.app.googlemaps",
        name: "Google Maps",
        url: "https://market.enonic.com/vendors/enonic/com.enonic.app.googlemaps",
        installUrl: "http://repo.enonic.com/public/com/enonic/app/googlemaps/1.0.1/googlemaps-1.0.1.jar",
        iconUrl: "https://market.enonic.com/applications/_/attachment/inline/281b0da2-0130-4f75-8604-8b534713f456:71dcb89f3e40aa8415262a0d870aa419faab6a05/googlemap.svg"
    }

    demoApps = [demoApp1, demoApp2, demoApp3];
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
               '    </div>' +
               '    <div class="install-apps">' +
               '        <button class="xp-admin-common-button action-button install-apps-button"><span>Install Demo Apps</span></button>    ' +
               '    </div>';
    '</div>';

    var element = api.dom.Element.fromString(html);
    return element;
}

function checkAllDemoAppsInstalled() {
    return new api.application.ListApplicationsRequest().sendAndParse().then(function (applications) {
        demoApps.forEach(function (demoApp) {
            demoApp["isInstalled"] = applications.some(function (application) {
                return application.id === demoApp.id
            });
        });

        canInstallDemoApps = demoApps.some(function (demoApp) {
            return !demoApp["isInstalled"];
        });
    });

}

function getDemoAppsHtml() {
    var html = "";
    demoApps.forEach(function (demoApp, index) {
        var installed = demoApp["isInstalled"] ? "Installed" : "";
        html += '<div class="demo-app demo-app-' + index + '">' +
                '    <a href="' + demoApps[index].url + '" target="_blank">' +
                '    <img class="demo-app-superhero" src="' + demoApps[index].iconUrl + '">' +
                '    <div class="demo-app-title">' + demoApps[index].name + '</div>' +
                '    <div class="demo-app-status ' + installed.toLowerCase() + '">' + installed + '</div>   ' +
                '    </a>' +
                '</div>'
    });

    return html;
}

function setupInstallAppsButton() {
    var installButton = document.querySelector(".install-apps-button");
    installButton.addEventListener("click", function () {
        loadDemoApps();
        installButton.style.visibility = "hidden";
    });
}

function loadDemoApps() {
    enableApplicationServerEventsListener();

    demoApps.forEach(function (demoApp, index) {
        if (!demoApp["isInstalled"]) {
            loadApp(demoApp.installUrl, document.querySelector(".demo-app-" + index));
        }
        else {
            document.querySelector(".demo-app-" + index).appendChild(
                new api.dom.DivEl("demo-app-status").setHtml("Installed").getHTMLElement());
        }
    });
}

// Required to update progress bar
function enableApplicationServerEventsListener() {
    var application = new api.app.Application('applications', 'Applications', 'AM', 'applications');
    application.setPath(api.rest.Path.fromString("/"));
    application.setWindow(window);
    var serverEventsListener = new api.app.ServerEventsListener([application]);
    serverEventsListener.start();
}

function loadApp(url, container) {
    var progressBar = new api.ui.ProgressBar(0);
    var progressHandler = function (event) {
        if (event.getApplicationUrl() == url &&
            event.getEventType() == api.application.ApplicationEventType.PROGRESS) {

            progressBar.setValue(event.getProgress());
        }
    };

    api.application.ApplicationEvent.on(progressHandler);
    container.appendChild(progressBar.getHTMLElement());

    new api.application.InstallUrlApplicationRequest(url).sendAndParse().then(function (result) {
        api.application.ApplicationEvent.un(progressHandler);
        progressBar.remove();
        var status = new api.dom.DivEl("demo-app-status");
        if (!result.getFailure()) {
            status.setHtml("Installed")
        }
        else {
            status.addClass("failure");
            status.setHtml("Failed");
        }

        container.appendChild(status.getHTMLElement());
    });

}

function updateHeaderStep(step) {
    tourDialog.setTitle("Welcome Tour - Step " + step + " of 5");
}

function setTourStep(step) {
    updateHeaderStep(step);
    tourDialog.getContentPanel().removeChildren();
    tourDialog.appendChildToContentPanel(tourSteps[step - 1]);

    if (step === tourSteps.length) {
        if (canInstallDemoApps) {
            setupInstallAppsButton();
        }
        else {
            document.querySelector(".install-apps-button").style.visibility = "hidden";
        }

    }

}