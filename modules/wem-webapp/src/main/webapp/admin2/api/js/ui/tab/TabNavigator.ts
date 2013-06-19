module api_ui_tab {

    export interface TabNavigator {

        addTab(tab:api_ui_tab.Tab);

        removeTab(tab:api_ui_tab.Tab);

        selectTab(tab:api_ui_tab.Tab);

        getActiveTab():api_ui_tab.Tab;

        /**
         * Deselects any selected tab.
         */
        deselectTab();

        getSize():number;

        addTabSelectedListener(listener:(Tab) => void);

        addTabRemoveListener(listener:(Tab) => bool);
    }
}
