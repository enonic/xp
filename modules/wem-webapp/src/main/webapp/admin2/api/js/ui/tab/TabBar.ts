module api_ui_tab {

    export class TabBar extends api_dom.DivEl implements api_ui.DeckPanelNavigator {

        constructor(idPrefix?:string) {
            super(idPrefix || "TabBar");
        }

        addNavigationItem(tab:api_ui.PanelNavigationItem) {

        }

        removeNavigationItem(tab:api_ui.PanelNavigationItem) {

        }

        getSize():number {
            return 0;
        }

        getSelectedNavigationItem():api_ui.PanelNavigationItem {
            return null;
        }

        getNavigationItem(tabIndex:number) {
            return null;
        }

        selectNavigationItem(tabIndex:number) {

        }

        deselectNavigationItem() {

        }

        addNavigationItemSelectedListener(listener:(tab:api_ui.PanelNavigationItem) => void) {

        }

        addNavigationItemRemoveListener(listener:(tab:api_ui.PanelNavigationItem) => bool) {

        }
    }
}
