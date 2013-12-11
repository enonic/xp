module app {

    export class ContentAppPanel extends api_app.BrowseAndWizardBasedAppPanel<api_content.ContentSummary> {

        constructor(appBar: api_app.AppBar) {

            var browsePanel = new app_browse.ContentBrowsePanel();

            super({
                appBar: appBar,
                browsePanel: browsePanel
            });

            app_new.NewContentEvent.on((event) => {
                this.handleNew(event.getContentType(), event.getParentContent(), event.isSiteRoot());
            });

            app_browse.OpenContentEvent.on((event) => {
                this.handleOpen(event.getModels());
            });

            app_browse.EditContentEvent.on((event) => {
                this.handleEdit(event.getModels());
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

        private handleNew(contentTypeSummary: api_schema_content.ContentTypeSummary, parentContent: api_content.Content,
                          siteRoot: boolean) {

            var tabId = api_app.AppBarTabId.forNew(contentTypeSummary.getName());
            var tabMenuItem = this.getAppBarTabMenu().getNavigationItemById(tabId);

            if (tabMenuItem != null) {
                this.selectPanel(tabMenuItem);

            } else {

                new api_schema_content.GetContentTypeByNameRequest(contentTypeSummary.getContentTypeName()).
                    sendAndParse().done((contentType: api_schema_content.ContentType) => {

                        new api_content_site.GetNearestSiteRequest(parentContent.getContentId()).
                            sendAndParse().
                            done((site: api_content.Content)=> {

                                tabMenuItem = new api_app.AppBarTabMenuItem("New " + contentTypeSummary.getDisplayName(), tabId);
                                var wizardPanel;
                                if (siteRoot) {
                                    wizardPanel = new app_wizard.SiteWizardPanel(tabId, contentType, parentContent, site);
                                } else {
                                    wizardPanel = new app_wizard.ContentWizardPanel(tabId, contentType, parentContent, site);
                                }
                                wizardPanel.renderNew();
                                this.addWizardPanel(tabMenuItem, wizardPanel);
                                wizardPanel.reRender();
                            });
                    });
            }
        }

        private handleOpen(contents: api_content.ContentSummary[]) {

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

        private handleEdit(contents: api_content.ContentSummary[]) {

            contents.forEach((content: api_content.ContentSummary) => {

                var tabId = api_app.AppBarTabId.forEdit(content.getId());
                var tabMenuItem = this.getAppBarTabMenu().getNavigationItemById(tabId);

                if (tabMenuItem != null) {
                    this.selectPanel(tabMenuItem);

                } else {

                    var getContentByIdPromise = new api_content.GetContentByIdRequest(content.getContentId()).sendAndParse();
                    var getContentTypeByNamePromise = new api_schema_content.GetContentTypeByNameRequest(content.getType()).sendAndParse();
                    jQuery.
                        when(getContentByIdPromise, getContentTypeByNamePromise).
                        then((contentToEdit: api_content.Content, contentType: api_schema_content.ContentType) => {

                            new api_content_site.GetNearestSiteRequest(contentToEdit.getContentId()).
                                sendAndParse().
                                done((site: api_content.Content)=> {

                                    tabMenuItem = new api_app.AppBarTabMenuItem(contentToEdit.getDisplayName(), tabId, true);

                                    if (contentToEdit.hasParent()) {
                                        new api_content.GetContentByPathRequest(contentToEdit.getPath().getParentPath()).
                                            sendAndParse().
                                            done((parentContent: api_content.Content) => {
                                                var contentWizardPanel = this.createContentWizardPanel(tabId, contentToEdit, contentType,
                                                    parentContent, site);

                                                this.addWizardPanel(tabMenuItem, contentWizardPanel);
                                            });
                                    }
                                    else {
                                        var contentWizardPanel = this.createContentWizardPanel(tabId, contentToEdit, contentType, null,
                                            site);
                                        this.addWizardPanel(tabMenuItem, contentWizardPanel);
                                    }
                                });

                        });

                }
            });
        }

        private createContentWizardPanel(tabId: api_app.AppBarTabId, contentToEdit: api_content.Content,
                                         contentType: api_schema_content.ContentType, parentContent: api_content.Content,
                                         site: api_content.Content): app_wizard.ContentWizardPanel {

            if (contentToEdit.isSite()) {
                var siteWizardPanel = new app_wizard.SiteWizardPanel(tabId, contentType,
                    parentContent, site);
                siteWizardPanel.setPersistedItem(contentToEdit);
                return siteWizardPanel;
            }
            else {
                var contentWizardPanel = new app_wizard.ContentWizardPanel(tabId, contentType,
                    parentContent, site);
                contentWizardPanel.setPersistedItem(contentToEdit);
                return contentWizardPanel;
            }
        }
    }

}
