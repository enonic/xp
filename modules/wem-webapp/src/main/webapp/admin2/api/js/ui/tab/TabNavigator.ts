module api_ui_tab {

    export interface TabNavigator {

        addTab(tab:api_ui_tab.Tab);

        getSize():number;

        addTabSelectedListener(listener:TabSelectedListener);

        addTabRemoveListener(listener:api_ui_tab.TabRemoveListener);
    }
}
