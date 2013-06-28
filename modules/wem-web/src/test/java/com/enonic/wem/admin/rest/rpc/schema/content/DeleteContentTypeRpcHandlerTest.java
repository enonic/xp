package com.enonic.wem.admin.rest.rpc.schema.content;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.jsonrpc.JsonRpcHandler;
import com.enonic.wem.admin.rest.rpc.AbstractRpcHandlerTest;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.schema.content.DeleteContentTypeResult;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;

import static com.enonic.wem.api.command.Commands.contentType;
import static org.mockito.Matchers.eq;

public class DeleteContentTypeRpcHandlerTest
    extends AbstractRpcHandlerTest
{
    private Client client;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        final DeleteContentTypeRpcHandler handler = new DeleteContentTypeRpcHandler();
        client = Mockito.mock( Client.class );
        handler.setClient( client );
        return handler;
    }

    @Test
    public void deleteSingleContentType()
        throws Exception
    {
        final QualifiedContentTypeName existingName = new QualifiedContentTypeName( "my:existing_content_type" );

        ContentTypeDeletionResult contentDeletionResult = new ContentTypeDeletionResult();
        contentDeletionResult.success( existingName );

        Mockito.when( client.execute( Mockito.any( contentType().delete().getClass() ) ) ).thenReturn( DeleteContentTypeResult.SUCCESS );

        testSuccess( "deleteContentType_param.json", "deleteContentType_success_result.json" );
    }

    @Test
    public void deleteVariousContentTypes()
        throws Exception
    {
        final QualifiedContentTypeName existingName = new QualifiedContentTypeName( "my:existing_content_type" );
        final QualifiedContentTypeName notFoundName = new QualifiedContentTypeName( "my:not_found_content_type" );
        final QualifiedContentTypeName beingUsedName = new QualifiedContentTypeName( "my:being_used_content_type" );

        Mockito.when( client.execute( eq( contentType().delete().name( existingName ) ) ) ).thenReturn( DeleteContentTypeResult.SUCCESS );
        Mockito.when( client.execute( eq( contentType().delete().name( notFoundName ) ) ) ).thenReturn( DeleteContentTypeResult.NOT_FOUND );
        Mockito.when( client.execute( eq( contentType().delete().name( beingUsedName ) ) ) ).thenReturn(
            DeleteContentTypeResult.UNABLE_TO_DELETE );

        testSuccess( "deleteContentType_param_multiple.json", "deleteContentType_error_result.json" );
    }

}
