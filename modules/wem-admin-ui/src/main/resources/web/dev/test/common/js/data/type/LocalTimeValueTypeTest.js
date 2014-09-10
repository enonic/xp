describe("api.data.type.LocalTimeValueType", function () {

    var Value = api.data.Value;
    var ValueTypes = api.data.type.ValueTypes;

    describe("when isValid", function () {

        it("given a time as string then true is returned", function () {
            expect(ValueTypes.LOCAL_TIME.isValid("10:43")).toBe(true);
        });

        it("given a time as string and wrong format then false is returned", function () {
            expect(ValueTypes.LOCAL_TIME.isValid("1:43")).toBe(false);
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
            expect(ValueTypes.LOCAL_TIME.isConvertible("12:43")).toBeTruthy();
        });

        it("given a time as string and wrong delimiter then false is returned", function () {
            expect(ValueTypes.LOCAL_TIME.isConvertible("12-43")).toBeFalsy();
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

        it("given time 11:11 as string then a new Value with that time is returned", function () {
            var actual = ValueTypes.LOCAL_TIME.newValue("11:11");
            var expected = new Value("11:11", ValueTypes.LOCAL_TIME);
            expect(actual).toEqual(expected);
        });

        it("given invalid time string 12-12 then a null is returned", function () {
            expect(ValueTypes.LOCAL_TIME.newValue("12-12")).toBeNull();
        });

        it("given an empty string then a null is returned", function () {
            expect(ValueTypes.LOCAL_TIME.newValue("")).toBeNull();
        });
    });

    describe("when toJsonValue", function () {

        it("given time 20:00 as string then an equal time string is returned", function () {
            expect(ValueTypes.LOCAL_TIME.toJsonValue(ValueTypes.LOCAL_TIME.newValue("20:00"))).toEqual("20:00");
        });

        it("given date 20:00 then an equal time string is returned", function () {
            expect(ValueTypes.LOCAL_TIME.toJsonValue(new Value("20:00", ValueTypes.LOCAL_TIME))).toEqual("20:00");
        });

        it("given date 04:00 then an equal time string is returned", function () {
            expect(ValueTypes.LOCAL_TIME.toJsonValue(new Value("04:00", ValueTypes.LOCAL_TIME))).toEqual("04:00");
        });
    });

});