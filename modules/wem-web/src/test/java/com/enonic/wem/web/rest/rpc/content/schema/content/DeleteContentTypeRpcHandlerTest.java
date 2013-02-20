package com.enonic.wem.web.rest.rpc.content.schema.content;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.content.schema.content.ContentTypeDeletionResult;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.exception.ContentTypeNotFoundException;
import com.enonic.wem.api.exception.UnableToDeleteContentTypeException;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;
import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;

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
        final QualifiedContentTypeName existingName = new QualifiedContentTypeName( "my:existingContentType" );

        ContentTypeDeletionResult contentDeletionResult = new ContentTypeDeletionResult();
        contentDeletionResult.success( existingName );

        Mockito.when( client.execute( Mockito.any( Commands.contentType().delete().getClass() ) ) ).thenReturn( contentDeletionResult );

        testSuccess( "deleteContentType_param.json", "deleteContentType_success_result.json" );
    }

    @Test
    public void deleteVariousContentTypes()
        throws Exception
    {
        final QualifiedContentTypeName existingName = new QualifiedContentTypeName( "my:existingContentType" );
        final QualifiedContentTypeName notFoundName = new QualifiedContentTypeName( "my:notFoundContentType" );
        final QualifiedContentTypeName beingUsedName = new QualifiedContentTypeName( "my:beingUsedContentType" );

        ContentTypeDeletionResult contentDeletionResult = new ContentTypeDeletionResult();
        contentDeletionResult.success( existingName );
        contentDeletionResult.failure( notFoundName, new ContentTypeNotFoundException( notFoundName ) );
        contentDeletionResult.failure( beingUsedName,
                                       new UnableToDeleteContentTypeException( beingUsedName, "Content type is being used" ) );

        Mockito.when( client.execute( Mockito.any( Commands.contentType().delete().getClass() ) ) ).thenReturn( contentDeletionResult );

        testSuccess( "deleteContentType_param.json", "deleteContentType_error_result.json" );
    }

}
