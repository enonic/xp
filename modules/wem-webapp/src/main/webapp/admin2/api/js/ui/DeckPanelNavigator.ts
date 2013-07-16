module api_ui {

    export interface DeckPanelNavigator {

        addNavigationItem(item:PanelNavigationItem);

        removeNavigationItem(item:PanelNavigationItem);

        getNavigationItem(index:number);

        selectNavigationItem(index:number);

        getSelectedNavigationItem():PanelNavigationItem;

        /**
         * Deselects any selected tab.
         */
        deselectNavigationItem();

        getSize():number;

    }
}
