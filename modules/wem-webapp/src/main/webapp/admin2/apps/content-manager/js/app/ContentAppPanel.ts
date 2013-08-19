module app {

    export class ContentAppPanel extends api_app.BrowseAndWizardBasedAppPanel {

        private browsePanel:app_browse.ContentBrowsePanel;

        private appBarTabMenu:api_app.AppBarTabMenu;

        constructor(appBar:ContentAppBar) {

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

        private handleNew(contentType:api_remote_contenttype.ContentType, parentContent:api_remote_content.Content) {

            var tabMenuItem = new ContentAppBarTabMenuItem("New Content");
            var wizardPanel = new app_wizard.ContentWizardPanel('new-content', contentType, parentContent);
            wizardPanel.renderNew();

            this.addWizardPanel(tabMenuItem, wizardPanel);
            this.selectPanel(tabMenuItem);
            wizardPanel.reRender();
        }

        private handleOpen(contents:api_model.ContentExtModel[]) {

            for (var i = 0; i < contents.length; i++) {
                var contentModel:api_model.ContentExtModel = contents[i];

                var tabMenuItem = new ContentAppBarTabMenuItem(contentModel.data.displayName);
                var id = this.generateTabId(contentModel.data.name, false);
                var contentItemViewPanel = new app_view.ContentItemViewPanel({
                    showPreviewAction: app_browse.ContentBrowseActions.get().SHOW_PREVIEW,
                    showDetailsAction: app_browse.ContentBrowseActions.get().SHOW_DETAILS
                });

                var contentItem = new api_app_view.ViewItem(contentModel)
                    .setDisplayName(contentModel.data.displayName)
                    .setPath(contentModel.data.name)
                    .setIconUrl(contentModel.data.iconUrl);

                contentItemViewPanel.setItem(contentItem);

                this.addNavigationItem(tabMenuItem, contentItemViewPanel);
                this.selectPanel(tabMenuItem);
            }
        }

        private handleEdit(contents:api_model.ContentExtModel[]) {

            for (var i = 0; i < contents.length; i++) {
                var contentModel:api_model.ContentExtModel = contents[i];

                // Fetch content to edit
                api_remote_content.RemoteContentService.content_get({
                        contentIds: [contentModel.data.id]
                    },
                    (contentResult:api_remote_content.GetResult) => {
                        var contentToEdit:api_remote_content.Content = contentResult.content[0];
                        var tabMenuItem = new ContentAppBarTabMenuItem(contentToEdit.displayName, true);

                        // Fetch content type of content to edit
                        api_remote_contenttype.RemoteContentTypeService.contentType_get({
                                qualifiedNames: [contentToEdit.type],
                                format: "JSON",
                                mixinReferencesToFormItems: true
                            },
                            (contentTypeResult:api_remote_contenttype.GetResult) => {

                                var contentType:api_remote_contenttype.ContentType = contentTypeResult.contentTypes[0];

                                var tabId = this.generateTabId(contentToEdit.name, true);

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
                                            this.selectPanel(tabMenuItem);
                                        });
                                }
                                else {
                                    var contentWizardPanel = new app_wizard.ContentWizardPanel(tabId,
                                        contentType, null);
                                    contentWizardPanel.renderExisting(contentResult);
                                    this.addWizardPanel(tabMenuItem, contentWizardPanel);
                                    this.selectPanel(tabMenuItem);
                                }
                            });

                    });
            }
        }

        private generateTabId(contentName, isEdit) {
            return 'tab-' + ( isEdit ? 'edit-' : 'preview-') + contentName;
        }
    }

}
