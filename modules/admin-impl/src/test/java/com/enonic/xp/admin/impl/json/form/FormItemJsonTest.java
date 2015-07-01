package com.enonic.xp.admin.impl.json.form;


import java.io.IOException;

import org.junit.Test;

import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.FieldSetJson;
import com.enonic.xp.form.FormItemJson;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.FormItemSetJson;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.InputJson;
import com.enonic.xp.form.inputtype.InputTypes;
import com.enonic.xp.form.inputtype.NullConfig;
import com.enonic.xp.support.JsonTestHelper;

import static org.junit.Assert.*;

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
        InputJson inputJson = new InputJson( Input.create().
            name( "myTextLine" ).
            label( "My TextLine" ).
            immutable( true ).
            indexed( true ).
            maximizeUIInputWidth( true ).
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
        InputJson inputJson = new InputJson( Input.create().
            name( "myTextLine" ).
            label( "My TextLine" ).
            immutable( true ).
            indexed( true ).
            validationRegexp( "script" ).
            customText( "Custom text" ).
            helpText( "Help text" ).
            occurrences( 1, 3 ).
            inputType( InputTypes.TEXT_AREA ).
            inputTypeConfig( NullConfig.create() ).
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
            addFormItem( Input.create().name( "myTextLine" ).label( "myTextLine" ).inputType( InputTypes.TEXT_LINE ).build() ).
            addFormItem( Input.create().name( "myDate" ).label( "myDate" ).inputType( InputTypes.DATE ).build() ).
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
            addFormItem( Input.create().name( "myTextLine" ).label( "myTextLine" ).inputType( InputTypes.TEXT_LINE ).build() ).
            addFormItem( Input.create().name( "myDate" ).label( "myDate" ).inputType( InputTypes.DATE ).build() ).
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
