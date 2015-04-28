module api.util {

    export class LocalTime implements api.Equitable {

        private static TIME_SEPARATOR: string = ":";

        private hours: number;

        private minutes: number;

        private seconds: number;

        constructor(builder: LocalTimeBuilder) {
            this.hours = builder.hours;
            this.minutes = builder.minutes;
            this.seconds = builder.seconds;
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

        toString(): string {
            var strSeconds = this.seconds ? LocalTime.TIME_SEPARATOR + this.padNumber(this.seconds) : StringHelper.EMPTY_STRING;

            return this.padNumber(this.hours) + LocalTime.TIME_SEPARATOR + this.padNumber(this.minutes) + strSeconds;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, LocalTime)) {
                return false;
            }

            var other = <LocalTime>o;

            if (!api.ObjectHelper.numberEquals(this.getHours(), other.getHours())) {
                return false;
            }
            if (!api.ObjectHelper.numberEquals(this.getMinutes(), other.getMinutes())) {
                return false;
            }
            if (!api.ObjectHelper.numberEquals(this.getSeconds(), other.getSeconds())) {
                return false;
            }
            return true;
        }

        private padNumber(num: number): string {
            return (num < 10 ? '0' : '') + num;
        }

        static isValidString(s: string): boolean {
            if (StringHelper.isBlank(s)) {
                return false;
            }
            //var re = /^[0-2]?\d:[0-5]?\d$/;
            // looks for strings like '12', '1:19', '21:05', '6:7', '15:9:8', '6:59:29'
            var re = /^(\d{1}|[0-1]{1}\d{1}|[2]{1}[0-3]{1})((?::)(\d{1}|[0-5]{1}\d{1}))?((?::)(\d{1}|[0-5]{1}\d{1}))?$/;
            return re.test(s);
        }

        static fromString(s: string): LocalTime {
            if (!LocalTime.isValidString(s)) {
                throw new Error("Cannot parse LocalTime from string: " + s);
            }
            var localTime: string[] = s.split(':');
            var hours = Number(localTime[0]);
            var minutes = Number(localTime[1]);
            var seconds = localTime.length>2 ? Number(localTime[2]) : 0;

            return LocalTime.create()
                .setHours(hours)
                .setMinutes(minutes)
                .setSeconds(seconds)
                .build();
        }

        public getAdjustedTime(): {hour: number; minute: number; seconds: number} {
            var date = new Date();
            date.setHours(this.getHours(), this.getMinutes(), this.getSeconds());

            return {
                hour: date.getHours(),
                minute: date.getMinutes(),
                seconds: date.getSeconds()
            }
        }

        public static create(): LocalTimeBuilder {
            return new LocalTimeBuilder();
        }

    }

    export class LocalTimeBuilder {

        hours: number;

        minutes: number;

        seconds: number;

        public setHours(value: number): LocalTimeBuilder {
            this.hours = value;
            return this;
        }

        public setMinutes(value: number): LocalTimeBuilder {
            this.minutes = value;
            return this;
        }

        public setSeconds(value: number): LocalTimeBuilder {
            this.seconds = value;
            return this;
        }

        public build(): LocalTime {
            return new LocalTime(this);
        }
    }
}