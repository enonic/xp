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

    export class TimePicker extends api.dom.DivEl {

        private popup: TimePickerPopup;

        private input: api.ui.text.TextInput;

        private popupTrigger: api.ui.button.Button;

        private validUserInput: boolean;

        constructor(builder: TimePickerBuilder) {
            super('time-picker');
            this.validUserInput = true;

            this.input = api.ui.text.TextInput.middle();
            this.input.onClicked((e: MouseEvent) => {
                e.stopPropagation();
                e.preventDefault();

                this.popup.show();
            });

            this.input.onKeyDown((event: KeyboardEvent) => {
                if (!api.ui.KeyHelper.isNumber(event) && !api.ui.KeyHelper.isBackspace(event) && !api.ui.KeyHelper.isDel(event) &&
                    !api.ui.KeyHelper.isColon(event)) {

                    event.preventDefault();
                }
            });

            this.input.onKeyUp((event: KeyboardEvent) => {
                if (api.ui.KeyHelper.isNumber(event) ||
                    api.ui.KeyHelper.isBackspace(event) ||
                                                        api.ui.KeyHelper.isDel(event) ||
                                                        api.ui.KeyHelper.isColon(event)) {

                    var typedTime = this.input.getValue();
                    if (api.util.StringHelper.isEmpty(typedTime)) {
                        this.popup.setSelectedTime(null, null);
                        this.validUserInput = true;
                        if (this.popup.isVisible()) {
                            this.popup.hide();
                        }
                    } else {
                        var parsedTime = typedTime.match(/^[0-2][0-9]:[0-5][0-9]$/);
                        if (parsedTime && parsedTime.length == 1) {
                            var splitTime = parsedTime[0].split(':');
                            this.popup.setSelectedTime(parseInt(splitTime[0]), parseInt(splitTime[1]));
                            this.validUserInput = true;
                            if (!this.popup.isVisible()) {
                                this.popup.show();
                            }
                        } else {
                            this.validUserInput = false;
                            this.popup.setSelectedTime(null, null);
                        }
                    }

                    this.updateInputStyling();
                }
            });

            var wrapper = new api.dom.DivEl('wrapper');
            wrapper.appendChild(this.input);

            var popupBuilder = new TimePickerPopupBuilder().
                setHours(builder.hours).
                setMinutes(builder.minutes).
                setCloseOnOutsideClick(builder.closeOnOutsideClick);
            this.popup = popupBuilder.build();
            wrapper.appendChild(this.popup);

            this.popupTrigger = new api.ui.button.Button();
            this.popupTrigger.addClass('icon-clock');
            wrapper.appendChild(this.popupTrigger);

            this.appendChild(wrapper);

            this.popupTrigger.onClicked((e: MouseEvent) => {
                e.stopPropagation();
                e.preventDefault();

                if (this.popup.isVisible()) {
                    this.popup.hide();
                } else {
                    this.popup.show();
                }
            });

            if (builder.hours || builder.minutes) {
                this.input.setValue(this.formatTime(builder.hours, builder.minutes));
            }

            this.popup.onSelectedTimeChanged((hours: number, minutes: number) => {
                if (hours != null && minutes != null) {
                    this.input.setValue(this.formatTime(hours, minutes));
                    this.validUserInput = true;
                }

                this.updateInputStyling();
            });

        }

        hasValidUserInput(): boolean {
            return this.validUserInput;
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
            return this.padNumber(hours, 2) + ':' + this.padNumber(minutes, 2);
        }

        private padNumber(value: number, pad: number): string {
            return Array(pad - String(value).length + 1).join('0') + value;
        }

        private updateInputStyling() {
            this.input.updateValidationStatusOnUserInput(this.validUserInput);
        }

        giveFocus(): boolean {
            return this.input.giveFocus();
        }
    }
}