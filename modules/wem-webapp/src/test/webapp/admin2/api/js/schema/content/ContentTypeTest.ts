///<reference path='../../TestCase.d.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/schema/content/ContentType.ts' />

TestCase("ContentType", {

    "test getDisplayName when displayName field does not exist in json": function () {

        var json = {
            name: "mytype",
            module: "mymodule"
        };
        var contentType = new api_schema_content.ContentType(json);

        assertEquals("mytype", contentType.getName());
        assertEquals("mymodule", contentType.getModule());
        assertEquals(null, contentType.getDisplayName());
    }
});

