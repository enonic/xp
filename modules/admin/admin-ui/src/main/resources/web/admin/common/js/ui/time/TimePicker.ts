module api.ui.time {

    export class TimePickerBuilder {

        hours: number;

        minutes: number;

        closeOnOutsideClick: boolean = true;

        setHours(value: number): TimePickerBuilder {
            this.hours = value;
            return this;
        }

        setMinutes(value: number): TimePickerBuilder {
            this.minutes = value;
            return this;
        }

        setCloseOnOutsideClick(value: boolean): TimePickerBuilder {
            this.closeOnOutsideClick = value;
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
            var value;
            if (builder.hours || builder.minutes) {
                value = this.formatTime(builder.hours, builder.minutes);
            }

            this.input = api.ui.text.TextInput.middle(undefined, value);
            this.input.onClicked((e: MouseEvent) => {
                e.preventDefault();
                this.togglePopupVisibility();
            });

            this.input.onKeyUp((event: KeyboardEvent) => {
                var typedTime = this.input.getValue();
                if (api.util.StringHelper.isEmpty(typedTime)) {
                    this.validUserInput = true;
                    this.popup.setSelectedTime(null, null);
                    if (this.popup.isVisible()) {
                        this.popup.hide();
                    }
                } else {
                    var parsedTime = typedTime.match(/^[0-2][0-9]:[0-5][0-9]$/);
                    if (parsedTime && parsedTime.length == 1) {
                        var splitTime = parsedTime[0].split(':');
                        this.validUserInput = true;
                        this.popup.setSelectedTime(parseInt(splitTime[0]), parseInt(splitTime[1]));
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

        protected initPopupTrigger() {
            this.popupTrigger = new api.ui.button.Button();
            this.popupTrigger.addClass('icon-clock');
        }

        protected setupListeners(builder: TimePickerBuilder) {

            if (builder.closeOnOutsideClick) {
                api.util.AppHelper.focusInOut(this, () => {
                    this.popup.hide();
                }, 50, false);

                // Prevent focus loss on mouse down
                this.popup.onMouseDown((event: MouseEvent) => {
                    event.preventDefault();
                });
            }

            this.popupTrigger.onClicked((e: MouseEvent) => {
                e.preventDefault();
                this.togglePopupVisibility();
            });

            this.popup.onSelectedTimeChanged((hours: number, minutes: number) => {
                if (hours != null && minutes != null) {
                    this.input.setValue(this.formatTime(hours, minutes), false, true);
                    this.validUserInput = true;
                }

                this.updateInputStyling();
            });

            this.popup.onKeyDown((event: KeyboardEvent) => {
                if (api.ui.KeyHelper.isTabKey(event)) {
                    if (!(document.activeElement == this.input.getEl().getHTMLElement())) {
                        event.preventDefault();
                        event.stopPropagation();
                        this.popup.hide();
                        this.popupTrigger.giveFocus();
                    }
                }
            });

            this.input.onKeyDown((event: KeyboardEvent) => {
                if (api.ui.KeyHelper.isTabKey(event)) { // handles tab navigation events on date input
                    if (!event.shiftKey) {
                        event.preventDefault();
                        event.stopPropagation();
                        this.popupTrigger.giveFocus();
                    } else {
                        this.popup.hide();
                    }
                }
            });

            this.popupTrigger.onKeyDown((event: KeyboardEvent) => {
                if (api.ui.KeyHelper.isTabKey(event)) {
                    this.popup.hide();
                }
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
                   "";
        }
    }
}