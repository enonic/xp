module api.util {

    export class DateHelper {

        public static isInvalidDate(value: Date): boolean {
            return isNaN(value.getTime());
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
            var monthAsString = "" + (date.getUTCMonth() + 1);
            if (monthAsString.length == 1) {
                monthAsString = "0" + monthAsString;
            }
            var dateAsString = "" + date.getUTCDate();
            if (dateAsString.length == 1) {
                dateAsString = "0" + dateAsString;
            }
            return yearAsString + "-" + monthAsString + "-" + dateAsString;
        }

        public static formatUTCDateTime(date: Date): string {
            var dateAsString = DateHelper.formatUTCDate(date);
            var hoursAsString = "" + date.getUTCHours();
            if (hoursAsString.length == 1) {
                hoursAsString = "0" + hoursAsString;
            }
            var minutesAsString = "" + date.getUTCMinutes();
            if (minutesAsString.length == 1) {
                minutesAsString = "0" + minutesAsString;
            }
            var secondsAsString = "" + date.getUTCSeconds();
            if (secondsAsString.length == 1) {
                secondsAsString = "0" + secondsAsString;
            }
            return dateAsString + "T" + hoursAsString + ":" + minutesAsString + ":" + secondsAsString;
        }

    }

}
