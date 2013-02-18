package com.enonic.wem.web.rest.rpc.content;

import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.command.content.GetContents;
import com.enonic.wem.api.command.content.UpdateContents;
import com.enonic.wem.api.command.content.type.GetContentTypes;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.MockContentId;
import com.enonic.wem.api.content.type.ContentTypes;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.exception.ContentNotFoundException;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;
import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;

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
    public void createNewContent()
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
        resultJson.put( "contentId", contentId.toString() );
        resultJson.put( "contentPath", "/myContent/my-child-content" );

        // exercise & verify
        testSuccess( "createOrUpdateContent_create_param.json", resultJson );
    }

    @Test
    public void createNewContent_with_existing_generated_name()
        throws Exception
    {
        ContentId contentId = MockContentId.from( "123" );
        Mockito.when( client.execute( isA( GetContentTypes.class ) ) ).thenReturn( ContentTypes.empty() );
        final Contents contents =
            Contents.from( Content.newContent().name( "my-child-content" ).type( QualifiedContentTypeName.unstructured() ).build() );
        Mockito.when( client.execute( isA( GetContents.class ) ) ).thenReturn( contents ).thenReturn( Contents.empty() );
        Mockito.when( client.execute( isA( CreateContent.class ) ) ).thenReturn( contentId );

        ObjectNode resultJson = objectNode();
        resultJson.put( "success", true );
        resultJson.put( "created", true );
        resultJson.put( "updated", false );
        resultJson.put( "contentId", contentId.toString() );
        resultJson.put( "contentPath", "/myContent/my-child-content-2" );

        // exercise & verify
        testSuccess( "createOrUpdateContent_create_param.json", resultJson );
    }

    @Test
    public void updateExistingContent()
        throws Exception
    {
        Mockito.when( client.execute( isA( GetContentTypes.class ) ) ).thenReturn( ContentTypes.empty() );
        Mockito.when( client.execute( isA( GetContents.class ) ) ).thenReturn( Contents.empty() );
        Mockito.when( client.execute( isA( UpdateContents.class ) ) ).thenReturn( 1 );

        ObjectNode expectedJson = objectNode();
        expectedJson.put( "success", true );
        expectedJson.put( "created", false );
        expectedJson.put( "updated", true );

        // exercise & verify
        testSuccess( "createOrUpdateContent_update_param.json", expectedJson );
    }

    @Test
    public void createContent_parent_not_found()
        throws Exception
    {
        ContentPath parentContentPath = ContentPath.from( "/myContent/childContent" ).getParentPath();
        Mockito.when( client.execute( isA( GetContentTypes.class ) ) ).thenReturn( ContentTypes.empty() );
        Mockito.when( client.execute( isA( GetContents.class ) ) ).thenReturn( Contents.empty() );
        Mockito.when( client.execute( isA( CreateContent.class ) ) ).thenThrow( new ContentNotFoundException( parentContentPath ) );

        ObjectNode expectedJson = objectNode();
        expectedJson.put( "success", false );
        expectedJson.put( "error", "Unable to create content. Path [/myContent] does not exist" );

        // exercise & verify
        testSuccess( "createOrUpdateContent_create_param.json", expectedJson );
    }
}
