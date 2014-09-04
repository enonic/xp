describe("api.data.Value", function () {

    describe("when new", function () {

        it("given a letter as value and ValueType is LONG then Error is thrown", function () {

            expect(function () {

                new api.data.Value("a", api.data.type.ValueTypes.LONG)

            }).toThrow(new Error("Invalid value for type Long: a"));
        });

        it("given a number as value and ValueType is LONG then new instance is created", function () {

            expect(new api.data.Value(1, api.data.type.ValueTypes.LONG)).not.toBe(null);
        });
    });

    describe("when getDate", function () {

        it("given a number as value and ValueType is LONG then new instance is created", function () {

            expect(new api.data.Value(1, api.data.type.ValueTypes.LONG)).not.toBe(null);
        });
    });
});