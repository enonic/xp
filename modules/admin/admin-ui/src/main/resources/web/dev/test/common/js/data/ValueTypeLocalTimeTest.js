describe("api.data.type.LocalTimeValueTypeTest", function () {

    var Value = api.data.Value;
    var ValueTypes = api.data.ValueTypes;

    describe("when isValid", function () {

        it("given a time as 'api.util.LocalTime' then true is returned", function () {
            expect(ValueTypes.LOCAL_TIME.isValid(new api.util.LocalTime.fromString("10:20"))).toBe(true);
        });

        it("given a time as string then false is returned", function () {
            expect(ValueTypes.LOCAL_TIME.isValid("2000-01-01")).toBe(false);
        });

        it("given a letter as string then false is returned", function () {
            expect(ValueTypes.LOCAL_TIME.isValid("a")).toBe(false);
        });

        it("given an empty string then false is returned", function () {
            expect(ValueTypes.LOCAL_TIME.isValid("")).toBe(false);
        });
    });

    describe("when isConvertible", function () {

        it("given a time as string then true is returned", function () {
            expect(ValueTypes.LOCAL_TIME.isConvertible("07:00")).toBeTruthy();
        });

        it("given a partly date as string then true is returned", function () {
            expect(ValueTypes.LOCAL_TIME.isConvertible("09-01")).toBeFalsy();
        });

        it("given a letter as string then false is returned", function () {
            expect(ValueTypes.LOCAL_TIME.isConvertible("a")).toBeFalsy();
        });

        it("given an empty string then false is returned", function () {
            expect(ValueTypes.LOCAL_TIME.isConvertible("")).toBeFalsy();
        });

        it("given an blank string then false is returned", function () {
            expect(ValueTypes.LOCAL_TIME.isConvertible(" ")).toBeFalsy();
        });
    });

    describe("when newValue", function () {

        it("given time string '20:00' then a new Value with that date is returned", function () {
            var actual = ValueTypes.LOCAL_TIME.newValue("20:00");
            var expected = new Value(new api.util.LocalTime.fromString("20:00"), ValueTypes.LOCAL_TIME);
            expect(actual).toEqual(expected);
        });

        it("given invalid time string '20,01' then a null is returned", function () {
            expect(ValueTypes.LOCAL_TIME.newValue("20,01")).toEqual(new Value(null, ValueTypes.LOCAL_TIME));
        });

        it("given an empty string then a null is returned", function () {
            expect(ValueTypes.LOCAL_TIME.newValue("")).toEqual(new Value(null, ValueTypes.LOCAL_TIME));
        });
    });

    describe("when toJsonValue", function () {

        it("given null then null is returned", function () {
            expect(ValueTypes.LOCAL_TIME.toJsonValue(new Value(null, ValueTypes.LOCAL_TIME))).toBeNull();
        });

        it("given a time '12:08' as string then an equal time string is returned", function () {
            expect(ValueTypes.LOCAL_TIME.toJsonValue(ValueTypes.LOCAL_TIME.newValue("12:08"))).toEqual("12:08");
        });

        it("given a time '16:20' then an equal time string is returned", function () {
            var newValue = new Value(new api.util.LocalTime.fromString("16:20"), ValueTypes.LOCAL_TIME);
            expect(ValueTypes.LOCAL_TIME.toJsonValue(newValue)).toEqual("16:20");
        });

        it("given a time '22:22' then an equal time string is returned", function () {
            var newValue = new Value(new api.util.LocalTime.fromString("22:22"), ValueTypes.LOCAL_TIME);
            expect(ValueTypes.LOCAL_TIME.toJsonValue(newValue)).toEqual("22:22");
        });
    });

});