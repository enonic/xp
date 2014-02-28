module api.ui.tab {

    export class TabMenuItemClosedEvent extends TabMenuItemEvent {

        constructor(tab: TabMenuItem) {
            super(tab);
        }
    }
}