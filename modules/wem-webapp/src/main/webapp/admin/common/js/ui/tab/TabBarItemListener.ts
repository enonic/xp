module api.ui.tab {

    export interface TabBarItemListener extends api.event.Listener {

        onSelected?: (tab:TabBarItem) => void;

    }

}