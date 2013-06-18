module api_ui_tab {

    export class TabBar extends api_ui.DivEl implements TabNavigator {

        constructor(idPrefix?:string) {
            super(idPrefix || "TabBar");
        }

        addTab(tab:api_ui_tab.Tab) {


            // TODO: cast to TabMenuItem and add
        }

        getSize():number {
            return 0;
        }

        addTabSelectedListener(listener:TabSelectedListener) {
            // TODO
        }

        addTabRemoveListener(listener:api_ui_tab.TabRemoveListener) {
            // TODO
        }
    }
}
