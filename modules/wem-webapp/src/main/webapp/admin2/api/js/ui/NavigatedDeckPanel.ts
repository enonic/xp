module api_ui {

    /**
     * A DeckPanel with PanelNavigationItem-s.
     */
    export class NavigatedDeckPanel extends DeckPanel {

        private navigator:api_ui.DeckPanelNavigator;

        constructor(navigator:DeckPanelNavigator, idPrefix?:string) {
            super(idPrefix);
            this.navigator = navigator;
        }

        getSelectedNavigationItem():PanelNavigationItem {
            return this.navigator.getSelectedNavigationItem();
        }

        addNavigablePanelToFront(item:PanelNavigationItem, panel:Panel) {
            this.navigator.addNavigationItem(item);
            super.addPanel(panel);

            this.selectPanel(item);
        }

        addNavigablePanelToBack(item:PanelNavigationItem, panel:Panel) {
            this.navigator.addNavigationItem(item);
            super.addPanel(panel);
        }

        selectPanel(item:PanelNavigationItem) {
            this.showPanel(item.getIndex());
            this.navigator.selectNavigationItem(item.getIndex());
        }

        removePanel(panel:Panel, checkCanRemovePanel:boolean = true):number {

            var panelIndex:number = this.getPanelIndex(panel);
            var navigationItem:api_ui.PanelNavigationItem = this.navigator.getNavigationItem(panelIndex);
            var removedPanelAtIndex = super.removePanel(panel, checkCanRemovePanel);
            var removed:boolean = removedPanelAtIndex !== -1;

            if (removed) {
                this.navigator.removeNavigationItem(navigationItem);
            }
            return removedPanelAtIndex;
        }
    }
}
