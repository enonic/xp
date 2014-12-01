describe("api.data.type.ValueTypeDataTest", function () {

    var Value = api.data.Value;
    var ValueTypes = api.data.ValueTypes;
    var PropertyTree = api.data.PropertyTree;

    describe("when isValid", function () {

        it("given a 'api.data.PropertySet' then true is returned", function () {
            var tree = new PropertyTree();
            var mySet = tree.newSet();
            expect(ValueTypes.DATA.isValid(mySet)).toBe(true);
        });
    });
});