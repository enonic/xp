module api.ui.tab {

    export class TabMenuItemSelectedEvent extends TabMenuItemEvent {

        constructor(tab: TabMenuItem) {
            super(tab);
        }
    }
}