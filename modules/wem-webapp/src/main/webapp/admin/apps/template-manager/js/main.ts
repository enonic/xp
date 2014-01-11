declare var Ext:Ext_Packages;
declare var Admin;
declare var CONFIG;

module components {
    export var detailPanel: app.browse.TemplateBrowseItemPanel;
    export var gridPanel: app.browse.TemplateTreeGridPanel;
}

window.onload = () => {
    var appBar = new api.app.AppBar("Template Manager", new api.app.AppBarTabMenu(true));
    var appPanel = new app.TemplateAppPanel(appBar);

    api.dom.Body.get().appendChild(appBar);
    api.dom.Body.get().appendChild(appPanel);

    appPanel.init();

    var siteTemplateDeleteDialog: app.remove.SiteTemplateDeleteDialog = new app.remove.SiteTemplateDeleteDialog();
    app.browse.event.DeleteSiteTemplatePromptEvent.on((event: app.browse.event.DeleteSiteTemplatePromptEvent) => {
        siteTemplateDeleteDialog.setSiteTemplateToDelete(event.getSiteTemplate());
        siteTemplateDeleteDialog.open();
    });


    if (window.parent["appLoaded"]) {
        window.parent["appLoaded"](getAppName());
    }

    window.onmessage = (e:MessageEvent) => {
        if( e.data.appLauncherEvent ) {
            var eventType:api.app.AppLauncherEventType = api.app.AppLauncherEventType[<string>e.data.appLauncherEvent];
            if( eventType ==  api.app.AppLauncherEventType.Show ) {
                appPanel.activateCurrentKeyBindings();
            }
        }
    }
};

function getAppName(): string {
    return jQuery(window.frameElement).data("wem-app");
}