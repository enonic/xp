describe("Path", function () {

    it("toString", function () {

        expect(new api_rest.Path(["a"]).toString()).toBe("/a");
        expect(new api_rest.Path(["a", "b"]).toString()).toBe("/a/b");
        expect(api_rest.Path.fromString("a/b").toString()).toBe("a/b");
        expect(api_rest.Path.fromString("/a/b").toString()).toBe("/a/b");
        expect(api_rest.Path.fromParent(api_rest.Path.fromString("a"), "b").toString()).toBe("a/b");
        expect(api_rest.Path.fromParent(api_rest.Path.fromString("/a"), "b").toString()).toBe("/a/b");

    });

    it("Path.fromString given path with one element then one element is returned from getElements", function () {

        var elements = api_rest.Path.fromString("/parent").getElements();
        expect(elements.length).toBe(1);
        expect(elements[0]).toBe("parent");

    });

    it("Path.fromString given path with two elements then two elements is returned from getElements", function () {

        var elements = api_rest.Path.fromString("/parent/child").getElements();
        expect(elements.length).toBe(2);
        expect(elements[0]).toBe("parent");
        expect(elements[1]).toBe("child");

    });

    it("test Path.fromParent", function () {

        var parent = api_rest.Path.fromString("/parent");
        expect(api_rest.Path.fromParent(parent, "child").toString()).toBe("/parent/child");
        expect(api_rest.Path.fromParent(parent, "child", "grandchild").toString()).toBe("/parent/child/grandchild");

    });

});
