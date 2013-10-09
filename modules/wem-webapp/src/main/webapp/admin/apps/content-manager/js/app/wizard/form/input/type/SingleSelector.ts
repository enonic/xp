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
        public static TYPE_COMBOBOX = "COMBOBOX";

        private config:SingleSelectorConfig;

        constructor(config?:SingleSelectorConfig) {
            super("SingleSelector");
            this.addClass("single-selector");
            this.config = config;
        }

        createInputOccurrenceElement(index:number, property?:api_data.Property):api_dom.Element {
            var inputEl;
            var name = this.getInput().getName() + "-" + index;

            var type = this.config && this.config.type && this.config.type.toUpperCase();
            if (SingleSelector.TYPE_RADIO == type) {
                inputEl = new api_ui.RadioGroup(name);
            } else if (SingleSelector.TYPE_COMBOBOX == type) {
                inputEl = new api_ui_combobox.ComboBox(name, [], {rowHeight: 24, filter: this.comboboxFilter});
                inputEl.addListener({
                    onInputValueChanged: function (oldValue, newValue, grid) {
                        grid.getDataView().setFilterArgs({searchString: newValue});
                        grid.getDataView().refresh();
                    }
                });
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

        private comboboxFilter(item:api_ui_combobox.OptionData, args) {
            return !(args && args.searchString && item.displayValue.toUpperCase().indexOf(args.searchString.toUpperCase()) == -1);
        }
    }

    app_wizard_form_input.InputTypeManager.register("SingleSelector", SingleSelector);
}