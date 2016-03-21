describe("api.data.type.LocalDateValueTypeTest", function () {

    var Value = api.data.Value;
    var ValueTypes = api.data.ValueTypes;

    describe("when isValid", function () {

        it("given a valid date as string should return true", function () {
            expect(ValueTypes.LOCAL_DATE.isValid("2000-01-01")).toBe(true);
        });

        it("given a letter as string then false is returned", function () {
            expect(ValueTypes.LOCAL_DATE.isValid("a")).toBe(false);
        });

        it("given an empty string then false is returned", function () {
            expect(ValueTypes.LOCAL_DATE.isValid("")).toBe(false);
        });
    });

    describe("when isConvertible", function () {

        it("given a date as string then true is returned", function () {
            expect(ValueTypes.LOCAL_DATE.isConvertible("2000-01-01")).toBeTruthy();
        });

        it("given a partly date as string then true is returned", function () {
            expect(ValueTypes.LOCAL_DATE.isConvertible("2000-01")).toBeFalsy();
        });

        it("given a letter as string then false is returned", function () {
            expect(ValueTypes.LOCAL_DATE.isConvertible("a")).toBeFalsy();
        });

        it("given an empty string then false is returned", function () {
            expect(ValueTypes.LOCAL_DATE.isConvertible("")).toBeFalsy();
        });

        it("given an blank string then false is returned", function () {
            expect(ValueTypes.LOCAL_DATE.isConvertible(" ")).toBeFalsy();
        });
    });

    describe("when newValue", function () {

        it("given invalid date string '2000-01' then a null is returned", function () {
            expect(ValueTypes.LOCAL_DATE.newValue("2000-01")).toEqual(new Value(null, ValueTypes.LOCAL_DATE));
        });

        it("given an empty string then a null is returned", function () {
            expect(ValueTypes.LOCAL_DATE.newValue("")).toEqual(new Value(null, ValueTypes.LOCAL_DATE));
        });
    });

    describe("when toJsonValue", function () {

        it("given null then null is returned", function () {
            expect(ValueTypes.LOCAL_DATE.toJsonValue(new Value(null, ValueTypes.LOCAL_DATE))).toBeNull();
        });

        it("given date 2000-01-01 as string then an equal date string is returned", function () {
            expect(ValueTypes.LOCAL_DATE.toJsonValue(ValueTypes.LOCAL_DATE.newValue("2000-01-01"))).toEqual("2000-01-01");
        });

        it("given date 2000-01-02 then an equal date string is returned", function () {
            expect(ValueTypes.LOCAL_DATE.toJsonValue(new Value("2000-01-02",
                ValueTypes.LOCAL_DATE))).toEqual("2000-01-02");
        });

        it("given date 2000-09-06 then an equal date string is returned", function () {
            expect(ValueTypes.LOCAL_DATE.toJsonValue(new Value("2000-09-06",
                ValueTypes.LOCAL_DATE))).toEqual("2000-09-06");
        });
    });

});