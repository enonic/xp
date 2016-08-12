describe("api.util.LocalDateTest", function () {
    var localDate,
        test_iso_date_string;
    describe("basic asserts", function () {
        beforeEach(function () {
            test_iso_date_string = "2001-02-14";
            localDate = api.util.LocalDate.fromISOString(test_iso_date_string);
        });
        it("should create an instance", function () {
            expect(localDate).toBeDefined();
        });

        it("getYear() should return correct year", function () {
            expect(localDate.getYear()).toEqual(2001);
        });
        it("getMonth() should return correct month", function () {
            expect(localDate.getMonth()).toEqual(1);
        });
        it("getDay() should return correct day", function () {
            expect(localDate.getDay()).toEqual(14);
        });

    });
    describe("conversion to string", function () {
        beforeEach(function () {
            localDate = api.util.LocalDate.fromISOString(test_iso_date_string);
        });
        it("should correctly convert", function () {
            expect("2001-02-14").toEqual(localDate.toString());
        });
    });
    describe("comparison", function () {
        it("should correctly compare equal dates", function () {
            var date1 = api.util.LocalDate.fromISOString(test_iso_date_string);
            var date2 = api.util.LocalDate.fromISOString(test_iso_date_string);
            expect(date1.equals(date2)).toBeTruthy();
        });
        it("should correctly compare unequal day", function () {
            var date1 = api.util.LocalDate.fromISOString(test_iso_date_string);
            var date2 = api.util.LocalDate.fromISOString("2001-02-15");
            expect(date1.equals(date2)).toBeFalsy();
        });
        it("should correctly compare unequal month", function () {
            var date1 = api.util.LocalDate.fromISOString(test_iso_date_string);
            var date2 = api.util.LocalDate.fromISOString("2001-03-14");
            expect(date1.equals(date2)).toBeFalsy();
        });
        it("should correctly compare unequal year", function () {
            var date1 = api.util.LocalDate.fromISOString(test_iso_date_string);
            var date2 = api.util.LocalDate.fromISOString("2002-02-14");
            expect(date1.equals(date2)).toBeFalsy();
        });
    });
    describe("parsing of a date literal", function () {
        it("should not parse empty string", function () {
            expect(function () {
                api.util.LocalDate.fromISOString("");
            }).toThrow();
        });
        it("should not parse value that is not a date", function () {
            expect(function () {
                api.util.LocalDate.fromISOString("this is not a date");
            }).toThrow();
        });
        it("should not parse date with incorrect separators", function () {
            expect(function () {
                api.util.LocalDate.fromISOString("2014.12.12");
            }).toThrow();
        });
        it("should not parse date with incorrect order of parts", function () {
            expect(function () {
                api.util.LocalDate.fromISOString("15-02-2015");
            }).toThrow();
        });
        it("should parse full date in correct format", function () {
            var parsedDate = api.util.LocalDate.fromISOString("2015-12-31");
            var originalDate = api.util.LocalDate.create().setYear(2015).setMonth(11).setDay(31).build();
            expect(originalDate.equals(parsedDate)).toBeTruthy();
        });

        it("should parse an instance from Date", function () {
            expect(localDate).toEqual(api.util.LocalDate.fromDate(new Date(2001, 1, 14)));
        });
    });
});