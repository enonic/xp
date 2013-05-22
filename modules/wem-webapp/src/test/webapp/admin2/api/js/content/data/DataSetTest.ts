///<reference path='../../TestCase.d.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/content/data/Data.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/content/data/Property.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/content/data/DataSet.ts' />

TestCase("DataSet", {

    "test given a name when getName() then given name is returned": function () {

        var dataSet = new API_content_data.DataSet('mySet');

        // exercise & verify
        assertEquals("mySet", dataSet.getName());
    },
    "test given an existing dataId when getData() then given Data is returned": function () {

        var dataSet = new API_content_data.DataSet('mySet');
        dataSet.addData(new API_content_data.Property('myProp', 'A value', 'String'))
        dataSet.addData(new API_content_data.Property('myOtherProp', 'A value', 'String'))

        // exercise & verify
        assertEquals("myProp", dataSet.getData('myProp').getName());
        assertEquals("myOtherProp", dataSet.getData('myOtherProp').getName());
    },
    "test given a dataId not existing when getData() then no Data is returned": function () {

        var dataSet = new API_content_data.DataSet('mySet');
        dataSet.addData(new API_content_data.Property('myProp', 'A value', 'String'))

        // exercise & verify
        assertEquals(null, dataSet.getData('myNonExistingProp'));
    },
    "test given a Data added to a DataSet when getParent() then the DataSet added to is returned": function () {

        var dataSet = new API_content_data.DataSet('mySet');
        dataSet.addData(new API_content_data.Property('myProp', 'A value', 'String'))

        var data = dataSet.getData('myProp');

        // exercise & verify
        assertEquals(dataSet, data.getParent());
    },
    "test given two data with same name when dataCount then two is returned": function () {

        var dataSet = new API_content_data.DataSet('mySet');
        dataSet.addData(new API_content_data.Property('myProp', 'A', 'String'))
        dataSet.addData(new API_content_data.Property('myProp', 'B', 'String'))

        // exercise & verify
        assertEquals(2, dataSet.dataCount('myProp'));
    },
    "test given Data with arrayIndex one when getData equal DataId then Data with arrayIndex one is returned": function () {

        var dataSet = new API_content_data.DataSet('mySet');
        dataSet.addData(new API_content_data.Property('myProp', 'A', 'String'))
        dataSet.addData(new API_content_data.Property('myProp', 'B', 'String'))

        assertEquals("myProp[1]", dataSet.getData('myProp[1]').getId().toString());
    }

});

