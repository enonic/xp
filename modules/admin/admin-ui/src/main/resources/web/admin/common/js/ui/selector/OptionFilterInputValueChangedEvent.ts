module api.ui.selector {

    export class OptionFilterInputValueChangedEvent<OPTION_DISPLAY_VALUE> {

        private oldValue: string;

        private newValue: string;

        constructor(oldValue: string, newValue: string) {
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
