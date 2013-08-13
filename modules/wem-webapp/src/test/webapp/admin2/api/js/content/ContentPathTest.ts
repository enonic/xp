///<reference path='../TestCase.d.ts' />
///<reference path='../../../../../../main/webapp/admin2/api/js/content/ContentPath.ts' />

TestCase("ContentPath", {

    "test toString": function () {

        assertEquals("mySpace:/parent/child", new api_content.ContentPath('mySpace', ["parent", "child"]).toString());
        assertEquals("mySpace:/", new api_content.ContentPath('mySpace', []).toString());
    },
    "test getSpaceName": function () {

        assertEquals("mySpace", new api_content.ContentPath('mySpace', ["parent", "child"]).getSpaceName());
        assertEquals("mySpace", new api_content.ContentPath('mySpace', []).getSpaceName());
    },
    "test getParentPath": function () {

        assertEquals("mySpace:/parent", new api_content.ContentPath('mySpace', ["parent", "child"]).getParentPath().toString());
        assertEquals(null, new api_content.ContentPath('mySpace', ["parent"]).getParentPath());
    }

});

