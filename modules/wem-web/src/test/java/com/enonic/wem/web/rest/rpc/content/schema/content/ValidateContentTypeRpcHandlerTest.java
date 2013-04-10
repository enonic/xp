package com.enonic.wem.web.rest.rpc.content.schema.content;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.schema.content.ValidateContentType;
import com.enonic.wem.api.content.schema.content.ContentType;
import com.enonic.wem.api.content.schema.content.validator.ContentTypeValidationError;
import com.enonic.wem.api.content.schema.content.validator.ContentTypeValidationResult;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;
import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;

import static org.mockito.Matchers.isA;

public class ValidateContentTypeRpcHandlerTest
    extends AbstractRpcHandlerTest
{

    private Client client;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        final ValidateContentTypeRpcHandler handler = new ValidateContentTypeRpcHandler();

        client = Mockito.mock( Client.class );
        handler.setClient( client );

        return handler;
    }

    @Test
    public void validate_invalid_xml()
        throws Exception
    {
        // setup
        final ContentTypeValidationResult contentTypeValidationResult = null;
        Mockito.when( client.execute( isA( ValidateContentType.class ) ) ).thenReturn( contentTypeValidationResult );

        // test
        testSuccess( "contentTypeValidate_param_error.json", "contentTypeValidate_invalid_xml_result.json" );
    }

    @Test
    public void validate_without_errors()
        throws Exception
    {
        // setup
        final ContentTypeValidationResult noErrors = ContentTypeValidationResult.empty();
        Mockito.when( client.execute( isA( ValidateContentType.class ) ) ).thenReturn( noErrors );

        // test
        testSuccess( "contentTypeValidate_param.json", "contentTypeValidate_no_errors_result.json" );
    }

    @Test
    public void validate_with_errors()
        throws Exception
    {
        // setup
        final ContentType contentType1 = ContentType.newContentType().module( ModuleName.from( "mymodule" ) ).name( "contentType" ).build();
        final ContentType contentType2 = ContentType.newContentType( contentType1 ).name( "my type2" ).build();
        final ContentTypeValidationError error1 = new ContentTypeValidationError( "Validation error message 1", contentType1 );
        final ContentTypeValidationError error2 = new ContentTypeValidationError( "Validation error message 2", contentType2 );
        final ContentTypeValidationResult validationErrors = ContentTypeValidationResult.from( error1, error2 );
        Mockito.when( client.execute( isA( ValidateContentType.class ) ) ).thenReturn( validationErrors );

        // test
        testSuccess( "contentTypeValidate_param.json", "contentTypeValidate_with_errors_result.json" );
    }
}
