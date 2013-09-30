///<reference path='../../../TestCase.d.ts' />
///<reference path='../../../../../../../../main/webapp/admin2/api/js/schema/content/form/InputTypeName.ts' />

TestCase("InputTypeNameTest", {

    "test parseInputTypeName when custom": function () {

        var inputTypeName = api_schema_content_form.InputTypeName.parseInputTypeName('custom:MyCustom');

        // exercise & verify
        assertEquals("MyCustom", inputTypeName.getName());
        assertEquals(false, inputTypeName.isBuiltIn());
    }
    ,
    "test parseInputTypeName when builtIn": function () {

        var inputTypeName = api_schema_content_form.InputTypeName.parseInputTypeName('TextLine');

        // exercise & verify
        assertEquals("TextLine", inputTypeName.getName());
        assertEquals(true, inputTypeName.isBuiltIn());
    }
});

