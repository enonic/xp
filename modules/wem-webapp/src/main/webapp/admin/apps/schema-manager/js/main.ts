
declare var Ext:Ext_Packages;
declare var Admin;
declare var CONFIG;

module app {
    // Application id for uniquely identifying app
    export var id = 'schema-manager';
}

module components {
    export var detailPanel:app.browse.SchemaBrowseItemPanel;
    export var gridPanel:app.browse.SchemaTreeGridPanel;
    export var newSchemaDialog:app.create.NewSchemaDialog;
    export var schemaDeleteDialog:app.remove.SchemaDeleteDialog;
}

window.onload = () => {
    var appBar = new api.app.AppBar("Schema Manager", new api.app.AppBarTabMenu());
    var appPanel = new app.SchemaAppPanel(appBar);

    api.dom.Body.get().appendChild(appBar);
    api.dom.Body.get().appendChild(appPanel);

    appPanel.init();

    window.onmessage = (e:MessageEvent) => {
        if( e.data.appLauncherEvent ) {
            var eventType:api.app.AppLauncherEventType = api.app.AppLauncherEventType[<string>e.data.appLauncherEvent];
            if( eventType ==  api.app.AppLauncherEventType.Show ) {
                appPanel.activateCurrentKeyBindings();
            }
        }
    }
};
