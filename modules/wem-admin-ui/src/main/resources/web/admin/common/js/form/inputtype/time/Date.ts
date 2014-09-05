module api.content.form.inputtype.time {

    import support = api.form.inputtype.support;

    import ValueTypes = api.data.type.ValueTypes;

    export class Date extends support.BaseInputTypeNotManagingAdd<any> {

        constructor(config: api.form.inputtype.InputTypeViewContext<any>) {
            super(config);
        }

        newInitialValue(): api.data.Value {
            return null;
        }

        createInputOccurrenceElement(index: number, property: api.data.Property): api.dom.Element {

            var datePickerBuilder = new api.ui.time.DatePickerBuilder();

            if (property != null) {
                var date = property.getDate();
                datePickerBuilder.
                    setSelectedDate(date).
                    setYear(date.getUTCFullYear()).
                    setMonth(date.getUTCMonth());
            }
            return datePickerBuilder.build();
        }

        availableSizeChanged() {
        }

        onOccurrenceValueChanged(element: api.dom.Element, listener: (event: api.form.inputtype.support.ValueChangedEvent) => void) {
            var datePicker = <api.ui.time.DatePicker>element;
            datePicker.onSelectedDateChanged((event: api.ui.time.SelectedDateChangedEvent) => {
                var changedValue = new api.data.Value(event.getDate(), ValueTypes.LOCAL_DATE);
                listener(new api.form.inputtype.support.ValueChangedEvent(changedValue));
            });
        }

        getValue(occurrence: api.dom.Element): api.data.Value {

            var datePicker: api.ui.time.DatePicker = < api.ui.time.DatePicker>occurrence;

            if (datePicker.getSelectedDate()) {
                return new api.data.Value(datePicker.getSelectedDate(), ValueTypes.LOCAL_DATE);
            }
            else {
                return null;
            }
        }

        valueBreaksRequiredContract(value: api.data.Value): boolean {
            if (value == null) {
                return true;
            }
            return !value.getType().equals(ValueTypes.LOCAL_DATE);
        }

    }
    api.form.inputtype.InputTypeManager.register(new api.Class("Date", Date));

}