module api_ui_tab {

    export interface TabBarItemListener extends api_event.Listener {

        onSelected?: (tab:TabBarItem) => void;

    }

}