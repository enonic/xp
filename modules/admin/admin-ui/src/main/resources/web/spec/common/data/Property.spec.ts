describe("api.data.PropertyTest", () => {

    describe("when getName", () => {

        it("given a Property with name 'myProp' then 'myProp' is returned", () => {
            var tree = new PropertyTree();
            var property = tree.setString("myProp", 0, "myValue");
            expect(property).not.toBeNull();
            expect(property.getName()).toBe("myProp");
        });
    });

    describe("when getIndex", () => {

        it("given a Property with index 0 then 0 is returned", () => {
            var tree = new PropertyTree();
            var property = tree.setString("myProp", 0, "myValue");
            expect(property).not.toBeNull();
            expect(property.getIndex()).toBe(0);
        });

        it("given a Property with index 1 then 1 is returned", () => {
            var tree = new PropertyTree();
            tree.setString("myProp", 0, "myValue");
            var property = tree.setString("myProp", 1, "myValue");
            expect(property).not.toBeNull();
            expect(property.getIndex()).toBe(1);
        });
    });

    describe("when getPath", () => {

        it("given a Property named 'myProp' which has root as parent then '.myProp' is returned", () => {
            var tree = new PropertyTree();
            var property = tree.setString("myProp", 0, "myValue");
            expect(property).not.toBeNull();
            expect(property.getPath().toString()).toBe(".myProp");
        });

        it("given a Property named 'myProp' which has root as parent then '.myProp' is returned", () => {
            var tree = new PropertyTree();
            var propertySet = tree.addPropertySet("mySet");
            var property = propertySet.setString("myProp", 0, "myValue");
            expect(property).not.toBeNull();
            expect(property.getPath().toString()).toBe(".mySet.myProp");
        });
    });

    describe("when getParentProperty", () => {

        it("given a Property having root as parent then null returned", () => {
            var tree = new PropertyTree();
            var property = tree.setString("myProp", 0, "myValue");
            expect(property).not.toBeNull();
            expect(property.getParentProperty()).toBeNull();
            expect(property.hasParentProperty()).toBeFalsy();
        });

        it("given a Property having a sub-set as parent then the Property of that sub-set is returned", () => {
            var tree = new PropertyTree();
            var propertySet = tree.addPropertySet("mySet");
            var property = propertySet.setString("myProp", 0, "myValue");
            expect(property).not.toBeNull();
            expect(property.getParentProperty()).toBe(tree.getProperty("mySet"));
            expect(property.hasParentProperty()).toBeTruthy();
        });
    });

    describe("when getParent", () => {

        it("given a Property having root as parent then the PropertyTree.root is returned", () => {
            var tree = new PropertyTree();
            var property = tree.setString("myProp", 0, "myValue");
            expect(property).not.toBeNull();
            expect(property.getParent()).toBe(tree.getRoot());
        });

        it("given a Property having a sub-set as parent then that sub-set is returned", () => {
            var tree = new PropertyTree();
            var propertySet = tree.addPropertySet("mySet");
            var property = propertySet.setString("myProp", 0, "myValue");
            expect(property).not.toBeNull();
            expect(property.getParent()).toBe(propertySet);
        });
    });

    describe("when getValue", () => {

        it("given a Property with a string value then the same string value is returned", () => {
            var tree = new PropertyTree();
            var property = tree.setString("myProp", 0, "myValue");
            expect(property).not.toBeNull();
            expect(property.getValue()).not.toBeNull();
            expect(property.getValue().getObject()).toBe("myValue");
        });
    });

    describe("when getType", () => {

        it("given a Property with ValueType String then ValueTypes.STRING is returned", () => {
            var tree = new PropertyTree();
            var property = tree.setString("myProp", 0, "myValue");
            expect(property.getType()).toBe(ValueTypes.STRING);
        });
    });

    describe("when hasNullValue", () => {

        it("given a Property with a value then false is returned", () => {
            var tree = new PropertyTree();
            var property = tree.setString("myProp", 0, "myValue");
            expect(property).not.toBeNull();
            expect(property.hasNullValue()).toBeFalsy();
        });
    });

    describe("when hasNonNullValue", () => {

        it("when getting a Property with a value then true is returned", () => {
            var tree = new PropertyTree();
            var property = tree.setString("myProp", 0, "myValue");
            expect(property).not.toBeNull();
            expect(property.hasNonNullValue()).toBeTruthy();
        });
    });
});