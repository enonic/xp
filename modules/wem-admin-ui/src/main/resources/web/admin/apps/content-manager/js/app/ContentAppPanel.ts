module app {

    import ContentIconUrlResolver = api.content.ContentIconUrlResolver;

    export class ContentAppPanel extends api.app.BrowseAndWizardBasedAppPanel<api.content.ContentSummary> {

        private mask: api.ui.mask.LoadMask;

        constructor(appBar: api.app.bar.AppBar, path?: api.rest.Path) {

            super({
                appBar: appBar
            });

            this.mask = new api.ui.mask.LoadMask(this);

            this.handleGlobalEvents();

            this.route(path);
        }

        addWizardPanel(tabMenuItem: api.app.bar.AppBarTabMenuItem, wizardPanel: api.app.wizard.WizardPanel<api.content.Content>) {
            super.addWizardPanel(tabMenuItem, wizardPanel);

            wizardPanel.getHeader().onPropertyChanged((event: api.PropertyChangedEvent) => {
                if (event.getPropertyName() == "displayName") {
                    tabMenuItem.setLabel(<string>event.getNewValue());
                }
            });
        }

        private route(path: api.rest.Path) {
            if (path) {
                var action = path.getElement(0);

                switch (action) {
                case 'edit':
                    var id = path.getElement(1);
                    if (id) {
                        new api.content.GetContentByIdRequest(new api.content.ContentId(id)).sendAndParse().
                            done((content: api.content.Content) => {
                                new app.browse.EditContentEvent([content]).fire();
                            });
                    }
                    break;
                case 'view' :
                    var id = path.getElement(1);
                    if (id) {
                        new api.content.GetContentByIdRequest(new api.content.ContentId(id)).sendAndParse().
                            done((content: api.content.Content) => {
                                new app.browse.ViewContentEvent([content]).fire();
                            });
                    }
                    break;
                default:
                    new api.app.bar.event.ShowBrowsePanelEvent().fire();
                    break;
                }
            }
        }

        private handleGlobalEvents() {
            app.create.NewContentEvent.on((event) => {
                this.handleNew(event);
            });

            app.browse.ViewContentEvent.on((event) => {
                this.handleView(event);
            });

            app.browse.EditContentEvent.on((event) => {
                this.handleEdit(event);
            });

            api.app.bar.event.ShowBrowsePanelEvent.on((event) => {
                this.handleBrowse(event);
            });

            api.content.ContentCreatedEvent.on((event) => {
                this.handleCreated(event);
            });

            api.content.ContentUpdatedEvent.on((event) => {
                this.handleUpdated(event);
            });
        }

        private handleCreated(event: api.content.ContentCreatedEvent) {

            var wizard = event.getWizard(),
                tabMenuItem = this.getAppBarTabMenu().getNavigationItemById(wizard.getTabId());
            // update tab id so that new wizard for the same content type can be created
            var newTabId = api.app.bar.AppBarTabId.forEdit(event.getContent().getId());
            tabMenuItem.setTabId(newTabId);
            wizard.setTabId(newTabId);
        }

        private handleUpdated(event: api.content.ContentUpdatedEvent) {
            // do something when content is updated
        }

        private handleBrowse(event: api.app.bar.event.ShowBrowsePanelEvent) {
            var browsePanel: api.app.browse.BrowsePanel<api.content.ContentSummary> = this.getBrowsePanel();
            if (!browsePanel) {
                this.addBrowsePanel(new app.browse.ContentBrowsePanel());
            } else {
                this.selectPanelByIndex(this.getPanelIndex(browsePanel));
            }
        }

        private handleNew(newContentEvent: app.create.NewContentEvent) {

            var contentTypeSummary = newContentEvent.getContentType();
            var parentContent = newContentEvent.getParentContent();
            var tabId = api.app.bar.AppBarTabId.forNew(contentTypeSummary.getName());
            var tabMenuItem = this.getAppBarTabMenu().getNavigationItemById(tabId);

            if (tabMenuItem != null) {
                this.selectPanel(tabMenuItem);
            } else {
                this.mask.show();
                var contentWizardPanelFactory = new app.wizard.ContentWizardPanelFactory().
                    setAppBarTabId(tabId).
                    setParentContent(parentContent).
                    setContentTypeName(contentTypeSummary.getContentTypeName());

                contentWizardPanelFactory.setCreateSite(newContentEvent.isCreateSite());
                contentWizardPanelFactory.createForNew().then((wizard: app.wizard.ContentWizardPanel) => {
                    tabMenuItem = new api.app.bar.AppBarTabMenuItemBuilder().setLabel("[New " + contentTypeSummary.getDisplayName() + "]").
                        setTabId(tabId).
                        setCloseAction(wizard.getCloseAction()).
                        build();
                    this.addWizardPanel(tabMenuItem, wizard);
                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).finally(() => {
                    this.mask.hide();
                }).done();
            }
        }

        private handleView(event: app.browse.ViewContentEvent) {

            var contents: api.content.ContentSummary[] = event.getModels();
            contents.forEach((content: api.content.ContentSummary) => {
                if (!content) {
                    return;
                }

                var tabMenuItem = this.resolveTabMenuItemForContentBeingEditedOrViewed(content);

                if (tabMenuItem) {
                    this.selectPanel(tabMenuItem);
                } else {
                    var tabId = api.app.bar.AppBarTabId.forView(content.getId());
                    var contentItemViewPanel = new app.view.ContentItemViewPanel();
                    tabMenuItem = new api.app.bar.AppBarTabMenuItemBuilder().setLabel(content.getDisplayName()).
                        setTabId(tabId).
                        setCloseAction(contentItemViewPanel.getCloseAction()).
                        build();

                    var contentItem = new api.app.view.ViewItem(content)
                        .setDisplayName(content.getDisplayName())
                        .setPath(content.getPath().toString())
                        .setIconUrl(new ContentIconUrlResolver().setContent(content).resolve());

                    contentItemViewPanel.setItem(contentItem);

                    this.addViewPanel(tabMenuItem, contentItemViewPanel);
                }
            });
        }

        private handleEdit(event: app.browse.EditContentEvent) {

            var contents: api.content.ContentSummary[] = event.getModels();
            contents.forEach((content: api.content.ContentSummary) => {
                if (!content) {
                    return;
                }
                var closeViewPanelMenuItem = this.resolveTabMenuItemForContentBeingViewed(content);
                var tabMenuItem = this.resolveTabMenuItemForContentBeingEdited(content);

                if (tabMenuItem != null) {
                    this.selectPanel(tabMenuItem);
                } else {
                    this.mask.show();
                    var tabId = api.app.bar.AppBarTabId.forEdit(content.getId());

                    new app.wizard.ContentWizardPanelFactory().
                        setAppBarTabId(tabId).
                        setContentToEdit(content.getContentId()).
                        createForEdit().then((wizard: app.wizard.ContentWizardPanel) => {
                            if (closeViewPanelMenuItem != null) {
                                this.getAppBarTabMenu().deselectNavigationItem();
                                this.getAppBarTabMenu().removeNavigationItem(closeViewPanelMenuItem);
                                this.removePanelByIndex(closeViewPanelMenuItem.getIndex());
                            }
                            tabMenuItem = new api.app.bar.AppBarTabMenuItemBuilder().setLabel(content.getDisplayName()).
                                setTabId(tabId).
                                setEditing(true).
                                setCloseAction(wizard.getCloseAction()).
                                build();
                            this.addWizardPanel(tabMenuItem, wizard);

                            var viewTabId = api.app.bar.AppBarTabId.forView(content.getId());
                            var viewTabMenuItem = this.getAppBarTabMenu().getNavigationItemById(viewTabId);
                            if (viewTabMenuItem != null) {
                                this.removePanelByIndex(viewTabMenuItem.getIndex());
                            }
                        }).catch((reason: any) => {
                            api.DefaultErrorHandler.handle(reason);
                        }).finally(() => {
                            this.mask.hide();
                        }).done();

                }
            });
        }

        private resolveTabMenuItemForContentBeingEditedOrViewed(content: api.content.ContentSummary): api.app.bar.AppBarTabMenuItem {
            var result = this.resolveTabMenuItemForContentBeingEdited(content);
            if (!result) {
                result = this.resolveTabMenuItemForContentBeingViewed(content)
            }
            return result;
        }

        private resolveTabMenuItemForContentBeingEdited(content: api.content.ContentSummary): api.app.bar.AppBarTabMenuItem {
            if (!!content) {

                var tabId = this.getAppBarTabMenu().getNavigationItemById(api.app.bar.AppBarTabId.forEdit(content.getId()));
                if (tabId) {
                    return tabId;
                }
            }
            return null;
        }

        private resolveTabMenuItemForContentBeingViewed(content: api.content.ContentSummary): api.app.bar.AppBarTabMenuItem {
            if (!!content) {
                var tabId = this.getAppBarTabMenu().getNavigationItemById(api.app.bar.AppBarTabId.forView(content.getId()));
                if (tabId) {
                    return tabId;
                }
            }

            return null;
        }
    }

}
