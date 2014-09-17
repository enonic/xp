describe("api.data.type.DataValueType", function () {

    var Value = api.data.Value;
    var ValueTypes = api.data.type.ValueTypes;

    describe("when isValid", function () {

        it("given a 'api.data.RootDataSet' then true is returned", function () {
            var data = new api.data.RootDataSet();
            data.addProperty("myProp", ValueTypes.STRING.newValue("myVaue"));
            expect(ValueTypes.DATA.isValid(data)).toBe(true);
        });
    });
});