module api_ui {

    export interface DeckPanelNavigator extends api_event.Observable {

        addNavigationItem(item:PanelNavigationItem);

        removeNavigationItem(item:PanelNavigationItem);

        getNavigationItem(index:number):PanelNavigationItem;

        selectNavigationItem(index:number);

        getSelectedNavigationItem():PanelNavigationItem;

        getSelectedIndex():number;

        /**
         * Deselects any selected tab.
         */
        deselectNavigationItem();

        getSize():number;

        getNavigationItems():PanelNavigationItem[];

        addListener(listener:DeckPanelNavigatorListener);

        removeListener(listener:DeckPanelNavigatorListener);

    }
}
