///<reference path='../TestCase.d.ts' />
///<reference path='../../../../../../main/webapp/admin2/api/js/data/_module.ts' />

TestCase("Property", {

    "test given a name when getName() then given name is returned": function () {

        var property = api_data.Property.fromStrings('myProp', 'A value', 'Text' );

        assertEquals("myProp", property.getName());
    }
    ,
    "test given a value when getValue() then given value is returned": function () {

        var property = api_data.Property.fromStrings('myProp', 'A value', 'Text' );

        assertEquals("A value", property.getString());
    }
    ,
    "test given a type when getType() then given type is returned": function () {

        var property = api_data.Property.fromStrings('myProp', 'A value', 'Text' );

        assertEquals("Text", property.getType().toString());
    }
});

