module api.ui.tab {

    export class TabBarItemEvent {

        private tab: TabBarItem;

        constructor(tab: TabBarItem) {
            this.tab = tab;
        }

        getTab(): TabBarItem {
            return this.tab;
        }
    }
}