module api.schema.content.inputtype {

    import ContentInputTypeViewContext = api.content.form.inputtype.ContentInputTypeViewContext;
    import InputValidationRecording = api.form.inputtype.InputValidationRecording;
    import InputValidityChangedEvent = api.form.inputtype.InputValidityChangedEvent;
    import ValueChangedEvent = api.form.inputtype.ValueChangedEvent;
    import ValueTypes = api.data.type.ValueTypes;
    import ValueType = api.data.type.ValueType;
    import Value = api.data.Value;
    import Property = api.data.Property;
    import Input = api.form.Input;
    import ContentTypeComboBox = api.schema.content.ContentTypeComboBox;
    import ContentTypeSummary = api.schema.content.ContentTypeSummary;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;

    export class ContentTypeFilter extends api.form.inputtype.support.BaseInputTypeManagingAdd<string> {

        private input: Input;

        private combobox: ContentTypeComboBox;

        private validationRecording: InputValidationRecording;

        private layoutInProgress: boolean;

        constructor(context: ContentInputTypeViewContext<any>) {
            super('content-type-filter');
        }

        getValueType(): ValueType {
            return ValueTypes.STRING;
        }

        newInitialValue(): string {
            return null;
        }

        layout(input: Input, properties: api.data.Property[]) {

            this.layoutInProgress = true;

            this.input = input;

            this.combobox = new ContentTypeComboBox(input.getOccurrences().getMaximum());

            // select properties once when combobox has been loaded first time
            var selectProperties = (items: ContentTypeSummary[]) => {
                var names = properties.map((property: Property) => property.getValue().asString());
                items.filter((item: ContentTypeSummary) => (names.indexOf(item.getContentTypeName().toString()) >= 0)).
                    forEach((item: ContentTypeSummary) => this.combobox.select(item));

                this.layoutInProgress = false;
                this.combobox.unLoaded(selectProperties);
            };
            this.combobox.onLoaded(selectProperties);

            this.combobox.onOptionSelected((event: OptionSelectedEvent<ContentTypeSummary>) => {
                if (this.layoutInProgress) {
                    return;
                }

                var value = new Value(event.getOption().displayValue.getContentTypeName().toString(), ValueTypes.STRING);
                if (this.combobox.countSelected() == 1) { // overwrite initial value
                    this.notifyValueChanged(new api.form.inputtype.ValueChangedEvent(value, 0));
                }
                else {
                    this.notifyValueAdded(value);
                }

                this.validate(false);
            });

            this.combobox.onOptionDeselected((option: SelectedOption<ContentTypeSummary>) => {
                this.notifyValueRemoved(option.getIndex());
                this.validate(false);
            });

            this.appendChild(this.combobox);
        }

        getValues(): Value[] {
            return this.combobox.getSelectedDisplayValues().map((contentType: ContentTypeSummary) => {
                return new Value(contentType.getContentTypeName().toString(), ValueTypes.STRING);
            });
        }

        validate(silent: boolean = true): InputValidationRecording {

            var recording = new InputValidationRecording(),
                values = this.getValues(),
                occurrences = this.input.getOccurrences();

            recording.setBreaksMinimumOccurrences(occurrences.minimumBreached(values.length));
            recording.setBreaksMaximumOccurrences(occurrences.maximumBreached(values.length));

            if (!silent && recording.validityChanged(this.validationRecording)) {
                this.notifyValidityChanged(new InputValidityChangedEvent(recording, this.input.getName()));
            }

            this.validationRecording = recording;
            return recording;
        }

        giveFocus(): boolean {
            return this.combobox.maximumOccurrencesReached() ? false : this.combobox.giveFocus();
        }

        onFocus(listener: (event: FocusEvent) => void) {
            this.combobox.onFocus(listener);
        }

        unFocus(listener: (event: FocusEvent) => void) {
            this.combobox.unFocus(listener);
        }

        onBlur(listener: (event: FocusEvent) => void) {
            this.combobox.onBlur(listener);
        }

        unBlur(listener: (event: FocusEvent) => void) {
            this.combobox.unBlur(listener);
        }
    }

    api.form.inputtype.InputTypeManager.register(new api.Class("ContentTypeFilter", ContentTypeFilter));

}