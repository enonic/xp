module api.ui {

    export interface DeckPanelNavigatorListener extends api.event.Listener {

        onNavigationItemAdded?: (tab:PanelNavigationItem) => void;

        onNavigationItemSelected?: (tab:PanelNavigationItem) => void;

        onNavigationItemDeselected?: (tab:PanelNavigationItem) => void;

    }

}