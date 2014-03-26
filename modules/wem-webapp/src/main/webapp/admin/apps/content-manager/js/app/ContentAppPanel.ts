module app {

    export class ContentAppPanel extends api.app.BrowseAndWizardBasedAppPanel<api.content.ContentSummary> {

        private mask: api.ui.LoadMask;

        constructor(appBar: api.app.AppBar) {

            var browsePanel = new app.browse.ContentBrowsePanel();

            super({
                appBar: appBar,
                browsePanel: browsePanel
            });

            this.mask = new api.ui.LoadMask(this);

            app.create.NewContentEvent.on((event) => {
                this.handleNew(event);
            });

            app.browse.ViewContentEvent.on((event) => {
                this.handleView(event);
            });

            app.browse.EditContentEvent.on((event) => {
                this.handleEdit(event);
            });

            api.app.ShowAppBrowsePanelEvent.on((event) => {
                this.showHomePanel();
                this.getAppBarTabMenu().deselectNavigationItem();
            });

            app.browse.CloseContentEvent.on((event) => {
                this.removePanel(event.getPanel(), event.isCheckCanRemovePanel());
            });
        }

        addWizardPanel(tabMenuItem: api.app.AppBarTabMenuItem, wizardPanel: api.app.wizard.WizardPanel<api.content.Content>) {
            super.addWizardPanel(tabMenuItem, wizardPanel);

            wizardPanel.getHeader().onPropertyChanged((event: api.app.wizard.PropertyChangedEvent) => {
                if (event.getProperty() == "displayName") {
                    tabMenuItem.setLabel(event.getNewValue());
                }
            });
        }

        private handleNew(newContentEvent: app.create.NewContentEvent) {

            var contentTypeSummary = newContentEvent.getContentType();
            var parentContent = newContentEvent.getParentContent();
            var tabId = api.app.AppBarTabId.forNew(contentTypeSummary.getName());
            var tabMenuItem = this.getAppBarTabMenu().getNavigationItemById(tabId);

            if (tabMenuItem != null) {
                this.selectPanel(tabMenuItem);

            }
            else {
                this.mask.show();
                tabMenuItem = new api.app.AppBarTabMenuItem("New " + contentTypeSummary.getDisplayName(), tabId);

                var contentWizardPanelFactory = new app.wizard.ContentWizardPanelFactory().
                    setAppBarTabId(tabId).
                    setParentContent(parentContent).
                    setContentTypeName(contentTypeSummary.getContentTypeName());

                if (newContentEvent.getSiteTemplate() != null) {
                    contentWizardPanelFactory.setCreateSite(newContentEvent.getSiteTemplate().getKey());
                }

                contentWizardPanelFactory.createForNew().then((wizard: app.wizard.ContentWizardPanel) => {
                    this.addWizardPanel(tabMenuItem, wizard);
                }).finally(() => {
                    this.mask.hide();
                }).done();
            }
        }

        private handleView(event: app.browse.ViewContentEvent) {

            var contents: api.content.ContentSummary[] = event.getModels();
            contents.forEach((content: api.content.ContentSummary) => {

                var tabMenuItem = this.isContentBeingEditedOrViewed(content);

                if (tabMenuItem) {
                    this.selectPanel(tabMenuItem);

                } else {
                    var tabId = api.app.AppBarTabId.forView(content.getId());
                    tabMenuItem = new api.app.AppBarTabMenuItem(content.getDisplayName(), tabId);
                    var contentItemViewPanel = new app.view.ContentItemViewPanel({
                        showPreviewAction: app.browse.ContentBrowseActions.get().SHOW_PREVIEW,
                        showDetailsAction: app.browse.ContentBrowseActions.get().SHOW_DETAILS
                    });

                    var contentItem = new api.app.view.ViewItem(content)
                        .setDisplayName(content.getDisplayName())
                        .setPath(content.getPath().toString())
                        .setIconUrl(content.getIconUrl());

                    contentItemViewPanel.setItem(contentItem);

                    this.addViewPanel(tabMenuItem, contentItemViewPanel);
                }
            });
        }

        private handleEdit(event: app.browse.EditContentEvent) {

            var contents: api.content.ContentSummary[] = event.getModels();
            contents.forEach((content: api.content.ContentSummary) => {

                var tabMenuItem = this.isContentBeingEditedOrViewed(content);

                if (tabMenuItem != null) {
                    this.selectPanel(tabMenuItem);

                } else {
                    this.mask.show();
                    var tabId = api.app.AppBarTabId.forEdit(content.getId());

                    new app.wizard.ContentWizardPanelFactory().
                        setAppBarTabId(tabId).
                        setContentToEdit(content.getContentId()).
                        createForEdit().then((wizard: app.wizard.ContentWizardPanel) => {

                            tabMenuItem = new api.app.AppBarTabMenuItem(content.getDisplayName(), tabId, true);
                            this.addWizardPanel(tabMenuItem, wizard);

                            var viewTabId = api.app.AppBarTabId.forView(content.getId());
                            var viewTabMenuItem = this.getAppBarTabMenu().getNavigationItemById(viewTabId);
                            if (viewTabMenuItem != null) {
                                this.removePanelByIndex(viewTabMenuItem.getIndex());
                            }
                        }).finally(() => {
                            this.mask.hide();
                        }).done();

                }
            });
        }

        private isContentBeingEditedOrViewed(content: api.content.ContentSummary): api.app.AppBarTabMenuItem {
            var tabId = this.getAppBarTabMenu().getNavigationItemById(api.app.AppBarTabId.forEdit(content.getId()));
            if (tabId) {
                return tabId;
            }
            tabId = this.getAppBarTabMenu().getNavigationItemById(api.app.AppBarTabId.forView(content.getId()));
            if (tabId) {
                return tabId;
            }
            else {
                return null;
            }
        }
    }

}
