
declare var Ext;
declare var Admin;
declare var CONFIG;

module app {
    // Application id for uniquely identifying app
    export var id = 'schema-manager';
}

module components {
    export var detailPanel:app_browse.SchemaBrowseItemPanel;
    export var gridPanel:app_browse.SchemaTreeGridPanel;
    export var newSchemaDialog:app_new.NewSchemaDialog;
    export var schemaDeleteDialog:app_delete.SchemaDeleteDialog;
}

window.onload = () => {
    var appBar = new api_app.AppBar("Schema Manager", new api_app.AppBarTabMenu("SchemaAppBarTabMenu"));
    var appPanel = new app.SchemaAppPanel(appBar);

    api_dom.Body.get().appendChild(appBar);
    api_dom.Body.get().appendChild(appPanel);

    appPanel.init();
};
