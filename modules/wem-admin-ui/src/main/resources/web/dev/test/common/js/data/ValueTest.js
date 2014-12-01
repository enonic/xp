describe("api.data.ValueTest", function () {

    var Value = api.data.Value;
    var ValueTypes = api.data.ValueTypes;

    describe("when new", function () {

        it("given a letter as value and ValueType is LONG then Error is thrown", function () {

            expect(function () {

                new Value("a", ValueTypes.LONG)

            }).toThrow(new Error("Invalid value for type Long: a"));
        });

        it("given a number as value and ValueType is LONG then new instance is created", function () {

            expect(new Value(1, ValueTypes.LONG)).not.toBe(null);
        });
    });

    describe("when isNull", function () {

        it("given a empty string as value and ValueType is STRING then false is returned", function () {

            expect(new Value("", ValueTypes.STRING).isNull()).toBeFalsy();
        });
    });

    describe("when isNotNull", function () {

        it("given a empty string as value and ValueType is STRING then true is returned", function () {

            expect(new Value("", ValueTypes.STRING).isNotNull()).toBeTruthy();
        });
    });

    describe("when getDate", function () {

        it("given a number as value and ValueType is LONG then new instance is created", function () {

            expect(new Value(1, ValueTypes.LONG)).not.toBe(null);
        });
    });
});