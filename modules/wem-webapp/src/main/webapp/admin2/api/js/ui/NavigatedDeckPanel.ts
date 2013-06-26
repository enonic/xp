module api_ui {

    /**
     * A DeckPanel with PanelNavigationItem-s.
     */
    export class NavigatedDeckPanel extends DeckPanel {

        private navigator:api_ui.DeckPanelNavigator;

        constructor(navigator:DeckPanelNavigator) {
            super();
            this.navigator = navigator;

            this.navigator.addNavigationItemRemoveListener((item:PanelNavigationItem) => {
                return this.handleNavigationItemRemoveEvent(item);
            });
            this.navigator.addNavigationItemSelectedListener((item:PanelNavigationItem)=> {
                this.selectPanel(item);
            });
        }

        getSelectedNavigationItem():PanelNavigationItem {
            return this.navigator.getSelectedNavigationItem();
        }

        addNavigationItem(item:PanelNavigationItem, panel:Panel) {

            this.navigator.addNavigationItem(item);
            this.addPanel(panel);
        }

        selectPanel(item:PanelNavigationItem) {
            this.showPanel(item.getIndex());
            this.navigator.selectNavigationItem(item.getIndex());
        }

        removePanel(panel:Panel, checkCanRemovePanel?:bool = true):number {

            var panelIndex:number = this.getPanelIndex(panel);
            var navigationItem:api_ui.PanelNavigationItem = this.navigator.getNavigationItem(panelIndex);
            var removedPanelAtIndex = super.removePanel(panel);
            var removed:bool = removedPanelAtIndex !== -1;

            if (removed) {
                this.navigator.removeNavigationItem(navigationItem);
            }
            return removedPanelAtIndex;
        }

        private handleNavigationItemRemoveEvent(item:PanelNavigationItem):bool {
            var removedPanel:Panel = this.removePanelByIndex(item.getIndex());
            return removedPanel !== null;
        }
    }
}
