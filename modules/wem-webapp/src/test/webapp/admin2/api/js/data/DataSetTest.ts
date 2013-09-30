///<reference path='../TestCase.d.ts' />
///<reference path='../../../../../../main/webapp/admin2/api/js/data/json/_module.ts' />
///<reference path='../../../../../../main/webapp/admin2/api/js/data/_module.ts' />

TestCase("DataSet", {

    "test given a name when getName() then given name is returned": function () {

        var dataSet = new api_data.DataSet('mySet');

        // exercise & verify
        assertEquals("mySet", dataSet.getName());
    },
    "test given an existing dataId when getData() then given Data is returned": function () {

        var dataSet = new api_data.DataSet('mySet');
        dataSet.addData(api_data.Property.fromStrings('myProp', 'A value', 'String'))
        dataSet.addData(api_data.Property.fromStrings('myOtherProp', 'A value', 'String'))

        // exercise & verify
        assertEquals("myProp", dataSet.getData('myProp').getName());
        assertEquals("myOtherProp", dataSet.getData('myOtherProp').getName());
    },
    "test given a dataId not existing when getData() then no Data is returned": function () {

        var dataSet = new api_data.DataSet('mySet');
        dataSet.addData(api_data.Property.fromStrings('myProp', 'A value', 'String'))

        // exercise & verify
        assertEquals(null, dataSet.getData('myNonExistingProp'));
    },
    "test given a Data added to a DataSet when getParent() then the DataSet added to is returned": function () {

        var dataSet = new api_data.DataSet('mySet');
        dataSet.addData(api_data.Property.fromStrings('myProp', 'A value', 'String'))

        var data = dataSet.getData('myProp');

        // exercise & verify
        assertEquals(dataSet, data.getParent());
    },
    "test given two data with same name when nameCount then two is returned": function () {

        var dataSet = new api_data.DataSet('mySet');
        dataSet.addData(api_data.Property.fromStrings('myProp', 'A', 'String'))
        dataSet.addData(api_data.Property.fromStrings('myProp', 'B', 'String'))

        // exercise & verify
        assertEquals(2, dataSet.nameCount('myProp'));
    },
    "test given Data with arrayIndex one when getData equal DataId then Data with arrayIndex one is returned": function () {

        var dataSet = new api_data.DataSet('mySet');
        dataSet.addData(api_data.Property.fromStrings('myProp', 'A', 'String'))
        dataSet.addData(api_data.Property.fromStrings('myProp', 'B', 'String'))

        assertEquals("myProp[1]", dataSet.getData('myProp[1]').getId().toString());
    },
    "test given two Data with same name and one different when getDataByName then two Data is returned": function () {

        var dataSet = new api_data.DataSet('mySet');
        dataSet.addData(api_data.Property.fromStrings('myInput', 'A', 'String'))
        dataSet.addData(api_data.Property.fromStrings('myOtherInput', 'B', 'String'))
        dataSet.addData(api_data.Property.fromStrings('myInput', 'C', 'String'))

        var dataArray:api_data.Data[] = dataSet.getDataByName('myInput');

        assertEquals(2, dataArray.length);
    }

});

