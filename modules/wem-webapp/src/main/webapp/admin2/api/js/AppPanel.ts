module api{

    export class AppPanel extends api_ui_tab.TabbedDeckPanel {

        private homePanel:api_ui.Panel;

        private homePanelActions:api_ui.Action[];

        constructor(tabNavigator:api_ui_tab.TabNavigator, homePanel:api_ui.Panel, homePanelActions:api_ui.Action[]) {
            super(tabNavigator);

            this.homePanel = homePanel;
            this.homePanelActions = homePanelActions;
            var homePanelMenuItem = new api_appbar.AppBarTabMenuItem("home");
            homePanelMenuItem.setVisible(false);
            homePanelMenuItem.setRemovable(false);
            this.addTab(homePanelMenuItem, this.homePanel);
            this.showPanel(0);
        }

        showHomePanel() {
            this.showPanel(0);
        }

        showPanel(index:number) {
            super.showPanel(index);

            if (this.isHomePanel(index)) {
                api_ui.Action.activateShortcuts(this.homePanelActions);
            }
            else {
                api_ui.Action.deactivateShortcuts(this.homePanelActions);
            }
        }

        removePanel(index:number):api_ui.Panel {
            var panelRemoved = super.removePanel(index);
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
