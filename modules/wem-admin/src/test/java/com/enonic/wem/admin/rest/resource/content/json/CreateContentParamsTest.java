package com.enonic.wem.admin.rest.resource.content.json;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.enonic.wem.admin.json.form.FormJson;
import com.enonic.wem.api.JsonTestHelper;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.FormItem;
import com.enonic.wem.api.form.FormItemSet;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.InputTypes;

import static junit.framework.Assert.assertEquals;

public class CreateContentParamsTest
{
    private JsonTestHelper jsonTestHelper;

    public CreateContentParamsTest()
    {
        jsonTestHelper = new JsonTestHelper( this, true );
    }

    @Test
    public void deserialize_serialization()
        throws IOException
    {

        List<FormItem> formItems = new ArrayList<>();
        Input input = Input.newInput().
            name( "myTextLine" ).
            label( "My TextLine" ).
            immutable( true ).
            indexed( true ).
            validationRegexp( "script" ).
            customText( "Custom text" ).
            helpText( "Help text" ).
            occurrences( 1, 3 ).
            inputType( InputTypes.TEXT_LINE ).build();
        formItems.add( input );

        FormItemSet formItemSet = FormItemSet.newFormItemSet().
            name( "mySet" ).
            label( "My set" ).
            customText( "Custom text" ).
            helpText( "Help text" ).
            occurrences( 0, 10 ).
            addFormItem( Input.newInput().name( "myTextLine" ).inputType( InputTypes.TEXT_LINE ).build() ).
            addFormItem( Input.newInput().name( "myDate" ).inputType( InputTypes.DATE ).build() ).
            build();
        formItems.add( formItemSet );

        CreateContentParams params = new CreateContentParams();
        params.setContentName( "test" );
        params.setDisplayName( "Test" );
        params.setContentData( jsonTestHelper.objectMapper().createArrayNode() );
        params.setForm( new FormJson( Form.newForm().addFormItems( formItems ).build() ) );

        String expectedSerialization = jsonTestHelper.objectToString( params );

        // exercise: deserialize
        CreateContentParams parsedParams = jsonTestHelper.objectMapper().readValue( expectedSerialization, CreateContentParams.class );

        String serializationOfDeserialization = jsonTestHelper.objectToString( parsedParams );

        assertEquals( expectedSerialization, serializationOfDeserialization );
    }
}
