describe("api.ui.time.CalendarDayTest", function () {

    var CalendarDayBuilder = api.ui.time.CalendarDayBuilder;
    var DaysOfWeek = api.ui.time.DaysOfWeek;

    describe("when getDayOfWeek", function () {

        it("given date 2014-01-01 then WEDNESDAY should be returned", function () {

            var calendarDay = new CalendarDayBuilder().
                setDate(api.util.DateHelper.newUTCDate(2014, 0, 1)).
                setMonth(0).
                build();

            expect(calendarDay.getDayOfWeek()).toBe(DaysOfWeek.WEDNESDAY);
        });

        it("given date 2014-12-31 then WEDNESDAY should be returned", function () {

            var calendarDay = new CalendarDayBuilder().
                setDate(api.util.DateHelper.newUTCDate(2014, 11, 31)).
                setMonth(11).
                build();

            expect(calendarDay.getDayOfWeek()).toBe(DaysOfWeek.WEDNESDAY);
        });
    });
})
;
