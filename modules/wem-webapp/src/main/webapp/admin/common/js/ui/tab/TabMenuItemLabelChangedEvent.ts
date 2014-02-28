module api.ui.tab {

    export class TabMenuItemLabelChangedEvent extends TabMenuItemEvent {

        private oldValue: string;
        private newValue: string;

        constructor(tab: TabMenuItem, oldValue: string, newValue: string) {
            super(tab);
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        getOldValue(): string {
            return this.oldValue;
        }

        getNewValue(): string {
            return this.newValue;
        }
    }
}