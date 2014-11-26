describe("api.ui.time.CalendarTest", function () {

    it("test calendar", function () {

        var calendar = new api.ui.time.CalendarBuilder().
            setYear(2014).
            setMonth(7).
            build();

        expect(calendar.getCalendarDays().length).toBe(31);
    });
});
