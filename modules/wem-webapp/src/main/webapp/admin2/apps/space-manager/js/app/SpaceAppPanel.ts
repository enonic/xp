module app {

    export class SpaceAppPanel extends api_app.AppPanel {

        private appBrowsePanel:app_browse.SpaceAppBrowsePanel;

        private appBarTabMenu:api_app.AppBarTabMenu;

        constructor(appBar:SpaceAppBar) {

            this.appBrowsePanel = new app_browse.SpaceAppBrowsePanel();
            this.appBarTabMenu = appBar.getTabMenu();

            super(appBar.getTabMenu(), this.appBrowsePanel, app_browse.SpaceBrowseActions.ACTIONS);

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

            app_browse.NewSpaceEvent.on((event) => {

                var tabMenuItem = new SpaceAppBarTabMenuItem("New Space");
                var spaceWizardPanel = new app_wizard.SpaceWizardPanel('new-space');
                this.addWizardPanel(tabMenuItem, spaceWizardPanel);
                this.selectPanel(tabMenuItem);
            });

            app_browse.OpenSpaceEvent.on((event) => {

                // TODO: Open detailpanel in "full screen"
            });

            app_browse.EditSpaceEvent.on((event) => {

                var spaces:api_model.SpaceModel[] = event.getModels();
                for (var i = 0; i < spaces.length; i++) {
                    var spaceModel:api_model.SpaceModel = spaces[i];

                    var spaceGetParams:api_remote.RemoteCallSpaceGetParams = {
                        "spaceName": [spaceModel.data.name]
                    };
                    api_remote.RemoteService.space_get(spaceGetParams, (result:api_remote.RemoteCallSpaceGetResult) => {

                        if (result) {

                            var tabMenuItem = new SpaceAppBarTabMenuItem(result.space.displayName);
                            var id = this.generateTabId(result.space.name, true);
                            var spaceWizardPanel = new app_wizard.SpaceWizardPanel(id);
                            spaceWizardPanel.setData(result);

                            this.addWizardPanel(tabMenuItem, spaceWizardPanel);
                            this.selectPanel(tabMenuItem);
                        } else {
                            console.error("Error", result ? result.error : "Unable to retrieve space.");
                        }
                    });
                }
            });

            app_wizard.CloseSpaceWizardPanelEvent.on((event) => {
                this.removePanel(event.getPanel(), event.isCheckCanRemovePanel());
            });
        }

        private generateTabId(spaceName, isEdit) {
            return 'tab-' + ( isEdit ? 'edit-' : 'preview-') + spaceName;
        }
    }

}
