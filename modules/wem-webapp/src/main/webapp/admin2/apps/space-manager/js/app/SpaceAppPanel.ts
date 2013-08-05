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

        private handleGlobalEvents() {

            api_app.ShowAppBrowsePanelEvent.on((event) => {
                this.showHomePanel();
                this.appBarTabMenu.deselectNavigationItem();
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

            app_browse.NewSpaceEvent.on((event) => {

                var tabMenuItem = new SpaceAppBarTabMenuItem("New Space");
                var spaceWizardPanel = new app_wizard.SpaceWizardPanel('new-space');
                this.addWizardPanel(tabMenuItem, spaceWizardPanel);
                this.selectPanel(tabMenuItem);
                spaceWizardPanel.reRender();
            });

            app_browse.OpenSpaceEvent.on((event) => {

                var spaces:api_model.SpaceExtModel[] = event.getModels();
                for (var i = 0; i < spaces.length; i++) {
                    var spaceModel:api_model.SpaceExtModel = spaces[i];

                    var tabMenuItem = new SpaceAppBarTabMenuItem(spaceModel.data.displayName);
                    var id = this.generateTabId(spaceModel.data.name, false);
                    var spaceItemViewPanel = new app_view.SpaceItemViewPanel(id);

                    var spaceItem = new api_app_view.ViewItem(spaceModel)
                        .setDisplayName(spaceModel.data.displayName)
                        .setPath(spaceModel.data.name)
                        .setIconUrl(spaceModel.data.iconUrl);

                    spaceItemViewPanel.setItem(spaceItem);

                    this.addNavigationItem(tabMenuItem, spaceItemViewPanel);
                    this.selectPanel(tabMenuItem);
                }

            });

            app_browse.EditSpaceEvent.on((event) => {

                var spaces:api_model.SpaceExtModel[] = event.getModels();
                for (var i = 0; i < spaces.length; i++) {
                    var spaceModel:api_model.SpaceExtModel = spaces[i];

                    var spaceGetParams:api_remote_space.GetParams = {
                        "spaceNames": [spaceModel.data.name]
                    };
                    api_remote.RemoteSpaceService.space_get(spaceGetParams, (result:api_remote_space.GetResult) => {

                        if (result && result.success) {
                            var space = result.spaces[0];
                            var tabMenuItem = new SpaceAppBarTabMenuItem(space.displayName, true);
                            var id = this.generateTabId(space.name, true);
                            var spaceWizardPanel = new app_wizard.SpaceWizardPanel(id);
                            spaceWizardPanel.setPersistedItem(space);

                            this.addWizardPanel(tabMenuItem, spaceWizardPanel);
                            this.selectPanel(tabMenuItem);
                        } else {
                            console.error("Error", result ? result.error : "Unable to retrieve space.");
                        }
                    });
                }
            });

            app_browse.CloseSpaceEvent.on((event) => {
                this.removePanel(event.getPanel(), event.isCheckCanRemovePanel());
            });

            api_app_wizard.CloseWizardPanelEvent.on((event) => {
                this.removePanel(event.getWizardPanel(), event.isCheckCanRemovePanel());
            });
        }

        private generateTabId(spaceName, isEdit) {
            return 'tab-' + ( isEdit ? 'edit-' : 'preview-') + spaceName;
        }
    }

}
