///<reference path='../../TestCase.d.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/content/data/Data.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/content/data/Property.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/content/data/DataSet.ts' />

TestCase("DataSet", {

    "test given a name when getName() then given name is returned": function () {

        var dataSet = new API_content_data.DataSet('mySet');

        assertEquals("mySet", dataSet.getName());
    },
    "test given a dataId when getData() then given Data is returned": function () {

        var dataSet = new API_content_data.DataSet('mySet');
        dataSet.addData(new API_content_data.Property('myProp', 'A value', 'String'))
        dataSet.addData(new API_content_data.Property('myOtherProp', 'A value', 'String'))
        assertEquals("myProp", dataSet.getData('myProp').getName());
        assertEquals("myOtherProp", dataSet.getData('myOtherProp').getName());
    },
    "test given a Data added to a DataSet when getParent() then the DataSet added to is returned": function () {

        var dataSet = new API_content_data.DataSet('mySet');
        dataSet.addData(new API_content_data.Property('myProp', 'A value', 'String'))

        var data = dataSet.getData('myProp');
        assertEquals(dataSet, data.getParent());
    },
    "test xxx": function () {

        var dataSet = new API_content_data.DataSet('mySet');
        dataSet.addData(new API_content_data.Property('myProp', 'A', 'String'))
        dataSet.addData(new API_content_data.Property('myProp', 'B', 'String'))

        assertEquals(2, dataSet.dataCount('myProp'));
    }
});

