declare var Ext:Ext_Packages;
declare var Admin;
declare var CONFIG;

function startApplication() {
    var application:api.app.Application = api.app.Application.getApplication();
    var appBar = new api.app.AppBar(application);
    var appPanel = new app.ModuleAppPanel(appBar);

    api.dom.Body.get().appendChild(appBar);
    api.dom.Body.get().appendChild(appPanel);

    appPanel.init();

    var moduleDeleteDialog:app.remove.ModuleDeleteDialog = new app.remove.ModuleDeleteDialog();
    app.browse.DeleteModulePromptEvent.on((event:app.browse.DeleteModulePromptEvent) => {
        moduleDeleteDialog.setModuleToDelete(event.getModule());
        moduleDeleteDialog.open();
    });

    application.setLoaded(true);

    window.onmessage = (e:MessageEvent) => {
        if( e.data.appLauncherEvent ) {
            var eventType:api.app.AppLauncherEventType = api.app.AppLauncherEventType[<string>e.data.appLauncherEvent];
            if( eventType ==  api.app.AppLauncherEventType.Show ) {
                appPanel.activateCurrentKeyBindings();
            }
        }
    }
}

module components {
    export var detailPanel:app.browse.ModuleBrowseItemPanel;
}

function route(path:api.rest.Path) {
    var action = path.getElement(0);

    switch (action) {
    case 'edit':
        console.log("edit");
        break;
    case 'view' :
        console.log("view");
        break;
    default:
        new api.app.ShowAppBrowsePanelEvent().fire();
        break;
    }
}