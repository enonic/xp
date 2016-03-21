describe("api.util.LocalDateTimeTest", function () {

    var localDateTime;

    describe("basic asserts", function () {

        beforeEach(function () {
            localDateTime = api.util.LocalDateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).build();
        });

        it("should create an instance", function () {
            expect(localDateTime).toBeDefined();
        });

        it("getYear() should return correct year", function () {
            expect(localDateTime.getYear()).toEqual(2015);
        });

        it("getMonth() should return correct month", function () {
            expect(localDateTime.getMonth()).toEqual(3);
        });

        it("getDay() should return correct day", function () {
            expect(localDateTime.getDay()).toEqual(25);
        });

        it("getHours() should return correct hours", function () {
            expect(localDateTime.getHours()).toEqual(12);
        });

        it("getMinutes() should return correct minutes", function () {
            expect(localDateTime.getMinutes()).toEqual(5);
        });

        it("getSeconds() should return 0 when seconds are not passed to constructor", function () {
            expect(localDateTime.getSeconds()).toEqual(0);
        });

        it("getFractions() should return 0 when fractions are not passed to constructor", function () {
            expect(localDateTime.getFractions()).toEqual(0);
        });

        it("getFractions() should return 0 when seconds are not passed to constructor", function () {
            localDateTime = api.util.LocalDateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).setFractions(256).build();

            expect(localDateTime.getFractions()).toEqual(0);
        });

        it("getSeconds() should return correct seconds when passed to constructor", function () {
            localDateTime = api.util.LocalDateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).setSeconds(37).build();

            expect(localDateTime.getSeconds()).toEqual(37);
        });

        it("getFractions() should return correct value when both seconds and fractions are passed to constructor", function () {
            localDateTime = api.util.LocalDateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).setSeconds(37).setFractions(256).build();

            expect(localDateTime.getFractions()).toEqual(256);
        });
    });


    describe("conversion to string", function () {

        it("should correctly convert without seconds and fractions", function () {
            localDateTime = api.util.LocalDateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).build();

            expect(localDateTime.toString()).toEqual("2015-04-25T12:05:00");
        });

        it("should correctly convert with seconds and no fractions", function () {
            localDateTime = api.util.LocalDateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).setSeconds(37).build();

            expect(localDateTime.toString()).toEqual("2015-04-25T12:05:37");
        });

        it("should correctly convert with seconds and fractions", function () {
            localDateTime = api.util.LocalDateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).setSeconds(37).setFractions(9).build();

            expect(localDateTime.toString()).toEqual("2015-04-25T12:05:37.009");
        });
    });

    describe("comparison", function () {

        it("should correctly compare equal dates", function () {
            var date1 = api.util.LocalDateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).build();
            var date2 = api.util.LocalDateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).build();

            expect(date1.equals(date2)).toBeTruthy();
        });

        it("should correctly compare unequal dates", function () {
            var date1 = api.util.LocalDateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).build();
            var date2 = api.util.LocalDateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).setSeconds(1).build();

            expect(date1.equals(date2)).toBeFalsy();
        });


        it("should correctly compare equal dates with different fraction part", function () {
            var date1 = api.util.LocalDateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).setSeconds(37).build();
            var date2 = api.util.LocalDateTime.create().setYear(2015).setMonth(3).setDay(25).setHours(12).setMinutes(5).setSeconds(37).setFractions(0).build();

            expect(date1.equals(date2)).toBeTruthy();
        });
    });


    describe("parsing of a date literal", function () {

        it("should not parse empty string", function () {
            expect(function() {
                api.util.LocalDateTime.fromString("");
            }).toThrow();
        });

        it("should not parse value that is not a datetime", function () {
            expect(function() {
                api.util.LocalDateTime.fromString("this is not a date");
            }).toThrow();
        });

        it("should not parse date without time part", function () {
            expect(function() {
                api.util.LocalDateTime.fromString("2015-03-25");
            }).toThrow();
        });

        it("should not parse date with incorrect separators", function () {
            expect(function() {
                api.util.LocalDateTime.fromString("2015.03.25T12:05:37.009");
            }).toThrow();
        });

        it("should not parse time with incorrect separators", function () {
            expect(function() {
                api.util.LocalDateTime.fromString("2015-03-25T12.05.37.009");
            }).toThrow();
        });

        it("should not parse datetime with incorrect order of parts", function () {
            expect(function() {
                api.util.LocalDateTime.fromString("25-03-2015T12:05:37.009");
            }).toThrow();
        });

        it("should not parse incorrect date", function () {
            expect(function() {
                api.util.LocalDateTime.fromString("2015-02-29T12:05:37.009");
            }).toThrow();
        });

        it("should not parse incorrect time", function () {
            expect(function() {
                api.util.LocalDateTime.fromString("2015-03-25T32:05:37.009");
            }).toThrow();
        });

        it("should parse full datetime in correct format", function () {
            var parsedDate = api.util.LocalDateTime.fromString("2015-03-25T12:05:37.009");
            var originalDate = api.util.LocalDateTime.create().setYear(2015).setMonth(2).setDay(25).setHours(12).setMinutes(5).setSeconds(37).setFractions(9).build();

            expect(originalDate.equals(parsedDate)).toBeTruthy();
        });

        it("should parse datetime without fractions", function () {
            var parsedDate = api.util.LocalDateTime.fromString("2015-03-25T12:05:37");
            var originalDate = api.util.LocalDateTime.create().setYear(2015).setMonth(2).setDay(25).setHours(12).setMinutes(5).setSeconds(37).build();

            expect(originalDate.equals(parsedDate)).toBeTruthy();
        });

        it("should parse datetime without seconds and fractions", function () {
            var parsedDate = api.util.LocalDateTime.fromString("2015-03-25T12:05");
            var originalDate = api.util.LocalDateTime.create().setYear(2015).setMonth(2).setDay(25).setHours(12).setMinutes(5).build();

            expect(originalDate.equals(parsedDate)).toBeTruthy();
        });
    });
});