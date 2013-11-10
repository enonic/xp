module api_form_input_type {

    export interface SingleSelectorConfig {
        type: string;
        options: {
            label: string;
            value: string;
        }[]
    }

    export class SingleSelector extends BaseInputTypeView {

        public static TYPE_DROPDOWN = "DROPDOWN";
        public static TYPE_RADIO = "RADIO";
        public static TYPE_COMBOBOX = "COMBOBOX";

        private config:SingleSelectorConfig;

        constructor(config:InputTypeViewConfig<SingleSelectorConfig>) {
            super("SingleSelector");
            this.addClass("single-selector");
            this.config = config.inputConfig;
        }

        createInputOccurrenceElement(index:number, property:api_data.Property):api_dom.Element {

            var type = this.config && this.config.type && this.config.type.toUpperCase();
            var name = this.getInput().getName() + "-" + index;
            if (SingleSelector.TYPE_RADIO == type) {
                return this.createRadioElement(name, property);
            }
            else if (SingleSelector.TYPE_COMBOBOX == type) {
                return this.createComboBoxElement(name, property);
            }
            else if (SingleSelector.TYPE_DROPDOWN == type) {
                return this.createDropdownElement(name, property);
            }
            else {
                throw new Error("Unsupported type of SingleSelector: " + type);
            }
        }

        private createComboBoxElement(name:string, property:api_data.Property):api_dom.Element {
            var inputEl = new api_dom.DivEl();

            var selectedOptionsView = new api_ui_combobox.ComboBoxSelectedOptionsView<string>();
            var comboBox = new api_ui_combobox.ComboBox<string>(name, {
                rowHeight: 24,
                filter: this.comboboxFilter,
                selectedOptionsView: selectedOptionsView,
                maximumOccurrences: 1
            });
            comboBox.addListener({
                onInputValueChanged: function (oldValue, newValue, grid) {
                    grid.getDataView().setFilterArgs({searchString: newValue});
                    grid.getDataView().refresh();
                },
                onSelectedOptionRemoved: null,
                onOptionSelected: null
            });

            if (this.config) {
                var option;
                for (var i = 0; i < this.config.options.length; i++) {
                    option = this.config.options[i];
                    comboBox.addOption({ value: option.value, displayValue: option.label});
                }
            }

            if (property) {
                comboBox.setValue(property.getString());
            }

            inputEl.appendChild(comboBox);
            inputEl.appendChild(selectedOptionsView);

            return inputEl;
        }

        private createDropdownElement(name:string, property:api_data.Property):api_dom.Element {

            var inputEl = new api_ui.Dropdown(name);

            if (this.config) {
                for (var i = 0; i < this.config.options.length; i++) {
                    var option = this.config.options[i];
                    inputEl.addOption(option.value, option.label);
                }
            }

            if (property) {
                inputEl.setValue(property.getString());
            }

            return inputEl;
        }


        private createRadioElement(name:string, property:api_data.Property):api_dom.Element {

            var inputEl = new api_ui.RadioGroup(name);

            if (this.config) {
                for (var i = 0; i < this.config.options.length; i++) {
                    var option = this.config.options[i];
                    inputEl.addOption(option.value, option.label);
                }
            }

            if (property) {
                inputEl.setValue(property.getString());
            }

            return inputEl;
        }

        getValue(occurrence:api_dom.Element):api_data.Value {
            var inputEl = <api_dom.FormInputEl>occurrence;
            return new api_data.Value(inputEl.getValue(), api_data.ValueTypes.STRING);
        }

        valueBreaksRequiredContract(value:api_data.Value):boolean {
            // TODO:
            return false;
        }

        private comboboxFilter(item:api_ui_combobox.OptionData<string>, args) {
            return !(args && args.searchString && item.displayValue.toUpperCase().indexOf(args.searchString.toUpperCase()) == -1);
        }
    }

    api_form_input.InputTypeManager.register("SingleSelector", SingleSelector);
}