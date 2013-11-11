module api_ui {

    export interface DeckPanelNavigatorListener extends api_event.Listener {

        onNavigationItemAdded?: (tab:PanelNavigationItem) => void;

        onNavigationItemSelected?: (tab:PanelNavigationItem) => void;

    }

}