module app_wizard_form_input_type {

    export interface ComboBoxConfig {
        options: ComboBoxOption[]
    }

    export interface ComboBoxOption {
        label: string;
        value: string;
    }

    export class ComboBox extends api_dom.DivEl implements InputTypeView {

        private input:api_schema_content_form.Input;

        private config:ComboBoxConfig;

        private comboBox:api_ui_combobox.ComboBox;


        constructor(config?:ComboBoxConfig) {
            super("ComboBox", "combo-box");
            this.config = config;
        }

        getHTMLElement():HTMLElement {
            return super.getHTMLElement();
        }

        isManagingAdd():boolean {
            return true;
        }

        addFormItemOccurrencesListener(listener:app_wizard_form.FormItemOccurrencesListener) {
            throw new Error("ComboBox manages occurrences self");
        }

        removeFormItemOccurrencesListener(listener:app_wizard_form.FormItemOccurrencesListener) {
            throw new Error("ComboBox manages occurrences self");
        }

        public maximumOccurrencesReached():boolean {
            return this.input.getOccurrences().maximumReached(this.comboBox.countSelected());
        }

        createAndAddOccurrence() {
            throw new Error("ComboBox manages occurrences self");
        }

        layout(input:api_schema_content_form.Input, properties:api_data.Property[]) {

            this.input = input;

            this.comboBox = this.createComboBox(input);

            this.config.options.forEach((option:ComboBoxOption) => {
                this.comboBox.addOption({value: option.value, displayValue: option.label})
            });

            if (properties != null) {
                var valueArray:string[] = [];
                properties.forEach((property:api_data.Property) => {
                    valueArray.push(property.getString());
                });
                this.comboBox.setValues(valueArray);
            }

            this.appendChild(this.comboBox);
        }

        createComboBox(input:api_schema_content_form.Input):api_ui_combobox.ComboBox {
            var comboboxConfig = {
                rowHeight: 24,
                filter: this.comboboxFilter,
                maximumOccurrences: input.getOccurrences().getMaximum()
            };
            var comboBox = new api_ui_combobox.ComboBox(name, comboboxConfig);

            comboBox.addListener({
                onInputValueChanged: function (oldValue, newValue, grid) {
                    grid.getDataView().setFilterArgs({searchString: newValue});
                    grid.getDataView().refresh();
                }
            });

            return comboBox;
        }

        getComboBox():api_ui_combobox.ComboBox {
            return this.comboBox;
        }

        getValues():api_data.Value[] {

            var values:api_data.Value[] = [];
            this.comboBox.getSelectedData().forEach((option:api_ui_combobox.OptionData)  => {
                var value = new api_data.Value(option.value, api_data.ValueTypes.STRING);
                values.push(value);
            });
            return values;
        }

        validate(validationRecorder:app_wizard_form.ValidationRecorder) {

            // TODO:
        }

        valueBreaksRequiredContract(value:api_data.Value):boolean {
            // TODO:
            return false;
        }

        private comboboxFilter(item:api_ui_combobox.OptionData, args) {
            return !(args && args.searchString && item.displayValue.toUpperCase().indexOf(args.searchString.toUpperCase()) == -1);
        }
    }

    app_wizard_form_input.InputTypeManager.register("ComboBox", ComboBox);
}