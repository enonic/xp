///<reference path='../../../api/js/lib/ExtJs.d.ts' />
///<reference path='../../../api/js/lib/Mousetrap.d.ts' />
///<reference path='../../../api/js/api.d.ts' />

///<reference path='model/_module.ts' />

///<reference path='app/_module.ts' />
///<reference path='app/browse/_module.ts' />
///<reference path='app/browse/filter/_module.ts' />
///<reference path='app/browse/grid/_module.ts' />
///<reference path='app/delete/_module.ts' />
///<reference path='app/new/_module.ts' />
///<reference path='app/view/_module.ts' />
///<reference path='app/wizard/_module.ts' />



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