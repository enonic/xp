module app {

    export class SchemaAppPanel extends api.app.BrowseAndWizardBasedAppPanel<api.schema.Schema> {

        private browseActions: app.browse.action.SchemaBrowseActions;

        private schemaTreeGrid: app.browse.SchemaTreeGrid;

        constructor(appBar: api.app.bar.AppBar, path?: api.rest.Path) {

            super({
                appBar: appBar
            });

            this.browseActions = new app.browse.action.SchemaBrowseActions();
            this.schemaTreeGrid = new app.browse.SchemaTreeGrid(this.browseActions);
            this.handleGlobalEvents();

            this.route(path);
        }

        addWizardPanel(tabMenuItem: api.app.bar.AppBarTabMenuItem, wizardPanel: api.app.wizard.WizardPanel<any>) {
            super.addWizardPanel(tabMenuItem, wizardPanel);

            wizardPanel.getHeader().onPropertyChanged((event: api.PropertyChangedEvent) => {
                if (event.getPropertyName() == "name") {
                    tabMenuItem.setLabel(<string>event.getNewValue());
                }
            });
        }

        private route(path: api.rest.Path) {
            var action = path.getElement(0);

            switch (action) {
            case 'edit':
                var id = path.getElement(1);
                if (id) {
                    //TODO
                }
                break;
            case 'view' :
                var id = path.getElement(1);
                if (id) {
                    //TODO
                }
                break;
            default:
                new api.app.bar.event.ShowBrowsePanelEvent().fire();
                break;
            }
        }

        private handleGlobalEvents() {

            api.app.bar.event.ShowBrowsePanelEvent.on((event) => {
                this.handleBrowse(event);
            });

            app.browse.ShowNewSchemaDialogEvent.on((event) => {
                if (!components.newSchemaDialog) {
                    components.newSchemaDialog = new app.create.NewSchemaDialog();
                }
                components.newSchemaDialog.open();
            });

            app.create.NewSchemaEvent.on((event) => {
                this.handleNew(event);
            });

            app.browse.EditSchemaEvent.on((event) => {
                this.handleEdit(event);
            });

            app.browse.OpenSchemaEvent.on((event) => {
                this.handleView(event);
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
                this.removeNavigablePanel(event.getPanel(), event.isCheckCanRemovePanel());
            });
        }

        private handleBrowse(event: api.app.bar.event.ShowBrowsePanelEvent) {
            var browsePanel: api.app.browse.BrowsePanel<api.schema.Schema> = this.getBrowsePanel();
            if (!browsePanel) {
                this.addBrowsePanel(new app.browse.SchemaBrowsePanel(this.browseActions, this.schemaTreeGrid));
            } else {
                this.showPanel(browsePanel);
            }
        }

        private handleNew(event: app.create.NewSchemaEvent) {
            var schemaKind: api.schema.SchemaKind = event.getSchemaKind();
            var tabId = api.app.bar.AppBarTabId.forNew(schemaKind.toString());
            var tabMenuItem = this.getAppBarTabMenu().getNavigationItemById(tabId);

            if (tabMenuItem != null) {
                this.selectPanel(tabMenuItem);

            } else {
                switch (schemaKind) {
                case api.schema.SchemaKind.CONTENT_TYPE:
                    tabMenuItem =
                    new api.app.bar.AppBarTabMenuItem("[" + app.wizard.ContentTypeWizardPanel.NEW_WIZARD_HEADER + "]", tabId, true);
                    new app.wizard.ContentTypeWizardPanel(tabId, null, (wizard: app.wizard.ContentTypeWizardPanel) => {
                        this.addWizardPanel(tabMenuItem, wizard);
                    });
                    break;
                case api.schema.SchemaKind.RELATIONSHIP_TYPE:
                    tabMenuItem =
                    new api.app.bar.AppBarTabMenuItem("[" + app.wizard.RelationshipTypeWizardPanel.NEW_WIZARD_HEADER + "]", tabId, true);
                    new app.wizard.RelationshipTypeWizardPanel(tabId, null, (wizard: app.wizard.RelationshipTypeWizardPanel) => {
                        this.addWizardPanel(tabMenuItem, wizard);
                    });
                    break;
                case api.schema.SchemaKind.MIXIN:
                    tabMenuItem = new api.app.bar.AppBarTabMenuItem("[" + app.wizard.MixinWizardPanel.NEW_WIZARD_HEADER + "]", tabId, true);
                    new app.wizard.MixinWizardPanel(tabId, null, (wizard: app.wizard.MixinWizardPanel) => {
                        this.addWizardPanel(tabMenuItem, wizard);
                    });
                    break;
                }
            }
        }

        private handleView(event) {
            event.getSchemas().forEach((schema: api.schema.Schema) => {

                    var tabId = api.app.bar.AppBarTabId.forEdit(schema.getId());
                    var tabMenuItem = this.getAppBarTabMenu().getNavigationItemById(tabId);

                    if (tabMenuItem != null) {
                        this.selectPanel(tabMenuItem);

                    } else {
                        tabId = api.app.bar.AppBarTabId.forView(schema.getKey());
                        tabMenuItem = this.getAppBarTabMenu().getNavigationItemById(tabId);

                        if (tabMenuItem != null) {
                            this.selectPanel(tabMenuItem);

                        } else {
                            tabMenuItem = new api.app.bar.AppBarTabMenuItem(schema.getName(), tabId);
                            var schemaItemViewPanel = new app.view.SchemaItemViewPanel();
                            var schemaViewItem = new api.app.view.ViewItem<api.schema.Schema>(schema)
                                .setDisplayName(schema.getDisplayName())
                                .setPath(schema.getName())
                                .setIconUrl(schema.getIconUrl());

                            schemaItemViewPanel.setItem(schemaViewItem);

                            this.addViewPanel(tabMenuItem, schemaItemViewPanel);
                        }
                    }
                }
            );
        }

        private handleEdit(event) {
            event.getSchemas().forEach((schema: api.schema.Schema) => {

                var tabId = api.app.bar.AppBarTabId.forEdit(schema.getKey());
                var tabMenuItem = this.getAppBarTabMenu().getNavigationItemById(tabId);

                var self = this;

                if (tabMenuItem != null) {
                    this.selectPanel(tabMenuItem);

                } else {
                    var promiseCreateWizardPanel = null;

                    if (schema.getSchemaKind() == api.schema.SchemaKind.CONTENT_TYPE) {
                        var contentType = <api.schema.content.ContentType>schema;
                        promiseCreateWizardPanel = new api.schema.content.GetContentTypeByNameRequest(contentType.getContentTypeName()).
                            sendAndParse().then((contentType: api.schema.content.ContentType) => {

                                tabMenuItem = new api.app.bar.AppBarTabMenuItem(contentType.getName(), tabId, true);

                                new app.wizard.ContentTypeWizardPanel(tabId, contentType,
                                    (wizard: app.wizard.ContentTypeWizardPanel) => {
                                        this.addWizardPanel(tabMenuItem, wizard);
                                    });
                            });
                    }
                    else if (schema.getSchemaKind() == api.schema.SchemaKind.RELATIONSHIP_TYPE) {
                        var relationhipType = <api.schema.relationshiptype.RelationshipType>schema;
                        promiseCreateWizardPanel =
                        new api.schema.relationshiptype.GetRelationshipTypeByNameRequest(relationhipType.getRelationshiptypeName()).
                            sendAndParse().then((relationshipType: api.schema.relationshiptype.RelationshipType) => {

                                tabMenuItem = new api.app.bar.AppBarTabMenuItem(relationshipType.getDisplayName(), tabId, true);

                                new app.wizard.RelationshipTypeWizardPanel(tabId, relationshipType,
                                    (wizard: app.wizard.RelationshipTypeWizardPanel) => {
                                        this.addWizardPanel(tabMenuItem, wizard);
                                    });
                            });
                    }
                    else if (schema.getSchemaKind() == api.schema.SchemaKind.MIXIN) {
                        var mixin = <api.schema.mixin.Mixin>schema;
                        promiseCreateWizardPanel = new api.schema.mixin.GetMixinByQualifiedNameRequest(mixin.getMixinName()).
                            sendAndParse().then((mixin: api.schema.mixin.Mixin)=> {

                                tabMenuItem = new api.app.bar.AppBarTabMenuItem(mixin.getDisplayName(), tabId, true);

                                new app.wizard.MixinWizardPanel(tabId, mixin, (wizard: app.wizard.MixinWizardPanel) => {
                                    this.addWizardPanel(tabMenuItem, wizard);
                                });
                            });
                    }
                    else {
                        throw new Error("Unknown SchemaKind: " + schema.getSchemaKind())
                    }

                    if (promiseCreateWizardPanel) {
                        promiseCreateWizardPanel.then(() => {
                            var viewTabId = api.app.bar.AppBarTabId.forView(schema.getId());
                            var viewTabMenuItem = self.getAppBarTabMenu().getNavigationItemById(viewTabId);
                            if (viewTabMenuItem != null) {
                                self.removePanelByIndex(viewTabMenuItem.getIndex());
                            }
                        }).
                            catch((reason: any) => api.DefaultErrorHandler.handle(reason)).
                            done();
                    }
                }
            });
        }

    }
}