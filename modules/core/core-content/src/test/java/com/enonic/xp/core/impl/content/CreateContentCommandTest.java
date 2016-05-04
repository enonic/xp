package com.enonic.xp.core.impl.content;

import java.time.Instant;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.core.impl.content.processor.ContentProcessors;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.form.Input;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.inputtype.InputTypeDefault;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.site.SiteService;

import static org.junit.Assert.*;

public class CreateContentCommandTest
{
    private ContentTypeService contentTypeService;

    private MixinService mixinService;

    private SiteService siteService;

    private NodeService nodeService;

    private ContentNodeTranslatorImpl translator;

    private EventPublisher eventPublisher;

    @Before
    public void setUp()
        throws Exception
    {
        this.siteService = Mockito.mock( SiteService.class );
        this.nodeService = Mockito.mock( NodeService.class );
        this.translator = new ContentNodeTranslatorImpl();
        this.translator.setNodeService( this.nodeService );
        this.eventPublisher = Mockito.mock( EventPublisher.class );
        this.mixinService = Mockito.mock( MixinService.class );
        this.contentTypeService = Mockito.mock( ContentTypeService.class );

        Mockito.when( this.nodeService.hasChildren( Mockito.any( Node.class ) ) ).thenReturn( false );
        Mockito.when( this.nodeService.create( Mockito.any( CreateNodeParams.class ) ) ).thenAnswer( this::mockNodeServiceCreate );
    }

    @Test(expected = IllegalArgumentException.class)
    public void contentTypeNull()
    {
        CreateContentCommand command = createContentCommand( createContentParams().build() );
        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( null );
        // exercise
        command.execute();
    }

    @Test(expected = ContentNotFoundException.class)
    public void badParentContentPath()
    {
        PropertyTree existingContentData = new PropertyTree();
        existingContentData.addString( "myData", "aaa" );

        CreateContentParams params = CreateContentParams.create().
            type( ContentTypeName.site() ).
            parent( ContentPath.from( "/myPath/myContent" ) ).
            contentData( existingContentData ).
            displayName( "displayName" ).
            build();

        CreateContentCommand command = createContentCommand( params );

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn(
            ContentType.create().superType( ContentTypeName.documentMedia() ).name( ContentTypeName.dataMedia() ).build() );

        command.execute();
    }

    @Test
    public void nameGeneratedFromDisplayName()
    {
        final CreateContentParams params = createContentParams().build();
        final CreateContentCommand command = createContentCommand( params );

        final ContentType contentType = ContentType.create().
            superType( ContentTypeName.documentMedia() ).
            name( ContentTypeName.dataMedia() ).
            build();
        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );

