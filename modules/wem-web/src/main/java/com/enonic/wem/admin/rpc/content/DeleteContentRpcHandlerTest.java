package com.enonic.wem.admin.rpc.content;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.json.rpc.JsonRpcHandler;
import com.enonic.wem.admin.rpc.AbstractRpcHandlerTest;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.content.DeleteContentResult;

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
        Mockito.when( client.execute( Mockito.any( Commands.content().delete().getClass() ) ) ).thenReturn( DeleteContentResult.SUCCESS );

        testSuccess( "deleteContent_successful_deletion_of_one_content_param.json",
                     "deleteContent_successful_deletion_of_one_content_result.json" );
    }

    @Test
    public void failed_deletion_of_one_content()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.any( Commands.content().delete().getClass() ) ) ).thenReturn( DeleteContentResult.NOT_FOUND );

        testSuccess( "deleteContent_failed_deletion_of_one_content_param.json",
                     "deleteContent_failed_deletion_of_one_content_result.json" );
    }

    @Test
    public void failed_deletion_of_two_content()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.any( Commands.content().delete().getClass() ) ) ).thenReturn( DeleteContentResult.NOT_FOUND,
                                                                                                            DeleteContentResult.UNABLE_TO_DELETE );

        ObjectNode param = objectNode();
        ArrayNode pathsToDelete = arrayNode();
        pathsToDelete.add( "/parent/childToDelete1" );
        pathsToDelete.add( "/parent/childToDelete2" );
        param.put( "contentPaths", pathsToDelete );

        testSuccess( param, "deleteContent_failed_deletion_of_two_content_result.json" );
    }
}
