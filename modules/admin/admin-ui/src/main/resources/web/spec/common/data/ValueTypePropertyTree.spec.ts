describe("api.data.type.ValueTypeDataTest", () => {

    describe("when isValid", () => {

        it("given a 'api.data.PropertySet' then true is returned", () => {
            var tree = new PropertyTree();
            var mySet = tree.newPropertySet();
            expect(ValueTypes.DATA.isValid(mySet)).toBe(true);
        });
    });
});