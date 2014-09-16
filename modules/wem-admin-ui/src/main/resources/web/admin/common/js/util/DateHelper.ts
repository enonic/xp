module api.util {

    export class DateHelper {

        public static isInvalidDate(value: Date): boolean {
            return isNaN(value.getTime());
        }

        public static getTZOffset(): number {
            return (new Date().getTimezoneOffset() / 60) * -1;
        }

        public static parseUTCTime(localTime: string): string {
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

        public static parseUTCDateTime(value: string): Date {

            var parsedYear: number = Number(value.substring(0, 4));
            var parsedMonth: number = Number(value.substring(5, 7));
            var parsedDayOfMonth: number = Number(value.substring(8, 10));
            var parsedHours: number = Number(value.substring(11, 13));
            var parsedMinutes: number = Number(value.substring(14, 16));
            var parsedSeconds: number = Number(value.substring(17, 19));
            return api.util.DateHelper.newUTCDateTime(parsedYear, parsedMonth - 1, parsedDayOfMonth, parsedHours, parsedMinutes,
                parsedSeconds);
        }

        public static newUTCDate(year: number, month: number, date: number) {

            return new Date(Date.UTC(year, month, date));
        }

        public static newUTCDateTime(year: number, month: number, date: number, hours: number, minutes: number, seconds: number = 0) {

            return new Date(Date.UTC(year, month, date, hours, minutes, seconds));
        }

        public static formatUTCDate(date: Date): string {
            var yearAsString = "" + date.getUTCFullYear();
            return yearAsString + "-" + this.padNumber(date.getUTCMonth() + 1) + "-" + this.padNumber(date.getUTCDate());
        }

        private static padNumber(num: number): string {
            return (num < 10 ? '0' : '') + num;
        }

        public static formatUTCDateTime(date: Date): string {
            var dateAsString = DateHelper.formatUTCDate(date);
            return dateAsString + "T" + this.padNumber(date.getUTCHours()) + ":" + this.padNumber(date.getUTCMinutes()) + ":" +
                   this.padNumber(date.getUTCSeconds());
        }

    }

}
