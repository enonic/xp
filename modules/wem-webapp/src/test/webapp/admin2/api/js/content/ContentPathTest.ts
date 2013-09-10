///<reference path='../TestCase.d.ts' />
///<reference path='../../../../../../main/webapp/admin2/api/js/content/ContentPath.ts' />

TestCase("ContentPath", {

    "test fromString": function () {

        assertEquals("mySpace", api_content.ContentPath.fromString("mySpace:/path").getSpaceName());
        assertEquals(["path"], api_content.ContentPath.fromString("mySpace:/path").getElements());
        assertEquals("/path", api_content.ContentPath.fromString("/path").toString());
        assertEquals("mySpace:/path", api_content.ContentPath.fromString("mySpace:/path").toString());
    },
    "test toString": function () {

        assertEquals("mySpace:/parent/child", new api_content.ContentPath('mySpace', ["parent", "child"]).toString());
        assertEquals("mySpace:/", new api_content.ContentPath('mySpace', []).toString());
        assertEquals("/parent/child", new api_content.ContentPath(null, ["parent", "child"]).toString());
    },
    "test getSpaceName": function () {

        assertEquals("mySpace", new api_content.ContentPath('mySpace', ["parent", "child"]).getSpaceName());
        assertEquals("mySpace", new api_content.ContentPath('mySpace', []).getSpaceName());
    },
    "test getElements": function () {

        assertEquals(["path"], new api_content.ContentPath('mySpace', ["path"]).getElements());
        assertEquals(["parent", "child"], new api_content.ContentPath('mySpace', ["parent", "child"]).getElements());
    },
    "test hasParent": function () {

        assertEquals(true, new api_content.ContentPath('mySpace', ["parent", "child"]).hasParent());
        assertEquals(true, new api_content.ContentPath('mySpace', ["child"]).hasParent());
        assertEquals(false, new api_content.ContentPath('mySpace', []).hasParent());
    },
    "test getParentPath": function () {

        assertEquals("mySpace:/parent", new api_content.ContentPath('mySpace', ["parent", "child"]).getParentPath().toString());
        assertEquals("mySpace:/", new api_content.ContentPath('mySpace', ["child"]).getParentPath().toString());
    }

});

