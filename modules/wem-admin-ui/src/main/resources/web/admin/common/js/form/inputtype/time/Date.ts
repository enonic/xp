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

            var datePicker: api.ui.time.DatePicker;

            if (property != null) {
                if (api.util.isStringEmpty(property.getValue().asString())) {
                    return new api.ui.time.DatePickerBuilder().build();
                }
                var date = property.getValue().getDate();

                datePicker = new api.ui.time.DatePickerBuilder().
                    setMonth(date.getMonth()).setYear(date.getFullYear()).setSelectedDate(date).build();
            }
            else {
                datePicker = new api.ui.time.DatePickerBuilder().build();
            }
            return datePicker;
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
            if (api.util.isStringBlank(value.asString())) {
                return true;
            } else {
                return false;
            }
        }

    }
    api.form.inputtype.InputTypeManager.register(new api.Class("Date", Date));

}