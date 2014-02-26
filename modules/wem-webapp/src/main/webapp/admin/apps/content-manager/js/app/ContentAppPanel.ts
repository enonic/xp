module app {

    export class ContentAppPanel extends api.app.BrowseAndWizardBasedAppPanel<api.content.ContentSummary> {

        constructor(appBar: api.app.AppBar) {

            var browsePanel = new app.browse.ContentBrowsePanel();

            super({
                appBar: appBar,
                browsePanel: browsePanel
            });

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

            wizardPanel.getHeader().addListener(
                {
                    onPropertyChanged: (event: api.app.wizard.WizardHeaderPropertyChangedEvent) => {
                        if (event.property == "displayName") {
                            tabMenuItem.setLabel(event.newValue);
                        }
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

                tabMenuItem = new api.app.AppBarTabMenuItem("New " + contentTypeSummary.getDisplayName(), tabId);

                var contentWizardPanelFactory = new app.wizard.ContentWizardPanelFactory().
                    setAppBarTabId(tabId).
                    setParentContent(parentContent).
                    setContentTypeName(contentTypeSummary.getContentTypeName());

                if (newContentEvent.getSiteTemplate() != null) {
                    contentWizardPanelFactory.setCreateSite(newContentEvent.getSiteTemplate().getKey());
                }

                contentWizardPanelFactory.createForNew().
                    then((wizard: app.wizard.ContentWizardPanel) => {
                        this.addWizardPanel(tabMenuItem, wizard);
                    }).done();
            }
        }

        private handleView(event: app.browse.ViewContentEvent) {

            var contents: api.content.ContentSummary[] = event.getModels();
            contents.forEach((content: api.content.ContentSummary) => {

                var tabId = api.app.AppBarTabId.forView(content.getId());
                var tabMenuItem = this.getAppBarTabMenu().getNavigationItemById(tabId);

                if (tabMenuItem != null) {
                    this.selectPanel(tabMenuItem);

                } else {
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

                var tabId = api.app.AppBarTabId.forEdit(content.getId());
                var tabMenuItem = this.getAppBarTabMenu().getNavigationItemById(tabId);

                if (tabMenuItem != null) {
                    this.selectPanel(tabMenuItem);

                } else {

                    new app.wizard.ContentWizardPanelFactory().
                        setAppBarTabId(tabId).
                        setContentToEdit(content.getContentId()).
                        createForEdit().then((wizard: app.wizard.ContentWizardPanel) => {

                            tabMenuItem = new api.app.AppBarTabMenuItem(content.getDisplayName(), tabId, true);
                            this.addWizardPanel(tabMenuItem, wizard);
                        }).
                        done();

                }
            });
        }
    }

}
