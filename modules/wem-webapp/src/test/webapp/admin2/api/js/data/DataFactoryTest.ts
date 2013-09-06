///<reference path='../TestCase.d.ts' />
///<reference path='../../../../../../main/webapp/admin2/api/js/data/Data.ts' />
///<reference path='../../../../../../main/webapp/admin2/api/js/data/Property.ts' />
///<reference path='../../../../../../main/webapp/admin2/api/js/data/DataSet.ts' />
///<reference path='../../../../../../main/webapp/admin2/api/js/data/json/DataJson.ts' />
///<reference path='../../../../../../main/webapp/admin2/api/js/data/json/DataSetJson.ts' />
///<reference path='../../../../../../main/webapp/admin2/api/js/data/json/PropertyJson.ts' />
///<reference path='../../../../../../main/webapp/admin2/api/js/data/DataFactory.ts' />

TestCase("DataFactory", {

    "test given a DataSet with two properties": function () {

        var dataSetJson = {
            "name": "mySet",
            "path": "mySet",
            "type": "DataSet",
            "value": [
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
            ]
        };
        var dataSet:api_data.DataSet = api_data.DataFactory.createDataSet(dataSetJson);

        // exercise & verify
        assertEquals("mySet", dataSet.getName());
        assertEquals(1, dataSet.getPropertiesByName("prop1").length);
        assertEquals(1, dataSet.getPropertiesByName("prop2").length);
        assertEquals("1", dataSet.getPropertiesByName("prop1")[0].getValue());
        assertEquals("2", dataSet.getPropertiesByName("prop2")[0].getValue());
    }

});

