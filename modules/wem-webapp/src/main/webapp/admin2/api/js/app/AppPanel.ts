module api_app{

    export class AppPanel extends api_ui.NavigatedDeckPanel {

        private homePanel:api_ui.Panel;

        private homePanelActions:api_ui.Action[];

        constructor(tabNavigator:AppBarTabMenu, homePanel:api_ui.Panel, homePanelActions:api_ui.Action[]) {
            super(tabNavigator);

            this.homePanel = homePanel;
            this.homePanelActions = homePanelActions;
            var homePanelMenuItem = new AppBarTabMenuItem("home", "home");
            homePanelMenuItem.setVisible(false);
            homePanelMenuItem.setRemovable(false);
            this.addNavigablePanelToFront(homePanelMenuItem, this.homePanel);

            this.addListener({
                onPanelShown: (event:api_ui.PanelShownEvent) => {
                    if (!this.isHomePanel(event.index)) {
                        // do panel afterRender to calculate offsets for each but home panel cuz they were created hidden
                        event.panel.afterRender();
                    }
                }
            });
        }

        showHomePanel() {
            this.showPanel(0);
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
