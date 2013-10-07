module api_ui {

    /**
     * A DeckPanel with PanelNavigationItem-s.
     */
    export class NavigatedDeckPanel extends DeckPanel {

        private navigator:api_ui.DeckPanelNavigator;

        constructor(navigator:DeckPanelNavigator, idPrefix?:string) {
            super(idPrefix);
            this.navigator = navigator;

            navigator.addListener({
                onNavigationItemSelected: (item:api_ui.PanelNavigationItem) => {
                    this.showPanel(item.getIndex());
                }
            });
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
            this.selectPanelFromIndex(item.getIndex());
        }

        selectPanelFromIndex(index:number) {
            this.showPanel(index);
            this.navigator.selectNavigationItem(index);
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
