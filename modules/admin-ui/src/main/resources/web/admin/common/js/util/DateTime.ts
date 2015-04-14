module api.util {

    import StringHelper = api.util.StringHelper;

    export class DateTime implements api.Equitable {

        private static DATE_TIME_SEPARATOR: string = "T";

        private static DATE_SEPARATOR: string = "-";

        private static TIME_SEPARATOR: string = ":";

        private static FRACTION_SEPARATOR: string = ".";

        private static TIMEZONE_SEPARATOR: string = "+";

        private static DEFAULT_TIMEZONE: string = "+00:00";

        private year: number;

        private month: number; // 0-11

        private day: number;

        private hours: number;

        private minutes: number;

        private seconds: number;

        private fractions: number;

        private timezone: Timezone;

        constructor(builder: DateTimeBuilder) {
            this.year = builder.year;
            this.month = builder.month;
            this.day = builder.day;
            this.hours = builder.hours;
            this.minutes = builder.minutes;
            this.seconds = builder.seconds;
            this.fractions = builder.fractions;
            this.timezone = builder.timezone;
        }

        getYear(): number {
            return this.year;
        }

        getMonth(): number {
            return this.month;
        }

        getDay(): number {
            return this.day;
        }

        getHours(): number {
            return this.hours;
        }

        getMinutes(): number {
            return this.minutes;
        }

        getSeconds(): number {
            return this.seconds || 0;
        }

        getFractions(): number {
            return this.fractions || 0;
        }

        getTimezone(): api.util.Timezone {
            return this.timezone;
        }

        dateToString(): string {
            return this.year + DateTime.DATE_SEPARATOR + this.padNumber(this.month + 1) + DateTime.DATE_SEPARATOR + this.padNumber(this.day);
        }

        timeToString(): string {
            var fractions = this.fractions ? DateTime.FRACTION_SEPARATOR + this.padNumber(this.fractions, 3) : StringHelper.EMPTY_STRING;

            return this.padNumber(this.hours) + DateTime.TIME_SEPARATOR + this.padNumber(this.minutes) + DateTime.TIME_SEPARATOR + this.padNumber(this.seconds ? this.seconds : 0) + fractions;
        }

        /** Returns date in ISO format. Month value is incremented because ISO month range is 1-12, whereas JS Date month range is 0-11 */
        toString(): string {
            return this.dateToString() + DateTime.DATE_TIME_SEPARATOR + this.timeToString() + (this.timezone ?  DateTime.TIMEZONE_SEPARATOR + this.timezone.toString() : DateTime.DEFAULT_TIMEZONE);
        }

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, DateTime)) {
                return false;
            }

            var other = <DateTime>o;

            if (!api.ObjectHelper.stringEquals(this.toString(), other.toString())) {
                return false;
            }

            return true;
        }

        toDate(): Date {
            return DateHelper.parseLongDateTime(DateTime.trimTZ(this.toString()), DateTime.DATE_TIME_SEPARATOR, DateTime.DATE_SEPARATOR, DateTime.TIME_SEPARATOR, DateTime.FRACTION_SEPARATOR);
        }

        private padNumber(num: number, length: number = 2): string {
            var numAsString = String(num);

            while (numAsString.length < length){
                numAsString = "0" + numAsString;
            }

            return numAsString;
        }

        static isValidDateTime(s: string): boolean {
            if (StringHelper.isBlank(s)) {
                return false;
            }
            //matches 2015-02-29T12:05 or 2015-02-29T12:05:59 or 2015-02-29T12:05:59Z or 2015-02-29T12:05:59+01:00 or 2015-02-29T12:05:59.001+01:00
            var re = /^(\d{2}|\d{4})(?:\-)?([0]{1}\d{1}|[1]{1}[0-2]{1})(?:\-)?([0-2]{1}\d{1}|[3]{1}[0-1]{1})(T)([0-1]{1}\d{1}|[2]{1}[0-3]{1})(?::)?([0-5]{1}\d{1})((:[0-5]{1}\d{1})(\.\d{3})?)?((\+|\-)([0-1]{1}\d{1}|[2]{1}[0-3]{1})(:)([0-5]{1}\d{1})|(z|Z)|$)$/;
            return re.test(s);
        }

        /**
         * Parsed passed string into DateTime object
         * @param s - date to parse in ISO format
         * @returns {DateTime}
         */
        static fromString(s: string): DateTime {
            if (!DateTime.isValidDateTime(s)) {
                throw new Error("Cannot parse DateTime from string: " + s);
            }

            var date, timezone;

            if(DateHelper.isUTCdate(s)) {
                date = DateHelper.parseUTCDateTime(s);
                timezone = Timezone.getLocalTimezone();
            } else {
                var withoutTZ = DateTime.trimTZ(s);
                date = DateHelper.parseLongDateTime(withoutTZ, DateTime.DATE_TIME_SEPARATOR, DateTime.DATE_SEPARATOR, DateTime.TIME_SEPARATOR, DateTime.FRACTION_SEPARATOR);
                var offset = DateHelper.parseOffset(s);
                if(offset != null) {
                    timezone = Timezone.fromOffset(offset);
                } else {
                    // assume that if passed date string is not in UTC format and does not contain explicit offset (like '2015-02-29T12:05:59') - use zero offset timezone
                    timezone = Timezone.getZeroOffsetTimezone();
                }
            }

            if (!date) {
                throw new Error("Cannot parse DateTime from string: " + s);
            }

            return DateTime.create()
                .setYear(date.getFullYear())
                .setMonth(date.getMonth())
                .setDay(date.getDate())
                .setHours(date.getHours())
                .setMinutes(date.getMinutes())
                .setSeconds(date.getSeconds())
                .setFractions(date.getMilliseconds())
                .setTimezone(timezone)
                .build();
         }

        static fromDate(s: Date): DateTime {
            return DateTime.create()
                .setYear(s.getFullYear())
                .setMonth(s.getMonth())
                .setDay(s.getDate())
                .setHours(s.getHours())
                .setMinutes(s.getMinutes())
                .setSeconds(s.getSeconds())
                .setFractions(s.getMilliseconds())
                .setTimezone(Timezone.fromOffset(DateHelper.getTZOffsetFromDate(s))) // replace with timezone picker value
                .build();
        }

        public static create(): DateTimeBuilder {
            return new DateTimeBuilder();
        }

        private static trimTZ(dateString : string): string {
            var tzStartIndex = dateString.indexOf("+");
            if(tzStartIndex > 0) {
                return dateString.substr(0, tzStartIndex);
            } else {
                tzStartIndex = dateString.toLowerCase().indexOf("z");
                if(tzStartIndex > 0)
                    return dateString.substr(0, tzStartIndex);
            }
            return dateString;
        }
    }


    class DateTimeBuilder {

        year: number;

        month: number;

        day: number;

        hours: number;

        minutes: number;

        seconds: number;

        fractions: number;

        timezone: Timezone;

        public setYear(value: number): DateTimeBuilder {
            this.year = value;
            return this;
        }

        public setMonth(value: number): DateTimeBuilder {
            this.month = value;
            return this;
        }

        public setDay(value: number): DateTimeBuilder {
            this.day = value;
            return this;
        }

        public setHours(value: number): DateTimeBuilder {
            this.hours = value;
            return this;
        }

        public setMinutes(value: number): DateTimeBuilder {
            this.minutes = value;
            return this;
        }

        public setSeconds(value: number): DateTimeBuilder {
            this.seconds = value;
            return this;
        }

        public setFractions(value: number): DateTimeBuilder {
            if (this.seconds && value > 0) this.fractions = value;
            return this;
        }

        public setTimezone(value: Timezone): DateTimeBuilder {
            this.timezone = value;
            return this;
        }

        public build(): DateTime {
            return new DateTime(this);
        }
    }
}