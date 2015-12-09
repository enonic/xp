module api.util {

    export class DateHelper {

        public static isInvalidDate(value: Date): boolean {
            return isNaN(value.getTime());
        }

        public static getTZOffset(): number {
            return (new Date().getTimezoneOffset() / 60) * -1;
        }

        public static getTZOffsetFromDate(s: Date): number {
            return (s.getTimezoneOffset() / 60) * -1;
        }

        /**
         * Parses passed string to UTC time string - local timezone will add a time shift to result.
         * @param localTime
         * @returns {string}
         */
        public static parseTimeToUTC(localTime: string): string {
            var values = localTime.split(':');
            var date = new Date();
            var localHours = Number(values[0]);
            if (values.length == 3) {
                date.setHours(localHours, Number(values[1]), Number(values[2]));
            } else {
                date.setHours(localHours, Number(values[1]));
            }

            var hoursAsString = "" + date.getUTCHours();
            var minutesAsString = "" + date.getUTCMinutes();
            return hoursAsString + ":" + minutesAsString;
        }

        public static parseUTCDate(value: string): Date {

            var parsedYear: number = Number(value.substring(0, 4));
            var parsedMonth: number = Number(value.substring(5, 7));
            var parsedDayOfMonth: number = Number(value.substring(8, 10));
            return api.util.DateHelper.newUTCDate(parsedYear, parsedMonth - 1, parsedDayOfMonth);
        }

        /**
         * Parses passed UTC string into Date object.
         * @param value
         * @param ignoreLocalTimezone - local time zone will be ignored if this parameter is true.
         * @returns {Date}
         */
        public static parseUTCDateTime(value: string, ignoreLocalTimezone: boolean = false): Date {

            var parsedYear: number = Number(value.substring(0, 4));
            var parsedMonth: number = Number(value.substring(5, 7));
            var parsedDayOfMonth: number = Number(value.substring(8, 10));
            var parsedHours: number = Number(value.substring(11, 13));
            var parsedMinutes: number = Number(value.substring(14, 16));
            var parsedSeconds: number = Number(value.substring(17, 19));
            if (ignoreLocalTimezone) {
                return DateHelper.newDateTime(parsedYear, parsedMonth - 1, parsedDayOfMonth, parsedHours, parsedMinutes,
                    parsedSeconds);
            } else {
                return DateHelper.newUTCDateTime(parsedYear, parsedMonth - 1, parsedDayOfMonth, parsedHours, parsedMinutes,
                    parsedSeconds);
            }
        }

        public static newUTCDate(year: number, month: number, date: number) {

            return new Date(Date.UTC(year, month, date));
        }

        public static newUTCDateTime(year: number, month: number, date: number, hours: number, minutes: number, seconds: number = 0) {

            return new Date(Date.UTC(year, month, date, hours, minutes, seconds));
        }

        /**
         * Creates Date object from passes values. Method does not take in account local timezone unlike newUTCDateTime() method.
         * @returns {Date}
         */
        public static newDateTime(year: number, month: number, date: number, hours: number, minutes: number, seconds: number = 0) {
            return new Date(year, month, date, hours, minutes, seconds);
        }

        public static formatUTCDate(date: Date): string {
            var yearAsString = "" + date.getUTCFullYear();
            return yearAsString + "-" + this.padNumber(date.getUTCMonth() + 1) + "-" + this.padNumber(date.getUTCDate());
        }

        /**
         * Formats date part of passed date object. Returns string like 2010-01-01
         * @param date
         * @returns {string}
         */
        public static formatDate(date: Date): string {
            var yearAsString = "" + date.getFullYear();
            return yearAsString + "-" + this.padNumber(date.getMonth() + 1) + "-" + this.padNumber(date.getDate());
        }

        /**
         * Formats time part of passed date object. Returns string like 10:55:00
         * @param date
         * @param includeSeconds
         * @returns {string}
         */
        public static formatTime(date: Date, includeSeconds: boolean = true): string {
            return this.padNumber(date.getHours()) + ":" + this.padNumber(date.getMinutes()) +
                   (includeSeconds ? ":" + this.padNumber(date.getSeconds()) : "");
        }

        private static padNumber(num: number): string {
            return (num < 10 ? '0' : '') + num;
        }

        public static formatUTCDateTime(date: Date): string {
            var dateAsString = DateHelper.formatUTCDate(date);
            return dateAsString + "T" + this.padNumber(date.getUTCHours()) + ":" + this.padNumber(date.getUTCMinutes()) + ":" +
                   this.padNumber(date.getUTCSeconds());
        }

        /**
         * Formats passed date as date string, like 2010-01-01T10:55:00
         * @param date
         * @returns {string}
         */
        public static formatDateTime(date: Date): string {
            var dateAsString = DateHelper.formatDate(date);
            var timeAsString = DateHelper.formatTime(date);
            return dateAsString + "T" + timeAsString;
        }

        public static formatUTCDateTimeForInstant(date: Date): string {
            return this.formatUTCDateTime(date) + "Z";
        }

        public static parseDate(value: string, dateSeparator: string = "-", forceDaysBeOfTwoChars: boolean = false): Date {
            var dateStr = (value || '').trim();
            if (dateStr.length < 8 || dateStr.length > 10) {
                return null;
            }
            var parts = dateStr.split(dateSeparator);
            if (parts.length !== 3 || parts[0].length !== 4) {
                return null;
            }

            if (forceDaysBeOfTwoChars && parts[2].length != 2) {
                return null;
            }

            var parsedYear: number = Number(parts[0]);
            var parsedMonth: number = Number(parts[1]);
            var parsedDayOfMonth: number = Number(parts[2]);

            var date = new Date(parsedYear, parsedMonth - 1, parsedDayOfMonth);
            return date.getFullYear() === parsedYear && date.getMonth() === (parsedMonth - 1) && date.getDate() === parsedDayOfMonth
                ? date
                : null;
        }

        /**
         * Parses passed value to Time object. Expected format is only 5 chars like "12:10"
         * @param value
         * @returns {*}
         */
        private static parseTime(value: string): Time {
            var dateStr = (value || '').trim();
            if (dateStr.length != 5) {
                return null;
            }
            var parts = dateStr.split(':');
            if (parts.length !== 2) {
                return null;
            }
            var hour: number = Number(parts[0]);
            var minute: number = Number(parts[1]);
            if (isNaN(hour) || isNaN(minute) || hour < 0 || hour > 23 || minute < 0 || minute > 59) {
                return null;
            }
            return {hour: hour, minute: minute};
        }

        /**
         * Parses passed value into Date object. Expected length is 14-16 chars, value should be like "2015-04-17 06:00"
         * @param value
         * @returns {*}
         */
        static parseDateTime(value: string): Date {
            var dateStr = (value || '').trim();
            if (dateStr.length < 14 || dateStr.length > 16) {
                return null;
            }
            var parts = dateStr.split(' ');
            if (parts.length !== 2) {
                return null;
            }
            var datePart = parts[0];
            var timePart = parts[1];
            var date = DateHelper.parseDate(datePart);
            if (!date) {
                return null;
            }
            var time = DateHelper.parseTime(timePart);
            if (!time) {
                return null;
            }
            date.setHours(time.hour, time.minute, 0, 0);
            return date;
        }

        private static parseLongTime(value: string, timeSeparator: string = ":", fractionSeparator: string = "."): LongTime {
            var timeStr = (value || '').trim();
            if (timeStr.length < 5 || timeStr.length > 12) {
                return null;
            }
            var time: string[] = timeStr.split(timeSeparator);

            if (time.length < 2 || time.length > 3) {
                return null;
            }

            var hours = Number(time[0]);
            var minutes = Number(time[1]);
            var seconds: number = 0;
            var fractions: number = 0;

            if (time[2]) {
                var secondArr: string[] = time[2].split(fractionSeparator);
                seconds = Number(secondArr[0]);
                if (secondArr[1]) {
                    fractions = Number(secondArr[1]);
                }
            }

            if (isNaN(hours) || isNaN(minutes) || isNaN(seconds) || isNaN(fractions) ||
                hours < 0 || hours > 23 || minutes < 0 || minutes > 59 || seconds < 0 || seconds > 59 || fractions < 0) {
                return null;
            }

            return {
                hours: hours,
                minutes: minutes,
                seconds: seconds,
                fractions: fractions
            };
        }

        /**
         * Parses passed string with use of given separators. Passed string should not contain timezone.
         * @param value
         * @param dateTimeSeparator
         * @param dateSeparator
         * @param timeSeparator
         * @param fractionSeparator
         * @returns {*}
         */
        static parseLongDateTime(value: string, dateTimeSeparator: string = "-", dateSeparator: string = "-", timeSeparator: string = ":",
                                 fractionSeparator: string = "."): Date {
            var dateStr = (value || '').trim();

            var parts = dateStr.split(dateTimeSeparator);
            if (parts.length !== 2) {
                return null;
            }
            var datePart = parts[0];
            var timePart = parts[1];

            var date = DateHelper.parseDate(datePart, dateSeparator);
            if (!date) {
                return null;
            }
            var time = DateHelper.parseLongTime(timePart, timeSeparator, fractionSeparator);
            if (!time) {
                return null;
            }
            date.setHours(time.hours, time.minutes, time.seconds, time.fractions);

            return date;
        }

        /**
         * Returns true if passed string ends with 'z'
         * @param value
         * @returns {number}
         */
        static isUTCdate(value: string): boolean {
            if (value != null && (value[value.length - 1] == "Z" || value[value.length - 1] == "z")) {
                return true;
            }
            return false;
        }

        /**
         * E.g. numDaysInMonth(2015, 1) -> 28
         * @param year
         * @param month 0 based month number of the year. 0 == January , 11 == December
         * @returns {number}
         */
        static numDaysInMonth(year: number, month: number): number {
            return new Date(year, month + 1, 0).getDate();
        }
    }

    interface Time {
        hour: number;
        minute: number;
    }

    interface LongTime {
        hours: number;
        minutes: number;
        seconds: number;
        fractions: number;
    }
}