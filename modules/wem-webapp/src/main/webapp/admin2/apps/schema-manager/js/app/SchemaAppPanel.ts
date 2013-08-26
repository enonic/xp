module app {

    export class SchemaAppPanel extends api_app.BrowseAndWizardBasedAppPanel {

        public static CONTENT_TYPE = 'ContentType';
        public static RELATIONSHIP_TYPE = 'RelationshipType';
        public static MIXIN = 'Mixin';

        private browsePanel:app_browse.SchemaBrowsePanel;

        private appBarTabMenu:api_app.AppBarTabMenu;

        constructor(appBar:api_app.AppBar) {
            this.browsePanel = new app_browse.SchemaBrowsePanel();
            this.appBarTabMenu = appBar.getTabMenu();

            super({
                appBar: appBar,
                browsePanel: this.browsePanel,
                browsePanelActions: app_browse.SchemaBrowseActions.get().getAllActions()
            });

            this.handleGlobalEvents();
        }

        addWizardPanel(tabMenuItem:api_app.AppBarTabMenuItem, wizardPanel:api_app_wizard.WizardPanel, inBackground?:bool = false) {
            super.addWizardPanel(tabMenuItem, wizardPanel, inBackground);

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
                this.appBarTabMenu.deselectNavigationItem();
            });

            api_ui_tab.TabMenuItemCloseEvent.on((event) => {
                var tabIndex = event.getTab().getIndex();
                var panel = this.getPanel(tabIndex);
                new app_browse.CloseSchemaEvent(panel, true).fire();
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
                var tabMenuItem = this.appBarTabMenu.getNavigationItemById(tabId);

                if (tabMenuItem != null) {
                    this.selectPanel(tabMenuItem);

                } else {
                    var schemaWizardPanel;

                    switch (schemaType) {
                    case SchemaAppPanel.CONTENT_TYPE:
                        tabMenuItem = new api_app.AppBarTabMenuItem(app_wizard.ContentTypeWizardPanel.NEW_WIZARD_HEADER, tabId, true);
                        schemaWizardPanel = new app_wizard.ContentTypeWizardPanel(tabId);
                        break;
                    case SchemaAppPanel.RELATIONSHIP_TYPE:
                        tabMenuItem = new api_app.AppBarTabMenuItem(app_wizard.RelationshipTypeWizardPanel.NEW_WIZARD_HEADER, tabId, true);
                        schemaWizardPanel = new app_wizard.RelationshipTypeWizardPanel(tabId);
                        break;
                    case SchemaAppPanel.MIXIN:
                        tabMenuItem = new api_app.AppBarTabMenuItem(app_wizard.MixinWizardPanel.NEW_WIZARD_HEADER, tabId, true);
                        schemaWizardPanel = new app_wizard.MixinWizardPanel(tabId);
                        break;
                    }

                    this.addWizardPanel(tabMenuItem, schemaWizardPanel);
                    schemaWizardPanel.reRender();
                }
            });

            app_browse.EditSchemaEvent.on((event) => {

                event.getModels().forEach((schemaModel:api_model.SchemaExtModel) => {

                    var tabId = this.generateTabId(schemaModel.data.type, schemaModel.data.name, true);
                    var tabMenuItem = this.appBarTabMenu.getNavigationItemById(tabId);

                    if (tabMenuItem != null) {
                        this.selectPanel(tabMenuItem);

                    } else {
                        var schemaWizardPanel;

                        switch (schemaModel.data.type) {
                        case SchemaAppPanel.CONTENT_TYPE:
                            var contentTypeGetParams:api_remote_contenttype.GetParams = {
                                qualifiedNames: [schemaModel.data.qualifiedName],
                                format: 'JSON'
                            };
                            api_remote_contenttype.RemoteContentTypeService.contentType_get(contentTypeGetParams,
                                (result:api_remote_contenttype.GetResult) => {

                                    var contentType:api_remote_contenttype.ContentType = result.contentTypes[0];

                                    tabMenuItem = new api_app.AppBarTabMenuItem(contentType.name, tabId, true);

                                    schemaWizardPanel = new app_wizard.ContentTypeWizardPanel(tabId);
                                    schemaWizardPanel.setPersistedItem(contentType);

                                    this.addWizardPanel(tabMenuItem, schemaWizardPanel);
                                });
                            break;
                        case SchemaAppPanel.RELATIONSHIP_TYPE:
                            var relationshipTypeGetParams:api_remote_relationshiptype.GetParams = {
                                qualifiedName: schemaModel.data.qualifiedName,
                                format: 'JSON'
                            };
                            api_remote_relationshiptype.RemoteRelationshipTypeService.relationshipType_get(relationshipTypeGetParams,
                                (result:api_remote_relationshiptype.GetResult) => {

                                    tabMenuItem = new api_app.AppBarTabMenuItem(result.relationshipType.displayName, tabId, true);

                                    schemaWizardPanel = new app_wizard.RelationshipTypeWizardPanel(tabId);
                                    schemaWizardPanel.setPersistedItem(result.relationshipType);

                                    this.addWizardPanel(tabMenuItem, schemaWizardPanel);
                                });
                            break;
                        case SchemaAppPanel.MIXIN:
                            var mixinGetParams:api_remote_mixin.GetParams = {
                                qualifiedName: schemaModel.data.qualifiedName,
                                format: 'JSON'
                            };
                            api_remote_mixin.RemoteMixinService.mixin_get(mixinGetParams, (result:api_remote_mixin.GetResult) => {

                                tabMenuItem = new api_app.AppBarTabMenuItem(result.mixin.displayName, tabId, true);

                                schemaWizardPanel = new app_wizard.MixinWizardPanel(tabId);
                                schemaWizardPanel.setPersistedItem(result.mixin);

                                this.addWizardPanel(tabMenuItem, schemaWizardPanel);
                            });
                            break;
                        }
                    }
                });
            });

            app_browse.OpenSchemaEvent.on((event) => {
                event.getModels().forEach((schemaModel:api_model.SchemaExtModel) => {

                        var tabId = this.generateTabId(schemaModel.data.type, schemaModel.data.name, false);
                        var tabMenuItem = this.appBarTabMenu.getNavigationItemById(tabId);

                        if (tabMenuItem != null) {
                            this.selectPanel(tabMenuItem);

                        } else {
                            tabMenuItem = new api_app.AppBarTabMenuItem(schemaModel.data.displayName, tabId);
                            var schemaItemViewPanel = new app_view.SchemaItemViewPanel();
                            var spaceItem = new api_app_view.ViewItem(schemaModel)
                                .setDisplayName(schemaModel.data.displayName)
                                .setPath(schemaModel.data.name)
                                .setIconUrl(schemaModel.data.iconUrl);

                            schemaItemViewPanel.setItem(spaceItem);

                            this.addNavigationItem(tabMenuItem, schemaItemViewPanel);
                        }
                    }
                );
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
        }

        private generateTabId(schemaType:string, schemaName ?:string, isEdit ?:bool = false) {
            return schemaName ? ( isEdit ? 'edit-' : 'view-') + schemaName : 'new-' + schemaType;
        }

    }
}