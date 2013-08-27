///<reference path='../TestCase.d.ts' />
///<reference path='../../../../../../main/webapp/admin2/api/js/data/Data.ts' />
///<reference path='../../../../../../main/webapp/admin2/api/js/data/Property.ts' />

TestCase("Property", {

    "test given a name when getName() then given name is returned": function () {

        var property = new api_data.Property('myProp', 'A value', 'String' );

        assertEquals("myProp", property.getName());
    }
    ,
    "test given a value when getValue() then given value is returned": function () {

        var property = new api_data.Property('myProp', 'A value', 'String' );

        assertEquals("A value", property.getValue());
    }
    ,
    "test given a type when getType() then given type is returned": function () {

        var property = new api_data.Property('myProp', 'A value', 'String' );

        assertEquals("String", property.getType());
    }
});

