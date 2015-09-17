module api.ui.tab {

    export class TabItemClosedEvent extends TabItemEvent {

        constructor(tab: TabItem) {
            super(tab);
        }
    }
}