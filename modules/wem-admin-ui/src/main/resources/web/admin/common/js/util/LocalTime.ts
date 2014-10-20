module api.util {

    export class LocalTime implements api.Equitable {

        private hours: number;

        private minutes: number;

        private seconds: number;

        private tzo: number;

        constructor(hours: number, minutes: number, tzo: number, seconds?: number) {
            this.hours = hours;
            this.minutes = minutes;
            this.tzo = tzo;
            if (seconds) {
                this.seconds = seconds;
            }
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

        getTZO(): number {
            return this.tzo;
        }

        toString(): string {
            if (this.seconds) {
                return this.padNumber(this.hours) + ":" + this.padNumber(this.minutes) + ":" + this.padNumber(this.seconds);
            }
            else {
                return this.padNumber(this.hours) + ":" + this.padNumber(this.minutes);
            }
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, LocalTime)) {
                return false;
            }

            var other = <LocalTime>o;

            if (!api.ObjectHelper.numberEquals(this.hours, other.hours)) {
                return false;
            }
            if (!api.ObjectHelper.numberEquals(this.minutes, other.minutes)) {
                return false;
            }
            if (!api.ObjectHelper.numberEquals(this.seconds, other.seconds)) {
                return false;
            }
            if (!api.ObjectHelper.numberEquals(this.tzo, other.tzo)) {
                return false;
            }
            return true;
        }

        private  padNumber(num: number): string {
            return (num < 10 ? '0' : '') + num;
        }

        static isValidString(s: string): boolean {
            if (StringHelper.isBlank(s)) {
                return false;
            }
            var re = /^[0-2]?\d:[0-5]?\d$/;
            return re.test(s);
        }

        static fromString(s: string): LocalTime {
            if (!LocalTime.isValidString(s)) {
                throw new Error("Cannot parse LocalTime from string: " + s);
            }
            var tzo = api.util.DateHelper.getTZOffset();
            var localTime: string[] = s.split(':');
            var hours = Number(localTime[0]);
            var minutes = Number(localTime[1]);
            return new LocalTime(hours, minutes, tzo);


        }

        public  getAdjustedTime(): {hour: number; minute: number} {
            var date = new Date();
            date.setHours(this.getHours() + DateHelper.getTZOffset(), this.getMinutes());
            return {
                hour: date.getHours(),
                minute: date.getMinutes()
            }
        }

    }
}