module api.ui {

    export class NavigatorEvent {

        private tab: NavigationItem;

        constructor(tab: NavigationItem) {

            this.tab = tab;
        }

        getItem(): NavigationItem {
            return this.tab;
        }
    }
}