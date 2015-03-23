module api.content.form.inputtype.time {

    import support = api.form.inputtype.support;
    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;

    export interface DateTimeConfig {
        withTimezone: boolean
    }

    /**
     * Uses [[api.data.ValueType]] [[api.data.ValueTypeLocalDateTime]].
     */
    export class DateTime extends support.BaseInputTypeNotManagingAdd<any,Date> {

        withTimezone: boolean = false;
        valueType: ValueType = ValueTypes.LOCAL_DATE_TIME;

        constructor(config: api.form.inputtype.InputTypeViewContext<DateTimeConfig>) {
            super(config);
            if (config.inputConfig.withTimezone && <any>config.inputConfig.withTimezone == 'true') {
                this.withTimezone = true;
                this.valueType = ValueTypes.DATE_TIME;
            }
        }

        getValueType(): ValueType {
            return this.valueType;
        }

        newInitialValue(): Value {
            return this.valueType.newNullValue();
        }

        createInputOccurrenceElement(index: number, property: Property): api.dom.Element {

            var dateTimeBuilder = new api.ui.time.DateTimePickerBuilder();
            if (property.hasNonNullValue()) {
                var date;
                if(this.valueType == ValueTypes.DATE_TIME) {
                    date = property.getDateTime();
                } else {
                    date = property.getLocalDateTime();
                }

                dateTimeBuilder.
                    setYear(date.getFullYear()).
                    setMonth(date.getMonth()).
                    setSelectedDate(date).
                    setHours(date.getHours()).
                    setMinutes(date.getMinutes());
            }

            var dateTimePicker = new api.ui.time.DateTimePicker(dateTimeBuilder);
            dateTimePicker.onSelectedDateTimeChanged((event: api.ui.time.SelectedDateChangedEvent) => {
                var newValue = new Value(event.getDate(), this.valueType);
                property.setValue(newValue);
            });
            return dateTimePicker;
        }

        hasInputElementValidUserInput(inputElement: api.dom.Element) {
            var dateTimePicker = <api.ui.time.DateTimePicker>inputElement;
            return dateTimePicker.hasValidUserInput();
        }

        availableSizeChanged() {
            // Nothing
        }

        valueBreaksRequiredContract(value: Value): boolean {
            return value.isNull() || !value.getType().equals(ValueTypes.LOCAL_DATE_TIME) || !value.getType().equals(ValueTypes.DATE_TIME);
        }
    }
    api.form.inputtype.InputTypeManager.register(new api.Class("DateTime", DateTime));

}