module api_ui {

    /**
     * A DeckPanel with PanelNavigationItem-s.
     */
    export class NavigatedDeckPanel extends DeckPanel {

        private navigator:api_ui.DeckPanelNavigator;

        constructor(navigator:DeckPanelNavigator) {
            super();
            this.navigator = navigator;
        }

        getSelectedNavigationItem():PanelNavigationItem {
            return this.navigator.getSelectedNavigationItem();
        }

        addNavigationItem(item:PanelNavigationItem, panel:Panel, inBackground?:bool = false) {

            this.navigator.addNavigationItem(item);
            this.addPanel(panel);

            if (!inBackground) {
                this.selectPanel(item);
            }
        }

        selectPanel(item:PanelNavigationItem) {
            this.showPanel(item.getIndex());
            this.navigator.selectNavigationItem(item.getIndex());
        }

        removePanel(panel:Panel, checkCanRemovePanel?:bool = true):number {

            var panelIndex:number = this.getPanelIndex(panel);
            var navigationItem:api_ui.PanelNavigationItem = this.navigator.getNavigationItem(panelIndex);
            var removedPanelAtIndex = super.removePanel(panel, checkCanRemovePanel);
            var removed:bool = removedPanelAtIndex !== -1;

            if (removed) {
                this.navigator.removeNavigationItem(navigationItem);
            }
            return removedPanelAtIndex;
        }
    }
}
