package com.enonic.xp.core.dynamic;

import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
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

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationRepoConstants;
import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.impl.app.ApplicationGeneration;
import com.enonic.xp.core.impl.app.ApplicationHelper;
import com.enonic.xp.core.impl.app.ApplicationRepoInitializer;
import com.enonic.xp.core.impl.app.DynamicSchemaAuditLogSupportImpl;
import com.enonic.xp.core.impl.app.DynamicSchemaServiceImpl;
import com.enonic.xp.core.impl.app.SchemaConfig;
import com.enonic.xp.core.impl.app.event.ApplicationEvents;
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
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.exception.ForbiddenAccessException;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.icon.IconUpdate;
import com.enonic.xp.internal.blobstore.MemoryBlobStore;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.itest.AbstractElasticsearchIntegrationTest;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.project.CreateProjectParams;
import com.enonic.xp.project.ProjectName;
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
import com.enonic.xp.resource.CreateOrUpdateCmsParams;
import com.enonic.xp.resource.CreateOrUpdateComponentParams;
import com.enonic.xp.resource.CreateOrUpdateContentSchemaParams;
import com.enonic.xp.resource.CreateOrUpdateMacroParams;
import com.enonic.xp.resource.CreateOrUpdatePhrasesParams;
import com.enonic.xp.resource.CreateOrUpdateStylesParams;
import com.enonic.xp.resource.DeleteDynamicComponentParams;
import com.enonic.xp.resource.DeleteDynamicContentSchemaParams;
import com.enonic.xp.resource.DeleteDynamicPhrasesParams;
import com.enonic.xp.resource.DynamicComponentType;
import com.enonic.xp.resource.DynamicContentSchemaType;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.GetDynamicComponentParams;
import com.enonic.xp.resource.GetDynamicContentSchemaParams;
import com.enonic.xp.resource.GetDynamicPhrasesParams;
import com.enonic.xp.resource.ListDynamicComponentsParams;
import com.enonic.xp.resource.ListDynamicContentSchemasParams;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.UpdateDynamicCmsParams;
import com.enonic.xp.resource.UpdateDynamicComponentParams;
import com.enonic.xp.resource.UpdateDynamicContentSchemaParams;
import com.enonic.xp.resource.UpdateDynamicStylesParams;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.formfragment.FormFragmentDescriptor;
import com.enonic.xp.schema.formfragment.FormFragmentName;
import com.enonic.xp.schema.mixin.MixinDescriptor;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.site.CmsDescriptor;
import com.enonic.xp.style.StyleDescriptor;
import com.enonic.xp.util.BinaryReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
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

    // the application events published by the service: one per change, see ApplicationEvents.updated
    private final List<Event> applicationEvents = new ArrayList<>();

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

    // admin context of the application repository, where the schema of an application is persisted below /<app>/cms
    private static Context appRepoAdminContext()
    {
        return ContextBuilder.copyOf( createAdminContext() )
            .repositoryId( ApplicationRepoConstants.APP_REPO_ID )
            .branch( ApplicationRepoConstants.APP_REPO_BRANCH )
            .build();
    }

    // an application owning a schema: its node, as created on install or by the application descriptor service, and the cms
    // descriptor below it, as created through the schema service. The schemas are written as /<app>/cms/<kind>:<name>,
    // the folders on the way are created as the schema service goes.
    private void createApplicationNodes( final ApplicationKey applicationKey )
    {
        createApplicationNode( applicationKey );
        createAdminContext().runWith( () -> dynamicSchemaService.createOrUpdateCms(
            CreateOrUpdateCmsParams.create().key( applicationKey ).resource( CMS_DESCRIPTOR_DEFAULT_VALUE ).build() ) );
    }

    // the node of an application created through the application descriptor service: nothing below it
    private void createApplicationNode( final ApplicationKey applicationKey )
    {
        ApplicationHelper.runAsAdmin( () -> {
            createFolderNode( NodePath.ROOT, applicationKey.toString() );
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

        ApplicationRepoInitializer.create().setIndexService( indexService ).setRepositoryService( repositoryService ).build().initialize();

        final SchemaConfig schemaConfig = mock( SchemaConfig.class, withSettings().stubOnly()
            .defaultAnswer( invocationOnMock -> invocationOnMock.getMethod().getDefaultValue() ) );

        final DynamicSchemaAuditLogSupportImpl schemaAuditLogSupport = new DynamicSchemaAuditLogSupportImpl( mock( AuditLogService.class ) );
        schemaAuditLogSupport.activate( schemaConfig );

        this.applicationEvents.clear();
        final EventPublisher applicationEventPublisher = mock( EventPublisher.class );
        final ApplicationGeneration applicationGeneration = new ApplicationGeneration();
        doAnswer( invocation -> applicationEvents.add( invocation.getArgument( 0 ) ) ).when( applicationEventPublisher ).publish( any() );

        this.dynamicSchemaService = new DynamicSchemaServiceImpl( nodeService, schemaAuditLogSupport, applicationEventPublisher, applicationGeneration );

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

        CreateOrUpdateContentSchemaParams params = CreateOrUpdateContentSchemaParams.create()
            .name( ContentTypeName.from( "myapp:mytype" ) )
            .resource( resource )
            .build();

        final DynamicSchemaResult<ContentType> result =
            createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateContentType( params ) );

        final ContentType contentType = (ContentType) result.getSchema();

        createAdminContext().runWith( () -> assertThat( contentType ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getContentType( ContentTypeName.from( "myapp:mytype" ) ).getSchema() ) );

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
        assertEquals( "myapp:/cms/content-types/mytype.yaml", result.getResource().getKey().toString() );
        assertTrue( result.getResource().getSize() > 0 );

        final Node resourceNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/contenttype:mytype" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void updateContentTypeSchema()
        throws Exception
    {
        final CreateOrUpdateContentSchemaParams createParams = CreateOrUpdateContentSchemaParams.create()
            .name( ContentTypeName.from( "myapp:mytype" ) ).resource( """
                                                                          kind: "ContentType"
                                                                          superType: "base:unstructured"
                                                                          """ )
            .build();

        createAdminContext().runWith( () -> dynamicSchemaService.createOrUpdateContentType( createParams ) );

        final String resource = readResource( "_contentType.yaml" );

        final CreateOrUpdateContentSchemaParams updateParams = CreateOrUpdateContentSchemaParams.create()
            .name( ContentTypeName.from( "myapp:mytype" ) )
            .resource( resource )
            .build();

        final DynamicSchemaResult<ContentType> result =
            createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateContentType( updateParams ) );

        final ContentType contentType = (ContentType) result.getSchema();

        createAdminContext().runWith( () -> assertThat( contentType ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getContentType( ContentTypeName.from( "myapp:mytype" ) ).getSchema() ) );

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
        assertEquals( "myapp:/cms/content-types/mytype.yaml", result.getResource().getKey().toString() );

        final Node resourceNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/contenttype:mytype" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void createFormFragmentSchema()
        throws Exception
    {
        final String resource = readResource( "_formFragment.yaml" );

        CreateOrUpdateContentSchemaParams params = CreateOrUpdateContentSchemaParams.create()
            .name( FormFragmentName.from( "myapp:my-fragment" ) )
            .resource( resource )
            .build();

        final DynamicSchemaResult<FormFragmentDescriptor> result =
            createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateFormFragment( params ) );

        final FormFragmentDescriptor fragment = (FormFragmentDescriptor) result.getSchema();

        createAdminContext().runWith( () -> assertThat( fragment ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getFormFragment( FormFragmentName.from( "myapp:my-fragment" ) ).getSchema() ) );

        assertEquals( "myapp:my-fragment", fragment.getName().toString() );
        assertEquals( "Virtual FormFragment", fragment.getTitle() );
        assertEquals( "FormFragment description", fragment.getDescription() );
        assertEquals( 2, fragment.getForm().size() );
        assertEquals( "myapp:inline", fragment.getForm().getFormFragment( "inline" ).getFormFragmentName().toString() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/form-fragments/my-fragment.yaml", result.getResource().getKey().toString() );

        final Node resourceNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/formfragment:my-fragment" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void createFormFragmentSchemaAsDevSchemaAdmin()
        throws Exception
    {
        final String resource = readResource( "_formFragment.yaml" );

        CreateOrUpdateContentSchemaParams params = CreateOrUpdateContentSchemaParams.create()
            .name( FormFragmentName.from( "myapp:my-fragment" ) )
            .resource( resource )
            .build();

        final DynamicSchemaResult<FormFragmentDescriptor> result =
            createSchemaAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateFormFragment( params ) );

        assertNotNull( result.getResource() );
    }

    @Test
    void createFormFragmentSchemaAsNonSchemaAdmin()
        throws Exception
    {
        final String resource = readResource( "_formFragment.yaml" );

        CreateOrUpdateContentSchemaParams params = CreateOrUpdateContentSchemaParams.create()
            .name( FormFragmentName.from( "myapp:my-fragment" ) )
            .resource( resource )
            .build();

        assertThrows( ForbiddenAccessException.class,
                      () -> ctxDefault().callWith( () -> dynamicSchemaService.createOrUpdateFormFragment( params ) ) );
    }

    @Test
    void updateFormFragmentSchema()
        throws Exception
    {

        final CreateOrUpdateContentSchemaParams createParams =
            CreateOrUpdateContentSchemaParams.create()
                .name( FormFragmentName.from( "myapp:my-fragment" ) )
                .resource( """
                               kind: "FormFragment"
                               title: "MyFormFragment"
                               """ )
                .build();

        createAdminContext().runWith( () -> dynamicSchemaService.createOrUpdateFormFragment( createParams ) );

        final String resource = readResource( "_formFragment.yaml" );

        final CreateOrUpdateContentSchemaParams updateParams = CreateOrUpdateContentSchemaParams.create()
            .name( FormFragmentName.from( "myapp:my-fragment" ) )
            .resource( resource )
            .build();

        final DynamicSchemaResult<FormFragmentDescriptor> result =
            createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateFormFragment( updateParams ) );

        final FormFragmentDescriptor fragment = (FormFragmentDescriptor) result.getSchema();

        createAdminContext().runWith( () -> assertThat( fragment ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getFormFragment( FormFragmentName.from( "myapp:my-fragment" ) ).getSchema() ) );

        assertEquals( "myapp:my-fragment", fragment.getName().toString() );
        assertEquals( "Virtual FormFragment", fragment.getTitle() );
        assertEquals( "FormFragment description", fragment.getDescription() );
        assertEquals( 2, fragment.getForm().size() );
        assertEquals( "myapp:inline", fragment.getForm().getFormFragment( "inline" ).getFormFragmentName().toString() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/form-fragments/my-fragment.yaml", result.getResource().getKey().toString() );

        final Node resourceNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/formfragment:my-fragment" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void updateFormFragmentSchemaAsSchemaAdmin()
        throws Exception
    {

        final CreateOrUpdateContentSchemaParams createParams =
            CreateOrUpdateContentSchemaParams.create()
                .name( FormFragmentName.from( "myapp:my-fragment" ) )
                .resource( """
                               kind: "FormFragment"
                               title: "FormFragment"
                               """ )
                .build();

        createSchemaAdminContext().runWith( () -> dynamicSchemaService.createOrUpdateFormFragment( createParams ) );

        final String resource = readResource( "_formFragment.yaml" );

        final CreateOrUpdateContentSchemaParams updateParams = CreateOrUpdateContentSchemaParams.create()
            .name( FormFragmentName.from( "myapp:my-fragment" ) )
            .resource( resource )
            .build();

        final DynamicSchemaResult<FormFragmentDescriptor> result =
            createSchemaAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateFormFragment( updateParams ) );

        assertNotNull( result.getResource() );
    }

    @Test
    void updateFormFragmentSchemaAsNonSchemaAdmin()
        throws Exception
    {
        final CreateOrUpdateContentSchemaParams createParams =
            CreateOrUpdateContentSchemaParams.create()
                .name( FormFragmentName.from( "myapp:my-fragment" ) )
                .resource( """
                               kind: "FormFragment"
                               title: "FormFragment"
                               """ )
                .build();

        createSchemaAdminContext().runWith( () -> dynamicSchemaService.createOrUpdateFormFragment( createParams ) );

        final String resource = readResource( "_formFragment.yaml" );

        final CreateOrUpdateContentSchemaParams updateParams = CreateOrUpdateContentSchemaParams.create()
            .name( FormFragmentName.from( "myapp:my-fragment" ) )
            .resource( resource )
            .build();

        assertThrows( ForbiddenAccessException.class,
                      () -> ctxDefault().callWith( () -> dynamicSchemaService.createOrUpdateFormFragment( updateParams ) ) );
    }


    @Test
    void createMixinSchema()
        throws Exception
    {
        final String resource = readResource( "_mixin.yaml" );

        CreateOrUpdateContentSchemaParams params = CreateOrUpdateContentSchemaParams.create()
            .name( MixinName.from( "myapp:mymixin" ) )
            .resource( resource )
            .build();

        final DynamicSchemaResult<MixinDescriptor> result =
            createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateMixin( params ) );

        final MixinDescriptor mixinDescriptor = (MixinDescriptor) result.getSchema();

        createAdminContext().runWith( () -> assertThat( mixinDescriptor ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getMixin( MixinName.from( "myapp:mymixin" ) ).getSchema() ) );

        assertEquals( "myapp:mymixin", mixinDescriptor.getName().toString() );
        assertEquals( "Virtual Mixin", mixinDescriptor.getTitle() );
        assertEquals( "Mixin description", mixinDescriptor.getDescription() );
        assertEquals( 1, mixinDescriptor.getForm().size() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/mixins/mymixin.yaml", result.getResource().getKey().toString() );

        final Node resourceNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/mixin:mymixin" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void updateMixinSchema()
        throws Exception
    {
        final CreateOrUpdateContentSchemaParams createParams =
            CreateOrUpdateContentSchemaParams.create()
                .name( MixinName.from( "myapp:mymixin" ) )
                .resource( """
                               kind: "Mixin"
                               title: "Virtual MIXIN"
                               form: [ ]
                               """ )
                .build();

        createAdminContext().runWith( () -> dynamicSchemaService.createOrUpdateMixin( createParams ) );

        final String resource = readResource( "_mixin.yaml" );

        final CreateOrUpdateContentSchemaParams updateParams = CreateOrUpdateContentSchemaParams.create()
            .name( MixinName.from( "myapp:mymixin" ) )
            .resource( resource )
            .build();

        final DynamicSchemaResult<MixinDescriptor> result =
            createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateMixin( updateParams ) );

        final MixinDescriptor mixinDescriptor = (MixinDescriptor) result.getSchema();

        createAdminContext().runWith( () -> assertThat( mixinDescriptor ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getMixin( MixinName.from( "myapp:mymixin" ) ).getSchema() ) );

        assertEquals( "myapp:mymixin", mixinDescriptor.getName().toString() );
        assertEquals( "Virtual Mixin", mixinDescriptor.getTitle() );
        assertEquals( "Mixin description", mixinDescriptor.getDescription() );
        assertEquals( 1, mixinDescriptor.getForm().size() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/mixins/mymixin.yaml", result.getResource().getKey().toString() );

        final Node resourceNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/mixin:mymixin" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void createPartComponent()
        throws Exception
    {
        final String resource = readResource( "_part.yaml" );

        CreateOrUpdateComponentParams params = CreateOrUpdateComponentParams.create()
            .key( DescriptorKey.from( "myapp:mypart" ) )
            .resource( resource )
            .build();

        final DynamicSchemaResult<PartDescriptor> result =
            createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdatePart( params ) );

        final PartDescriptor partDescriptor = (PartDescriptor) result.getSchema();

        createAdminContext().runWith( () -> assertThat( partDescriptor ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getPart( DescriptorKey.from( "myapp:mypart" ) ).getSchema() ) );

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
        assertEquals( "myapp:/cms/parts/mypart.yaml", result.getResource().getKey().toString() );

        final Node resourceNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/part:mypart" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void updatePartComponent()
        throws Exception
    {

        final CreateOrUpdateComponentParams createParams =
            CreateOrUpdateComponentParams.create()
                .key( DescriptorKey.from( "myapp:mypart" ) )
                .resource( """
                               kind: "Part"
                               title: "MyPart"
                               form: [ ]
                               """ )
                .build();

        createAdminContext().runWith( () -> dynamicSchemaService.createOrUpdatePart( createParams ) );

        final String resource = readResource( "_part.yaml" );

        final CreateOrUpdateComponentParams updateParams = CreateOrUpdateComponentParams.create()
            .key( DescriptorKey.from( "myapp:mypart" ) )
            .resource( resource )
            .build();

        final DynamicSchemaResult<PartDescriptor> result =
            createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdatePart( updateParams ) );

        final PartDescriptor partDescriptor = (PartDescriptor) result.getSchema();

        createAdminContext().runWith( () -> assertThat( partDescriptor ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getPart( DescriptorKey.from( "myapp:mypart" ) ).getSchema() ) );

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
        assertEquals( "myapp:/cms/parts/mypart.yaml", result.getResource().getKey().toString() );

        final Node resourceNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/part:mypart" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void createLayoutComponent()
        throws Exception
    {
        final String resource = readResource( "_layout.yaml" );

        CreateOrUpdateComponentParams params = CreateOrUpdateComponentParams.create()
            .key( DescriptorKey.from( "myapp:mylayout" ) )
            .resource( resource )
            .build();

        final DynamicSchemaResult<LayoutDescriptor> result =
            createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateLayout( params ) );

        final LayoutDescriptor layoutDescriptor = (LayoutDescriptor) result.getSchema();

        createAdminContext().runWith( () -> assertThat( layoutDescriptor ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getLayout( DescriptorKey.from( "myapp:mylayout" ) ).getSchema() ) );

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
        assertEquals( "myapp:/cms/layouts/mylayout.yaml", result.getResource().getKey().toString() );

        final Node resourceNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/layout:mylayout" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void updateLayoutComponent()
        throws Exception
    {
        final CreateOrUpdateComponentParams params =
            CreateOrUpdateComponentParams.create()
                .key( DescriptorKey.from( "myapp:mylayout" ) )
                .resource( """
                               kind: "Layout"
                               title: MyLayout
                               regions: [ ]
                               
                               """ )
                .build();

        createAdminContext().runWith( () -> dynamicSchemaService.createOrUpdateLayout( params ) );

        final String resource = readResource( "_layout.yaml" );

        final CreateOrUpdateComponentParams updateParams = CreateOrUpdateComponentParams.create()
            .key( DescriptorKey.from( "myapp:mylayout" ) )
            .resource( resource )
            .build();

        final DynamicSchemaResult<LayoutDescriptor> result =
            createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateLayout( updateParams ) );

        final LayoutDescriptor layoutDescriptor = (LayoutDescriptor) result.getSchema();

        createAdminContext().runWith( () -> assertThat( layoutDescriptor ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getLayout( DescriptorKey.from( "myapp:mylayout" ) ).getSchema() ) );

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
        assertEquals( "myapp:/cms/layouts/mylayout.yaml", result.getResource().getKey().toString() );

        final Node resourceNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/layout:mylayout" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void createPageComponent()
        throws Exception
    {
        final String resource = readResource( "_page.yaml" );

        CreateOrUpdateComponentParams params = CreateOrUpdateComponentParams.create()
            .key( DescriptorKey.from( "myapp:mypage" ) )
            .resource( resource )
            .build();

        final DynamicSchemaResult<PageDescriptor> result =
            createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdatePage( params ) );

        final PageDescriptor pageDescriptor = (PageDescriptor) result.getSchema();

        createAdminContext().runWith( () -> assertThat( pageDescriptor ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getPage( DescriptorKey.from( "myapp:mypage" ) ).getSchema() ) );

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
        assertEquals( "myapp:/cms/pages/mypage.yaml", result.getResource().getKey().toString() );

        final Node resourceNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/page:mypage" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void updatePageComponent()
        throws Exception
    {
        final CreateOrUpdateComponentParams createParams =
            CreateOrUpdateComponentParams.create()
                .key( DescriptorKey.from( "myapp:mypage" ) )
                .resource( """
                               kind: "Page"
                               title: "MyPage"
                               regions:
                                 - "main"
                               """ )
                .build();

        createAdminContext().runWith( () -> dynamicSchemaService.createOrUpdatePage( createParams ) );

        final String resource = readResource( "_page.yaml" );

        final CreateOrUpdateComponentParams updateParams = CreateOrUpdateComponentParams.create()
            .key( DescriptorKey.from( "myapp:mypage" ) )
            .resource( resource )
            .build();

        final DynamicSchemaResult<PageDescriptor> result =
            createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdatePage( updateParams ) );

        final PageDescriptor pageDescriptor = (PageDescriptor) result.getSchema();

        createAdminContext().runWith( () -> assertThat( pageDescriptor ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getPage( DescriptorKey.from( "myapp:mypage" ) ).getSchema() ) );

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
        assertEquals( "myapp:/cms/pages/mypage.yaml", result.getResource().getKey().toString() );

        final Node resourceNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/page:mypage" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void createCms()
        throws Exception
    {
        // an application created through the application descriptor service has its application node and nothing below it
        final ApplicationKey applicationKey = ApplicationKey.from( "nodeonly" );
        createApplicationNode( applicationKey );
        final String resource = readResource( "_cms.yaml" );

        // no cms descriptor until one is created
        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.getCmsDescriptor( applicationKey ) ) ).isNull();

        final DynamicSchemaResult<CmsDescriptor> result = createAdminContext().callWith(
            () -> dynamicSchemaService.createOrUpdateCms( CreateOrUpdateCmsParams.create().key( applicationKey ).resource( resource ).build() ) );

        final CmsDescriptor cmsDescriptor = result.getSchema();

        createAdminContext().runWith( () -> assertThat( cmsDescriptor ).usingRecursiveComparison(
                RecursiveComparisonConfiguration.builder().withIgnoredFields( "mappingDescriptors" ).build() )
            .isEqualTo( dynamicSchemaService.getCmsDescriptor( applicationKey ).getSchema() ) );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "nodeonly:/cms/cms.yaml", result.getResource().getKey().toString() );
        assertNotNull( cmsDescriptor.getModifiedTime() );

        // the cms folder is created with the descriptor
        assertTrue( appRepoAdminContext().callWith( () -> nodeService.nodeExists( new NodePath( "/nodeonly/cms" ) ) ) );
        final Node resourceNode =
            appRepoAdminContext().callWith( () -> nodeService.getByPath( new NodePath( "/nodeonly/cms/cms" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void createCmsUnknownApplication()
        throws Exception
    {
        // the application node itself is never created by the schema service
        final String resource = readResource( "_cms.yaml" );
        final CreateOrUpdateCmsParams params = CreateOrUpdateCmsParams.create().key( ApplicationKey.from( "unknownapp" ) ).resource( resource ).build();

        final IllegalArgumentException exception = assertThrows( IllegalArgumentException.class, () -> createAdminContext().callWith(
            () -> dynamicSchemaService.createOrUpdateCms( params ) ) );
        assertEquals( "Application [unknownapp] is not installed", exception.getMessage() );

        assertFalse( appRepoAdminContext().callWith( () -> nodeService.nodeExists( new NodePath( "/unknownapp" ) ) ) );
    }

    @Test
    void createSchemaWithoutCmsDescriptor()
        throws Exception
    {
        // a schema is written for an application owning a schema only: the cms descriptor comes first
        final ApplicationKey applicationKey = ApplicationKey.from( "nodeonly" );
        createApplicationNode( applicationKey );

        final CreateOrUpdateContentSchemaParams contentTypeParams = CreateOrUpdateContentSchemaParams.create()
            .name( ContentTypeName.from( "nodeonly:mytype" ) )
            .resource( readResource( "_contentType.yaml" ) )
            .build();
        final CreateOrUpdateComponentParams partParams = CreateOrUpdateComponentParams.create()
            .key( DescriptorKey.from( "nodeonly:mypart" ) )
            .resource( readResource( "_part.yaml" ) )
            .build();
        final CreateOrUpdateStylesParams stylesParams =
            CreateOrUpdateStylesParams.create().key( applicationKey ).resource( readResource( "_styles.yaml" ) ).build();

        for ( final Runnable write : List.<Runnable>of( () -> dynamicSchemaService.createOrUpdateContentType( contentTypeParams ),
                                                        () -> dynamicSchemaService.createOrUpdatePart( partParams ),
                                                        () -> dynamicSchemaService.createOrUpdateStyles( stylesParams ) ) )
        {
            final IllegalArgumentException exception =
                assertThrows( IllegalArgumentException.class, () -> createAdminContext().runWith( write ) );
            assertEquals( "Application [nodeonly] has no CMS descriptor", exception.getMessage() );
        }

        // nothing is written on the way, the cms folder included
        assertFalse( appRepoAdminContext().callWith( () -> nodeService.nodeExists( new NodePath( "/nodeonly/cms" ) ) ) );

        // an unknown application is told apart
        final IllegalArgumentException exception = assertThrows( IllegalArgumentException.class, () -> createAdminContext().runWith(
            () -> dynamicSchemaService.createOrUpdateStyles(
                CreateOrUpdateStylesParams.create().key( ApplicationKey.from( "unknownapp" ) ).resource( "kind: \"Style\"\n" ).build() ) ) );
        assertEquals( "Application [unknownapp] is not installed", exception.getMessage() );
    }

    @Test
    void deprecatedGenericComponentApi()
        throws Exception
    {
        // the 8.1 generic methods dispatch by the type of the params to the method of the kind
        final String resource = readResource( "_part.yaml" );
        final DescriptorKey key = DescriptorKey.from( "myapp:mypart" );

        final DynamicSchemaResult<PartDescriptor> created = createAdminContext().callWith(
            () -> dynamicSchemaService.<PartDescriptor>createComponent(
                CreateDynamicComponentParams.create().descriptorKey( key ).type( DynamicComponentType.PART ).resource( resource ).build() ) );
        assertEquals( key, created.getSchema().getKey() );
        assertEquals( "myapp:/cms/parts/mypart.yaml", created.getResource().getKey().toString() );

        final DynamicSchemaResult<PartDescriptor> fetched = createAdminContext().callWith(
            () -> dynamicSchemaService.<PartDescriptor>getComponent(
                GetDynamicComponentParams.create().descriptorKey( key ).type( DynamicComponentType.PART ).build() ) );
        assertEquals( resource, fetched.getResource().readString() );
        // the kind is told by the type: there is no page of that key
        assertNull( createAdminContext().callWith( () -> dynamicSchemaService.getComponent(
            GetDynamicComponentParams.create().descriptorKey( key ).type( DynamicComponentType.PAGE ).build() ) ) );

        final List<DynamicSchemaResult<PartDescriptor>> listed = createAdminContext().callWith(
            () -> dynamicSchemaService.<PartDescriptor>listComponents( ListDynamicComponentsParams.create()
                                                                            .applicationKey( ApplicationKey.from( "myapp" ) )
                                                                            .type( DynamicComponentType.PART )
                                                                            .build() ) );
        assertThat( listed ).hasSize( 1 );

        final DynamicSchemaResult<PartDescriptor> updated = createAdminContext().callWith(
            () -> dynamicSchemaService.<PartDescriptor>updateComponent(
                UpdateDynamicComponentParams.create().descriptorKey( key ).type( DynamicComponentType.PART ).resource( resource ).build() ) );
        assertEquals( "myapp:/cms/parts/mypart.yaml", updated.getResource().getKey().toString() );

        assertTrue( createAdminContext().callWith( () -> dynamicSchemaService.deleteComponent(
            DeleteDynamicComponentParams.create().descriptorKey( key ).type( DynamicComponentType.PART ).build() ) ) );
        assertNull( createAdminContext().callWith( () -> dynamicSchemaService.getPart( key ) ) );
    }

    @Test
    void deprecatedGenericContentSchemaApi()
        throws Exception
    {
        final String resource = readResource( "_contentType.yaml" );
        final ContentTypeName name = ContentTypeName.from( "myapp:mytype" );

        final DynamicSchemaResult<ContentType> created = createAdminContext().callWith(
            () -> dynamicSchemaService.<ContentType>createContentSchema( CreateDynamicContentSchemaParams.create()
                                                                              .name( name )
                                                                              .type( DynamicContentSchemaType.CONTENT_TYPE )
                                                                              .resource( resource )
                                                                              .build() ) );
        assertEquals( name, created.getSchema().getName() );
        assertEquals( "myapp:/cms/content-types/mytype.yaml", created.getResource().getKey().toString() );

        final DynamicSchemaResult<ContentType> fetched = createAdminContext().callWith(
            () -> dynamicSchemaService.<ContentType>getContentSchema(
                GetDynamicContentSchemaParams.create().name( name ).type( DynamicContentSchemaType.CONTENT_TYPE ).build() ) );
        assertEquals( resource, fetched.getResource().readString() );

        // the name must be of the kind the type tells
        assertThrows( IllegalArgumentException.class, () -> createAdminContext().callWith( () -> dynamicSchemaService.getContentSchema(
            GetDynamicContentSchemaParams.create().name( name ).type( DynamicContentSchemaType.MIXIN ).build() ) ) );

        final List<DynamicSchemaResult<ContentType>> listed = createAdminContext().callWith(
            () -> dynamicSchemaService.<ContentType>listContentSchemas( ListDynamicContentSchemasParams.create()
                                                                             .applicationKey( ApplicationKey.from( "myapp" ) )
                                                                             .type( DynamicContentSchemaType.CONTENT_TYPE )
                                                                             .build() ) );
        assertThat( listed ).hasSize( 1 );

        final DynamicSchemaResult<ContentType> updated = createAdminContext().callWith(
            () -> dynamicSchemaService.<ContentType>updateContentSchema( UpdateDynamicContentSchemaParams.create()
                                                                              .name( name )
                                                                              .type( DynamicContentSchemaType.CONTENT_TYPE )
                                                                              .resource( resource )
                                                                              .build() ) );
        assertEquals( "myapp:/cms/content-types/mytype.yaml", updated.getResource().getKey().toString() );

        assertTrue( createAdminContext().callWith( () -> dynamicSchemaService.deleteContentSchema(
            DeleteDynamicContentSchemaParams.create().name( name ).type( DynamicContentSchemaType.CONTENT_TYPE ).build() ) ) );
        assertNull( createAdminContext().callWith( () -> dynamicSchemaService.getContentType( name ) ) );
    }

    @Test
    void deprecatedGenericApiRequiresType()
        throws Exception
    {
        // the generic methods cannot do without the type: their params reject a missing one up front
        final String part = readResource( "_part.yaml" );
        final String contentType = readResource( "_contentType.yaml" );

        final NullPointerException component = assertThrows( NullPointerException.class, () -> CreateDynamicComponentParams.create()
            .descriptorKey( DescriptorKey.from( "myapp:mypart" ) )
            .resource( part )
            .build() );
        assertEquals( "type is required", component.getMessage() );

        final NullPointerException schema = assertThrows( NullPointerException.class, () -> UpdateDynamicContentSchemaParams.create()
            .name( ContentTypeName.from( "myapp:mytype" ) )
            .resource( contentType )
            .build() );
        assertEquals( "type is required", schema.getMessage() );
    }

    // the deprecated create and update of the styles both create or update, like the generic methods
    @Test
    void deprecatedStylesApi()
        throws Exception
    {
        final String resource = readResource( "_styles.yaml" );
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        final DynamicSchemaResult<StyleDescriptor> updatedFirst = createAdminContext().callWith( () -> dynamicSchemaService.updateStyles(
            UpdateDynamicStylesParams.create().key( applicationKey ).resource( resource ).build() ) );
        assertEquals( resource, updatedFirst.getResource().readString() );

        final DynamicSchemaResult<StyleDescriptor> createdSecond = createAdminContext().callWith( () -> dynamicSchemaService.createStyles(
            CreateDynamicStylesParams.create().key( applicationKey ).resource( resource ).build() ) );
        assertEquals( resource, createdSecond.getResource().readString() );
        assertEquals( updatedFirst.getResource().getKey(), createdSecond.getResource().getKey() );
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
            appRepoAdminContext().callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/cms" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void updateNotCreatedSite()
        throws Exception
    {
        // the deprecated updateCms creates the descriptor like createOrUpdateCms does, as it always did
        final ApplicationKey applicationKey = ApplicationKey.from( "nodeonly" );
        createApplicationNode( applicationKey );
        final String resource = readResource( "_cms.yaml" );

        final DynamicSchemaResult<CmsDescriptor> result = createAdminContext().callWith(
            () -> dynamicSchemaService.updateCms( UpdateDynamicCmsParams.create().key( applicationKey ).resource( resource ).build() ) );

        assertEquals( "nodeonly:/cms/cms.yaml", result.getResource().getKey().toString() );
        assertEquals( resource, result.getResource().readString() );
        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.getCmsDescriptor( applicationKey ) ) ).isNotNull();
        assertTrue( appRepoAdminContext().callWith( () -> nodeService.nodeExists( new NodePath( "/nodeonly/cms" ) ) ) );
    }

    @Test
    void deleteCms()
        throws Exception
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );
        final String contentType = readResource( "_contentType.yaml" );
        final String styles = readResource( "_styles.yaml" );

        // the application owns a schema: the cms descriptor and the schemas below cms
        createAdminContext().runWith( () -> {
            dynamicSchemaService.createOrUpdateContentType(
                CreateOrUpdateContentSchemaParams.create().name( ContentTypeName.from( "myapp:mytype" ) ).resource( contentType ).build() );
            dynamicSchemaService.createOrUpdateStyles( CreateOrUpdateStylesParams.create().key( applicationKey ).resource( styles ).build() );
        } );
        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.getCmsDescriptor( applicationKey ) ) ).isNotNull();
        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.listContentTypes( applicationKey ) ) ).hasSize( 1 );

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.deleteCms( applicationKey ) ) ).isTrue();

        // the whole persisted schema is gone with the descriptor, nothing is synthesized in its place
        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.getCmsDescriptor( applicationKey ) ) ).isNull();
        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.getStyles( applicationKey ) ) ).isNull();
        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.listContentTypes( applicationKey ) ) ).isEmpty();
        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.getContentType( ContentTypeName.from( "myapp:mytype" ) ) ) )
            .isNull();
        assertFalse( appRepoAdminContext().callWith( () -> nodeService.nodeExists( new NodePath( "/myapp/cms" ) ) ) );
        assertTrue( appRepoAdminContext().callWith( () -> nodeService.nodeExists( new NodePath( "/myapp" ) ) ) );

        // nothing left to delete, and no schema can be written until the descriptor is created again
        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.deleteCms( applicationKey ) ) ).isFalse();
        assertThrows( IllegalArgumentException.class, () -> createAdminContext().runWith( () -> dynamicSchemaService.createOrUpdateStyles(
            CreateOrUpdateStylesParams.create().key( applicationKey ).resource( "kind: \"Style\"\n" ).build() ) ) );
    }

    @Test
    void createStyles()
        throws Exception
    {
        final String resource = readResource( "_styles.yaml" );
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.getStyles( applicationKey ) ) ).isNull();

        final DynamicSchemaResult<StyleDescriptor> result = createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateStyles(
            CreateOrUpdateStylesParams.create().key( applicationKey ).resource( resource ).build() ) );

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
            appRepoAdminContext().callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/style" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void updateStyles()
        throws Exception
    {
        final String resource = readResource( "_styles.yaml" );
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateStyles(
            CreateOrUpdateStylesParams.create().key( applicationKey ).resource( "kind: \"Style\"\n" ).build() ) );

        final DynamicSchemaResult<StyleDescriptor> result = createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateStyles(
            CreateOrUpdateStylesParams.create().key( applicationKey ).resource( resource ).build() ) );

        final StyleDescriptor styleDescriptor = result.getSchema();

        createAdminContext().runWith( () -> assertThat( styleDescriptor ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getStyles( applicationKey ).getSchema() ) );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/style/style.yaml", result.getResource().getKey().toString() );

        final Node resourceNode =
            appRepoAdminContext().callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/style" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void deleteStyles()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.getStyles( applicationKey ) ) ).isNull();

        createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateStyles(
            CreateOrUpdateStylesParams.create().key( applicationKey ).resource( readResource( "_styles.yaml" ) ).build() ) );

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.getStyles( applicationKey ) ) ).isNotNull();

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.deleteStyles( applicationKey ) ) ).isTrue();

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.getStyles( applicationKey ) ) ).isNull();
    }


    @Test
    void listPartComponents()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        List<DynamicSchemaResult<PartDescriptor>> results = createAdminContext().callWith( () -> dynamicSchemaService.listParts(
            applicationKey  ) );

        assertTrue( results.isEmpty() );

        DynamicSchemaResult<PartDescriptor> part1 = createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdatePart(
            CreateOrUpdateComponentParams.create()
                .key( DescriptorKey.from( "myapp:mypart1" ) )
                .resource( readResource( "_part.yaml" ) )
                .build() ) );
        DynamicSchemaResult<PartDescriptor> part2 = createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdatePart(
            CreateOrUpdateComponentParams.create()
                .key( DescriptorKey.from( "myapp:mypart2" ) )
                .resource( readResource( "_part.yaml" ) )
                .build() ) );
        DynamicSchemaResult<PartDescriptor> part3 = createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdatePart(
            CreateOrUpdateComponentParams.create()
                .key( DescriptorKey.from( "my_other_app:mypart" ) )
                .resource( readResource( "_part.yaml" ) )
                .build() ) );

        results = createAdminContext().callWith( () -> dynamicSchemaService.listParts(
            applicationKey  ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( part1, part2 ) );

        results = createAdminContext().callWith( () -> dynamicSchemaService.listParts( ApplicationKey.from( "my_other_app" ) ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( part3 ) );

    }

    @Test
    void listContentTypes()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        List<DynamicSchemaResult<ContentType>> results = createAdminContext().callWith( () -> dynamicSchemaService.listContentTypes(
            applicationKey  ) );

        assertTrue( results.isEmpty() );

        DynamicSchemaResult<ContentType> contentType1 = createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateContentType(
            CreateOrUpdateContentSchemaParams.create()
                .name( ContentTypeName.from( "myapp:mytype1" ) )
                .resource( readResource( "_contentType.yaml" ) )
                .build() ) );
        DynamicSchemaResult<ContentType> contentType2 = createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateContentType(
            CreateOrUpdateContentSchemaParams.create()
                .name( ContentTypeName.from( "myapp:mytype2" ) )
                .resource( readResource( "_contentType.yaml" ) )
                .build() ) );
        DynamicSchemaResult<ContentType> contentType3 = createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateContentType(
            CreateOrUpdateContentSchemaParams.create()
                .name( ContentTypeName.from( "my_other_app:mytype" ) )
                .resource( readResource( "_contentType.yaml" ) )
                .build() ) );

        results = createAdminContext().callWith( () -> dynamicSchemaService.listContentTypes( applicationKey  ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( contentType1, contentType2 ) );

        results = createAdminContext().callWith( () -> dynamicSchemaService.listContentTypes( ApplicationKey.from(
                                                                                                        "my_other_app" ) ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( contentType3 ) );

    }

    @Test
    void listFormFragments()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        List<DynamicSchemaResult<FormFragmentDescriptor>> results = createAdminContext().callWith( () -> dynamicSchemaService.listFormFragments(
            applicationKey  ) );

        assertTrue( results.isEmpty() );

        DynamicSchemaResult<FormFragmentDescriptor> fragment1 = createAdminContext().callWith(
            () -> dynamicSchemaService.createOrUpdateFormFragment( CreateOrUpdateContentSchemaParams.create()
                                                                .name( FormFragmentName.from( "myapp:mytype1" ) )
                                                                .resource( readResource( "_formFragment.yaml" ) )
                                                                .build() ) );
        DynamicSchemaResult<FormFragmentDescriptor> fragment2 = createAdminContext().callWith(
            () -> dynamicSchemaService.createOrUpdateFormFragment( CreateOrUpdateContentSchemaParams.create()
                                                                .name( FormFragmentName.from( "myapp:mytype2" ) )
                                                                .resource( readResource( "_formFragment.yaml" ) )
                                                                .build() ) );
        DynamicSchemaResult<FormFragmentDescriptor> fragment3 = createAdminContext().callWith(
            () -> dynamicSchemaService.createOrUpdateFormFragment( CreateOrUpdateContentSchemaParams.create()
                                                                .name( FormFragmentName.from( "my_other_app:mytype" ) )
                                                                .resource( readResource( "_formFragment.yaml" ) )
                                                                .build() ) );

        results = createAdminContext().callWith( () -> dynamicSchemaService.listFormFragments( applicationKey  ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( fragment1, fragment2 ) );

        results = createAdminContext().callWith( () -> dynamicSchemaService.listFormFragments( ApplicationKey.from(
                                                                                                        "my_other_app" ) ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( fragment3 ) );

    }

    @Test
    void listFormFragmentsAsSchemaAdmin()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        List<DynamicSchemaResult<FormFragmentDescriptor>> results = createSchemaAdminContext().callWith(
            () -> dynamicSchemaService.listFormFragments( applicationKey  ) );

        assertTrue( results.isEmpty() );

        DynamicSchemaResult<FormFragmentDescriptor> fragment1 = createAdminContext().callWith(
            () -> dynamicSchemaService.createOrUpdateFormFragment( CreateOrUpdateContentSchemaParams.create()
                                                                .name( FormFragmentName.from( "myapp:mytype1" ) )
                                                                .resource( readResource( "_formFragment.yaml" ) )
                                                                .build() ) );
        DynamicSchemaResult<FormFragmentDescriptor> fragment2 = createAdminContext().callWith(
            () -> dynamicSchemaService.createOrUpdateFormFragment( CreateOrUpdateContentSchemaParams.create()
                                                                .name( FormFragmentName.from( "myapp:mytype2" ) )
                                                                .resource( readResource( "_formFragment.yaml" ) )
                                                                .build() ) );
        DynamicSchemaResult<FormFragmentDescriptor> fragment3 = createAdminContext().callWith(
            () -> dynamicSchemaService.createOrUpdateFormFragment( CreateOrUpdateContentSchemaParams.create()
                                                                .name( FormFragmentName.from( "my_other_app:mytype" ) )
                                                                .resource( readResource( "_formFragment.yaml" ) )
                                                                .build() ) );

        results = createAdminContext().callWith( () -> dynamicSchemaService.listFormFragments( applicationKey  ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( fragment1, fragment2 ) );

        results = createAdminContext().callWith( () -> dynamicSchemaService.listFormFragments( ApplicationKey.from(
                                                                                                        "my_other_app" ) ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( fragment3 ) );

    }

    @Test
    void listFormFragmentsAsNonSchemaAdmin()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        List<DynamicSchemaResult<FormFragmentDescriptor>> results = createSchemaAdminContext().callWith(
            () -> dynamicSchemaService.listFormFragments( applicationKey  ) );

        assertTrue( results.isEmpty() );

        createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateFormFragment( CreateOrUpdateContentSchemaParams.create()
                                                                                           .name( FormFragmentName.from( "myapp:mytype1" ) )
                                                                                           .resource( readResource( "_formFragment.yaml" ) )
                                                                                           .build() ) );
        createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateFormFragment( CreateOrUpdateContentSchemaParams.create()
                                                                                           .name( FormFragmentName.from( "myapp:mytype2" ) )
                                                                                           .resource( readResource( "_formFragment.yaml" ) )
                                                                                           .build() ) );
        createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateFormFragment( CreateOrUpdateContentSchemaParams.create()
                                                                                           .name( FormFragmentName.from(
                                                                                               "my_other_app:mytype" ) )
                                                                                           .resource( readResource( "_formFragment.yaml" ) )
                                                                                           .build() ) );

        assertThrows( ForbiddenAccessException.class, () -> ctxDefault()
            .callWith( () -> dynamicSchemaService.listFormFragments( applicationKey  ) ) );
    }

    @Test
    void listMixinsTypes()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        List<DynamicSchemaResult<MixinDescriptor>> results = createAdminContext().callWith( () -> dynamicSchemaService.listMixins(
            applicationKey  ) );

        assertTrue( results.isEmpty() );

        DynamicSchemaResult<MixinDescriptor> mixin1 = createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateMixin(
            CreateOrUpdateContentSchemaParams.create()
                .name( MixinName.from( "myapp:mytype1" ) )
                .resource( readResource( "_mixin.yaml" ) )
                .build() ) );
        DynamicSchemaResult<MixinDescriptor> mixin2 = createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateMixin(
            CreateOrUpdateContentSchemaParams.create()
                .name( MixinName.from( "myapp:mytype2" ) )
                .resource( readResource( "_mixin.yaml" ) )
                .build() ) );
        DynamicSchemaResult<MixinDescriptor> mixin3 = createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateMixin(
            CreateOrUpdateContentSchemaParams.create()
                .name( MixinName.from( "my_other_app:mytype" ) )
                .resource( readResource( "_mixin.yaml" ) )
                .build() ) );

        results = createAdminContext().callWith( () -> dynamicSchemaService.listMixins(
            applicationKey  ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( mixin1, mixin2 ) );

        results = createAdminContext().callWith( () -> dynamicSchemaService.listMixins( ApplicationKey.from(
                                                                                                        "my_other_app" ) ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( mixin3 ) );

    }

    @Test
    void deleteContentTypeComponent()
    {
        DynamicSchemaResult<ContentType> contentType = createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateContentType(
            CreateOrUpdateContentSchemaParams.create()
                .name( ContentTypeName.from( "myapp:mytype" ) )
                .resource( readResource( "_contentType.yaml" ) )
                .build() ) );

        final boolean result = createAdminContext().callWith( () -> dynamicSchemaService.deleteContentType(
            contentType.getSchema().getName() ) );

        assertThat( result ).isTrue();

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.listContentTypes( ApplicationKey.from( "myapp" ) ) ) ).isEmpty();

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.getContentType( contentType.getSchema().getName() ) ) ).isNull();
    }

    @Test
    void deleteContentTypeComponentAsSchemaAdmin()
    {
        DynamicSchemaResult<ContentType> contentType = createSchemaAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateContentType(
            CreateOrUpdateContentSchemaParams.create()
                .name( ContentTypeName.from( "myapp:mytype" ) )
                .resource( readResource( "_contentType.yaml" ) )
                .build() ) );

        final boolean result = createSchemaAdminContext().callWith( () -> dynamicSchemaService.deleteContentType(
            contentType.getSchema().getName() ) );

        assertThat( result ).isTrue();
    }

    @Test
    void deleteContentTypeComponentAsNonSchemaAdmin()
    {
        DynamicSchemaResult<ContentType> contentType = createSchemaAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateContentType(
            CreateOrUpdateContentSchemaParams.create()
                .name( ContentTypeName.from( "myapp:mytype" ) )
                .resource( readResource( "_contentType.yaml" ) )
                .build() ) );

        assertThrows( ForbiddenAccessException.class, () -> ctxDefault()
            .callWith( () -> dynamicSchemaService.deleteContentType( contentType.getSchema().getName() ) ) );

    }


    @Test
    void deletePartComponent()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        DynamicSchemaResult<PartDescriptor> part = createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdatePart(
            CreateOrUpdateComponentParams.create()
                .key( DescriptorKey.from( "myapp:mypart" ) )
                .resource( readResource( "_part.yaml" ) )
                .build() ) );

        final boolean result = createAdminContext().callWith( () -> dynamicSchemaService.deletePart(
            part.getSchema().getKey() ) );

        assertTrue( result );

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.listParts(
            applicationKey  ) ) ).isEmpty();

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.getPart( part.getSchema().getKey() ) ) ).usingRecursiveComparison()
            .isNull();

    }

    @Test
    void createContentTypeSchemaInvalid()
    {
        final String resource = "unsupportedField: [ ]";

        CreateOrUpdateContentSchemaParams params = CreateOrUpdateContentSchemaParams.create()
            .name( ContentTypeName.from( "myapp:mytype" ) )
            .resource( resource )
            .build();

        final RuntimeException exception = assertThrows( RuntimeException.class, () -> createAdminContext().callWith(
            () -> dynamicSchemaService.createOrUpdateContentType( params ) ) );

        assertEquals( "Could not parse dynamic content type [myapp:mytype]", exception.getMessage() );
    }

    @Test
    void createContentTypeSchemaWithNameOfAnotherKind()
    {
        final CreateOrUpdateContentSchemaParams params = CreateOrUpdateContentSchemaParams.create()
            .name( MixinName.from( "myapp:mytype" ) )
            .resource( "kind: \"ContentType\"" )
            .build();

        final IllegalArgumentException exception = assertThrows( IllegalArgumentException.class, () -> createAdminContext().callWith(
            () -> dynamicSchemaService.createOrUpdateContentType( params ) ) );

        assertEquals( "expected ContentTypeName but got MixinName: myapp:mytype", exception.getMessage() );
    }

    @Test
    public void createFormFragmentSchemaInvalid()
    {
        final String resource = """
            kind: "FormFragment"
            unsupportedField: [ ]
            """;

        CreateOrUpdateContentSchemaParams params = CreateOrUpdateContentSchemaParams.create()
            .name( FormFragmentName.from( "myapp:mytype" ) )
            .resource( resource )
            .build();

        assertThrows( UncheckedIOException.class,
                      () -> createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateFormFragment( params ) ) );
    }

    @Test
    void createMixinSchemaInvalid()
    {
        final String resource = """
            kind: "Mixin"
            unsupportedField: [ ]
            """;

        CreateOrUpdateContentSchemaParams params = CreateOrUpdateContentSchemaParams.create()
            .name( MixinName.from( "myapp:mytype" ) )
            .resource( resource )
            .build();

        assertThrows( UncheckedIOException.class,
                      () -> createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateMixin( params ) ) );
    }

    @Test
    void createPartInvalid()
    {
        final String resource = """
            kind: "Part"        
            unsupportedField: [ ]
            """;

        final CreateOrUpdateComponentParams params = CreateOrUpdateComponentParams.create()
            .key( DescriptorKey.from( "myapp:mytype" ) )
            .resource( resource )
            .build();

        assertThrows( UncheckedIOException.class,
                      () -> createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdatePart( params ) ) );
    }

    @Test
    void createLayoutInvalid()
    {
        final String resource = """
            kind: "Layout"
            unsupportedField: [ ]
            """;

        final CreateOrUpdateComponentParams params = CreateOrUpdateComponentParams.create()
            .key( DescriptorKey.from( "myapp:mytype" ) )
            .resource( resource )
            .build();

        assertThrows( UncheckedIOException.class,
                      () -> createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateLayout( params ) ) );
    }

    @Test
    void createPageInvalid()
    {
        final String resource = """
            kind: "Page"
            unsupportedField: [ ]
            """;

        final CreateOrUpdateComponentParams params = CreateOrUpdateComponentParams.create()
            .key( DescriptorKey.from( "myapp:mytype" ) )
            .resource( resource )
            .build();

        assertThrows( UncheckedIOException.class,
                      () -> createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdatePage( params ) ) );
    }

    @Test
    void createStylesInvalid()
    {
        final String resource = """
            kind: "Style"
            unsupportedField: [ ]
            """;

        final CreateOrUpdateStylesParams params =
            CreateOrUpdateStylesParams.create().key( ApplicationKey.from( "myapp" ) ).resource( resource ).build();

        assertThrows( Exception.class, () -> createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateStyles( params ) ) );
    }

    @Test
    void createSiteInvalid()
    {
        final String resource = """
            kind: "CMS"
            unsupportedField: [ ]
            """;

        final CreateOrUpdateCmsParams params =
            CreateOrUpdateCmsParams.create().key( ApplicationKey.from( "myapp" ) ).resource( resource ).build();

        assertThrows( Exception.class, () -> createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateCms( params ) ) );
    }

    @Test
    void createMacro()
        throws Exception
    {
        final String resource = readResource( "_macro.yaml" );

        final CreateOrUpdateMacroParams params =
            CreateOrUpdateMacroParams.create().key( MacroKey.from( "myapp:mymacro" ) ).resource( resource ).build();

        final DynamicSchemaResult<MacroDescriptor> result = createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateMacro( params ) );

        final MacroDescriptor macroDescriptor = result.getSchema();

        createAdminContext().runWith( () -> assertThat( macroDescriptor ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getMacro( MacroKey.from( "myapp:mymacro" ) ).getSchema() ) );

        assertEquals( "mymacro", macroDescriptor.getName() );
        assertEquals( "myapp", macroDescriptor.getKey().getApplicationKey().toString() );
        assertEquals( "Dynamic Macro", macroDescriptor.getTitle() );
        assertEquals( "key.display-name", macroDescriptor.getTitleI18nKey() );
        assertEquals( "My Macro Description", macroDescriptor.getDescription() );
        assertEquals( "key.description", macroDescriptor.getDescriptionI18nKey() );
        assertEquals( 1, macroDescriptor.getForm().size() );
        assertNotNull( macroDescriptor.getModifiedTime() );
        assertEquals( 1, macroDescriptor.getSchemaConfig().properties().size() );
        assertNull( macroDescriptor.getIcon() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/macros/mymacro.yaml", result.getResource().getKey().toString() );

        // the macros folder did not exist for the application, it is created on the way
        final Node resourceNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/macro:mymacro" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void createMacroWithoutAdminRole()
    {
        final CreateOrUpdateMacroParams params = CreateOrUpdateMacroParams.create()
            .key( MacroKey.from( "myapp:mymacro" ) )
            .resource( "kind: \"Macro\"\ntitle: \"MyMacro\"\n" )
            .build();

        assertThrows( ForbiddenAccessException.class, () -> ctxDefault().callWith( () -> dynamicSchemaService.createOrUpdateMacro( params ) ) );
    }

    @Test
    void updateMacro()
        throws Exception
    {
        final CreateOrUpdateMacroParams createParams = CreateOrUpdateMacroParams.create()
            .key( MacroKey.from( "myapp:mymacro" ) )
            .resource( """
                           kind: "Macro"
                           title: "MyMacro"
                           form: [ ]
                           """ )
            .build();

        createAdminContext().runWith( () -> dynamicSchemaService.createOrUpdateMacro( createParams ) );

        final String resource = readResource( "_macro.yaml" );

        final CreateOrUpdateMacroParams updateParams =
            CreateOrUpdateMacroParams.create().key( MacroKey.from( "myapp:mymacro" ) ).resource( resource ).build();

        final DynamicSchemaResult<MacroDescriptor> result =
            createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateMacro( updateParams ) );

        assertEquals( "Dynamic Macro", result.getSchema().getTitle() );
        assertEquals( resource, result.getResource().readString() );
    }

    @Test
    void listMacros()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.listMacros( applicationKey ) ) ).isEmpty();

        final DynamicSchemaResult<MacroDescriptor> macro1 = createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateMacro(
            CreateOrUpdateMacroParams.create().key( MacroKey.from( "myapp:mymacro1" ) ).resource( readResource( "_macro.yaml" ) ).build() ) );
        final DynamicSchemaResult<MacroDescriptor> macro2 = createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateMacro(
            CreateOrUpdateMacroParams.create().key( MacroKey.from( "myapp:mymacro2" ) ).resource( readResource( "_macro.yaml" ) ).build() ) );
        createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateMacro( CreateOrUpdateMacroParams.create()
                                                                                   .key( MacroKey.from( "my_other_app:mymacro" ) )
                                                                                   .resource( readResource( "_macro.yaml" ) )
                                                                                   .build() ) );

        final List<DynamicSchemaResult<MacroDescriptor>> results =
            createAdminContext().callWith( () -> dynamicSchemaService.listMacros( applicationKey ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( macro1, macro2 ) );
    }

    @Test
    void deleteMacro()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        final DynamicSchemaResult<MacroDescriptor> macro = createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateMacro(
            CreateOrUpdateMacroParams.create().key( MacroKey.from( "myapp:mymacro" ) ).resource( readResource( "_macro.yaml" ) ).build() ) );

        assertTrue( createAdminContext().callWith( () -> dynamicSchemaService.deleteMacro( macro.getSchema().getKey() ) ) );

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.listMacros( applicationKey ) ) ).isEmpty();
        assertNull( createAdminContext().callWith( () -> dynamicSchemaService.getMacro( macro.getSchema().getKey() ) ) );
    }

    @Test
    void createMacroInvalid()
    {
        final CreateOrUpdateMacroParams params = CreateOrUpdateMacroParams.create()
            .key( MacroKey.from( "myapp:mymacro" ) )
            .resource( """
                           kind: "Macro"
                           unsupportedField: [ ]
                           """ )
            .build();

        assertThrows( Exception.class, () -> createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateMacro( params ) ) );
    }

    @Test
    void createPhrases()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );
        final String resource = "action.save=Save\naction.delete=Delete\n";

        assertNull( createAdminContext().callWith( () -> dynamicSchemaService.getPhrases(
            GetDynamicPhrasesParams.create().key( applicationKey ).name( "phrases_en" ).build() ) ) );

        final Resource result = createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdatePhrases(
            CreateOrUpdatePhrasesParams.create().key( applicationKey ).name( "phrases_en" ).resource( resource ).build() ) );

        assertEquals( "node", result.getResolverName() );
        assertTrue( result.exists() );
        assertEquals( resource, result.readString() );
        assertEquals( "myapp:/cms/i18n/phrases/phrases_en.properties", result.getKey().toString() );

        final Resource fetched = createAdminContext().callWith( () -> dynamicSchemaService.getPhrases(
            GetDynamicPhrasesParams.create().key( applicationKey ).name( "phrases_en" ).build() ) );

        assertEquals( "node", fetched.getResolverName() );
        assertEquals( resource, fetched.readString() );

        // both the i18n and the phrases folders are created on the way
        final Node resourceNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/phrases:phrases_en" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void createPhrasesWithoutAdminRole()
    {
        final CreateOrUpdatePhrasesParams params =
            CreateOrUpdatePhrasesParams.create().key( ApplicationKey.from( "myapp" ) ).name( "phrases_en" ).resource( "a=b\n" ).build();

        assertThrows( ForbiddenAccessException.class, () -> ctxDefault().callWith( () -> dynamicSchemaService.createOrUpdatePhrases( params ) ) );
    }

    @Test
    void updatePhrases()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );
        final String resource = "action.save=Save changes\n";

        createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdatePhrases(
            CreateOrUpdatePhrasesParams.create().key( applicationKey ).name( "phrases_en" ).resource( "action.save=Save\n" ).build() ) );

        final Resource result = createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdatePhrases(
            CreateOrUpdatePhrasesParams.create().key( applicationKey ).name( "phrases_en" ).resource( resource ).build() ) );

        assertEquals( "node", result.getResolverName() );
        assertEquals( resource, result.readString() );
        assertEquals( "myapp:/cms/i18n/phrases/phrases_en.properties", result.getKey().toString() );

        final Node resourceNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/phrases:phrases_en" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void listPhrases()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        assertTrue( createAdminContext().callWith( () -> dynamicSchemaService.listPhrases( applicationKey ) ).isEmpty() );

        createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdatePhrases(
            CreateOrUpdatePhrasesParams.create().key( applicationKey ).name( "phrases" ).resource( "action.save=Save\n" ).build() ) );
        createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdatePhrases(
            CreateOrUpdatePhrasesParams.create().key( applicationKey ).name( "phrases_no" ).resource( "action.save=Lagre\n" ).build() ) );
        createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdatePhrases( CreateOrUpdatePhrasesParams.create()
                                                                                     .key( ApplicationKey.from( "my_other_app" ) )
                                                                                     .name( "phrases" )
                                                                                     .resource( "action.save=Other\n" )
                                                                                     .build() ) );

        final List<Resource> result = createAdminContext().callWith( () -> dynamicSchemaService.listPhrases( applicationKey ) );

        assertThat( result.stream().map( resource -> resource.getKey().toString() ) ).containsExactlyInAnyOrder(
            "myapp:/cms/i18n/phrases/phrases.properties", "myapp:/cms/i18n/phrases/phrases_no.properties" );
    }

    @Test
    void deletePhrases()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdatePhrases(
            CreateOrUpdatePhrasesParams.create().key( applicationKey ).name( "phrases_en" ).resource( "action.save=Save\n" ).build() ) );

        assertNotNull( createAdminContext().callWith( () -> dynamicSchemaService.getPhrases(
            GetDynamicPhrasesParams.create().key( applicationKey ).name( "phrases_en" ).build() ) ) );

        assertTrue( createAdminContext().callWith( () -> dynamicSchemaService.deletePhrases(
            DeleteDynamicPhrasesParams.create().key( applicationKey ).name( "phrases_en" ).build() ) ) );

        assertNull( createAdminContext().callWith( () -> dynamicSchemaService.getPhrases(
            GetDynamicPhrasesParams.create().key( applicationKey ).name( "phrases_en" ).build() ) ) );

        assertFalse( createAdminContext().callWith( () -> dynamicSchemaService.deletePhrases(
            DeleteDynamicPhrasesParams.create().key( applicationKey ).name( "phrases_en" ).build() ) ) );
    }

    @Test
    void createContentTypeWithIcon()
        throws Exception
    {
        final String resource = readResource( "_contentType.yaml" );
        final byte[] iconData = "<svg/>".getBytes( StandardCharsets.UTF_8 );

        final DynamicSchemaResult<ContentType> created = createAdminContext().callWith(
            () -> dynamicSchemaService.createOrUpdateContentType( contentTypeParams( "myapp:mytype", resource, svgIcon( iconData ) ) ) );

        final Icon icon = created.getSchema().getIcon();
        assertEquals( "image/svg+xml", icon.getMimeType() );
        assertArrayEquals( iconData, icon.toByteArray() );

        // stored on the schema node, like the icons persisted on application install: a binary named by its format
        final Node schemaNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/contenttype:mytype" ) ) );

        assertEquals( resource, schemaNode.data().getString( "resource" ) );
        assertEquals( BinaryReference.from( "icon.svg" ), schemaNode.data().getBinaryReference( "icon" ) );
        assertNotNull( schemaNode.getAttachedBinaries().getByBinaryReference( BinaryReference.from( "icon.svg" ) ) );
        assertArrayEquals( iconData, appRepoAdminContext()
            .callWith( () -> nodeService.getBinary( schemaNode.id(), BinaryReference.from( "icon.svg" ) ) )
            .read() );

        final ContentType contentType = createAdminContext().callWith(
            () -> dynamicSchemaService.getContentType( ContentTypeName.from( "myapp:mytype" ) ) ).getSchema();
        assertNotNull( contentType.getIcon() );
        assertArrayEquals( iconData, contentType.getIcon().toByteArray() );

        final List<DynamicSchemaResult<ContentType>> listed = createAdminContext().callWith(
            () -> dynamicSchemaService.listContentTypes( ApplicationKey.from( "myapp" ) ) );
        assertNotNull( listed.get( 0 ).getSchema().getIcon() );
    }

    // the resource left out keeps the stored schema and changes the icon alone, the icon left out keeps the icon
    @Test
    void updateContentTypeIconAlone()
        throws Exception
    {
        final String resource = readResource( "_contentType.yaml" );
        final ContentTypeName name = ContentTypeName.from( "myapp:mytype" );

        final DynamicSchemaResult<ContentType> created = createAdminContext().callWith(
            () -> dynamicSchemaService.createOrUpdateContentType( contentTypeParams( "myapp:mytype", resource, IconUpdate.KEEP ) ) );
        assertNull( created.getSchema().getIcon() );

        final byte[] svgData = "<svg/>".getBytes( StandardCharsets.UTF_8 );
        final DynamicSchemaResult<ContentType> withSvg = createAdminContext().callWith(
            () -> dynamicSchemaService.createOrUpdateContentType( contentTypeParams( "myapp:mytype", null, svgIcon( svgData ) ) ) );
        assertEquals( "image/svg+xml", withSvg.getSchema().getIcon().getMimeType() );
        assertEquals( resource, withSvg.getResource().readString() );
        assertEquals( resource, createAdminContext().callWith( () -> dynamicSchemaService.getContentType( name ) ).getResource().readString() );

        // an icon of the other format replaces the stored one, so a schema never has two icons
        final byte[] pngData = {(byte) 0x89, 'P', 'N', 'G'};
        final DynamicSchemaResult<ContentType> withPng = createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateContentType(
            contentTypeParams( "myapp:mytype", null, IconUpdate.replace( ByteSource.wrap( pngData ), "image/png" ) ) ) );
        assertEquals( "image/png", withPng.getSchema().getIcon().getMimeType() );
        assertArrayEquals( pngData, withPng.getSchema().getIcon().toByteArray() );

        appRepoAdminContext().runWith( () -> {
            final Node schemaNode = nodeService.getByPath( new NodePath( "/myapp/cms/contenttype:mytype" ) );
            assertEquals( BinaryReference.from( "icon.png" ), schemaNode.data().getBinaryReference( "icon" ) );
            assertNotNull( schemaNode.getAttachedBinaries().getByBinaryReference( BinaryReference.from( "icon.png" ) ) );
            assertNull( schemaNode.getAttachedBinaries().getByBinaryReference( BinaryReference.from( "icon.svg" ) ) );
        } );

        final DynamicSchemaResult<ContentType> resourceOnly = createAdminContext().callWith(
            () -> dynamicSchemaService.createOrUpdateContentType( contentTypeParams( "myapp:mytype", resource, IconUpdate.KEEP ) ) );
        assertEquals( "image/png", resourceOnly.getSchema().getIcon().getMimeType() );

        final DynamicSchemaResult<ContentType> removed = createAdminContext().callWith(
            () -> dynamicSchemaService.createOrUpdateContentType( contentTypeParams( "myapp:mytype", null, IconUpdate.REMOVE ) ) );
        assertNull( removed.getSchema().getIcon() );
        assertNull( createAdminContext().callWith( () -> dynamicSchemaService.getContentType( name ) ).getSchema().getIcon() );
        appRepoAdminContext().runWith( () -> assertNull(
            nodeService.getByPath( new NodePath( "/myapp/cms/contenttype:mytype" ) ).data().getBinaryReference( "icon" ) ) );

        // nothing to remove: the schema is left as it is
        final DynamicSchemaResult<ContentType> removedAgain = createAdminContext().callWith(
            () -> dynamicSchemaService.createOrUpdateContentType( contentTypeParams( "myapp:mytype", null, IconUpdate.REMOVE ) ) );
        assertNull( removedAgain.getSchema().getIcon() );
        assertEquals( resource, removedAgain.getResource().readString() );
    }

    @Test
    void createContentTypeIconAloneForMissingSchema()
    {
        final CreateOrUpdateContentSchemaParams params =
            contentTypeParams( "myapp:mytype", null, svgIcon( "<svg/>".getBytes( StandardCharsets.UTF_8 ) ) );

        final IllegalArgumentException exception = assertThrows( IllegalArgumentException.class, () -> createAdminContext().callWith(
            () -> dynamicSchemaService.createOrUpdateContentType( params ) ) );
        assertEquals( "resource is required to create [myapp:mytype]", exception.getMessage() );
    }

    @Test
    void updateContentTypeWithoutResourceOrIcon()
        throws Exception
    {
        final String resource = readResource( "_contentType.yaml" );
        createAdminContext().callWith(
            () -> dynamicSchemaService.createOrUpdateContentType( contentTypeParams( "myapp:mytype", resource, IconUpdate.KEEP ) ) );

        final CreateOrUpdateContentSchemaParams params = contentTypeParams( "myapp:mytype", null, IconUpdate.KEEP );

        final IllegalArgumentException exception = assertThrows( IllegalArgumentException.class, () -> createAdminContext().callWith(
            () -> dynamicSchemaService.createOrUpdateContentType( params ) ) );
        assertEquals( "resource or icon is required to update [myapp:mytype]", exception.getMessage() );
    }

    // the icon is validated before anything is written
    @Test
    void createContentTypeInvalidIconMimeType()
        throws Exception
    {
        final String resource = readResource( "_contentType.yaml" );
        final CreateOrUpdateContentSchemaParams params = contentTypeParams( "myapp:mytype", resource, IconUpdate.replace(
            ByteSource.wrap( "<svg/>".getBytes( StandardCharsets.UTF_8 ) ), "text/plain" ) );

        final IllegalArgumentException exception = assertThrows( IllegalArgumentException.class, () -> createAdminContext().callWith(
            () -> dynamicSchemaService.createOrUpdateContentType( params ) ) );

        assertEquals( "unsupported icon mime type: text/plain", exception.getMessage() );
        assertNull( createAdminContext().callWith( () -> dynamicSchemaService.getContentType( ContentTypeName.from( "myapp:mytype" ) ) ) );
    }

    @Test
    void createContentTypeEmptyIcon()
        throws Exception
    {
        final String resource = readResource( "_contentType.yaml" );
        final CreateOrUpdateContentSchemaParams params =
            contentTypeParams( "myapp:mytype", resource, IconUpdate.replace( ByteSource.empty(), "image/svg+xml" ) );

        final IllegalArgumentException exception = assertThrows( IllegalArgumentException.class, () -> createAdminContext().callWith(
            () -> dynamicSchemaService.createOrUpdateContentType( params ) ) );

        assertEquals( "icon data is required", exception.getMessage() );
        assertNull( createAdminContext().callWith( () -> dynamicSchemaService.getContentType( ContentTypeName.from( "myapp:mytype" ) ) ) );
    }

    @Test
    void createContentTypeIconExceedingMaxSize()
        throws Exception
    {
        final String resource = readResource( "_contentType.yaml" );
        final CreateOrUpdateContentSchemaParams params =
            contentTypeParams( "myapp:mytype", resource, svgIcon( new byte[100 * 1024 + 1] ) );

        assertThrows( IllegalArgumentException.class,
                      () -> createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateContentType( params ) ) );
        assertNull( createAdminContext().callWith( () -> dynamicSchemaService.getContentType( ContentTypeName.from( "myapp:mytype" ) ) ) );
    }

    @Test
    void createContentTypeIconWithoutAdminRole()
        throws Exception
    {
        final String resource = readResource( "_contentType.yaml" );
        final CreateOrUpdateContentSchemaParams params =
            contentTypeParams( "myapp:mytype", resource, svgIcon( "<svg/>".getBytes( StandardCharsets.UTF_8 ) ) );

        assertThrows( ForbiddenAccessException.class,
                      () -> ctxDefault().callWith( () -> dynamicSchemaService.createOrUpdateContentType( params ) ) );
    }

    @Test
    void deleteContentTypeCascadesIcon()
        throws Exception
    {
        final String resource = readResource( "_contentType.yaml" );
        createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateContentType(
            contentTypeParams( "myapp:mytype", resource, svgIcon( "<svg/>".getBytes( StandardCharsets.UTF_8 ) ) ) ) );

        assertTrue( createAdminContext().callWith( () -> dynamicSchemaService.deleteContentType( ContentTypeName.from( "myapp:mytype" ) ) ) );

        appRepoAdminContext().runWith( () -> assertNull( nodeService.getByPath( new NodePath( "/myapp/cms/contenttype:mytype" ) ) ) );
    }

    @Test
    void createPartWithIcon()
        throws Exception
    {
        final String resource = readResource( "_part.yaml" );
        final DescriptorKey key = DescriptorKey.from( "myapp:mypart" );
        final byte[] iconData = "<svg/>".getBytes( StandardCharsets.UTF_8 );

        final DynamicSchemaResult<PartDescriptor> created = createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdatePart(
            CreateOrUpdateComponentParams.create().key( key ).resource( resource ).icon( svgIcon( iconData ) ).build() ) );
        assertArrayEquals( iconData, created.getSchema().getIcon().toByteArray() );

        final PartDescriptor fetched = createAdminContext().callWith( () -> dynamicSchemaService.getPart( key ) ).getSchema();
        assertNotNull( fetched.getIcon() );
        assertArrayEquals( iconData, fetched.getIcon().toByteArray() );

        final DynamicSchemaResult<PartDescriptor> removed = createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdatePart(
            CreateOrUpdateComponentParams.create().key( key ).icon( IconUpdate.REMOVE ).build() ) );
        assertNull( removed.getSchema().getIcon() );
        assertEquals( resource, removed.getResource().readString() );
        assertNull( createAdminContext().callWith( () -> dynamicSchemaService.getPart( key ) ).getSchema().getIcon() );
    }

    // only parts have an icon among the components
    @Test
    void createLayoutOrPageWithIconFails()
        throws Exception
    {
        final String layout = readResource( "_layout.yaml" );
        final String page = readResource( "_page.yaml" );
        final IconUpdate icon = svgIcon( "<svg/>".getBytes( StandardCharsets.UTF_8 ) );

        final CreateOrUpdateComponentParams layoutParams =
            CreateOrUpdateComponentParams.create().key( DescriptorKey.from( "myapp:mylayout" ) ).resource( layout ).icon( icon ).build();
        final IllegalArgumentException layoutException = assertThrows( IllegalArgumentException.class, () -> createAdminContext().callWith(
            () -> dynamicSchemaService.createOrUpdateLayout( layoutParams ) ) );
        assertEquals( "icon is not supported for LAYOUT [myapp:mylayout]", layoutException.getMessage() );

        final CreateOrUpdateComponentParams pageParams =
            CreateOrUpdateComponentParams.create().key( DescriptorKey.from( "myapp:mypage" ) ).resource( page ).icon( IconUpdate.REMOVE ).build();
        assertThrows( IllegalArgumentException.class,
                      () -> createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdatePage( pageParams ) ) );

        assertNull( createAdminContext().callWith( () -> dynamicSchemaService.getLayout( DescriptorKey.from( "myapp:mylayout" ) ) ) );
        assertNull( createAdminContext().callWith( () -> dynamicSchemaService.getPage( DescriptorKey.from( "myapp:mypage" ) ) ) );
    }

    @Test
    void createMacroWithIcon()
        throws Exception
    {
        final String resource = readResource( "_macro.yaml" );
        final MacroKey key = MacroKey.from( "myapp:mymacro" );
        final byte[] iconData = "<svg/>".getBytes( StandardCharsets.UTF_8 );

        createAdminContext().callWith(
            () -> dynamicSchemaService.createOrUpdateMacro( CreateOrUpdateMacroParams.create().key( key ).resource( resource ).build() ) );

        final DynamicSchemaResult<MacroDescriptor> withIcon = createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateMacro(
            CreateOrUpdateMacroParams.create().key( key ).icon( svgIcon( iconData ) ).build() ) );
        assertArrayEquals( iconData, withIcon.getSchema().getIcon().toByteArray() );
        assertEquals( resource, withIcon.getResource().readString() );

        final MacroDescriptor fetched = createAdminContext().callWith( () -> dynamicSchemaService.getMacro( key ) ).getSchema();
        assertNotNull( fetched.getIcon() );
        assertArrayEquals( iconData, fetched.getIcon().toByteArray() );

        final DynamicSchemaResult<MacroDescriptor> removed = createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateMacro(
            CreateOrUpdateMacroParams.create().key( key ).icon( IconUpdate.REMOVE ).build() ) );
        assertNull( removed.getSchema().getIcon() );
        assertNull( createAdminContext().callWith( () -> dynamicSchemaService.getMacro( key ) ).getSchema().getIcon() );
    }

    @Test
    void createMixinWithIcon()
        throws Exception
    {
        final String resource = readResource( "_mixin.yaml" );
        final MixinName name = MixinName.from( "myapp:mymixin" );
        final byte[] iconData = "<svg/>".getBytes( StandardCharsets.UTF_8 );

        final DynamicSchemaResult<MixinDescriptor> created = createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateMixin(
            CreateOrUpdateContentSchemaParams.create().name( name ).resource( resource ).icon( svgIcon( iconData ) ).build() ) );
        assertArrayEquals( iconData, created.getSchema().getIcon().toByteArray() );

        final MixinDescriptor fetched = createAdminContext().callWith( () -> dynamicSchemaService.getMixin( name ) ).getSchema();
        assertNotNull( fetched.getIcon() );

        final DynamicSchemaResult<MixinDescriptor> removed = createAdminContext().callWith( () -> dynamicSchemaService.createOrUpdateMixin(
            CreateOrUpdateContentSchemaParams.create().name( name ).icon( IconUpdate.REMOVE ).build() ) );
        assertNull( removed.getSchema().getIcon() );
        assertNull( createAdminContext().callWith( () -> dynamicSchemaService.getMixin( name ) ).getSchema().getIcon() );
    }

    @Test
    void createFormFragmentWithIcon()
        throws Exception
    {
        final String resource = readResource( "_formFragment.yaml" );
        final FormFragmentName name = FormFragmentName.from( "myapp:myfragment" );
        final byte[] iconData = "<svg/>".getBytes( StandardCharsets.UTF_8 );

        final DynamicSchemaResult<FormFragmentDescriptor> created = createAdminContext().callWith(
            () -> dynamicSchemaService.createOrUpdateFormFragment(
                CreateOrUpdateContentSchemaParams.create().name( name ).resource( resource ).icon( svgIcon( iconData ) ).build() ) );
        assertArrayEquals( iconData, created.getSchema().getIcon().toByteArray() );

        final FormFragmentDescriptor fetched = createAdminContext().callWith( () -> dynamicSchemaService.getFormFragment( name ) ).getSchema();
        assertNotNull( fetched.getIcon() );

        final DynamicSchemaResult<FormFragmentDescriptor> removed = createAdminContext().callWith(
            () -> dynamicSchemaService.createOrUpdateFormFragment(
                CreateOrUpdateContentSchemaParams.create().name( name ).icon( IconUpdate.REMOVE ).build() ) );
        assertNull( removed.getSchema().getIcon() );
        assertNull( createAdminContext().callWith( () -> dynamicSchemaService.getFormFragment( name ) ).getSchema().getIcon() );
    }

    // every change of the schema is told to the cluster as an UPDATED application event naming what changed
    @Test
    void applicationEventsOnSchemaChanges()
        throws Exception
    {
        final String contentType = readResource( "_contentType.yaml" );
        final String part = readResource( "_part.yaml" );
        final String styles = readResource( "_styles.yaml" );
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );
        applicationEvents.clear();

        createAdminContext().runWith( () -> {
            dynamicSchemaService.createOrUpdateContentType( contentTypeParams( "myapp:mytype", contentType, IconUpdate.KEEP ) );
            dynamicSchemaService.createOrUpdateContentType( contentTypeParams( "myapp:mytype", null, svgIcon( "<svg/>".getBytes( StandardCharsets.UTF_8 ) ) ) );
            dynamicSchemaService.deleteContentType( ContentTypeName.from( "myapp:mytype" ) );
            dynamicSchemaService.deleteContentType( ContentTypeName.from( "myapp:mytype" ) );
            dynamicSchemaService.createOrUpdatePart(
                CreateOrUpdateComponentParams.create().key( DescriptorKey.from( "myapp:mypart" ) ).resource( part ).build() );
            dynamicSchemaService.createOrUpdateStyles( CreateOrUpdateStylesParams.create().key( applicationKey ).resource( styles ).build() );
            dynamicSchemaService.createOrUpdatePhrases(
                CreateOrUpdatePhrasesParams.create().key( applicationKey ).name( "phrases_en" ).resource( "a=b" ).build() );
            dynamicSchemaService.deleteCms( applicationKey );
        } );

        // the kind is the one the descriptor declares, Phrases for the i18n phrases, which declare none
        assertEquals( List.of( "ContentType myapp:mytype created", //
                               "ContentType myapp:mytype updated", //
                               "ContentType myapp:mytype deleted", //
                               "Part myapp:mypart created", //
                               "Style null created", //
                               "Phrases phrases_en created", //
                               "CMS null deleted" ), applicationEvents.stream().map( DynamicSchemaServiceImplTest::describe ).toList() );

        final Event event = applicationEvents.get( 0 );
        assertEquals( ApplicationEvents.EVENT_TYPE, event.getType() );
        assertTrue( event.isDistributed() );
        assertEquals( ApplicationEvents.UPDATED, event.getData().get( ApplicationEvents.EVENT_TYPE_KEY ) );
        assertEquals( "myapp", String.valueOf( event.getData().get( ApplicationEvents.APPLICATION_KEY_KEY ) ) );
    }

    private static String describe( final Event event )
    {
        return event.getData().get( ApplicationEvents.KIND_KEY ) + " " + event.getData().get( ApplicationEvents.NAME_KEY ) + " " +
            event.getData().get( ApplicationEvents.ACTION_KEY );
    }

    private static IconUpdate svgIcon( final byte[] data )
    {
        return IconUpdate.replace( ByteSource.wrap( data ), "image/svg+xml" );
    }

    private static CreateOrUpdateContentSchemaParams contentTypeParams( final String name, final String resource, final IconUpdate icon )
    {
        return CreateOrUpdateContentSchemaParams.create().name( ContentTypeName.from( name ) ).resource( resource ).icon( icon ).build();
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
