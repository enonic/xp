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

        public static newUTCDate(year: number, month: number, date: number) {

            return new Date(Date.UTC(year, month, date));
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

    }

}
