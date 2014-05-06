module api.ui {

    export interface DeckPanelNavigator {

        addNavigationItem(item: PanelNavigationItem);

        removeNavigationItem(item: PanelNavigationItem);

        getNavigationItem(index: number):PanelNavigationItem;

        selectNavigationItem(index: number);

        getSelectedNavigationItem():PanelNavigationItem;

        getSelectedIndex():number;

        /**
         * Deselects any selected tab.
         */
        deselectNavigationItem();

        getSize():number;

        getNavigationItems():PanelNavigationItem[];

        onNavigationItemAdded(listener: (event: DeckPanelNavigatorEvent) => void);

        onNavigationItemSelected(listener: (event: DeckPanelNavigatorEvent) => void);

        onNavigationItemDeselected(listener: (event: DeckPanelNavigatorEvent) => void);

        unNavigationItemAdded(listener: (event: DeckPanelNavigatorEvent) => void);

        unNavigationItemSelected(listener: (event: DeckPanelNavigatorEvent) => void);

        unNavigationItemDeselected(listener: (event: DeckPanelNavigatorEvent) => void);
    }
}
