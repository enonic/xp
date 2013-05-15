///<reference path='../../TestCase.d.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/content/data/Data.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/content/data/Property.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/content/data/DataSet.ts' />

TestCase("DataSet", {

    "test given a name when getName() then given name is returned": function () {

        var dataSet = new API.content.data.DataSet({ name: 'mySet'});

        assertEquals("mySet", dataSet.getName());
    }
    ,
    "test given a dataId when getData() then given Data is returned": function () {

        var dataSet = new API.content.data.DataSet({ name: 'mySet'});
        dataSet.addData( new API.content.data.Property('myProp', 'A value', 'String' ) )
        dataSet.addData( new API.content.data.Property('myOtherProp', 'A value', 'String' ) )
        assertEquals("myProp", dataSet.getData( 'myProp').getName());
        assertEquals("myOtherProp", dataSet.getData( 'myOtherProp').getName());
    }
});

