import CalendarDayBuilder = api.ui.time.CalendarDayBuilder;
import DaysOfWeek = api.ui.time.DaysOfWeek;

describe('api.ui.time.CalendarDay', () => {

    describe('when getDayOfWeek', () => {

        it('given date 2014-01-01 then WEDNESDAY should be returned', () => {

            let calendarDay = new CalendarDayBuilder().
                setDate(new Date(Date.UTC(2014, 0, 1))).
                setMonth(0).
                build();

            expect(calendarDay.getDayOfWeek()).toBe(DaysOfWeek.WEDNESDAY);
        });

        it('given date 2014-12-31 then WEDNESDAY should be returned', () => {

            let calendarDay = new CalendarDayBuilder().
                setDate(new Date(Date.UTC(2014, 11, 31))).
                setMonth(11).
                build();

            expect(calendarDay.getDayOfWeek()).toBe(DaysOfWeek.WEDNESDAY);
        });
    });
})
;
