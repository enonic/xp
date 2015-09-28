describe("api.data.type.ValueTypeBooleanTest", function () {

    var Value = api.data.Value;
    var ValueTypes = api.data.ValueTypes;

    describe("when isValid", function () {

        it("given a boolean as boolean then true is returned", function () {
            expect(ValueTypes.BOOLEAN.isValid(true)).toBe(true);
        });

        it("given a boolean as string then false is returned", function () {
            expect(ValueTypes.BOOLEAN.isValid("true")).toBe(false);
        });

        it("given a number as number then false is returned", function () {
            expect(ValueTypes.BOOLEAN.isValid(1)).toBe(false);
        });

        it("given an empty string then false is returned", function () {
            expect(ValueTypes.BOOLEAN.isValid("")).toBe(false);
        });
    });

    describe("when isConvertible", function () {

        it("given true as string then true is returned", function () {
            expect(ValueTypes.BOOLEAN.isConvertible("true")).toBeTruthy();
        });

        it("given false as string then true is returned", function () {
            expect(ValueTypes.BOOLEAN.isConvertible("false")).toBeTruthy();
        });

        it("given a letter as string then false is returned", function () {
            expect(ValueTypes.BOOLEAN.isConvertible("a")).toBeFalsy();
        });

        it("given a number as string then false is returned", function () {
            expect(ValueTypes.BOOLEAN.isConvertible("a")).toBeFalsy();
        });

        it("given an empty string then false is returned", function () {
            expect(ValueTypes.BOOLEAN.isConvertible("")).toBeFalsy();
        });

        it("given an blank string then false is returned", function () {
            expect(ValueTypes.BOOLEAN.isConvertible(" ")).toBeFalsy();
        });
    });

    describe("when newValue", function () {

        it("given true as string then a new Value with true is returned", function () {
            expect(ValueTypes.BOOLEAN.newValue("true")).toEqual(new Value(true, ValueTypes.BOOLEAN));
        });

        it("given false as string then a new Value with false is returned", function () {
            expect(ValueTypes.BOOLEAN.newValue("false")).toEqual(new Value(false, ValueTypes.BOOLEAN));
        });

        it("given an empty string then a null is returned", function () {
            expect(ValueTypes.BOOLEAN.newValue("")).toEqual(new Value(null, ValueTypes.BOOLEAN));
        });
    });

});