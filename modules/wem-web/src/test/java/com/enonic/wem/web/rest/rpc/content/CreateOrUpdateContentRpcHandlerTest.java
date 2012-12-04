package com.enonic.wem.web.rest.rpc.content;

import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.command.content.GetContents;
import com.enonic.wem.api.command.content.UpdateContents;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.exception.ContentNotFoundException;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;
import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;

import static com.enonic.wem.api.content.Content.newContent;
import static org.mockito.Matchers.isA;

public class CreateOrUpdateContentRpcHandlerTest
    extends AbstractRpcHandlerTest
{

    private Client client;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        final CreateOrUpdateContentRpcHandler handler = new CreateOrUpdateContentRpcHandler();

        client = Mockito.mock( Client.class );
        handler.setClient( client );

        return handler;
    }

    @Test
    public void testRequestCreateContent()
        throws Exception
    {
        final ContentPath contentPath = ContentPath.from( "/myContent/childContent" );
        Mockito.when( client.execute( isA( CreateContent.class ) ) ).thenReturn( contentPath );
        Mockito.when( client.execute( isA( GetContents.class ) ) ).thenReturn( Contents.empty() );

        final ObjectNode resultJson = objectNode();
        resultJson.put( "success", true );
        resultJson.put( "created", true );
        resultJson.put( "updated", false );
        testSuccess( "createOrUpdateContent_param.json", resultJson );
    }

    @Test
    public void testRequestUpdateContent()
        throws Exception
    {
        Mockito.when( client.execute( isA( UpdateContents.class ) ) ).thenReturn( 1 );
        final Content content = newContent().build();
        final Contents contents = Contents.from( content );
        Mockito.when( client.execute( isA( GetContents.class ) ) ).thenReturn( contents );

        final ObjectNode resultJson = objectNode();
        resultJson.put( "success", true );
        resultJson.put( "created", false );
        resultJson.put( "updated", true );
        testSuccess( "createOrUpdateContent_param.json", resultJson );
    }

    @Test
    public void testRequestCreateContent_missing_parent_path()
        throws Exception
    {
        final ContentPath contentPath = ContentPath.from( "/myContent/childContent" );
        Mockito.when( client.execute( isA( CreateContent.class ) ) ).thenThrow( new ContentNotFoundException( contentPath ) );
        GetContents getContents = Commands.content().get().paths( ContentPaths.from( contentPath ) );
        Mockito.when( client.execute( getContents ) ).thenReturn( Contents.empty() );

        final ObjectNode resultJson = objectNode();
        resultJson.put( "success", false );
        resultJson.put( "error", "Unable to create content. Path [myContent] does not exist" );
        testSuccess( "createOrUpdateContent_param.json", resultJson );
    }

}
