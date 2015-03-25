describe("api.util.LocalDateTimeTest", function () {

    var localDateTime;

    describe("basic asserts", function () {

        beforeEach(function () {
            localDateTime = new api.util.LocalDateTime(2015, 3, 25, 12, 5);
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

        it("getSeconds() should return undefined when seconds are not specified", function () {
            expect(localDateTime.getSeconds()).not.toBeDefined();
        });

        it("getFractions() should return undefined when fractions are not specified", function () {
            expect(localDateTime.getFractions()).not.toBeDefined();
        });

        it("getFractions() should return undefined when seconds are not specified", function () {
            localDateTime = new api.util.LocalDateTime(2015, 3, 25, 12, 5, null, 256);

            expect(localDateTime.getFractions()).not.toBeDefined();
        });

        it("getSeconds() should return correct seconds when passed to constructor", function () {
            localDateTime = new api.util.LocalDateTime(2015, 3, 25, 12, 5, 37);

            expect(localDateTime.getSeconds()).toEqual(37);
        });

        it("getFractions() should return undefined when seconds are specified and fractions equal 0", function () {
            localDateTime = new api.util.LocalDateTime(2015, 3, 25, 12, 5, 37, 0);

            expect(localDateTime.getFractions()).not.toBeDefined();
        });

        it("getFractions() should return correct value when seconds are specified and fractions are not 0", function () {
            localDateTime = new api.util.LocalDateTime(2015, 3, 25, 12, 5, 37, 256);

            expect(localDateTime.getFractions()).toEqual(256);
        });
    });


    describe("conversion to string", function () {

        it("should correctly convert without seconds and fractions", function () {
            localDateTime = new api.util.LocalDateTime(2015, 3, 25, 12, 5);

            expect(localDateTime.toString()).toEqual("2015-03-25T12:05");
        });

        it("should correctly convert with seconds and no fractions", function () {
            localDateTime = new api.util.LocalDateTime(2015, 3, 25, 12, 5, 37);

            expect(localDateTime.toString()).toEqual("2015-03-25T12:05:37");
        });

        it("should correctly convert with seconds and fractions", function () {
            localDateTime = new api.util.LocalDateTime(2015, 3, 25, 12, 5, 37, 9);

            expect(localDateTime.toString()).toEqual("2015-03-25T12:05:37.009");
        });
    });

    describe("comparison", function () {

        it("should correctly compare equal dates", function () {
            var date1 = new api.util.LocalDateTime(2015, 3, 25, 12, 5);
            var date2 = new api.util.LocalDateTime(2015, 3, 25, 12, 5);

            expect(date1.equals(date2)).toBeTruthy();
        });

        it("should correctly compare unequal dates", function () {
            var date1 = new api.util.LocalDateTime(2015, 3, 25, 12, 5);
            var date2 = new api.util.LocalDateTime(2015, 3, 25, 12, 5, 1);

            expect(date1.equals(date2)).toBeFalsy();
        });


        it("should correctly compare equal dates with different fraction part", function () {
            var date1 = new api.util.LocalDateTime(2015, 3, 25, 12, 5, 37);
            var date2 = new api.util.LocalDateTime(2015, 3, 25, 12, 5, 37, 0);

            expect(date1.equals(date2)).toBeTruthy();
        });
    });


    describe("parsing of a date literal", function () {

        it("should not parse empty string", function () {
            expect(function() {
                api.util.LocalDateTime.fromString("");
            }).toThrow();
        });

        it("should not parse value that is not a date", function () {
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

        it("should not parse date with incorrect order of parts", function () {
            expect(function() {
                api.util.LocalDateTime.fromString("25-03-2015T12:05:37.009");
            }).toThrow();
        });

        it("should parse full date in correct format", function () {
            var parsedDate = api.util.LocalDateTime.fromString("2015-03-25T12:05:37.009");
            var originalDate = new api.util.LocalDateTime(2015, 3, 25, 12, 5, 37, 9);
            expect(originalDate.equals(parsedDate)).toBeTruthy();
        });

        it("should parse date without fractions", function () {
            var parsedDate = api.util.LocalDateTime.fromString("2015-03-25T12:05:37");
            var originalDate = new api.util.LocalDateTime(2015, 3, 25, 12, 5, 37);
            expect(originalDate.equals(parsedDate)).toBeTruthy();
        });

        it("should parse date without seconds and fractions", function () {
            var parsedDate = api.util.LocalDateTime.fromString("2015-03-25T12:05");
            var originalDate = new api.util.LocalDateTime(2015, 3, 25, 12, 5);
            expect(originalDate.equals(parsedDate)).toBeTruthy();
        });
    });
});