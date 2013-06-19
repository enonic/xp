module app {

    export class SpaceAppTabPanelController extends api_ui_tab.TabPanelController {

        private static singleton:SpaceAppTabPanelController;

        private appBarTabMenu:api_appbar.AppBarTabMenu;

        private formDeckPanel:api.AppDeckPanel;

        private extTabByTabIndex = {};

        static init():SpaceAppTabPanelController {
            return singleton = new SpaceAppTabPanelController();
        }

        static get():SpaceAppTabPanelController {
            return singleton;
        }

        constructor() {
            super(null, null);

            this.appBarTabMenu = new app_appbar.SpaceAppBarTabMenu();
            this.formDeckPanel = new api.AppDeckPanel();

            this.appBarTabMenu.addTabSelectedListener(this);
            this.appBarTabMenu.addTabRemoveListener(this);

            app_event.NewSpaceEvent.on((event) => {

                var tabMenuItem = new app_appbar.SpaceAppBarTabMenuItem("New Space");
                this.appBarTabMenu.addTab(tabMenuItem);
                this.appBarTabMenu.selectTab(tabMenuItem);

                var spaceWizardPanel = new app_ui_wizard.SpaceWizardPanel('new-space', 'New Space', true);
                (<any>app_ui.TabPanel.get()).addTab(spaceWizardPanel.ext);

                this.extTabByTabIndex[tabMenuItem.getTabIndex()] = spaceWizardPanel.ext;

                console.log("NewSpaceEvent: Added tab", tabMenuItem);
                console.log(".. at index", tabMenuItem.getTabIndex());
                console.log(".. created spaceWizardPanel", spaceWizardPanel);
                console.log(".. spaceWizardPanel.ext", spaceWizardPanel.ext);
            });

            app_event.OpenSpaceEvent.on((event) => {

                /*var spaceWizardPanel = new app_ui_wizard.SpaceWizardPanel('new-space', 'New Space', true);
                 app_ui.TabPanel.get().addTab(spaceWizardPanel.ext);

                 var tabMenuItem = new app_appbar.SpaceAppBarTabMenuItem("Edit Space");
                 //this.addTab( tabMenuItem );
                 this.appBarTabMenu.addTab(tabMenuItem);
                 this.appBarTabMenu.selectTab(tabMenuItem);*/
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
                            this.appBarTabMenu.addTab(tabMenuItem);


                            var id = this.generateTabId(result.space.name, true);
                            var editing = true;
                            var title = <string> result.space.displayName;
                            //var spaceWizardPanel = new app_ui_wizard.SpaceWizardPanel(id, title, editing, spaceModel);
                            var spaceWizardPanel = new app_wizard.SpaceWizardPanel2(id, title, result.space.iconUrl);


                            //check if preview tab (open action) is open and close it
                            var index = app_ui.TabPanel.get().getExtEl().items.indexOfKey(this.generateTabId(result.space.name, false));
                            if (index >= 0) {
                                app_ui.TabPanel.get().getExtEl().remove(index);
                            }
                            app_ui.TabPanel.get().addTab(spaceWizardPanel.ext, index >= 0 ? index : undefined, undefined);

                            this.extTabByTabIndex[tabMenuItem.getTabIndex()] = spaceWizardPanel.ext;

                            console.log("EditSpaceEvent: Added tab", tabMenuItem);
                            console.log(".. at index", tabMenuItem.getTabIndex());
                            console.log(".. created spaceWizardPanel", spaceWizardPanel);
                            console.log(".. spaceWizardPanel.ext", spaceWizardPanel.ext);

                        } else {
                            console.error("Error", result ? result.error : "Unable to retrieve space.");
                        }

                    });
                }

            });

            api_appbar.ShowAppBrowsePanelEvent.on((event) => {
                app_ui.TabPanel.get().getExtEl().setActiveTab(0);
                this.appBarTabMenu.deselectTab();
            });
        }

        getAppBarTabMenu():api_appbar.AppBarTabMenu {
            return this.appBarTabMenu;
        }

        tabRemove(tab:api_ui_tab.Tab) {
            var activeTab = app_ui.TabPanel.get().getExtEl().getActiveTab();
            var wizardPanel = <app_ui_wizard.SpaceWizardPanel>activeTab.wrapper;

            if (wizardPanel != null && wizardPanel.getWizardDirty()) {
                Ext.Msg.confirm('Close wizard', 'There are unsaved changes, do you want to close it anyway ?',
                    (answer) => {
                        if ('yes' === answer) {
                            this.removeTab(tab);
                        }
                    });
            } else {
                this.removeTab(tab);
            }
        }

        removeTab(tab:api_ui_tab.Tab) {

            console.log("removeTab: ", tab);
            console.log(".. tabIndex", tab.getTabIndex());
            console.log(".. corresponding wizardPanel", this.extTabByTabIndex[tab.getTabIndex()]);

            this.appBarTabMenu.removeTab(tab);
            //var activeTab = app_ui.TabPanel.get().getExtEl().getActiveTab();
            var extTab = this.extTabByTabIndex[tab.getTabIndex()];
            app_ui.TabPanel.get().getExtEl().remove(extTab);
            delete this.extTabByTabIndex[tab.getTabIndex()];
        }

        selectedTab(tab:api_ui_tab.Tab) {
            var extTab = this.extTabByTabIndex[tab.getTabIndex()];

            console.log("selectedTab: ", tab);
            console.log(".. tabIndex", tab.getTabIndex());
            console.log(".. corresponding wizardPanel", extTab);

            app_ui.TabPanel.get().getExtEl().setActiveTab(extTab);
        }

        private generateTabId(spaceName, isEdit) {
            return 'tab-' + ( isEdit ? 'edit-' : 'preview-') + spaceName;
        }

    }
}
