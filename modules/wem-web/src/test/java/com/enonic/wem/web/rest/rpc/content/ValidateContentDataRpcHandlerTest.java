package com.enonic.wem.web.rest.rpc.content;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.ValidateRootDataSet;
import com.enonic.wem.api.command.content.schema.type.GetContentTypes;
import com.enonic.wem.api.content.schema.type.ContentType;
import com.enonic.wem.api.content.schema.type.ContentTypes;
import com.enonic.wem.api.content.schema.type.form.Input;
import com.enonic.wem.api.content.schema.type.form.inputtype.InputTypes;
import com.enonic.wem.api.content.schema.type.validator.DataValidationErrors;
import com.enonic.wem.api.content.schema.type.validator.MinimumOccurrencesValidationError;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;
import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;

import static com.enonic.wem.api.content.schema.type.ContentType.newContentType;
import static com.enonic.wem.api.content.schema.type.form.FieldSet.newFieldSet;
import static com.enonic.wem.api.content.schema.type.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.content.schema.type.form.Input.newInput;
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
            name( "MyType" ).
            addFormItem( newFieldSet().label( "My layout" ).name( "myLayout" ).add( newFormItemSet().name( "mySet" ).required( true ).add(
                newInput().name( "myInput" ).type( InputTypes.TEXT_LINE ).build() ).build() ).build() ).
            build();
        Mockito.when( client.execute( isA( GetContentTypes.class ) ) ).thenReturn( ContentTypes.from( contentType ) );

        final DataValidationErrors noErrors = DataValidationErrors.empty();
        Mockito.when( client.execute( isA( ValidateRootDataSet.class ) ) ).thenReturn( noErrors );

        // test
        testSuccess( "contentValidate_param.json", "contentValidate_no_errors_result.json" );
    }

    @Test
    public void validate_with_errors()
        throws Exception
    {
        // setup
        final Input myInput = newInput().name( "myInput" ).type( InputTypes.TEXT_LINE ).minimumOccurrences( 1 ).build();
        final ContentType contentType = newContentType().
            module( Module.SYSTEM.getName() ).
            name( "MyType" ).
            addFormItem( myInput ).
            build();
        Mockito.when( client.execute( isA( GetContentTypes.class ) ) ).thenReturn( ContentTypes.from( contentType ) );

        final DataValidationErrors errors = DataValidationErrors.from( new MinimumOccurrencesValidationError( myInput, 0 ) );
        Mockito.when( client.execute( isA( ValidateRootDataSet.class ) ) ).thenReturn( errors );

        // test
        testSuccess( "contentValidate_param.json", "contentValidate_with_errors_result.json" );
    }
}
