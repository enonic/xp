module api_app {

    export interface BrowseBasedAppPanelConfig {

        appBar:api_app.AppBar;
        browsePanel:api_app_browse.BrowsePanel;
        browsePanelActions:api_ui.Action[];
    }

    export class BrowseAndWizardBasedAppPanel extends api_app.AppPanel {

        private browsePanel:api_app_browse.BrowsePanel;

        private appBarTabMenu:api_app.AppBarTabMenu;

        constructor(config:BrowseBasedAppPanelConfig) {
            super(config.appBar.getTabMenu(), config.browsePanel, config.browsePanelActions);

            this.browsePanel = config.browsePanel;
            this.appBarTabMenu = config.appBar.getTabMenu();

            this.addListener({
                onPanelShown: (event:api_ui.PanelShownEvent) => {
                    if (event.panel === this.browsePanel) {
                        this.browsePanel.refreshGrid();
                    }

                    var previousActions = this.resolveActions(event.previousPanel);
                    api_ui.KeyBindings.unbindKeys(api_ui.Action.getKeyBindings(previousActions));

                    var nextActions = this.resolveActions(event.panel);
                    api_ui.KeyBindings.bindKeys(api_ui.Action.getKeyBindings(nextActions));
                }
            });

            api_ui_tab.TabMenuItemSelectEvent.on((event) => {
                this.appBarTabMenu.hideMenu();
                this.selectPanel(event.getTab());
            });
        }

        getAppBarTabMenu():api_app.AppBarTabMenu {
            return this.appBarTabMenu;
        }

        addWizardPanel(tabMenuItem:AppBarTabMenuItem, wizardPanel:api_app_wizard.WizardPanel, inBackground:boolean = false) {
            super.addNavigationItem(tabMenuItem, wizardPanel, inBackground);

            wizardPanel.addListener({
                onClosed: (wizard) => {
                    this.removePanel(wizard, false);
                }
            });
        }

        canRemovePanel(panel:api_ui.Panel):boolean {
            if (panel instanceof api_app_wizard.WizardPanel) {
                var wizardPanel:api_app_wizard.WizardPanel = <api_app_wizard.WizardPanel>panel;
                return wizardPanel.canClose();
            }
            return true;
        }

        private resolveActions(panel:api_ui.Panel):api_ui.Action[] {

            if (panel instanceof api_app_wizard.WizardPanel || panel instanceof api_app_browse.BrowsePanel) {
                var actionContainer:api_ui.ActionContainer = <any>panel;
                return actionContainer.getActions();
            }
            else {
                return [];
            }
        }
    }
}
