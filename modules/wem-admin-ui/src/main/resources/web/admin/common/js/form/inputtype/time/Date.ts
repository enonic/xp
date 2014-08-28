module api.content.form.inputtype.time {

    import support = api.form.inputtype.support;

    export class Date extends support.BaseInputTypeNotManagingAdd<any> {

        constructor(config: api.form.inputtype.InputTypeViewContext<any>) {
            super(config);
        }

        newInitialValue(): api.data.Value {
            return new api.data.Value("", api.data.ValueTypes.STRING);
        }

        createInputOccurrenceElement(index: number, property: api.data.Property): api.dom.Element {

            var datePicker: api.ui.time.DatePicker;

            if (property != null) {
                if (api.util.isStringEmpty(property.getValue().asString())) {
                    return new api.ui.time.DatePickerBuilder().build();
                }
                var date = api.util.parseDate(property.getValue().asString());

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
                listener(new api.form.inputtype.support.ValueChangedEvent(this.newValue(event.getDate().toDateString())));
            });
        }


        private newValue(s: string): api.data.Value {
            return new api.data.Value(s, api.data.ValueTypes.STRING);
        }

        getValue(occurrence: api.dom.Element): api.data.Value {
            var datePicker: api.ui.time.DatePicker = < api.ui.time.DatePicker>occurrence;
            if (datePicker.getSelectedDate()) {
                return  this.newValue(datePicker.getSelectedDate().toDateString());
            }
            else {
                return  this.newValue("");
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