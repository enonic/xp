module api.content.form.inputtype.time {

    import support = api.form.inputtype.support;
    import ValueTypes = api.data.type.ValueTypes;

    export class DateTime extends support.BaseInputTypeNotManagingAdd<any> {

        constructor(config: api.form.inputtype.InputTypeViewContext<any>) {
            super(config);
        }

        newInitialValue(): api.data.Value {
            return new api.data.Value("", ValueTypes.STRING);
        }


        createInputOccurrenceElement(index: number, property: api.data.Property): api.dom.Element {
            if (property != null) {
                if (api.util.isStringEmpty(property.getValue().asString())) {
                    return new api.ui.time.DateTime();
                } else {

                    var date = property.getValue().getDate();
                    var dateBuilder = new api.ui.time.DatePickerBuilder().
                        setMonth(date.getMonth()).setYear(date.getFullYear()).setSelectedDate(date);
                    var timeBuilder = new api.ui.time.TimePickerBuilder().setHours(date.getHours()).setMinutes(date.getMinutes());
                    return new api.ui.time.DateTime(dateBuilder, timeBuilder);
                }


            } else {
                return    new api.ui.time.DateTime();
            }


        }

        availableSizeChanged() {
        }

        onOccurrenceValueChanged(element: api.dom.Element, listener: (event: api.form.inputtype.support.ValueChangedEvent) => void) {
            var dateTime = <api.ui.time.DateTime>element;
            dateTime.onTimeChanged((hours: number, minutes: number) => {
                var newDateTime: string = dateTime.getSelectedDate() + " " + hours + ":" + minutes;
                listener(new api.form.inputtype.support.ValueChangedEvent(this.newValue(newDateTime)));
            });
            dateTime.onDateChanged((event: api.ui.time.SelectedDateChangedEvent) => {
                var newDateTime = event.getDate().toDateString() + " " + dateTime.getSelectedTime();
                listener(new api.form.inputtype.support.ValueChangedEvent(this.newValue(newDateTime)));
            });
        }


        private newValue(s: string): api.data.Value {
            return new api.data.Value(s, ValueTypes.STRING);
        }

        getValue(occurrence: api.dom.Element): api.data.Value {
            var dateTime: api.ui.time.DateTime = < api.ui.time.DateTime>occurrence;
            if (dateTime.getSelectedDate() != null) {
                return  this.newValue(dateTime.getSelectedDate() + " " + dateTime.getSelectedTime());
            }
            else {
                return    this.newValue("");
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
    api.form.inputtype.InputTypeManager.register(new api.Class("DateTime", DateTime));

}