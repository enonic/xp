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
        }

        getAppBarTabMenu():api_app.AppBarTabMenu {
            return this.appBarTabMenu;
        }

        addViewPanel(tabMenuItem:AppBarTabMenuItem, viewPanel:api_app_view.ItemViewPanel) {
            super.addNavigablePanelToFront(tabMenuItem, viewPanel);

            tabMenuItem.addListener({
                onClose: (tab: AppBarTabMenuItem) => {
                    viewPanel.close();
                }
            });

            viewPanel.addListener({
                onClosed: (view) => {
                    this.removePanel(view, false);
                }
            });
        }

        addWizardPanel(tabMenuItem:AppBarTabMenuItem, wizardPanel:api_app_wizard.WizardPanel) {
            super.addNavigablePanelToFront(tabMenuItem, wizardPanel);

            tabMenuItem.addListener({
                onClose: (tab: AppBarTabMenuItem) => {
                    wizardPanel.close();
                }
            });

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
