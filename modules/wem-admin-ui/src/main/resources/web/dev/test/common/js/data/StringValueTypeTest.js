describe("api.data.type.StringValueTypeTest", function () {

    var Value = api.data.Value;
    var ValueTypes = api.data.ValueTypes;

    describe("when isValid", function () {

        it("given a string then true is returned", function () {
            expect(ValueTypes.STRING.isValid("a")).toBe(true);
        });

        it("given an empty string then true is returned", function () {
            expect(ValueTypes.STRING.isValid("")).toBe(true);
        });

        it("given a number then false is returned", function () {
            expect(ValueTypes.STRING.isValid(1)).toBe(false);
        });

        it("given a boolean then false is returned", function () {
            expect(ValueTypes.STRING.isValid(true)).toBe(false);
        });

        it("given a decimal number then false is returned", function () {
            expect(ValueTypes.STRING.isValid(1.1)).toBe(false);
        });
    });

    describe("when isConvertible", function () {

        it("given a  string then true is returned", function () {
            expect(ValueTypes.STRING.isConvertible("a")).toBeTruthy();
        });
    });

    describe("when newValue", function () {

        it("given a string then a new Value is returned", function () {
            expect(ValueTypes.STRING.newValue("1")).toEqual(new Value("1", ValueTypes.STRING));
        });
    });

});