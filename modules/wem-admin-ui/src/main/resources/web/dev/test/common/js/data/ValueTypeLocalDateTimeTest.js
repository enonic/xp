describe("api.data.type.LocalDateTimeValueTypeTest", function () {

    var Value = api.data.Value;
    var ValueTypes = api.data.ValueTypes;

    describe("when isValid", function () {

        it("given a date time as Date then true is returned", function () {
            expect(ValueTypes.LOCAL_DATE_TIME.isValid(new Date(2000, 0, 1, 12, 30, 0))).toBe(true);
        });

        it("given a date time as string then false is returned", function () {
            expect(ValueTypes.LOCAL_DATE_TIME.isValid("2000-01-01T12:30:00")).toBe(false);
        });

        it("given a letter as string then false is returned", function () {
            expect(ValueTypes.LOCAL_DATE_TIME.isValid("a")).toBe(false);
        });

        it("given an empty string then false is returned", function () {
            expect(ValueTypes.LOCAL_DATE_TIME.isValid("")).toBe(false);
        });
    });

    describe("when isConvertible", function () {

        it("given a date time as string then true is returned", function () {
            expect(ValueTypes.LOCAL_DATE_TIME.isConvertible("2000-01-01T12:30:00")).toBeTruthy();
        });

        it("given a partly date time as string then true is returned", function () {
            expect(ValueTypes.LOCAL_DATE_TIME.isConvertible("2000-01-01T12")).toBeFalsy();
        });

        it("given a letter as string then false is returned", function () {
            expect(ValueTypes.LOCAL_DATE_TIME.isConvertible("a")).toBeFalsy();
        });

        it("given an empty string then false is returned", function () {
            expect(ValueTypes.LOCAL_DATE_TIME.isConvertible("")).toBeFalsy();
        });

        it("given an blank string then false is returned", function () {
            expect(ValueTypes.LOCAL_DATE_TIME.isConvertible(" ")).toBeFalsy();
        });
    });

    describe("when newValue", function () {

        it("given date time string '2000-01-01T12:30:01' then a new Value with that date is returned", function () {
            var actual = ValueTypes.LOCAL_DATE_TIME.newValue("2000-01-01T12:30:01");
            var expected = new Value(new Date(Date.UTC(2000, 0, 1, 12, 30, 1)), ValueTypes.LOCAL_DATE_TIME);
            expect(actual).toEqual(expected);
        });

        it("given invalid date time string '2000-01-01T12' then a null is returned", function () {
            expect(ValueTypes.LOCAL_DATE_TIME.newValue("2000-01-01T12")).toEqual(new Value(null, ValueTypes.LOCAL_DATE_TIME));
        });

        it("given an empty string then a null is returned", function () {
            expect(ValueTypes.LOCAL_DATE_TIME.newValue("")).toEqual(new Value(null, ValueTypes.LOCAL_DATE_TIME));
        });
    });

    describe("when toJsonValue", function () {

        it("given null then null is returned", function () {
            expect(ValueTypes.LOCAL_DATE_TIME.toJsonValue(new Value(null, ValueTypes.LOCAL_DATE_TIME))).toBeNull();
        });

        it("given date time string '2000-01-01T12:30:00' then an equal date string is returned", function () {
            expect(ValueTypes.LOCAL_DATE_TIME.toJsonValue(ValueTypes.LOCAL_DATE_TIME.newValue("2000-01-01T12:30:00"))).toEqual("2000-01-01T12:30:00");
        });

        it("given date time string '2000-01-02T12:30:00' then an equal date string is returned", function () {
            expect(ValueTypes.LOCAL_DATE_TIME.toJsonValue(new Value(new Date(Date.UTC(2000, 0, 2, 12, 30, 0)),
                ValueTypes.LOCAL_DATE_TIME))).toEqual("2000-01-02T12:30:00");
        });

        it("given date 2000-09-06T15:44:11 then an equal date string is returned", function () {
            expect(ValueTypes.LOCAL_DATE_TIME.toJsonValue(new Value(new Date(Date.UTC(2000, 8, 6, 15, 44, 11)),
                ValueTypes.LOCAL_DATE_TIME))).toEqual("2000-09-06T15:44:11");
        });
    });

});