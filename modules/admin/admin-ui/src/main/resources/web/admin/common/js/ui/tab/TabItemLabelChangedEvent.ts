module api.ui.tab {

    export class TabItemLabelChangedEvent extends TabItemEvent {

        private oldValue: string;
        private newValue: string;

        constructor(tab: TabItem, oldValue: string, newValue: string) {
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