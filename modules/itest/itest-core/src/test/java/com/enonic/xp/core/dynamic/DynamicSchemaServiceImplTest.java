package com.enonic.xp.core.dynamic;

import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.felix.framework.Felix;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.CreateVirtualApplicationParams;
import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.impl.app.AppConfig;
import com.enonic.xp.core.impl.app.AppFilterService;
import com.enonic.xp.core.impl.app.AppFilterServiceImpl;
import com.enonic.xp.core.impl.app.ApplicationAuditLogSupportImpl;
import com.enonic.xp.core.impl.app.ApplicationFactoryServiceImpl;
import com.enonic.xp.core.impl.app.ApplicationListenerHub;
import com.enonic.xp.core.impl.app.ApplicationRegistry;
import com.enonic.xp.core.impl.app.ApplicationRegistryImpl;
import com.enonic.xp.core.impl.app.ApplicationRepoInitializer;
import com.enonic.xp.core.impl.app.ApplicationRepoServiceImpl;
import com.enonic.xp.core.impl.app.ApplicationServiceImpl;
import com.enonic.xp.core.impl.app.CreateDynamicCmsParams;
import com.enonic.xp.core.impl.app.DynamicSchemaServiceImpl;
import com.enonic.xp.core.impl.app.VirtualAppConstants;
import com.enonic.xp.core.impl.app.VirtualAppContext;
import com.enonic.xp.core.impl.app.VirtualAppInitializer;
import com.enonic.xp.core.impl.app.VirtualAppService;
import com.enonic.xp.core.impl.app.resource.ResourceServiceImpl;
import com.enonic.xp.core.impl.event.EventPublisherImpl;
import com.enonic.xp.core.impl.project.init.ContentInitializer;
import com.enonic.xp.core.impl.security.SecurityAuditLogSupportImpl;
import com.enonic.xp.core.impl.security.SecurityConfig;
import com.enonic.xp.core.impl.security.SecurityInitializer;
import com.enonic.xp.core.impl.security.SecurityServiceImpl;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.exception.ForbiddenAccessException;
import com.enonic.xp.internal.blobstore.MemoryBlobStore;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.region.ComponentDescriptor;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.repo.impl.binary.BinaryServiceImpl;
import com.enonic.xp.repo.impl.branch.storage.BranchServiceImpl;
import com.enonic.xp.repo.impl.commit.CommitServiceImpl;
import com.enonic.xp.repo.impl.config.RepoConfiguration;
import com.enonic.xp.repo.impl.elasticsearch.AbstractElasticsearchIntegrationTest;
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

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DynamicSchemaServiceImplTest
    extends AbstractElasticsearchIntegrationTest
{
    NodeServiceImpl nodeService;

    private DynamicSchemaServiceImpl dynamicSchemaService;

    @TempDir
    private Path felixTempFolder;

    private static Context ctxDefault()
    {
        return ContextBuilder.copyOf( ContextAccessor.current() ).build();
    }

    private static Context createAdminContext()
    {
        return ContextBuilder.copyOf( ctxDefault() )
            .authInfo( AuthenticationInfo.create().principals( RoleKeys.AUTHENTICATED, RoleKeys.ADMIN ).user( User.ANONYMOUS ).build() )
            .build();
    }

    private static Context createSchemaAdminContext()
    {
        return ContextBuilder.copyOf( ctxDefault() )
            .authInfo(
                AuthenticationInfo.create().principals( RoleKeys.AUTHENTICATED, RoleKeys.SCHEMA_ADMIN ).user( User.ANONYMOUS ).build() )
            .build();
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
            new RepositoryServiceImpl( repositoryEntryService, indexServiceInternal, nodeRepositoryService, storageService, searchService );
        SystemRepoInitializer.create()
            .setIndexServiceInternal( indexServiceInternal )
            .setRepositoryService( repositoryService )
            .setNodeStorageService( storageService )
            .build()
            .initialize();

        nodeService =
            new NodeServiceImpl( indexServiceInternal, storageService, searchService, eventPublisher, binaryService, repositoryService );

        Path cacheDir = Files.createDirectory( this.felixTempFolder.resolve( "cache" ) ).toAbsolutePath();

        Felix felix = createFelixInstance( cacheDir );
        felix.start();

        ApplicationRepoServiceImpl repoService = new ApplicationRepoServiceImpl( nodeService );
        ApplicationRepoInitializer.create().setIndexService( indexService ).setNodeService( nodeService ).build().initialize();

        BundleContext bundleContext = felix.getBundleContext();

        AppConfig appConfig = mock( AppConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( appConfig.virtual_enabled() ).thenReturn( true );

        ApplicationFactoryServiceImpl applicationFactoryService =
            new ApplicationFactoryServiceImpl( bundleContext, nodeService, appConfig );
        applicationFactoryService.activate();

        ResourceServiceImpl resourceService = new ResourceServiceImpl( applicationFactoryService );

        this.dynamicSchemaService = new DynamicSchemaServiceImpl( nodeService, resourceService );

        AppFilterService appFilterService = new AppFilterServiceImpl( appConfig );

        ApplicationRegistry applicationRegistry =
            new ApplicationRegistryImpl( bundleContext, new ApplicationListenerHub(), applicationFactoryService );

        final SecurityConfig securityConfig = mock( SecurityConfig.class );
        when( securityConfig.auditlog_enabled() ).thenReturn( Boolean.TRUE );

        final SecurityAuditLogSupportImpl securityAuditLogSupport = new SecurityAuditLogSupportImpl( mock( AuditLogService.class ) );
        securityAuditLogSupport.activate( securityConfig );

        SecurityServiceImpl securityService = new SecurityServiceImpl( nodeService, securityAuditLogSupport );
        SecurityInitializer.create()
            .setIndexService( indexService )
            .setSecurityService( securityService )
            .setNodeService( nodeService )
            .build()
            .initialize();

        final VirtualAppService virtualAppService = new VirtualAppService( nodeService );
        VirtualAppInitializer.create()
            .setIndexService( indexService )
            .setRepositoryService( repositoryService )
            .setSecurityService( securityService )
            .build()
            .initialize();

        ApplicationService applicationService =
            new ApplicationServiceImpl( bundleContext, applicationRegistry, repoService, eventPublisher, appFilterService,
                                        virtualAppService, new ApplicationAuditLogSupportImpl( mock( AuditLogService.class ) ) );

        createSchemaAdminContext().runWith( () -> applicationService.createVirtualApplication(
            CreateVirtualApplicationParams.create().key( ApplicationKey.from( "myapp" ) ).build() ) );

        createAdminContext().runWith( () -> applicationService.createVirtualApplication(
            CreateVirtualApplicationParams.create().key( ApplicationKey.from( "my-other-app" ) ).build() ) );

        ContentInitializer.create()
            .setIndexService( indexService )
            .setNodeService( nodeService )
            .setRepositoryService( repositoryService )
            .repositoryId( ProjectName.from( "my-project" ).getRepoId() )
            .build()
            .initialize();
    }

    @Test
    void createContentTypeSchema()
        throws Exception
    {
        final String resource = readResource( "_contentType.yml" );

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
        assertEquals( "Virtual Content Type", contentType.getDisplayName() );
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
        assertEquals( "myapp:/cms/content-types/mytype/mytype.yml", result.getResource().getKey().toString() );
        assertTrue( result.getResource().getSize() > 0 );

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/content-types/mytype/mytype.yml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void updateContentTypeSchema()
        throws Exception
    {
        final CreateDynamicContentSchemaParams createParams = CreateDynamicContentSchemaParams.create()
            .name( ContentTypeName.from( "myapp:mytype" ) )
            .resource( "superType: \"base:unstructured\"" )
            .type( DynamicContentSchemaType.CONTENT_TYPE )
            .build();

        createAdminContext().runWith( () -> dynamicSchemaService.createContentSchema( createParams ) );

        final String resource = readResource( "_contentType.yml" );

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
        assertEquals( "Virtual Content Type", contentType.getDisplayName() );
        assertEquals( "description", contentType.getDescription() );
        assertEquals( 1, contentType.getForm().size() );
        assertFalse( contentType.allowChildContent() );
        assertFalse( contentType.isAbstract() );
        assertTrue( contentType.isFinal() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/content-types/mytype/mytype.yml", result.getResource().getKey().toString() );

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/content-types/mytype/mytype.yml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void createFormFragmentSchema()
        throws Exception
    {
        final String resource = readResource( "_formFragment.yml" );

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
        assertEquals( "Virtual FormFragment", fragment.getDisplayName() );
        assertEquals( "FormFragment description", fragment.getDescription() );
        assertEquals( 2, fragment.getForm().size() );
        assertEquals( "myapp:inline", fragment.getForm().getFormFragment( "inline" ).getFormFragmentName().toString() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/form-fragments/my-fragment/my-fragment.yml", result.getResource().getKey().toString() );

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/form-fragments/my-fragment/my-fragment.yml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void createFormFragmentSchemaAsDevSchemaAdmin()
        throws Exception
    {
        final String resource = readResource( "_formFragment.yml" );

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
        final String resource = readResource( "_formFragment.yml" );

        CreateDynamicContentSchemaParams params = CreateDynamicContentSchemaParams.create()
            .name( FormFragmentName.from( "myapp:my-fragment" ) )
            .resource( resource )
            .type( DynamicContentSchemaType.FORM_FRAGMENT )
            .build();

        assertThrows( ForbiddenAccessException.class,
                      () -> VirtualAppContext.createContext().callWith( () -> dynamicSchemaService.createContentSchema( params ) ) );
    }

    @Test
    void updateFormFragmentSchema()
        throws Exception
    {

        final CreateDynamicContentSchemaParams createParams =
            CreateDynamicContentSchemaParams.create()
                .name( FormFragmentName.from( "myapp:my-fragment" ) )
                .resource( """
                               displayName: "MyFormFragment"
                               """ )
                .type( DynamicContentSchemaType.FORM_FRAGMENT )
                .build();

        createAdminContext().runWith( () -> dynamicSchemaService.createContentSchema( createParams ) );

        final String resource = readResource( "_formFragment.yml" );

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
        assertEquals( "Virtual FormFragment", fragment.getDisplayName() );
        assertEquals( "FormFragment description", fragment.getDescription() );
        assertEquals( 2, fragment.getForm().size() );
        assertEquals( "myapp:inline", fragment.getForm().getFormFragment( "inline" ).getFormFragmentName().toString() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/form-fragments/my-fragment/my-fragment.yml", result.getResource().getKey().toString() );

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/form-fragments/my-fragment/my-fragment.yml" ) ) );

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
                               displayName: "FormFragment"
                               """ )
                .type( DynamicContentSchemaType.FORM_FRAGMENT )
                .build();

        createSchemaAdminContext().runWith( () -> dynamicSchemaService.createContentSchema( createParams ) );

        final String resource = readResource( "_formFragment.yml" );

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
                               displayName: "FormFragment"
                               """ )
                .type( DynamicContentSchemaType.FORM_FRAGMENT )
                .build();

        createSchemaAdminContext().runWith( () -> dynamicSchemaService.createContentSchema( createParams ) );

        final String resource = readResource( "_formFragment.yml" );

        final UpdateDynamicContentSchemaParams updateParams = UpdateDynamicContentSchemaParams.create()
            .name( FormFragmentName.from( "myapp:my-fragment" ) )
            .resource( resource )
            .type( DynamicContentSchemaType.FORM_FRAGMENT )
            .build();

        assertThrows( ForbiddenAccessException.class,
                      () -> VirtualAppContext.createContext().callWith( () -> dynamicSchemaService.updateContentSchema( updateParams ) ) );
    }


    @Test
    void createMixinSchema()
        throws Exception
    {
        final String resource = readResource( "_mixin.yml" );

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
        assertEquals( "Virtual Mixin", mixinDescriptor.getDisplayName() );
        assertEquals( "Mixin description", mixinDescriptor.getDescription() );
        assertEquals( 1, mixinDescriptor.getForm().size() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/mixins/mymixin/mymixin.yml", result.getResource().getKey().toString() );

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/mixins/mymixin/mymixin.yml" ) ) );

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
                               displayName: "Virtual MIXIN"
                               form: [ ]
                               """ )
                .type( DynamicContentSchemaType.MIXIN )
                .build();

        createAdminContext().runWith( () -> dynamicSchemaService.createContentSchema( createParams ) );

        final String resource = readResource( "_mixin.yml" );

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
        assertEquals( "Virtual Mixin", mixinDescriptor.getDisplayName() );
        assertEquals( "Mixin description", mixinDescriptor.getDescription() );
        assertEquals( 1, mixinDescriptor.getForm().size() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/mixins/mymixin/mymixin.yml", result.getResource().getKey().toString() );

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/mixins/mymixin/mymixin.yml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void createPartComponent()
        throws Exception
    {
        final String resource = readResource( "_part.yml" );

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
        assertEquals( "Virtual Part", partDescriptor.getDisplayName() );
        assertEquals( "key.display-name", partDescriptor.getDisplayNameI18nKey() );
        assertEquals( "My Part Description", partDescriptor.getDescription() );
        assertEquals( "key.description", partDescriptor.getDescriptionI18nKey() );
        assertEquals( 2, partDescriptor.getConfig().size() );
        assertNotNull( partDescriptor.getModifiedTime() );

        assertEquals( 1, partDescriptor.getSchemaConfig().getProperties().size() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/parts/mypart/mypart.yml", result.getResource().getKey().toString() );

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/parts/mypart/mypart.yml" ) ) );

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
                               displayName: "MyPart"
                               form: [ ]
                               """ )
                .type( DynamicComponentType.PART )
                .build();

        createAdminContext().runWith( () -> dynamicSchemaService.createComponent( createParams ) );

        final String resource = readResource( "_part.yml" );

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
        assertEquals( "Virtual Part", partDescriptor.getDisplayName() );
        assertEquals( "key.display-name", partDescriptor.getDisplayNameI18nKey() );
        assertEquals( "My Part Description", partDescriptor.getDescription() );
        assertEquals( "key.description", partDescriptor.getDescriptionI18nKey() );
        assertEquals( 2, partDescriptor.getConfig().size() );

        assertEquals( 1, partDescriptor.getSchemaConfig().getProperties().size() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/parts/mypart/mypart.yml", result.getResource().getKey().toString() );

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/parts/mypart/mypart.yml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void createLayoutComponent()
        throws Exception
    {
        final String resource = readResource( "_layout.yml" );

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
        assertEquals( "Virtual Layout", layoutDescriptor.getDisplayName() );
        assertEquals( "key.display-name", layoutDescriptor.getDisplayNameI18nKey() );
        assertEquals( "My Layout Description", layoutDescriptor.getDescription() );
        assertEquals( "key.description", layoutDescriptor.getDescriptionI18nKey() );
        assertEquals( 2, layoutDescriptor.getConfig().size() );
        assertEquals( 3, layoutDescriptor.getRegions().numberOfRegions() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/layouts/mylayout/mylayout.yml", result.getResource().getKey().toString() );

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/layouts/mylayout/mylayout.yml" ) ) );

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
                               displayName: MyLayout
                               regions: [ ]
                               
                               """ )
                .type( DynamicComponentType.LAYOUT )
                .build();

        createAdminContext().runWith( () -> dynamicSchemaService.createComponent( params ) );

        final String resource = readResource( "_layout.yml" );

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
        assertEquals( "Virtual Layout", layoutDescriptor.getDisplayName() );
        assertEquals( "key.display-name", layoutDescriptor.getDisplayNameI18nKey() );
        assertEquals( "My Layout Description", layoutDescriptor.getDescription() );
        assertEquals( "key.description", layoutDescriptor.getDescriptionI18nKey() );
        assertEquals( 2, layoutDescriptor.getConfig().size() );
        assertEquals( 3, layoutDescriptor.getRegions().numberOfRegions() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/layouts/mylayout/mylayout.yml", result.getResource().getKey().toString() );

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/layouts/mylayout/mylayout.yml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void createPageComponent()
        throws Exception
    {
        final String resource = readResource( "_page.yml" );

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
        assertEquals( "Virtual Page", pageDescriptor.getDisplayName() );
        assertEquals( "key.display-name", pageDescriptor.getDisplayNameI18nKey() );
        assertEquals( "My Page Description", pageDescriptor.getDescription() );
        assertEquals( "key.description", pageDescriptor.getDescriptionI18nKey() );
        assertEquals( 1, pageDescriptor.getConfig().size() );
        assertEquals( 3, pageDescriptor.getRegions().numberOfRegions() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/pages/mypage/mypage.yml", result.getResource().getKey().toString() );

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/pages/mypage/mypage.yml" ) ) );

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
                               displayName: "MyPage"
                               regions:
                                 - "main"
                               """ )
                .type( DynamicComponentType.PAGE )
                .build();

        createAdminContext().runWith( () -> dynamicSchemaService.createComponent( createParams ) );

        final String resource = readResource( "_page.yml" );

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
        assertEquals( "Virtual Page", pageDescriptor.getDisplayName() );
        assertEquals( "key.display-name", pageDescriptor.getDisplayNameI18nKey() );
        assertEquals( "My Page Description", pageDescriptor.getDescription() );
        assertEquals( "key.description", pageDescriptor.getDescriptionI18nKey() );
        assertEquals( 1, pageDescriptor.getConfig().size() );
        assertEquals( 3, pageDescriptor.getRegions().numberOfRegions() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/pages/mypage/mypage.yml", result.getResource().getKey().toString() );

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/pages/mypage/mypage.yml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void createSite()
        throws Exception
    {
        final String resource = readResource( "_cms.yml" );
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.getCmsDescriptor( applicationKey ) ) ).isNotNull();

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
        assertEquals( "myapp:/cms/cms.yml", result.getResource().getKey().toString() );
        assertNotNull( cmsDescriptor.getModifiedTime() );

        final Node resourceNode =
            VirtualAppContext.createAdminContext().callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/cms.yml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void getSiteNonExistedApp()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "non-app" );

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.getCmsDescriptor( applicationKey ) ) ).isNull();
    }

    @Test
    void updateSite()
        throws Exception
    {
        final String resource = readResource( "_cms.yml" );
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        createAdminContext().runWith( () -> dynamicSchemaService.createCms(
            CreateDynamicCmsParams.create().key( applicationKey ).resource( VirtualAppConstants.CMS_DESCRIPTOR_DEFAULT_VALUE ).build() ) );

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
        assertEquals( "myapp:/cms/cms.yml", result.getResource().getKey().toString() );

        final Node resourceNode =
            VirtualAppContext.createAdminContext().callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/cms.yml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void updateNotCreatedSite()
        throws Exception
    {
        final String resource = readResource( "_cms.yml" );
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
        assertEquals( "myapp:/cms/cms.yml", result.getResource().getKey().toString() );

        final Node resourceNode =
            VirtualAppContext.createAdminContext().callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/cms.yml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void deleteCms()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        createAdminContext().callWith( () -> dynamicSchemaService.getCmsDescriptor( applicationKey ) );

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.getCmsDescriptor( applicationKey ) ) ).isNotNull();

        createAdminContext().callWith( () -> dynamicSchemaService.createCms(
            CreateDynamicCmsParams.create().key( applicationKey ).resource( readResource( "_cms.yml" ) ).build() ) );

        DynamicSchemaResult<CmsDescriptor> cmsDescriptorResult =
            createAdminContext().callWith( () -> dynamicSchemaService.getCmsDescriptor( applicationKey ) );

        assertThat( cmsDescriptorResult.getSchema().getForm() ).isNotEmpty();
        assertThat( cmsDescriptorResult.getSchema().getMixinMappings() ).isNotEmpty();

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.deleteCms( applicationKey ) ) ).isTrue();

        cmsDescriptorResult = createAdminContext().callWith( () -> dynamicSchemaService.getCmsDescriptor( applicationKey ) );

        assertThat( cmsDescriptorResult.getSchema().getForm() ).isEmpty();
        assertThat( cmsDescriptorResult.getSchema().getMixinMappings() ).isEmpty();
    }

    @Test
    void createStyles()
        throws Exception
    {
        final String resource = readResource( "_styles.yml" );
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
        assertEquals( "myapp:/cms/styles/image.yml", result.getResource().getKey().toString() );
        assertNotNull( styleDescriptor.getModifiedTime() );

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/styles/image.yml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void updateStyles()
        throws Exception
    {
        final String resource = readResource( "_styles.yml" );
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        createAdminContext().callWith( () -> dynamicSchemaService.createStyles(
            CreateDynamicStylesParams.create().key( applicationKey ).resource( "css: \"assets/styles.css\"" ).build() ) );

        final DynamicSchemaResult<StyleDescriptor> result = createAdminContext().callWith( () -> dynamicSchemaService.updateStyles(
            UpdateDynamicStylesParams.create().key( applicationKey ).resource( resource ).build() ) );

        final StyleDescriptor styleDescriptor = result.getSchema();

        createAdminContext().runWith( () -> assertThat( styleDescriptor ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getStyles( applicationKey ).getSchema() ) );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/styles/image.yml", result.getResource().getKey().toString() );

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/styles/image.yml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void deleteStyles()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.getStyles( applicationKey ) ) ).isNull();

        createAdminContext().callWith( () -> dynamicSchemaService.createStyles(
            CreateDynamicStylesParams.create().key( applicationKey ).resource( readResource( "_styles.yml" ) ).build() ) );

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
                .resource( readResource( "_part.yml" ) )
                .type( DynamicComponentType.PART )
                .build() ) );
        DynamicSchemaResult<PartDescriptor> part2 = createAdminContext().callWith( () -> dynamicSchemaService.createComponent(
            CreateDynamicComponentParams.create()
                .descriptorKey( DescriptorKey.from( "myapp:mypart2" ) )
                .resource( readResource( "_part.yml" ) )
                .type( DynamicComponentType.PART )
                .build() ) );
        DynamicSchemaResult<PartDescriptor> part3 = createAdminContext().callWith( () -> dynamicSchemaService.createComponent(
            CreateDynamicComponentParams.create()
                .descriptorKey( DescriptorKey.from( "my-other-app:mypart" ) )
                .resource( readResource( "_part.yml" ) )
                .type( DynamicComponentType.PART )
                .build() ) );

        results = createAdminContext().callWith( () -> dynamicSchemaService.listComponents(
            ListDynamicComponentsParams.create().applicationKey( applicationKey ).type( DynamicComponentType.PART ).build() ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( part1, part2 ) );

        results = createAdminContext().callWith( () -> dynamicSchemaService.listComponents( ListDynamicComponentsParams.create()
                                                                                                .applicationKey(
                                                                                                    ApplicationKey.from( "my-other-app" ) )
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
                .resource( readResource( "_contentType.yml" ) )
                .type( DynamicContentSchemaType.CONTENT_TYPE )
                .build() ) );
        DynamicSchemaResult<ContentType> contentType2 = createAdminContext().callWith( () -> dynamicSchemaService.createContentSchema(
            CreateDynamicContentSchemaParams.create()
                .name( ContentTypeName.from( "myapp:mytype2" ) )
                .resource( readResource( "_contentType.yml" ) )
                .type( DynamicContentSchemaType.CONTENT_TYPE )
                .build() ) );
        DynamicSchemaResult<ContentType> contentType3 = createAdminContext().callWith( () -> dynamicSchemaService.createContentSchema(
            CreateDynamicContentSchemaParams.create()
                .name( ContentTypeName.from( "my-other-app:mytype" ) )
                .resource( readResource( "_contentType.yml" ) )
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
                                                                                                        "my-other-app" ) )
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
            ListDynamicContentSchemasParams.create().applicationKey( applicationKey ).type( DynamicContentSchemaType.FORM_FRAGMENT ).build() ) );

        assertTrue( results.isEmpty() );

        DynamicSchemaResult<FormFragmentDescriptor> fragment1 = createAdminContext().callWith( () -> dynamicSchemaService.createContentSchema(
            CreateDynamicContentSchemaParams.create()
                .name( FormFragmentName.from( "myapp:mytype1" ) )
                .resource( readResource( "_formFragment.yml" ) )
                .type( DynamicContentSchemaType.FORM_FRAGMENT )
                .build() ) );
        DynamicSchemaResult<FormFragmentDescriptor> fragment2 = createAdminContext().callWith( () -> dynamicSchemaService.createContentSchema(
            CreateDynamicContentSchemaParams.create()
                .name( FormFragmentName.from( "myapp:mytype2" ) )
                .resource( readResource( "_formFragment.yml" ) )
                .type( DynamicContentSchemaType.FORM_FRAGMENT )
                .build() ) );
        DynamicSchemaResult<FormFragmentDescriptor> fragment3 = createAdminContext().callWith( () -> dynamicSchemaService.createContentSchema(
            CreateDynamicContentSchemaParams.create()
                .name( FormFragmentName.from( "my-other-app:mytype" ) )
                .resource( readResource( "_formFragment.yml" ) )
                .type( DynamicContentSchemaType.FORM_FRAGMENT )
                .build() ) );

        results = createAdminContext().callWith( () -> dynamicSchemaService.listContentSchemas(
            ListDynamicContentSchemasParams.create().applicationKey( applicationKey ).type( DynamicContentSchemaType.FORM_FRAGMENT ).build() ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( fragment1, fragment2 ) );

        results = createAdminContext().callWith( () -> dynamicSchemaService.listContentSchemas( ListDynamicContentSchemasParams.create()
                                                                                                    .applicationKey( ApplicationKey.from(
                                                                                                        "my-other-app" ) )
                                                                                                    .type( DynamicContentSchemaType.FORM_FRAGMENT )
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

        DynamicSchemaResult<FormFragmentDescriptor> fragment1 = createAdminContext().callWith( () -> dynamicSchemaService.createContentSchema(
            CreateDynamicContentSchemaParams.create()
                .name( FormFragmentName.from( "myapp:mytype1" ) )
                .resource( readResource( "_formFragment.yml" ) )
                .type( DynamicContentSchemaType.FORM_FRAGMENT )
                .build() ) );
        DynamicSchemaResult<FormFragmentDescriptor> fragment2 = createAdminContext().callWith( () -> dynamicSchemaService.createContentSchema(
            CreateDynamicContentSchemaParams.create()
                .name( FormFragmentName.from( "myapp:mytype2" ) )
                .resource( readResource( "_formFragment.yml" ) )
                .type( DynamicContentSchemaType.FORM_FRAGMENT )
                .build() ) );
        DynamicSchemaResult<FormFragmentDescriptor> fragment3 = createAdminContext().callWith( () -> dynamicSchemaService.createContentSchema(
            CreateDynamicContentSchemaParams.create()
                .name( FormFragmentName.from( "my-other-app:mytype" ) )
                .resource( readResource( "_formFragment.yml" ) )
                .type( DynamicContentSchemaType.FORM_FRAGMENT )
                .build() ) );

        results = createAdminContext().callWith( () -> dynamicSchemaService.listContentSchemas(
            ListDynamicContentSchemasParams.create().applicationKey( applicationKey ).type( DynamicContentSchemaType.FORM_FRAGMENT ).build() ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( fragment1, fragment2 ) );

        results = createAdminContext().callWith( () -> dynamicSchemaService.listContentSchemas( ListDynamicContentSchemasParams.create()
                                                                                                    .applicationKey( ApplicationKey.from(
                                                                                                        "my-other-app" ) )
                                                                                                    .type( DynamicContentSchemaType.FORM_FRAGMENT )
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
                                                                                           .resource( readResource( "_formFragment.yml" ) )
                                                                                           .type( DynamicContentSchemaType.FORM_FRAGMENT )
                                                                                           .build() ) );
        createAdminContext().callWith( () -> dynamicSchemaService.createContentSchema( CreateDynamicContentSchemaParams.create()
                                                                                           .name( FormFragmentName.from( "myapp:mytype2" ) )
                                                                                           .resource( readResource( "_formFragment.yml" ) )
                                                                                           .type( DynamicContentSchemaType.FORM_FRAGMENT )
                                                                                           .build() ) );
        createAdminContext().callWith( () -> dynamicSchemaService.createContentSchema( CreateDynamicContentSchemaParams.create()
                                                                                           .name( FormFragmentName.from( "my-other-app:mytype" ) )
                                                                                           .resource( readResource( "_formFragment.yml" ) )
                                                                                           .type( DynamicContentSchemaType.FORM_FRAGMENT )
                                                                                           .build() ) );

        assertThrows( ForbiddenAccessException.class, () -> VirtualAppContext.createContext()
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
                .resource( readResource( "_mixin.yml" ) )
                .type( DynamicContentSchemaType.MIXIN )
                .build() ) );
        DynamicSchemaResult<MixinDescriptor> mixin2 = createAdminContext().callWith( () -> dynamicSchemaService.createContentSchema(
            CreateDynamicContentSchemaParams.create()
                .name( MixinName.from( "myapp:mytype2" ) )
                .resource( readResource( "_mixin.yml" ) )
                .type( DynamicContentSchemaType.MIXIN )
                .build() ) );
        DynamicSchemaResult<MixinDescriptor> mixin3 = createAdminContext().callWith( () -> dynamicSchemaService.createContentSchema(
            CreateDynamicContentSchemaParams.create()
                .name( MixinName.from( "my-other-app:mytype" ) )
                .resource( readResource( "_mixin.yml" ) )
                .type( DynamicContentSchemaType.MIXIN )
                .build() ) );

        results = createAdminContext().callWith( () -> dynamicSchemaService.listContentSchemas(
            ListDynamicContentSchemasParams.create().applicationKey( applicationKey ).type( DynamicContentSchemaType.MIXIN ).build() ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( mixin1, mixin2 ) );

        results = createAdminContext().callWith( () -> dynamicSchemaService.listContentSchemas( ListDynamicContentSchemasParams.create()
                                                                                                    .applicationKey( ApplicationKey.from(
                                                                                                        "my-other-app" ) )
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
                .resource( readResource( "_contentType.yml" ) )
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
                .resource( readResource( "_contentType.yml" ) )
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
                .resource( readResource( "_contentType.yml" ) )
                .type( DynamicContentSchemaType.CONTENT_TYPE )
                .build() ) );

        assertThrows( ForbiddenAccessException.class, () -> VirtualAppContext.createContext()
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
                .resource( readResource( "_part.yml" ) )
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
        final String resource = "unsupportedField: [ ]";

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
        final String resource = "unsupportedField: [ ]";

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
        final String resource = "unsupportedField: [ ]";

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
        final String resource = "unsupportedField: [ ]";

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
        final String resource = "unsupportedField: [ ]";

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
        final String resource = "unsupportedField: [ ]";

        final CreateDynamicStylesParams params =
            CreateDynamicStylesParams.create().key( ApplicationKey.from( "myapp" ) ).resource( resource ).build();

        assertThrows( Exception.class, () -> createAdminContext().callWith( () -> dynamicSchemaService.createStyles( params ) ) );
    }

    @Test
    void createSiteInvalid()
    {
        final String resource = "unsupportedField: [ ]";

        final CreateDynamicCmsParams params =
            CreateDynamicCmsParams.create().key( ApplicationKey.from( "myapp" ) ).resource( resource ).build();

        assertThrows( Exception.class, () -> createAdminContext().callWith( () -> dynamicSchemaService.createCms( params ) ) );
    }


    private Felix createFelixInstance( final Path cacheDir )
    {
        Map<String, Object> config = new HashMap<>();
        config.put( Constants.FRAMEWORK_STORAGE, cacheDir.toString() );
        config.put( Constants.FRAMEWORK_STORAGE_CLEAN, Constants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT );

        return new Felix( config );
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
