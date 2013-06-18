module api_ui_tab {

    export class TabPanelController implements api_ui_tab.TabRemoveListener, TabSelectedListener {

        private tabNavigator:TabNavigator;

        private deckPanel:api_ui.DeckPanel;

        private deckIndexByTabIndex = {};

        constructor(tabNavigator:TabNavigator, deckPanel:api_ui.DeckPanel) {

            this.tabNavigator = tabNavigator;
            this.deckPanel = deckPanel;

            //this.tabNavigator.addTabSelectedListener(this);
            //this.tabNavigator.addTabRemoveListener(this);
        }

        addPanel(panel:api_ui.Panel, tab:api_ui_tab.Tab) {

            var tabIndex:number = this.tabNavigator.getSize();
            tab.setTabIndex(tabIndex);
            this.tabNavigator.addTab(tab);

            if (this.deckPanel != null) {
                this.deckIndexByTabIndex[tabIndex] = this.deckPanel.addPanel(panel);
            }
        }

        tabRemove(tab:Tab) {
            var deckIndex = this.deckIndexByTabIndex[tab.getTabIndex()];

            if (this.deckPanel != null) {
                this.deckPanel.removePanel(deckIndex);
            }
        }

        selectedTab(tab:api_ui_tab.Tab) {
            var deckIndex = this.deckIndexByTabIndex[tab.getTabIndex()];

            if (this.deckPanel != null) {
                this.deckPanel.showPanel(deckIndex);
            }
        }
    }
}
