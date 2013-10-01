///<reference path='../TestCase.d.ts' />
///<reference path='../../../../../../main/webapp/admin2/api/js/rest/Path.ts' />

TestCase("Path", {

    "test toString": function () {

        assertEquals("/a", new api_rest.Path(["a"]).toString());
        assertEquals("/a/b", new api_rest.Path(["a", "b"]).toString());
        assertEquals("a/b", api_rest.Path.fromString("a/b").toString());
        assertEquals("/a/b", api_rest.Path.fromString("/a/b").toString());
        assertEquals("a/b", api_rest.Path.fromParent(api_rest.Path.fromString("a"), "b").toString());
        assertEquals("/a/b", api_rest.Path.fromParent(api_rest.Path.fromString("/a"), "b").toString());
    },
    "test Path.fromString given path with one element then one element is returned from getElements": function () {

        var elements:string[] = api_rest.Path.fromString("/parent").getElements();
        assertEquals(1, elements.length);
        assertEquals("parent", elements[0]);
    },
    "test Path.fromString given path with two elements then two elements is returned from getElements": function () {

        var elements:string[] = api_rest.Path.fromString("/parent/child").getElements();
        assertEquals(2, elements.length);
        assertEquals("parent", elements[0]);
        assertEquals("child", elements[1]);
    },
    "test Path.fromParent": function () {

        var parent = api_rest.Path.fromString("/parent");
        assertEquals("/parent/child", api_rest.Path.fromParent(parent, "child").toString());
        assertEquals("/parent/child/grandchild", api_rest.Path.fromParent(parent, "child", "grandchild").toString());
    }
});

