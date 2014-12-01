describe("api.data2.type.ValueTypeDataTest", function () {

    var Value = api.data2.Value;
    var ValueTypes = api.data2.ValueTypes;
    var PropertyTree = api.data2.PropertyTree;

    describe("when isValid", function () {

        it("given a 'api.data2.PropertySet' then true is returned", function () {
            var tree = new PropertyTree();
            var mySet = tree.newSet();
            expect(ValueTypes.DATA.isValid(mySet)).toBe(true);
        });
    });
});