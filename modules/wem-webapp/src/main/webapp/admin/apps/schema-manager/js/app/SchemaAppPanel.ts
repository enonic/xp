module app {

    export class SchemaAppPanel extends api.app.BrowseAndWizardBasedAppPanel<api.schema.Schema> {

        constructor(appBar: api.app.AppBar) {
            var browsePanel = new app.browse.SchemaBrowsePanel();

            super({
                appBar: appBar,
                browsePanel: browsePanel
            });

            this.handleGlobalEvents();
        }

        addWizardPanel(tabMenuItem: api.app.AppBarTabMenuItem, wizardPanel: api.app.wizard.WizardPanel<any>) {
            super.addWizardPanel(tabMenuItem, wizardPanel);

            wizardPanel.getHeader().addListener(
                {
                    onPropertyChanged: (event: api.app.wizard.WizardHeaderPropertyChangedEvent) => {
                        if (event.property == "name") {
                            tabMenuItem.setLabel(event.newValue);
                        }
                    }
                });
        }

        private handleGlobalEvents() {

            api.app.ShowAppBrowsePanelEvent.on((event) => {
                this.showHomePanel();
                this.getAppBarTabMenu().deselectNavigationItem();
            });

            app.browse.ShowNewSchemaDialogEvent.on((event) => {
                if (!components.newSchemaDialog) {
                    components.newSchemaDialog = new app.create.NewSchemaDialog();
                }
                components.newSchemaDialog.open();
            });

            app.create.NewSchemaEvent.on((event) => {

                var schemaKind: api.schema.SchemaKind = event.getSchemaKind();
                var tabId = api.app.AppBarTabId.forNew(schemaKind.toString());
                var tabMenuItem = this.getAppBarTabMenu().getNavigationItemById(tabId);

                if (tabMenuItem != null) {
                    this.selectPanel(tabMenuItem);

                } else {
                    switch (schemaKind) {
                    case api.schema.SchemaKind.CONTENT_TYPE:
                        tabMenuItem = new api.app.AppBarTabMenuItem(app.wizard.ContentTypeWizardPanel.NEW_WIZARD_HEADER, tabId, true);
                        new app.wizard.ContentTypeWizardPanel(tabId, null, (wizard:app.wizard.ContentTypeWizardPanel) => {
                            this.addWizardPanel(tabMenuItem, wizard);
                            wizard.initWizardPanel();
                            wizard.reRender();
                        });
                        break;
                    case api.schema.SchemaKind.RELATIONSHIP_TYPE:
                        tabMenuItem = new api.app.AppBarTabMenuItem(app.wizard.RelationshipTypeWizardPanel.NEW_WIZARD_HEADER, tabId, true);
                        new app.wizard.RelationshipTypeWizardPanel(tabId, null, (wizard:app.wizard.RelationshipTypeWizardPanel) => {
                            this.addWizardPanel(tabMenuItem, wizard);
                            wizard.initWizardPanel();
                            wizard.reRender();
                        });
                        break;
                    case api.schema.SchemaKind.MIXIN:
                        tabMenuItem = new api.app.AppBarTabMenuItem(app.wizard.MixinWizardPanel.NEW_WIZARD_HEADER, tabId, true);
                        new app.wizard.MixinWizardPanel(tabId, null, (wizard:app.wizard.MixinWizardPanel) => {
                            this.addWizardPanel(tabMenuItem, wizard);
                            wizard.initWizardPanel();
                            wizard.reRender();
                        });
                        break;
                    }
                }
            });

            app.browse.EditSchemaEvent.on((event) => {

                event.getSchemas().forEach((schema: api.schema.Schema) => {

                    var tabId = api.app.AppBarTabId.forEdit(schema.getKey());
                    var tabMenuItem = this.getAppBarTabMenu().getNavigationItemById(tabId);

                    if (tabMenuItem != null) {
                        this.selectPanel(tabMenuItem);

                    } else {
                        if (schema.getSchemaKind() == api.schema.SchemaKind.CONTENT_TYPE) {
                            var contentType = <api.schema.content.ContentType>schema;
                            new api.schema.content.GetContentTypeByNameRequest(contentType.getContentTypeName()).
                                sendAndParse().done((contentType: api.schema.content.ContentType) => {

                                    tabMenuItem = new api.app.AppBarTabMenuItem(contentType.getName(), tabId, true);

                                    new app.wizard.ContentTypeWizardPanel(tabId, contentType, (wizard:app.wizard.ContentTypeWizardPanel) => {
                                        this.addWizardPanel(tabMenuItem, wizard);
                                        wizard.initWizardPanel();
                                        wizard.reRender();
                                    });
                                });
                        }
                        else if (schema.getSchemaKind() == api.schema.SchemaKind.RELATIONSHIP_TYPE) {
                            var relationhipType = <api.schema.relationshiptype.RelationshipType>schema;
                            new api.schema.relationshiptype.GetRelationshipTypeByNameRequest(relationhipType.getRelationshiptypeName()).
                                sendAndParse().done((relationshipType: api.schema.relationshiptype.RelationshipType) => {

                                    tabMenuItem = new api.app.AppBarTabMenuItem(relationshipType.getDisplayName(), tabId, true);

                                    new app.wizard.RelationshipTypeWizardPanel(tabId, relationshipType, (wizard:app.wizard.RelationshipTypeWizardPanel) => {
                                        this.addWizardPanel(tabMenuItem, wizard);
                                        wizard.initWizardPanel();
                                        wizard.reRender();
                                    });
                                });
                        }
                        else if (schema.getSchemaKind() == api.schema.SchemaKind.MIXIN) {
                            var mixin = <api.schema.mixin.Mixin>schema;
                            new api.schema.mixin.GetMixinByQualifiedNameRequest(mixin.getMixinName()).
                                sendAndParse().done((mixin: api.schema.mixin.Mixin)=> {

                                    tabMenuItem = new api.app.AppBarTabMenuItem(mixin.getDisplayName(), tabId, true);

                                    new app.wizard.MixinWizardPanel(tabId, mixin, (wizard:app.wizard.MixinWizardPanel) => {
                                        this.addWizardPanel(tabMenuItem, wizard);
                                        wizard.initWizardPanel();
                                        wizard.reRender();
                                    });
                                });
                        }
                        else {
                            throw new Error("Unknown SchemaKind: " + schema.getSchemaKind())
                        }
                    }
                });
            });

            app.browse.OpenSchemaEvent.on((event) => {
                event.getSchemas().forEach((schema: api.schema.Schema) => {

                        var tabId = api.app.AppBarTabId.forView(schema.getKey());
                        var tabMenuItem = this.getAppBarTabMenu().getNavigationItemById(tabId);

                        if (tabMenuItem != null) {
                            this.selectPanel(tabMenuItem);

                        } else {
                            tabMenuItem = new api.app.AppBarTabMenuItem(schema.getName(), tabId);
                            var schemaItemViewPanel = new app.view.SchemaItemViewPanel();
                            var schemaViewItem = new api.app.view.ViewItem<api.schema.Schema>(schema)
                                .setDisplayName(schema.getDisplayName())
                                .setPath(schema.getName())
                                .setIconUrl(schema.getIconUrl());

                            schemaItemViewPanel.setItem(schemaViewItem);

                            this.addViewPanel(tabMenuItem, schemaItemViewPanel);
                        }
                    }
                );
            });

            app.browse.DeleteSchemaPromptEvent.on((event) => {
                if (!components.schemaDeleteDialog) {
                    components.schemaDeleteDialog = new app.remove.SchemaDeleteDialog();
                }
                components.schemaDeleteDialog.setSchemaToDelete(event.getSchemas()).open();
            });

            app.browse.ReindexSchemaEvent.on(() => {
                console.log('TODO: implement handler for ReindexSchemaEvent');
            });

            app.browse.ExportSchemaEvent.on(() => {
                console.log('TODO: implement handler for ExportSchemaEvent');
            });

            app.browse.CloseSchemaEvent.on((event) => {
                this.removePanel(event.getPanel(), event.isCheckCanRemovePanel());
            });
        }

    }
}