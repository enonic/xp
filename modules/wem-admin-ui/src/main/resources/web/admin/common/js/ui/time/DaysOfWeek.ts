module api.ui.time {

    export class DaysOfWeek {

        public static MONDAY: DayOfWeek = new DayOfWeek(1, "M", "Mon", "Monday");

        public static TUESDAY: DayOfWeek = new DayOfWeek(2, "T", "Tue", "Tuesday");

        public static WEDNESDAY: DayOfWeek = new DayOfWeek(3, "W", "Wed", "Wednesday");

        public static THURSDAY: DayOfWeek = new DayOfWeek(4, "T", "Thu", "Thursday");

        public static FRIDAY: DayOfWeek = new DayOfWeek(5, "F", "Fri", "Friday");

        public static SATURDAY: DayOfWeek = new DayOfWeek(6, "S", "Sat", "Saturday");

        public static SUNDAY: DayOfWeek = new DayOfWeek(0, "S", "Sun", "Sunday");

        private static ALL: DayOfWeek[] = [DaysOfWeek.MONDAY, DaysOfWeek.TUESDAY, DaysOfWeek.WEDNESDAY, DaysOfWeek.THURSDAY,
            DaysOfWeek.FRIDAY, DaysOfWeek.SATURDAY, DaysOfWeek.SUNDAY];

        public static getByNumberCode(value: number) {

            var match: DayOfWeek = null;
            DaysOfWeek.ALL.forEach((dayOfWeek: DayOfWeek) => {
                if (dayOfWeek.getNumberCode() == value) {
                    match = dayOfWeek;
                }
            });
            return match;
        }
    }
}
