describe("api.data.DataSet", function () {

    var DataSet = api.data.DataSet;
    var ValueTypes = api.data.type.ValueTypes;

    describe("when getProperty", function () {

        describe("given a DataSet with a property named 'myProp'", function () {

            var dataSet = new DataSet("mySet");
            var value = ValueTypes.STRING.newValue("myVal");
            dataSet.addProperty("myProp", value);

            it("when getProperty with name 'myProp' then not null is returned", function () {
                expect(dataSet.getProperty("myProp")).not.toBe(null);
            });

            it("when getProperty with name 'noProp' then null is returned", function () {
                expect(dataSet.getProperty("nonExisting")).toBeNull();
            });

            it("when getProperty with DataPath('myProp') then not null is returned", function () {
                expect(dataSet.getProperty(api.data.DataPath.fromString("myProp"))).not.toBeNull();
            });

            it("when getProperty with a DataId(name='myProp' and index=0) then not null is returned", function () {
                var dataSet = new DataSet("mySet");
                dataSet.addProperty("myProp", ValueTypes.STRING.newValue("myVal"));
                expect(dataSet.getProperty(new api.data.DataId("myProp", 0))).not.toBeNull();
            });
        });
    });
});