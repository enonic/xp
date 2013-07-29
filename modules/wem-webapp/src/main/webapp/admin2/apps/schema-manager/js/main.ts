///<reference path='../../../api/js/ExtJs.d.ts' />
///<reference path='../../../api/js/Mousetrap.d.ts' />
///<reference path='../../../api/js/api.d.ts' />

///<reference path='model/SchemaModel.ts' />

///<reference path='app/browse/SchemaBrowseEvents.ts' />
///<reference path='app/browse/SchemaBrowseActions.ts' />
///<reference path='app/browse/SchemaBrowseToolbar.ts' />
///<reference path='app/browse/SchemaBrowseItemPanel.ts' />
///<reference path='app/browse/SchemaBrowsePanel.ts' />
///<reference path='app/browse/SchemaViewActions.ts' />
///<reference path='app/browse/SchemaItemStatisticsPanel.ts' />
///<reference path='app/browse/SchemaItemViewToolbar.ts' />
///<reference path='app/browse/SchemaItemViewPanel.ts' />
///<reference path='app/browse/SchemaActionMenu.ts' />
///<reference path='app/browse/SchemaTreeGridPanel.ts' />
///<reference path='app/browse/SchemaTreeGridContextMenu.ts' />
///<reference path='app/browse/NewSchemaDialog.ts' />
///<reference path='app/delete/SchemaDeleteDialog.ts' />

///<reference path='app/wizard/ContentTypeForm.ts' />
///<reference path='app/wizard/ContentTypeWizardPanel.ts' />
///<reference path='app/wizard/ContentTypeWizardActions.ts' />
///<reference path='app/wizard/ContentTypeWizardToolbar.ts' />
///<reference path='app/wizard/ContentTypeWizardEvents.ts' />
///<reference path='app/wizard/RelationshipTypeForm.ts' />
///<reference path='app/wizard/RelationshipTypeWizardPanel.ts' />
///<reference path='app/wizard/RelationshipTypeWizardActions.ts' />
///<reference path='app/wizard/RelationshipTypeWizardToolbar.ts' />
///<reference path='app/wizard/RelationshipTypeWizardEvents.ts' />
///<reference path='app/wizard/MixinForm.ts' />
///<reference path='app/wizard/MixinWizardPanel.ts' />
///<reference path='app/wizard/MixinWizardActions.ts' />
///<reference path='app/wizard/MixinWizardToolbar.ts' />
///<reference path='app/wizard/MixinWizardEvents.ts' />

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
    export var newSchemaDialog:app_browse.NewSchemaDialog;
    export var schemaDeleteDialog:app_delete.SchemaDeleteDialog;
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

        var schemaGridContextMenu = new app_browse.SchemaTreeGridContextMenu();
        schemaGridContextMenu.hide();
        app_browse.ShowContextMenuEvent.on((event) => {
            schemaGridContextMenu.showAt(event.getX(), event.getY());
        })
    }
});

app.SchemaContext.init();
app_browse.SchemaBrowseActions.init();