package com.enonic.wem.admin.json.form;


import java.io.IOException;

import org.junit.Test;

import com.enonic.wem.api.form.FieldSet;
import com.enonic.wem.api.form.FormItemSet;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.form.inputtype.TextAreaConfig;
import com.enonic.wem.api.support.JsonTestHelper;

import static junit.framework.Assert.assertEquals;

public class FormItemJsonTest
{

    private JsonTestHelper jsonTestHelper;

    public FormItemJsonTest()
    {
        jsonTestHelper = new JsonTestHelper( this, true );
    }

    @Test
    public void deserialize_serialization_of_Input()
        throws IOException
    {
        InputJson inputJson = new InputJson( Input.newInput().
            name( "myTextLine" ).
            label( "My TextLine" ).
            immutable( true ).
            indexed( true ).
            validationRegexp( "script" ).
            customText( "Custom text" ).
            helpText( "Help text" ).
            occurrences( 1, 3 ).
            inputType( InputTypes.TEXT_LINE ).
            build() );

        // serialize from object
        String expectedSerialization = jsonTestHelper.objectToString( inputJson );

        // de-serialize
        FormItemJson parsedFormItem = jsonTestHelper.objectMapper().readValue( expectedSerialization, FormItemJson.class );

        // serialize from json
        String serializationOfDeSerialization = jsonTestHelper.objectToString( parsedFormItem );

        assertEquals( expectedSerialization, serializationOfDeSerialization );
    }

    @Test
    public void deserialize_serialization_of_Input_with_config()
        throws IOException
    {
        InputJson inputJson = new InputJson( Input.newInput().
            name( "myTextLine" ).
            label( "My TextLine" ).
            immutable( true ).
            indexed( true ).
            validationRegexp( "script" ).
            customText( "Custom text" ).
            helpText( "Help text" ).
            occurrences( 1, 3 ).
            inputType( InputTypes.TEXT_AREA ).
            inputTypeConfig( TextAreaConfig.newTextAreaConfig().columns( 100 ).rows( 100 ).build() ).
            build() );

        // serialize from object
        String expectedSerialization = jsonTestHelper.objectToString( inputJson );

        // de-serialize
        FormItemJson parsedFormItem = jsonTestHelper.objectMapper().readValue( expectedSerialization, FormItemJson.class );

        // serialize from json
        String serializationOfDeSerialization = jsonTestHelper.objectToString( parsedFormItem );

        assertEquals( expectedSerialization, serializationOfDeSerialization );
    }

    @Test
    public void deserialize_serialization_of_FormItemSet()
        throws IOException
    {
        FormItemSetJson formItemSetJson = new FormItemSetJson( FormItemSet.newFormItemSet().
            name( "mySet" ).
            label( "My set" ).
            customText( "Custom text" ).
            helpText( "Help text" ).
            occurrences( 0, 10 ).
            addFormItem( Input.newInput().name( "myTextLine" ).inputType( InputTypes.TEXT_LINE ).build() ).
            addFormItem( Input.newInput().name( "myDate" ).inputType( InputTypes.DATE ).build() ).
            build() );

        // serialize from object
        String expectedSerialization = jsonTestHelper.objectToString( formItemSetJson );

        // de-serialize
        FormItemJson parsedFormItem = jsonTestHelper.objectMapper().readValue( expectedSerialization, FormItemJson.class );

        // serialize from json
        String serializationOfDeSerialization = jsonTestHelper.objectToString( parsedFormItem );

        assertEquals( expectedSerialization, serializationOfDeSerialization );
    }

    @Test
    public void deserialize_serialization_of_FieldSet()
        throws IOException
    {
        FieldSetJson fieldSetJson = new FieldSetJson( FieldSet.newFieldSet().
            name( "myFieldSet" ).
            label( "My field set" ).
            addFormItem( Input.newInput().name( "myTextLine" ).inputType( InputTypes.TEXT_LINE ).build() ).
            addFormItem( Input.newInput().name( "myDate" ).inputType( InputTypes.DATE ).build() ).
            build() );

        // serialize from object
        String expectedSerialization = jsonTestHelper.objectToString( fieldSetJson );

        // de-serialize
        FormItemJson parsedFormItem = jsonTestHelper.objectMapper().readValue( expectedSerialization, FormItemJson.class );

        // serialize from json
        String serializationOfDeSerialization = jsonTestHelper.objectToString( parsedFormItem );

        assertEquals( expectedSerialization, serializationOfDeSerialization );
    }
}
