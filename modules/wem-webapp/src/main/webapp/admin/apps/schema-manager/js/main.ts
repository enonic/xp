///<reference path='../../../api/js/lib/ExtJs.d.ts' />
///<reference path='../../../api/js/lib/Mousetrap.d.ts' />
///<reference path='../../../api/js/api.d.ts' />

///<reference path='model/SchemaModel.ts' />

///<reference path='app/browse/filter/SchemaBrowseFilterEvents.ts'/>
///<reference path='app/browse/filter/SchemaBrowseFilterPanel.ts'/>

///<reference path='app/browse/SchemaBrowseEvents.ts' />
///<reference path='app/browse/SchemaBrowseActions.ts' />
///<reference path='app/browse/SchemaBrowseToolbar.ts' />
///<reference path='app/browse/SchemaBrowseItemPanel.ts' />
///<reference path='app/browse/SchemaBrowsePanel.ts' />
///<reference path='app/delete/SchemaDeleteDialog.ts' />
///<reference path='app/browse/SchemaTreeGridContextMenu.ts' />
///<reference path='app/browse/grid/SchemaGridStore.ts' />
///<reference path='app/browse/grid/SchemaTreeStore.ts' />
///<reference path='app/browse/SchemaTreeGridPanel.ts' />

///<reference path='app/new/NewSchemaEvent.ts' />
///<reference path='app/new/SchemaTypesListListener.ts' />
///<reference path='app/new/SchemaTypesList.ts' />
///<reference path='app/new/NewSchemaDialog.ts' />

///<reference path='app/view/SchemaViewActions.ts' />
///<reference path='app/view/SchemaItemStatisticsPanel.ts' />
///<reference path='app/view/SchemaItemViewToolbar.ts' />
///<reference path='app/view/SchemaItemViewPanel.ts' />

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

///<reference path='app/SchemaAppPanel.ts' />

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