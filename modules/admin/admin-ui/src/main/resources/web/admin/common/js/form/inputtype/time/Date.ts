module api.content.form.inputtype.time {

    import support = api.form.inputtype.support;

    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;

    /**
     * Uses [[api.data.ValueType]] [[api.data.ValueTypeLocalDate]].
     */
    export class Date extends support.BaseInputTypeNotManagingAdd<Date> {

        constructor(config: api.form.inputtype.InputTypeViewContext) {
            super(config);
        }

        getValueType(): ValueType {
            return ValueTypes.LOCAL_DATE;
        }

        newInitialValue(): Value {
            return ValueTypes.LOCAL_DATE.newNullValue();
        }

        createInputOccurrenceElement(index: number, property: Property): api.dom.Element {

            var datePickerBuilder = new api.ui.time.DatePickerBuilder();

            if (!property.hasNullValue()) {
                var date = property.getLocalDate();
                datePickerBuilder.
                    setSelectedDate(date).
                    setYear(date.getFullYear()).
                    setMonth(date.getMonth());
            }
            var datePicker = datePickerBuilder.build();
            datePicker.onSelectedDateChanged((event: api.ui.time.SelectedDateChangedEvent) => {
                this.onValueChanged(property, event.getDate(), ValueTypes.LOCAL_DATE);
            });

            property.onPropertyValueChanged((event: api.data.PropertyValueChangedEvent) => {
                this.updateInputOccurrenceElement(datePicker, property, true);
            });
            return datePicker;
        }

        availableSizeChanged() {
        }


        updateInputOccurrenceElement(occurrence: api.dom.Element, property: api.data.Property, unchangedOnly?: boolean) {
            var datePicker = <api.ui.time.DatePicker> occurrence;
            //TODO: add method to date picker
            //datePicker.setSelectedDate(property.getLocalDate());
        }

        valueBreaksRequiredContract(value: Value): boolean {
            return value.isNull() || !value.getType().equals(ValueTypes.LOCAL_DATE);
        }

        hasInputElementValidUserInput(inputElement: api.dom.Element) {
            var datePicker = <api.ui.time.DatePicker>inputElement;
            return datePicker.hasValidUserInput();
        }
    }
    api.form.inputtype.InputTypeManager.register(new api.Class("Date", Date));

}