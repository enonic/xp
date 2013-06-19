module app {

    export class SpaceAppPanel extends api.AppPanel {

        private appBrowsePanel:SpaceAppBrowsePanel;

        private appDeckPanel:api.AppDeckPanel;

        constructor(appBar:app_appbar.SpaceAppBar) {

            this.appBrowsePanel = new SpaceAppBrowsePanel();
            this.appDeckPanel = new api.AppDeckPanel(appBar.getTabMenu());
            appBar.getTabMenu().addTabSelectedListener((tab:api_ui_tab.Tab) => {
                this.showDeckPanel();
            });

            super(this.appBrowsePanel, this.appDeckPanel);

            api_appbar.ShowAppBrowsePanelEvent.on((event) => {
                this.showBrowsePanel();
                appBar.getTabMenu().deselectTab();
            });

            app_event.NewSpaceEvent.on((event) => {

                var tabMenuItem = new app_appbar.SpaceAppBarTabMenuItem("New Space");
                var spaceWizardPanel = new app_wizard.SpaceWizardPanel2('new-space', 'New Space', "");

                this.appDeckPanel.addTab(tabMenuItem, spaceWizardPanel);
                this.appDeckPanel.showTab(tabMenuItem);
                this.showDeckPanel();
            });

            app_event.OpenSpaceEvent.on((event) => {

                // TODO: Open detailpanel in "full screen"
            });

            app_event.EditSpaceEvent.on((event) => {

                var spaces:api_model.SpaceModel[] = event.getModels();
                for (var i = 0; i < spaces.length; i++) {
                    var spaceModel:api_model.SpaceModel = spaces[i];

                    var spaceGetParams:api_remote.RemoteCallSpaceGetParams = {
                        "spaceName": [spaceModel.data.name]
                    };
                    api_remote.RemoteService.space_get(spaceGetParams, (result:api_remote.RemoteCallSpaceGetResult) => {

                        if (result) {

                            var tabMenuItem = new app_appbar.SpaceAppBarTabMenuItem(result.space.displayName);
                            var id = this.generateTabId(result.space.name, true);
                            var spaceWizardPanel = new app_wizard.SpaceWizardPanel2(id, result.space.displayName, result.space.iconUrl);

                            this.appDeckPanel.addTab(tabMenuItem, spaceWizardPanel);
                            this.appDeckPanel.showTab(tabMenuItem);
                            this.showDeckPanel();
                        } else {
                            console.error("Error", result ? result.error : "Unable to retrieve space.");
                        }

                    });
                }


            });
        }

        init() {
            this.appBrowsePanel.init();
        }


        private generateTabId(spaceName, isEdit) {
            return 'tab-' + ( isEdit ? 'edit-' : 'preview-') + spaceName;
        }
    }

}
