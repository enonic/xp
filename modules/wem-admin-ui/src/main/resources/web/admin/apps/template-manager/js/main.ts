declare var Admin;
declare var CONFIG;

module components {
    export var detailPanel: app.browse.TemplateBrowseItemPanel;
}

function startApplication() {
    var application: api.app.Application = api.app.Application.getApplication();
    var appBar = new api.app.bar.AppBar(application);
    var appPanel = new app.TemplateAppPanel(appBar, application.getPath());

    api.dom.Body.get().appendChild(appBar);
    api.dom.Body.get().appendChild(appPanel);

    appPanel.init();

    var siteTemplateDeleteDialog: app.remove.SiteTemplateDeleteDialog = new app.remove.SiteTemplateDeleteDialog();
    app.browse.event.DeleteTemplatePromptEvent.on((event: app.browse.event.DeleteTemplatePromptEvent) => {
        siteTemplateDeleteDialog.setSiteTemplateToDelete(event.getTemplate());
        siteTemplateDeleteDialog.open();
    });

    application.setLoaded(true);

    window.onmessage = (e: MessageEvent) => {
        if (e.data.appLauncherEvent) {
            var eventType: api.app.AppLauncherEventType = api.app.AppLauncherEventType[<string>e.data.appLauncherEvent];
            if (eventType == api.app.AppLauncherEventType.Show) {
                appPanel.activateCurrentKeyBindings();
            }
        }
    }
}