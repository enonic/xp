module api.util {

    export function parseDate(str: string): Date {
        return new Date(Date.parse(str));
    }

    export class DateHelper {

        public static formUtcDate(date: Date = new Date): string {
            return date.getFullYear() + "-" +
                   (date.getMonth() < 9 ? "0" : "") + (date.getMonth() + 1) + "-" +
                   (date.getDate() < 10 ? "0" : "") + date.getDate() + " " +
                   (date.getHours() < 10 ? "0" : "") + date.getHours() + ":" +
                   (date.getMinutes() < 10 ? "0" : "") + date.getMinutes() + ":" +
                   (date.getSeconds() < 10 ? "0" : "") + date.getSeconds() + " " +
                   (/(GMT[-,+]\d*)/g).exec(date.toString())[0];
        }

    }

}
