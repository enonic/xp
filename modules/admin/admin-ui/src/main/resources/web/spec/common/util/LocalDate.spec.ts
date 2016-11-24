import LocalDate = api.util.LocalDate;

describe("api.util.LocalDate", () => {
    var localDate,
        test_iso_date_string;

    describe("basic asserts", () => {
        beforeEach(() => {
            test_iso_date_string = "2001-02-14";
            localDate = LocalDate.fromISOString(test_iso_date_string);
        });
        it("should create an instance", () => {
            expect(localDate).toBeDefined();
        });

        it("getYear() should return correct year", () => {
            expect(localDate.getYear()).toEqual(2001);
        });
        it("getMonth() should return correct month", () => {
            expect(localDate.getMonth()).toEqual(1);
        });
        it("getDay() should return correct day", () => {
            expect(localDate.getDay()).toEqual(14);
        });

    });
    describe("conversion to string", () => {
        beforeEach(() => {
            localDate = LocalDate.fromISOString(test_iso_date_string);
        });
        it("should correctly convert", () => {
            expect("2001-02-14").toEqual(localDate.toString());
        });
    });
    describe("comparison", () => {
        it("should correctly compare equal dates", () => {
            var date1 = LocalDate.fromISOString(test_iso_date_string);
            var date2 = LocalDate.fromISOString(test_iso_date_string);
            expect(date1.equals(date2)).toBeTruthy();
        });
        it("should correctly compare unequal day", () => {
            var date1 = LocalDate.fromISOString(test_iso_date_string);
            var date2 = LocalDate.fromISOString("2001-02-15");
            expect(date1.equals(date2)).toBeFalsy();
        });
        it("should correctly compare unequal month", () => {
            var date1 = LocalDate.fromISOString(test_iso_date_string);
            var date2 = LocalDate.fromISOString("2001-03-14");
            expect(date1.equals(date2)).toBeFalsy();
        });
        it("should correctly compare unequal year", () => {
            var date1 = LocalDate.fromISOString(test_iso_date_string);
            var date2 = LocalDate.fromISOString("2002-02-14");
            expect(date1.equals(date2)).toBeFalsy();
        });
    });
    describe("parsing of a date literal", () => {
        it("should not parse empty string", () => {
            expect(() => {
                LocalDate.fromISOString("");
            }).toThrow();
        });
        it("should not parse value that is not a date", () => {
            expect(() => {
                LocalDate.fromISOString("this is not a date");
            }).toThrow();
        });
        it("should not parse date with incorrect separators", () => {
            expect(() => {
                LocalDate.fromISOString("2014.12.12");
            }).toThrow();
        });
        it("should not parse date with incorrect order of parts", () => {
            expect(() => {
                LocalDate.fromISOString("15-02-2015");
            }).toThrow();
        });
        it("should parse full date in correct format", () => {
            var parsedDate = LocalDate.fromISOString("2015-12-31");
            var originalDate = LocalDate.create().setYear(2015).setMonth(11).setDay(31).build();
            expect(originalDate.equals(parsedDate)).toBeTruthy();
        });

        it("should parse an instance from Date", () => {
            expect(localDate).toEqual(LocalDate.fromDate(new Date(2001, 1, 14)));
        });
    });
});