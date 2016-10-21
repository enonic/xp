package com.enonic.xp.admin.impl.json.form;


import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.FormOptionSet;
import com.enonic.xp.form.FormOptionSetOption;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.Occurrences;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;
import com.enonic.xp.support.JsonTestHelper;

public class FormItemJsonTest
{

    private JsonTestHelper jsonTestHelper;

    public FormItemJsonTest()
    {
        jsonTestHelper = new JsonTestHelper( this );
    }

    @Test
    public void serialization_of_Input()
        throws IOException
    {
        InputJson inputJson = new InputJson( Input.create().
            name( "myTextLine" ).
            label( "My TextLine" ).
            immutable( true ).
            indexed( true ).
            maximizeUIInputWidth( false ).
            validationRegexp( "script" ).
            customText( "Custom text" ).
            helpText( "Help text" ).
            occurrences( 1, 3 ).
            inputType( InputTypeName.TEXT_LINE ).
            build() );

        JsonNode json = jsonTestHelper.objectToJson( inputJson );
        this.jsonTestHelper.assertJsonEquals( jsonTestHelper.loadTestJson( "input.json" ), json );
    }

    @Test
    public void serialization_of_Input_with_config()
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
            inputType( InputTypeName.TEXT_AREA ).
            build() );

        JsonNode json = jsonTestHelper.objectToJson( inputJson );
        this.jsonTestHelper.assertJsonEquals( jsonTestHelper.loadTestJson( "inputWithConfig.json" ), json );
    }

    @Test
    public void serialization_of_FormItemSet()
        throws IOException
    {
        FormItemSetJson formItemSetJson = new FormItemSetJson( FormItemSet.create().
            name( "mySet" ).
            label( "My set" ).
            customText( "Custom text" ).
            helpText( "Help text" ).
            occurrences( 0, 10 ).
            addFormItem( Input.create().name( "myTextLine" ).label( "myTextLine" ).inputType( InputTypeName.TEXT_LINE ).build() ).
            addFormItem( Input.create().name( "myDate" ).label( "myDate" ).inputType( InputTypeName.DATE ).build() ).
            build() );

        JsonNode json = jsonTestHelper.objectToJson( formItemSetJson );
        this.jsonTestHelper.assertJsonEquals( jsonTestHelper.loadTestJson( "formItemSet.json" ), json );
    }

    @Test
    public void serialization_of_FieldSet()
        throws IOException
    {
        FieldSetJson fieldSetJson = new FieldSetJson( FieldSet.create().
            name( "myFieldSet" ).
            label( "My field set" ).
            addFormItem( Input.create().name( "myTextLine" ).label( "myTextLine" ).inputType( InputTypeName.TEXT_LINE ).build() ).
            addFormItem( Input.create().name( "myDate" ).label( "myDate" ).inputType( InputTypeName.DATE ).
                inputTypeProperty( InputTypeProperty.create( "timezone", "true" ).build() ).build() ).
            addFormItem( Input.create().name( "myOptions" ).label( "myOptions" ).inputType( InputTypeName.CHECK_BOX ).
                inputTypeProperty( InputTypeProperty.create( "option", "label1" ).attribute( "value", "value1" ).build() ).
                inputTypeProperty( InputTypeProperty.create( "option", "label2" ).attribute( "value", "value2" ).build() ).
                build() ).
            build() );

        JsonNode json = jsonTestHelper.objectToJson( fieldSetJson );
        this.jsonTestHelper.assertJsonEquals( jsonTestHelper.loadTestJson( "fieldSet.json" ), json );
    }

    @Test
    public void serialization_of_FormOptionSet()
        throws IOException
    {
        final FormOptionSetJson formOptionSetJson = new FormOptionSetJson( FormOptionSet.create().
            name( "myOptionSet" ).
            label( "My option set" ).
            helpText( "Help text" ).
            multiselection( Occurrences.create( 1, 3 ) ).
            addOptionSetOption( FormOptionSetOption.create().name( "myOptionSetOption1" ).label( "option label1" ).
                addFormItem(
                    Input.create().name( "myTextLine1" ).label( "myTextLine1" ).inputType( InputTypeName.TEXT_LINE ).build() ).build() ).
            addOptionSetOption( FormOptionSetOption.create().name( "myOptionSetOption2" ).label( "option label2" ).
                addFormItem(
                    Input.create().name( "myTextLine2" ).label( "myTextLine2" ).inputType( InputTypeName.TEXT_LINE ).build() ).build() ).
            addOptionSetOption( FormOptionSetOption.create().name( "myOptionSetOption3" ).label( "option label3" ).
                addFormItem(
                    Input.create().name( "myTextLine2" ).label( "myTextLine2" ).inputType( InputTypeName.TEXT_LINE ).build() ).build() ).
            build() );

        JsonNode json = jsonTestHelper.objectToJson( formOptionSetJson );
        this.jsonTestHelper.assertJsonEquals( jsonTestHelper.loadTestJson( "optionSet.json" ), json );
    }
}
