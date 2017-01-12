describe("api.data.type.StringValueType", () => {

    describe("when isValid", () => {

        it("given a string then true is returned", () => {
            expect(ValueTypes.STRING.isValid("a")).toBe(true);
        });

        it("given an empty string then true is returned", () => {
            expect(ValueTypes.STRING.isValid("")).toBe(true);
        });

        it("given a number then false is returned", () => {
            expect(ValueTypes.STRING.isValid(1)).toBe(false);
        });

        it("given a boolean then false is returned", () => {
            expect(ValueTypes.STRING.isValid(true)).toBe(false);
        });

        it("given a decimal number then false is returned", () => {
            expect(ValueTypes.STRING.isValid(1.1)).toBe(false);
        });
    });

    describe("when isConvertible", () => {

        it("given a  string then true is returned", () => {
            expect(ValueTypes.STRING.isConvertible("a")).toBeTruthy();
        });
    });

    describe("when newValue", () => {

        it("given a string then a new Value is returned", () => {
            expect(ValueTypes.STRING.newValue("1")).toEqual(new Value("1", ValueTypes.STRING));
        });
    });

});
