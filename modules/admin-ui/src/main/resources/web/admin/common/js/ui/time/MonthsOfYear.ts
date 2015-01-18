module api.ui.time {

    export class MonthsOfYear {

        public static JANUARY = new MonthOfYear(0, "J", "Jan", "January");
        public static FEBRUARY = new MonthOfYear(1, "F", "Feb", "February");
        public static MARCH = new MonthOfYear(2, "M", "Mar", "March");
        public static APRIL = new MonthOfYear(3, "A", "Apr", "April");
        public static MAY = new MonthOfYear(4, "M", "May", "May");
        public static JUNE = new MonthOfYear(5, "J", "Jun", "June");
        public static JULY = new MonthOfYear(6, "J", "Jul", "July");
        public static AUGUST = new MonthOfYear(7, "A", "Aug", "August");
        public static SEPTEMBER = new MonthOfYear(8, "S", "Sep", "September");
        public static OCTOBER = new MonthOfYear(9, "O", "Oct", "October");
        public static NOVEMBER = new MonthOfYear(10, "N", "Nov", "November");
        public static DECEMBER = new MonthOfYear(11, "D", "Dec", "December");

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
