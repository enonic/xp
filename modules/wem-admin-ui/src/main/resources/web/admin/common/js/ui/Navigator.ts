module api.ui {

    export interface Navigator {

        addNavigationItem(item: NavigationItem);

        removeNavigationItem(item: NavigationItem);

        getNavigationItem(index: number):NavigationItem;

        selectNavigationItem(index: number, silent?: boolean);

        getSelectedNavigationItem():NavigationItem;

        getSelectedIndex():number;

        /**
         * Deselects any selected tab.
         */
        deselectNavigationItem();

        getSize():number;

        getNavigationItems():NavigationItem[];

        onNavigationItemAdded(listener: (event: NavigatorEvent) => void);

        onNavigationItemSelected(listener: (event: NavigatorEvent) => void);

        onNavigationItemDeselected(listener: (event: NavigatorEvent) => void);

        unNavigationItemAdded(listener: (event: NavigatorEvent) => void);

        unNavigationItemSelected(listener: (event: NavigatorEvent) => void);

        unNavigationItemDeselected(listener: (event: NavigatorEvent) => void);
    }
}
