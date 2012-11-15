package com.enonic.wem.web.rest.rpc.content;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.content.ContentDeletionResult;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.exception.UnableToDeleteContentException;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;
import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;

public class DeleteContentRpcHandlerTest
    extends AbstractRpcHandlerTest
{
    private Client client;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        final DeleteContentRpcHandler handler = new DeleteContentRpcHandler();
        client = Mockito.mock( Client.class );
        handler.setClient( client );
        return handler;
    }

    @Test
    public void successful_deletion_of_one_content()
        throws Exception
    {
        final ContentPath deletedContentPath = ContentPath.from( "/parent/childToDelete" );

        ContentDeletionResult contentDeletionResult = new ContentDeletionResult();
        contentDeletionResult.success( deletedContentPath );

        Mockito.when( client.execute( Mockito.any( Commands.content().delete().getClass() ) ) ).thenReturn( contentDeletionResult );

        testSuccess( "deleteContent_successful_deletion_of_one_content_param.json",
                     "deleteContent_successful_deletion_of_one_content_result.json" );
    }

    @Test
    public void failed_deletion_of_one_content()
        throws Exception
    {
        final ContentPath failedContentPath = ContentPath.from( "/parent/childToDelete" );

        ContentDeletionResult contentDeletionResult = new ContentDeletionResult();
        contentDeletionResult.failure( failedContentPath, new UnableToDeleteContentException( failedContentPath, "Test" ) );

        Mockito.when( client.execute( Mockito.any( Commands.content().delete().getClass() ) ) ).thenReturn( contentDeletionResult );

        testSuccess( "deleteContent_failed_deletion_of_one_content_param.json",
                     "deleteContent_failed_deletion_of_one_content_result.json" );
    }
}
