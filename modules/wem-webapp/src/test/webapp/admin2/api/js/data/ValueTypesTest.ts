///<reference path='../TestCase.d.ts' />
///<reference path='../../../../../../main/webapp/admin2/api/js/data/_module.ts' />

TestCase("ValueTypes", {

    "test fromName": function () {

        var valueType = api_data.ValueTypes.fromName("Text" );
        assertEquals("Text", valueType.toString());
    }
});

