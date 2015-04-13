module api.ui.selector {

    export class OptionSelectedEvent<OPTION_DISPLAY_VALUE> {

        private option: Option<OPTION_DISPLAY_VALUE>;

        private index: number;

        constructor(option: Option<OPTION_DISPLAY_VALUE>, index: number = null) {
            this.option = option;
            this.index = index == null ? -1 : index;
        }

        getOption(): Option<OPTION_DISPLAY_VALUE> {
            return this.option;
        }

        getIndex(): number {
            return this.index;
        }
    }
}