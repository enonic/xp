module app {

    export class ContentAppPanel extends api_app.AppPanel {

        private appBrowsePanel:app_browse.ContentAppBrowsePanel;

        private appBarTabMenu:api_app.AppBarTabMenu;

        constructor(appBar:ContentAppBar) {

            this.appBrowsePanel = new app_browse.ContentAppBrowsePanel();
            this.appBarTabMenu = appBar.getTabMenu();

            super(appBar.getTabMenu(), this.appBrowsePanel, app_browse.ContentBrowseActions.ACTIONS);

            this.handleGlobalEvents();
        }

        init() {
            this.appBrowsePanel.init();
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

                // TODO: uncomment after wizard panel is ready
                /*
                var tabMenuItem = new ContentAppBarTabMenuItem("New Content");
                var contentWizardPanel = new app_wizard.ContentWizardPanel('new-content');
                this.addNavigationItem(tabMenuItem, contentWizardPanel);
                this.selectPanel(tabMenuItem);
                */
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

                            this.addNavigationItem(tabMenuItem, conetntWizardPanel);
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
