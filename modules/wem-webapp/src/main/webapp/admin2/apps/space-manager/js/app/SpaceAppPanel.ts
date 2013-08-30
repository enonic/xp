module app {

    export class SpaceAppPanel extends api_app.BrowseAndWizardBasedAppPanel {


        constructor(appBar:api_app.AppBar) {

            var browsePanel = new app_browse.SpaceBrowsePanel();

            super({
                appBar: appBar,
                browsePanel: browsePanel,
                browsePanelActions: app_browse.SpaceBrowseActions.get().getAllActions()
            });

            this.handleGlobalEvents();
        }

        addWizardPanel(tabMenuItem:api_app.AppBarTabMenuItem, wizardPanel:api_app_wizard.WizardPanel, inBackground:boolean = false) {
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

        private handleGlobalEvents() {

            api_app.ShowAppBrowsePanelEvent.on((event) => {
                this.showHomePanel();
                this.getAppBarTabMenu().deselectNavigationItem();
            });

            api_ui_tab.TabMenuItemCloseEvent.on((event) => {
                var tabIndex = event.getTab().getIndex();
                var panel = this.getPanel(tabIndex);
                new app_browse.CloseSpaceEvent(panel, true).fire();
            });

            app_browse.NewSpaceEvent.on((event) => {

                var tabId = this.generateTabId();
                var tabMenuItem = this.getAppBarTabMenu().getNavigationItemById(tabId);

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
                    var tabMenuItem = this.getAppBarTabMenu().getNavigationItemById(tabId);

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
                    var tabMenuItem = this.getAppBarTabMenu().getNavigationItemById(tabId);

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

        private generateTabId(spaceName?:string, isEdit:boolean = false) {
            return spaceName ? ( isEdit ? 'edit-' : 'view-') + spaceName : 'new-';
        }
    }

}
