declare var Ext:Ext_Packages;
declare var Admin;
declare var CONFIG;

import ModuleSummary = api.module.ModuleSummary;

function startApplication() {
    var application:api.app.Application = api.app.Application.getApplication();
    var appBar = new api.app.AppBar(application);
    var appPanel = new app.ModuleAppPanel(appBar);

    api.dom.Body.get().appendChild(appBar);
    api.dom.Body.get().appendChild(appPanel);

    appPanel.init();

    registerEvents();

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

function registerEvents() {
    var moduleDeleteDialog:app.remove.ModuleDeleteDialog = new app.remove.ModuleDeleteDialog();
    app.browse.DeleteModulePromptEvent.on((event:app.browse.DeleteModulePromptEvent) => {
        moduleDeleteDialog.setModuleToDelete(event.getModule());
        moduleDeleteDialog.open();
    });

    app.browse.StopModuleEvent.on((event: app.browse.StopModuleEvent) => {
        var moduleKeys: string[] = event.getModules().map<string>((mod: ModuleSummary) => {
            return mod.getModuleKey().toString();
        });
        new api.module.StopModuleRequest(moduleKeys).sendAndParse();
    });
    app.browse.StartModuleEvent.on((event: app.browse.StartModuleEvent) => {
        var moduleKeys: string[] = event.getModules().map<string>((mod: ModuleSummary) => {
            return mod.getModuleKey().toString();
        });
        new api.module.StartModuleRequest(moduleKeys).sendAndParse();
    });
    app.browse.UpdateModuleEvent.on((event: app.browse.UpdateModuleEvent) => {
        var moduleKeys: string[] = event.getModules().map<string>((mod: ModuleSummary) => {
            return mod.getModuleKey().toString();
        });
        new api.module.UpdateModuleRequest(moduleKeys).sendAndParse();
    });
    app.browse.UninstallModuleEvent.on((event: app.browse.UninstallModuleEvent) => {
        var moduleKeys: string[] = event.getModules().map<string>((mod: ModuleSummary) => {
            return mod.getModuleKey().toString();
        });
        new api.module.UninstallModuleRequest(moduleKeys).sendAndParse();
    });
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