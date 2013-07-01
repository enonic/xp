package com.enonic.wem.admin.rest.rpc.content;

import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.jsonrpc.JsonRpcHandler;
import com.enonic.wem.admin.rest.rpc.AbstractRpcHandlerTest;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.command.content.CreateContentResult;
import com.enonic.wem.api.command.content.GetContents;
import com.enonic.wem.api.command.content.RenameContent;
import com.enonic.wem.api.command.content.UpdateContent;
import com.enonic.wem.api.command.content.UpdateContentResult;
import com.enonic.wem.api.command.schema.content.GetContentTypes;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentAlreadyExistException;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.CreateContentException;
import com.enonic.wem.api.content.RenameContentException;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;

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
        ContentId contentId = ContentId.from( "123" );
        ContentType contentType = ContentType.newContentType().
            name( "my_content_type" ).
            module( ModuleName.from( "mymodule" ) ).
            build();

        CreateContentResult createContentResult = new CreateContentResult( contentId, ContentPath.from( "/myContent/my-child-content" ) );

        Mockito.when( client.execute( isA( GetContentTypes.class ) ) ).thenReturn( ContentTypes.from( contentType ) );
        Mockito.when( client.execute( isA( GetContents.class ) ) ).thenReturn( Contents.empty() );
        Mockito.when( client.execute( isA( CreateContent.class ) ) ).thenReturn( createContentResult );

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
        ContentId contentId = ContentId.from( "123" );
        ContentType contentType = ContentType.newContentType().
            name( "my_content_type" ).
            module( ModuleName.from( "mymodule" ) ).
            build();

        CreateContentResult createContentResult = new CreateContentResult( contentId, ContentPath.from( "/myContent/my-child-content-2" ) );

        Mockito.when( client.execute( isA( GetContentTypes.class ) ) ).thenReturn( ContentTypes.from( contentType ) );
        final Contents contents =
            Contents.from( Content.newContent().name( "my-child-content" ).type( QualifiedContentTypeName.unstructured() ).build() );
        Mockito.when( client.execute( isA( GetContents.class ) ) ).thenReturn( contents ).thenReturn( Contents.empty() );
        Mockito.when( client.execute( isA( CreateContent.class ) ) ).thenReturn( createContentResult );

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
        ContentType contentType = ContentType.newContentType().
            name( "my_content_type" ).
            module( ModuleName.from( "mymodule" ) ).
            build();

        Mockito.when( client.execute( isA( GetContentTypes.class ) ) ).thenReturn( ContentTypes.from( contentType ) );
        Mockito.when( client.execute( isA( GetContents.class ) ) ).thenReturn( Contents.empty() );
        Mockito.when( client.execute( isA( UpdateContent.class ) ) ).thenReturn( UpdateContentResult.SUCCESS );

        ObjectNode expectedJson = objectNode();
        expectedJson.put( "success", true );
        expectedJson.put( "created", false );
        expectedJson.put( "updated", true );

        // exercise & verify
        testSuccess( "createOrUpdateContent_update_param.json", expectedJson );
    }

    @Test
    public void createContent_failed_to_create_content()
        throws Exception
    {
        ContentType contentType = ContentType.newContentType().
            name( "my_content_type" ).
            module( ModuleName.from( "mymodule" ) ).
            build();
        ContentPath parentContentPath = ContentPath.from( "/myContent/childContent" ).getParentPath();

        CreateContent createContent = new CreateContent().displayName( "Child content" ).parentContentPath( parentContentPath );
        Mockito.when( client.execute( isA( GetContentTypes.class ) ) ).thenReturn( ContentTypes.from( contentType ) );
        Mockito.when( client.execute( isA( GetContents.class ) ) ).thenReturn( Contents.empty() );
        Mockito.when( client.execute( isA( CreateContent.class ) ) ).thenThrow(
            new CreateContentException( createContent, new ContentNotFoundException( parentContentPath ) ) );

        ObjectNode expectedJson = objectNode();
        expectedJson.put( "success", false );
        expectedJson.put( "error",
                          "Failed to create content [Child content] at path [/myContent]: Content with path [/myContent] was not found" );

        // exercise & verify
        testSuccess( "createOrUpdateContent_create_param.json", expectedJson );
    }

    @Test
    public void updateWithRenaming()
        throws Exception
    {
        ContentType contentType = ContentType.newContentType().
            name( "my_content_type" ).
            module( ModuleName.from( "mymodule" ) ).
            build();

        Mockito.when( client.execute( isA( GetContentTypes.class ) ) ).thenReturn( ContentTypes.from( contentType ) );
        Mockito.when( client.execute( isA( GetContents.class ) ) ).thenReturn( Contents.empty() );
        Mockito.when( client.execute( isA( UpdateContent.class ) ) ).thenReturn( UpdateContentResult.SUCCESS );

        ObjectNode expectedJson = objectNode();
        expectedJson.put( "success", true );
        expectedJson.put( "created", false );
        expectedJson.put( "updated", true );

        // exercise & verify
        testSuccess( "createOrUpdateContent_rename_param.json", expectedJson );
    }

    @Test
    public void updateWithRenaming_to_existing_path()
        throws Exception
    {
        ContentType contentType = ContentType.newContentType().
            name( "my_content_type" ).
            module( ModuleName.from( "mymodule" ) ).
            build();
        RenameContent renameContent =
            new RenameContent().contentId( ContentId.from( "1fad493a-6a72-41a3-bac4-88aba3d83bcc" ) ).newName( "newName" );

        Mockito.when( client.execute( isA( GetContentTypes.class ) ) ).thenReturn( ContentTypes.from( contentType ) );
        Mockito.when( client.execute( isA( GetContents.class ) ) ).thenReturn( Contents.empty() );
        Mockito.when( client.execute( isA( UpdateContent.class ) ) ).thenReturn( UpdateContentResult.SUCCESS );
        Mockito.when( client.execute( isA( RenameContent.class ) ) ).thenThrow( new RenameContentException( renameContent,
                                                                                                            new ContentAlreadyExistException(
                                                                                                                ContentPath.from(
                                                                                                                    "mysite:/existingContent" ) ) ) );

        ObjectNode expectedJson = objectNode();
        expectedJson.put( "success", false );
        expectedJson.put( "error",
                          "Failed to rename content [1fad493a-6a72-41a3-bac4-88aba3d83bcc] to newName: Content at path [mysite:/existingContent] already exist" );

        // exercise & verify
        testSuccess( "createOrUpdateContent_rename_param.json", expectedJson );
    }

}
