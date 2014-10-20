describe("api.ObjectHelper", function () {

    describe("objectEquals", function () {

        it("given strings 'a' and 'b' then false should be returned", function () {
            expect(api.ObjectHelper.objectEquals("a", "b")).toBeFalsy();
        });

    });
});