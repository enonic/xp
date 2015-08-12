module api.form.inputtype.combobox {

    import PropertyArray = api.data.PropertyArray;
    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;

    export interface ComboBoxConfig {
        options: ComboBoxOption[]
    }

    export class ComboBox extends api.form.inputtype.support.BaseInputTypeManagingAdd<string> {

        private context: api.form.inputtype.InputTypeViewContext<ComboBoxConfig>;

        private comboBoxConfig: ComboBoxConfig;

        private comboBox: api.ui.selector.combobox.ComboBox<string>;

        private selectedOptionsView: api.ui.selector.combobox.SelectedOptionsView<string>;

        constructor(context: api.form.inputtype.InputTypeViewContext<ComboBoxConfig>) {
            super("combo-box");
            this.addClass("input-type-view");
            this.context = context;
            this.comboBoxConfig = context.inputConfig;
        }

        availableSizeChanged() {
            // console.log("ComboBox.availableSizeChanged(" + this.getEl().getWidth() + "x" + this.getEl().getWidth() + ")");
        }

        getValueType(): ValueType {
            return ValueTypes.STRING;
        }

        newInitialValue(): Value {
            return null;
        }

        layout(input: api.form.Input, propertyArray: PropertyArray): wemQ.Promise<void> {

            super.layout(input, propertyArray);

            this.selectedOptionsView = new api.ui.selector.combobox.BaseSelectedOptionsView<string>();
            this.comboBox = this.createComboBox(input);

            if (this.comboBoxConfig.options) {
                this.comboBoxConfig.options.forEach((option: ComboBoxOption) => {
                    this.comboBox.addOption({value: option.value, displayValue: option.label})
                });
            }

            var valueArray: string[] = [];
            this.getPropertyArray().forEach((property: Property) => {
                valueArray.push(property.getString());
            });
            this.comboBox.setValues(valueArray, true);

            this.appendChild(this.comboBox);
            this.appendChild(this.selectedOptionsView);

            this.setLayoutInProgress(false);

            return wemQ<void>(null);
        }

        createComboBox(input: api.form.Input): api.ui.selector.combobox.ComboBox<string> {
            var comboBox = new api.ui.selector.combobox.ComboBox<string>(name, {
                filter: this.comboBoxFilter,
                selectedOptionsView: this.selectedOptionsView,
                maximumOccurrences: input.getOccurrences().getMaximum(),
                optionDisplayValueViewer: new ComboBoxDisplayValueViewer(),
                hideComboBoxWhenMaxReached: true
            });

            comboBox.onOptionFilterInputValueChanged((event: api.ui.selector.OptionFilterInputValueChangedEvent<string>) => {
                this.comboBox.setFilterArgs({searchString: event.getNewValue()});
            });
            comboBox.onOptionSelected((event: api.ui.selector.OptionSelectedEvent<string>) => {

                var value = new Value(event.getOption().value, ValueTypes.STRING);
                if (event.getIndex() >= 0) {
                    this.getPropertyArray().set(event.getIndex(), value);
                } else {
                    this.getPropertyArray().add(value);
                }


                this.validate(false);
            });
            comboBox.onOptionDeselected((removed: api.ui.selector.combobox.SelectedOption<string>) => {

                this.getPropertyArray().remove(removed.getIndex());
                //this.notifyValueRemoved(removed.getIndex());

                this.validate(false);
            });

            return comboBox;
        }

        giveFocus(): boolean {
            if (this.comboBox.maximumOccurrencesReached()) {
                return false;
            }
            return this.comboBox.giveFocus();
        }

        valueBreaksRequiredContract(value: Value): boolean {
            return value.isNull() || !value.getType().equals(ValueTypes.STRING) || !this.isExistingValue(value.getString());
        }

        private isExistingValue(value: string): boolean {
            return this.comboBoxConfig.options.some((option: ComboBoxOption) => {
                return option.value == value;
            });
        }

        private comboBoxFilter(item: api.ui.selector.Option<string>, args) {
            return !(args && args.searchString && item.displayValue.toUpperCase().indexOf(args.searchString.toUpperCase()) == -1);
        }

        protected getNumberOfValids(): number {
            return this.comboBox.countSelectedOptions();
        }


        onFocus(listener: (event: FocusEvent) => void) {
            this.comboBox.onFocus(listener);
        }

        unFocus(listener: (event: FocusEvent) => void) {
            this.comboBox.unFocus(listener);
        }

        onBlur(listener: (event: FocusEvent) => void) {
            this.comboBox.onBlur(listener);
        }

        unBlur(listener: (event: FocusEvent) => void) {
            this.comboBox.unBlur(listener);
        }

    }

    api.form.inputtype.InputTypeManager.register(new api.Class("ComboBox", ComboBox));
}