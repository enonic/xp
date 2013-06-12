module api_ui_tab {

    export class TabPanelController implements TabRemovedListener, TabSelectedListener {

        private tabNavigator:TabNavigator;

        private deckPanel:api_ui.DeckPanel;

        private tabs:Tab[] = [];

        private deckIndexByTabIndex = {};

        constructor(tabNavigator:TabNavigator, deckPanel:api_ui.DeckPanel) {

            this.tabNavigator = tabNavigator;
            this.deckPanel = deckPanel;

            this.tabNavigator.addTabSelectedListener(this);
            this.tabNavigator.addTabRemovedListener(this);
        }

        addPanel(panel:api_ui.Panel, tab:api_ui_tab.Tab) {

            var tabIndex:number = this.tabs.length;
            tab.setTabIndex(tabIndex);
            this.tabNavigator.addTab(tab);

            this.deckIndexByTabIndex[tabIndex] = this.deckPanel.addPanel(panel);
            this.tabs.push(tab);
        }

        removedTab(tab:Tab) {
            var deckIndex = this.deckIndexByTabIndex[tab.getTabIndex()];
            this.deckPanel.removePanel(deckIndex);
        }

        selectedTab(tab:Tab) {
            var deckIndex = this.deckIndexByTabIndex[tab.getTabIndex()];
            this.deckPanel.showPanel(deckIndex);
        }
    }


}
