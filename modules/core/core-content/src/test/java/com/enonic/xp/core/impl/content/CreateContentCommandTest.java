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
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.xdata.XDataService;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.site.SiteService;

import static org.junit.Assert.*;

public class CreateContentCommandTest
{
    private ContentTypeService contentTypeService;

    private XDataService xDataService;

    private SiteService siteService;

    private NodeService nodeService;

    private PageDescriptorService pageDescriptorService;

    private ContentNodeTranslatorImpl translator;

    private EventPublisher eventPublisher;

    @Before
    public void setUp()
        throws Exception
    {
        this.siteService = Mockito.mock( SiteService.class );
        this.nodeService = Mockito.mock( NodeService.class );
        this.pageDescriptorService = Mockito.mock( PageDescriptorService.class );
        this.translator = new ContentNodeTranslatorImpl();
        this.translator.setNodeService( this.nodeService );
        this.eventPublisher = Mockito.mock( EventPublisher.class );
        this.xDataService = Mockito.mock( XDataService.class );
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
    public void createContentInValidPageTemplate()
    {
        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).
            thenReturn( ContentType.create().
                superType( ContentTypeName.structured() ).
                name( ContentTypeName.pageTemplate() ).
                allowChildContent( false ).
                build() );

        final NodePath sitePath = initContent( ContentTypeName.site(),"site", ContentConstants.CONTENT_ROOT_PATH );
        final NodePath templatePath =
            initContent( ContentTypeName.pageTemplate(),"template", sitePath );

        final CreateContentParams params = CreateContentParams.create().
            type( ContentTypeName.folder() ).
            name( "folder" ).
            parent( ContentPath.from( "/site/template" ) ).
            contentData( new PropertyTree() ).
            displayName( "displayName" ).
            build();

        CreateContentCommand command = createContentCommand( params );

        final Content content = command.execute();
        assertNotNull( content );
        assertEquals( content.getParentPath(), ContentPath.from( ContentPath.ROOT, "site" ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void createContentInInvalidPageTemplate()
    {
        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).
            thenReturn( ContentType.create().
                superType( ContentTypeName.structured() ).
                name( ContentTypeName.pageTemplate() ).
                allowChildContent( false ).
                build() );

        initContent( ContentTypeName.pageTemplate(),"template", ContentConstants.CONTENT_ROOT_PATH );

        final CreateContentParams params = CreateContentParams.create().
            type( ContentTypeName.folder() ).
            name( "folder" ).
            parent( ContentPath.from( "/template" ) ).
            contentData( new PropertyTree() ).
            displayName( "displayName" ).
            build();

        CreateContentCommand command = createContentCommand( params );

        command.execute();
    }

    @Test(expected = IllegalArgumentException.class)
    public void createContentForDisallowedContentType()
    {
        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).
            thenReturn( ContentType.create().
                superType( ContentTypeName.structured() ).
                name( ContentTypeName.folder() ).
                allowChildContent( false ).
                build() );

        initContent( ContentTypeName.folder(),"folder", ContentConstants.CONTENT_ROOT_PATH );

        final CreateContentParams params = CreateContentParams.create().
            type( ContentTypeName.folder() ).
            name( "folder" ).
            parent( ContentPath.from( "/folder" ) ).
            contentData( new PropertyTree() ).
            displayName( "displayName" ).
            build();

        CreateContentCommand command = createContentCommand( params );
        command.execute();
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
        catch ( RuntimeException e )
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
            xDataService( this.xDataService ).
            siteService( this.siteService ).
            pageDescriptorService( this.pageDescriptorService ).
            contentProcessors( new ContentProcessors() ).
            formDefaultValuesProcessor( ( form, data ) -> {
            } ).
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

    private NodePath initContent( final ContentTypeName contentTypeName, final String name, final NodePath parentPath )
    {
        final PropertyTree nodeData = new PropertyTree();
        nodeData.setString( ContentPropertyNames.TYPE, contentTypeName.toString() );
        nodeData.setSet( ContentPropertyNames.DATA, new PropertySet() );
        nodeData.setString( ContentPropertyNames.CREATOR, "user:myuserstore:user1" );

        final Node node = Node.create().
            id( NodeId.from( name ) ).
            name( name ).
            parentPath(parentPath ).
            data( nodeData ).
            build();

        Mockito.when( nodeService.getByPath( Mockito.eq( node.path() ) ) ).thenReturn( node );
        Mockito.when( nodeService.getById( Mockito.eq( node.id() ) ) ).thenReturn( node );

        return node.path();
    }

}
