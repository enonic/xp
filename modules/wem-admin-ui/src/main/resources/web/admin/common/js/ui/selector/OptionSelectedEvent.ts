module api.ui.selector {

    export class OptionSelectedEvent<OPTION_DISPLAY_VALUE> {

        private option: Option<OPTION_DISPLAY_VALUE>;

        constructor(option: Option<OPTION_DISPLAY_VALUE>) {
            this.option = option;
        }

        getOption(): Option<OPTION_DISPLAY_VALUE> {
            return this.option;
        }
    }
}