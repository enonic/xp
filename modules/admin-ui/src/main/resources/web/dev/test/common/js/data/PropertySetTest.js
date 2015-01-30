describe("api.data.PropertySetTest", function () {

    var Property = api.data.Property;
    var PropertySet = api.data.PropertySet;
    var PropertyTree = api.data.PropertyTree;
    var PropertyChangedEvent = api.data.PropertyChangedEvent;
    var PropertyChangedEventType = api.data.PropertyChangedEventType;
    var ValueTypes = api.data.ValueTypes;
    var Value = api.data.Value;
    var PropertyPath = api.data.PropertyPath;

    describe("when addStrings", function () {

        it("given 2 string values then 2 properties are returned ", function () {
            var tree = new PropertyTree();
            var propertySet = tree.addPropertySet("mySet");
            var properties = tree.addStrings("myProp", ["1", "2"]);
            expect(properties.length).toBe(2);
            expect(properties[0].getString()).toBe("1");
            expect(properties[1].getString()).toBe("2");
        });
    });

    describe("when setProperty", function () {

        it("given ('myProp', 0) then not null is returned", function () {
            var tree = new PropertyTree();
            expect(tree.getRoot().setProperty("myProp", 0, new Value("myalue", ValueTypes.STRING))).not.toBeNull();
        });

        // TODO: Disabled: since toThrowError does not seem to work with PhantomJS
        xit("given ('myProp', 1) then Error is thrown", function () {
            var tree = new PropertyTree();
            var value = new Value("myalue", ValueTypes.STRING);
            var propertySet = tree.getRoot();
            expect(function () {
                propertySet.setProperty("myProp", 1, value);
            }).toThrowError("Index out of bounds: index: 1, size: 0");
        });
    });

    describe("when setPropertyByPath", function () {

        it("given (PropertyPath.fromString('myProp[0]')) then not null is returned", function () {
            var tree = new PropertyTree();
            expect(tree.getRoot().setPropertyByPath(PropertyPath.fromString("myProp[0]"),
                new Value("myalue", ValueTypes.STRING))).not.toBeNull();
        });

        it("given ('myProp[0]') then not null is returned", function () {
            var tree = new PropertyTree();
            var property = tree.getRoot().setPropertyByPath("myProp[0]", new Value("myalue", ValueTypes.STRING));
            expect(property).not.toBeNull();
        });

        // TODO: Disabled: since toThrowError does not seem to work with PhantomJS
        xit("given ('myProp[1]') then Error is thrown", function () {
            var tree = new PropertyTree();
            var value = new Value("myalue", ValueTypes.STRING);
            var propertySet = tree.getRoot();
            expect(function () {
                propertySet.setPropertyByPath("myProp[1]", value);
            }).toThrowError("Index out of bounds: index: 1, size: 0");
        });
    });

    describe("when getSize", function () {

        it("given a PropertySet with 3 properties when getSize returns 3", function () {
            var tree = new PropertyTree();
            var mySet = tree.addPropertySet("mySet");
            mySet.addStrings("myProp", ["1", "2"]);
            mySet.addPropertySet("subSet");

            expect(mySet.getSize()).toBe(3);
        });
    });

    describe("when removeProperty", function () {

        it("given a PropertySet with 3 properties when removing one then getTotalSize returns 2", function () {
            var tree = new PropertyTree();
            var mySet = tree.addPropertySet("mySet");
            mySet.addStrings("myProp", ["1", "2"]);
            var subSet = mySet.addPropertySet("subSet");
            subSet.addStrings("myProp", ["1", "2"]);

            mySet.removeProperty("myProp", 1);

            expect(mySet.getSize()).toBe(2);
        });
    });

    describe("when getProperty", function () {

        describe("given a PropertySet with a property named 'myProp'", function () {

            var tree = new PropertyTree();
            var mySet = tree.addPropertySet("mySet");
            var property = mySet.addProperty("myProp", new Value("myVal", ValueTypes.STRING));

            it("given name 'myProp' then not null is returned", function () {
                expect(mySet.getProperty("myProp")).toBe(property);
            });

            it("given name 'nonExisting' then null is returned", function () {
                expect(mySet.getProperty("nonExisting")).toBeNull();
            });

            it("given PropertyPath('myProp') then not null is returned", function () {
                expect(mySet.getProperty(PropertyPath.fromString("myProp"))).toBe(property);
            });

            it("given (name='myProp' and index=0) then not null is returned", function () {
                expect(mySet.getProperty("myProp", 0)).toBe(property);
            });

            it("given no arguments then the Property for the PropertySet is returned", function () {
                expect(mySet.getProperty()).toBe(tree.getProperty("mySet"));
            });
        });
    });

    describe("when forEach", function () {

        it("given two property arrays with two elements in each then iteration will iterate through all of them", function () {

            var properties = [];
            var tree = new PropertyTree();
            var mySet = tree.addPropertySet("mySet");
            properties.push(mySet.addString("a", "1"));
            properties.push(mySet.addString("a", "2"));
            properties.push(mySet.addString("b", "1"));
            properties.push(mySet.addString("b", "2"));

            mySet.forEach(function (property, index) {
                expect(property.getString()).toBe(properties[index].getString());
            });
        });
    });

    describe("when toTree", function () {

        it("given a PropertySet containing 5 properties then all 5 are available in the new PropertyTree", function () {

            var tree = new PropertyTree();
            var subSet = tree.addPropertySet("subTree");
            var mySet = subSet.addPropertySet("mySet");
            mySet.addStrings("a", ["1", "2"]);
            mySet.addStrings("b", ["1", "2"]);

            var subTree = subSet.toTree();

            expect(subTree.getString("mySet.a")).toBe("1");
            expect(subTree.getString("mySet.a[1]")).toBe("2");
            expect(subTree.getString("mySet.b[0]")).toBe("1");
            expect(subTree.getString("mySet.b[1]")).toBe("2");
            expect(subTree.getTotalSize()).toBe(5);
        });
    });
});