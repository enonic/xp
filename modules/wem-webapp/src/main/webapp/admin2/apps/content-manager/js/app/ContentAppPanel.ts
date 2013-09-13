module app {

    export class ContentAppPanel extends api_app.BrowseAndWizardBasedAppPanel {

        constructor(appBar:api_app.AppBar) {

            var browsePanel = new app_browse.ContentBrowsePanel();

            super({
                appBar: appBar,
                browsePanel: browsePanel,
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
                this.getAppBarTabMenu().deselectNavigationItem();
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

        addWizardPanel(tabMenuItem:api_app.AppBarTabMenuItem, wizardPanel:api_app_wizard.WizardPanel) {
            super.addWizardPanel(tabMenuItem, wizardPanel);

            wizardPanel.getHeader().addListener(
                {
                    onPropertyChanged: (event:api_app_wizard.WizardHeaderPropertyChangedEvent) => {
                        if (event.property == "displayName") {
                            tabMenuItem.setLabel(event.newValue);
                        }
                    }
                });
        }

        private handleNew(contentType:api_remote_contenttype.ContentType, parentContent:api_content.Content) {

            var tabId = this.generateTabId();
            var tabMenuItem = this.getAppBarTabMenu().getNavigationItemById(tabId);

            if (tabMenuItem != null) {
                this.selectPanel(tabMenuItem);

            } else {

                var contentTypeRequest = new api_schema_content.GetContentTypeByQualifiedNameRequest(contentType.qualifiedName).send();
                jQuery.
                    when(contentTypeRequest).
                    then((contentTypeResponse:api_rest.JsonResponse) => {

                        var newContentType = new api_schema_content.ContentType(contentTypeResponse.getJson());

                        tabMenuItem = new api_app.AppBarTabMenuItem(app_wizard.ContentWizardPanel.NEW_WIZARD_HEADER, tabId);
                        var wizardPanel = new app_wizard.ContentWizardPanel(tabId, newContentType, parentContent);
                        wizardPanel.renderNew();

                        this.addWizardPanel(tabMenuItem, wizardPanel);

                        wizardPanel.reRender();
                    });
            }
        }

        private handleOpen(contents:api_model.ContentExtModel[]) {

            contents.forEach((contentModel:api_model.ContentExtModel) => {

                var tabId = this.generateTabId(contentModel.data.path, false);
                var tabMenuItem = this.getAppBarTabMenu().getNavigationItemById(tabId);

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

                    this.addNavigablePanelToFront(tabMenuItem, contentItemViewPanel);
                }
            });
        }

        private handleEdit(contents:api_model.ContentExtModel[]) {

            contents.forEach((contentModel:api_model.ContentExtModel) => {

                var tabId = this.generateTabId(contentModel.data.path, true);
                var tabMenuItem = this.getAppBarTabMenu().getNavigationItemById(tabId);

                if (tabMenuItem != null) {
                    this.selectPanel(tabMenuItem);

                } else {


                    var getContentByIdPromise = new api_content.GetContentByIdRequest(contentModel.data.id).send();
                    var getContentTypeByQualifiedNamePromise = new api_schema_content.GetContentTypeByQualifiedNameRequest(contentModel.data.type).send();
                    jQuery.
                        when(getContentByIdPromise, getContentTypeByQualifiedNamePromise).
                        then((contentResponse:api_rest.JsonResponse, contentTypeResponse:api_rest.JsonResponse) => {

                            var contentToEdit:api_content.Content = new api_content.Content(<api_content_json.ContentJson>contentResponse.getJson().contents[0]);
                            var contentType:api_schema_content.ContentType = new api_schema_content.ContentType(<api_schema_content_json.ContentTypeJson>contentTypeResponse.getJson());
                            tabMenuItem = new api_app.AppBarTabMenuItem(contentToEdit.getDisplayName(), tabId, true);

                            if (contentToEdit.getPath().hasParent()) {
                                new api_content.GetContentByPathRequest(contentToEdit.getPath().getParentPath()).send().
                                    done((parentContentResponse:api_rest.JsonResponse) => {
                                        var parentContent:api_content.Content = new api_content.Content(<api_content_json.ContentJson>parentContentResponse.getJson().contents[0]);

                                        var contentWizardPanel = new app_wizard.ContentWizardPanel(tabId, contentType, parentContent);

                                        contentWizardPanel.setPersistedItem(contentToEdit);
                                        this.addWizardPanel(tabMenuItem, contentWizardPanel);

                                    });
                            }
                            else {
                                var contentWizardPanel = new app_wizard.ContentWizardPanel(tabId, contentType, null);
                                contentWizardPanel.setPersistedItem(contentToEdit);
                                this.addWizardPanel(tabMenuItem, contentWizardPanel);
                            }
                        });

                }
            });
        }

        private generateTabId(contentName?:string, isEdit:boolean = false) {
            return contentName ? ( isEdit ? 'edit-' : 'view-') + contentName : 'new-';
        }
    }

}
