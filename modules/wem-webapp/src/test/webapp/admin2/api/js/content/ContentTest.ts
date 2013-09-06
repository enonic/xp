///<reference path='../TestCase.d.ts' />
///<reference path='../../../../../../main/webapp/admin2/api/js/data/Data.ts' />
///<reference path='../../../../../../main/webapp/admin2/api/js/data/Property.ts' />
///<reference path='../../../../../../main/webapp/admin2/api/js/data/DataSet.ts' />
///<reference path='../../../../../../main/webapp/admin2/api/js/data/json/DataJson.ts' />
///<reference path='../../../../../../main/webapp/admin2/api/js/data/json/DataSetJson.ts' />
///<reference path='../../../../../../main/webapp/admin2/api/js/data/json/PropertyJson.ts' />
///<reference path='../../../../../../main/webapp/admin2/api/js/data/DataFactory.ts' />
///<reference path='../../../../../../main/webapp/admin2/api/js/item/ItemJson.ts' />
///<reference path='../../../../../../main/webapp/admin2/api/js/content/json/ContentSummaryJson.ts' />
///<reference path='../../../../../../main/webapp/admin2/api/js/content/json/ContentJson.ts' />
///<reference path='../../../../../../main/webapp/admin2/api/js/content/ContentData.ts' />
///<reference path='../../../../../../main/webapp/admin2/api/js/content/ContentDataFactory.ts' />
///<reference path='../../../../../../main/webapp/admin2/api/js/content/ContentSummary.ts' />
///<reference path='../../../../../../main/webapp/admin2/api/js/content/Content.ts' />

TestCase("Content", {

    "test given a JSON representing Content with ContentData consisting of two properties": function () {

        var contentDataJson:api_data_json.DataJson[] = [
            {
                "name": "prop1",
                "path": "mySet.prop1",
                "type": "WholeNumber",
                "value": "1"
            },
            {
                "name": "prop2",
                "path": "mySet.prop2",
                "type": "WholeNumber",
                "value": "2"
            }
        ];

        var contentJson:api_content_json.ContentJson  = <api_content_json.ContentJson>
        {
            id: "123",
            name: "mycontent",
            displayName: "My Content",
            path: "myspace:/mycontent",
            root: false,
            hasChildren: false,
            type: "Test",
            iconUrl: "http://localhost/myicon.png",
            createdTime: "2013-08-23T12:55:09.162Z",
            modifiedTime: "2013-08-23T13:55:09.162Z",
            owner: "user:system:root",
            modifier: "user:system:anonymous",
            data: contentDataJson
        };


        // exercise & verify
        var content:api_content.Content = new api_content.Content(contentJson);
        assertEquals("123", content.getId());
        assertEquals("mycontent", content.getName());
        assertEquals("My Content", content.getDisplayName());
        assertEquals("myspace:/mycontent", content.getPath().toString());
        assertEquals(false, content.isRoot());
        assertEquals(false, content.hasChildren());
        assertEquals("Test", content.getType());
        assertEquals("http://localhost/myicon.png", content.getIconUrl());
        assertEquals("2013-08-23T12:55:09.162Z", content.getCreatedTime().toISOString());
        assertEquals("2013-08-23T13:55:09.162Z", content.getModifiedTime().toISOString());
        assertEquals("user:system:root", content.getOwner());
        assertEquals("user:system:anonymous", content.getModifier());

        var contentData:api_content.ContentData = content.getContentData();
        assertEquals("", contentData.getName());
        assertEquals(1, contentData.getPropertiesByName("prop1").length);
        assertEquals(1, contentData.getPropertiesByName("prop2").length);
        assertEquals("1", contentData.getPropertiesByName("prop1")[0].getValue());
        assertEquals("2", contentData.getPropertiesByName("prop2")[0].getValue());
    }

});

