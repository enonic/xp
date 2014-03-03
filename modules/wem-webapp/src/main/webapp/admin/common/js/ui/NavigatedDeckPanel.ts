module api.ui {

    /**
     * A DeckPanel with PanelNavigationItem-s.
     */
    export class NavigatedDeckPanel extends DeckPanel {

        private navigator: api.ui.DeckPanelNavigator;

        constructor(navigator: DeckPanelNavigator, className?: string) {
            super(className);
            this.navigator = navigator;

            navigator.onNavigationItemSelected((event: DeckPanelNavigatorEvent) => {
                this.showPanel(event.getTabItem().getIndex());
            });
        }

        getSelectedNavigationItem(): PanelNavigationItem {
            return this.navigator.getSelectedNavigationItem();
        }

        addNavigablePanelToFront(item: PanelNavigationItem, panel: Panel) {
            this.navigator.addNavigationItem(item);
            super.addPanel(panel);

            this.selectPanel(item);
        }

        addNavigablePanelToBack(item: PanelNavigationItem, panel: Panel) {
            this.navigator.addNavigationItem(item);
            super.addPanel(panel);
        }

        selectPanel(item: PanelNavigationItem) {
            this.selectPanelFromIndex(item.getIndex());
        }

        selectPanelFromIndex(index: number) {
            this.navigator.selectNavigationItem(index);
            // panel will be shown because of the selected navigator listener in constructor
        }

        removePanel(panel: Panel, checkCanRemovePanel: boolean = true): number {

            var panelIndex: number = this.getPanelIndex(panel);
            var navigationItem: api.ui.PanelNavigationItem = this.navigator.getNavigationItem(panelIndex);
            var removedPanelAtIndex = super.removePanel(panel, checkCanRemovePanel);
            var removed: boolean = removedPanelAtIndex !== -1;

            if (removed) {
                this.navigator.removeNavigationItem(navigationItem);
            }
            return removedPanelAtIndex;
        }
    }
}
