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
            super(config.appBar.getTabMenu(), config.browsePanel, config.browsePanelActions)

            this.browsePanel = config.browsePanel;
            this.appBarTabMenu = config.appBar.getTabMenu();

            this.addPanelShownChangedListener((panel:api_ui.Panel, index:number) => {
                if (panel === this.browsePanel) {
                    this.browsePanel.refreshGrid();
                }
            });

            api_app_wizard.CloseWizardPanelEvent.on((event) => {
                this.removePanel(event.getWizardPanel(), event.isCheckCanRemovePanel());
            });

            api_ui_tab.TabMenuItemSelectEvent.on((event) => {
                this.appBarTabMenu.hideMenu();
                this.selectPanel(event.getTab());
            });
        }

        addWizardPanel(tabMenuItem:AppBarTabMenuItem, wizardPanel:api_app_wizard.WizardPanel) {
            super.addNavigationItem(tabMenuItem, wizardPanel);

            api_app_wizard.DisplayNameChangedEvent.on(function (event) {
                if (event.getWizardPanel() == wizardPanel) {
                    tabMenuItem.setLabel(wizardPanel.getDisplayName());
                }
            });

            api_app_wizard.NameChangedEvent.on(function (event) {
                if (event.getWizardPanel() == wizardPanel) {
                    // update something when name changed
                }
            });
        }

        canRemovePanel(panel:api_ui.Panel):bool {
            if (panel instanceof api_app_wizard.WizardPanel) {
                var wizardPanel:api_app_wizard.WizardPanel = <api_app_wizard.WizardPanel>panel;
                return wizardPanel.canClose();
            }
            return true;
        }
    }
}
