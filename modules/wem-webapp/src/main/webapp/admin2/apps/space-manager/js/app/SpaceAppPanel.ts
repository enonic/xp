module app {

    export class SpaceAppPanel extends api_app.BrowseAndWizardBasedAppPanel {

        private browsePanel:app_browse.SpaceBrowsePanel;

        private appBarTabMenu:api_app.AppBarTabMenu;

        constructor(appBar:api_app.AppBar) {

            this.browsePanel = new app_browse.SpaceBrowsePanel();
            this.appBarTabMenu = appBar.getTabMenu();

            super({
                appBar: appBar,
                browsePanel: this.browsePanel,
                browsePanelActions: app_browse.SpaceBrowseActions.get().getAllActions()
            });

            this.handleGlobalEvents();
        }

        private handleGlobalEvents() {

            api_app.ShowAppBrowsePanelEvent.on((event) => {
                this.showHomePanel();
                this.appBarTabMenu.deselectNavigationItem();
            });

            api_ui_tab.TabMenuItemCloseEvent.on((event) => {
                var tabIndex = event.getTab().getIndex();
                var panel = this.getPanel(tabIndex);
                new app_browse.CloseSpaceEvent(panel, true).fire();
            });

            app_browse.NewSpaceEvent.on((event) => {

                var tabId = this.generateTabId();
                var tabMenuItem = this.appBarTabMenu.getNavigationItemById(tabId);

                if (tabMenuItem != null) {
                    this.selectPanel(tabMenuItem);

                } else {
                    tabMenuItem = new api_app.AppBarTabMenuItem("New Space", tabId);
                    var spaceWizardPanel = new app_wizard.SpaceWizardPanel(tabId);
                    this.addWizardPanel(tabMenuItem, spaceWizardPanel);
                    spaceWizardPanel.reRender();
                }
            });

            app_browse.OpenSpaceEvent.on((event) => {

                event.getModels().forEach((spaceModel:api_model.SpaceExtModel) => {

                    var tabId = this.generateTabId(spaceModel.data.name, false);
                    var tabMenuItem = this.appBarTabMenu.getNavigationItemById(tabId);

                    if (tabMenuItem != null) {
                        this.selectPanel(tabMenuItem);

                    } else {
                        tabMenuItem = new api_app.AppBarTabMenuItem(spaceModel.data.displayName, tabId);
                        var spaceItemViewPanel = new app_view.SpaceItemViewPanel();
                        var spaceItem = new api_app_view.ViewItem(spaceModel)
                            .setDisplayName(spaceModel.data.displayName)
                            .setPath(spaceModel.data.name)
                            .setIconUrl(spaceModel.data.iconUrl);

                        spaceItemViewPanel.setItem(spaceItem);

                        this.addNavigationItem(tabMenuItem, spaceItemViewPanel);
                    }
                });
            });

            app_browse.EditSpaceEvent.on((event) => {

                event.getModels().forEach((spaceModel:api_model.SpaceExtModel) => {

                    var tabId = this.generateTabId(spaceModel.data.name, true);
                    var tabMenuItem = this.appBarTabMenu.getNavigationItemById(tabId);

                    if (tabMenuItem != null) {
                        this.selectPanel(tabMenuItem);

                    } else {
                        var spaceGetParams:api_remote_space.GetParams = {
                            "spaceNames": [spaceModel.data.name]
                        };
                        api_remote_space.RemoteSpaceService.space_get(spaceGetParams, (result:api_remote_space.GetResult) => {
                            var space = result.spaces[0];

                            tabMenuItem = new api_app.AppBarTabMenuItem(space.displayName, tabId, true);
                            var spaceWizardPanel = new app_wizard.SpaceWizardPanel(tabId);
                            spaceWizardPanel.setPersistedItem(space);

                            this.addWizardPanel(tabMenuItem, spaceWizardPanel);
                        });
                    }
                });
            });

            app_browse.CloseSpaceEvent.on((event) => {
                this.removePanel(event.getPanel(), event.isCheckCanRemovePanel());
            });
        }

        private generateTabId(spaceName?:string, isEdit?:bool = false) {
            return spaceName ? ( isEdit ? 'edit-' : 'view-') + spaceName : 'new-';
        }
    }

}
