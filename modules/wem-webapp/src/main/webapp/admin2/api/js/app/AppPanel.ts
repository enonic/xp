module api_app{

    export class AppPanel extends api_ui.NavigatedDeckPanel {

        private tabMenu:AppBarTabMenu;

        private homePanel:api_ui.Panel;

        private homePanelActions:api_ui.Action[];

        constructor(tabNavigator:AppBarTabMenu, homePanel:api_ui.Panel, homePanelActions:api_ui.Action[]) {
            super(tabNavigator);

            this.homePanel = homePanel;
            this.homePanelActions = homePanelActions;
            var homePanelMenuItem = new AppBarTabMenuItem("home");
            homePanelMenuItem.setVisible(false);
            homePanelMenuItem.setRemovable(false);
            this.addNavigationItem(homePanelMenuItem, this.homePanel);
            this.showPanel(0);
        }

        addWizardPanel(item:AppBarTabMenuItem, wizardPanel:api_app_wizard.WizardPanel) {
            super.addNavigationItem(item, wizardPanel);

            // TODO: Register as listener for changes to WizardPanel.displayName and update label of AppBarTabMenuItem
        }

        showHomePanel() {
            this.showPanel(0);
        }

        showPanel(index:number) {
            super.showPanel(index);

            if (this.isHomePanel(index)) {
                api_ui.KeyBindings.bindKeys(api_ui.Action.getKeyBindings(this.homePanelActions));
            }
            else {
                api_ui.KeyBindings.unbindKeys(api_ui.Action.getKeyBindings(this.homePanelActions));
            }
        }

        removePanelByIndex(index:number):api_ui.Panel {
            var panelRemoved = super.removePanelByIndex(index);
            if (this.getSize() == 0) {
                this.showHomePanel();
            }
            return panelRemoved;
        }

        private isHomePanel(index:number) {
            return index == 0;
        }
    }
}
