import Timezone = api.util.Timezone;
import DateTime = api.util.DateTime;

describe("DateTime", () => {

    var dateTime, timeZone;

    describe("basic asserts", () => {

        beforeEach(() => {
            timeZone = Timezone.create().setOffset(1).build();
            dateTime =
                DateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).setTimezone(timeZone).build();
        });

        it("should create an instance", () => {
            expect(dateTime).toBeDefined();
        });

        it("getYear() should return correct year", () => {
            expect(dateTime.getYear()).toEqual(2015);
        });

        it("getMonth() should return correct month", () => {
            expect(dateTime.getMonth()).toEqual(3);
        });

        it("getDay() should return correct day", () => {
            expect(dateTime.getDay()).toEqual(25);
        });

        it("getHours() should return correct hours", () => {
            expect(dateTime.getHours()).toEqual(12);
        });

        it("getMinutes() should return correct minutes", () => {
            expect(dateTime.getMinutes()).toEqual(5);
        });

        it("getSeconds() should return 0 when seconds are not passed to constructor", () => {
            expect(dateTime.getSeconds()).toEqual(0);
        });

        it("getFractions() should return 0 when fractions are not passed to constructor", () => {
            expect(dateTime.getFractions()).toEqual(0);
        });

        it("getFractions() should return 0 when seconds are not passed to constructor", () => {
            dateTime = DateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).setFractions(256).build();

            expect(dateTime.getFractions()).toEqual(0);
        });

        it("getSeconds() should return correct seconds when passed to constructor", () => {
            dateTime = DateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).setSeconds(37).build();

            expect(dateTime.getSeconds()).toEqual(37);
        });

        it("getFractions() should return correct value when both seconds and fractions are passed to constructor", () => {
            dateTime =
                DateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).setSeconds(37).setFractions(
                    256).build();

            expect(dateTime.getFractions()).toEqual(256);
        });

        it("getFractions() should return correct value when both seconds and fractions are passed to constructor", () => {
            dateTime =
                DateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).setSeconds(37).setFractions(
                    256).build();

            expect(dateTime.getFractions()).toEqual(256);
        });

        it("getTimezone().getOffset() should return correct value for timezone offset", () => {
            expect(dateTime.getTimezone().getOffset()).toEqual(1);
        });

        it("getTimezone().offsetToString() should return correctly padded value for offset", () => {
            expect(dateTime.getTimezone().offsetToString()).toEqual("+01:00");
        });
    });

    describe("negative offset toString()", () => {

        it("timeZone.toString() should return correctly padded value for offset", () => {
            timeZone = Timezone.create().setOffset(-1).build();
            expect((timeZone.getOffset())).toEqual(-1);
        });

        it("timeZone.toString() should return correctly padded value for offset", () => {
            timeZone = Timezone.create().setOffset(-1).build();
            expect(timeZone.toString()).toEqual("-01:00");
        });

        it("timeZone.toString() should return correctly padded value for offset", () => {
            timeZone = Timezone.create().setOffset(-11).build();
            expect(timeZone.toString()).toEqual("-11:00");
        });
    });


    describe("parse string with negative offset", () => {

        it("String with negative timezone should be parsed correctly", () => {
            dateTime = DateTime.fromString("2015-04-25T12:05:00-05:00");
            expect(dateTime.getTimezone().getOffset()).toEqual(-5);
            expect(dateTime.toString()).toEqual("2015-04-25T12:05:00-05:00");
        });

        it("String with no tz be parsed correctly", () => {
            dateTime = DateTime.fromString("2015-04-25T12:05:00");
            expect(dateTime.getTimezone().getOffset()).toEqual(0);
            expect(dateTime.toString()).toEqual("2015-04-25T12:05:00+00:00");
        });
    });

    describe("conversion to string", () => {

        it("should correctly convert when seconds, fractions and timezone not specified in constructor", () => {
            dateTime = DateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).build();

            expect(dateTime.toString()).toEqual("2015-04-25T12:05:00+00:00");
        });

        it("should correctly convert with timezone", () => {
            timeZone = Timezone.create().setOffset(1).build();
            dateTime =
                DateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).setSeconds(37).setTimezone(
                    timeZone).build();

            expect(dateTime.toString()).toEqual("2015-04-25T12:05:37+01:00");
        });


        it("should correctly convert with fractions and timezone", () => {
            timeZone = Timezone.create().setOffset(1).build();
            dateTime =
                DateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).setSeconds(37).setFractions(
                    9).setTimezone(timeZone).build();

            expect(dateTime.toString()).toEqual("2015-04-25T12:05:37.009+01:00");
        });
    });

    describe("comparison", () => {

        it("should correctly compare equal dates", () => {
            var date1 = DateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).build();
            var date2 = DateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).build();

            expect(date1.equals(date2)).toBeTruthy();
        });

        it("should correctly compare unequal dates", () => {
            var date1 = DateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).build();
            var date2 = DateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).setSeconds(1).build();

            expect(date1.equals(date2)).toBeFalsy();
        });

        it("should correctly compare equal dates with different fraction part", () => {
            var date1 = DateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).setSeconds(37).build();
            var date2 = DateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).setSeconds(
                37).setFractions(0).build();

            expect(date1.equals(date2)).toBeTruthy();
        });

        it("should correctly compare equal dates with timezones", () => {
            timeZone = Timezone.create().setOffset(1).build();
            var date1 = DateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).setSeconds(
                37).setTimezone(timeZone).build();
            var date2 = DateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).setSeconds(
                37).setTimezone(timeZone).build();

            expect(date1.equals(date2)).toBeTruthy();
        });

        it("should correctly compare unequal dates with different timezones", () => {
            var timeZone1 = Timezone.create().setOffset(1).build();
            var timeZone2 = Timezone.create().setOffset(2).build();
            var date1 = DateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).setSeconds(
                37).setTimezone(timeZone1).build();
            var date2 = DateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).setSeconds(
                37).setTimezone(timeZone2).build();

            expect(date1.equals(date2)).toBeFalsy();
        });
    });


    describe("parsing of a date literal", () => {

        it("should not parse empty string", () => {
            expect(() => {
                DateTime.fromString("");
            }).toThrow();
        });

        it("should not parse value that is not a datetime", () => {
            expect(() => {
                DateTime.fromString("this is not a date");
            }).toThrow();
        });

        it("should not parse date without time part", () => {
            expect(() => {
                DateTime.fromString("2015-03-25");
            }).toThrow();
        });

        it("should not parse date with incorrect separators", () => {
            expect(() => {
                DateTime.fromString("2015.03.25T12:05:37.009");
            }).toThrow();
        });

        it("should not parse time with incorrect separators", () => {
            expect(() => {
                DateTime.fromString("2015-03-25T12.05.37.009");
            }).toThrow();
        });

        it("should not parse datetime with incorrect order of parts", () => {
            expect(() => {
                DateTime.fromString("25-03-2015T12:05:37.009");
            }).toThrow();
        });

        it("should not parse incorrect date", () => {
            expect(() => {
                DateTime.fromString("2015-02-29T12:05:37.009");
            }).toThrow();
        });

        it("should not parse incorrect time", () => {
            expect(() => {
                DateTime.fromString("2015-03-25T32:05:37.009");
            }).toThrow();
        });

        it("should not parse date with incorrect timezone", () => {
            expect(() => {
                DateTime.fromString("2015-03-25T32:05:37.009+25:00");
            }).toThrow();
        });

        it("should parse full datetime in correct format", () => {
            var parsedDate = DateTime.fromString("2015-03-25T12:05:37.009");
            var originalDate = DateTime.create().setYear(2015).setMonth(2).setDay(25).setHours(12).setMinutes(5).setSeconds(
                37).setFractions(9).build();

            expect(originalDate.equals(parsedDate)).toBeTruthy();
        });

        it("should parse datetime without fractions", () => {
            var parsedDate = DateTime.fromString("2015-03-25T12:05:37");
            var originalDate = DateTime.create().setYear(2015).setMonth(2).setDay(25).setHours(12).setMinutes(5).setSeconds(
                37).build();

            expect(originalDate.equals(parsedDate)).toBeTruthy();
        });

        it("should parse datetime without seconds and fractions", () => {
            var parsedDate = DateTime.fromString("2015-03-25T12:05");
            var originalDate = DateTime.create().setYear(2015).setMonth(2).setDay(25).setHours(12).setMinutes(5).build();

            expect(originalDate.equals(parsedDate)).toBeTruthy();
        });

        it("should parse datetime without timezone", () => {
            timeZone = Timezone.create().setOffset(1).build();
            var parsedDate = DateTime.fromString("2015-03-25T12:05+01:00");
            var originalDate = DateTime.create().setYear(2015).setMonth(2).setDay(25).setHours(12).setMinutes(5).setTimezone(
                timeZone).build();

            expect(originalDate.equals(parsedDate)).toBeTruthy();
        });
    });
});