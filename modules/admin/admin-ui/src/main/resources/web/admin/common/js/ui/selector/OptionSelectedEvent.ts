module api.ui.selector {

    export class OptionSelectedEvent<OPTION_DISPLAY_VALUE> {

        private option: Option<OPTION_DISPLAY_VALUE>;

        private previousOption: Option<OPTION_DISPLAY_VALUE>;

        private index: number;

        private keyCode: number;

        constructor(option: Option<OPTION_DISPLAY_VALUE>, previousOption: Option<OPTION_DISPLAY_VALUE>, index: number = -1,
                    keyCode: number = -1) {
            this.option = option;
            this.previousOption = previousOption;
            this.index = index;
            this.keyCode = keyCode;
        }

        getOption(): Option<OPTION_DISPLAY_VALUE> {
            return this.option;
        }

        getPreviousOption(): Option<OPTION_DISPLAY_VALUE> {
            return this.previousOption;
        }

        getIndex(): number {
            return this.index;
        }

        getKeyCode(): number {
            return this.keyCode;
        }
    }
}
