///<reference path='../TestCase.d.ts' />
///<reference path='../../../../../../main/webapp/admin2/api/js/data/Data.ts' />
///<reference path='../../../../../../main/webapp/admin2/api/js/data/Property.ts' />
///<reference path='../../../../../../main/webapp/admin2/api/js/data/DataSet.ts' />
///<reference path='../../../../../../main/webapp/admin2/api/js/data/json/DataJson.ts' />
///<reference path='../../../../../../main/webapp/admin2/api/js/data/json/DataSetJson.ts' />
///<reference path='../../../../../../main/webapp/admin2/api/js/data/json/PropertyJson.ts' />
///<reference path='../../../../../../main/webapp/admin2/api/js/data/DataFactory.ts' />
///<reference path='../../../../../../main/webapp/admin2/api/js/content/ContentData.ts' />
///<reference path='../../../../../../main/webapp/admin2/api/js/content/ContentDataFactory.ts' />

TestCase("ContentDataFactory", {

    "test given a Data array with two properties": function () {

        var dataArray = [
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
        var contentData:api_content.ContentData = api_content.ContentDataFactory.createContentData(dataArray);

        // exercise & verify
        assertEquals("", contentData.getName());
        assertEquals(1, contentData.getPropertiesByName("prop1").length);
        assertEquals(1, contentData.getPropertiesByName("prop2").length);
        assertEquals("1", contentData.getPropertiesByName("prop1")[0].getString());
        assertEquals("2", contentData.getPropertiesByName("prop2")[0].getString());
    }

});

