module app {

    export class SchemaAppPanel extends api_app.AppPanel {

        public static CONTENT_TYPE = 'ContentType';
        public static RELATIONSHIP_TYPE = 'RelationshipType';
        public static MIXIN = 'Mixin';

        private browsePanel:app_browse.SchemaBrowsePanel;

        private appBarTabMenu:api_app.AppBarTabMenu;

        constructor(appBar:api_app.AppBar) {
            this.browsePanel = new app_browse.SchemaBrowsePanel();
            this.appBarTabMenu = appBar.getTabMenu();

            super(this.appBarTabMenu, this.browsePanel, app_browse.SchemaBrowseActions.ACTIONS);

            this.handleGlobalEvents();
        }

        private handleGlobalEvents() {

            api_app.ShowAppBrowsePanelEvent.on((event) => {
                this.showHomePanel();
                this.appBarTabMenu.deselectNavigationItem();
            });

            api_ui_tab.TabMenuItemSelectEvent.on((event) => {
                this.appBarTabMenu.hideMenu();
                this.selectPanel(event.getTab());
            });

            api_ui_tab.TabMenuItemCloseEvent.on((event) => {
                var tabIndex = event.getTab().getIndex();
                var panel = this.getPanel(tabIndex);
                new app_browse.CloseSchemaEvent(panel, true).fire();
            });

            app_browse.NewSchemaEvent.on((event) => {
                var schemaType = event.getSchemaType();

                if (!schemaType) {
                    if (!components.newSchemaDialog) {
                        components.newSchemaDialog = new app_browse.NewSchemaDialog();
                    }
                    components.newSchemaDialog.open();
                } else {
                    var tabMenuItem, schemaWizardPanel;

                    switch (schemaType) {
                    case SchemaAppPanel.CONTENT_TYPE:
                        tabMenuItem = new SchemaAppBarTabMenuItem("New Content Type", true);
                        schemaWizardPanel = new app_wizard.ContentTypeWizardPanel('new-content-type');
                        break;
                    case SchemaAppPanel.RELATIONSHIP_TYPE:
                        tabMenuItem = new SchemaAppBarTabMenuItem("New Relationship Type", true);
                        schemaWizardPanel = new app_wizard.RelationshipTypeWizardPanel('new-relationship-type');
                        break;
                    case SchemaAppPanel.MIXIN:
                        tabMenuItem = new SchemaAppBarTabMenuItem("New Mixin", true);
                        schemaWizardPanel = new app_wizard.MixinWizardPanel('new-mixin');
                        break;
                    }

                    this.addWizardPanel(tabMenuItem, schemaWizardPanel);
                    this.selectPanel(tabMenuItem);
                    schemaWizardPanel.reRender();
                }
            });

            app_browse.EditSchemaEvent.on((event) => {
                event.getModels().forEach((schemaModel:api_model.SchemaExtModel) => {
                    switch (schemaModel.data.type) {
                    case SchemaAppPanel.CONTENT_TYPE:
                        var contentTypeGetParams:api_remote_contenttype.GetParams = {
                            qualifiedNames: [schemaModel.data.qualifiedName],
                            format: 'JSON'
                        };
                        api_remote.RemoteContentTypeService.contentType_get(contentTypeGetParams,
                            (result:api_remote_contenttype.GetResult) => {
                                if (result && result.success) {
                                    var contentType:api_remote_contenttype.ContentType = result.contentTypes[0];
                                    var tabMenuItem = new SchemaAppBarTabMenuItem(contentType.displayName, true);

                                    var id = this.generateTabId(contentType.name, true);
                                    var schemaWizardPanel = new app_wizard.ContentTypeWizardPanel(id);
                                    // TODO: update rpc response to have iconUrl inside contentType property.
                                    contentType.iconUrl = result.iconUrl;
                                    schemaWizardPanel.setPersistedItem(contentType);

                                    this.addWizardPanel(tabMenuItem, schemaWizardPanel);
                                    this.selectPanel(tabMenuItem);
                                } else {
                                    console.error("Error", result ? result.error : "Unable to retrieve schema.");
                                }
                            });
                        break;
                    case SchemaAppPanel.RELATIONSHIP_TYPE:
                        var relationshipTypeGetParams:api_remote_relationshiptype.GetParams = {
                            qualifiedRelationshipTypeName: schemaModel.data.qualifiedName,
                            format: 'JSON'
                        };
                        api_remote.RemoteRelationshipTypeService.relationshipType_get(relationshipTypeGetParams,
                            (result:api_remote_relationshiptype.GetResult) => {
                                if (result && result.success) {
                                    var tabMenuItem = new SchemaAppBarTabMenuItem(result.relationshipType.displayName, true);

                                    var id = this.generateTabId(result.relationshipType.name, true);
                                    var schemaWizardPanel = new app_wizard.RelationshipTypeWizardPanel(id);
                                    schemaWizardPanel.setPersistedItem(result.relationshipType);

                                    this.addWizardPanel(tabMenuItem, schemaWizardPanel);
                                    this.selectPanel(tabMenuItem);
                                } else {
                                    console.error("Error", result ? result.error : "Unable to retrieve schema.");
                                }
                            });
                        break;
                    case SchemaAppPanel.MIXIN:
                        var mixinGetParams:api_remote_mixin.GetParams = {
                            mixin: schemaModel.data.qualifiedName,
                            format: 'JSON'
                        };
                        api_remote.RemoteMixinService.mixin_get(mixinGetParams, (result:api_remote_mixin.GetResult) => {
                            if (result && result.success) {
                                var tabMenuItem = new SchemaAppBarTabMenuItem(result.mixin.displayName, true);

                                var id = this.generateTabId(result.mixin.name, true);
                                var schemaWizardPanel = new app_wizard.MixinWizardPanel(id);
                                schemaWizardPanel.setPersistedItem(result.mixin);

                                this.addWizardPanel(tabMenuItem, schemaWizardPanel);
                                this.selectPanel(tabMenuItem);
                            } else {
                                console.error("Error", result ? result.error : "Unable to retrieve schema.");
                            }
                        });
                        break;
                    }
                });
            });

            app_browse.OpenSchemaEvent.on((event) => {
                var schemes:api_model.SchemaExtModel[] = event.getModels();
                for (var i = 0; i < schemes.length; i++) {
                    var schemaModel:api_model.SchemaExtModel = schemes[i];

                    var tabMenuItem = new SchemaAppBarTabMenuItem(schemaModel.data.displayName);

                    var id = this.generateTabId(schemaModel.data.name, false);
                    var schemaItemViewPanel = new app_view.SchemaItemViewPanel(id);

                    var spaceItem = new api_app_browse.BrowseItem(schemaModel)
                        .setDisplayName(schemaModel.data.displayName)
                        .setPath(schemaModel.data.name)
                        .setIconUrl(schemaModel.data.iconUrl);

                    schemaItemViewPanel.setItem(spaceItem);

                    this.addNavigationItem(tabMenuItem, schemaItemViewPanel);
                    this.selectPanel(tabMenuItem);
                }
            });

            app_browse.DeleteSchemaPromptEvent.on((event) => {
                if (!components.schemaDeleteDialog) {
                    components.schemaDeleteDialog = new app_delete.SchemaDeleteDialog();
                }
                components.schemaDeleteDialog.setSchemaToDelete(event.getModels()).open();
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

            api_app_wizard.CloseWizardPanelEvent.on((event) => {
                this.removePanel(event.getWizardPanel(), event.isCheckCanRemovePanel());
            });

        }

        private generateTabId(shemaName, isEdit) {
            return 'tab-' + ( isEdit ? 'edit-' : 'preview-') + shemaName;
        }

    }
}