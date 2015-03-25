module api.util {

    export class LocalDateTime implements api.Equitable {

        static dateTimeSeparator: string = "T";

        static dateSeparator: string = "-";

        static timeSeparator: string = ":";

        static fractionsSeparator: string = ".";

        private year: number;

        private month: number;

        private day: number;

        private hours: number;

        private minutes: number;

        private seconds: number;

        private fractions: number;

        constructor(year: number, month: number, day: number, hours: number, minutes: number, seconds?: number, fractions?: number) {
            this.year = year;
            this.month = month;
            this.day = day;
            this.hours = hours;
            this.minutes = minutes;
            if (seconds) {
                this.seconds = seconds;
                if (fractions && fractions !== 0) {
                    this.fractions = fractions;
                }
            }
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
            return this.seconds;
        }

        getFractions(): number {
            return this.fractions;
        }

        dateToString(): string {
            return this.year + LocalDateTime.dateSeparator + this.padNumber(this.month) + LocalDateTime.dateSeparator + this.padNumber(this.day);
        }

        timeToString(): string {
            var seconds = this.seconds ? LocalDateTime.timeSeparator + this.padNumber(this.seconds) : StringHelper.EMPTY_STRING;
            var fractions = this.fractions ? LocalDateTime.fractionsSeparator + this.padNumber(this.fractions, 3) : StringHelper.EMPTY_STRING;

            return this.padNumber(this.hours) + LocalDateTime.timeSeparator + this.padNumber(this.minutes) + seconds + fractions;
        }

        toString(): string {
            return this.dateToString() + LocalDateTime.dateTimeSeparator + this.timeToString();
        }

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, LocalDateTime)) {
                return false;
            }

            var other = <LocalDateTime>o;

            if (!api.ObjectHelper.stringEquals(this.toString(), other.toString())) {
                return false;
            }

            return true;
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

            var re = /^(\d{2}|\d{4})(?:\-)?([0]{1}\d{1}|[1]{1}[0-2]{1})(?:\-)?([0-2]{1}\d{1}|[3]{1}[0-1]{1})(T)([0-1]{1}\d{1}|[2]{1}[0-3]{1})(?::)?([0-5]{1}\d{1})(?::)?([0-5]{1}\d{1})?(?:.)?(\d{3})?/;
            return re.test(s);
        }

        static parseDate(s: string): {
            year: number;
            month: number;
            day: number
        } {
            var dateTimeArr: string[] = s.split(LocalDateTime.dateTimeSeparator);
            var date: string[] = dateTimeArr[0].split(LocalDateTime.dateSeparator);

            return {
                year: Number(date[0]),
                month: Number(date[1]),
                day: Number(date[2])
            };
        }

        static parseTime(s: string): {
            hours: number;
            minutes: number;
            seconds?: number;
            fractions?: number;
        } {
            var dateTimeArr: string[] = s.split(LocalDateTime.dateTimeSeparator);
            var time: string[] = dateTimeArr[1].split(LocalDateTime.timeSeparator);

            var seconds: number;
            var fractions: number;

            if (time[2]) {
                var secondArr: string[] = time[2].split(LocalDateTime.fractionsSeparator);
                seconds = Number(secondArr[0]);
                if (secondArr[1]) {
                    fractions = Number(secondArr[1]);
                }
            }

            return {
                hours: Number(time[0]),
                minutes: Number(time[1]),
                seconds: seconds,
                fractions: fractions
            };
        }

        static fromString(s: string): LocalDateTime {
            if (!LocalDateTime.isValidDateTime(s)) {
                throw new Error("Cannot parse LocalDateTime from string: " + s);
            }

            var date = LocalDateTime.parseDate(s);
            var time = LocalDateTime.parseTime(s);

            return new LocalDateTime(date.year, date.month, date.day, time.hours, time.minutes, time.seconds, time.fractions);
        }
    }
}