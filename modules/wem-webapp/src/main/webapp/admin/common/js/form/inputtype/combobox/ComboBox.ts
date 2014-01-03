module api.form.inputtype.combobox {

    export interface ComboBoxConfig {
        options: ComboBoxOption[]
    }

    export class ComboBox extends api.dom.DivEl implements api.form.inputtype.InputTypeView {

        private input: api.form.Input;

        private config: ComboBoxConfig;

        private comboBox: api.ui.combobox.ComboBox<string>;

        private selectedOptionsView: api.ui.combobox.SelectedOptionsView<string>;

        constructor(config: api.form.inputtype.InputTypeViewConfig<ComboBoxConfig>) {
            super("ComboBox", "combo-box");
            this.config = config.inputConfig;
        }

        getHTMLElement(): HTMLElement {
            return super.getHTMLElement();
        }

        isManagingAdd(): boolean {
            return true;
        }

        addFormItemOccurrencesListener(listener: api.form.FormItemOccurrencesListener) {
            throw new Error("ComboBox manages occurrences self");
        }

        removeFormItemOccurrencesListener(listener: api.form.FormItemOccurrencesListener) {
            throw new Error("ComboBox manages occurrences self");
        }

        public maximumOccurrencesReached(): boolean {
            return this.input.getOccurrences().maximumReached(this.comboBox.countSelected());
        }

        createAndAddOccurrence() {
            throw new Error("ComboBox manages occurrences self");
        }

        layout(input: api.form.Input, properties: api.data.Property[]) {

            this.input = input;

            this.selectedOptionsView = new api.ui.combobox.SelectedOptionsView<string>();
            this.comboBox = this.createComboBox(input);

            this.config.options.forEach((option: ComboBoxOption) => {
                this.comboBox.addOption({value: option.value, displayValue: option.label})
            });

            if (properties != null) {
                var valueArray: string[] = [];
                properties.forEach((property: api.data.Property) => {
                    valueArray.push(property.getString());
                });
                this.comboBox.setValues(valueArray);
            }

            this.appendChild(this.comboBox);
            this.appendChild(this.selectedOptionsView);
        }

        createComboBox(input: api.form.Input): api.ui.combobox.ComboBox<string> {
            var comboboxConfig = {
                rowHeight: 24,
                filter: this.comboboxFilter,
                selectedOptionsView: this.selectedOptionsView,
                maximumOccurrences: input.getOccurrences().getMaximum(),
                hideComboBoxWhenMaxReached: true
            };
            var comboBox = new api.ui.combobox.ComboBox<string>(name, comboboxConfig);

            comboBox.addListener({
                onInputValueChanged: function (oldValue, newValue, grid) {
                    grid.getDataView().setFilterArgs({searchString: newValue});
                    grid.getDataView().refresh();
                },
                onOptionSelected: null
            });

            return comboBox;
        }

        getValues(): api.data.Value[] {

            var values: api.data.Value[] = [];
            this.comboBox.getSelectedData().forEach((option: api.ui.combobox.Option<string>)  => {
                var value = new api.data.Value(option.value, api.data.ValueTypes.STRING);
                values.push(value);
            });
            return values;
        }

        getAttachments(): api.content.attachment.Attachment[] {
            return [];
        }

        validate(validationRecorder: api.form.ValidationRecorder) {

            // TODO:
        }

        giveFocus(): boolean {
            if (this.comboBox.maximumOccurrencesReached()) {
                return false;
            }
            return this.comboBox.giveFocus();
        }

        valueBreaksRequiredContract(value: api.data.Value): boolean {
            // TODO:
            return false;
        }

        private comboboxFilter(item: api.ui.combobox.Option<string>, args) {
            return !(args && args.searchString && item.displayValue.toUpperCase().indexOf(args.searchString.toUpperCase()) == -1);
        }
    }

    api.form.input.InputTypeManager.register("ComboBox", ComboBox);
}