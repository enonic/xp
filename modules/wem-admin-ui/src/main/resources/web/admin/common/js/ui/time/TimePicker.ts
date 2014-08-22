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

        constructor(builder: TimePickerBuilder) {
            super('time-picker');

            this.input = api.ui.text.TextInput.middle();
            this.input.onClicked((e: MouseEvent) => {
                e.stopPropagation();
                e.preventDefault();

                this.popup.show();
            });
            this.appendChild(this.input);

            this.popupTrigger = new api.ui.button.Button('Change');
            this.appendChild(this.popupTrigger);

            this.popup = new TimePickerPopup(builder);
            this.appendChild(this.popup);

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

            this.onSelectedTimeChanged((hours: number, minutes: number) => {
                this.input.setValue(this.formatTime(hours, minutes));
            });

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

        private formatTime(hours: number, minutes: number): string {
            return this.padNumber(hours, 2) + ':' + this.padNumber(minutes, 2);
        }

        private padNumber(value: number, pad: number): string {
            return Array(pad - String(value).length + 1).join('0') + value;
        }

    }

    class TimePickerPopup extends api.dom.UlEl {

        private nextHour: api.dom.AEl;
        private hour: api.dom.SpanEl;
        private prevHour: api.dom.AEl;
        private nextMinute: api.dom.AEl;
        private minute: api.dom.SpanEl;
        private prevMinute: api.dom.AEl;

        private selectedHour: number;
        private selectedMinute: number;
        private interval: number;

        private timeChangedListeners: {(hours: number, minutes: number) : void}[] = [];

        constructor(builder: TimePickerBuilder) {
            super('time-picker-dialog');

            var hourContainer = new api.dom.LiEl();
            this.appendChild(hourContainer);

            this.nextHour = new api.dom.AEl('next');
            this.nextHour.onMouseDown((e: MouseEvent) => {
                this.addHour(+1);
                this.startInterval(this.addHour, 1);
            });
            api.dom.Body.get().onMouseUp((e: MouseEvent) => {
                this.stopInterval();
            });
            this.nextHour.appendChild(new api.dom.SpanEl());
            hourContainer.appendChild(this.nextHour);

            this.hour = new api.dom.SpanEl();
            hourContainer.appendChild(this.hour);

            this.prevHour = new api.dom.AEl('prev');
            this.prevHour.onMouseDown((e: MouseEvent) => {
                this.addHour(-1);
                this.startInterval(this.addHour, -1)
            });
            this.prevHour.appendChild(new api.dom.SpanEl());
            hourContainer.appendChild(this.prevHour);

            this.appendChild(new api.dom.LiEl('colon'));

            var minuteContainer = new api.dom.LiEl();
            this.appendChild(minuteContainer);

            this.nextMinute = new api.dom.AEl('next');
            this.nextMinute.onMouseDown((e: MouseEvent) => {
                this.addMinute(+1);
                this.startInterval(this.addMinute, 1);
            });
            this.nextMinute.appendChild(new api.dom.SpanEl());
            minuteContainer.appendChild(this.nextMinute);

            this.minute = new api.dom.SpanEl();
            minuteContainer.appendChild(this.minute);

            this.prevMinute = new api.dom.AEl('prev');
            this.prevMinute.onMouseDown((e: MouseEvent) => {
                this.addMinute(-1);
                this.startInterval(this.addMinute, -1);
            });
            this.prevMinute.appendChild(new api.dom.SpanEl());
            minuteContainer.appendChild(this.prevMinute);

            this.selectedHour = builder.hours != undefined ? builder.hours : 0;
            this.selectedMinute = builder.minutes != undefined ? builder.minutes : 0;

            this.hour.setHtml(this.padNumber(this.selectedHour, 2));
            this.minute.setHtml(this.padNumber(this.selectedMinute, 2));

            if (builder.closeOnOutsideClick) {
                api.dom.Body.get().onClicked((e: MouseEvent) => this.outsideClickListener(e));
            }

        }

        getSelectedTime(): {hour: number; minute: number} {
            return {
                hour: this.selectedHour,
                minute: this.selectedMinute
            }
        }

        onSelectedTimeChanged(listener: (hours: number, minutes: number) => void) {
            this.timeChangedListeners.push(listener);
        }

        unSelectedTimeChanged(listener: (hours: number, minutes: number) => void) {
            this.timeChangedListeners = this.timeChangedListeners.filter((curr) => {
                return curr != listener;
            })
        }

        private startInterval(fn: Function, ...args: any[]) {
            var times = 0;
            var delay = 400;
            var intervalFn = () => {
                fn.apply(this, args);
                if (++times % 5 == 0 && delay > 50) {
                    // speed up after 5 occurrences but not faster than 50ms
                    this.stopInterval();
                    delay /= 2;
                    this.interval = setInterval(intervalFn, delay);
                }
            };
            this.interval = setInterval(intervalFn, delay);
        }

        private stopInterval() {
            clearInterval(this.interval);
        }


        private addHour(add: number, silent?: boolean) {
            this.selectedHour += add;
            if (this.selectedHour < 0) {
                this.selectedHour += 24;
            } else if (this.selectedHour > 23) {
                this.selectedHour -= 24;
            }

            this.hour.setHtml(this.padNumber(this.selectedHour, 2));
            if (!silent) {
                this.notifyTimeChanged(this.selectedHour, this.selectedMinute);
            }
        }

        private addMinute(add: number, silent?: boolean) {
            this.selectedMinute += add;
            if (this.selectedMinute < 0) {
                this.selectedMinute += 60
                this.addHour(-1, true);
            } else if (this.selectedMinute > 59) {
                this.selectedMinute -= 60;
                this.addHour(1, true);
            }
            this.minute.setHtml(this.padNumber(this.selectedMinute, 2));
            if (!silent) {
                this.notifyTimeChanged(this.selectedHour, this.selectedMinute);
            }
        }

        private notifyTimeChanged(hours: number, minutes: number) {
            this.timeChangedListeners.forEach((listener) => {
                listener(hours, minutes);
            });
        }

        private outsideClickListener(e: MouseEvent) {
            if (!this.getEl().contains(<HTMLElement> e.target)) {
                this.hide();
            }
        }

        private padNumber(value: number, pad: number): string {
            return Array(pad - String(value).length + 1).join('0') + value;
        }
    }

}