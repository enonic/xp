module api.ui.time {

    export class MonthsOfYear {

        public static JANUARY: MonthOfYear = new MonthOfYear(0, "J", "Jan", "January");
        public static FEBRUARY: MonthOfYear = new MonthOfYear(1, "F", "Feb", "February");
        public static MARCH: MonthOfYear = new MonthOfYear(2, "M", "Mar", "March");
        public static APRIL: MonthOfYear = new MonthOfYear(3, "A", "Apr", "April");
        public static MAY: MonthOfYear = new MonthOfYear(4, "M", "May", "May");
        public static JUNE: MonthOfYear = new MonthOfYear(5, "J", "Jun", "June");
        public static JULY: MonthOfYear = new MonthOfYear(6, "J", "Jul", "July");
        public static AUGUST: MonthOfYear = new MonthOfYear(7, "A", "Aug", "August");
        public static SEPTEMBER: MonthOfYear = new MonthOfYear(8, "S", "Sep", "September");
        public static OCTOBER: MonthOfYear = new MonthOfYear(9, "O", "Oct", "October");
        public static NOVEMBER: MonthOfYear = new MonthOfYear(10, "N", "Nov", "November");
        public static DECEMBER: MonthOfYear = new MonthOfYear(11, "D", "Dec", "December");

        private static monthsByCode: {[key:number]:MonthOfYear} = {
            0: MonthsOfYear.JANUARY,
            1: MonthsOfYear.FEBRUARY,
            2: MonthsOfYear.MARCH,
            3: MonthsOfYear.APRIL,
            4: MonthsOfYear.MAY,
            5: MonthsOfYear.JUNE,
            6: MonthsOfYear.JULY,
            7: MonthsOfYear.AUGUST,
            8: MonthsOfYear.SEPTEMBER,
            9: MonthsOfYear.OCTOBER,
            10: MonthsOfYear.NOVEMBER,
            11: MonthsOfYear.DECEMBER
        };

        public static getByNumberCode(code: number): MonthOfYear {
            return MonthsOfYear.monthsByCode[code];
        }
    }
}
