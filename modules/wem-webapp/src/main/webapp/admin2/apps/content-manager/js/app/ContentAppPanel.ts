module app {

    export class ContentAppPanel extends api_app.AppPanel {

        private browsePanel:app_browse.ContentBrowsePanel;

        private appBarTabMenu:api_app.AppBarTabMenu;

        constructor(appBar:ContentAppBar) {

            this.browsePanel = new app_browse.ContentBrowsePanel();
            this.appBarTabMenu = appBar.getTabMenu();

            super(appBar.getTabMenu(), this.browsePanel, app_browse.ContentBrowseActions.ACTIONS);

            this.handleGlobalEvents();
        }

        init() {
            this.browsePanel.init();
        }

        canRemovePanel(panel:api_ui.Panel, index:number):bool {

            if (panel instanceof api_app_wizard.WizardPanel) {
                var wizardPanel:api_app_wizard.WizardPanel = <api_app_wizard.WizardPanel>panel;
                return wizardPanel.canClose();
            }
            return true;
        }

        private handleGlobalEvents() {

            api_app.ShowAppBrowsePanelEvent.on((event) => {
                this.showHomePanel();
                this.appBarTabMenu.deselectNavigationItem();
            });

            app_browse.NewContentEvent.on((event) => {
                // Done in Controller.js
            });

            app_browse.OpenContentEvent.on((event) => {

                // TODO: Open detailpanel in "full screen"
            });

            app_browse.EditContentEvent.on((event) => {

               // TODO: uncomment after wizard panel is ready
               /*
               var contents:api_model.ContentModel[] = event.getModels();
                for (var i = 0; i < contents.length; i++) {
                    var contentModel:api_model.ContentModel = contents[i];

                    var contentGetParams:api_remote.RemoteCallContentGetParams = {
                        "contentName": [contentModel.data.name]
                    };
                    api_remote.RemoteService.content_get(contentGetParams, (result:api_remote.RemoteCallContentGetResult) => {

                        if (result) {

                            var tabMenuItem = new ContentAppBarTabMenuItem(result.content.displayName);
                            var id = this.generateTabId(result.content.name, true);
                            var conetntWizardPanel = new app_wizard.ContentWizardPanel(id);
                            conetntWizardPanel.setData(result);

                            this.addWizardPanel(tabMenuItem, conetntWizardPanel);
                            this.selectPanel(tabMenuItem);
                        } else {
                            console.error("Error", result ? result.error : "Unable to retrieve content.");
                        }
                    });
                }
                */
            });

        }

        private generateTabId(contentName, isEdit) {
            return 'tab-' + ( isEdit ? 'edit-' : 'preview-') + contentName;
        }
    }

}
