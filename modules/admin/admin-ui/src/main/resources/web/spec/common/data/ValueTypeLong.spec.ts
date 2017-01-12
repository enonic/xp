describe("api.data.type.LongValueType", () => {

    describe("when isValid", () => {

        it("given a whole number as number then true is returned", () => {
            expect(ValueTypes.LONG.isValid(1)).toBe(true);
        });

        it("given a decimal number as number then false is returned", () => {
            expect(ValueTypes.LONG.isValid(1.1)).toBe(false);
        });

        it("given a number as string then false is returned", () => {
            expect(ValueTypes.LONG.isValid("1")).toBe(false);
        });

        it("given a letter as string then false is returned", () => {
            expect(ValueTypes.LONG.isValid("a")).toBe(false);
        });

        it("given an empty string then false is returned", () => {
            expect(ValueTypes.LONG.isValid("")).toBe(false);
        });
    });

    describe("when isConvertible", () => {

        it("given a number as string then true is returned", () => {
            expect(ValueTypes.LONG.isConvertible("1")).toBeTruthy();
        });

        it("given a decimal number as string then false is returned", () => {
            expect(ValueTypes.LONG.isConvertible("1.1")).toBeFalsy();
        });

        it("given a letter as string then false is returned", () => {
            expect(ValueTypes.LONG.isConvertible("a")).toBeFalsy();
        });

        it("given an empty string then false is returned", () => {
            expect(ValueTypes.LONG.isConvertible("")).toBeFalsy();
        });

        it("given an blank string then false is returned", () => {
            expect(ValueTypes.LONG.isConvertible(" ")).toBeFalsy();
        });
    });

    describe("when newValue", () => {

        it("given a number as string then a new Value is returned", () => {
            expect(ValueTypes.LONG.newValue("1")).toEqual(new Value(1, ValueTypes.LONG));
        });

        it("given a decimal number as string then a Value with null is returned", () => {
            expect(ValueTypes.LONG.newValue("1.1")).toEqual(new Value(null, ValueTypes.LONG));
        });

        it("given an empty string then a Value with null is returned", () => {
            expect(ValueTypes.LONG.newValue("")).toEqual(new Value(null, ValueTypes.LONG));
        });
    });

});