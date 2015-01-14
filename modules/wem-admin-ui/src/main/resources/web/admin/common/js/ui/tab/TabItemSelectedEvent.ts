module api.ui.tab {

    export class TabItemSelectedEvent extends TabItemEvent {

        constructor(tab: TabItem) {
            super(tab);
        }
    }
}