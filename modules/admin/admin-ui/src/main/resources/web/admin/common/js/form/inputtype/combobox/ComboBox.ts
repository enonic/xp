module api.form.inputtype.combobox {

    import PropertyArray = api.data.PropertyArray;
    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;
    import SelectedOptionEvent = api.ui.selector.combobox.SelectedOptionEvent;
    import FocusSwitchEvent = api.ui.FocusSwitchEvent;

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
            let options: ComboBoxOption[] = [];

            let optionValues = inputConfig['option'] || [];
            let l = optionValues.length, optionValue;
            for (let i = 0; i < l; i++) {
                optionValue = optionValues[i];
                options.push({label: optionValue['value'], value: optionValue['@value']});
            }
            this.comboBoxOptions = options;
        }

        getComboBox(): api.ui.selector.combobox.ComboBox<string> {
            return this.comboBox;
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
                this.comboBox.addOption({value: option.value, displayValue: option.label});
            });

            this.appendChild(this.comboBox);
            this.appendChild(<api.dom.Element> this.selectedOptionsView);

            this.setLayoutInProgress(false);

            return wemQ<void>(null);
        }

        update(propertyArray: api.data.PropertyArray, unchangedOnly?: boolean): Q.Promise<void> {
            let superPromise = super.update(propertyArray, unchangedOnly);

            if (!unchangedOnly || !this.comboBox.isDirty()) {
                return superPromise.then(() => {
                    this.comboBox.setValue(this.getValueFromPropertyArray(propertyArray));
                });
            } else {
                return superPromise;
            }
        }

        reset() {
            this.comboBox.resetBaseValues();
        }

        createComboBox(input: api.form.Input, propertyArray: PropertyArray): api.ui.selector.combobox.ComboBox<string> {
            let comboBox = new api.ui.selector.combobox.ComboBox<string>(name, {
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
            comboBox.onOptionSelected((event: SelectedOptionEvent<string>) => {
                this.ignorePropertyChange = true;

                const option = event.getSelectedOption();
                let value = new Value(option.getOption().value, ValueTypes.STRING);
                if (option.getIndex() >= 0) {
                    this.getPropertyArray().set(option.getIndex(), value);
                } else {
                    this.getPropertyArray().add(value);
                }

                this.ignorePropertyChange = false;
                this.validate(false);

                this.fireFocusSwitchEvent(event);
            });
            comboBox.onOptionDeselected((event: SelectedOptionEvent<string>) => {
                this.ignorePropertyChange = true;

                this.getPropertyArray().remove(event.getSelectedOption().getIndex());

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

        private comboBoxFilter(item: api.ui.selector.Option<string>, args: any) {
            return !(args && args.searchString && item.displayValue.toUpperCase().indexOf(args.searchString.toUpperCase()) == -1);
        }

        protected getNumberOfValids(): number {
            return this.getPropertyArray().getSize();
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
