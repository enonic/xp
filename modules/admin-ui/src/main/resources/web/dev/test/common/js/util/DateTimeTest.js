describe("api.util.DateTimeTest", function () {

    var dateTime;
    var timeZone;

    describe("basic asserts", function () {

        beforeEach(function () {
            timeZone =  api.util.Timezone.create().setOffset(1).build();
            dateTime = api.util.DateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).setTimezone(timeZone).build();
        });

        it("should create an instance", function () {
            expect(dateTime).toBeDefined();
        });

        it("getYear() should return correct year", function () {
            expect(dateTime.getYear()).toEqual(2015);
        });

        it("getMonth() should return correct month", function () {
            expect(dateTime.getMonth()).toEqual(3);
        });

        it("getDay() should return correct day", function () {
            expect(dateTime.getDay()).toEqual(25);
        });

        it("getHours() should return correct hours", function () {
            expect(dateTime.getHours()).toEqual(12);
        });

        it("getMinutes() should return correct minutes", function () {
            expect(dateTime.getMinutes()).toEqual(5);
        });

        it("getSeconds() should return 0 when seconds are not passed to constructor", function () {
            expect(dateTime.getSeconds()).toEqual(0);
        });

        it("getFractions() should return 0 when fractions are not passed to constructor", function () {
            expect(dateTime.getFractions()).toEqual(0);
        });

        it("getFractions() should return 0 when seconds are not passed to constructor", function () {
            dateTime = api.util.DateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).setFractions(256).build();

            expect(dateTime.getFractions()).toEqual(0);
        });

        it("getSeconds() should return correct seconds when passed to constructor", function () {
            dateTime = api.util.DateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).setSeconds(37).build();

            expect(dateTime.getSeconds()).toEqual(37);
        });

        it("getFractions() should return correct value when both seconds and fractions are passed to constructor", function () {
            dateTime = api.util.DateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).setSeconds(37).setFractions(256).build();

            expect(dateTime.getFractions()).toEqual(256);
        });

        it("getFractions() should return correct value when both seconds and fractions are passed to constructor", function () {
            dateTime = api.util.DateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).setSeconds(37).setFractions(256).build();

            expect(dateTime.getFractions()).toEqual(256);
        });

        it("getTimezone().getOffset() should return correct value for timezone offset", function () {
             expect(dateTime.getTimezone().getOffset()).toEqual(1);
        });

        it("getTimezone().offsetToString() should return correctly padded value for offset", function () {
            expect(dateTime.getTimezone().offsetToString()).toEqual("+01:00");
        });
    });

    describe("negative offset toString()", function () {

        it("timeZone.toString() should return correctly padded value for offset", function () {
            timeZone = api.util.Timezone.create().setOffset(-1).build();
            expect((timeZone.getOffset())).toEqual(-1);
        });

        it("timeZone.toString() should return correctly padded value for offset", function () {
            timeZone = api.util.Timezone.create().setOffset(-1).build();
            expect(timeZone.toString()).toEqual("-01:00");
        });

        it("timeZone.toString() should return correctly padded value for offset", function () {
            timeZone = api.util.Timezone.create().setOffset(-11).build();
            expect(timeZone.toString()).toEqual("-11:00");
        });
    });


    describe("parse string with negative offset", function () {

        it("String with negative timezone should be parsed correctly", function () {
            dateTime = api.util.DateTime.fromString("2015-04-25T12:05:00-05:00");
            expect(dateTime.getTimezone().getOffset()).toEqual(-5);
            expect(dateTime.toString()).toEqual("2015-04-25T12:05:00-05:00");
        });

        it("String with no tz be parsed correctly", function () {
            dateTime = api.util.DateTime.fromString("2015-04-25T12:05:00");
            expect(dateTime.getTimezone().getOffset()).toEqual(0);
            expect(dateTime.toString()).toEqual("2015-04-25T12:05:00+00:00");
        });
    });

    describe("conversion to string", function () {

        it("should correctly convert when seconds, fractions and timezone not specified in constructor", function () {
            dateTime = api.util.DateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).build();

            expect(dateTime.toString()).toEqual("2015-04-25T12:05:00+00:00");
        });

        it("should correctly convert with timezone", function () {
            timeZone =  api.util.Timezone.create().setOffset(1).build();
            dateTime = api.util.DateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).setSeconds(37).setTimezone(timeZone).build();

            expect(dateTime.toString()).toEqual("2015-04-25T12:05:37+01:00");
        });


        it("should correctly convert with fractions and timezone", function () {
            timeZone =  api.util.Timezone.create().setOffset(1).build();
            dateTime = api.util.DateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).setSeconds(37).setFractions(9).setTimezone(timeZone).build();

            expect(dateTime.toString()).toEqual("2015-04-25T12:05:37.009+01:00");
        });
    });

    describe("comparison", function () {

        it("should correctly compare equal dates", function () {
            var date1 = api.util.DateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).build();
            var date2 = api.util.DateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).build();

            expect(date1.equals(date2)).toBeTruthy();
        });

        it("should correctly compare unequal dates", function () {
            var date1 = api.util.DateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).build();
            var date2 = api.util.DateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).setSeconds(1).build();

            expect(date1.equals(date2)).toBeFalsy();
        });

        it("should correctly compare equal dates with different fraction part", function () {
            var date1 = api.util.DateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).setSeconds(37).build();
            var date2 = api.util.DateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).setSeconds(37).setFractions(0).build();

            expect(date1.equals(date2)).toBeTruthy();
        });

        it("should correctly compare equal dates with timezones", function () {
            timeZone =  api.util.Timezone.create().setOffset(1).build();
            var date1 = api.util.DateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).setSeconds(37).setTimezone(timeZone).build();
            var date2 = api.util.DateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).setSeconds(37).setTimezone(timeZone).build();

            expect(date1.equals(date2)).toBeTruthy();
        });

        it("should correctly compare unequal dates with different timezones", function () {
            var timeZone1 =  api.util.Timezone.create().setOffset(1).build();
            var timeZone2 =  api.util.Timezone.create().setOffset(2).build();
            var date1 = api.util.DateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).setSeconds(37).setTimezone(timeZone1).build();
            var date2 = api.util.DateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).setSeconds(37).setTimezone(timeZone2).build();

            expect(date1.equals(date2)).toBeFalsy();
        });
    });


    describe("parsing of a date literal", function () {

        it("should not parse empty string", function () {
            expect(function() {
                api.util.DateTime.fromString("");
            }).toThrow();
        });

        it("should not parse value that is not a datetime", function () {
            expect(function() {
                api.util.DateTime.fromString("this is not a date");
            }).toThrow();
        });

        it("should not parse date without time part", function () {
            expect(function() {
                api.util.DateTime.fromString("2015-03-25");
            }).toThrow();
        });

        it("should not parse date with incorrect separators", function () {
            expect(function() {
                api.util.DateTime.fromString("2015.03.25T12:05:37.009");
            }).toThrow();
        });

        it("should not parse time with incorrect separators", function () {
            expect(function() {
                api.util.DateTime.fromString("2015-03-25T12.05.37.009");
            }).toThrow();
        });

        it("should not parse datetime with incorrect order of parts", function () {
            expect(function() {
                api.util.DateTime.fromString("25-03-2015T12:05:37.009");
            }).toThrow();
        });

        it("should not parse incorrect date", function () {
            expect(function() {
                api.util.DateTime.fromString("2015-02-29T12:05:37.009");
            }).toThrow();
        });

        it("should not parse incorrect time", function () {
            expect(function() {
                api.util.DateTime.fromString("2015-03-25T32:05:37.009");
            }).toThrow();
        });

        it("should not parse date with incorrect timezone", function () {
            expect(function() {
                api.util.DateTime.fromString("2015-03-25T32:05:37.009+25:00");
            }).toThrow();
        });

        it("should parse full datetime in correct format", function () {
            var parsedDate = api.util.DateTime.fromString("2015-03-25T12:05:37.009");
            var originalDate = api.util.DateTime.create().setYear(2015).setMonth(2).setDay(25).setHours(12).setMinutes(5).setSeconds(37).setFractions(9).build();

            expect(originalDate.equals(parsedDate)).toBeTruthy();
        });

        it("should parse datetime without fractions", function () {
            var parsedDate = api.util.DateTime.fromString("2015-03-25T12:05:37");
            var originalDate = api.util.DateTime.create().setYear(2015).setMonth(2).setDay(25).setHours(12).setMinutes(5).setSeconds(37).build();

            expect(originalDate.equals(parsedDate)).toBeTruthy();
        });

        it("should parse datetime without seconds and fractions", function () {
            var parsedDate = api.util.DateTime.fromString("2015-03-25T12:05");
            var originalDate = api.util.DateTime.create().setYear(2015).setMonth(2).setDay(25).setHours(12).setMinutes(5).build();

            expect(originalDate.equals(parsedDate)).toBeTruthy();
        });

        it("should parse datetime without timezone", function () {
            timeZone =  api.util.Timezone.create().setOffset(1).build();
            var parsedDate = api.util.DateTime.fromString("2015-03-25T12:05+01:00");
            var originalDate = api.util.DateTime.create().setYear(2015).setMonth(2).setDay(25).setHours(12).setMinutes(5).setTimezone(timeZone).build();

            expect(originalDate.equals(parsedDate)).toBeTruthy();
        });
    });
});