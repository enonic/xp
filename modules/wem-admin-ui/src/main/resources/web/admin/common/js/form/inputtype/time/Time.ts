module api.content.form.inputtype.time {

    import support = api.form.inputtype.support;

    export class Time extends support.BaseInputTypeNotManagingAdd<any> {

        constructor(config: api.form.inputtype.InputTypeViewContext<any>) {
            super(config);
        }

        newInitialValue(): api.data.Value {
            return new api.data.Value("", api.data.ValueTypes.STRING);
        }

        createInputOccurrenceElement(index: number, property: api.data.Property): api.dom.Element {

            var timePicker: api.ui.time.TimePicker;

            if (property != null) {
                if (property.getValue().asString().length == 0) {
                    return new api.ui.time.TimePickerBuilder().build();
                }
                var time = property.getValue().asString();
                var arrTime = time.split(":");
                var h = arrTime[0];
                var m = arrTime[1];
                timePicker = new api.ui.time.TimePickerBuilder().setHours(parseInt(h)).setMinutes(parseInt(m)).build();
            }
            else {
                timePicker = new api.ui.time.TimePickerBuilder().build();
            }
            return timePicker;
        }

        availableSizeChanged() {
        }

        onOccurrenceValueChanged(element: api.dom.Element, listener: (event: api.form.inputtype.support.ValueChangedEvent) => void) {
            var timePicker = <api.ui.time.TimePicker>element;
            timePicker.onSelectedTimeChanged((hours: number, minutes: number) => {
                var newTime: string = hours + ":" + minutes;
                listener(new api.form.inputtype.support.ValueChangedEvent(this.newValue(newTime)));
            });
        }


        private newValue(s: string): api.data.Value {
            return new api.data.Value(s, api.data.ValueTypes.STRING);
        }

        getValue(occurrence: api.dom.Element): api.data.Value {
            var timePicker: api.ui.time.TimePicker = < api.ui.time.TimePicker>occurrence;
            if (timePicker.getSelectedTime()) {
                return  this.newValue(timePicker.getSelectedTime().hour + ":" + timePicker.getSelectedTime().minute);
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
    api.form.inputtype.InputTypeManager.register(new api.Class("Time", Time));

}