module api.content.form.inputtype.time {

    import support = api.form.inputtype.support;
    import ValueTypes = api.data.type.ValueTypes;

    export class Time extends support.BaseInputTypeNotManagingAdd<any,api.util.LocalTime> {

        constructor(config: api.form.inputtype.InputTypeViewContext<any>) {
            super(config);
        }

        getValueType(): api.data.type.ValueType {
            return api.data.type.ValueTypes.LOCAL_TIME;
        }

        newInitialValue(): api.util.LocalTime {
            return null;
        }

        createInputOccurrenceElement(index: number, property: api.data.Property): api.dom.Element {
            var localTimeEl = new api.ui.time.LocalTime();

            var timeValue: api.util.LocalTime = property.getLocalTime();
            if (timeValue) {
                localTimeEl.setTime(timeValue);
            }

            return localTimeEl;
        }

        availableSizeChanged() {
        }

        onOccurrenceValueChanged(element: api.dom.Element, listener: (event: api.form.inputtype.support.ValueChangedEvent) => void) {
            var localTimeEl = <api.ui.time.LocalTime>element;
            localTimeEl.onTimeChanged((hours: number, minutes: number) => {
                var newTime: string = hours + ":" + minutes;
                var utcTime = api.util.DateHelper.parseUTCTime(newTime);
                var changedValue: api.data.Value = ValueTypes.LOCAL_TIME.newValue(utcTime);
                listener(new api.form.inputtype.support.ValueChangedEvent(changedValue));


            });
        }


        getValue(occurrence: api.dom.Element): api.data.Value {
            var localTimeEl: api.ui.time.LocalTime = <api.ui.time.LocalTime>occurrence;
            var selectedTime = localTimeEl.getSelectedTime();
            var time: string = selectedTime.hour + ":" + selectedTime.minute;
            return ValueTypes.LOCAL_TIME.newValue(time);
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