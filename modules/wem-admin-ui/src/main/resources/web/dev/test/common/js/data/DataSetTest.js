describe("api.data.DataSetTest", function () {

    var DataId = api.data.DataId;
    var DataSet = api.data.DataSet;
    var RootDataSet = api.data.RootDataSet;
    var PropertyChangedEvent = api.data.PropertyChangedEvent;
    var PropertyChangedEventType = api.data.PropertyChangedEventType;
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

    describe("when onPropertyChanged", function () {

        it("adding Property to a sub set then PropertyChangedEvent of type ADDED is received", function () {

            var rootSet = new RootDataSet();
            var subSet = new DataSet("mySet");
            rootSet.addData(subSet);

            rootSet.onPropertyChanged(function (event) {
                expect(event.getType()).toBe(PropertyChangedEventType.ADDED);
                expect(event.getPath().toString()).toBe(".mySet[0].myProp[0]");
                expect(event.getValue().asString()).toBe("myVal");
            });
            subSet.addProperty("myProp", ValueTypes.STRING.newValue("myVal"));
        });

        it("changing Property in a sub set then PropertyChangedEvent of type CHANGED is received", function () {

            var rootSet = new RootDataSet();
            var subSet = new DataSet("mySet");
            rootSet.addData(subSet);
            var property = subSet.addProperty("myProp", ValueTypes.STRING.newValue("myVal"));

            rootSet.onPropertyChanged(function (event) {
                expect(event.getType()).toBe(PropertyChangedEventType.CHANGED);
                expect(event.getPath().toString()).toBe(".mySet[0].myProp[0]");
                expect(event.getValue().asString()).toBe("changed");
            });
            property.setValue(ValueTypes.STRING.newValue("changed"));
        });

        it("removing Property in a sub set then PropertyChangedEvent of type REMOVED is received", function () {

            var rootSet = new RootDataSet();
            var subSet = new DataSet("mySet");
            rootSet.addData(subSet);
            var property = subSet.addProperty("myProp", ValueTypes.STRING.newValue("myVal"));
            rootSet.onPropertyChanged(function (event) {
                expect(event.getType()).toBe(PropertyChangedEventType.REMOVED);
                expect(event.getPath().toString()).toBe(".mySet[0].myProp[0]");
                expect(event.getValue().asString()).toBe("myVal");
            });
            subSet.removeData(new DataId("myProp", 0))
        });
    });
});