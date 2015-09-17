module api.app.view {

    export class MultiItemStatisticsPanel<M extends api.Equitable> extends ItemStatisticsPanel<M> {

        private tabMenu: api.ui.tab.TabMenu;

        private deckPanel: api.ui.panel.NavigatedDeckPanel;

        constructor(className?: string) {
            super(className);

            this.tabMenu = new api.ui.tab.TabMenu();
            this.tabMenu.hide();
            this.appendChild(this.tabMenu);


            this.deckPanel = new api.ui.panel.NavigatedDeckPanel(this.tabMenu);
            this.deckPanel.setDoOffset(false);
            this.appendChild(this.deckPanel);
        }


        getTabMenu(): api.ui.tab.TabMenu {
            return this.tabMenu;
        }

        getDeckPanel(): api.ui.panel.NavigatedDeckPanel {
            return this.deckPanel;
        }

        addNavigablePanel(tab: api.ui.tab.TabMenuItem, panel: api.ui.panel.Panel, select?: boolean) {
            this.tabMenu.show();
            this.deckPanel.addNavigablePanel(tab, panel, select);
        }

        showPanel(index: number) {
            this.tabMenu.selectNavigationItem(index);
        }
    }
}
