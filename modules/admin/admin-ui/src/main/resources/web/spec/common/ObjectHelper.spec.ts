describe("api.ObjectHelper", () => {

    describe("objectEquals", () => {

        it("given strings 'a' and 'b' then false should be returned", () => {
            expect(api.ObjectHelper.objectEquals("a", "b")).toBeFalsy();
        });

    });

    describe("string equality", () => {

        it("given two String instances of 'a' then identity equals returns false", () => {
            expect(new String("a") === new String("a")).toBeFalsy();
        });

        it("given two 'a' then identity equals returns true", () => {
            expect("a" === "a").toBeTruthy();
        });

    });

    describe("when stringEquals", () => {

        it("given two 'a' then true is returned", () => {
            expect(api.ObjectHelper.stringEquals("a", "a")).toBeTruthy();
        });
    });

    describe("when stringEquals", () => {

        it("given two instances of 'a' then true is returned", () => {
            expect(api.ObjectHelper.stringEquals(String("a"), String("a"))).toBeTruthy();
        });

    });
});