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

        tabRemove(tab:api_ui_tab.Tab):bool {

            if (this.hasUnsavedChanges()) {
                return false;
            }
            else {
                return super.tabRemove(tab);
            }
        }

        private hasUnsavedChanges():bool {
            /*TODO: if (wizardPanel != null && wizardPanel.getWizardDirty()) {
             Ext.Msg.confirm('Close wizard', 'There are unsaved changes, do you want to close it anyway ?',
             (answer) => {
             if ('yes' === answer) {
             this.removeTab(tab);
             } else {
             return false;
             }
             });
             } else {
             this.removeTab(tab);
             }*/
            return false;
        }

        private handleGlobalEvents() {

            api_app.ShowAppBrowsePanelEvent.on((event) => {
                this.showHomePanel();
                this.appBarTabMenu.deselectTab();
            });

            app_event.NewSpaceEvent.on((event) => {

                var tabMenuItem = new SpaceAppBarTabMenuItem("New Space");
                var spaceWizardPanel = new app_wizard.SpaceWizardPanel2('new-space', 'New Space');

                this.addTab(tabMenuItem, spaceWizardPanel);
                this.showTab(tabMenuItem);
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

                            var tabMenuItem = new SpaceAppBarTabMenuItem(result.space.displayName);
                            var id = this.generateTabId(result.space.name, true);
                            var spaceWizardPanel = new app_wizard.SpaceWizardPanel2(id, result.space.displayName, result.space.iconUrl);

                            this.addTab(tabMenuItem, spaceWizardPanel);
                            this.showTab(tabMenuItem);
                        } else {
                            console.error("Error", result ? result.error : "Unable to retrieve space.");
                        }

                    });
                }
            });
        }

        private generateTabId(spaceName, isEdit) {
            return 'tab-' + ( isEdit ? 'edit-' : 'preview-') + spaceName;
        }
    }

}
