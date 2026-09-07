package com.enonic.xp.core.dynamic;

import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.impl.app.ApplicationHelper;
import com.enonic.xp.core.impl.app.ApplicationRepoInitializer;
import com.enonic.xp.core.impl.app.CreateDynamicCmsParams;
import com.enonic.xp.core.impl.app.DynamicSchemaServiceImpl;
import com.enonic.xp.core.impl.app.SchemaResourceNames;
import com.enonic.xp.core.impl.event.EventPublisherImpl;
import com.enonic.xp.core.impl.project.ProjectConfig;
import com.enonic.xp.core.impl.project.ProjectServiceImpl;
import com.enonic.xp.core.impl.security.PasswordSecurityService;
import com.enonic.xp.core.impl.security.SecurityAuditLogSupportImpl;
import com.enonic.xp.core.impl.security.SecurityConfig;
import com.enonic.xp.core.impl.security.SecurityInitializer;
import com.enonic.xp.core.impl.security.SecurityServiceImpl;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.exception.ForbiddenAccessException;
import com.enonic.xp.internal.blobstore.MemoryBlobStore;
import com.enonic.xp.itest.AbstractElasticsearchIntegrationTest;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.project.CreateProjectParams;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.region.ComponentDescriptor;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.repo.impl.binary.BinaryServiceImpl;
import com.enonic.xp.repo.impl.branch.storage.BranchServiceImpl;
import com.enonic.xp.repo.impl.commit.CommitServiceImpl;
import com.enonic.xp.repo.impl.config.RepoConfiguration;
import com.enonic.xp.repo.impl.elasticsearch.IndexServiceInternalImpl;
import com.enonic.xp.repo.impl.elasticsearch.search.SearchDaoImpl;
import com.enonic.xp.repo.impl.elasticsearch.storage.StorageDaoImpl;
import com.enonic.xp.repo.impl.index.IndexServiceImpl;
import com.enonic.xp.repo.impl.node.NodeServiceImpl;
import com.enonic.xp.repo.impl.node.dao.NodeVersionServiceImpl;
import com.enonic.xp.repo.impl.repository.NodeRepositoryServiceImpl;
import com.enonic.xp.repo.impl.repository.RepositoryEntryServiceImpl;
import com.enonic.xp.repo.impl.repository.RepositoryServiceImpl;
import com.enonic.xp.repo.impl.repository.SystemRepoInitializer;
import com.enonic.xp.repo.impl.search.NodeSearchServiceImpl;
import com.enonic.xp.repo.impl.storage.IndexDataServiceImpl;
import com.enonic.xp.repo.impl.storage.NodeStorageServiceImpl;
import com.enonic.xp.repo.impl.version.VersionServiceImpl;
import com.enonic.xp.resource.CreateDynamicComponentParams;
import com.enonic.xp.resource.CreateDynamicContentSchemaParams;
import com.enonic.xp.resource.CreateDynamicStylesParams;
import com.enonic.xp.resource.DeleteDynamicComponentParams;
import com.enonic.xp.resource.DeleteDynamicContentSchemaParams;
import com.enonic.xp.resource.DynamicComponentType;
import com.enonic.xp.resource.DynamicContentSchemaType;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.GetDynamicComponentParams;
import com.enonic.xp.resource.GetDynamicContentSchemaParams;
import com.enonic.xp.resource.ListDynamicComponentsParams;
import com.enonic.xp.resource.ListDynamicContentSchemasParams;
import com.enonic.xp.resource.UpdateDynamicCmsParams;
import com.enonic.xp.resource.UpdateDynamicComponentParams;
import com.enonic.xp.resource.UpdateDynamicContentSchemaParams;
import com.enonic.xp.resource.UpdateDynamicStylesParams;
import com.enonic.xp.schema.BaseSchema;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.formfragment.FormFragmentDescriptor;
import com.enonic.xp.schema.formfragment.FormFragmentName;
import com.enonic.xp.schema.mixin.MixinDescriptor;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.site.CmsDescriptor;
import com.enonic.xp.style.StyleDescriptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DynamicSchemaServiceImplTest
    extends AbstractElasticsearchIntegrationTest
{
    NodeServiceImpl nodeService;

    private DynamicSchemaServiceImpl dynamicSchemaService;

    private ProjectServiceImpl projectService;

    private static final String CMS_DESCRIPTOR_DEFAULT_VALUE = """
        kind: "CMS"
        mixins: [ ]
        form: [ ]
        """;

    private static Context ctxDefault()
    {
        return ContextBuilder.copyOf( ContextAccessor.current() ).build();
    }

    private static Context createAdminContext()
    {
        return ContextBuilder.copyOf( ctxDefault() )
            .authInfo( AuthenticationInfo.create().principals( RoleKeys.AUTHENTICATED, RoleKeys.ADMIN ).user( User.anonymous() ).build() )
            .build();
    }

    private static Context createSchemaAdminContext()
    {
        return ContextBuilder.copyOf( ctxDefault() )
            .authInfo(
                AuthenticationInfo.create().principals( RoleKeys.AUTHENTICATED, RoleKeys.SCHEMA_ADMIN ).user( User.anonymous() ).build() )
            .build();
    }

    // admin context of system-repo, where the schema of an application is persisted below /applications/<app>/cms
    private static Context appRepoAdminContext()
    {
        return ContextBuilder.copyOf( createAdminContext() )
            .repositoryId( SystemConstants.SYSTEM_REPO_ID )
            .branch( SystemConstants.BRANCH_SYSTEM )
            .build();
    }

    // the dynamic schema service writes under /applications/<app>/cms/<kind>, and only creates the innermost folder itself
    private void createApplicationNodes( final ApplicationKey applicationKey )
    {
        ApplicationHelper.runAsAdmin( () -> {
            final NodePath appPath = createFolderNode( new NodePath( "/applications" ), applicationKey.toString() );
            final NodePath cmsPath = createFolderNode( appPath, SchemaResourceNames.CMS_ROOT_NAME );

            for ( final String name : List.of( SchemaResourceNames.CONTENT_TYPE_ROOT_NAME, SchemaResourceNames.PART_ROOT_NAME,
                                               SchemaResourceNames.LAYOUT_ROOT_NAME, SchemaResourceNames.PAGE_ROOT_NAME,
                                               SchemaResourceNames.FORM_FRAGMENTS_ROOT_NAME, SchemaResourceNames.MIXINS_ROOT_NAME ) )
            {
                createFolderNode( cmsPath, name );
            }

            nodeService.refresh( RefreshMode.ALL );
        } );
    }

    private NodePath createFolderNode( final NodePath parent, final String name )
    {
        return nodeService.create( CreateNodeParams.create()
                                       .data( new PropertyTree() )
                                       .name( name )
                                       .parent( parent )
                                       .inheritPermissions( true )
                                       .build() ).path();
    }

    @BeforeEach
    void initService()
        throws Exception
    {
        deleteAllIndices();

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        final MemoryBlobStore blobStore = new MemoryBlobStore();

        BinaryServiceImpl binaryService = new BinaryServiceImpl( blobStore );

        final StorageDaoImpl storageDao = new StorageDaoImpl( client );

        final EventPublisherImpl eventPublisher = new EventPublisherImpl( executorService );

        final SearchDaoImpl searchDao = new SearchDaoImpl( client );

        BranchServiceImpl branchService = new BranchServiceImpl( storageDao, searchDao );

        VersionServiceImpl versionService = new VersionServiceImpl( storageDao );

        CommitServiceImpl commitService = new CommitServiceImpl( storageDao );

        IndexServiceInternalImpl indexServiceInternal = new IndexServiceInternalImpl( client );

        NodeVersionServiceImpl nodeDao = new NodeVersionServiceImpl( blobStore, new RepoConfiguration( Map.of() ) );

        IndexDataServiceImpl indexedDataService = new IndexDataServiceImpl( storageDao );

        NodeSearchServiceImpl searchService = new NodeSearchServiceImpl( searchDao );

        NodeStorageServiceImpl storageService =
            new NodeStorageServiceImpl( versionService, branchService, commitService, nodeDao, indexedDataService );

        final RepositoryEntryServiceImpl repositoryEntryService =
            new RepositoryEntryServiceImpl( indexServiceInternal, storageService, searchService, eventPublisher, binaryService );

        IndexServiceImpl indexService =
            new IndexServiceImpl( indexServiceInternal, indexedDataService, searchService, nodeDao, repositoryEntryService );

        final NodeRepositoryServiceImpl nodeRepositoryService = new NodeRepositoryServiceImpl( indexServiceInternal );

        RepositoryServiceImpl repositoryService =
            new RepositoryServiceImpl( repositoryEntryService, nodeRepositoryService, storageService, searchService, branchService,
                                       () -> null );
        SystemRepoInitializer.create()
            .setIndexServiceInternal( indexServiceInternal )
            .setNodeStorageService( storageService )
            .setRepositoryEntryService( repositoryEntryService )
            .setNodeRepositoryService( nodeRepositoryService )
            .build()
            .initialize();

        nodeService = new NodeServiceImpl( indexServiceInternal, storageService, searchService, eventPublisher, binaryService );

        ApplicationRepoInitializer.create().setIndexService( indexService ).setNodeService( nodeService ).build().initialize();

        this.dynamicSchemaService = new DynamicSchemaServiceImpl( nodeService );

        final SecurityConfig securityConfig = mock( SecurityConfig.class, withSettings().stubOnly()
            .defaultAnswer( invocationOnMock -> invocationOnMock.getMethod().getDefaultValue() ) );

        final SecurityAuditLogSupportImpl securityAuditLogSupport = new SecurityAuditLogSupportImpl( mock( AuditLogService.class ) );
        securityAuditLogSupport.activate( securityConfig );

        final PasswordSecurityService passwordSecurityService = new PasswordSecurityService();
        passwordSecurityService.activate( securityConfig );

        SecurityServiceImpl securityService = new SecurityServiceImpl( nodeService, securityAuditLogSupport, passwordSecurityService );
        SecurityInitializer.create()
            .setIndexService( indexService )
            .setSecurityService( securityService )
            .setNodeService( nodeService )
            .build()
            .initialize();

        createApplicationNodes( ApplicationKey.from( "myapp" ) );
        createApplicationNodes( ApplicationKey.from( "my_other_app" ) );

        projectService =
            new ProjectServiceImpl( repositoryService, repositoryService, indexService, nodeService, securityService, eventPublisher,
                                    mock( ProjectConfig.class ) );
        projectService.initialize();

        createAdminContext().runWith( () -> projectService.create(
            CreateProjectParams.create().name( ProjectName.from( "my-project" ) ).displayName( "test" ).build() ) );

    }

    @Test
    void createContentTypeSchema()
        throws Exception
    {
        final String resource = readResource( "_contentType.yaml" );

        CreateDynamicContentSchemaParams params = CreateDynamicContentSchemaParams.create()
            .name( ContentTypeName.from( "myapp:mytype" ) )
            .resource( resource )
            .type( DynamicContentSchemaType.CONTENT_TYPE )
            .build();

        final DynamicSchemaResult<BaseSchema<?>> result =
            createAdminContext().callWith( () -> dynamicSchemaService.createContentSchema( params ) );

        final ContentType contentType = (ContentType) result.getSchema();

        createAdminContext().runWith( () -> assertThat( contentType ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getContentSchema( GetDynamicContentSchemaParams.create()
                                                                   .name( ContentTypeName.from( "myapp:mytype" ) )
                                                                   .type( DynamicContentSchemaType.CONTENT_TYPE )
                                                                   .build() ).getSchema() ) );

        assertEquals( "myapp:mytype", contentType.getName().toString() );
        assertEquals( "Virtual Content Type", contentType.getTitle() );
        assertEquals( "description", contentType.getDescription() );
        assertEquals( 1, contentType.getForm().size() );
        assertFalse( contentType.allowChildContent() );
        assertFalse( contentType.isAbstract() );
        assertTrue( contentType.isFinal() );
        assertNotNull( contentType.getModifiedTime() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/content-types/mytype/mytype.yaml", result.getResource().getKey().toString() );
        assertTrue( result.getResource().getSize() > 0 );

        final Node resourceNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/applications/myapp/cms/content-types/mytype/mytype.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void updateContentTypeSchema()
        throws Exception
    {
        final CreateDynamicContentSchemaParams createParams = CreateDynamicContentSchemaParams.create()
            .name( ContentTypeName.from( "myapp:mytype" ) ).resource( """
                                                                          kind: "ContentType"
                                                                          superType: "base:unstructured"
                                                                          """ )
            .type( DynamicContentSchemaType.CONTENT_TYPE )
            .build();

        createAdminContext().runWith( () -> dynamicSchemaService.createContentSchema( createParams ) );

        final String resource = readResource( "_contentType.yaml" );

        final UpdateDynamicContentSchemaParams updateParams = UpdateDynamicContentSchemaParams.create()
            .name( ContentTypeName.from( "myapp:mytype" ) )
            .resource( resource )
            .type( DynamicContentSchemaType.CONTENT_TYPE )
            .build();

        final DynamicSchemaResult<BaseSchema<?>> result =
            createAdminContext().callWith( () -> dynamicSchemaService.updateContentSchema( updateParams ) );

        final ContentType contentType = (ContentType) result.getSchema();

        createAdminContext().runWith( () -> assertThat( contentType ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getContentSchema( GetDynamicContentSchemaParams.create()
                                                                   .name( ContentTypeName.from( "myapp:mytype" ) )
                                                                   .type( DynamicContentSchemaType.CONTENT_TYPE )
                                                                   .build() ).getSchema() ) );

        assertEquals( "myapp:mytype", contentType.getName().toString() );
        assertEquals( "Virtual Content Type", contentType.getTitle() );
        assertEquals( "description", contentType.getDescription() );
        assertEquals( 1, contentType.getForm().size() );
        assertFalse( contentType.allowChildContent() );
        assertFalse( contentType.isAbstract() );
        assertTrue( contentType.isFinal() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/content-types/mytype/mytype.yaml", result.getResource().getKey().toString() );

        final Node resourceNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/applications/myapp/cms/content-types/mytype/mytype.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void createFormFragmentSchema()
        throws Exception
    {
        final String resource = readResource( "_formFragment.yaml" );

        CreateDynamicContentSchemaParams params = CreateDynamicContentSchemaParams.create()
            .name( FormFragmentName.from( "myapp:my-fragment" ) )
            .resource( resource )
            .type( DynamicContentSchemaType.FORM_FRAGMENT )
            .build();

        final DynamicSchemaResult<BaseSchema<?>> result =
            createAdminContext().callWith( () -> dynamicSchemaService.createContentSchema( params ) );

        final FormFragmentDescriptor fragment = (FormFragmentDescriptor) result.getSchema();

        createAdminContext().runWith( () -> assertThat( fragment ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getContentSchema( GetDynamicContentSchemaParams.create()
                                                                   .name( FormFragmentName.from( "myapp:my-fragment" ) )
                                                                   .type( DynamicContentSchemaType.FORM_FRAGMENT )
                                                                   .build() ).getSchema() ) );

        assertEquals( "myapp:my-fragment", fragment.getName().toString() );
        assertEquals( "Virtual FormFragment", fragment.getTitle() );
        assertEquals( "FormFragment description", fragment.getDescription() );
        assertEquals( 2, fragment.getForm().size() );
        assertEquals( "myapp:inline", fragment.getForm().getFormFragment( "inline" ).getFormFragmentName().toString() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/form-fragments/my-fragment/my-fragment.yaml", result.getResource().getKey().toString() );

        final Node resourceNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/applications/myapp/cms/form-fragments/my-fragment/my-fragment.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void createFormFragmentSchemaAsDevSchemaAdmin()
        throws Exception
    {
        final String resource = readResource( "_formFragment.yaml" );

        CreateDynamicContentSchemaParams params = CreateDynamicContentSchemaParams.create()
            .name( FormFragmentName.from( "myapp:my-fragment" ) )
            .resource( resource )
            .type( DynamicContentSchemaType.FORM_FRAGMENT )
            .build();

        final DynamicSchemaResult<BaseSchema<?>> result =
            createSchemaAdminContext().callWith( () -> dynamicSchemaService.createContentSchema( params ) );

        assertNotNull( result.getResource() );
    }

    @Test
    void createFormFragmentSchemaAsNonSchemaAdmin()
        throws Exception
    {
        final String resource = readResource( "_formFragment.yaml" );

        CreateDynamicContentSchemaParams params = CreateDynamicContentSchemaParams.create()
            .name( FormFragmentName.from( "myapp:my-fragment" ) )
            .resource( resource )
            .type( DynamicContentSchemaType.FORM_FRAGMENT )
            .build();

        assertThrows( ForbiddenAccessException.class,
                      () -> ctxDefault().callWith( () -> dynamicSchemaService.createContentSchema( params ) ) );
    }

    @Test
    void updateFormFragmentSchema()
        throws Exception
    {

        final CreateDynamicContentSchemaParams createParams =
            CreateDynamicContentSchemaParams.create()
                .name( FormFragmentName.from( "myapp:my-fragment" ) )
                .resource( """
                               kind: "FormFragment"
                               title: "MyFormFragment"
                               """ )
                .type( DynamicContentSchemaType.FORM_FRAGMENT )
                .build();

        createAdminContext().runWith( () -> dynamicSchemaService.createContentSchema( createParams ) );

        final String resource = readResource( "_formFragment.yaml" );

        final UpdateDynamicContentSchemaParams updateParams = UpdateDynamicContentSchemaParams.create()
            .name( FormFragmentName.from( "myapp:my-fragment" ) )
            .resource( resource )
            .type( DynamicContentSchemaType.FORM_FRAGMENT )
            .build();

        final DynamicSchemaResult<BaseSchema<?>> result =
            createAdminContext().callWith( () -> dynamicSchemaService.updateContentSchema( updateParams ) );

        final FormFragmentDescriptor fragment = (FormFragmentDescriptor) result.getSchema();

        createAdminContext().runWith( () -> assertThat( fragment ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getContentSchema( GetDynamicContentSchemaParams.create()
                                                                   .name( FormFragmentName.from( "myapp:my-fragment" ) )
                                                                   .type( DynamicContentSchemaType.FORM_FRAGMENT )
                                                                   .build() ).getSchema() ) );

        assertEquals( "myapp:my-fragment", fragment.getName().toString() );
        assertEquals( "Virtual FormFragment", fragment.getTitle() );
        assertEquals( "FormFragment description", fragment.getDescription() );
        assertEquals( 2, fragment.getForm().size() );
        assertEquals( "myapp:inline", fragment.getForm().getFormFragment( "inline" ).getFormFragmentName().toString() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/form-fragments/my-fragment/my-fragment.yaml", result.getResource().getKey().toString() );

        final Node resourceNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/applications/myapp/cms/form-fragments/my-fragment/my-fragment.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void updateFormFragmentSchemaAsSchemaAdmin()
        throws Exception
    {

        final CreateDynamicContentSchemaParams createParams =
            CreateDynamicContentSchemaParams.create()
                .name( FormFragmentName.from( "myapp:my-fragment" ) )
                .resource( """
                               kind: "FormFragment"
                               title: "FormFragment"
                               """ )
                .type( DynamicContentSchemaType.FORM_FRAGMENT )
                .build();

        createSchemaAdminContext().runWith( () -> dynamicSchemaService.createContentSchema( createParams ) );

        final String resource = readResource( "_formFragment.yaml" );

        final UpdateDynamicContentSchemaParams updateParams = UpdateDynamicContentSchemaParams.create()
            .name( FormFragmentName.from( "myapp:my-fragment" ) )
            .resource( resource )
            .type( DynamicContentSchemaType.FORM_FRAGMENT )
            .build();

        final DynamicSchemaResult<BaseSchema<?>> result =
            createSchemaAdminContext().callWith( () -> dynamicSchemaService.updateContentSchema( updateParams ) );

        assertNotNull( result.getResource() );
    }

    @Test
    void updateFormFragmentSchemaAsNonSchemaAdmin()
        throws Exception
    {
        final CreateDynamicContentSchemaParams createParams =
            CreateDynamicContentSchemaParams.create()
                .name( FormFragmentName.from( "myapp:my-fragment" ) )
                .resource( """
                               kind: "FormFragment"
                               title: "FormFragment"
                               """ )
                .type( DynamicContentSchemaType.FORM_FRAGMENT )
                .build();

        createSchemaAdminContext().runWith( () -> dynamicSchemaService.createContentSchema( createParams ) );

        final String resource = readResource( "_formFragment.yaml" );

        final UpdateDynamicContentSchemaParams updateParams = UpdateDynamicContentSchemaParams.create()
            .name( FormFragmentName.from( "myapp:my-fragment" ) )
            .resource( resource )
            .type( DynamicContentSchemaType.FORM_FRAGMENT )
            .build();

        assertThrows( ForbiddenAccessException.class,
                      () -> ctxDefault().callWith( () -> dynamicSchemaService.updateContentSchema( updateParams ) ) );
    }


    @Test
    void createMixinSchema()
        throws Exception
    {
        final String resource = readResource( "_mixin.yaml" );

        CreateDynamicContentSchemaParams params = CreateDynamicContentSchemaParams.create()
            .name( MixinName.from( "myapp:mymixin" ) )
            .resource( resource )
            .type( DynamicContentSchemaType.MIXIN )
            .build();

        final DynamicSchemaResult<BaseSchema<?>> result =
            createAdminContext().callWith( () -> dynamicSchemaService.createContentSchema( params ) );

        final MixinDescriptor mixinDescriptor = (MixinDescriptor) result.getSchema();

        createAdminContext().runWith( () -> assertThat( mixinDescriptor ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getContentSchema( GetDynamicContentSchemaParams.create()
                                                                   .name( MixinName.from( "myapp:mymixin" ) )
                                                                   .type( DynamicContentSchemaType.MIXIN )
                                                                   .build() ).getSchema() ) );

        assertEquals( "myapp:mymixin", mixinDescriptor.getName().toString() );
        assertEquals( "Virtual Mixin", mixinDescriptor.getTitle() );
        assertEquals( "Mixin description", mixinDescriptor.getDescription() );
        assertEquals( 1, mixinDescriptor.getForm().size() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/mixins/mymixin/mymixin.yaml", result.getResource().getKey().toString() );

        final Node resourceNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/applications/myapp/cms/mixins/mymixin/mymixin.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void updateMixinSchema()
        throws Exception
    {
        final CreateDynamicContentSchemaParams createParams =
            CreateDynamicContentSchemaParams.create()
                .name( MixinName.from( "myapp:mymixin" ) )
                .resource( """
                               kind: "Mixin"
                               title: "Virtual MIXIN"
                               form: [ ]
                               """ )
                .type( DynamicContentSchemaType.MIXIN )
                .build();

        createAdminContext().runWith( () -> dynamicSchemaService.createContentSchema( createParams ) );

        final String resource = readResource( "_mixin.yaml" );

        final UpdateDynamicContentSchemaParams updateParams = UpdateDynamicContentSchemaParams.create()
            .name( MixinName.from( "myapp:mymixin" ) )
            .resource( resource )
            .type( DynamicContentSchemaType.MIXIN )
            .build();

        final DynamicSchemaResult<BaseSchema<?>> result =
            createAdminContext().callWith( () -> dynamicSchemaService.updateContentSchema( updateParams ) );

        final MixinDescriptor mixinDescriptor = (MixinDescriptor) result.getSchema();

        createAdminContext().runWith( () -> assertThat( mixinDescriptor ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getContentSchema( GetDynamicContentSchemaParams.create()
                                                                   .name( MixinName.from( "myapp:mymixin" ) )
                                                                   .type( DynamicContentSchemaType.MIXIN )
                                                                   .build() ).getSchema() ) );

        assertEquals( "myapp:mymixin", mixinDescriptor.getName().toString() );
        assertEquals( "Virtual Mixin", mixinDescriptor.getTitle() );
        assertEquals( "Mixin description", mixinDescriptor.getDescription() );
        assertEquals( 1, mixinDescriptor.getForm().size() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/mixins/mymixin/mymixin.yaml", result.getResource().getKey().toString() );

        final Node resourceNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/applications/myapp/cms/mixins/mymixin/mymixin.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void createPartComponent()
        throws Exception
    {
        final String resource = readResource( "_part.yaml" );

        CreateDynamicComponentParams params = CreateDynamicComponentParams.create()
            .descriptorKey( DescriptorKey.from( "myapp:mypart" ) )
            .resource( resource )
            .type( DynamicComponentType.PART )
            .build();

        final DynamicSchemaResult<ComponentDescriptor> result =
            createAdminContext().callWith( () -> dynamicSchemaService.createComponent( params ) );

        final PartDescriptor partDescriptor = (PartDescriptor) result.getSchema();

        createAdminContext().runWith( () -> assertThat( partDescriptor ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getComponent( GetDynamicComponentParams.create()
                                                               .descriptorKey( DescriptorKey.from( "myapp:mypart" ) )
                                                               .type( DynamicComponentType.PART )
                                                               .build() ).getSchema() ) );

        assertEquals( "mypart", partDescriptor.getName() );
        assertEquals( "myapp", partDescriptor.getApplicationKey().toString() );
        assertEquals( "Virtual Part", partDescriptor.getTitle() );
        assertEquals( "key.display-name", partDescriptor.getTitleI18nKey() );
        assertEquals( "My Part Description", partDescriptor.getDescription() );
        assertEquals( "key.description", partDescriptor.getDescriptionI18nKey() );
        assertEquals( 2, partDescriptor.getConfig().size() );
        assertNotNull( partDescriptor.getModifiedTime() );

        assertEquals( 1, partDescriptor.getSchemaConfig().properties().size() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/parts/mypart/mypart.yaml", result.getResource().getKey().toString() );

        final Node resourceNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/applications/myapp/cms/parts/mypart/mypart.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void updatePartComponent()
        throws Exception
    {

        final CreateDynamicComponentParams createParams =
            CreateDynamicComponentParams.create()
                .descriptorKey( DescriptorKey.from( "myapp:mypart" ) )
                .resource( """
                               kind: "Part"
                               title: "MyPart"
                               form: [ ]
                               """ )
                .type( DynamicComponentType.PART )
                .build();

        createAdminContext().runWith( () -> dynamicSchemaService.createComponent( createParams ) );

        final String resource = readResource( "_part.yaml" );

        final UpdateDynamicComponentParams updateParams = UpdateDynamicComponentParams.create()
            .descriptorKey( DescriptorKey.from( "myapp:mypart" ) )
            .resource( resource )
            .type( DynamicComponentType.PART )
            .build();

        final DynamicSchemaResult<ComponentDescriptor> result =
            createAdminContext().callWith( () -> dynamicSchemaService.updateComponent( updateParams ) );

        final PartDescriptor partDescriptor = (PartDescriptor) result.getSchema();

        createAdminContext().runWith( () -> assertThat( partDescriptor ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getComponent( GetDynamicComponentParams.create()
                                                               .descriptorKey( DescriptorKey.from( "myapp:mypart" ) )
                                                               .type( DynamicComponentType.PART )
                                                               .build() ).getSchema() ) );

        assertEquals( "mypart", partDescriptor.getName() );
        assertEquals( "myapp", partDescriptor.getApplicationKey().toString() );
        assertEquals( "Virtual Part", partDescriptor.getTitle() );
        assertEquals( "key.display-name", partDescriptor.getTitleI18nKey() );
        assertEquals( "My Part Description", partDescriptor.getDescription() );
        assertEquals( "key.description", partDescriptor.getDescriptionI18nKey() );
        assertEquals( 2, partDescriptor.getConfig().size() );

        assertEquals( 1, partDescriptor.getSchemaConfig().properties().size() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/parts/mypart/mypart.yaml", result.getResource().getKey().toString() );

        final Node resourceNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/applications/myapp/cms/parts/mypart/mypart.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void createLayoutComponent()
        throws Exception
    {
        final String resource = readResource( "_layout.yaml" );

        CreateDynamicComponentParams params = CreateDynamicComponentParams.create()
            .descriptorKey( DescriptorKey.from( "myapp:mylayout" ) )
            .resource( resource )
            .type( DynamicComponentType.LAYOUT )
            .build();

        final DynamicSchemaResult<ComponentDescriptor> result =
            createAdminContext().callWith( () -> dynamicSchemaService.createComponent( params ) );

        final LayoutDescriptor layoutDescriptor = (LayoutDescriptor) result.getSchema();

        createAdminContext().runWith( () -> assertThat( layoutDescriptor ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getComponent( GetDynamicComponentParams.create()
                                                               .descriptorKey( DescriptorKey.from( "myapp:mylayout" ) )
                                                               .type( DynamicComponentType.LAYOUT )
                                                               .build() ).getSchema() ) );

        assertEquals( "mylayout", layoutDescriptor.getName() );
        assertEquals( "myapp", layoutDescriptor.getApplicationKey().toString() );
        assertEquals( "Virtual Layout", layoutDescriptor.getTitle() );
        assertEquals( "key.display-name", layoutDescriptor.getTitleI18nKey() );
        assertEquals( "My Layout Description", layoutDescriptor.getDescription() );
        assertEquals( "key.description", layoutDescriptor.getDescriptionI18nKey() );
        assertEquals( 2, layoutDescriptor.getConfig().size() );
        assertEquals( 3, layoutDescriptor.getRegions().numberOfRegions() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/layouts/mylayout/mylayout.yaml", result.getResource().getKey().toString() );

        final Node resourceNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/applications/myapp/cms/layouts/mylayout/mylayout.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void updateLayoutComponent()
        throws Exception
    {
        final CreateDynamicComponentParams params =
            CreateDynamicComponentParams.create()
                .descriptorKey( DescriptorKey.from( "myapp:mylayout" ) )
                .resource( """
                               kind: "Layout"
                               title: MyLayout
                               regions: [ ]
                               
                               """ )
                .type( DynamicComponentType.LAYOUT )
                .build();

        createAdminContext().runWith( () -> dynamicSchemaService.createComponent( params ) );

        final String resource = readResource( "_layout.yaml" );

        final UpdateDynamicComponentParams updateParams = UpdateDynamicComponentParams.create()
            .descriptorKey( DescriptorKey.from( "myapp:mylayout" ) )
            .resource( resource )
            .type( DynamicComponentType.LAYOUT )
            .build();

        final DynamicSchemaResult<ComponentDescriptor> result =
            createAdminContext().callWith( () -> dynamicSchemaService.updateComponent( updateParams ) );

        final LayoutDescriptor layoutDescriptor = (LayoutDescriptor) result.getSchema();

        createAdminContext().runWith( () -> assertThat( layoutDescriptor ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getComponent( GetDynamicComponentParams.create()
                                                               .descriptorKey( DescriptorKey.from( "myapp:mylayout" ) )
                                                               .type( DynamicComponentType.LAYOUT )
                                                               .build() ).getSchema() ) );

        assertEquals( "mylayout", layoutDescriptor.getName() );
        assertEquals( "myapp", layoutDescriptor.getApplicationKey().toString() );
        assertEquals( "Virtual Layout", layoutDescriptor.getTitle() );
        assertEquals( "key.display-name", layoutDescriptor.getTitleI18nKey() );
        assertEquals( "My Layout Description", layoutDescriptor.getDescription() );
        assertEquals( "key.description", layoutDescriptor.getDescriptionI18nKey() );
        assertEquals( 2, layoutDescriptor.getConfig().size() );
        assertEquals( 3, layoutDescriptor.getRegions().numberOfRegions() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/layouts/mylayout/mylayout.yaml", result.getResource().getKey().toString() );

        final Node resourceNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/applications/myapp/cms/layouts/mylayout/mylayout.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void createPageComponent()
        throws Exception
    {
        final String resource = readResource( "_page.yaml" );

        CreateDynamicComponentParams params = CreateDynamicComponentParams.create()
            .descriptorKey( DescriptorKey.from( "myapp:mypage" ) )
            .resource( resource )
            .type( DynamicComponentType.PAGE )
            .build();

        final DynamicSchemaResult<ComponentDescriptor> result =
            createAdminContext().callWith( () -> dynamicSchemaService.createComponent( params ) );

        final PageDescriptor pageDescriptor = (PageDescriptor) result.getSchema();

        createAdminContext().runWith( () -> assertThat( pageDescriptor ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getComponent( GetDynamicComponentParams.create()
                                                               .descriptorKey( DescriptorKey.from( "myapp:mypage" ) )
                                                               .type( DynamicComponentType.PAGE )
                                                               .build() ).getSchema() ) );

        assertEquals( "mypage", pageDescriptor.getName() );
        assertEquals( "myapp", pageDescriptor.getApplicationKey().toString() );
        assertEquals( "Virtual Page", pageDescriptor.getTitle() );
        assertEquals( "key.display-name", pageDescriptor.getTitleI18nKey() );
        assertEquals( "My Page Description", pageDescriptor.getDescription() );
        assertEquals( "key.description", pageDescriptor.getDescriptionI18nKey() );
        assertEquals( 1, pageDescriptor.getConfig().size() );
        assertEquals( 3, pageDescriptor.getRegions().numberOfRegions() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/pages/mypage/mypage.yaml", result.getResource().getKey().toString() );

        final Node resourceNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/applications/myapp/cms/pages/mypage/mypage.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void updatePageComponent()
        throws Exception
    {
        final CreateDynamicComponentParams createParams =
            CreateDynamicComponentParams.create()
                .descriptorKey( DescriptorKey.from( "myapp:mypage" ) )
                .resource( """
                               kind: "Page"
                               title: "MyPage"
                               regions:
                                 - "main"
                               """ )
                .type( DynamicComponentType.PAGE )
                .build();

        createAdminContext().runWith( () -> dynamicSchemaService.createComponent( createParams ) );

        final String resource = readResource( "_page.yaml" );

        final UpdateDynamicComponentParams updateParams = UpdateDynamicComponentParams.create()
            .descriptorKey( DescriptorKey.from( "myapp:mypage" ) )
            .resource( resource )
            .type( DynamicComponentType.PAGE )
            .build();

        final DynamicSchemaResult<ComponentDescriptor> result =
            createAdminContext().callWith( () -> dynamicSchemaService.updateComponent( updateParams ) );

        final PageDescriptor pageDescriptor = (PageDescriptor) result.getSchema();

        createAdminContext().runWith( () -> assertThat( pageDescriptor ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getComponent( GetDynamicComponentParams.create()
                                                               .descriptorKey( DescriptorKey.from( "myapp:mypage" ) )
                                                               .type( DynamicComponentType.PAGE )
                                                               .build() ).getSchema() ) );

        assertEquals( "mypage", pageDescriptor.getName() );
        assertEquals( "myapp", pageDescriptor.getApplicationKey().toString() );
        assertEquals( "Virtual Page", pageDescriptor.getTitle() );
        assertEquals( "key.display-name", pageDescriptor.getTitleI18nKey() );
        assertEquals( "My Page Description", pageDescriptor.getDescription() );
        assertEquals( "key.description", pageDescriptor.getDescriptionI18nKey() );
        assertEquals( 1, pageDescriptor.getConfig().size() );
        assertEquals( 3, pageDescriptor.getRegions().numberOfRegions() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/pages/mypage/mypage.yaml", result.getResource().getKey().toString() );

        final Node resourceNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/applications/myapp/cms/pages/mypage/mypage.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void createSite()
        throws Exception
    {
        final String resource = readResource( "_cms.yaml" );
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        // no cms descriptor until one is created
        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.getCmsDescriptor( applicationKey ) ) ).isNull();

        final DynamicSchemaResult<CmsDescriptor> result = createAdminContext().callWith(
            () -> dynamicSchemaService.createCms( CreateDynamicCmsParams.create().key( applicationKey ).resource( resource ).build() ) );

        final CmsDescriptor cmsDescriptor = result.getSchema();

        createAdminContext().runWith( () -> assertThat( cmsDescriptor ).usingRecursiveComparison(
                RecursiveComparisonConfiguration.builder().withIgnoredFields( "mappingDescriptors" ).build() )
            .isEqualTo( dynamicSchemaService.getCmsDescriptor( applicationKey ).getSchema() ) );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/cms.yaml", result.getResource().getKey().toString() );
        assertNotNull( cmsDescriptor.getModifiedTime() );

        final Node resourceNode =
            appRepoAdminContext().callWith( () -> nodeService.getByPath( new NodePath( "/applications/myapp/cms/cms.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void getSiteNonExistedApp()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "nonapp" );

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.getCmsDescriptor( applicationKey ) ) ).isNull();
    }

    @Test
    void updateSite()
        throws Exception
    {
        final String resource = readResource( "_cms.yaml" );
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        createAdminContext().runWith( () -> dynamicSchemaService.createCms(
            CreateDynamicCmsParams.create().key( applicationKey ).resource( CMS_DESCRIPTOR_DEFAULT_VALUE ).build() ) );

        final DynamicSchemaResult<CmsDescriptor> result = createAdminContext().callWith(
            () -> dynamicSchemaService.updateCms( UpdateDynamicCmsParams.create().key( applicationKey ).resource( resource ).build() ) );

        final CmsDescriptor cmsDescriptor = result.getSchema();

        createAdminContext().runWith( () -> assertThat( cmsDescriptor ).usingRecursiveComparison(
                RecursiveComparisonConfiguration.builder().withIgnoredFields( "mappingDescriptors" ).build() )
            .isEqualTo( dynamicSchemaService.getCmsDescriptor( applicationKey ).getSchema() ) );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/cms.yaml", result.getResource().getKey().toString() );

        final Node resourceNode =
            appRepoAdminContext().callWith( () -> nodeService.getByPath( new NodePath( "/applications/myapp/cms/cms.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void updateNotCreatedSite()
        throws Exception
    {
        final String resource = readResource( "_cms.yaml" );
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        final DynamicSchemaResult<CmsDescriptor> result = createAdminContext().callWith(
            () -> dynamicSchemaService.updateCms( UpdateDynamicCmsParams.create().key( applicationKey ).resource( resource ).build() ) );

        final CmsDescriptor cmsDescriptor = result.getSchema();

        createAdminContext().runWith( () -> assertThat( cmsDescriptor ).usingRecursiveComparison(
                RecursiveComparisonConfiguration.builder().withIgnoredFields( "mappingDescriptors" ).build() )
            .isEqualTo( dynamicSchemaService.getCmsDescriptor( applicationKey ).getSchema() ) );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/cms.yaml", result.getResource().getKey().toString() );

        final Node resourceNode =
            appRepoAdminContext().callWith( () -> nodeService.getByPath( new NodePath( "/applications/myapp/cms/cms.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void deleteCms()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        // no cms descriptor until one is created
        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.getCmsDescriptor( applicationKey ) ) ).isNull();

        createAdminContext().callWith( () -> dynamicSchemaService.createCms(
            CreateDynamicCmsParams.create().key( applicationKey ).resource( readResource( "_cms.yaml" ) ).build() ) );

        DynamicSchemaResult<CmsDescriptor> cmsDescriptorResult =
            createAdminContext().callWith( () -> dynamicSchemaService.getCmsDescriptor( applicationKey ) );

        assertThat( cmsDescriptorResult.getSchema().getForm() ).isNotEmpty();
        assertThat( cmsDescriptorResult.getSchema().getMixinMappings() ).isNotEmpty();

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.deleteCms( applicationKey ) ) ).isTrue();

        // the deleted descriptor is gone, nothing is synthesized in its place
        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.getCmsDescriptor( applicationKey ) ) ).isNull();
    }

    @Test
    void createStyles()
        throws Exception
    {
        final String resource = readResource( "_styles.yaml" );
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.getStyles( applicationKey ) ) ).isNull();

        final DynamicSchemaResult<StyleDescriptor> result = createAdminContext().callWith( () -> dynamicSchemaService.createStyles(
            CreateDynamicStylesParams.create().key( applicationKey ).resource( resource ).build() ) );

        final StyleDescriptor styleDescriptor = result.getSchema();

        createAdminContext().runWith( () -> assertThat( styleDescriptor ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getStyles( applicationKey ).getSchema() ) );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/style/style.yaml", result.getResource().getKey().toString() );
        assertNotNull( styleDescriptor.getModifiedTime() );

        final Node resourceNode =
            appRepoAdminContext().callWith( () -> nodeService.getByPath( new NodePath( "/applications/myapp/cms/style/style.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void updateStyles()
        throws Exception
    {
        final String resource = readResource( "_styles.yaml" );
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        createAdminContext().callWith( () -> dynamicSchemaService.createStyles(
            CreateDynamicStylesParams.create().key( applicationKey ).resource( "kind: \"Style\"\n" ).build() ) );

        final DynamicSchemaResult<StyleDescriptor> result = createAdminContext().callWith( () -> dynamicSchemaService.updateStyles(
            UpdateDynamicStylesParams.create().key( applicationKey ).resource( resource ).build() ) );

        final StyleDescriptor styleDescriptor = result.getSchema();

        createAdminContext().runWith( () -> assertThat( styleDescriptor ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getStyles( applicationKey ).getSchema() ) );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/style/style.yaml", result.getResource().getKey().toString() );

        final Node resourceNode =
            appRepoAdminContext().callWith( () -> nodeService.getByPath( new NodePath( "/applications/myapp/cms/style/style.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void deleteStyles()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.getStyles( applicationKey ) ) ).isNull();

        createAdminContext().callWith( () -> dynamicSchemaService.createStyles(
            CreateDynamicStylesParams.create().key( applicationKey ).resource( readResource( "_styles.yaml" ) ).build() ) );

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.getStyles( applicationKey ) ) ).isNotNull();

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.deleteStyles( applicationKey ) ) ).isTrue();

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.getStyles( applicationKey ) ) ).isNull();
    }


    @Test
    void listPartComponents()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        List<DynamicSchemaResult<ComponentDescriptor>> results = createAdminContext().callWith( () -> dynamicSchemaService.listComponents(
            ListDynamicComponentsParams.create().applicationKey( applicationKey ).type( DynamicComponentType.PART ).build() ) );

        assertTrue( results.isEmpty() );

        DynamicSchemaResult<PartDescriptor> part1 = createAdminContext().callWith( () -> dynamicSchemaService.createComponent(
            CreateDynamicComponentParams.create()
                .descriptorKey( DescriptorKey.from( "myapp:mypart1" ) )
                .resource( readResource( "_part.yaml" ) )
                .type( DynamicComponentType.PART )
                .build() ) );
        DynamicSchemaResult<PartDescriptor> part2 = createAdminContext().callWith( () -> dynamicSchemaService.createComponent(
            CreateDynamicComponentParams.create()
                .descriptorKey( DescriptorKey.from( "myapp:mypart2" ) )
                .resource( readResource( "_part.yaml" ) )
                .type( DynamicComponentType.PART )
                .build() ) );
        DynamicSchemaResult<PartDescriptor> part3 = createAdminContext().callWith( () -> dynamicSchemaService.createComponent(
            CreateDynamicComponentParams.create()
                .descriptorKey( DescriptorKey.from( "my_other_app:mypart" ) )
                .resource( readResource( "_part.yaml" ) )
                .type( DynamicComponentType.PART )
                .build() ) );

        results = createAdminContext().callWith( () -> dynamicSchemaService.listComponents(
            ListDynamicComponentsParams.create().applicationKey( applicationKey ).type( DynamicComponentType.PART ).build() ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( part1, part2 ) );

        results = createAdminContext().callWith( () -> dynamicSchemaService.listComponents( ListDynamicComponentsParams.create()
                                                                                                .applicationKey(
                                                                                                    ApplicationKey.from( "my_other_app" ) )
                                                                                                .type( DynamicComponentType.PART )
                                                                                                .build() ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( part3 ) );

    }

    @Test
    void listContentTypes()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        List<DynamicSchemaResult<BaseSchema<?>>> results = createAdminContext().callWith( () -> dynamicSchemaService.listContentSchemas(
            ListDynamicContentSchemasParams.create()
                .applicationKey( applicationKey )
                .type( DynamicContentSchemaType.CONTENT_TYPE )
                .build() ) );

        assertTrue( results.isEmpty() );

        DynamicSchemaResult<ContentType> contentType1 = createAdminContext().callWith( () -> dynamicSchemaService.createContentSchema(
            CreateDynamicContentSchemaParams.create()
                .name( ContentTypeName.from( "myapp:mytype1" ) )
                .resource( readResource( "_contentType.yaml" ) )
                .type( DynamicContentSchemaType.CONTENT_TYPE )
                .build() ) );
        DynamicSchemaResult<ContentType> contentType2 = createAdminContext().callWith( () -> dynamicSchemaService.createContentSchema(
            CreateDynamicContentSchemaParams.create()
                .name( ContentTypeName.from( "myapp:mytype2" ) )
                .resource( readResource( "_contentType.yaml" ) )
                .type( DynamicContentSchemaType.CONTENT_TYPE )
                .build() ) );
        DynamicSchemaResult<ContentType> contentType3 = createAdminContext().callWith( () -> dynamicSchemaService.createContentSchema(
            CreateDynamicContentSchemaParams.create()
                .name( ContentTypeName.from( "my_other_app:mytype" ) )
                .resource( readResource( "_contentType.yaml" ) )
                .type( DynamicContentSchemaType.CONTENT_TYPE )
                .build() ) );

        results = createAdminContext().callWith( () -> dynamicSchemaService.listContentSchemas( ListDynamicContentSchemasParams.create()
                                                                                                    .applicationKey( applicationKey )
                                                                                                    .type(
                                                                                                        DynamicContentSchemaType.CONTENT_TYPE )
                                                                                                    .build() ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( contentType1, contentType2 ) );

        results = createAdminContext().callWith( () -> dynamicSchemaService.listContentSchemas( ListDynamicContentSchemasParams.create()
                                                                                                    .applicationKey( ApplicationKey.from(
                                                                                                        "my_other_app" ) )
                                                                                                    .type(
                                                                                                        DynamicContentSchemaType.CONTENT_TYPE )
                                                                                                    .build() ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( contentType3 ) );

    }

    @Test
    void listFormFragments()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        List<DynamicSchemaResult<BaseSchema<?>>> results = createAdminContext().callWith( () -> dynamicSchemaService.listContentSchemas(
            ListDynamicContentSchemasParams.create()
                .applicationKey( applicationKey )
                .type( DynamicContentSchemaType.FORM_FRAGMENT )
                .build() ) );

        assertTrue( results.isEmpty() );

        DynamicSchemaResult<FormFragmentDescriptor> fragment1 = createAdminContext().callWith(
            () -> dynamicSchemaService.createContentSchema( CreateDynamicContentSchemaParams.create()
                                                                .name( FormFragmentName.from( "myapp:mytype1" ) )
                                                                .resource( readResource( "_formFragment.yaml" ) )
                                                                .type( DynamicContentSchemaType.FORM_FRAGMENT )
                                                                .build() ) );
        DynamicSchemaResult<FormFragmentDescriptor> fragment2 = createAdminContext().callWith(
            () -> dynamicSchemaService.createContentSchema( CreateDynamicContentSchemaParams.create()
                                                                .name( FormFragmentName.from( "myapp:mytype2" ) )
                                                                .resource( readResource( "_formFragment.yaml" ) )
                                                                .type( DynamicContentSchemaType.FORM_FRAGMENT )
                                                                .build() ) );
        DynamicSchemaResult<FormFragmentDescriptor> fragment3 = createAdminContext().callWith(
            () -> dynamicSchemaService.createContentSchema( CreateDynamicContentSchemaParams.create()
                                                                .name( FormFragmentName.from( "my_other_app:mytype" ) )
                                                                .resource( readResource( "_formFragment.yaml" ) )
                                                                .type( DynamicContentSchemaType.FORM_FRAGMENT )
                                                                .build() ) );

        results = createAdminContext().callWith( () -> dynamicSchemaService.listContentSchemas( ListDynamicContentSchemasParams.create()
                                                                                                    .applicationKey( applicationKey )
                                                                                                    .type(
                                                                                                        DynamicContentSchemaType.FORM_FRAGMENT )
                                                                                                    .build() ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( fragment1, fragment2 ) );

        results = createAdminContext().callWith( () -> dynamicSchemaService.listContentSchemas( ListDynamicContentSchemasParams.create()
                                                                                                    .applicationKey( ApplicationKey.from(
                                                                                                        "my_other_app" ) )
                                                                                                    .type(
                                                                                                        DynamicContentSchemaType.FORM_FRAGMENT )
                                                                                                    .build() ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( fragment3 ) );

    }

    @Test
    void listFormFragmentsAsSchemaAdmin()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        List<DynamicSchemaResult<BaseSchema<?>>> results = createSchemaAdminContext().callWith(
            () -> dynamicSchemaService.listContentSchemas( ListDynamicContentSchemasParams.create()
                                                               .applicationKey( applicationKey )
                                                               .type( DynamicContentSchemaType.FORM_FRAGMENT )
                                                               .build() ) );

        assertTrue( results.isEmpty() );

        DynamicSchemaResult<FormFragmentDescriptor> fragment1 = createAdminContext().callWith(
            () -> dynamicSchemaService.createContentSchema( CreateDynamicContentSchemaParams.create()
                                                                .name( FormFragmentName.from( "myapp:mytype1" ) )
                                                                .resource( readResource( "_formFragment.yaml" ) )
                                                                .type( DynamicContentSchemaType.FORM_FRAGMENT )
                                                                .build() ) );
        DynamicSchemaResult<FormFragmentDescriptor> fragment2 = createAdminContext().callWith(
            () -> dynamicSchemaService.createContentSchema( CreateDynamicContentSchemaParams.create()
                                                                .name( FormFragmentName.from( "myapp:mytype2" ) )
                                                                .resource( readResource( "_formFragment.yaml" ) )
                                                                .type( DynamicContentSchemaType.FORM_FRAGMENT )
                                                                .build() ) );
        DynamicSchemaResult<FormFragmentDescriptor> fragment3 = createAdminContext().callWith(
            () -> dynamicSchemaService.createContentSchema( CreateDynamicContentSchemaParams.create()
                                                                .name( FormFragmentName.from( "my_other_app:mytype" ) )
                                                                .resource( readResource( "_formFragment.yaml" ) )
                                                                .type( DynamicContentSchemaType.FORM_FRAGMENT )
                                                                .build() ) );

        results = createAdminContext().callWith( () -> dynamicSchemaService.listContentSchemas( ListDynamicContentSchemasParams.create()
                                                                                                    .applicationKey( applicationKey )
                                                                                                    .type(
                                                                                                        DynamicContentSchemaType.FORM_FRAGMENT )
                                                                                                    .build() ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( fragment1, fragment2 ) );

        results = createAdminContext().callWith( () -> dynamicSchemaService.listContentSchemas( ListDynamicContentSchemasParams.create()
                                                                                                    .applicationKey( ApplicationKey.from(
                                                                                                        "my_other_app" ) )
                                                                                                    .type(
                                                                                                        DynamicContentSchemaType.FORM_FRAGMENT )
                                                                                                    .build() ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( fragment3 ) );

    }

    @Test
    void listFormFragmentsAsNonSchemaAdmin()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        List<DynamicSchemaResult<BaseSchema<?>>> results = createSchemaAdminContext().callWith(
            () -> dynamicSchemaService.listContentSchemas( ListDynamicContentSchemasParams.create()
                                                               .applicationKey( applicationKey )
                                                               .type( DynamicContentSchemaType.FORM_FRAGMENT )
                                                               .build() ) );

        assertTrue( results.isEmpty() );

        createAdminContext().callWith( () -> dynamicSchemaService.createContentSchema( CreateDynamicContentSchemaParams.create()
                                                                                           .name( FormFragmentName.from( "myapp:mytype1" ) )
                                                                                           .resource( readResource( "_formFragment.yaml" ) )
                                                                                           .type( DynamicContentSchemaType.FORM_FRAGMENT )
                                                                                           .build() ) );
        createAdminContext().callWith( () -> dynamicSchemaService.createContentSchema( CreateDynamicContentSchemaParams.create()
                                                                                           .name( FormFragmentName.from( "myapp:mytype2" ) )
                                                                                           .resource( readResource( "_formFragment.yaml" ) )
                                                                                           .type( DynamicContentSchemaType.FORM_FRAGMENT )
                                                                                           .build() ) );
        createAdminContext().callWith( () -> dynamicSchemaService.createContentSchema( CreateDynamicContentSchemaParams.create()
                                                                                           .name( FormFragmentName.from(
                                                                                               "my_other_app:mytype" ) )
                                                                                           .resource( readResource( "_formFragment.yaml" ) )
                                                                                           .type( DynamicContentSchemaType.FORM_FRAGMENT )
                                                                                           .build() ) );

        assertThrows( ForbiddenAccessException.class, () -> ctxDefault()
            .callWith( () -> dynamicSchemaService.listContentSchemas( ListDynamicContentSchemasParams.create()
                                                                          .applicationKey( applicationKey )
                                                                          .type( DynamicContentSchemaType.FORM_FRAGMENT )
                                                                          .build() ) ) );
    }

    @Test
    void listMixinsTypes()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        List<DynamicSchemaResult<BaseSchema<?>>> results = createAdminContext().callWith( () -> dynamicSchemaService.listContentSchemas(
            ListDynamicContentSchemasParams.create().applicationKey( applicationKey ).type( DynamicContentSchemaType.MIXIN ).build() ) );

        assertTrue( results.isEmpty() );

        DynamicSchemaResult<MixinDescriptor> mixin1 = createAdminContext().callWith( () -> dynamicSchemaService.createContentSchema(
            CreateDynamicContentSchemaParams.create()
                .name( MixinName.from( "myapp:mytype1" ) )
                .resource( readResource( "_mixin.yaml" ) )
                .type( DynamicContentSchemaType.MIXIN )
                .build() ) );
        DynamicSchemaResult<MixinDescriptor> mixin2 = createAdminContext().callWith( () -> dynamicSchemaService.createContentSchema(
            CreateDynamicContentSchemaParams.create()
                .name( MixinName.from( "myapp:mytype2" ) )
                .resource( readResource( "_mixin.yaml" ) )
                .type( DynamicContentSchemaType.MIXIN )
                .build() ) );
        DynamicSchemaResult<MixinDescriptor> mixin3 = createAdminContext().callWith( () -> dynamicSchemaService.createContentSchema(
            CreateDynamicContentSchemaParams.create()
                .name( MixinName.from( "my_other_app:mytype" ) )
                .resource( readResource( "_mixin.yaml" ) )
                .type( DynamicContentSchemaType.MIXIN )
                .build() ) );

        results = createAdminContext().callWith( () -> dynamicSchemaService.listContentSchemas(
            ListDynamicContentSchemasParams.create().applicationKey( applicationKey ).type( DynamicContentSchemaType.MIXIN ).build() ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( mixin1, mixin2 ) );

        results = createAdminContext().callWith( () -> dynamicSchemaService.listContentSchemas( ListDynamicContentSchemasParams.create()
                                                                                                    .applicationKey( ApplicationKey.from(
                                                                                                        "my_other_app" ) )
                                                                                                    .type( DynamicContentSchemaType.MIXIN )
                                                                                                    .build() ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( mixin3 ) );

    }

    @Test
    void deleteContentTypeComponent()
    {
        DynamicSchemaResult<ContentType> contentType = createAdminContext().callWith( () -> dynamicSchemaService.createContentSchema(
            CreateDynamicContentSchemaParams.create()
                .name( ContentTypeName.from( "myapp:mytype" ) )
                .resource( readResource( "_contentType.yaml" ) )
                .type( DynamicContentSchemaType.CONTENT_TYPE )
                .build() ) );

        final boolean result = createAdminContext().callWith( () -> dynamicSchemaService.deleteContentSchema(
            DeleteDynamicContentSchemaParams.create()
                .name( contentType.getSchema().getName() )
                .type( DynamicContentSchemaType.CONTENT_TYPE )
                .build() ) );

        assertThat( result ).isTrue();

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.listContentSchemas( ListDynamicContentSchemasParams.create()
                                                                                                      .applicationKey(
                                                                                                          ApplicationKey.from( "myapp" ) )
                                                                                                      .type(
                                                                                                          DynamicContentSchemaType.CONTENT_TYPE )
                                                                                                      .build() ) ) ).isEmpty();

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.getContentSchema( GetDynamicContentSchemaParams.create()
                                                                                                    .type(
                                                                                                        DynamicContentSchemaType.CONTENT_TYPE )
                                                                                                    .name(
                                                                                                        contentType.getSchema().getName() )
                                                                                                    .build() ) ) ).isNull();
    }

    @Test
    void deleteContentTypeComponentAsSchemaAdmin()
    {
        DynamicSchemaResult<ContentType> contentType = createSchemaAdminContext().callWith( () -> dynamicSchemaService.createContentSchema(
            CreateDynamicContentSchemaParams.create()
                .name( ContentTypeName.from( "myapp:mytype" ) )
                .resource( readResource( "_contentType.yaml" ) )
                .type( DynamicContentSchemaType.CONTENT_TYPE )
                .build() ) );

        final boolean result = createSchemaAdminContext().callWith( () -> dynamicSchemaService.deleteContentSchema(
            DeleteDynamicContentSchemaParams.create()
                .name( contentType.getSchema().getName() )
                .type( DynamicContentSchemaType.CONTENT_TYPE )
                .build() ) );

        assertThat( result ).isTrue();
    }

    @Test
    void deleteContentTypeComponentAsNonSchemaAdmin()
    {
        DynamicSchemaResult<ContentType> contentType = createSchemaAdminContext().callWith( () -> dynamicSchemaService.createContentSchema(
            CreateDynamicContentSchemaParams.create()
                .name( ContentTypeName.from( "myapp:mytype" ) )
                .resource( readResource( "_contentType.yaml" ) )
                .type( DynamicContentSchemaType.CONTENT_TYPE )
                .build() ) );

        assertThrows( ForbiddenAccessException.class, () -> ctxDefault()
            .callWith( () -> dynamicSchemaService.deleteContentSchema( DeleteDynamicContentSchemaParams.create()
                                                                           .name( contentType.getSchema().getName() )
                                                                           .type( DynamicContentSchemaType.CONTENT_TYPE )
                                                                           .build() ) ) );

    }


    @Test
    void deletePartComponent()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        DynamicSchemaResult<PartDescriptor> part = createAdminContext().callWith( () -> dynamicSchemaService.createComponent(
            CreateDynamicComponentParams.create()
                .descriptorKey( DescriptorKey.from( "myapp:mypart" ) )
                .resource( readResource( "_part.yaml" ) )
                .type( DynamicComponentType.PART )
                .build() ) );

        final boolean result = createAdminContext().callWith( () -> dynamicSchemaService.deleteComponent(
            DeleteDynamicComponentParams.create().type( DynamicComponentType.PART ).descriptorKey( part.getSchema().getKey() ).build() ) );

        assertTrue( result );

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.listComponents(
            ListDynamicComponentsParams.create().applicationKey( applicationKey ).type( DynamicComponentType.PART ).build() ) ) ).isEmpty();

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.getComponent( GetDynamicComponentParams.create()
                                                                                                .type( DynamicComponentType.PART )
                                                                                                .descriptorKey( part.getSchema().getKey() )
                                                                                                .build() ) ) ).usingRecursiveComparison()
            .isNull();

    }

    @Test
    void createContentTypeSchemaInvalid()
    {
        final String resource = "unsupportedField: [ ]";

        CreateDynamicContentSchemaParams params = CreateDynamicContentSchemaParams.create()
            .name( ContentTypeName.from( "myapp:mytype" ) )
            .resource( resource )
            .type( DynamicContentSchemaType.CONTENT_TYPE )
            .build();

        final RuntimeException exception = assertThrows( RuntimeException.class, () -> createAdminContext().callWith(
            () -> dynamicSchemaService.createContentSchema( params ) ) );

        assertEquals( "Could not parse dynamic content type [myapp:mytype]", exception.getMessage() );
    }

    @Test
    public void createFormFragmentSchemaInvalid()
    {
        final String resource = """
            kind: "FormFragment"
            unsupportedField: [ ]
            """;

        CreateDynamicContentSchemaParams params = CreateDynamicContentSchemaParams.create()
            .name( FormFragmentName.from( "myapp:mytype" ) )
            .resource( resource )
            .type( DynamicContentSchemaType.FORM_FRAGMENT )
            .build();

        assertThrows( UncheckedIOException.class,
                      () -> createAdminContext().callWith( () -> dynamicSchemaService.createContentSchema( params ) ) );
    }

    @Test
    void createMixinSchemaInvalid()
    {
        final String resource = """
            kind: "Mixin"
            unsupportedField: [ ]
            """;

        CreateDynamicContentSchemaParams params = CreateDynamicContentSchemaParams.create()
            .name( MixinName.from( "myapp:mytype" ) )
            .resource( resource )
            .type( DynamicContentSchemaType.MIXIN )
            .build();

        assertThrows( UncheckedIOException.class,
                      () -> createAdminContext().callWith( () -> dynamicSchemaService.createContentSchema( params ) ) );
    }

    @Test
    void createPartInvalid()
    {
        final String resource = """
            kind: "Part"        
            unsupportedField: [ ]
            """;

        final CreateDynamicComponentParams params = CreateDynamicComponentParams.create()
            .descriptorKey( DescriptorKey.from( "myapp:mytype" ) )
            .resource( resource )
            .type( DynamicComponentType.PART )
            .build();

        assertThrows( UncheckedIOException.class,
                      () -> createAdminContext().callWith( () -> dynamicSchemaService.createComponent( params ) ) );
    }

    @Test
    void createLayoutInvalid()
    {
        final String resource = """
            kind: "Layout"
            unsupportedField: [ ]
            """;

        final CreateDynamicComponentParams params = CreateDynamicComponentParams.create()
            .descriptorKey( DescriptorKey.from( "myapp:mytype" ) )
            .resource( resource )
            .type( DynamicComponentType.LAYOUT )
            .build();

        assertThrows( UncheckedIOException.class,
                      () -> createAdminContext().callWith( () -> dynamicSchemaService.createComponent( params ) ) );
    }

    @Test
    void createPageInvalid()
    {
        final String resource = """
            kind: "Page"
            unsupportedField: [ ]
            """;

        final CreateDynamicComponentParams params = CreateDynamicComponentParams.create()
            .descriptorKey( DescriptorKey.from( "myapp:mytype" ) )
            .resource( resource )
            .type( DynamicComponentType.PAGE )
            .build();

        assertThrows( UncheckedIOException.class,
                      () -> createAdminContext().callWith( () -> dynamicSchemaService.createComponent( params ) ) );
    }

    @Test
    void createStylesInvalid()
    {
        final String resource = """
            kind: "Style"
            unsupportedField: [ ]
            """;

        final CreateDynamicStylesParams params =
            CreateDynamicStylesParams.create().key( ApplicationKey.from( "myapp" ) ).resource( resource ).build();

        assertThrows( Exception.class, () -> createAdminContext().callWith( () -> dynamicSchemaService.createStyles( params ) ) );
    }

    @Test
    void createSiteInvalid()
    {
        final String resource = """
            kind: "CMS"
            unsupportedField: [ ]
            """;

        final CreateDynamicCmsParams params =
            CreateDynamicCmsParams.create().key( ApplicationKey.from( "myapp" ) ).resource( resource ).build();

        assertThrows( Exception.class, () -> createAdminContext().callWith( () -> dynamicSchemaService.createCms( params ) ) );
    }


    private String readResource( final String suffix )
        throws Exception
    {
        final String name = getClass().getSimpleName() + suffix;
        final URL url = getClass().getResource( name );

        if ( url == null )
        {
            throw new IllegalArgumentException( "Could not find resource [" + name + "]" );
        }

        return new String( url.openStream().readAllBytes(), StandardCharsets.UTF_8 );
    }
}
