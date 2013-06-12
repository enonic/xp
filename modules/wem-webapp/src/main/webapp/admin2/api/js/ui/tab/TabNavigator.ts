module api_ui_tab {

    export interface TabNavigator {

        addTab(tab:api_ui_tab.Tab);

        addTabSelectedListener(listener:TabSelectedListener);

        addTabRemovedListener(listener:TabRemovedListener);
    }
}
