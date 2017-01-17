import CalendarBuilder = api.ui.time.CalendarBuilder;

describe('api.ui.time.Calendar', () => {

    it('test calendar', () => {

        let calendar = new CalendarBuilder().setYear(2014).setMonth(7).build();

        expect(calendar.getCalendarDays().length).toBe(31);
    });
});
