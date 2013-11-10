module api_form_input_type {

    export interface ComboBoxConfig {
        options: ComboBoxOption[]
    }

    export interface ComboBoxOption {
        label: string;
        value: string;
    }

    export class ComboBox extends api_dom.DivEl implements InputTypeView {

        private input:api_form.Input;

        private config:ComboBoxConfig;

        private comboBox:api_ui_combobox.ComboBox<string>;

        private selectedOptionsView:api_ui_combobox.ComboBoxSelectedOptionsView<string>;

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

        addFormItemOccurrencesListener(listener:api_form.FormItemOccurrencesListener) {
            throw new Error("ComboBox manages occurrences self");
        }

        removeFormItemOccurrencesListener(listener:api_form.FormItemOccurrencesListener) {
            throw new Error("ComboBox manages occurrences self");
        }

        public maximumOccurrencesReached():boolean {
            return this.input.getOccurrences().maximumReached(this.comboBox.countSelected());
        }

        createAndAddOccurrence() {
            throw new Error("ComboBox manages occurrences self");
        }

        layout(input:api_form.Input, properties:api_data.Property[]) {

            this.input = input;

            this.selectedOptionsView = new api_ui_combobox.ComboBoxSelectedOptionsView<string>();
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
            this.appendChild(this.selectedOptionsView);
        }

        createComboBox(input:api_form.Input):api_ui_combobox.ComboBox<string> {
            var comboboxConfig = {
                rowHeight: 24,
                filter: this.comboboxFilter,
                selectedOptionsView: this.selectedOptionsView,
                maximumOccurrences: input.getOccurrences().getMaximum()
            };
            var comboBox = new api_ui_combobox.ComboBox<string>(name, comboboxConfig);

            comboBox.addListener({
                onInputValueChanged: function (oldValue, newValue, grid) {
                    grid.getDataView().setFilterArgs({searchString: newValue});
                    grid.getDataView().refresh();
                },
                onSelectedOptionRemoved: null,
                onOptionSelected: null
            });

            return comboBox;
        }

        getValues():api_data.Value[] {

            var values:api_data.Value[] = [];
            this.comboBox.getSelectedData().forEach((option:api_ui_combobox.OptionData<string>)  => {
                var value = new api_data.Value(option.value, api_data.ValueTypes.STRING);
                values.push(value);
            });
            return values;
        }

        getAttachments():api_content.Attachment[] {
            return [];
        }

        validate(validationRecorder:api_form.ValidationRecorder) {

            // TODO:
        }

        valueBreaksRequiredContract(value:api_data.Value):boolean {
            // TODO:
            return false;
        }

        private comboboxFilter(item:api_ui_combobox.OptionData<string>, args) {
            return !(args && args.searchString && item.displayValue.toUpperCase().indexOf(args.searchString.toUpperCase()) == -1);
        }
    }

    api_form_input.InputTypeManager.register("ComboBox", ComboBox);
}