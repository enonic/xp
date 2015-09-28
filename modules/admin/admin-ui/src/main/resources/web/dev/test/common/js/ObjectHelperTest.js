describe("api.ObjectHelperTest", function () {

    describe("objectEquals", function () {

        it("given strings 'a' and 'b' then false should be returned", function () {
            expect(api.ObjectHelper.objectEquals("a", "b")).toBeFalsy();
        });

    });

    describe("string equality", function () {

        it("given two String instances of 'a' then identity equals returns false", function () {
            expect(new String("a") === new String("a")).toBeFalsy();
        });

        it("given two 'a' then identity equals returns true", function () {
            expect("a" === "a").toBeTruthy();
        });

    });

    describe("when stringEquals", function () {

        it("given two 'a' then true is returned", function () {
            expect(api.ObjectHelper.stringEquals("a", "a")).toBeTruthy();
        });
    });

    describe("when stringEquals", function () {

        it("given two instances of 'a' then true is returned", function () {
            expect(api.ObjectHelper.stringEquals(new String("a"), new String("a"))).toBeTruthy();
        });

    });
});