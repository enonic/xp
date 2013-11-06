module app {

    export class SchemaAppPanel extends api_app.BrowseAndWizardBasedAppPanel<api_schema.Schema> {

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

                var schemaKind:api_schema.SchemaKind = event.getSchemaKind();
                var tabId = api_app.AppBarTabId.forNew(schemaKind.toString());
                var tabMenuItem = this.getAppBarTabMenu().getNavigationItemById(tabId);

                if (tabMenuItem != null) {
                    this.selectPanel(tabMenuItem);

                } else {
                    var schemaWizardPanel;

                    switch (schemaKind) {
                        case api_schema.SchemaKind.CONTENT_TYPE:
                            tabMenuItem = new api_app.AppBarTabMenuItem(app_wizard.ContentTypeWizardPanel.NEW_WIZARD_HEADER, tabId, true);
                            schemaWizardPanel = new app_wizard.ContentTypeWizardPanel(tabId);
                            break;
                        case api_schema.SchemaKind.RELATIONSHIP_TYPE:
                            tabMenuItem = new api_app.AppBarTabMenuItem(app_wizard.RelationshipTypeWizardPanel.NEW_WIZARD_HEADER, tabId, true);
                            schemaWizardPanel = new app_wizard.RelationshipTypeWizardPanel(tabId);
                            break;
                        case api_schema.SchemaKind.MIXIN:
                            tabMenuItem = new api_app.AppBarTabMenuItem(app_wizard.MixinWizardPanel.NEW_WIZARD_HEADER, tabId, true);
                            schemaWizardPanel = new app_wizard.MixinWizardPanel(tabId);
                            break;
                    }

                    this.addWizardPanel(tabMenuItem, schemaWizardPanel);
                    schemaWizardPanel.reRender();
                    schemaWizardPanel.renderNew();
                }
            });

            app_browse.EditSchemaEvent.on((event) => {

                event.getSchemas().forEach((schema:api_schema.Schema) => {

                    var tabId = api_app.AppBarTabId.forEdit(schema.getKey());
                    var tabMenuItem = this.getAppBarTabMenu().getNavigationItemById(tabId);

                    if (tabMenuItem != null) {
                        this.selectPanel(tabMenuItem);

                    } else {
                        var schemaWizardPanel;

                        if( schema.getSchemaKind() == api_schema.SchemaKind.CONTENT_TYPE ) {
                            new api_schema_content.GetContentTypeByQualifiedNameRequest(schema.getName()).
                                send().done((jsonResponse:api_rest.JsonResponse<api_schema_content_json.ContentTypeJson>) => {
                                                var contentType = new api_schema_content.ContentType(jsonResponse.getResult());

                                                tabMenuItem = new api_app.AppBarTabMenuItem(contentType.getName(), tabId, true);

                                                schemaWizardPanel = new app_wizard.ContentTypeWizardPanel(tabId);
                                                schemaWizardPanel.setPersistedItem(contentType);

                                                this.addWizardPanel(tabMenuItem, schemaWizardPanel);
                                            });
                        }
                        else if( schema.getSchemaKind() == api_schema.SchemaKind.RELATIONSHIP_TYPE ) {
                            new api_schema_relationshiptype.GetRelationshipTypeByQualifiedNameRequest(schema.getName()).
                                send().done((jsonResponse:api_rest.JsonResponse<api_schema_relationshiptype_json.RelationshipTypeJson>) => {
                                                var relationshipType = new api_schema_relationshiptype.RelationshipType(jsonResponse.getResult());

                                                tabMenuItem = new api_app.AppBarTabMenuItem(relationshipType.getDisplayName(), tabId, true);

                                                schemaWizardPanel = new app_wizard.RelationshipTypeWizardPanel(tabId);
                                                schemaWizardPanel.setPersistedItem(relationshipType);

                                                this.addWizardPanel(tabMenuItem, schemaWizardPanel);
                                            });
                        }
                        else if( schema.getSchemaKind() == api_schema.SchemaKind.MIXIN ) {
                            new api_schema_mixin.GetMixinByQualifiedNameRequest(schema.getName()).
                                send().done((jsonResponse:api_rest.JsonResponse<api_schema_mixin_json.MixinJson>)=> {
                                                var mixin:api_schema_mixin.Mixin = new api_schema_mixin.Mixin(jsonResponse.getResult());
                                                tabMenuItem = new api_app.AppBarTabMenuItem(mixin.getDisplayName(), tabId, true);

                                                schemaWizardPanel = new app_wizard.MixinWizardPanel(tabId);
                                                schemaWizardPanel.setPersistedItem(mixin);

                                                this.addWizardPanel(tabMenuItem, schemaWizardPanel);
                                            });
                        }
                        else {
                            throw new Error("Unknown SchemaKind: " + schema.getSchemaKind() )
                        }
                    }
                });
            });

            app_browse.OpenSchemaEvent.on((event) => {
                event.getSchemas().forEach((schema:api_schema.Schema) => {

                        var tabId = api_app.AppBarTabId.forView(schema.getKey());
                        var tabMenuItem = this.getAppBarTabMenu().getNavigationItemById(tabId);

                        if (tabMenuItem != null) {
                            this.selectPanel(tabMenuItem);

                        } else {
                            tabMenuItem = new api_app.AppBarTabMenuItem(schema.getName(), tabId);
                            var schemaItemViewPanel = new app_view.SchemaItemViewPanel();
                            var schemaViewItem = new api_app_view.ViewItem<api_schema.Schema>(schema)
                                .setDisplayName(schema.getDisplayName())
                                .setPath(schema.getName())
                                .setIconUrl(schema.getIcon());

                            schemaItemViewPanel.setItem(schemaViewItem);

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

    }
}