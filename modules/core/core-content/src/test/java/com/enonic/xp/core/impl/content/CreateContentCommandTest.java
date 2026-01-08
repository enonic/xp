package com.enonic.xp.core.impl.content;

import java.time.Instant;
import java.util.EnumSet;
import java.util.Locale;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.impl.schema.content.BuiltinContentTypesAccessor;
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
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectRole;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.xdata.XDataService;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfigService;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteConfigsDataSerializer;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.site.XDataMappingService;
import com.enonic.xp.site.XDataOptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CreateContentCommandTest
{
    private ContentTypeService contentTypeService;

    private XDataService xDataService;

    private SiteService siteService;

    private NodeService nodeService;

    private PageDescriptorService pageDescriptorService;

    private XDataMappingService xDataMappingService;

    private SiteConfigService siteConfigService;

    private EventPublisher eventPublisher;

    @BeforeEach
    void setUp()
    {
        this.siteService = Mockito.mock( SiteService.class );
        this.nodeService = Mockito.mock( NodeService.class );
        this.pageDescriptorService = Mockito.mock( PageDescriptorService.class );
        this.eventPublisher = Mockito.mock( EventPublisher.class );
        this.xDataService = Mockito.mock( XDataService.class );
        this.contentTypeService = Mockito.mock( ContentTypeService.class );
        this.xDataMappingService = Mockito.mock( XDataMappingService.class );
        this.siteConfigService = Mockito.mock( SiteConfigService.class );

        when( this.xDataMappingService.getXDataMappingOptions( any(), any() ) ).thenReturn( XDataOptions.empty() );
        when( this.siteConfigService.getSiteConfigs( any() ) ).thenReturn( SiteConfigs.empty() );
        when( this.nodeService.create( any( CreateNodeParams.class ) ) ).thenAnswer( this::mockNodeServiceCreate );
    }

    @Test
    void contentTypeNull()
    {
        CreateContentCommand command = createContentCommand( createContentParams().build() );
        when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( null );

        assertThrows( IllegalArgumentException.class, command::execute );
    }

    @Test
    void badParentContentPath()
    {
        PropertyTree existingContentData = new PropertyTree();
        existingContentData.addString( "myData", "aaa" );

        CreateContentParams params = CreateContentParams.create()
            .name( "name" )
            .type( ContentTypeName.site() )
            .parent( ContentPath.from( "/myPath/myContent" ) )
            .contentData( existingContentData )
            .displayName( "displayName" )
            .build();

        CreateContentCommand command = createContentCommand( params );

        when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn(
            ContentType.create().superType( ContentTypeName.documentMedia() ).name( ContentTypeName.dataMedia() ).build() );

        assertThrows( IllegalStateException.class, command::execute );
    }

    @Test
    void nameGeneratedFromDisplayName()
    {
        final CreateContentParams params = createContentParams().build();
        final CreateContentCommand command = createContentCommand( params );

        final ContentType contentType =
            ContentType.create().superType( ContentTypeName.documentMedia() ).name( ContentTypeName.dataMedia() ).build();
        when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );

        mockContentRootNode();
        // exercise
        final Content createdContent = command.execute();
        assertEquals( ContentName.from( "displayname" ), createdContent.getName() );
    }

    @Test
    void createContentInValidPageTemplate()
    {
        when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenAnswer(
            a -> BuiltinContentTypesAccessor.getContentType( ( (GetContentTypeParams) a.getArgument( 0 ) ).getContentTypeName() ) );

        final NodePath sitePath = initContent( ContentTypeName.site(), "site", ContentConstants.CONTENT_ROOT_PATH );
        final NodePath templatePath = initContent( ContentTypeName.pageTemplate(), "template", sitePath );

        final CreateContentParams params = CreateContentParams.create()
            .type( ContentTypeName.dataMedia() )
            .name( "media" )
            .parent( ContentPath.from( "/site/template" ) )
            .contentData( new PropertyTree() )
            .displayName( "displayName" )
            .build();

        CreateContentCommand command = createContentCommand( params );

        final Content content = command.execute();
        assertNotNull( content );
        assertEquals( ContentPath.from( "/site/template" ), content.getParentPath() );
    }

    @Test
    void createContent_unknown_parent_content_type()
    {
        when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenAnswer(
            a -> BuiltinContentTypesAccessor.getContentType( ( (GetContentTypeParams) a.getArgument( 0 ) ).getContentTypeName() ) );

        final NodePath sitePath = initContent( ContentTypeName.site(), "site", ContentConstants.CONTENT_ROOT_PATH );
        final NodePath parentPath = initContent( ContentTypeName.from( "unknown:unknown" ), "parent", sitePath );

        final CreateContentParams params = CreateContentParams.create()
            .type( ContentTypeName.dataMedia() )
            .name( "media" )
            .parent( ContentPath.from( "/site/parent" ) )
            .contentData( new PropertyTree() )
            .displayName( "displayName" )
            .build();

        CreateContentCommand command = createContentCommand( params );

        final Content content = command.execute();
        assertNotNull( content );
    }

    @Test
    void createContentInInvalidPageTemplate()
    {
        when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( ContentType.create()
                                                                                                          .superType(
                                                                                                              ContentTypeName.structured() )
                                                                                                          .name(
                                                                                                              ContentTypeName.pageTemplate() )
                                                                                                          .allowChildContent( false )
                                                                                                          .build() );

        initContent( ContentTypeName.pageTemplate(), "template", ContentConstants.CONTENT_ROOT_PATH );

        final CreateContentParams params = CreateContentParams.create()
            .type( ContentTypeName.folder() )
            .name( "folder" )
            .parent( ContentPath.from( "/template" ) )
            .contentData( new PropertyTree() )
            .displayName( "displayName" )
            .build();

        CreateContentCommand command = createContentCommand( params );

        assertThrows( IllegalArgumentException.class, command::execute );
    }

    @Test
    void createContentInTemplateFolder_fails()
    {
        when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenAnswer(
            a -> BuiltinContentTypesAccessor.getContentType( ( (GetContentTypeParams) a.getArgument( 0 ) ).getContentTypeName() ) );

        initContent( ContentTypeName.templateFolder(), "template", ContentConstants.CONTENT_ROOT_PATH );

        final CreateContentParams params = CreateContentParams.create()
            .type( ContentTypeName.folder() )
            .name( "folder" )
            .parent( ContentPath.from( "/template" ) )
            .contentData( new PropertyTree() )
            .displayName( "displayName" )
            .build();

        CreateContentCommand command = createContentCommand( params );

        assertThrows( IllegalArgumentException.class, command::execute );
    }

    @Test
    void createFolderInPageTemplate_fails()
    {
        when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenAnswer(
            a -> BuiltinContentTypesAccessor.getContentType( ( (GetContentTypeParams) a.getArgument( 0 ) ).getContentTypeName() ) );

        initContent( ContentTypeName.pageTemplate(), "template", ContentConstants.CONTENT_ROOT_PATH );

        final CreateContentParams params = CreateContentParams.create()
            .type( ContentTypeName.folder() )
            .name( "folder" )
            .parent( ContentPath.from( "/template" ) )
            .contentData( new PropertyTree() )
            .displayName( "displayName" )
            .build();

        CreateContentCommand command = createContentCommand( params );

        assertThrows( IllegalArgumentException.class, command::execute );
    }

    @Test
    void createContentForDisallowedContentType_fails()
    {
        when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( ContentType.create()
                                                                                                          .superType(
                                                                                                              ContentTypeName.structured() )
                                                                                                          .name( ContentTypeName.folder() )
                                                                                                          .allowChildContent( false )
                                                                                                          .build() );

        initContent( ContentTypeName.folder(), "folder", ContentConstants.CONTENT_ROOT_PATH );

        final CreateContentParams params = CreateContentParams.create()
            .type( ContentTypeName.folder() )
            .name( "folder" )
            .parent( ContentPath.from( "/folder" ) )
            .contentData( new PropertyTree() )
            .displayName( "displayName" )
            .build();

        CreateContentCommand command = createContentCommand( params );
        assertThrows( IllegalArgumentException.class, command::execute );
    }

    @Test
    void unnamedContent()
    {
        final CreateContentParams params = createContentParams().name( (ContentName) null ).displayName( null ).build();
        final CreateContentCommand command = createContentCommand( params );

        final ContentType contentType =
            ContentType.create().superType( ContentTypeName.documentMedia() ).name( ContentTypeName.dataMedia() ).build();
        when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );

        mockContentRootNode();

        // exercise
        final Content createdContent = command.execute();
        assertTrue( createdContent.getName().isUnnamed() );
        assertEquals( "", createdContent.getDisplayName() );
    }

    @Test
    void createPageTemplateInRoot_fails()
    {
        when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenAnswer(
            a -> BuiltinContentTypesAccessor.getContentType( ( (GetContentTypeParams) a.getArgument( 0 ) ).getContentTypeName() ) );

        mockContentRootNode();

        final CreateContentParams params = CreateContentParams.create()
            .type( ContentTypeName.templateFolder() )
            .name( "folder" )
            .parent( ContentPath.ROOT )
            .contentData( new PropertyTree() )
            .displayName( "displayName" )
            .build();

        CreateContentCommand command = createContentCommand( params );

        assertThrows( IllegalArgumentException.class, command::execute );
    }

    @Test
    void createTemplateFolderInRoot_fails()
    {
        when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenAnswer(
            a -> BuiltinContentTypesAccessor.getContentType( ( (GetContentTypeParams) a.getArgument( 0 ) ).getContentTypeName() ) );

        mockContentRootNode();

        final CreateContentParams params = CreateContentParams.create()
            .type( ContentTypeName.pageTemplate() )
            .name( "template" )
            .parent( ContentPath.ROOT )
            .contentData( new PropertyTree() )
            .displayName( "displayName" )
            .build();

        CreateContentCommand command = createContentCommand( params );

        assertThrows( IllegalArgumentException.class, command::execute );
    }

    @Test
    void namePresentAndUnchanged()
    {
        PropertyTree existingContentData = new PropertyTree();
        existingContentData.addString( "myData", "aaa" );

        final CreateContentParams params = CreateContentParams.create()
            .name( "myname" )
            .type( ContentTypeName.site() )
            .parent( ContentPath.ROOT )
            .contentData( existingContentData )
            .displayName( "displayName" )
            .build();

        CreateContentCommand command = createContentCommand( params );

        when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn(
            ContentType.create().superType( ContentTypeName.documentMedia() ).name( ContentTypeName.dataMedia() ).build() );

        mockContentRootNode();

        final Content createdContent = command.execute();
        assertEquals( ContentName.from( "myname" ), createdContent.getName() );
    }

    @Test
    void createTemplateFolderOutsideSite()
    {
        final PropertyTree parentNodeData = new PropertyTree();
        parentNodeData.setString( ContentPropertyNames.TYPE, ContentTypeName.unstructured().toString() );
        parentNodeData.setSet( ContentPropertyNames.DATA, parentNodeData.newSet() );
        parentNodeData.setString( ContentPropertyNames.CREATOR, "user:myidprovider:user1" );
        final Node parentNode = Node.create()
            .id( NodeId.from( "id1" ) )
            .name( "parent" )
            .parentPath( ContentConstants.CONTENT_ROOT_PATH )
            .data( parentNodeData )
            .build();

        when( nodeService.getByPath( Mockito.eq( new NodePath( "/content/parent" ) ) ) ).thenReturn( parentNode );

        final CreateContentParams params = CreateContentParams.create()
            .type( ContentTypeName.templateFolder() )
            .name( "_templates" )
            .parent( ContentPath.from( "/parent" ) )
            .contentData( new PropertyTree() )
            .displayName( "displayName" )
            .build();

        CreateContentCommand command = createContentCommand( params );

        when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn(
            ContentType.create().superType( ContentTypeName.documentMedia() ).name( ContentTypeName.dataMedia() ).build() );

        assertThrows( IllegalArgumentException.class, command::execute );
    }

    @Test
    void createContentWithDefaultLanguage()
    {
        final PropertyTree parentNodeData = new PropertyTree();
        parentNodeData.setSet( ContentPropertyNames.DATA, parentNodeData.newSet() );
        parentNodeData.setString( ContentPropertyNames.CREATOR, "user:myidprovider:user1" );
        parentNodeData.setString( ContentPropertyNames.LANGUAGE, "en" );
        final Node parentNode = Node.create()
            .id( NodeId.from( "id1" ) )
            .name( "parent" )
            .parentPath( ContentConstants.CONTENT_ROOT_PATH )
            .data( parentNodeData )
            .build();

        when( nodeService.getByPath( Mockito.eq( new NodePath( "/content" ) ) ) ).thenReturn( parentNode );

        final CreateContentParams params = CreateContentParams.create()
            .type( ContentTypeName.folder() )
            .name( "name" )
            .parent( ContentPath.from( "/" ) )
            .contentData( new PropertyTree() )
            .displayName( "displayName" )
            .build();

        CreateContentCommand command = createContentCommand( params );

        when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn(
            ContentType.create().superType( ContentTypeName.folder() ).name( ContentTypeName.folder() ).build() );

        final Content content = command.execute();
        assertEquals( Locale.ENGLISH, content.getLanguage() );
    }


    @Test
    void createContentWithProjectLanguage()
    {

        mockContentRootNode( "no" );
        mockContentNode( "parent", "/content", "en", EnumSet.of( ContentInheritType.CONTENT ) );

        when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn(
            ContentType.create().superType( ContentTypeName.folder() ).name( ContentTypeName.folder() ).build() );

        final CreateContentParams params = CreateContentParams.create()
            .type( ContentTypeName.folder() )
            .name( "name" )
            .parent( ContentPath.from( "/parent" ) )
            .contentData( new PropertyTree() )
            .displayName( "displayName" )
            .build();

        final CreateContentCommand command = createContentCommand( params );

        Content content = command.execute();
        assertEquals( "no", content.getLanguage().getLanguage() );

        mockContentRootNode( null );

        content = command.execute();
        assertNull( content.getLanguage() );

        mockContentNode( "parent", "/content", "en", EnumSet.noneOf( ContentInheritType.class ) );

        content = command.execute();
        assertEquals( "en", content.getLanguage().getLanguage() );

    }

    @Test
    void createTemplateFolderInsideSite()
    {
        final PropertyTree parentNodeData = new PropertyTree();
        parentNodeData.setString( ContentPropertyNames.TYPE, ContentTypeName.site().toString() );
        parentNodeData.setSet( ContentPropertyNames.DATA, parentNodeData.newSet() );
        parentNodeData.setString( ContentPropertyNames.CREATOR, "user:myidprovider:user1" );
        final Node parentNode = Node.create()
            .id( NodeId.from( "id1" ) )
            .name( "parent" )
            .parentPath( ContentConstants.CONTENT_ROOT_PATH )
            .data( parentNodeData )
            .build();

        when( nodeService.getByPath( Mockito.eq( new NodePath( "/content/parent" ) ) ) ).thenReturn( parentNode );

        final CreateContentParams params = CreateContentParams.create()
            .type( ContentTypeName.templateFolder() )
            .name( "_templates" )
            .parent( ContentPath.from( "/parent" ) )
            .contentData( new PropertyTree() )
            .displayName( "displayName" )
            .build();

        CreateContentCommand command = createContentCommand( params );

        when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn(
            ContentType.create().superType( ContentTypeName.documentMedia() ).name( ContentTypeName.dataMedia() ).build() );

        // exercise
        final Content createdContent = command.execute();
        assertNotNull( createdContent );
    }

    @Test
    void createPageTemplateUnderTemplateFolder()
    {
        final PropertyTree parentNodeData = new PropertyTree();
        parentNodeData.setString( ContentPropertyNames.TYPE, ContentTypeName.templateFolder().toString() );
        parentNodeData.setSet( ContentPropertyNames.DATA, parentNodeData.newSet() );
        parentNodeData.setString( ContentPropertyNames.CREATOR, "user:myidprovider:user1" );
        final Node parentNode = Node.create()
            .id( NodeId.from( "id1" ) )
            .name( "_templates" )
            .parentPath( ContentConstants.CONTENT_ROOT_PATH )
            .data( parentNodeData )
            .build();

        when( nodeService.getByPath( Mockito.eq( new NodePath( "/content/_templates" ) ) ) ).thenReturn( parentNode );

        final CreateContentParams params = CreateContentParams.create()
            .type( ContentTypeName.pageTemplate() )
            .name( "mytemplate" )
            .parent( ContentPath.from( "/_templates" ) )
            .contentData( new PropertyTree() )
            .displayName( "displayName" )
            .build();

        CreateContentCommand command = createContentCommand( params );

        when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn(
            ContentType.create().superType( ContentTypeName.documentMedia() ).name( ContentTypeName.dataMedia() ).build() );

        // exercise
        final Content createdContent = command.execute();
        assertNotNull( createdContent );
    }

    @Test
    void createPageTemplateNotUnderTemplateFolder()
    {
        final PropertyTree parentNodeData = new PropertyTree();
        parentNodeData.setString( ContentPropertyNames.TYPE, ContentTypeName.folder().toString() );
        parentNodeData.setSet( ContentPropertyNames.DATA, parentNodeData.newSet() );
        parentNodeData.setString( ContentPropertyNames.CREATOR, "user:myidprovider:user1" );
        final Node parentNode = Node.create()
            .id( NodeId.from( "id1" ) )
            .name( "_templates" )
            .parentPath( ContentConstants.CONTENT_ROOT_PATH )
            .data( parentNodeData )
            .build();

        when( nodeService.getByPath( Mockito.eq( new NodePath( "/content/_templates" ) ) ) ).thenReturn( parentNode );

        final CreateContentParams params = CreateContentParams.create()
            .type( ContentTypeName.pageTemplate() )
            .name( "mytemplate" )
            .parent( ContentPath.from( "/_templates" ) )
            .contentData( new PropertyTree() )
            .displayName( "displayName" )
            .build();

        CreateContentCommand command = createContentCommand( params );

        when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn(
            ContentType.create().superType( ContentTypeName.documentMedia() ).name( ContentTypeName.dataMedia() ).build(),
            ContentType.create().name( ContentTypeName.folder() ).setBuiltIn().build() );

        assertThrows( IllegalArgumentException.class, command::execute );
    }

    @Test
    void createContentWithSiteConfigs_success()
    {
        mockContentRootNode( "en" );

        final PropertyTree contentData = new PropertyTree();
        final PropertySet siteConfig = contentData.addSet( ContentPropertyNames.SITECONFIG );
        siteConfig.addString( "applicationKey", "value1" );
        final PropertySet appConfig = siteConfig.addSet( "config" );
        appConfig.addString( "key", "value2" );

        final CreateContentParams params = CreateContentParams.create()
            .type( ContentTypeName.site() )
            .name( "site" )
            .parent( ContentPath.from( "/" ) )
            .contentData( contentData )
            .displayName( "displayName" )
            .build();

        CreateContentCommand command = createContentCommand( params );

        when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn(
            ContentType.create().superType( ContentTypeName.documentMedia() ).name( ContentTypeName.dataMedia() ).build() );

        final User repoOwner =
            User.create().key( PrincipalKey.ofUser( IdProviderKey.system(), "custom-user" ) ).login( "custom-user" ).build();

        final ProjectName projectName = ProjectName.from( "test-project" );

        final Site createdContent = ContextBuilder.from( ContextAccessor.current() )
            .repositoryId( projectName.getRepoId() )
            .authInfo( AuthenticationInfo.create()
                           .principals( RoleKeys.AUTHENTICATED )
                           .principals( RoleKeys.CONTENT_MANAGER_APP )
                           .principals( ProjectAccessHelper.createRoleKey( projectName, ProjectRole.OWNER ) ) // важно!
                           .user( repoOwner )
                           .build() )
            .build()
            .callWith( () -> (Site) command.execute() );

        assertNotNull( createdContent );
        assertNotNull( SiteConfigsDataSerializer.fromData( createdContent.getData().getRoot() ) );
        assertEquals( 1, SiteConfigsDataSerializer.fromData( createdContent.getData().getRoot() ).getSize() );
        assertEquals( "value1",
                      SiteConfigsDataSerializer.fromData( createdContent.getData().getRoot() ).get( 0 ).getApplicationKey().toString() );
        assertEquals( "value2",
                      SiteConfigsDataSerializer.fromData( createdContent.getData().getRoot() ).get( 0 ).getConfig().getString( "key" ) );
    }

    @Test
    void createContentWithSiteConfigs_shouldFailWithoutOwnerRole()
    {
        mockContentRootNode( "en" );

        final PropertyTree contentData = new PropertyTree();
        final PropertySet siteConfig = contentData.addSet( ContentPropertyNames.SITECONFIG );
        siteConfig.addString( "applicationKey", "value1" );
        final PropertySet appConfig = siteConfig.addSet( "config" );
        appConfig.addString( "key", "value2" );

        final CreateContentParams params = CreateContentParams.create()
            .type( ContentTypeName.site() )
            .name( "site" )
            .parent( ContentPath.from( "/" ) )
            .contentData( contentData )
            .displayName( "displayName" )
            .build();

        CreateContentCommand command = createContentCommand( params );

        when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn(
            ContentType.create().superType( ContentTypeName.documentMedia() ).name( ContentTypeName.dataMedia() ).build() );

        final User repoOwner =
            User.create().key( PrincipalKey.ofUser( IdProviderKey.system(), "custom-user" ) ).login( "custom-user" ).build();

        final ProjectName projectName = ProjectName.from( "test-project" );

        assertThrows( Exception.class, () -> ContextBuilder.from( ContextAccessor.current() )
            .repositoryId( projectName.getRepoId() )
            .authInfo( AuthenticationInfo.create()
                           .principals( RoleKeys.AUTHENTICATED )
                           .principals( RoleKeys.CONTENT_MANAGER_APP )
                           .user( repoOwner )
                           .build() )
            .build()
            .callWith( () -> (Site) command.execute() ) );
    }

    private CreateContentParams.Builder createContentParams()
    {
        PropertyTree existingContentData = new PropertyTree();
        existingContentData.addString( "myData", "aaa" );

        return CreateContentParams.create()
            .type( ContentTypeName.site() )
            .parent( ContentPath.ROOT )
            .contentData( existingContentData )
            .displayName( "displayName" );
    }

    private CreateContentCommand createContentCommand( CreateContentParams params )
    {
        final MediaInfo mediaInfo = MediaInfo.create().mediaType( "image/jpeg" ).build();
        return CreateContentCommand.create()
            .params( params )
            .contentTypeService( this.contentTypeService )
            .nodeService( this.nodeService )
            .eventPublisher( this.eventPublisher )
            .mediaInfo( mediaInfo )
            .xDataService( this.xDataService )
            .siteService( this.siteService )
            .pageDescriptorService( this.pageDescriptorService )
            .xDataMappingService( this.xDataMappingService )
            .siteConfigService( this.siteConfigService )
            .formDefaultValuesProcessor( ( form, data ) -> {
            } )
            .pageFormDefaultValuesProcessor( ( page ) -> {
            } )
            .xDataDefaultValuesProcessor( extraDatas -> {
            } )
            .build();
    }

    private Node mockNodeServiceCreate( final InvocationOnMock invocation )
    {
        CreateNodeParams params = invocation.getArgument( 0 );

        final AccessControlList permissions = AccessControlList.create()
            .add( AccessControlEntry.create().allowAll().principal( PrincipalKey.ofAnonymous() ).build() )
            .build();

        return Node.create()
            .id( params.getNodeId() != null ? params.getNodeId() : new NodeId() )
            .parentPath( params.getParent() )
            .name( NodeName.from( params.getName() ) )
            .data( params.getData() )
            .indexConfigDocument( params.getIndexConfigDocument() )
            .childOrder( params.getChildOrder() != null ? params.getChildOrder() : ChildOrder.defaultOrder() )
            .permissions( permissions )
            .nodeType( params.getNodeType() != null ? params.getNodeType() : NodeType.DEFAULT_NODE_COLLECTION )
            .timestamp( Instant.now() )
            .build();
    }

    private void mockContentRootNode()
    {
        mockContentRootNode( null );
    }

    private void mockContentRootNode( final String language )
    {

        final PropertyTree tree = new PropertyTree();
        tree.addString( ContentPropertyNames.TYPE, "folder" );
        tree.addString( ContentPropertyNames.CREATOR, "user:system:user1" );
        tree.addString( ContentPropertyNames.LANGUAGE, language );
        tree.addSet( ContentPropertyNames.DATA, tree.newSet() );

        final Node contentRootNode =
            Node.create().id( NodeId.from( "id1" ) ).name( "content" ).parentPath( NodePath.ROOT ).data( tree ).build();

        when( nodeService.getByPath( ContentConstants.CONTENT_ROOT_PATH ) ).thenReturn( contentRootNode );

    }

    private void mockContentNode( final String name, final String parentPath, final String language,
                                  final EnumSet<ContentInheritType> inherit )
    {
        final PropertyTree data = new PropertyTree();
        data.setSet( ContentPropertyNames.DATA, data.newSet() );
        data.setString( ContentPropertyNames.CREATOR, "user:myidprovider:user1" );
        data.setString( ContentPropertyNames.TYPE, ContentTypeName.folder().toString() );
        data.setString( ContentPropertyNames.LANGUAGE, language );
        data.addStrings( ContentPropertyNames.INHERIT, inherit.stream().map( ContentInheritType::name ).collect( Collectors.toList() ) );

        final Node contentNode =
            Node.create().id( NodeId.from( "id1" ) ).name( name ).parentPath( new NodePath( parentPath ) ).data( data ).build();

        when( nodeService.getByPath( contentNode.path() ) ).thenReturn( contentNode );

    }

    private NodePath initContent( final ContentTypeName contentTypeName, final String name, final NodePath parentPath )
    {
        final PropertyTree nodeData = new PropertyTree();
        nodeData.setString( ContentPropertyNames.TYPE, contentTypeName.toString() );
        nodeData.setSet( ContentPropertyNames.DATA, nodeData.newSet() );
        nodeData.setString( ContentPropertyNames.CREATOR, "user:myidprovider:user1" );

        final Node node = Node.create().id( NodeId.from( name ) ).name( name ).parentPath( parentPath ).data( nodeData ).build();

        when( nodeService.getByPath( Mockito.eq( node.path() ) ) ).thenReturn( node );
        when( nodeService.getById( Mockito.eq( node.id() ) ) ).thenReturn( node );

        return node.path();
    }

}
