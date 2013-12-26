declare var Ext:Ext_Packages;
declare var Admin;
declare var CONFIG;

module components {
    export var detailPanel: app_browse.TemplateBrowseItemPanel;
    export var gridPanel: app_browse.TemplateTreeGridPanel;
}

window.onload = () => {
    var appBar = new api_app.AppBar("Template Manager", new api_app.AppBarTabMenu("TemplateAppBarTabMenu"));
    var appPanel = new app.TemplateAppPanel(appBar);

    api_dom.Body.get().appendChild(appBar);
    api_dom.Body.get().appendChild(appPanel);

    appPanel.init();

    var siteTemplateDeleteDialog: app_delete.SiteTemplateDeleteDialog = new app_delete.SiteTemplateDeleteDialog();
    app_browse.DeleteSiteTemplatePromptEvent.on((event: app_browse.DeleteSiteTemplatePromptEvent) => {
        siteTemplateDeleteDialog.setSiteTemplateToDelete(event.getSiteTemplate());
        siteTemplateDeleteDialog.open();
    });


    if (window.parent["appLoaded"]) {
        window.parent["appLoaded"](getAppName());
    }

    window.onmessage = (e:MessageEvent) => {
        if( e.data.appLauncherEvent ) {
            var eventType:api_app.AppLauncherEventType = api_app.AppLauncherEventType[<string>e.data.appLauncherEvent];
            if( eventType ==  api_app.AppLauncherEventType.Show ) {
                appPanel.activateCurrentKeyBindings();
            }
        }
    }
};

function getAppName(): string {
    return jQuery(window.frameElement).data("wem-app");
}