        // exercise
        final Content createdContent = command.execute();
        assertEquals( ContentName.from( "displayname" ), createdContent.getName() );
    }

    @Test
    public void unnamedContent()
    {
        final CreateContentParams params = createContentParams().name( (ContentName) null ).displayName( null ).build();
        final CreateContentCommand command = createContentCommand( params );

        final ContentType contentType = ContentType.create().
            superType( ContentTypeName.documentMedia() ).
            name( ContentTypeName.dataMedia() ).
            build();
        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );

        // exercise
        final Content createdContent = command.execute();
        assertTrue( createdContent.getName().isUnnamed() );
        assertEquals( "", createdContent.getDisplayName() );
    }

    private void defaultValue_string( final InputTypeName inputTypeName )
    {
        final CreateContentParams params = createContentParams().name( ContentName.from( "name" ) ).displayName( "" ).build();
        final CreateContentCommand command = createContentCommand( params );

        Input input = Input.create().
            name( "testInput" ).
            label( "testInput" ).
            inputTypeProperty( InputTypeProperty.create( "one", "one" ).build() ).
            inputTypeProperty( InputTypeProperty.create( "two", "two" ).build() ).
            inputTypeProperty( InputTypeProperty.create( "three", "three" ).build() ).
            inputType( inputTypeName ).
            defaultValue( InputTypeDefault.create().property( InputTypeProperty.create( "default", "two" ).build() ).build() ).
            build();

        final ContentType contentType = ContentType.create().
            superType( ContentTypeName.documentMedia() ).
            name( ContentTypeName.dataMedia() ).
            addFormItem( input ).
            build();

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );

        final Content createdContent = command.execute();
        assertTrue( createdContent.getData().getString( "testInput" ).equals( "two" ) );
    }

    @Test
    public void defaultValue_combobox()
    {
        this.defaultValue_string( InputTypeName.COMBO_BOX );
    }

    @Test
    public void defaultValue_radio()
    {
        this.defaultValue_string( InputTypeName.RADIO_BUTTON );
    }

    @Test
    public void defaultValue_checkbox()
    {
        final CreateContentParams params = createContentParams().name( ContentName.from( "name" ) ).displayName( "" ).build();
        final CreateContentCommand command = createContentCommand( params );

        Input input = Input.create().
            name( "testInput" ).
            label( "testInput" ).
            inputType( InputTypeName.CHECK_BOX ).
            defaultValue( InputTypeDefault.create().property( InputTypeProperty.create( "default", "checked" ).build() ).build() ).
            build();

        final ContentType contentType = ContentType.create().
            superType( ContentTypeName.documentMedia() ).
            name( ContentTypeName.dataMedia() ).
            addFormItem( input ).
            build();

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );

        final Content createdContent = command.execute();
        assertTrue( createdContent.getData().getString( "testInput" ).equals( "true" ) );
    }

    @Test
    public void defaultValue_checkbox_invalid()
    {
        final CreateContentParams params = createContentParams().name( ContentName.from( "name" ) ).displayName( "" ).build();
        final CreateContentCommand command = createContentCommand( params );

        Input input = Input.create().
            name( "testInput" ).
            label( "testInput" ).
            inputType( InputTypeName.CHECK_BOX ).
            defaultValue( InputTypeDefault.create().property( InputTypeProperty.create( "default", "unchecked" ).build() ).build() ).
            build();

        final ContentType contentType = ContentType.create().
            superType( ContentTypeName.documentMedia() ).
            name( ContentTypeName.dataMedia() ).
            addFormItem( input ).
            build();

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );

        final Content createdContent = command.execute();
        assertNull( createdContent.getData().getString( "testInput" ) );
    }

    @Test
    public void namePresentAndUnchanged()
    {
        PropertyTree existingContentData = new PropertyTree();
        existingContentData.addString( "myData", "aaa" );

        final CreateContentParams params = CreateContentParams.create().name( "myname" ).
            type( ContentTypeName.site() ).
            parent( ContentPath.ROOT ).
            contentData( existingContentData ).
            displayName( "displayName" ).
            build();

        CreateContentCommand command = createContentCommand( params );

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn(
            ContentType.create().superType( ContentTypeName.documentMedia() ).name( ContentTypeName.dataMedia() ).build() );

        // exercise
        final Content createdContent = command.execute();
        assertEquals( ContentName.from( "myname" ), createdContent.getName() );
    }

    @Test
    public void createTemplateFolderOutsideSite()
    {
        final PropertyTree parentNodeData = new PropertyTree();
        parentNodeData.setString( ContentPropertyNames.TYPE, ContentTypeName.unstructured().toString() );
        parentNodeData.setSet( ContentPropertyNames.DATA, new PropertySet() );
        parentNodeData.setString( ContentPropertyNames.CREATOR, "user:myuserstore:user1" );
        final Node parentNode = Node.create().
            id( NodeId.from( "id1" ) ).
            name( "parent" ).
            parentPath( ContentConstants.CONTENT_ROOT_PATH ).
            data( parentNodeData ).
            build();

        Mockito.when( nodeService.getByPath( Mockito.eq( NodePath.create( "/content/parent" ).build() ) ) ).thenReturn( parentNode );

        final CreateContentParams params = CreateContentParams.create().
            type( ContentTypeName.templateFolder() ).
            name( "_templates" ).
            parent( ContentPath.from( "/parent" ) ).
            contentData( new PropertyTree() ).
            displayName( "displayName" ).
            build();

        CreateContentCommand command = createContentCommand( params );

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn(
            ContentType.create().superType( ContentTypeName.documentMedia() ).name( ContentTypeName.dataMedia() ).build() );

        // exercise
        try
        {
            command.execute();
            Assert.fail( "Expected exception" );
        }
        catch ( IllegalArgumentException e )
        {
            assertEquals( "A template folder can only be created below a content of type 'site'. Path: /parent/_templates",
                          e.getMessage() );
        }
    }

    @Test
    public void createTemplateFolderInsideSite()
    {
        final PropertyTree parentNodeData = new PropertyTree();
        parentNodeData.setString( ContentPropertyNames.TYPE, ContentTypeName.site().toString() );
        parentNodeData.setSet( ContentPropertyNames.DATA, new PropertySet() );
        parentNodeData.setString( ContentPropertyNames.CREATOR, "user:myuserstore:user1" );
        final Node parentNode = Node.create().
            id( NodeId.from( "id1" ) ).
            name( "parent" ).
            parentPath( ContentConstants.CONTENT_ROOT_PATH ).
            data( parentNodeData ).
            build();

        Mockito.when( nodeService.getByPath( Mockito.eq( NodePath.create( "/content/parent" ).build() ) ) ).thenReturn( parentNode );

        final CreateContentParams params = CreateContentParams.create().
            type( ContentTypeName.templateFolder() ).
            name( "_templates" ).
            parent( ContentPath.from( "/parent" ) ).
            contentData( new PropertyTree() ).
            displayName( "displayName" ).
            build();

        CreateContentCommand command = createContentCommand( params );

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn(
            ContentType.create().superType( ContentTypeName.documentMedia() ).name( ContentTypeName.dataMedia() ).build() );

        // exercise
        final Content createdContent = command.execute();
        assertNotNull( createdContent );
    }

    @Test
    public void createPageTemplateUnderTemplateFolder()
    {
        final PropertyTree parentNodeData = new PropertyTree();
        parentNodeData.setString( ContentPropertyNames.TYPE, ContentTypeName.templateFolder().toString() );
        parentNodeData.setSet( ContentPropertyNames.DATA, new PropertySet() );
        parentNodeData.setString( ContentPropertyNames.CREATOR, "user:myuserstore:user1" );
        final Node parentNode = Node.create().
            id( NodeId.from( "id1" ) ).
            name( "_templates" ).
            parentPath( ContentConstants.CONTENT_ROOT_PATH ).
            data( parentNodeData ).
            build();

        Mockito.when( nodeService.getByPath( Mockito.eq( NodePath.create( "/content/_templates" ).build() ) ) ).thenReturn( parentNode );

        final CreateContentParams params = CreateContentParams.create().
            type( ContentTypeName.pageTemplate() ).
            name( "mytemplate" ).
            parent( ContentPath.from( "/_templates" ) ).
            contentData( new PropertyTree() ).
            displayName( "displayName" ).
            build();

        CreateContentCommand command = createContentCommand( params );

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn(
            ContentType.create().superType( ContentTypeName.documentMedia() ).name( ContentTypeName.dataMedia() ).build() );

        // exercise
        final Content createdContent = command.execute();
        assertNotNull( createdContent );
    }

    @Test
    public void createPageTemplateNotUnderTemplateFolder()
    {
        final PropertyTree parentNodeData = new PropertyTree();
        parentNodeData.setString( ContentPropertyNames.TYPE, ContentTypeName.folder().toString() );
        parentNodeData.setSet( ContentPropertyNames.DATA, new PropertySet() );
        parentNodeData.setString( ContentPropertyNames.CREATOR, "user:myuserstore:user1" );
        final Node parentNode = Node.create().
            id( NodeId.from( "id1" ) ).
            name( "_templates" ).
            parentPath( ContentConstants.CONTENT_ROOT_PATH ).
            data( parentNodeData ).
            build();

        Mockito.when( nodeService.getByPath( Mockito.eq( NodePath.create( "/content/_templates" ).build() ) ) ).thenReturn( parentNode );

        final CreateContentParams params = CreateContentParams.create().
            type( ContentTypeName.pageTemplate() ).
            name( "mytemplate" ).
            parent( ContentPath.from( "/_templates" ) ).
            contentData( new PropertyTree() ).
            displayName( "displayName" ).
            build();

        CreateContentCommand command = createContentCommand( params );

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn(
            ContentType.create().superType( ContentTypeName.documentMedia() ).name( ContentTypeName.dataMedia() ).build() );

        // exercise
        try
        {
            command.execute();
            Assert.fail( "Expected exception" );
        }
        catch ( IllegalArgumentException e )
        {
            assertEquals( "A page template can only be created below a content of type 'template-folder'. Path: /_templates/mytemplate",
                          e.getMessage() );
        }
    }

    private CreateContentParams.Builder createContentParams()
    {
        PropertyTree existingContentData = new PropertyTree();
        existingContentData.addString( "myData", "aaa" );

        return CreateContentParams.create().
            type( ContentTypeName.site() ).
            parent( ContentPath.ROOT ).
            contentData( existingContentData ).
            displayName( "displayName" );
    }

    private CreateContentCommand createContentCommand( CreateContentParams params )
    {
        final MediaInfo mediaInfo = MediaInfo.create().mediaType( "image/jpg" ).build();
        return CreateContentCommand.create().
            params( params ).
            contentTypeService( this.contentTypeService ).
            nodeService( this.nodeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            mediaInfo( mediaInfo ).
            mixinService( this.mixinService ).
            siteService( this.siteService ).
            contentProcessors( new ContentProcessors() ).
            build();
    }

    private Node mockNodeServiceCreate( final InvocationOnMock invocation )
        throws Throwable
    {
        CreateNodeParams params = (CreateNodeParams) invocation.getArguments()[0];

        final AccessControlList permissions = AccessControlList.create().
            add( AccessControlEntry.create().
                allowAll().
                principal( PrincipalKey.ofAnonymous() ).
                build() ).
            build();

        return Node.create().
            id( params.getNodeId() != null ? params.getNodeId() : new NodeId() ).
            parentPath( params.getParent() ).
            name( NodeName.from( params.getName() ) ).
            data( params.getData() ).
            indexConfigDocument( params.getIndexConfigDocument() ).
            childOrder( params.getChildOrder() != null ? params.getChildOrder() : ChildOrder.defaultOrder() ).
            permissions( permissions ).
            inheritPermissions( params.inheritPermissions() ).
            nodeType( params.getNodeType() != null ? params.getNodeType() : NodeType.DEFAULT_NODE_COLLECTION ).
            timestamp( Instant.now() ).
            build();
    }
}
