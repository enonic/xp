import CalendarBuilder = api.ui.time.CalendarBuilder;

describe("api.ui.time.CalendarTest", () => {

    it("test calendar", () => {

        var calendar = new CalendarBuilder().setYear(2014).setMonth(7).build();

        expect(calendar.getCalendarDays().length).toBe(31);
    });
});
