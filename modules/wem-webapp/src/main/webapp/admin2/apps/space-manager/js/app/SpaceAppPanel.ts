module app {

    export class SpaceAppPanel extends api_app.AppPanel {

        private browsePanel:app_browse.SpaceBrowsePanel;

        private appBarTabMenu:api_app.AppBarTabMenu;

        constructor(appBar:SpaceAppBar) {

            this.browsePanel = new app_browse.SpaceBrowsePanel();
            this.appBarTabMenu = appBar.getTabMenu();

            super(appBar.getTabMenu(), this.browsePanel, app_browse.SpaceBrowseActions.ACTIONS);

            this.handleGlobalEvents();
        }

        canRemovePanel(panel:api_ui.Panel):bool {

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
                spaceWizardPanel.reRender();
            });

            app_browse.OpenSpaceEvent.on((event) => {

                var spaces:api_model.SpaceModel[] = event.getModels();
                for (var i = 0; i < spaces.length; i++) {
                    var spaceModel:api_model.SpaceModel = spaces[i];

                    var tabMenuItem = new SpaceAppBarTabMenuItem(spaceModel.data.displayName);
                    var id = this.generateTabId(spaceModel.data.name, false);
                    var spaceItemViewPanel = new app_browse.SpaceItemViewPanel(id);

                    var spaceItem = new api_app_browse.BrowseItem(spaceModel)
                        .setDisplayName(spaceModel.data.displayName)
                        .setPath(spaceModel.data.name)
                        .setIconUrl(spaceModel.data.iconUrl);

                    spaceItemViewPanel.setItem(spaceItem);

                    this.addNavigationItem(tabMenuItem, spaceItemViewPanel);
                    this.selectPanel(tabMenuItem);
                }

            });

            app_browse.EditSpaceEvent.on((event) => {

                var spaces:api_model.SpaceModel[] = event.getModels();
                for (var i = 0; i < spaces.length; i++) {
                    var spaceModel:api_model.SpaceModel = spaces[i];

                    var spaceGetParams:api_remote.RemoteCallSpaceGetParams = {
                        "spaceName": [spaceModel.data.name]
                    };
                    api_remote.RemoteService.space_get(spaceGetParams, (result:api_remote.RemoteCallSpaceGetResult) => {

                        if (result && result.success) {

                            var tabMenuItem = new SpaceAppBarTabMenuItem(result.space.displayName);
                            var id = this.generateTabId(result.space.name, true);
                            var spaceWizardPanel = new app_wizard.SpaceWizardPanel(id);
                            spaceWizardPanel.setPersistedItem(result.space);

                            this.addWizardPanel(tabMenuItem, spaceWizardPanel);
                            this.selectPanel(tabMenuItem);
                        } else {
                            console.error("Error", result ? result.error : "Unable to retrieve space.");
                        }
                    });
                }
            });

            api_ui_tab.TabMenuItemSelectEvent.on((event) => {
                this.appBarTabMenu.hideMenu();
                this.selectPanel(event.getTab());
            });

            api_ui_tab.TabMenuItemCloseEvent.on((event) => {
                var tabIndex = event.getTab().getIndex();
                var panel = this.getPanel(tabIndex);
                new app_browse.CloseSpaceEvent(panel, true).fire();
            });

            app_browse.CloseSpaceEvent.on((event) => {
                this.removePanel(event.getPanel(), event.isCheckCanRemovePanel());
            });
        }

        private generateTabId(spaceName, isEdit) {
            return 'tab-' + ( isEdit ? 'edit-' : 'preview-') + spaceName;
        }
    }

}
