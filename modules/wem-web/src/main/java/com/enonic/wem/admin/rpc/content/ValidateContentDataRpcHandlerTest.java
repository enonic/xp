package com.enonic.wem.admin.rpc.content;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.json.rpc.JsonRpcHandler;
import com.enonic.wem.admin.rpc.AbstractRpcHandlerTest;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.ValidateContentData;
import com.enonic.wem.api.command.schema.content.GetContentTypes;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.form.Input;
import com.enonic.wem.api.schema.content.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;
import com.enonic.wem.api.schema.content.validator.MinimumOccurrencesValidationError;

import static com.enonic.wem.api.schema.content.ContentType.newContentType;
import static com.enonic.wem.api.schema.content.form.FieldSet.newFieldSet;
import static com.enonic.wem.api.schema.content.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.schema.content.form.Input.newInput;
import static org.mockito.Matchers.isA;

public class ValidateContentDataRpcHandlerTest
    extends AbstractRpcHandlerTest
{

    private Client client;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        final ValidateContentDataRpcHandler handler = new ValidateContentDataRpcHandler();

        client = Mockito.mock( Client.class );
        handler.setClient( client );

        return handler;
    }

    @Test
    public void validate_without_errors()
        throws Exception
    {
        // setup
        final ContentType contentType = newContentType().
            module( Module.SYSTEM.getName() ).
            name( "my_type" ).
            addFormItem( newFieldSet().label( "My layout" ).name( "myLayout" ).add(
                newFormItemSet().name( "mySet" ).required( true ).addFormItem(
                    newInput().name( "myInput" ).inputType( InputTypes.TEXT_LINE ).build() ).build() ).build() ).
            build();
        Mockito.when( client.execute( isA( GetContentTypes.class ) ) ).thenReturn( ContentTypes.from( contentType ) );

        final DataValidationErrors noErrors = DataValidationErrors.empty();
        Mockito.when( client.execute( isA( ValidateContentData.class ) ) ).thenReturn( noErrors );

        // test
        testSuccess( "contentValidate_param.json", "contentValidate_no_errors_result.json" );
    }

    @Test
    public void validate_with_errors()
        throws Exception
    {
        // setup
        final Input myInput = newInput().name( "my_input" ).inputType( InputTypes.TEXT_LINE ).minimumOccurrences( 1 ).build();
        final ContentType contentType = newContentType().
            module( Module.SYSTEM.getName() ).
            name( "my_type" ).
            addFormItem( myInput ).
            build();
        Mockito.when( client.execute( isA( GetContentTypes.class ) ) ).thenReturn( ContentTypes.from( contentType ) );

        final DataValidationErrors errors = DataValidationErrors.from( new MinimumOccurrencesValidationError( myInput, 0 ) );
        Mockito.when( client.execute( isA( ValidateContentData.class ) ) ).thenReturn( errors );

        // test
        testSuccess( "contentValidate_param.json", "contentValidate_with_errors_result.json" );
    }
}
