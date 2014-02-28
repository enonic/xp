module api.ui {

    export class DeckPanelNavigatorEvent {

        private tab: PanelNavigationItem;

        constructor(tab: PanelNavigationItem) {

            this.tab = tab;
        }

        getTabItem(): PanelNavigationItem {
            return this.tab;
        }
    }
}