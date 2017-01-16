module api.ui.time {

    export class TimePickerBuilder {

        hours: number;

        minutes: number;

        setHours(value: number): TimePickerBuilder {
            this.hours = value;
            return this;
        }

        setMinutes(value: number): TimePickerBuilder {
            this.minutes = value;
            return this;
        }

        build(): TimePicker {
            return new TimePicker(this);
        }
    }

    export class TimePicker extends Picker {

        constructor(builder: TimePickerBuilder) {
            super(builder, 'time-picker');
        }

        protected initPopup(builder: TimePickerBuilder) {
            this.popup = new TimePickerPopupBuilder().
                setHours(builder.hours).
                setMinutes(builder.minutes).
                build();
        }

        protected initInput(builder: TimePickerBuilder) {
            let value;
            if (builder.hours || builder.minutes) {
                value = this.formatTime(builder.hours, builder.minutes);
            }

            this.input = api.ui.text.TextInput.middle(undefined, value);
            this.input.setPlaceholder('hh:mm');
        }

        protected setupListeners(builder: TimePickerBuilder) {

            this.popup.onSelectedTimeChanged((hours: number, minutes: number) => {
                if (hours != null && minutes != null) {
                    this.input.setValue(this.formatTime(hours, minutes), false, true);
                    this.validUserInput = true;
                }

                this.updateInputStyling();
            });

            this.input.onKeyUp((event: KeyboardEvent) => {
                if (api.ui.KeyHelper.isArrowKey(event) || api.ui.KeyHelper.isModifierKey(event)) {
                    return;
                }

                let typedTime = this.input.getValue();
                if (api.util.StringHelper.isEmpty(typedTime)) {
                    this.validUserInput = true;
                    this.popup.setSelectedTime(null, null);
                    if (this.popup.isVisible()) {
                        this.popup.hide();
                    }
                } else {
                    let parsedTime = typedTime.match(/^[0-2][0-9]:[0-5][0-9]$/);
                    if (parsedTime && parsedTime.length == 1) {
                        let splitTime = parsedTime[0].split(':');
                        this.validUserInput = true;
                        this.popup.setSelectedTime(parseInt(splitTime[0], 10), parseInt(splitTime[1], 10));
                        if (!this.popup.isVisible()) {
                            this.popup.show();
                        }
                    } else {
                        this.validUserInput = false;
                        this.popup.setSelectedTime(null, null);
                    }
                }

                this.updateInputStyling();
            });
        }

        setSelectedTime(hour: number, minute: number) {
            this.input.setValue(this.formatTime(hour, minute));
            this.popup.setSelectedTime(hour, minute, true);
        }

        getSelectedTime(): {hour: number; minute: number} {
            return this.popup.getSelectedTime();
        }

        onSelectedTimeChanged(listener: (hours: number, minutes: number) => void) {
            this.popup.onSelectedTimeChanged(listener);
        }

        unSelectedTimeChanged(listener: (hours: number, minutes: number) => void) {
            this.popup.unSelectedTimeChanged(listener);
        }

        formatTime(hours: number, minutes: number): string {
            return this.popup.isHoursValid(hours) && this.popup.isMinutesValid(minutes) ?
                   this.popup.padNumber(hours, 2) + ':' + this.popup.padNumber(minutes, 2) :
                   '';
        }
    }
}
