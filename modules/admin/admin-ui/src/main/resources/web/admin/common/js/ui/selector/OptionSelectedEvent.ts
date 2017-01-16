module api.ui.selector {

    export class OptionSelectedEvent<OPTION_DISPLAY_VALUE> {

        private option: Option<OPTION_DISPLAY_VALUE>;

        private index: number;

        private keyCode: number;

        constructor(option: Option<OPTION_DISPLAY_VALUE>, index: number = -1, keyCode: number = -1) {
            this.option = option;
            this.index = index;
            this.keyCode = keyCode;
        }

        getOption(): Option<OPTION_DISPLAY_VALUE> {
            return this.option;
        }

        getIndex(): number {
            return this.index;
        }

        getKeyCode(): number {
            return this.keyCode;
        }
    }
}
