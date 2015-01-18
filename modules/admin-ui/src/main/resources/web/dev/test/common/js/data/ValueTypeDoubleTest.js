describe("api.data.ValueTypeDoubleTest", function () {

    var Value = api.data.Value;
    var ValueTypes = api.data.ValueTypes;

    describe("when isValid", function () {

        it("given a whole number as number then true is returned", function () {
            expect(ValueTypes.DOUBLE.isValid(1)).toBe(true);
        });

        it("given a decimal number as number then true is returned", function () {
            expect(ValueTypes.DOUBLE.isValid(1.1)).toBe(true);
        });

        it("given a double as string then false is returned", function () {
            expect(ValueTypes.DOUBLE.isValid("1.1")).toBe(false);
        });

        it("given a letter as string then false is returned", function () {
            expect(ValueTypes.DOUBLE.isValid("a")).toBe(false);
        });

        it("given an empty string then false is returned", function () {
            expect(ValueTypes.DOUBLE.isValid("")).toBe(false);
        });
    });

    describe("when isConvertible", function () {

        it("given a whole number as string then true is returned", function () {
            expect(ValueTypes.DOUBLE.isConvertible("1")).toBeTruthy();
        });

        it("given a decimal number as string then true is returned", function () {
            expect(ValueTypes.DOUBLE.isConvertible("1.1")).toBeTruthy();
        });

        it("given a letter as string then false is returned", function () {
            expect(ValueTypes.DOUBLE.isConvertible("a")).toBeFalsy();
        });

        it("given an empty string then false is returned", function () {
            expect(ValueTypes.DOUBLE.isConvertible("")).toBeFalsy();
        });

        it("given an blank string then false is returned", function () {
            expect(ValueTypes.DOUBLE.isConvertible(" ")).toBeFalsy();
        });
    });

    describe("when newValue", function () {

        it("given a number as string then a new Value is returned", function () {
            expect(ValueTypes.DOUBLE.newValue("1")).toEqual(new Value(1, ValueTypes.DOUBLE));
        });

        it("given a decimal number as string then a Value with null is returned", function () {
            expect(ValueTypes.DOUBLE.newValue("1.1")).toEqual(new Value(1.1, ValueTypes.DOUBLE));
        });

        it("given an empty string then a Value with null is returned", function () {
            expect(ValueTypes.DOUBLE.newValue("")).toEqual(new Value(null, ValueTypes.DOUBLE));
        });
    });

});