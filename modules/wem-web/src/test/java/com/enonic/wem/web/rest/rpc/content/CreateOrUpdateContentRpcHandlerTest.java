package com.enonic.wem.web.rest.rpc.content;

import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.command.content.GetContents;
import com.enonic.wem.api.command.content.UpdateContents;
import com.enonic.wem.api.command.content.type.GetContentTypes;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.MockContentId;
import com.enonic.wem.api.content.type.ContentTypes;
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
        CreateOrUpdateContentRpcHandler handler = new CreateOrUpdateContentRpcHandler();

        client = Mockito.mock( Client.class );
        handler.setClient( client );

        return handler;
    }

    @Test
    public void given_path_to_content_not_existing_when_handle_then_json_with_created_true_and_expected_contentId_is_returned()
        throws Exception
    {
        ContentId contentId = MockContentId.from( "123" );
        Mockito.when( client.execute( isA( GetContentTypes.class ) ) ).thenReturn( ContentTypes.empty() );
        Mockito.when( client.execute( isA( GetContents.class ) ) ).thenReturn( Contents.empty() );
        Mockito.when( client.execute( isA( CreateContent.class ) ) ).thenReturn( contentId );

        ObjectNode resultJson = objectNode();
        resultJson.put( "success", true );
        resultJson.put( "created", true );
        resultJson.put( "updated", false );
        resultJson.put( "contentId", contentId.id() );

        // exercise & verify
        testSuccess( "createOrUpdateContent_param.json", resultJson );
    }

    @Test
    public void given_path_to_content_existing_when_handle_then_json_with_updated_true_is_returned()
        throws Exception
    {
        Mockito.when( client.execute( isA( GetContentTypes.class ) ) ).thenReturn( ContentTypes.empty() );
        Mockito.when( client.execute( isA( UpdateContents.class ) ) ).thenReturn( 1 );
        Content content = newContent().build();
        Contents contents = Contents.from( content );
        Mockito.when( client.execute( isA( GetContents.class ) ) ).thenReturn( contents );

        ObjectNode expectedJson = objectNode();
        expectedJson.put( "success", true );
        expectedJson.put( "created", false );
        expectedJson.put( "updated", true );
        expectedJson.putNull( "contentId" );

        // exercise & verify
        testSuccess( "createOrUpdateContent_param.json", expectedJson );
    }

    @Test
    public void given_path_to_with_non_existing_parent_when_handle_then_error()
        throws Exception
    {
        ContentPath contentPath = ContentPath.from( "/myContent/childContent" );
        Mockito.when( client.execute( isA( GetContentTypes.class ) ) ).thenReturn( ContentTypes.empty() );
        Mockito.when( client.execute( isA( CreateContent.class ) ) ).thenThrow( new ContentNotFoundException( contentPath ) );

        GetContents getContents = Commands.content().get().selectors( ContentPaths.from( contentPath ) );
        Mockito.when( client.execute( getContents ) ).thenReturn( Contents.empty() );

        ObjectNode expectedJson = objectNode();
        expectedJson.put( "success", false );
        expectedJson.put( "error", "Unable to create content. Path [myContent] does not exist" );

        // exercise & verify
        testSuccess( "createOrUpdateContent_param.json", expectedJson );
    }

}
