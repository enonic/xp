module app {

    export class SchemaAppPanel extends api_app.BrowseAndWizardBasedAppPanel {

        public static CONTENT_TYPE = 'ContentType';
        public static RELATIONSHIP_TYPE = 'RelationshipType';
        public static MIXIN = 'Mixin';

        constructor(appBar:api_app.AppBar) {
            var browsePanel = new app_browse.SchemaBrowsePanel();

            super({
                appBar: appBar,
                browsePanel: browsePanel
            });

            this.handleGlobalEvents();
        }

        addWizardPanel(tabMenuItem:api_app.AppBarTabMenuItem, wizardPanel:api_app_wizard.WizardPanel<any>) {
            super.addWizardPanel(tabMenuItem, wizardPanel);

            wizardPanel.getHeader().addListener(
                {
                    onPropertyChanged: (event:api_app_wizard.WizardHeaderPropertyChangedEvent) => {
                        if (event.property == "name") {
                            tabMenuItem.setLabel(event.newValue);
                        }
                    }
                });
        }

        private handleGlobalEvents() {

            api_app.ShowAppBrowsePanelEvent.on((event) => {
                this.showHomePanel();
                this.getAppBarTabMenu().deselectNavigationItem();
            });

            app_browse.ShowNewSchemaDialogEvent.on((event) => {
                if (!components.newSchemaDialog) {
                    components.newSchemaDialog = new app_new.NewSchemaDialog();
                }
                components.newSchemaDialog.open();
            });

            app_new.NewSchemaEvent.on((event) => {

                var schemaType = event.getSchemaType();
                var tabId = this.generateTabId(schemaType);
                var tabMenuItem = this.getAppBarTabMenu().getNavigationItemById(tabId);

                if (tabMenuItem != null) {
                    this.selectPanel(tabMenuItem);

                } else {
                    var schemaWizardPanel;

                    switch (schemaType) {
                    case SchemaAppPanel.CONTENT_TYPE:
                        tabMenuItem = new api_app.AppBarTabMenuItem(app_wizard.ContentTypeWizardPanel.NEW_WIZARD_HEADER, tabId, true);
                        schemaWizardPanel = new app_wizard.ContentTypeWizardPanel();
                        break;
                    case SchemaAppPanel.RELATIONSHIP_TYPE:
                        tabMenuItem = new api_app.AppBarTabMenuItem(app_wizard.RelationshipTypeWizardPanel.NEW_WIZARD_HEADER, tabId, true);
                        schemaWizardPanel = new app_wizard.RelationshipTypeWizardPanel();
                        break;
                    case SchemaAppPanel.MIXIN:
                        tabMenuItem = new api_app.AppBarTabMenuItem(app_wizard.MixinWizardPanel.NEW_WIZARD_HEADER, tabId, true);
                        schemaWizardPanel = new app_wizard.MixinWizardPanel();
                        break;
                    }

                    this.addWizardPanel(tabMenuItem, schemaWizardPanel);
                    schemaWizardPanel.reRender();
                    schemaWizardPanel.renderNew();
                }
            });

            app_browse.EditSchemaEvent.on((event) => {

                event.getSchemas().forEach((schema:api_schema.Schema) => {

                    var tabId = this.generateTabId(schema.getSchemaType(), schema.getSchemaName(), true);
                    var tabMenuItem = this.getAppBarTabMenu().getNavigationItemById(tabId);

                    if (tabMenuItem != null) {
                        this.selectPanel(tabMenuItem);

                    } else {
                        var schemaWizardPanel;

                        switch (schema.getSchemaType()) {
                        case SchemaAppPanel.CONTENT_TYPE:
                            new api_schema_content.GetContentTypeByQualifiedNameRequest(schema.getSchemaName()).
                                send().done((jsonResponse:api_rest.JsonResponse<api_schema_content_json.ContentTypeJson>) => {
                                    var contentType = new api_schema_content.ContentType(jsonResponse.getResult());

                                    tabMenuItem = new api_app.AppBarTabMenuItem(contentType.getName(), tabId, true);

                                    schemaWizardPanel = new app_wizard.ContentTypeWizardPanel();
                                    schemaWizardPanel.setPersistedItem(contentType);

                                    this.addWizardPanel(tabMenuItem, schemaWizardPanel);
                                });
                            break;
                        case SchemaAppPanel.RELATIONSHIP_TYPE:
                            new api_schema_relationshiptype.GetRelationshipTypeByQualifiedNameRequest(schema.getSchemaName()).
                                send().done((jsonResponse:api_rest.JsonResponse<api_schema_relationshiptype_json.RelationshipTypeJson>) => {
                                    var relationshipType = new api_schema_relationshiptype.RelationshipType(jsonResponse.getResult());

                                    tabMenuItem = new api_app.AppBarTabMenuItem(relationshipType.getDisplayName(), tabId, true);

                                    schemaWizardPanel = new app_wizard.RelationshipTypeWizardPanel();
                                    schemaWizardPanel.setPersistedItem(relationshipType);

                                    this.addWizardPanel(tabMenuItem, schemaWizardPanel);
                                });
                            break;
                        case SchemaAppPanel.MIXIN:
                            new api_schema_mixin.GetMixinByQualifiedNameRequest(schema.getSchemaName()).
                                send().done((jsonResponse:api_rest.JsonResponse<api_schema_mixin_json.MixinJson>)=> {
                                    var mixin:api_schema_mixin.Mixin = new api_schema_mixin.Mixin(jsonResponse.getResult());
                                    tabMenuItem = new api_app.AppBarTabMenuItem(mixin.getDisplayName(), tabId, true);

                                    schemaWizardPanel = new app_wizard.MixinWizardPanel();
                                    schemaWizardPanel.setPersistedItem(mixin);

                                    this.addWizardPanel(tabMenuItem, schemaWizardPanel);
                                });

                            break;
                        }
                    }
                });
            });

            app_browse.OpenSchemaEvent.on((event) => {
                event.getSchemas().forEach((schema:api_schema.Schema) => {

                        var tabId = this.generateTabId(schema.getSchemaType(), schema.getSchemaName(), false);
                        var tabMenuItem = this.getAppBarTabMenu().getNavigationItemById(tabId);

                        if (tabMenuItem != null) {
                            this.selectPanel(tabMenuItem);

                        } else {
                            tabMenuItem = new api_app.AppBarTabMenuItem(schema.getSchemaName(), tabId);
                            var schemaItemViewPanel = new app_view.SchemaItemViewPanel();
                            var spaceItem = new api_app_view.ViewItem(schema)
                                .setDisplayName(schema.getDisplayName())
                                .setPath(schema.getSchemaName())
                                .setIconUrl(schema.getIcon());

                            schemaItemViewPanel.setItem(spaceItem);

                            this.addViewPanel(tabMenuItem, schemaItemViewPanel);
                        }
                    }
                );
            });

            app_browse.DeleteSchemaPromptEvent.on((event) => {
                if (!components.schemaDeleteDialog) {
                    components.schemaDeleteDialog = new app_delete.SchemaDeleteDialog();
                }
                components.schemaDeleteDialog.setSchemaToDelete(event.getSchemas()).open();
            });

            app_browse.ReindexSchemaEvent.on(() => {
                console.log('TODO: implement handler for ReindexSchemaEvent');
            });

            app_browse.ExportSchemaEvent.on(() => {
                console.log('TODO: implement handler for ExportSchemaEvent');
            });

            app_browse.CloseSchemaEvent.on((event) => {
                this.removePanel(event.getPanel(), event.isCheckCanRemovePanel());
            });
        }

        private generateTabId(schemaType:string, schemaName?:string, isEdit:boolean = false) {
            return schemaName ? ( isEdit ? 'edit-' : 'view-') + schemaName : 'new-' + schemaType;
        }

    }
}