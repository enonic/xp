module api_ui_tab {

    export class TabBar extends api_dom.DivEl implements TabNavigator {

        constructor(idPrefix?:string) {
            super(idPrefix || "TabBar");
        }

        addTab(tab:api_ui_tab.Tab) {

        }

        removeTab(tab:api_ui_tab.Tab) {

        }

        getSize():number {
            return 0;
        }

        getActiveTab():api_ui_tab.Tab {
            return null;
        }

        selectTab(tab:api_ui_tab.Tab) {

        }

        deselectTab() {

        }

        addTabSelectedListener(listener:(Tab) => void) {

        }

        addTabRemoveListener(listener:(Tab) => bool) {

        }
    }
}
