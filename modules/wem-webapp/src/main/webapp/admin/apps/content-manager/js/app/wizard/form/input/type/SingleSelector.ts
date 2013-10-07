module app_wizard_form_input_type {

    export interface SingleSelectorConfig {
        type: string;
        options: {
            label: string;
            value: string;
        }[]
    }

    export class SingleSelector extends BaseInputTypeView {

        public static TYPE = "DROPDOWN";
        public static TYPE_RADIO = "RADIO";

        private config:SingleSelectorConfig;

        constructor(config?:SingleSelectorConfig) {
            super("SingleSelector");
            this.config = config;
        }

        createInputOccurrenceElement(index:number, property?:api_data.Property):api_dom.Element {
            var inputEl;
            var name = this.getInput().getName() + "-" + index;

            if (this.config && this.config.type &&
                SingleSelector.TYPE_RADIO == this.config.type.toUpperCase()) {

                inputEl = new api_ui.RadioGroup(name);
            } else {
                inputEl = new api_ui.Dropdown(name);
            }

            if (this.config) {
                var option;
                for (var i = 0; i < this.config.options.length; i++) {
                    option = this.config.options[i];
                    inputEl.addOption(option.value, option.label);
                }
            }

            if (property) {
                inputEl.setValue(property.getValue());
            }

            return inputEl;
        }

        getValue(occurrence:api_dom.Element):api_data.Value {
            var inputEl = <api_dom.FormInputEl>occurrence;
            return new api_data.Value(inputEl.getValue(), api_data.ValueTypes.STRING);
        }
    }

    app_wizard_form_input.InputTypeManager.register("SingleSelector", SingleSelector);
}