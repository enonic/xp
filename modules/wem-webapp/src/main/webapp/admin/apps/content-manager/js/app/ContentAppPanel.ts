module app {

    export class ContentAppPanel extends api_app.BrowseAndWizardBasedAppPanel<api_content.ContentSummary> {

        constructor(appBar: api_app.AppBar) {

            var browsePanel = new app_browse.ContentBrowsePanel();

            super({
                appBar: appBar,
                browsePanel: browsePanel
            });

            app_new.NewContentEvent.on((event) => {
                this.handleNew(event);
            });

            app_browse.ViewContentEvent.on((event) => {
                this.handleView(event);
            });

            app_browse.EditContentEvent.on((event) => {
                this.handleEdit(event);
            });

            api_app.ShowAppBrowsePanelEvent.on((event) => {
                this.showHomePanel();
                this.getAppBarTabMenu().deselectNavigationItem();
            });

            app_browse.CloseContentEvent.on((event) => {
                this.removePanel(event.getPanel(), event.isCheckCanRemovePanel());
            });
        }

        addWizardPanel(tabMenuItem: api_app.AppBarTabMenuItem, wizardPanel: api_app_wizard.WizardPanel<api_content.Content>) {
            super.addWizardPanel(tabMenuItem, wizardPanel);

            wizardPanel.getHeader().addListener(
                {
                    onPropertyChanged: (event: api_app_wizard.WizardHeaderPropertyChangedEvent) => {
                        if (event.property == "displayName") {
                            tabMenuItem.setLabel(event.newValue);
                        }
                    }
                });
        }

        private handleNew(newContentEvent: app_new.NewContentEvent) {

            var contentTypeSummary = newContentEvent.getContentType();
            var parentContent = newContentEvent.getParentContent();
            var tabId = api_app.AppBarTabId.forNew(contentTypeSummary.getName());
            var tabMenuItem = this.getAppBarTabMenu().getNavigationItemById(tabId);

            if (tabMenuItem != null) {
                this.selectPanel(tabMenuItem);

            } else {

                tabMenuItem = new api_app.AppBarTabMenuItem("New " + contentTypeSummary.getDisplayName(), tabId);

                new app_wizard.ContentWizardPanelFactory().
                    setAppBarTabId(tabId).
                    setParentContent(parentContent).
                    setContentTypeName(contentTypeSummary.getContentTypeName()).
                    setSiteTemplate(newContentEvent.getSiteTemplate() != null ? newContentEvent.getSiteTemplate().getKey()
                        : null).
                    createForNew().then((wizard: app_wizard.ContentWizardPanel) => {
                        this.addWizardPanel(tabMenuItem, wizard);
                        wizard.initWizardPanel();
                        wizard.reRender();
                    }).done();
            }
        }

        private handleView(event: app_browse.ViewContentEvent) {

            var contents: api_content.ContentSummary[] = event.getModels();
            contents.forEach((content: api_content.ContentSummary) => {

                var tabId = api_app.AppBarTabId.forView(content.getId());
                var tabMenuItem = this.getAppBarTabMenu().getNavigationItemById(tabId);

                if (tabMenuItem != null) {
                    this.selectPanel(tabMenuItem);

                } else {
                    tabMenuItem = new api_app.AppBarTabMenuItem(content.getDisplayName(), tabId);
                    var contentItemViewPanel = new app_view.ContentItemViewPanel({
                        showPreviewAction: app_browse.ContentBrowseActions.get().SHOW_PREVIEW,
                        showDetailsAction: app_browse.ContentBrowseActions.get().SHOW_DETAILS
                    });

                    var contentItem = new api_app_view.ViewItem(content)
                        .setDisplayName(content.getDisplayName())
                        .setPath(content.getPath().toString())
                        .setIconUrl(content.getIconUrl());

                    contentItemViewPanel.setItem(contentItem);

                    this.addViewPanel(tabMenuItem, contentItemViewPanel);
                }
            });
        }

        private handleEdit(event: app_browse.EditContentEvent) {

            var contents: api_content.ContentSummary[] = event.getModels();
            contents.forEach((content: api_content.ContentSummary) => {

                var tabId = api_app.AppBarTabId.forEdit(content.getId());
                var tabMenuItem = this.getAppBarTabMenu().getNavigationItemById(tabId);

                if (tabMenuItem != null) {
                    this.selectPanel(tabMenuItem);

                } else {

                    new app_wizard.ContentWizardPanelFactory().
                        setAppBarTabId(tabId).
                        setContentToEdit(content.getContentId()).
                        createForEdit().then((wizard: app_wizard.ContentWizardPanel) => {

                            tabMenuItem = new api_app.AppBarTabMenuItem(content.getDisplayName(), tabId, true);
                            this.addWizardPanel(tabMenuItem, wizard);
                            wizard.initWizardPanel();
                            wizard.reRender();
                        }).
                        done();

                }
            });
        }
    }

}
