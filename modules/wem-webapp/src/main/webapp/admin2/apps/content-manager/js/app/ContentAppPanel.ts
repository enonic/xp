module app {

    export class ContentAppPanel extends api_app.BrowseAndWizardBasedAppPanel {

        private browsePanel:app_browse.ContentBrowsePanel;

        private appBarTabMenu:api_app.AppBarTabMenu;

        constructor(appBar:api_app.AppBar) {

            this.browsePanel = new app_browse.ContentBrowsePanel();
            this.appBarTabMenu = appBar.getTabMenu();

            super({
                appBar: appBar,
                browsePanel: this.browsePanel,
                browsePanelActions: app_browse.ContentBrowseActions.get().getAllActions()
            });

            app_new.NewContentEvent.on((event) => {
                this.handleNew(event.getContentType(), event.getParentContent());
            });

            app_browse.OpenContentEvent.on((event) => {
                this.handleOpen(event.getModels());
            });

            app_browse.EditContentEvent.on((event) => {
                this.handleEdit(event.getModels());
            });

            api_app.ShowAppBrowsePanelEvent.on((event) => {
                this.showHomePanel();
                this.appBarTabMenu.deselectNavigationItem();
            });

            api_ui_tab.TabMenuItemCloseEvent.on((event) => {
                var tabIndex = event.getTab().getIndex();
                var panel = this.getPanel(tabIndex);
                new app_browse.CloseContentEvent(panel, true).fire();
            });

            app_browse.CloseContentEvent.on((event) => {
                this.removePanel(event.getPanel(), event.isCheckCanRemovePanel());
            });
        }

        addWizardPanel(tabMenuItem:api_app.AppBarTabMenuItem, wizardPanel:api_app_wizard.WizardPanel, inBackground?:bool = false) {
            super.addWizardPanel(tabMenuItem, wizardPanel, inBackground);

            wizardPanel.getHeader().addListener(
                {
                    onPropertyChanged: (event:api_app_wizard.WizardHeaderPropertyChangedEvent) => {
                        if (event.property == "displayName") {
                            tabMenuItem.setLabel(event.newValue);
                        }
                    }
                });
        }

        private handleNew(contentType:api_remote_contenttype.ContentType, parentContent:api_remote_content.Content) {

            var tabId = this.generateTabId();
            var tabMenuItem = this.appBarTabMenu.getNavigationItemById(tabId);

            if (tabMenuItem != null) {
                this.selectPanel(tabMenuItem);

            } else {
                tabMenuItem = new api_app.AppBarTabMenuItem(app_wizard.ContentWizardPanel.NEW_WIZARD_HEADER, tabId);
                var wizardPanel = new app_wizard.ContentWizardPanel(tabId, contentType, parentContent);
                wizardPanel.renderNew();

                this.addWizardPanel(tabMenuItem, wizardPanel);

                wizardPanel.reRender();
            }
        }

        private handleOpen(contents:api_model.ContentExtModel[]) {

            contents.forEach((contentModel:api_model.ContentExtModel) => {

                var tabId = this.generateTabId(contentModel.data.path, false);
                var tabMenuItem = this.appBarTabMenu.getNavigationItemById(tabId);

                if (tabMenuItem != null) {
                    this.selectPanel(tabMenuItem);

                } else {
                    tabMenuItem = new api_app.AppBarTabMenuItem(contentModel.data.displayName, tabId);
                    var contentItemViewPanel = new app_view.ContentItemViewPanel({
                        showPreviewAction: app_browse.ContentBrowseActions.get().SHOW_PREVIEW,
                        showDetailsAction: app_browse.ContentBrowseActions.get().SHOW_DETAILS
                    });

                    var contentItem = new api_app_view.ViewItem(contentModel)
                        .setDisplayName(contentModel.data.displayName)
                        .setPath(contentModel.data.path)
                        .setIconUrl(contentModel.data.iconUrl);

                    contentItemViewPanel.setItem(contentItem);

                    this.addNavigationItem(tabMenuItem, contentItemViewPanel);
                }
            });
        }

        private handleEdit(contents:api_model.ContentExtModel[]) {

            contents.forEach((contentModel:api_model.ContentExtModel) => {

                var tabId = this.generateTabId(contentModel.data.path, true);
                var tabMenuItem = this.appBarTabMenu.getNavigationItemById(tabId);

                if (tabMenuItem != null) {
                    this.selectPanel(tabMenuItem);

                } else {
                    api_remote_content.RemoteContentService.content_get({
                            contentIds: [contentModel.data.id]
                        },
                        (contentResult:api_remote_content.GetResult) => {

                            var contentToEdit:api_remote_content.Content = contentResult.content[0];
                            // Fetch content type of content to edit
                            api_remote_contenttype.RemoteContentTypeService.contentType_get({
                                    qualifiedNames: [contentToEdit.type],
                                    format: "JSON",
                                    mixinReferencesToFormItems: true
                                },
                                (contentTypeResult:api_remote_contenttype.GetResult) => {

                                    var contentType:api_remote_contenttype.ContentType = contentTypeResult.contentTypes[0];
                                    tabMenuItem = new api_app.AppBarTabMenuItem(contentToEdit.displayName, tabId, true);

                                    var contentToEditPath:api_content.ContentPath = api_content.ContentPath.fromString(contentToEdit.path);
                                    var parentContentPath:api_content.ContentPath = contentToEditPath.getParentPath();
                                    if (parentContentPath != null) {
                                        // Fetch parent of content to edit
                                        api_remote_content.RemoteContentService.content_get({
                                                path: parentContentPath.toString()
                                            },
                                            (parentContentResult:api_remote_content.GetResult) => {

                                                var contentWizardPanel = new app_wizard.ContentWizardPanel(tabId,
                                                    contentType, parentContentResult.content[0]);

                                                contentWizardPanel.renderExisting(contentResult);
                                                this.addWizardPanel(tabMenuItem, contentWizardPanel);
                                            });
                                    }
                                    else {
                                        var contentWizardPanel = new app_wizard.ContentWizardPanel(tabId,
                                            contentType, null);
                                        contentWizardPanel.renderExisting(contentResult);
                                        this.addWizardPanel(tabMenuItem, contentWizardPanel);
                                    }
                                });

                        });
                }
            });
        }

        private generateTabId(contentName?:string, isEdit?:bool = false) {
            return contentName ? ( isEdit ? 'edit-' : 'view-') + contentName : 'new-';
        }
    }

}
