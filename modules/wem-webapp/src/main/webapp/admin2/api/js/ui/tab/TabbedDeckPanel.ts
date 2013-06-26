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
                return this.handleTabRemoveEvent(tab);
            });
            this.navigator.addTabSelectedListener((tab:Tab)=> {
                this.showTab(tab);
            });
        }

        getSelectedTab():api_ui_tab.Tab {
            return this.navigator.getSelectedTab();
        }

        addTab(tab:api_ui_tab.Tab, panel:api_ui.Panel) {

            this.navigator.addTab(tab);
            this.addPanel(panel);
        }

        showTab(tab:api_ui_tab.Tab) {
            this.showPanel(tab.getTabIndex());
            this.navigator.selectTab(tab.getTabIndex());
        }

        removePanel(panel:api_ui.Panel):number {

            var panelIndex:number = this.getPanelIndex(panel);
            var tab:Tab = this.navigator.getTab(panelIndex);
            var removedPanelAtIndex = super.removePanel(panel);
            var removed:bool = removedPanelAtIndex !== -1;

            if (removed) {
                this.navigator.removeTab(tab);
            }
            return removedPanelAtIndex;
        }

        handleTabRemoveEvent(tab:api_ui_tab.Tab):bool {
            var removedPanel:api_ui.Panel = this.removePanelByIndex(tab.getTabIndex());
            return removedPanel !== null;
        }
    }
}
