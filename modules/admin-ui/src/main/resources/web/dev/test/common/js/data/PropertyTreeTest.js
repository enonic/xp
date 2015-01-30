describe("api.data.PropertyTreeTest", function () {

    var PropertySet = api.data.PropertySet;
    var PropertyTree = api.data.PropertyTree;
    var PropertyEventType = api.data.PropertyEventType;
    var PropertyValueChangedEvent = api.data.PropertyValueChangedEvent;
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

    describe("when getPropertyById", function () {

        it("given a detached PropertySet added to tree then not null is returned for property within added PropertySet", function () {
            var tree = new PropertyTree();
            tree.addString("a", "b");

            var detachedSet = new PropertySet();
            var p1 = detachedSet.addString("myProp", "1");
            tree.addPropertySet("mySet", detachedSet);

            expect(p1.getId()).not.toBe(null);
            expect(p1.getId().toString()).toBe("3");

            expect(tree.getPropertyById(p1.getId())).toBe(p1);
        });
    });

    describe("when removeProperty", function () {

        it("given a PropertyTree with 3 properties when removing one then getTotalSize returns 2", function () {
            var tree = new PropertyTree();
            var mySet = tree.addPropertySet("mySet");
            mySet.addStrings("myProp", ["1", "2"]);
            mySet.removeProperty("myProp", 1);

            expect(tree.getTotalSize()).toBe(2);
        });
    });

    describe("when getTotalSize", function () {

        it("given a Property then not null is returned", function () {
            var tree = new PropertyTree();
            tree.addPropertySet("mySet");
            tree.addStrings("myProp", ["1", "2"]);
            expect(tree.getTotalSize()).toBe(3);
        });
    });

    describe("when copy", function () {

        it("given generateNewPropertyIds as false then copied PropertyTree has properties with equal ids", function () {
            var originalTree = new PropertyTree();
            originalTree.addString("myProp", "1");
            var originalSet = originalTree.addPropertySet("mySet");
            originalSet.addString("myProp", "2");

            // exercise
            var copiedTree = originalTree.copy(false);

            // verify
            expect(copiedTree.getProperty("myProp").getId()).toEqual(originalTree.getProperty("myProp").getId());
            expect(copiedTree.getProperty("mySet").getId()).toEqual(originalTree.getProperty("mySet").getId());
            expect(copiedTree.getProperty("mySet.myProp").getId()).toEqual(originalTree.getProperty("mySet.myProp").getId());

            expect(copiedTree.equals(originalTree)).toBeTruthy();
        });

        it("given generateNewPropertyIds as true then copied PropertyTree has properties with different ids", function () {
            var originalTree = new PropertyTree();
            originalTree.addString("myProp", "1");
            var originalSet = originalTree.addPropertySet("mySet");
            originalSet.addString("myProp", "2");

            // exercise
            var copiedTree = originalTree.copy(true);

            // verify
            expect(copiedTree.getProperty("myProp").getId()).not.toEqual(originalTree.getProperty("myProp").getId());
            expect(copiedTree.getProperty("mySet").getId()).not.toEqual(originalTree.getProperty("mySet").getId());
            expect(copiedTree.getProperty("mySet.myProp").getId()).not.toEqual(originalTree.getProperty("mySet.myProp").getId());

            expect(copiedTree.equals(originalTree)).toBeFalsy();
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