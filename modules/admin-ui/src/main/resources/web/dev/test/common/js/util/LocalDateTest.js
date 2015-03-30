describe("api.util.LocalDateTest", function () {
    var localDate;
    var TEST_YEAR, TEST_MONTH, TEST_DAY;
    describe("basic asserts", function () {
        beforeEach(function () {
            TEST_YEAR = 2001, TEST_MONTH = 2, TEST_DAY = 14;
            localDate = api.util.LocalDate.create().
                setYear(TEST_YEAR).
                setMonth(TEST_MONTH).
                setDay(TEST_DAY).
                build();
        });
        it("should create an instance", function () {
            expect(localDate).toBeDefined();
        });

        it("getYear() should return correct year", function () {
            expect(localDate.getYear()).toEqual(TEST_YEAR);
        });
        it("getMonth() should return correct month", function () {
            expect(localDate.getMonth()).toEqual(TEST_MONTH);
        });
        it("getDay() should return correct day", function () {
            expect(localDate.getDay()).toEqual(TEST_DAY);
        });

    });
    describe("conversion to string", function () {
        beforeEach(function () {
            localDate = api.util.LocalDate.create().setYear(TEST_YEAR).setMonth(TEST_MONTH).setDay(TEST_DAY).build();
        });
        it("should correctly convert", function () {
            expect("2001-02-14").toEqual(localDate.toString());
        });
    });
    describe("comparison", function () {
        it("should correctly compare equal dates", function () {
            var date1 = api.util.LocalDate.create().setYear(TEST_YEAR).setMonth(TEST_MONTH).setDay(TEST_DAY).build();
            var date2 = api.util.LocalDate.create().setYear(TEST_YEAR).setMonth(TEST_MONTH).setDay(TEST_DAY).build();
            expect(date1.equals(date2)).toBeTruthy();
        });
        it("should correctly compare unequal day", function () {
            var date1 = api.util.LocalDate.create().setYear(TEST_YEAR).setMonth(TEST_MONTH).setDay(TEST_DAY).build();
            var date2 = api.util.LocalDate.create().setYear(TEST_YEAR).setMonth(TEST_MONTH).setDay(TEST_DAY + 1).build();
            expect(date1.equals(date2)).toBeFalsy();
        });
        it("should correctly compare unequal month", function () {
            var date1 = api.util.LocalDate.create().setYear(TEST_YEAR).setMonth(TEST_MONTH).setDay(TEST_DAY).build();
            var date2 = api.util.LocalDate.create().setYear(TEST_YEAR).setMonth(TEST_MONTH + 1).setDay(TEST_DAY).build();
            expect(date1.equals(date2)).toBeFalsy();
        });
        it("should correctly compare unequal year", function () {
            var date1 = api.util.LocalDate.create().setYear(TEST_YEAR).setMonth(TEST_MONTH).setDay(TEST_DAY).build();
            var date2 = api.util.LocalDate.create().setYear(TEST_YEAR + 1).setMonth(TEST_MONTH).setDay(TEST_DAY).build();
            expect(date1.equals(date2)).toBeFalsy();
        });
    });
    describe("parsing of a date literal", function () {
        it("should not parse empty string", function () {
            expect(function () {
                api.util.LocalDate.parseDate("");
            }).toThrow();
        });
        it("should not parse value that is not a date", function () {
            expect(function () {
                api.util.LocalDate.parseDate("this is not a date");
            }).toThrow();
        });
        it("should not parse date with incorrect separators", function () {
            expect(function () {
                api.util.LocalDate.parseDate(TEST_YEAR + "." + TEST_MONTH +
                                              "." + TEST_DAY);
            }).toThrow();
        });
        it("should not parse date with incorrect order of parts", function () {
            expect(function () {
                api.util.LocalDate.parseDate(TEST_MONTH + api.util.LocalDate.DATE_SEPARATOR + TEST_DAY +
                                              api.util.LocalDate.DATE_SEPARATOR + TEST_YEAR);
            }).toThrow();
        });
        it("should parse full date in correct format", function () {
            var parsedDate = api.util.LocalDate.parseDate(TEST_YEAR + api.util.LocalDate.DATE_SEPARATOR + TEST_MONTH +
                                                           api.util.LocalDate.DATE_SEPARATOR + TEST_DAY);
            var originalDate = api.util.LocalDate.create().setYear(TEST_YEAR).setMonth(TEST_MONTH).setDay(TEST_DAY).build();
            expect(originalDate.equals(parsedDate)).toBeTruthy();
        });

        it("should parse an instance from Date", function () {
            expect(localDate).toEqual(api.util.LocalDate.fromDate(new Date(TEST_YEAR, TEST_MONTH-1, TEST_DAY)));
        });
    });
});