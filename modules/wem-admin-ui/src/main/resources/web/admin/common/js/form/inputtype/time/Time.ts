module api.content.form.inputtype.time {

    import support = api.form.inputtype.support;
    import ValueTypes = api.data.type.ValueTypes;

    export class Time extends support.BaseInputTypeNotManagingAdd<any> {

        constructor(config: api.form.inputtype.InputTypeViewContext<any>) {
            super(config);
        }

        getValueType(): api.data.type.ValueType {
            return api.data.type.ValueTypes.LOCAL_TIME;
        }

        newInitialValue(): api.data.Value {
            return null;
        }

        createInputOccurrenceElement(index: number, property: api.data.Property): api.dom.Element {

            var timePicker: api.ui.time.TimePicker;

            if (property.hasNonNullValue()) {
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
                var newTime: string = api.util.DateHelper.padNumber(hours, 2) + ":" + api.util.DateHelper.padNumber(minutes, 2);
                var changedValue = ValueTypes.LOCAL_TIME.newValue(newTime);
                listener(new api.form.inputtype.support.ValueChangedEvent(changedValue));

            });
        }


        getValue(occurrence: api.dom.Element): api.data.Value {
            var timePicker: api.ui.time.TimePicker = < api.ui.time.TimePicker>occurrence;
            if (timePicker.getSelectedTime()) {
                var time: string = api.util.DateHelper.padNumber(timePicker.getSelectedTime().hour, 2) + ":" +
                                   api.util.DateHelper.padNumber(timePicker.getSelectedTime().minute, 2);
                return new api.data.Value(time, ValueTypes.LOCAL_TIME);

            }
            else {
                return   null;
            }
        }


        valueBreaksRequiredContract(value: api.data.Value): boolean {
            if (value == null) {
                return true;
            }
            if (api.util.isStringBlank(value.asString())) {
                return true;
            }
            return !value.getType().equals(ValueTypes.LOCAL_TIME);

        }

    }
    api.form.inputtype.InputTypeManager.register(new api.Class("Time", Time));

}