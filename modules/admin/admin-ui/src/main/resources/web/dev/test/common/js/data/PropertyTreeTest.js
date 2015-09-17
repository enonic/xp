describe("api.data.PropertyTreeTest", function () {

    var PropertySet = api.data.PropertySet;
    var PropertyTree = api.data.PropertyTree;
    var PropertyEventType = api.data.PropertyEventType;

    describe("when getRoot", function () {

        it("when getRoot() then PropertySet is returned", function () {

            var tree = new PropertyTree();
            var root = tree.getRoot();
            expect(root).not.toBeNull();
            expect(api.ObjectHelper.iFrameSafeInstanceOf(root, PropertySet)).toBeTruthy();
            expect(root.getTree()).toBe(tree);
            expect(root.getProperty()).toBeNull();
        });

    });

    describe("when removeProperty", function () {

        it("given a PropertyTree with 3 properties removing one", function () {
            var tree = new PropertyTree();
            var mySet = tree.addPropertySet("mySet");
            mySet.addStrings("myProp", ["1", "2"]);
            mySet.removeProperty("myProp", 1);

            expect(mySet.getProperty("myProp", 1)).toBeNull();
        });
    });

    describe("when onPropertyChanged", function () {

        it("adding Property to a sub set then PropertyEvent of type ADDED is received", function () {

            var rootSet = new PropertyTree();
            var subSet = rootSet.addPropertySet("mySet");

            rootSet.onPropertyAdded(function (event) {
                expect(event.getType()).toBe(PropertyEventType.ADDED);
                expect(event.getPath().toString()).toBe(".mySet.myProp");
                expect(event.getProperty().getString()).toBe("myVal");
            });
            subSet.addString("myProp", "myVal");
        });
    });

    describe("when onPropertyValueChanged", function () {

        it("changing Property in a sub set then PropertyEvent of type CHANGED is received", function () {

            var rootSet = new PropertyTree();
            var subSet = rootSet.addPropertySet("mySet");
            subSet.addString("myProp", "myVal");

            rootSet.onPropertyValueChanged(function (event) {
                expect(event.getType()).toBe(PropertyEventType.VALUE_CHANGED);
                expect(event.getPath().toString()).toBe(".mySet.myProp");
                expect(event.getValue().getString()).toBe("changed");
            });
            subSet.addString("myProp", "changed");
        });
    });

    describe("when onPropertyRemoved", function () {

        it("removing Property in a sub set then PropertyEvent of type REMOVED is received", function () {

            var rootSet = new PropertyTree();
            var subSet = rootSet.addPropertySet("mySet");
            subSet.addString("myProp", "myVal");

            rootSet.onPropertyRemoved(function (event) {
                expect(event.getType()).toBe(PropertyEventType.REMOVED);
                expect(event.getPath().toString()).toBe(".mySet.myProp");
                expect(event.getProperty().getString()).toBe("myVal");
            });
            subSet.removeProperty("myProp", 0)
        });
    });
});