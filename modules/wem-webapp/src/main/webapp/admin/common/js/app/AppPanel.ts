module api.app {

    export class AppPanel extends api.ui.NavigatedDeckPanel {

        private homePanel: api.ui.Panel;

        constructor(tabNavigator: AppBarTabMenu, homePanel: api.ui.Panel) {
            super(tabNavigator);

            this.homePanel = homePanel;
            var homePanelMenuItem = new AppBarTabMenuItem("home", new AppBarTabId("hidden", "____home"));
            homePanelMenuItem.setVisibleInMenu(false);
            homePanelMenuItem.setRemovable(false);
            this.addNavigablePanelToFront(homePanelMenuItem, this.homePanel);

            this.onPanelShown((event: api.ui.PanelShownEvent) => {
                if (!this.isHomePanel(event.getIndex())) {
                    // do panel afterRender to calculate offsets for each but home panel cuz they were created hidden
                    //event.getPanel().afterRender();
                }
            });
        }

        showHomePanel() {
            if (this.getPanelShownIndex() != 0) {
                this.showPanelByIndex(0);
            }
        }

        removePanelByIndex(index: number): api.ui.Panel {
            var panelRemoved = super.removePanelByIndex(index);
            if (this.getSize() == 0) {
                this.showHomePanel();
            }
            return panelRemoved;
        }

        private isHomePanel(index: number) {
            return index == 0;
        }
    }
}
