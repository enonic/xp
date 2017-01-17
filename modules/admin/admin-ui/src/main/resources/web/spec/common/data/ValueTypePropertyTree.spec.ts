describe('api.data.type.ValueTypeData', () => {

    describe('when isValid', () => {

        it("given a 'api.data.PropertySet' then true is returned", () => {
            let tree = new PropertyTree();
            let mySet = tree.newPropertySet();
            expect(ValueTypes.DATA.isValid(mySet)).toBe(true);
        });
    });
});
