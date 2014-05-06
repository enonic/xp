module api.ui.tab {

    export class TabMenuItemEvent {

        private tab: TabMenuItem;

        constructor(tab: TabMenuItem) {
            this.tab = tab;
        }

        getTab(): TabMenuItem {
            return this.tab;
        }
    }
}