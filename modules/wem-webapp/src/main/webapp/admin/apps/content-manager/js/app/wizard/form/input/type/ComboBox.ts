module app_wizard_form_input_type {

    export interface ComboBoxConfig {
        options: ComboBoxOption[]
    }

    export interface ComboBoxOption {
        label: string;
        value: string;
    }

    export class ComboBox extends api_dom.DivEl implements InputTypeView {

        private input:api_schema_content_form.Input

        private config:ComboBoxConfig;

        private comboBox:api_ui.ComboBox;


        constructor(config?:ComboBoxConfig) {
            super("ComboBox");
            this.addClass("combo-box");
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

        layout(input:api_schema_content_form.Input, properties?:api_data.Property[]) {

            this.input = input;

            this.comboBox = new api_ui.ComboBox(name, [], {rowHeight: 24, filter: this.comboboxFilter});

            this.comboBox.addListener({
                onInputValueChanged: function (oldValue, newValue, grid) {
                    grid.getDataView().setFilterArgs({searchString: newValue});
                    grid.getDataView().refresh();
                }
            });

            this.config.options.forEach((option:ComboBoxOption) => {
                this.comboBox.addOption(option.value, option.label)
            });

            this.appendChild(this.comboBox);
        }

        getValues():api_data.Value[] {

            var values:api_data.Value[] = [];
            this.comboBox.getSelectedData().forEach((option:api_ui.OptionData)  => {
                var value = new api_data.Value(option.id, api_data.ValueTypes.STRING);
                values.push(value);
            });
            return values;
        }

        private comboboxFilter(item:api_ui.OptionData, args) {
            return !(args && args.searchString && item.value.toUpperCase().indexOf(args.searchString.toUpperCase()) == -1);
        }
    }

    app_wizard_form_input.InputTypeManager.register("ComboBox", ComboBox);
}