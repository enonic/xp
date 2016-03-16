module api.form.inputtype.combobox {

    import PropertyArray = api.data.PropertyArray;
    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;

    export class ComboBox extends api.form.inputtype.support.BaseInputTypeManagingAdd<string> {

        private context: api.form.inputtype.InputTypeViewContext;

        private comboBoxOptions: ComboBoxOption[];

        private comboBox: api.ui.selector.combobox.ComboBox<string>;

        private selectedOptionsView: api.ui.selector.combobox.SelectedOptionsView<string>;

        constructor(context: api.form.inputtype.InputTypeViewContext) {
            super("");
            this.context = context;
            this.readConfig(context.inputConfig);
        }

        private readConfig(inputConfig: { [element: string]: { [name: string]: string }[]; }): void {
            var options: ComboBoxOption[] = [];

            var optionValues = inputConfig['option'] || [];
            var l = optionValues.length, optionValue;
            for (var i = 0; i < l; i++) {
                optionValue = optionValues[i];
                options.push({label: optionValue['value'], value: optionValue['@value']});
            }
            this.comboBoxOptions = options;
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
            if (!ValueTypes.STRING.equals(propertyArray.getType())) {
                propertyArray.convertValues(ValueTypes.STRING);
            }
            super.layout(input, propertyArray);

            this.selectedOptionsView = new api.ui.selector.combobox.BaseSelectedOptionsView<string>();
            this.comboBox = this.createComboBox(input, propertyArray);

            this.comboBoxOptions.forEach((option: ComboBoxOption) => {
                this.comboBox.addOption({value: option.value, displayValue: option.label})
            });

            this.appendChild(this.comboBox);
            this.appendChild(<api.dom.Element> this.selectedOptionsView);

            this.setLayoutInProgress(false);

            return wemQ<void>(null);
        }

        update(propertyArray: api.data.PropertyArray, unchangedOnly?: boolean): Q.Promise<void> {
            var superPromise = super.update(propertyArray, unchangedOnly);

            if (!unchangedOnly || !this.comboBox.isDirty()) {
                return superPromise.then(() => {
                    this.comboBox.setValue(this.getValueFromPropertyArray(propertyArray));
                });
            } else {
                return superPromise;
            }
        }

        createComboBox(input: api.form.Input, propertyArray: PropertyArray): api.ui.selector.combobox.ComboBox<string> {
            var comboBox = new api.ui.selector.combobox.ComboBox<string>(name, {
                filter: this.comboBoxFilter,
                selectedOptionsView: this.selectedOptionsView,
                maximumOccurrences: input.getOccurrences().getMaximum(),
                optionDisplayValueViewer: new ComboBoxDisplayValueViewer(),
                hideComboBoxWhenMaxReached: true,
                value: this.getValueFromPropertyArray(propertyArray)
            });

            comboBox.onOptionFilterInputValueChanged((event: api.ui.selector.OptionFilterInputValueChangedEvent<string>) => {
                this.comboBox.setFilterArgs({searchString: event.getNewValue()});
            });
            comboBox.onOptionSelected((selectedOption: SelectedOption<string>) => {
                this.ignorePropertyChange = true;

                var value = new Value(selectedOption.getOption().value, ValueTypes.STRING);
                if (selectedOption.getIndex() >= 0) {
                    this.getPropertyArray().set(selectedOption.getIndex(), value);
                } else {
                    this.getPropertyArray().add(value);
                }

                this.ignorePropertyChange = false;
                this.validate(false);
            });
            comboBox.onOptionDeselected((removed: api.ui.selector.combobox.SelectedOption<string>) => {
                this.ignorePropertyChange = true;

                this.getPropertyArray().remove(removed.getIndex());

                this.ignorePropertyChange = false;
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
            return this.comboBoxOptions.some((option: ComboBoxOption) => {
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