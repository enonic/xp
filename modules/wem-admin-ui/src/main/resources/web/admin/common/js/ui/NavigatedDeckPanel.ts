module api.ui {

    /**
     * A DeckPanel with NavigationItem-s.
     */
    export class NavigatedDeckPanel extends DeckPanel {

        private navigator: api.ui.Navigator;

        constructor(navigator: Navigator, className?: string) {
            super(className);
            this.navigator = navigator;

            navigator.onNavigationItemSelected((event: NavigatorEvent) => {
                this.showPanelByIndex(event.getItem().getIndex());
            });
        }

        getSelectedNavigationItem(): NavigationItem {
            return this.navigator.getSelectedNavigationItem();
        }

        addNavigablePanelToFront(item: NavigationItem, panel: Panel) {
            this.navigator.addNavigationItem(item);
            super.addPanel(panel);

            this.selectPanel(item);
        }

        addNavigablePanelToBack(item: NavigationItem, panel: Panel) {
            this.navigator.addNavigationItem(item);
            super.addPanel(panel);
        }

        selectPanel(item: NavigationItem) {
            this.selectPanelFromIndex(item.getIndex());
        }

        selectPanelFromIndex(index: number) {
            this.navigator.selectNavigationItem(index);
            // panel will be shown because of the selected navigator listener in constructor
        }

        removePanel(panel: Panel, checkCanRemovePanel: boolean = true): number {

            var panelIndex: number = this.getPanelIndex(panel);
            var navigationItem: api.ui.NavigationItem = this.navigator.getNavigationItem(panelIndex);
            var removedPanelAtIndex = super.removePanel(panel, checkCanRemovePanel);
            var removed: boolean = removedPanelAtIndex !== -1;

            if (removed) {
                this.navigator.removeNavigationItem(navigationItem);
            }
            return removedPanelAtIndex;
        }
    }
}
