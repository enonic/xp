describe("api.data.PropertyTreeTest", function () {

    var PropertySet = api.data.PropertySet;
    var PropertyTree = api.data.PropertyTree;
    var PropertyChangedEvent = api.data.PropertyChangedEvent;
    var PropertyChangedEventType = api.data.PropertyChangedEventType;
    var ValueTypes = api.data.ValueTypes;
    var Value = api.data.Value;
    var PropertyPath = api.data.PropertyPath;

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

    describe("when getPropertyById", function () {

        it("given a Property then not null is returned", function () {
            var tree = new PropertyTree();
            var p1 = tree.addString("myProp", "1");
            var p2 = tree.addString("myProp", "2");
            expect(tree.getPropertyById(p1.getId())).toBe(p1);
            expect(tree.getPropertyById(p2.getId())).toBe(p2);
        });
    });

    describe("when removeProperty", function () {

        it("given a PropertyTree with 3 properties when removing one then getTotalSize returns 2", function () {
            var tree = new PropertyTree();
            var mySet = tree.addSet("mySet");
            mySet.addStrings("myProp", ["1", "2"]);
            mySet.removeProperty("myProp", 1);

            expect(tree.getTotalSize()).toBe(2);
        });
    });

    describe("when getTotalSize", function () {

        it("given a Property then not null is returned", function () {
            var tree = new PropertyTree();
            var propertySet = tree.addSet("mySet");
            tree.addStrings("myProp", ["1", "2"]);
            expect(tree.getTotalSize()).toBe(3);
        });
    });

    describe("when onPropertyChanged", function () {

        it("adding Property to a sub set then PropertyChangedEvent of type ADDED is received", function () {

            var rootSet = new PropertyTree();
            var subSet = rootSet.addSet("mySet");

            rootSet.onPropertyChanged(function (event) {
                expect(event.getType()).toBe(PropertyChangedEventType.ADDED);
                expect(event.getPath().toString()).toBe(".mySet[0].myProp[0]");
                expect(event.getValue().asString()).toBe("myVal");
            });
            subSet.addString("myProp", "myVal");
        });

        it("changing Property in a sub set then PropertyChangedEvent of type CHANGED is received", function () {

            var rootSet = new PropertyTree();
            var subSet = rootSet.addSet("mySet");
            subSet.addString("myProp", "myVal");

            rootSet.onPropertyChanged(function (event) {
                expect(event.getType()).toBe(PropertyChangedEventType.CHANGED);
                expect(event.getPath().toString()).toBe(".mySet[0].myProp[0]");
                expect(event.getValue().asString()).toBe("changed");
            });
            subSet.addString("myProp", "changed");
        });

        it("removing Property in a sub set then PropertyChangedEvent of type REMOVED is received", function () {

            var rootSet = new PropertyTree();
            var subSet = rootSet.addSet("mySet");
            subSet.addString("myProp", "myVal");

            rootSet.onPropertyChanged(function (event) {
                expect(event.getType()).toBe(PropertyChangedEventType.REMOVED);
                expect(event.getPath().toString()).toBe(".mySet[0].myProp[0]");
                expect(event.getValue().asString()).toBe("myVal");
            });
            subSet.removeProperty("myProp", 0)
        });
    });
});