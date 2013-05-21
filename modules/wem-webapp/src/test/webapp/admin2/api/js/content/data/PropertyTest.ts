///<reference path='../../TestCase.d.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/content/data/Data.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/content/data/Property.ts' />

TestCase("Property", {

    "test given a name when getName() then given name is returned": function () {

        var property = new API_content_data.Property('myProp', 'A value', 'String' );

        assertEquals("myProp", property.getName());
    }
    ,
    "test given a value when getValue() then given value is returned": function () {

        var property = new API_content_data.Property('myProp', 'A value', 'String' );

        assertEquals("A value", property.getValue());
    }
    ,
    "test given a type when getType() then given type is returned": function () {

        var property = new API_content_data.Property('myProp', 'A value', 'String' );

        assertEquals("String", property.getType());
    }
});

