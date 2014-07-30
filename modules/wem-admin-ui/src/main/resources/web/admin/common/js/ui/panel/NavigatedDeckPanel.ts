module api.ui.panel {

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

        addNavigablePanel(item: NavigationItem, panel: Panel, select?: boolean) {
            this.navigator.addNavigationItem(item);
            var index = this.addPanel(panel);
            if (select) {
                this.selectPanelByIndex(index);
            }
            return index;
        }

        selectPanel(item: NavigationItem) {
            this.selectPanelByIndex(item.getIndex());
        }

        selectPanelByIndex(index: number) {
            this.navigator.selectNavigationItem(index);
            // panel will be shown because of the selected navigator listener in constructor
        }

        removeNavigablePanel(panel: Panel, checkCanRemovePanel: boolean = true): number {
            var index = this.removePanel(panel, checkCanRemovePanel);
            if (index > -1) {
                var navigationItem: api.ui.NavigationItem = this.navigator.getNavigationItem(index);
                this.navigator.removeNavigationItem(navigationItem);
            }
            return index;
        }
    }
}
