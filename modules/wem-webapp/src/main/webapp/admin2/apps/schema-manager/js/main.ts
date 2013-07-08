///<reference path='../../../api/js/ExtJs.d.ts' />
///<reference path='../../../api/js/Mousetrap.d.ts' />
///<reference path='../../../api/js/api.d.ts' />

///<reference path='plugin/PersistentGridSelectionPlugin.ts' />
///<reference path='plugin/GridToolbarPlugin.ts' />

///<reference path='model/SchemaModel.ts' />

///<reference path='app/browse/SchemaBrowseEvents.ts' />
///<reference path='app/browse/SchemaBrowseActions.ts' />
///<reference path='app/browse/SchemaBrowseToolbar.ts' />
///<reference path='app/browse/SchemaBrowseItemPanel.ts' />
///<reference path='app/browse/SchemaBrowsePanel.ts' />
///<reference path='app/browse/SchemaActionMenu.ts' />
///<reference path='app/browse/SchemaTreeGridPanel.ts' />
///<reference path='app/browse/SchemaTreeGridContextMenu.ts' />

///<reference path='app/SchemaAppBar.ts' />
///<reference path='app/SchemaAppBarTabMenu.ts' />
///<reference path='app/SchemaAppBarTabMenuItem.ts' />
///<reference path='app/SchemaAppPanel.ts' />
///<reference path='app/SchemaContext.ts' />

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
}

Ext.application({
    name: 'schemaManager',

    controllers: [],

    stores: [],

    launch: function () {
        var appBar = new app.SchemaAppBar();
        var appPanel = new app.SchemaAppPanel(appBar);

        api_dom.Body.get().appendChild(appBar);
        api_dom.Body.get().appendChild(appPanel);

        appPanel.init();
    }
});

app.SchemaContext.init();
app_browse.SchemaBrowseActions.init();