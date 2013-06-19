module api_ui_tab {

    /**
     * A DeckPanel with Tab-s.
     */
    export class TabbedDeckPanel extends api_ui.DeckPanel {

        private navigator:TabNavigator;

        constructor(navigator:TabNavigator) {
            super();
            this.navigator = navigator;

            this.navigator.addTabRemoveListener((tab:Tab) => {
                return this.tabRemove(tab);
            });
            this.navigator.addTabSelectedListener((tab:Tab)=>{
                this.showTab(tab);
            });
        }

        addTab(tab:api_ui_tab.Tab, panel:api_ui.Panel) {
            this.navigator.addTab(tab);
            this.addPanel(panel);
        }

        showTab(tab:api_ui_tab.Tab) {
            super.showPanel(tab.getTabIndex());
            this.navigator.selectTab(tab);
        }

        tabRemove(tab:api_ui_tab.Tab):bool {
            this.removePanel(tab.getTabIndex());
            return true;
        }
    }
}
