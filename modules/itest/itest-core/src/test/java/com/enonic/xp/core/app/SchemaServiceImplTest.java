package com.enonic.xp.core.app;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.CreateNamespaceParams;
import com.enonic.xp.app.Namespace;
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
import com.enonic.xp.core.impl.app.SchemaServiceImpl;
import com.enonic.xp.core.impl.app.VirtualAppConstants;
import com.enonic.xp.core.impl.app.VirtualAppContext;
import com.enonic.xp.core.impl.app.VirtualAppInitializer;
import com.enonic.xp.core.impl.app.VirtualAppService;
import com.enonic.xp.core.impl.app.resource.ResourceServiceImpl;
import com.enonic.xp.core.impl.event.EventPublisherImpl;
import com.enonic.xp.core.impl.project.ProjectConfig;
import com.enonic.xp.core.impl.project.ProjectServiceImpl;
import com.enonic.xp.core.impl.security.PasswordSecurityService;
import com.enonic.xp.core.impl.security.SecurityAuditLogSupportImpl;
import com.enonic.xp.core.impl.security.SecurityConfig;
import com.enonic.xp.core.impl.security.SecurityInitializer;
import com.enonic.xp.core.impl.security.SecurityServiceImpl;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.exception.ForbiddenAccessException;
import com.enonic.xp.internal.blobstore.MemoryBlobStore;
import com.enonic.xp.itest.AbstractElasticsearchIntegrationTest;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.project.CreateProjectParams;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectRole;
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
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.CreateDynamicComponentParams;
import com.enonic.xp.resource.CreateDynamicContentSchemaParams;
import com.enonic.xp.resource.CreateDynamicMacroParams;
import com.enonic.xp.resource.CreateDynamicStylesParams;
import com.enonic.xp.resource.DeleteDynamicComponentParams;
import com.enonic.xp.resource.DeleteDynamicContentSchemaParams;
import com.enonic.xp.resource.DeleteDynamicMacroParams;
import com.enonic.xp.resource.DynamicComponentType;
import com.enonic.xp.resource.DynamicContentSchemaType;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.GetDynamicComponentParams;
import com.enonic.xp.resource.GetDynamicContentSchemaParams;
import com.enonic.xp.resource.GetDynamicMacroParams;
import com.enonic.xp.resource.ListDynamicComponentsParams;
import com.enonic.xp.resource.ListDynamicContentSchemasParams;
import com.enonic.xp.resource.ListDynamicMacrosParams;
import com.enonic.xp.resource.UpdateDynamicCmsParams;
import com.enonic.xp.resource.UpdateDynamicComponentParams;
import com.enonic.xp.resource.UpdateDynamicContentSchemaParams;
import com.enonic.xp.resource.UpdateDynamicMacroParams;
import com.enonic.xp.resource.UpdateDynamicStylesParams;
import com.enonic.xp.schema.BaseSchema;
import com.enonic.xp.core.impl.site.CmsServiceImpl;
import com.enonic.xp.schema.content.CmsFormFragmentService;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.formfragment.FormFragmentDescriptor;
import com.enonic.xp.schema.formfragment.FormFragmentName;
import com.enonic.xp.schema.mixin.MixinDescriptor;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.site.CmsDescriptor;
import com.enonic.xp.style.StyleDescriptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SchemaServiceImplTest
    extends AbstractElasticsearchIntegrationTest
{
    NodeServiceImpl nodeService;

    private SchemaServiceImpl schemaService;

    private ResourceServiceImpl resourceService;

    private ApplicationService applicationService;

    private ProjectServiceImpl projectService;

    private Felix felix;

    @TempDir
    private Path felixTempFolder;

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

    private static Context createContentManagerAdminContext()
    {
        return ContextBuilder.copyOf( ctxDefault() )
            .authInfo( AuthenticationInfo.create()
                           .principals( RoleKeys.AUTHENTICATED, RoleKeys.CONTENT_MANAGER_ADMIN )
                           .user( User.anonymous() )
                           .build() )
            .build();
    }

    private static Context createProjectRoleContext( final ProjectRole projectRole )
    {
        final PrincipalKey projectRoleKey = PrincipalKey.ofRole(
            ProjectConstants.PROJECT_NAME_PREFIX + "my-project" + "." + projectRole.name().toLowerCase() );
        return ContextBuilder.copyOf( ctxDefault() )
            .authInfo(
                AuthenticationInfo.create().principals( RoleKeys.AUTHENTICATED, projectRoleKey ).user( User.anonymous() ).build() )
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

        Path cacheDir = Files.createDirectory( this.felixTempFolder.resolve( "cache" ) ).toAbsolutePath();

        felix = createFelixInstance( cacheDir );
        felix.start();

        ApplicationRepoServiceImpl repoService = new ApplicationRepoServiceImpl( nodeService );
        ApplicationRepoInitializer.create().setIndexService( indexService ).setNodeService( nodeService ).build().initialize();

        BundleContext bundleContext = felix.getBundleContext();

        AppConfig appConfig = mock( AppConfig.class, invocation -> invocation.getMethod().getDefaultValue() );

        ApplicationFactoryServiceImpl applicationFactoryService = new ApplicationFactoryServiceImpl( bundleContext, nodeService );
        applicationFactoryService.activate();

        this.resourceService = new ResourceServiceImpl( applicationFactoryService );

        AppFilterService appFilterService = new AppFilterServiceImpl( appConfig );

        ApplicationRegistry applicationRegistry =
            new ApplicationRegistryImpl( bundleContext, new ApplicationListenerHub(), applicationFactoryService );

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

        final VirtualAppService virtualAppService = new VirtualAppService( nodeService );
        VirtualAppInitializer.create().setIndexService( indexService ).setRepositoryService( repositoryService ).build().initialize();

        this.schemaService = new SchemaServiceImpl( nodeService, resourceService, applicationRegistry, virtualAppService );

        applicationService = new ApplicationServiceImpl( applicationRegistry, repoService, eventPublisher, appFilterService,
                                                         virtualAppService,
                                                         new ApplicationAuditLogSupportImpl( mock( AuditLogService.class ) ) );

        createSchemaAdminContext().runWith( () -> schemaService.createNamespace(
            CreateNamespaceParams.create().key( ApplicationKey.from( "myapp" ) ).build() ) );

        createAdminContext().runWith( () -> schemaService.createNamespace(
            CreateNamespaceParams.create().key( ApplicationKey.from( "my_other_app" ) ).build() ) );

        projectService =
            new ProjectServiceImpl( repositoryService, repositoryService, indexService, nodeService, securityService, eventPublisher,
                                    mock( ProjectConfig.class ) );
        projectService.initialize();

        createAdminContext().runWith( () -> projectService.create(
            CreateProjectParams.create().name( ProjectName.from( "my-project" ) ).displayName( "test" ).build() ) );

    }

    @AfterEach
    void stopFelix()
        throws Exception
    {
        if ( felix != null )
        {
            felix.stop();
            felix.waitForStop( 10000 );
        }
    }

    @Test
    void namespace_cms_descriptor()
    {
        final CmsFormFragmentService formFragmentService = mock( CmsFormFragmentService.class );
        when( formFragmentService.inlineFormItems( org.mockito.ArgumentMatchers.any() ) ).thenAnswer( inv -> inv.getArgument( 0 ) );

        final CmsServiceImpl cmsService = new CmsServiceImpl( resourceService, formFragmentService );

        final CmsDescriptor descriptor =
            createAdminContext().callWith( () -> cmsService.getDescriptor( ApplicationKey.from( "myapp" ) ) );

        assertNotNull( descriptor );
        assertEquals( ApplicationKey.from( "myapp" ), descriptor.getApplicationKey() );
    }

    @Test
    void namespace_cms_yaml_resource_admin()
    {
        final Resource resource = createAdminContext().callWith(
            () -> resourceService.getResource( ResourceKey.from( ApplicationKey.from( "myapp" ), "cms/cms.yaml" ) ) );

        assertTrue( resource.exists() );
    }

    @Test
    void namespace_cms_yaml_resource_non_admin()
    {
        final Resource resource = createContentManagerAdminContext().callWith(
            () -> resourceService.getResource( ResourceKey.from( ApplicationKey.from( "myapp" ), "cms/cms.yaml" ) ) );

        assertTrue( resource.exists() );
    }

    @Test
    void namespace_cms_yaml_resource_unauthenticated()
    {
        final Resource resource = ContextBuilder.copyOf( ctxDefault() )
            .authInfo( AuthenticationInfo.unAuthenticated() )
            .build()
            .callWith( () -> resourceService.getResource( ResourceKey.from( ApplicationKey.from( "myapp" ), "cms/cms.yaml" ) ) );

        assertTrue( resource.exists() );
    }

    @Test
    void get_namespace()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        final Namespace result = createAdminContext().callWith( () -> schemaService.getNamespace( applicationKey ) );

        assertNotNull( result );
        assertEquals( applicationKey, result.getKey() );
    }

    @Test
    void get_namespace_not_found()
    {
        final Namespace result = createAdminContext().callWith( () -> schemaService.getNamespace( ApplicationKey.from( "nonexistent" ) ) );

        assertNull( result );
    }

    @Test
    void list_application_keys()
    {
        final ApplicationKeys result = createAdminContext().callWith( schemaService::listApplicationKeys );

        assertThat( result ).contains( ApplicationKey.from( "myapp" ), ApplicationKey.from( "my_other_app" ) );
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
            createAdminContext().callWith( () -> schemaService.createContentSchema( params ) );

        final ContentType contentType = (ContentType) result.getSchema();

        createAdminContext().runWith( () -> assertThat( contentType ).usingRecursiveComparison()
            .isEqualTo( schemaService.getContentSchema( GetDynamicContentSchemaParams.create()
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

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/content-types/mytype/mytype.yaml" ) ) );

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

        createAdminContext().runWith( () -> schemaService.createContentSchema( createParams ) );

        final String resource = readResource( "_contentType.yaml" );

        final UpdateDynamicContentSchemaParams updateParams = UpdateDynamicContentSchemaParams.create()
            .name( ContentTypeName.from( "myapp:mytype" ) )
            .resource( resource )
            .type( DynamicContentSchemaType.CONTENT_TYPE )
            .build();

        final DynamicSchemaResult<BaseSchema<?>> result =
            createAdminContext().callWith( () -> schemaService.updateContentSchema( updateParams ) );

        final ContentType contentType = (ContentType) result.getSchema();

        createAdminContext().runWith( () -> assertThat( contentType ).usingRecursiveComparison()
            .isEqualTo( schemaService.getContentSchema( GetDynamicContentSchemaParams.create()
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

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/content-types/mytype/mytype.yaml" ) ) );

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
            createAdminContext().callWith( () -> schemaService.createContentSchema( params ) );

        final FormFragmentDescriptor fragment = (FormFragmentDescriptor) result.getSchema();

        createAdminContext().runWith( () -> assertThat( fragment ).usingRecursiveComparison()
            .isEqualTo( schemaService.getContentSchema( GetDynamicContentSchemaParams.create()
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

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/form-fragments/my-fragment/my-fragment.yaml" ) ) );

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
            createSchemaAdminContext().callWith( () -> schemaService.createContentSchema( params ) );

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
                      () -> VirtualAppContext.createContext().callWith( () -> schemaService.createContentSchema( params ) ) );
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

        createAdminContext().runWith( () -> schemaService.createContentSchema( createParams ) );

        final String resource = readResource( "_formFragment.yaml" );

        final UpdateDynamicContentSchemaParams updateParams = UpdateDynamicContentSchemaParams.create()
            .name( FormFragmentName.from( "myapp:my-fragment" ) )
            .resource( resource )
            .type( DynamicContentSchemaType.FORM_FRAGMENT )
            .build();

        final DynamicSchemaResult<BaseSchema<?>> result =
            createAdminContext().callWith( () -> schemaService.updateContentSchema( updateParams ) );

        final FormFragmentDescriptor fragment = (FormFragmentDescriptor) result.getSchema();

        createAdminContext().runWith( () -> assertThat( fragment ).usingRecursiveComparison()
            .isEqualTo( schemaService.getContentSchema( GetDynamicContentSchemaParams.create()
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

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/form-fragments/my-fragment/my-fragment.yaml" ) ) );

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

        createSchemaAdminContext().runWith( () -> schemaService.createContentSchema( createParams ) );

        final String resource = readResource( "_formFragment.yaml" );

        final UpdateDynamicContentSchemaParams updateParams = UpdateDynamicContentSchemaParams.create()
            .name( FormFragmentName.from( "myapp:my-fragment" ) )
            .resource( resource )
            .type( DynamicContentSchemaType.FORM_FRAGMENT )
            .build();

        final DynamicSchemaResult<BaseSchema<?>> result =
            createSchemaAdminContext().callWith( () -> schemaService.updateContentSchema( updateParams ) );

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

        createSchemaAdminContext().runWith( () -> schemaService.createContentSchema( createParams ) );

        final String resource = readResource( "_formFragment.yaml" );

        final UpdateDynamicContentSchemaParams updateParams = UpdateDynamicContentSchemaParams.create()
            .name( FormFragmentName.from( "myapp:my-fragment" ) )
            .resource( resource )
            .type( DynamicContentSchemaType.FORM_FRAGMENT )
            .build();

        assertThrows( ForbiddenAccessException.class,
                      () -> VirtualAppContext.createContext().callWith( () -> schemaService.updateContentSchema( updateParams ) ) );
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
            createAdminContext().callWith( () -> schemaService.createContentSchema( params ) );

        final MixinDescriptor mixinDescriptor = (MixinDescriptor) result.getSchema();

        createAdminContext().runWith( () -> assertThat( mixinDescriptor ).usingRecursiveComparison()
            .isEqualTo( schemaService.getContentSchema( GetDynamicContentSchemaParams.create()
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

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/mixins/mymixin/mymixin.yaml" ) ) );

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

        createAdminContext().runWith( () -> schemaService.createContentSchema( createParams ) );

        final String resource = readResource( "_mixin.yaml" );

        final UpdateDynamicContentSchemaParams updateParams = UpdateDynamicContentSchemaParams.create()
            .name( MixinName.from( "myapp:mymixin" ) )
            .resource( resource )
            .type( DynamicContentSchemaType.MIXIN )
            .build();

        final DynamicSchemaResult<BaseSchema<?>> result =
            createAdminContext().callWith( () -> schemaService.updateContentSchema( updateParams ) );

        final MixinDescriptor mixinDescriptor = (MixinDescriptor) result.getSchema();

        createAdminContext().runWith( () -> assertThat( mixinDescriptor ).usingRecursiveComparison()
            .isEqualTo( schemaService.getContentSchema( GetDynamicContentSchemaParams.create()
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

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/mixins/mymixin/mymixin.yaml" ) ) );

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
            createAdminContext().callWith( () -> schemaService.createComponent( params ) );

        final PartDescriptor partDescriptor = (PartDescriptor) result.getSchema();

        createAdminContext().runWith( () -> assertThat( partDescriptor ).usingRecursiveComparison()
            .isEqualTo( schemaService.getComponent( GetDynamicComponentParams.create()
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

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/parts/mypart/mypart.yaml" ) ) );

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

        createAdminContext().runWith( () -> schemaService.createComponent( createParams ) );

        final String resource = readResource( "_part.yaml" );

        final UpdateDynamicComponentParams updateParams = UpdateDynamicComponentParams.create()
            .descriptorKey( DescriptorKey.from( "myapp:mypart" ) )
            .resource( resource )
            .type( DynamicComponentType.PART )
            .build();

        final DynamicSchemaResult<ComponentDescriptor> result =
            createAdminContext().callWith( () -> schemaService.updateComponent( updateParams ) );

        final PartDescriptor partDescriptor = (PartDescriptor) result.getSchema();

        createAdminContext().runWith( () -> assertThat( partDescriptor ).usingRecursiveComparison()
            .isEqualTo( schemaService.getComponent( GetDynamicComponentParams.create()
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

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/parts/mypart/mypart.yaml" ) ) );

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
            createAdminContext().callWith( () -> schemaService.createComponent( params ) );

        final LayoutDescriptor layoutDescriptor = (LayoutDescriptor) result.getSchema();

        createAdminContext().runWith( () -> assertThat( layoutDescriptor ).usingRecursiveComparison()
            .isEqualTo( schemaService.getComponent( GetDynamicComponentParams.create()
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

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/layouts/mylayout/mylayout.yaml" ) ) );

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

        createAdminContext().runWith( () -> schemaService.createComponent( params ) );

        final String resource = readResource( "_layout.yaml" );

        final UpdateDynamicComponentParams updateParams = UpdateDynamicComponentParams.create()
            .descriptorKey( DescriptorKey.from( "myapp:mylayout" ) )
            .resource( resource )
            .type( DynamicComponentType.LAYOUT )
            .build();

        final DynamicSchemaResult<ComponentDescriptor> result =
            createAdminContext().callWith( () -> schemaService.updateComponent( updateParams ) );

        final LayoutDescriptor layoutDescriptor = (LayoutDescriptor) result.getSchema();

        createAdminContext().runWith( () -> assertThat( layoutDescriptor ).usingRecursiveComparison()
            .isEqualTo( schemaService.getComponent( GetDynamicComponentParams.create()
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

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/layouts/mylayout/mylayout.yaml" ) ) );

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
            createAdminContext().callWith( () -> schemaService.createComponent( params ) );

        final PageDescriptor pageDescriptor = (PageDescriptor) result.getSchema();

        createAdminContext().runWith( () -> assertThat( pageDescriptor ).usingRecursiveComparison()
            .isEqualTo( schemaService.getComponent( GetDynamicComponentParams.create()
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

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/pages/mypage/mypage.yaml" ) ) );

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

        createAdminContext().runWith( () -> schemaService.createComponent( createParams ) );

        final String resource = readResource( "_page.yaml" );

        final UpdateDynamicComponentParams updateParams = UpdateDynamicComponentParams.create()
            .descriptorKey( DescriptorKey.from( "myapp:mypage" ) )
            .resource( resource )
            .type( DynamicComponentType.PAGE )
            .build();

        final DynamicSchemaResult<ComponentDescriptor> result =
            createAdminContext().callWith( () -> schemaService.updateComponent( updateParams ) );

        final PageDescriptor pageDescriptor = (PageDescriptor) result.getSchema();

        createAdminContext().runWith( () -> assertThat( pageDescriptor ).usingRecursiveComparison()
            .isEqualTo( schemaService.getComponent( GetDynamicComponentParams.create()
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

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/pages/mypage/mypage.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void createSite()
        throws Exception
    {
        final String resource = readResource( "_cms.yaml" );
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        assertThat( createAdminContext().callWith( () -> schemaService.getCmsDescriptor( applicationKey ) ) ).isNotNull();

        final DynamicSchemaResult<CmsDescriptor> result = createAdminContext().callWith(
            () -> schemaService.createCms( CreateDynamicCmsParams.create().key( applicationKey ).resource( resource ).build() ) );

        final CmsDescriptor cmsDescriptor = result.getSchema();

        createAdminContext().runWith( () -> assertThat( cmsDescriptor ).usingRecursiveComparison(
                RecursiveComparisonConfiguration.builder().withIgnoredFields( "mappingDescriptors" ).build() )
            .isEqualTo( schemaService.getCmsDescriptor( applicationKey ).getSchema() ) );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/cms.yaml", result.getResource().getKey().toString() );
        assertNotNull( cmsDescriptor.getModifiedTime() );

        final Node resourceNode =
            VirtualAppContext.createAdminContext().callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/cms.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void getSiteNonExistedApp()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "nonapp" );

        assertThat( createAdminContext().callWith( () -> schemaService.getCmsDescriptor( applicationKey ) ) ).isNull();
    }

    @Test
    void updateSite()
        throws Exception
    {
        final String resource = readResource( "_cms.yaml" );
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        createAdminContext().runWith( () -> schemaService.createCms(
            CreateDynamicCmsParams.create().key( applicationKey ).resource( VirtualAppConstants.CMS_DESCRIPTOR_DEFAULT_VALUE ).build() ) );

        final DynamicSchemaResult<CmsDescriptor> result = createAdminContext().callWith(
            () -> schemaService.updateCms( UpdateDynamicCmsParams.create().key( applicationKey ).resource( resource ).build() ) );

        final CmsDescriptor cmsDescriptor = result.getSchema();

        createAdminContext().runWith( () -> assertThat( cmsDescriptor ).usingRecursiveComparison(
                RecursiveComparisonConfiguration.builder().withIgnoredFields( "mappingDescriptors" ).build() )
            .isEqualTo( schemaService.getCmsDescriptor( applicationKey ).getSchema() ) );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/cms.yaml", result.getResource().getKey().toString() );

        final Node resourceNode =
            VirtualAppContext.createAdminContext().callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/cms.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void updateNotCreatedSite()
        throws Exception
    {
        final String resource = readResource( "_cms.yaml" );
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        final DynamicSchemaResult<CmsDescriptor> result = createAdminContext().callWith(
            () -> schemaService.updateCms( UpdateDynamicCmsParams.create().key( applicationKey ).resource( resource ).build() ) );

        final CmsDescriptor cmsDescriptor = result.getSchema();

        createAdminContext().runWith( () -> assertThat( cmsDescriptor ).usingRecursiveComparison(
                RecursiveComparisonConfiguration.builder().withIgnoredFields( "mappingDescriptors" ).build() )
            .isEqualTo( schemaService.getCmsDescriptor( applicationKey ).getSchema() ) );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/cms.yaml", result.getResource().getKey().toString() );

        final Node resourceNode =
            VirtualAppContext.createAdminContext().callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/cms.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void deleteCms()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        createAdminContext().callWith( () -> schemaService.getCmsDescriptor( applicationKey ) );

        assertThat( createAdminContext().callWith( () -> schemaService.getCmsDescriptor( applicationKey ) ) ).isNotNull();

        createAdminContext().callWith( () -> schemaService.createCms(
            CreateDynamicCmsParams.create().key( applicationKey ).resource( readResource( "_cms.yaml" ) ).build() ) );

        DynamicSchemaResult<CmsDescriptor> cmsDescriptorResult =
            createAdminContext().callWith( () -> schemaService.getCmsDescriptor( applicationKey ) );

        assertThat( cmsDescriptorResult.getSchema().getForm() ).isNotEmpty();
        assertThat( cmsDescriptorResult.getSchema().getMixinMappings() ).isNotEmpty();

        assertThat( createAdminContext().callWith( () -> schemaService.deleteCms( applicationKey ) ) ).isTrue();

        cmsDescriptorResult = createAdminContext().callWith( () -> schemaService.getCmsDescriptor( applicationKey ) );

        assertThat( cmsDescriptorResult.getSchema().getForm() ).isEmpty();
        assertThat( cmsDescriptorResult.getSchema().getMixinMappings() ).isEmpty();
    }

    @Test
    void createStyles()
        throws Exception
    {
        final String resource = readResource( "_styles.yaml" );
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        assertThat( createAdminContext().callWith( () -> schemaService.getStyles( applicationKey ) ) ).isNull();

        final DynamicSchemaResult<StyleDescriptor> result = createAdminContext().callWith( () -> schemaService.createStyles(
            CreateDynamicStylesParams.create().key( applicationKey ).resource( resource ).build() ) );

        final StyleDescriptor styleDescriptor = result.getSchema();

        createAdminContext().runWith( () -> assertThat( styleDescriptor ).usingRecursiveComparison()
            .isEqualTo( schemaService.getStyles( applicationKey ).getSchema() ) );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/style/style.yaml", result.getResource().getKey().toString() );
        assertNotNull( styleDescriptor.getModifiedTime() );

        final Node resourceNode =
            VirtualAppContext.createAdminContext().callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/style/style.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void updateStyles()
        throws Exception
    {
        final String resource = readResource( "_styles.yaml" );
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        createAdminContext().callWith( () -> schemaService.createStyles(
            CreateDynamicStylesParams.create().key( applicationKey ).resource( "kind: \"Style\"\n" ).build() ) );

        final DynamicSchemaResult<StyleDescriptor> result = createAdminContext().callWith( () -> schemaService.updateStyles(
            UpdateDynamicStylesParams.create().key( applicationKey ).resource( resource ).build() ) );

        final StyleDescriptor styleDescriptor = result.getSchema();

        createAdminContext().runWith( () -> assertThat( styleDescriptor ).usingRecursiveComparison()
            .isEqualTo( schemaService.getStyles( applicationKey ).getSchema() ) );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/style/style.yaml", result.getResource().getKey().toString() );

        final Node resourceNode =
            VirtualAppContext.createAdminContext().callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/style/style.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void deleteStyles()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        assertThat( createAdminContext().callWith( () -> schemaService.getStyles( applicationKey ) ) ).isNull();

        createAdminContext().callWith( () -> schemaService.createStyles(
            CreateDynamicStylesParams.create().key( applicationKey ).resource( readResource( "_styles.yaml" ) ).build() ) );

        assertThat( createAdminContext().callWith( () -> schemaService.getStyles( applicationKey ) ) ).isNotNull();

        assertThat( createAdminContext().callWith( () -> schemaService.deleteStyles( applicationKey ) ) ).isTrue();

        assertThat( createAdminContext().callWith( () -> schemaService.getStyles( applicationKey ) ) ).isNull();
    }


    @Test
    void listPartComponents()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        List<DynamicSchemaResult<ComponentDescriptor>> results = createAdminContext().callWith( () -> schemaService.listComponents(
            ListDynamicComponentsParams.create().applicationKey( applicationKey ).type( DynamicComponentType.PART ).build() ) );

        assertTrue( results.isEmpty() );

        DynamicSchemaResult<PartDescriptor> part1 = createAdminContext().callWith( () -> schemaService.createComponent(
            CreateDynamicComponentParams.create()
                .descriptorKey( DescriptorKey.from( "myapp:mypart1" ) )
                .resource( readResource( "_part.yaml" ) )
                .type( DynamicComponentType.PART )
                .build() ) );
        DynamicSchemaResult<PartDescriptor> part2 = createAdminContext().callWith( () -> schemaService.createComponent(
            CreateDynamicComponentParams.create()
                .descriptorKey( DescriptorKey.from( "myapp:mypart2" ) )
                .resource( readResource( "_part.yaml" ) )
                .type( DynamicComponentType.PART )
                .build() ) );
        DynamicSchemaResult<PartDescriptor> part3 = createAdminContext().callWith( () -> schemaService.createComponent(
            CreateDynamicComponentParams.create()
                .descriptorKey( DescriptorKey.from( "my_other_app:mypart" ) )
                .resource( readResource( "_part.yaml" ) )
                .type( DynamicComponentType.PART )
                .build() ) );

        results = createAdminContext().callWith( () -> schemaService.listComponents(
            ListDynamicComponentsParams.create().applicationKey( applicationKey ).type( DynamicComponentType.PART ).build() ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( part1, part2 ) );

        results = createAdminContext().callWith( () -> schemaService.listComponents( ListDynamicComponentsParams.create()
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

        List<DynamicSchemaResult<BaseSchema<?>>> results = createAdminContext().callWith( () -> schemaService.listContentSchemas(
            ListDynamicContentSchemasParams.create()
                .applicationKey( applicationKey )
                .type( DynamicContentSchemaType.CONTENT_TYPE )
                .build() ) );

        assertTrue( results.isEmpty() );

        DynamicSchemaResult<ContentType> contentType1 = createAdminContext().callWith( () -> schemaService.createContentSchema(
            CreateDynamicContentSchemaParams.create()
                .name( ContentTypeName.from( "myapp:mytype1" ) )
                .resource( readResource( "_contentType.yaml" ) )
                .type( DynamicContentSchemaType.CONTENT_TYPE )
                .build() ) );
        DynamicSchemaResult<ContentType> contentType2 = createAdminContext().callWith( () -> schemaService.createContentSchema(
            CreateDynamicContentSchemaParams.create()
                .name( ContentTypeName.from( "myapp:mytype2" ) )
                .resource( readResource( "_contentType.yaml" ) )
                .type( DynamicContentSchemaType.CONTENT_TYPE )
                .build() ) );
        DynamicSchemaResult<ContentType> contentType3 = createAdminContext().callWith( () -> schemaService.createContentSchema(
            CreateDynamicContentSchemaParams.create()
                .name( ContentTypeName.from( "my_other_app:mytype" ) )
                .resource( readResource( "_contentType.yaml" ) )
                .type( DynamicContentSchemaType.CONTENT_TYPE )
                .build() ) );

        results = createAdminContext().callWith( () -> schemaService.listContentSchemas( ListDynamicContentSchemasParams.create()
                                                                                                    .applicationKey( applicationKey )
                                                                                                    .type(
                                                                                                        DynamicContentSchemaType.CONTENT_TYPE )
                                                                                                    .build() ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( contentType1, contentType2 ) );

        results = createAdminContext().callWith( () -> schemaService.listContentSchemas( ListDynamicContentSchemasParams.create()
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

        List<DynamicSchemaResult<BaseSchema<?>>> results = createAdminContext().callWith( () -> schemaService.listContentSchemas(
            ListDynamicContentSchemasParams.create()
                .applicationKey( applicationKey )
                .type( DynamicContentSchemaType.FORM_FRAGMENT )
                .build() ) );

        assertTrue( results.isEmpty() );

        DynamicSchemaResult<FormFragmentDescriptor> fragment1 = createAdminContext().callWith(
            () -> schemaService.createContentSchema( CreateDynamicContentSchemaParams.create()
                                                                .name( FormFragmentName.from( "myapp:mytype1" ) )
                                                                .resource( readResource( "_formFragment.yaml" ) )
                                                                .type( DynamicContentSchemaType.FORM_FRAGMENT )
                                                                .build() ) );
        DynamicSchemaResult<FormFragmentDescriptor> fragment2 = createAdminContext().callWith(
            () -> schemaService.createContentSchema( CreateDynamicContentSchemaParams.create()
                                                                .name( FormFragmentName.from( "myapp:mytype2" ) )
                                                                .resource( readResource( "_formFragment.yaml" ) )
                                                                .type( DynamicContentSchemaType.FORM_FRAGMENT )
                                                                .build() ) );
        DynamicSchemaResult<FormFragmentDescriptor> fragment3 = createAdminContext().callWith(
            () -> schemaService.createContentSchema( CreateDynamicContentSchemaParams.create()
                                                                .name( FormFragmentName.from( "my_other_app:mytype" ) )
                                                                .resource( readResource( "_formFragment.yaml" ) )
                                                                .type( DynamicContentSchemaType.FORM_FRAGMENT )
                                                                .build() ) );

        results = createAdminContext().callWith( () -> schemaService.listContentSchemas( ListDynamicContentSchemasParams.create()
                                                                                                    .applicationKey( applicationKey )
                                                                                                    .type(
                                                                                                        DynamicContentSchemaType.FORM_FRAGMENT )
                                                                                                    .build() ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( fragment1, fragment2 ) );

        results = createAdminContext().callWith( () -> schemaService.listContentSchemas( ListDynamicContentSchemasParams.create()
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
            () -> schemaService.listContentSchemas( ListDynamicContentSchemasParams.create()
                                                               .applicationKey( applicationKey )
                                                               .type( DynamicContentSchemaType.FORM_FRAGMENT )
                                                               .build() ) );

        assertTrue( results.isEmpty() );

        DynamicSchemaResult<FormFragmentDescriptor> fragment1 = createAdminContext().callWith(
            () -> schemaService.createContentSchema( CreateDynamicContentSchemaParams.create()
                                                                .name( FormFragmentName.from( "myapp:mytype1" ) )
                                                                .resource( readResource( "_formFragment.yaml" ) )
                                                                .type( DynamicContentSchemaType.FORM_FRAGMENT )
                                                                .build() ) );
        DynamicSchemaResult<FormFragmentDescriptor> fragment2 = createAdminContext().callWith(
            () -> schemaService.createContentSchema( CreateDynamicContentSchemaParams.create()
                                                                .name( FormFragmentName.from( "myapp:mytype2" ) )
                                                                .resource( readResource( "_formFragment.yaml" ) )
                                                                .type( DynamicContentSchemaType.FORM_FRAGMENT )
                                                                .build() ) );
        DynamicSchemaResult<FormFragmentDescriptor> fragment3 = createAdminContext().callWith(
            () -> schemaService.createContentSchema( CreateDynamicContentSchemaParams.create()
                                                                .name( FormFragmentName.from( "my_other_app:mytype" ) )
                                                                .resource( readResource( "_formFragment.yaml" ) )
                                                                .type( DynamicContentSchemaType.FORM_FRAGMENT )
                                                                .build() ) );

        results = createAdminContext().callWith( () -> schemaService.listContentSchemas( ListDynamicContentSchemasParams.create()
                                                                                                    .applicationKey( applicationKey )
                                                                                                    .type(
                                                                                                        DynamicContentSchemaType.FORM_FRAGMENT )
                                                                                                    .build() ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( fragment1, fragment2 ) );

        results = createAdminContext().callWith( () -> schemaService.listContentSchemas( ListDynamicContentSchemasParams.create()
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
            () -> schemaService.listContentSchemas( ListDynamicContentSchemasParams.create()
                                                               .applicationKey( applicationKey )
                                                               .type( DynamicContentSchemaType.FORM_FRAGMENT )
                                                               .build() ) );

        assertTrue( results.isEmpty() );

        createAdminContext().callWith( () -> schemaService.createContentSchema( CreateDynamicContentSchemaParams.create()
                                                                                           .name( FormFragmentName.from( "myapp:mytype1" ) )
                                                                                           .resource( readResource( "_formFragment.yaml" ) )
                                                                                           .type( DynamicContentSchemaType.FORM_FRAGMENT )
                                                                                           .build() ) );
        createAdminContext().callWith( () -> schemaService.createContentSchema( CreateDynamicContentSchemaParams.create()
                                                                                           .name( FormFragmentName.from( "myapp:mytype2" ) )
                                                                                           .resource( readResource( "_formFragment.yaml" ) )
                                                                                           .type( DynamicContentSchemaType.FORM_FRAGMENT )
                                                                                           .build() ) );
        createAdminContext().callWith( () -> schemaService.createContentSchema( CreateDynamicContentSchemaParams.create()
                                                                                           .name( FormFragmentName.from(
                                                                                               "my_other_app:mytype" ) )
                                                                                           .resource( readResource( "_formFragment.yaml" ) )
                                                                                           .type( DynamicContentSchemaType.FORM_FRAGMENT )
                                                                                           .build() ) );

        assertThrows( ForbiddenAccessException.class, () -> VirtualAppContext.createContext()
            .callWith( () -> schemaService.listContentSchemas( ListDynamicContentSchemasParams.create()
                                                                          .applicationKey( applicationKey )
                                                                          .type( DynamicContentSchemaType.FORM_FRAGMENT )
                                                                          .build() ) ) );
    }

    @Test
    void listFormFragmentsWithSiteConfigAccess()
    {
        final DynamicSchemaResult<FormFragmentDescriptor> fragment = createAdminContext().callWith(
            () -> schemaService.createContentSchema( CreateDynamicContentSchemaParams.create()
                                                                .name( FormFragmentName.from( "myapp:mytype1" ) )
                                                                .resource( readResource( "_formFragment.yaml" ) )
                                                                .type( DynamicContentSchemaType.FORM_FRAGMENT )
                                                                .build() ) );

        final ListDynamicContentSchemasParams params = ListDynamicContentSchemasParams.create()
            .applicationKey( ApplicationKey.from( "myapp" ) )
            .type( DynamicContentSchemaType.FORM_FRAGMENT )
            .build();

        List<DynamicSchemaResult<BaseSchema<?>>> results =
            createContentManagerAdminContext().callWith( () -> schemaService.listContentSchemas( params ) );
        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( fragment ) );

        results = createProjectRoleContext( ProjectRole.OWNER ).callWith( () -> schemaService.listContentSchemas( params ) );
        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( fragment ) );
    }

    @Test
    void listFormFragmentsAsNonOwnerProjectRole()
    {
        createAdminContext().callWith( () -> schemaService.createContentSchema( CreateDynamicContentSchemaParams.create()
                                                                                           .name( FormFragmentName.from( "myapp:mytype1" ) )
                                                                                           .resource( readResource( "_formFragment.yaml" ) )
                                                                                           .type( DynamicContentSchemaType.FORM_FRAGMENT )
                                                                                           .build() ) );

        final ListDynamicContentSchemasParams params = ListDynamicContentSchemasParams.create()
            .applicationKey( ApplicationKey.from( "myapp" ) )
            .type( DynamicContentSchemaType.FORM_FRAGMENT )
            .build();

        assertThrows( ForbiddenAccessException.class, () -> createProjectRoleContext( ProjectRole.VIEWER ).callWith(
            () -> schemaService.listContentSchemas( params ) ) );
        assertThrows( ForbiddenAccessException.class, () -> createProjectRoleContext( ProjectRole.EDITOR ).callWith(
            () -> schemaService.listContentSchemas( params ) ) );
    }

    @Test
    void getFormFragmentWithProjectRole()
    {
        final DynamicSchemaResult<FormFragmentDescriptor> fragment = createAdminContext().callWith(
            () -> schemaService.createContentSchema( CreateDynamicContentSchemaParams.create()
                                                                .name( FormFragmentName.from( "myapp:mytype1" ) )
                                                                .resource( readResource( "_formFragment.yaml" ) )
                                                                .type( DynamicContentSchemaType.FORM_FRAGMENT )
                                                                .build() ) );

        final GetDynamicContentSchemaParams params = GetDynamicContentSchemaParams.create()
            .name( FormFragmentName.from( "myapp:mytype1" ) )
            .type( DynamicContentSchemaType.FORM_FRAGMENT )
            .build();

        for ( final ProjectRole projectRole : ProjectRole.values() )
        {
            final DynamicSchemaResult<BaseSchema<?>> result =
                createProjectRoleContext( projectRole ).callWith( () -> schemaService.getContentSchema( params ) );
            assertThat( result ).usingRecursiveComparison().isEqualTo( fragment );
        }

        final DynamicSchemaResult<BaseSchema<?>> result =
            createContentManagerAdminContext().callWith( () -> schemaService.getContentSchema( params ) );
        assertThat( result ).usingRecursiveComparison().isEqualTo( fragment );

        assertThrows( ForbiddenAccessException.class, () -> VirtualAppContext.createContext()
            .callWith( () -> schemaService.getContentSchema( params ) ) );
    }

    @Test
    void installGlobalApplicationPersistsSchema()
        throws Exception
    {
        final String contentTypeResource = readResource( "_contentType.yaml" );
        final String cmsResource = "kind: \"CMS\"\nform: [ ]\n";

        final ByteSource app = createAppSource( "myglobalapp", "1.0.0",
                                                Map.of( "cms/cms.yaml", cmsResource, "cms/content-types/mytype/mytype.yml",
                                                        contentTypeResource ) );

        createAdminContext().runWith( () -> applicationService.installGlobalApplication( app ) );

        assertTrue( createAdminContext().callWith( () -> schemaService.listNamespaces() )
                        .stream()
                        .anyMatch( namespace -> "myglobalapp".equals( namespace.getKey().toString() ) ) );

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myglobalapp/cms/content-types/mytype/mytype.yaml" ) ) );

        assertNotNull( resourceNode );
        assertEquals( contentTypeResource, resourceNode.data().getString( "resource" ) );

        final DynamicSchemaResult<BaseSchema<?>> schema = createAdminContext().callWith( () -> schemaService.getContentSchema(
            GetDynamicContentSchemaParams.create()
                .name( ContentTypeName.from( "myglobalapp:mytype" ) )
                .type( DynamicContentSchemaType.CONTENT_TYPE )
                .build() ) );

        assertNotNull( schema );
        assertEquals( "node", schema.getResource().getResolverName() );
        assertEquals( contentTypeResource, schema.getResource().readString() );

        final ByteSource updatedApp = createAppSource( "myglobalapp", "1.0.1",
                                                       Map.of( "cms/cms.yaml", cmsResource, "cms/content-types/newtype/newtype.yaml",
                                                               contentTypeResource ) );

        createAdminContext().runWith( () -> applicationService.installGlobalApplication( updatedApp ) );

        assertFalse( VirtualAppContext.createAdminContext()
                         .callWith(
                             () -> nodeService.nodeExists( new NodePath( "/myglobalapp/cms/content-types/mytype/mytype.yaml" ) ) ) );

        assertNull( createAdminContext().callWith( () -> schemaService.getContentSchema( GetDynamicContentSchemaParams.create()
                                                                                                    .name( ContentTypeName.from(
                                                                                                        "myglobalapp:mytype" ) )
                                                                                                    .type(
                                                                                                        DynamicContentSchemaType.CONTENT_TYPE )
                                                                                                    .build() ) ) );

        final DynamicSchemaResult<BaseSchema<?>> newSchema = createAdminContext().callWith( () -> schemaService.getContentSchema(
            GetDynamicContentSchemaParams.create()
                .name( ContentTypeName.from( "myglobalapp:newtype" ) )
                .type( DynamicContentSchemaType.CONTENT_TYPE )
                .build() ) );

        assertNotNull( newSchema );
        assertEquals( "node", newSchema.getResource().getResolverName() );
    }

    @Test
    void installGlobalApplicationWithoutCmsYamlDoesNotCreateNamespace()
        throws Exception
    {
        final String contentTypeResource = readResource( "_contentType.yaml" );

        final ByteSource app =
            createAppSource( "mynoncmsapp", "1.0.0", Map.of( "cms/content-types/mytype/mytype.yml", contentTypeResource ) );

        createAdminContext().runWith( () -> applicationService.installGlobalApplication( app ) );

        assertFalse( createAdminContext().callWith( () -> schemaService.listNamespaces() )
                         .stream()
                         .anyMatch( namespace -> "mynoncmsapp".equals( namespace.getKey().toString() ) ) );

        assertFalse(
            VirtualAppContext.createAdminContext().callWith( () -> nodeService.nodeExists( new NodePath( "/mynoncmsapp" ) ) ) );
    }

    private static ByteSource createAppSource( final String name, final String version, final Map<String, String> resources )
    {
        try
        {
            final Manifest manifest = new Manifest();
            manifest.getMainAttributes().putValue( "Manifest-Version", "1.0" );
            manifest.getMainAttributes().putValue( Constants.BUNDLE_MANIFESTVERSION, "2" );
            manifest.getMainAttributes().putValue( Constants.BUNDLE_SYMBOLICNAME, name );
            manifest.getMainAttributes().putValue( Constants.BUNDLE_VERSION, version );
            manifest.getMainAttributes().putValue( "X-Bundle-Type", "application" );

            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (JarOutputStream jar = new JarOutputStream( out, manifest ))
            {
                for ( final Map.Entry<String, String> entry : resources.entrySet() )
                {
                    jar.putNextEntry( new ZipEntry( entry.getKey() ) );
                    jar.write( entry.getValue().getBytes( StandardCharsets.UTF_8 ) );
                    jar.closeEntry();
                }
            }
            return ByteSource.wrap( out.toByteArray() );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    @Test
    void listMixinsTypes()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        List<DynamicSchemaResult<BaseSchema<?>>> results = createAdminContext().callWith( () -> schemaService.listContentSchemas(
            ListDynamicContentSchemasParams.create().applicationKey( applicationKey ).type( DynamicContentSchemaType.MIXIN ).build() ) );

        assertTrue( results.isEmpty() );

        DynamicSchemaResult<MixinDescriptor> mixin1 = createAdminContext().callWith( () -> schemaService.createContentSchema(
            CreateDynamicContentSchemaParams.create()
                .name( MixinName.from( "myapp:mytype1" ) )
                .resource( readResource( "_mixin.yaml" ) )
                .type( DynamicContentSchemaType.MIXIN )
                .build() ) );
        DynamicSchemaResult<MixinDescriptor> mixin2 = createAdminContext().callWith( () -> schemaService.createContentSchema(
            CreateDynamicContentSchemaParams.create()
                .name( MixinName.from( "myapp:mytype2" ) )
                .resource( readResource( "_mixin.yaml" ) )
                .type( DynamicContentSchemaType.MIXIN )
                .build() ) );
        DynamicSchemaResult<MixinDescriptor> mixin3 = createAdminContext().callWith( () -> schemaService.createContentSchema(
            CreateDynamicContentSchemaParams.create()
                .name( MixinName.from( "my_other_app:mytype" ) )
                .resource( readResource( "_mixin.yaml" ) )
                .type( DynamicContentSchemaType.MIXIN )
                .build() ) );

        results = createAdminContext().callWith( () -> schemaService.listContentSchemas(
            ListDynamicContentSchemasParams.create().applicationKey( applicationKey ).type( DynamicContentSchemaType.MIXIN ).build() ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( mixin1, mixin2 ) );

        results = createAdminContext().callWith( () -> schemaService.listContentSchemas( ListDynamicContentSchemasParams.create()
                                                                                                    .applicationKey( ApplicationKey.from(
                                                                                                        "my_other_app" ) )
                                                                                                    .type( DynamicContentSchemaType.MIXIN )
                                                                                                    .build() ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( mixin3 ) );

    }

    @Test
    void deleteContentTypeComponent()
    {
        DynamicSchemaResult<ContentType> contentType = createAdminContext().callWith( () -> schemaService.createContentSchema(
            CreateDynamicContentSchemaParams.create()
                .name( ContentTypeName.from( "myapp:mytype" ) )
                .resource( readResource( "_contentType.yaml" ) )
                .type( DynamicContentSchemaType.CONTENT_TYPE )
                .build() ) );

        final boolean result = createAdminContext().callWith( () -> schemaService.deleteContentSchema(
            DeleteDynamicContentSchemaParams.create()
                .name( contentType.getSchema().getName() )
                .type( DynamicContentSchemaType.CONTENT_TYPE )
                .build() ) );

        assertThat( result ).isTrue();

        assertThat( createAdminContext().callWith( () -> schemaService.listContentSchemas( ListDynamicContentSchemasParams.create()
                                                                                                      .applicationKey(
                                                                                                          ApplicationKey.from( "myapp" ) )
                                                                                                      .type(
                                                                                                          DynamicContentSchemaType.CONTENT_TYPE )
                                                                                                      .build() ) ) ).isEmpty();

        assertThat( createAdminContext().callWith( () -> schemaService.getContentSchema( GetDynamicContentSchemaParams.create()
                                                                                                    .type(
                                                                                                        DynamicContentSchemaType.CONTENT_TYPE )
                                                                                                    .name(
                                                                                                        contentType.getSchema().getName() )
                                                                                                    .build() ) ) ).isNull();
    }

    @Test
    void deleteContentTypeComponentAsSchemaAdmin()
    {
        DynamicSchemaResult<ContentType> contentType = createSchemaAdminContext().callWith( () -> schemaService.createContentSchema(
            CreateDynamicContentSchemaParams.create()
                .name( ContentTypeName.from( "myapp:mytype" ) )
                .resource( readResource( "_contentType.yaml" ) )
                .type( DynamicContentSchemaType.CONTENT_TYPE )
                .build() ) );

        final boolean result = createSchemaAdminContext().callWith( () -> schemaService.deleteContentSchema(
            DeleteDynamicContentSchemaParams.create()
                .name( contentType.getSchema().getName() )
                .type( DynamicContentSchemaType.CONTENT_TYPE )
                .build() ) );

        assertThat( result ).isTrue();
    }

    @Test
    void deleteContentTypeComponentAsNonSchemaAdmin()
    {
        DynamicSchemaResult<ContentType> contentType = createSchemaAdminContext().callWith( () -> schemaService.createContentSchema(
            CreateDynamicContentSchemaParams.create()
                .name( ContentTypeName.from( "myapp:mytype" ) )
                .resource( readResource( "_contentType.yaml" ) )
                .type( DynamicContentSchemaType.CONTENT_TYPE )
                .build() ) );

        assertThrows( ForbiddenAccessException.class, () -> VirtualAppContext.createContext()
            .callWith( () -> schemaService.deleteContentSchema( DeleteDynamicContentSchemaParams.create()
                                                                           .name( contentType.getSchema().getName() )
                                                                           .type( DynamicContentSchemaType.CONTENT_TYPE )
                                                                           .build() ) ) );

    }


    @Test
    void deletePartComponent()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        DynamicSchemaResult<PartDescriptor> part = createAdminContext().callWith( () -> schemaService.createComponent(
            CreateDynamicComponentParams.create()
                .descriptorKey( DescriptorKey.from( "myapp:mypart" ) )
                .resource( readResource( "_part.yaml" ) )
                .type( DynamicComponentType.PART )
                .build() ) );

        final boolean result = createAdminContext().callWith( () -> schemaService.deleteComponent(
            DeleteDynamicComponentParams.create().type( DynamicComponentType.PART ).descriptorKey( part.getSchema().getKey() ).build() ) );

        assertTrue( result );

        assertThat( createAdminContext().callWith( () -> schemaService.listComponents(
            ListDynamicComponentsParams.create().applicationKey( applicationKey ).type( DynamicComponentType.PART ).build() ) ) ).isEmpty();

        assertThat( createAdminContext().callWith( () -> schemaService.getComponent( GetDynamicComponentParams.create()
                                                                                                .type( DynamicComponentType.PART )
                                                                                                .descriptorKey( part.getSchema().getKey() )
                                                                                                .build() ) ) ).usingRecursiveComparison()
            .isNull();

    }

    @Test
    void createMacro()
        throws Exception
    {
        final String resource = readResource( "_macro.yaml" );

        final CreateDynamicMacroParams params =
            CreateDynamicMacroParams.create().key( MacroKey.from( "myapp:mymacro" ) ).resource( resource ).build();

        final DynamicSchemaResult<MacroDescriptor> result =
            createAdminContext().callWith( () -> schemaService.createMacro( params ) );

        final MacroDescriptor macroDescriptor = result.getSchema();

        createAdminContext().runWith( () -> assertThat( macroDescriptor ).usingRecursiveComparison()
            .isEqualTo( schemaService.getMacro(
                GetDynamicMacroParams.create().key( MacroKey.from( "myapp:mymacro" ) ).build() ).getSchema() ) );

        assertEquals( "mymacro", macroDescriptor.getName() );
        assertEquals( "myapp", macroDescriptor.getKey().getApplicationKey().toString() );
        assertEquals( "Virtual Macro", macroDescriptor.getTitle() );
        assertEquals( "key.display-name", macroDescriptor.getTitleI18nKey() );
        assertEquals( "My Macro Description", macroDescriptor.getDescription() );
        assertEquals( "key.description", macroDescriptor.getDescriptionI18nKey() );
        assertEquals( 1, macroDescriptor.getForm().size() );
        assertNotNull( macroDescriptor.getModifiedTime() );
        assertEquals( 1, macroDescriptor.getSchemaConfig().properties().size() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/macros/mymacro/mymacro.yaml", result.getResource().getKey().toString() );

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/macros/mymacro/mymacro.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void updateMacro()
        throws Exception
    {
        final CreateDynamicMacroParams createParams = CreateDynamicMacroParams.create()
            .key( MacroKey.from( "myapp:mymacro" ) )
            .resource( """
                           kind: "Macro"
                           title: "MyMacro"
                           form: [ ]
                           """ )
            .build();

        createAdminContext().runWith( () -> schemaService.createMacro( createParams ) );

        final String resource = readResource( "_macro.yaml" );

        final UpdateDynamicMacroParams updateParams =
            UpdateDynamicMacroParams.create().key( MacroKey.from( "myapp:mymacro" ) ).resource( resource ).build();

        final DynamicSchemaResult<MacroDescriptor> result =
            createAdminContext().callWith( () -> schemaService.updateMacro( updateParams ) );

        assertEquals( "Virtual Macro", result.getSchema().getTitle() );
        assertEquals( resource, result.getResource().readString() );
    }

    @Test
    void listMacros()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        assertThat( createAdminContext().callWith( () -> schemaService.listMacros(
            ListDynamicMacrosParams.create().applicationKey( applicationKey ).build() ) ) ).isEmpty();

        DynamicSchemaResult<MacroDescriptor> macro1 = createAdminContext().callWith( () -> schemaService.createMacro(
            CreateDynamicMacroParams.create().key( MacroKey.from( "myapp:mymacro1" ) ).resource( readResource( "_macro.yaml" ) ).build() ) );
        DynamicSchemaResult<MacroDescriptor> macro2 = createAdminContext().callWith( () -> schemaService.createMacro(
            CreateDynamicMacroParams.create().key( MacroKey.from( "myapp:mymacro2" ) ).resource( readResource( "_macro.yaml" ) ).build() ) );
        createAdminContext().callWith( () -> schemaService.createMacro(
            CreateDynamicMacroParams.create()
                .key( MacroKey.from( "my_other_app:mymacro" ) )
                .resource( readResource( "_macro.yaml" ) )
                .build() ) );

        final List<DynamicSchemaResult<MacroDescriptor>> results = createAdminContext().callWith(
            () -> schemaService.listMacros( ListDynamicMacrosParams.create().applicationKey( applicationKey ).build() ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( macro1, macro2 ) );
    }

    @Test
    void deleteMacro()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        DynamicSchemaResult<MacroDescriptor> macro = createAdminContext().callWith( () -> schemaService.createMacro(
            CreateDynamicMacroParams.create().key( MacroKey.from( "myapp:mymacro" ) ).resource( readResource( "_macro.yaml" ) ).build() ) );

        final boolean result = createAdminContext().callWith(
            () -> schemaService.deleteMacro( DeleteDynamicMacroParams.create().key( macro.getSchema().getKey() ).build() ) );

        assertTrue( result );

        assertThat( createAdminContext().callWith( () -> schemaService.listMacros(
            ListDynamicMacrosParams.create().applicationKey( applicationKey ).build() ) ) ).isEmpty();

        assertThat( createAdminContext().callWith( () -> schemaService.getMacro(
            GetDynamicMacroParams.create().key( macro.getSchema().getKey() ).build() ) ) ).usingRecursiveComparison().isNull();
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
            () -> schemaService.createContentSchema( params ) ) );

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
                      () -> createAdminContext().callWith( () -> schemaService.createContentSchema( params ) ) );
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
                      () -> createAdminContext().callWith( () -> schemaService.createContentSchema( params ) ) );
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
                      () -> createAdminContext().callWith( () -> schemaService.createComponent( params ) ) );
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
                      () -> createAdminContext().callWith( () -> schemaService.createComponent( params ) ) );
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
                      () -> createAdminContext().callWith( () -> schemaService.createComponent( params ) ) );
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

        assertThrows( Exception.class, () -> createAdminContext().callWith( () -> schemaService.createStyles( params ) ) );
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

        assertThrows( Exception.class, () -> createAdminContext().callWith( () -> schemaService.createCms( params ) ) );
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
