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
import com.enonic.xp.app.NamespaceNotFoundException;
import com.enonic.xp.app.UpdateNamespaceParams;
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
import com.enonic.xp.core.impl.schema.SchemaAuditLogSupportImpl;
import com.enonic.xp.core.impl.schema.SchemaServiceImpl;
import com.enonic.xp.core.impl.schema.NamespaceContext;
import com.enonic.xp.core.impl.schema.NamespaceAppInitializer;
import com.enonic.xp.core.impl.schema.NamespaceAppService;
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
import com.enonic.xp.icon.Icon;
import com.enonic.xp.internal.blobstore.MemoryBlobStore;
import com.enonic.xp.itest.AbstractElasticsearchIntegrationTest;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.project.CreateProjectParams;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectRole;
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
import com.enonic.xp.schema.CreateCmsParams;
import com.enonic.xp.schema.CreateComponentParams;
import com.enonic.xp.schema.CreateContentSchemaParams;
import com.enonic.xp.schema.CreateMacroParams;
import com.enonic.xp.schema.CreatePhrasesParams;
import com.enonic.xp.schema.CreateStylesParams;
import com.enonic.xp.schema.DeleteMacroParams;
import com.enonic.xp.schema.DeletePhrasesParams;
import com.enonic.xp.schema.SchemaResult;
import com.enonic.xp.schema.GetMacroParams;
import com.enonic.xp.schema.GetPhrasesParams;
import com.enonic.xp.schema.ListMacrosParams;
import com.enonic.xp.schema.SchemaNotFoundException;
import com.enonic.xp.schema.SetComponentIconParams;
import com.enonic.xp.schema.SetMacroIconParams;
import com.enonic.xp.schema.SetSchemaIconParams;
import com.enonic.xp.schema.UpdateCmsParams;
import com.enonic.xp.schema.UpdateComponentParams;
import com.enonic.xp.schema.UpdateContentSchemaParams;
import com.enonic.xp.schema.UpdateMacroParams;
import com.enonic.xp.schema.UpdatePhrasesParams;
import com.enonic.xp.schema.UpdateStylesParams;
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
import com.enonic.xp.util.BinaryReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SchemaServiceImplTest
    extends AbstractElasticsearchIntegrationTest
{
    NodeServiceImpl nodeService;

    private SchemaServiceImpl schemaService;

    private AuditLogService auditLogService;

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

        final NamespaceAppService namespaceAppService = new NamespaceAppService( nodeService );
        NamespaceAppInitializer.create().setIndexService( indexService ).setRepositoryService( repositoryService ).build().initialize();

        applicationService = new ApplicationServiceImpl( applicationRegistry, repoService, eventPublisher, appFilterService,
                                                         namespaceAppService,
                                                         new ApplicationAuditLogSupportImpl( mock( AuditLogService.class ) ) );

        this.auditLogService = mock( AuditLogService.class );
        this.schemaService = new SchemaServiceImpl( nodeService, resourceService, applicationService, namespaceAppService,
                                                    new SchemaAuditLogSupportImpl( auditLogService ) );

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
        createDefaultCms( ApplicationKey.from( "myapp" ) );

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
        createDefaultCms( ApplicationKey.from( "myapp" ) );

        final Resource resource = createAdminContext().callWith(
            () -> resourceService.getResource( ResourceKey.from( ApplicationKey.from( "myapp" ), "cms/cms.yaml" ) ) );

        assertTrue( resource.exists() );
    }

    @Test
    void namespace_cms_yaml_resource_non_admin()
    {
        createDefaultCms( ApplicationKey.from( "myapp" ) );

        final Resource resource = createContentManagerAdminContext().callWith(
            () -> resourceService.getResource( ResourceKey.from( ApplicationKey.from( "myapp" ), "cms/cms.yaml" ) ) );

        assertTrue( resource.exists() );
    }

    @Test
    void namespace_cms_yaml_resource_unauthenticated()
    {
        createDefaultCms( ApplicationKey.from( "myapp" ) );

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
    void update_namespace()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        createDefaultCms( applicationKey );

        final Namespace result = createAdminContext().callWith( () -> schemaService.updateNamespace(
            UpdateNamespaceParams.create().key( applicationKey ).description( "updated description" ).build() ) );

        assertEquals( applicationKey, result.getKey() );
        assertEquals( "updated description", result.getDescription() );

        final Namespace fetched = createAdminContext().callWith( () -> schemaService.getNamespace( applicationKey ) );
        assertEquals( "updated description", fetched.getDescription() );

        // omitted description clears it
        final Namespace cleared = createAdminContext().callWith( () -> schemaService.updateNamespace(
            UpdateNamespaceParams.create().key( applicationKey ).build() ) );
        assertNull( cleared.getDescription() );

        // schemas survive the update
        assertTrue( createAdminContext().callWith( () -> resourceService.getResource(
            ResourceKey.from( applicationKey, "cms/cms.yaml" ) ) ).exists() );
    }

    @Test
    void update_namespace_not_found()
    {
        assertThrows( NamespaceNotFoundException.class, () -> createAdminContext().callWith( () -> schemaService.updateNamespace(
            UpdateNamespaceParams.create().key( ApplicationKey.from( "nonexistent" ) ).description( "x" ).build() ) ) );
    }

    @Test
    void update_namespace_without_admin()
    {
        assertThrows( ForbiddenAccessException.class, () -> NamespaceContext.createContext()
            .callWith( () -> schemaService.updateNamespace(
                UpdateNamespaceParams.create().key( ApplicationKey.from( "myapp" ) ).description( "x" ).build() ) ) );
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

        CreateContentSchemaParams params = CreateContentSchemaParams.create()
            .name( ContentTypeName.from( "myapp:mytype" ) )
            .resource( resource )
            .build();

        final SchemaResult<ContentType> result =
            createAdminContext().callWith( () -> schemaService.createContentType( params ) );

        final ContentType contentType = result.getSchema();

        createAdminContext().runWith( () -> assertThat( contentType ).usingRecursiveComparison()
            .isEqualTo( schemaService.getContentType( ContentTypeName.from( "myapp:mytype" ) ).getSchema() ) );

        assertEquals( "myapp:mytype", contentType.getName().toString() );
        assertEquals( "Dynamic Content Type", contentType.getTitle() );
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

        final Node resourceNode = NamespaceContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/content-types/mytype/mytype.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void updateContentTypeSchema()
        throws Exception
    {
        final CreateContentSchemaParams createParams = CreateContentSchemaParams.create()
            .name( ContentTypeName.from( "myapp:mytype" ) ).resource( """
                                                                          kind: "ContentType"
                                                                          superType: "base:unstructured"
                                                                          """ )
            .build();

        createAdminContext().runWith( () -> schemaService.createContentType( createParams ) );

        final String resource = readResource( "_contentType.yaml" );

        final UpdateContentSchemaParams updateParams = UpdateContentSchemaParams.create()
            .name( ContentTypeName.from( "myapp:mytype" ) )
            .resource( resource )
            .build();

        final SchemaResult<ContentType> result =
            createAdminContext().callWith( () -> schemaService.updateContentType( updateParams ) );

        final ContentType contentType = result.getSchema();

        createAdminContext().runWith( () -> assertThat( contentType ).usingRecursiveComparison()
            .isEqualTo( schemaService.getContentType( ContentTypeName.from( "myapp:mytype" ) ).getSchema() ) );

        assertEquals( "myapp:mytype", contentType.getName().toString() );
        assertEquals( "Dynamic Content Type", contentType.getTitle() );
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

        final Node resourceNode = NamespaceContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/content-types/mytype/mytype.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void createFormFragmentSchema()
        throws Exception
    {
        final String resource = readResource( "_formFragment.yaml" );

        CreateContentSchemaParams params = CreateContentSchemaParams.create()
            .name( FormFragmentName.from( "myapp:my-fragment" ) )
            .resource( resource )
            .build();

        final SchemaResult<FormFragmentDescriptor> result =
            createAdminContext().callWith( () -> schemaService.createFormFragment( params ) );

        final FormFragmentDescriptor fragment = result.getSchema();

        createAdminContext().runWith( () -> assertThat( fragment ).usingRecursiveComparison()
            .isEqualTo( schemaService.getFormFragment( FormFragmentName.from( "myapp:my-fragment" ) ).getSchema() ) );

        assertEquals( "myapp:my-fragment", fragment.getName().toString() );
        assertEquals( "Dynamic FormFragment", fragment.getTitle() );
        assertEquals( "FormFragment description", fragment.getDescription() );
        assertEquals( 2, fragment.getForm().size() );
        assertEquals( "myapp:inline", fragment.getForm().getFormFragment( "inline" ).getFormFragmentName().toString() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/form-fragments/my-fragment/my-fragment.yaml", result.getResource().getKey().toString() );

        final Node resourceNode = NamespaceContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/form-fragments/my-fragment/my-fragment.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void createFormFragmentSchemaAsDevSchemaAdmin()
        throws Exception
    {
        final String resource = readResource( "_formFragment.yaml" );

        CreateContentSchemaParams params = CreateContentSchemaParams.create()
            .name( FormFragmentName.from( "myapp:my-fragment" ) )
            .resource( resource )
            .build();

        final SchemaResult<FormFragmentDescriptor> result =
            createSchemaAdminContext().callWith( () -> schemaService.createFormFragment( params ) );

        assertNotNull( result.getResource() );
    }

    @Test
    void createFormFragmentSchemaAsNonSchemaAdmin()
        throws Exception
    {
        final String resource = readResource( "_formFragment.yaml" );

        CreateContentSchemaParams params = CreateContentSchemaParams.create()
            .name( FormFragmentName.from( "myapp:my-fragment" ) )
            .resource( resource )
            .build();

        assertThrows( ForbiddenAccessException.class,
                      () -> NamespaceContext.createContext().callWith( () -> schemaService.createFormFragment( params ) ) );
    }

    @Test
    void updateFormFragmentSchema()
        throws Exception
    {

        final CreateContentSchemaParams createParams =
            CreateContentSchemaParams.create()
                .name( FormFragmentName.from( "myapp:my-fragment" ) )
                .resource( """
                               kind: "FormFragment"
                               title: "MyFormFragment"
                               """ )
                .build();

        createAdminContext().runWith( () -> schemaService.createFormFragment( createParams ) );

        final String resource = readResource( "_formFragment.yaml" );

        final UpdateContentSchemaParams updateParams = UpdateContentSchemaParams.create()
            .name( FormFragmentName.from( "myapp:my-fragment" ) )
            .resource( resource )
            .build();

        final SchemaResult<FormFragmentDescriptor> result =
            createAdminContext().callWith( () -> schemaService.updateFormFragment( updateParams ) );

        final FormFragmentDescriptor fragment = result.getSchema();

        createAdminContext().runWith( () -> assertThat( fragment ).usingRecursiveComparison()
            .isEqualTo( schemaService.getFormFragment( FormFragmentName.from( "myapp:my-fragment" ) ).getSchema() ) );

        assertEquals( "myapp:my-fragment", fragment.getName().toString() );
        assertEquals( "Dynamic FormFragment", fragment.getTitle() );
        assertEquals( "FormFragment description", fragment.getDescription() );
        assertEquals( 2, fragment.getForm().size() );
        assertEquals( "myapp:inline", fragment.getForm().getFormFragment( "inline" ).getFormFragmentName().toString() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/form-fragments/my-fragment/my-fragment.yaml", result.getResource().getKey().toString() );

        final Node resourceNode = NamespaceContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/form-fragments/my-fragment/my-fragment.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void updateFormFragmentSchemaAsSchemaAdmin()
        throws Exception
    {

        final CreateContentSchemaParams createParams =
            CreateContentSchemaParams.create()
                .name( FormFragmentName.from( "myapp:my-fragment" ) )
                .resource( """
                               kind: "FormFragment"
                               title: "FormFragment"
                               """ )
                .build();

        createSchemaAdminContext().runWith( () -> schemaService.createFormFragment( createParams ) );

        final String resource = readResource( "_formFragment.yaml" );

        final UpdateContentSchemaParams updateParams = UpdateContentSchemaParams.create()
            .name( FormFragmentName.from( "myapp:my-fragment" ) )
            .resource( resource )
            .build();

        final SchemaResult<FormFragmentDescriptor> result =
            createSchemaAdminContext().callWith( () -> schemaService.updateFormFragment( updateParams ) );

        assertNotNull( result.getResource() );
    }

    @Test
    void updateFormFragmentSchemaAsNonSchemaAdmin()
        throws Exception
    {
        final CreateContentSchemaParams createParams =
            CreateContentSchemaParams.create()
                .name( FormFragmentName.from( "myapp:my-fragment" ) )
                .resource( """
                               kind: "FormFragment"
                               title: "FormFragment"
                               """ )
                .build();

        createSchemaAdminContext().runWith( () -> schemaService.createFormFragment( createParams ) );

        final String resource = readResource( "_formFragment.yaml" );

        final UpdateContentSchemaParams updateParams = UpdateContentSchemaParams.create()
            .name( FormFragmentName.from( "myapp:my-fragment" ) )
            .resource( resource )
            .build();

        assertThrows( ForbiddenAccessException.class,
                      () -> NamespaceContext.createContext().callWith( () -> schemaService.updateFormFragment( updateParams ) ) );
    }


    @Test
    void createMixinSchema()
        throws Exception
    {
        final String resource = readResource( "_mixin.yaml" );

        CreateContentSchemaParams params = CreateContentSchemaParams.create()
            .name( MixinName.from( "myapp:mymixin" ) )
            .resource( resource )
            .build();

        final SchemaResult<MixinDescriptor> result =
            createAdminContext().callWith( () -> schemaService.createMixin( params ) );

        final MixinDescriptor mixinDescriptor = result.getSchema();

        createAdminContext().runWith( () -> assertThat( mixinDescriptor ).usingRecursiveComparison()
            .isEqualTo( schemaService.getMixin( MixinName.from( "myapp:mymixin" ) ).getSchema() ) );

        assertEquals( "myapp:mymixin", mixinDescriptor.getName().toString() );
        assertEquals( "Dynamic Mixin", mixinDescriptor.getTitle() );
        assertEquals( "Mixin description", mixinDescriptor.getDescription() );
        assertEquals( 1, mixinDescriptor.getForm().size() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/mixins/mymixin/mymixin.yaml", result.getResource().getKey().toString() );

        final Node resourceNode = NamespaceContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/mixins/mymixin/mymixin.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void updateMixinSchema()
        throws Exception
    {
        final CreateContentSchemaParams createParams =
            CreateContentSchemaParams.create()
                .name( MixinName.from( "myapp:mymixin" ) )
                .resource( """
                               kind: "Mixin"
                               title: "Dynamic MIXIN"
                               form: [ ]
                               """ )
                .build();

        createAdminContext().runWith( () -> schemaService.createMixin( createParams ) );

        final String resource = readResource( "_mixin.yaml" );

        final UpdateContentSchemaParams updateParams = UpdateContentSchemaParams.create()
            .name( MixinName.from( "myapp:mymixin" ) )
            .resource( resource )
            .build();

        final SchemaResult<MixinDescriptor> result =
            createAdminContext().callWith( () -> schemaService.updateMixin( updateParams ) );

        final MixinDescriptor mixinDescriptor = result.getSchema();

        createAdminContext().runWith( () -> assertThat( mixinDescriptor ).usingRecursiveComparison()
            .isEqualTo( schemaService.getMixin( MixinName.from( "myapp:mymixin" ) ).getSchema() ) );

        assertEquals( "myapp:mymixin", mixinDescriptor.getName().toString() );
        assertEquals( "Dynamic Mixin", mixinDescriptor.getTitle() );
        assertEquals( "Mixin description", mixinDescriptor.getDescription() );
        assertEquals( 1, mixinDescriptor.getForm().size() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/mixins/mymixin/mymixin.yaml", result.getResource().getKey().toString() );

        final Node resourceNode = NamespaceContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/mixins/mymixin/mymixin.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void createPartComponent()
        throws Exception
    {
        final String resource = readResource( "_part.yaml" );

        CreateComponentParams params = CreateComponentParams.create()
            .descriptorKey( DescriptorKey.from( "myapp:mypart" ) )
            .resource( resource )
            .build();

        final SchemaResult<PartDescriptor> result =
            createAdminContext().callWith( () -> schemaService.createPart( params ) );

        final PartDescriptor partDescriptor = result.getSchema();

        createAdminContext().runWith( () -> assertThat( partDescriptor ).usingRecursiveComparison()
            .isEqualTo( schemaService.getPart( DescriptorKey.from( "myapp:mypart" ) ).getSchema() ) );

        assertEquals( "mypart", partDescriptor.getName() );
        assertEquals( "myapp", partDescriptor.getApplicationKey().toString() );
        assertEquals( "Dynamic Part", partDescriptor.getTitle() );
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

        final Node resourceNode = NamespaceContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/parts/mypart/mypart.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void updatePartComponent()
        throws Exception
    {

        final CreateComponentParams createParams =
            CreateComponentParams.create()
                .descriptorKey( DescriptorKey.from( "myapp:mypart" ) )
                .resource( """
                               kind: "Part"
                               title: "MyPart"
                               form: [ ]
                               """ )
                .build();

        createAdminContext().runWith( () -> schemaService.createPart( createParams ) );

        final String resource = readResource( "_part.yaml" );

        final UpdateComponentParams updateParams = UpdateComponentParams.create()
            .descriptorKey( DescriptorKey.from( "myapp:mypart" ) )
            .resource( resource )
            .build();

        final SchemaResult<PartDescriptor> result =
            createAdminContext().callWith( () -> schemaService.updatePart( updateParams ) );

        final PartDescriptor partDescriptor = result.getSchema();

        createAdminContext().runWith( () -> assertThat( partDescriptor ).usingRecursiveComparison()
            .isEqualTo( schemaService.getPart( DescriptorKey.from( "myapp:mypart" ) ).getSchema() ) );

        assertEquals( "mypart", partDescriptor.getName() );
        assertEquals( "myapp", partDescriptor.getApplicationKey().toString() );
        assertEquals( "Dynamic Part", partDescriptor.getTitle() );
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

        final Node resourceNode = NamespaceContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/parts/mypart/mypart.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void createLayoutComponent()
        throws Exception
    {
        final String resource = readResource( "_layout.yaml" );

        CreateComponentParams params = CreateComponentParams.create()
            .descriptorKey( DescriptorKey.from( "myapp:mylayout" ) )
            .resource( resource )
            .build();

        final SchemaResult<LayoutDescriptor> result =
            createAdminContext().callWith( () -> schemaService.createLayout( params ) );

        final LayoutDescriptor layoutDescriptor = result.getSchema();

        createAdminContext().runWith( () -> assertThat( layoutDescriptor ).usingRecursiveComparison()
            .isEqualTo( schemaService.getLayout( DescriptorKey.from( "myapp:mylayout" ) ).getSchema() ) );

        assertEquals( "mylayout", layoutDescriptor.getName() );
        assertEquals( "myapp", layoutDescriptor.getApplicationKey().toString() );
        assertEquals( "Dynamic Layout", layoutDescriptor.getTitle() );
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

        final Node resourceNode = NamespaceContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/layouts/mylayout/mylayout.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void updateLayoutComponent()
        throws Exception
    {
        final CreateComponentParams params =
            CreateComponentParams.create()
                .descriptorKey( DescriptorKey.from( "myapp:mylayout" ) )
                .resource( """
                               kind: "Layout"
                               title: MyLayout
                               regions: [ ]

                               """ )
                .build();

        createAdminContext().runWith( () -> schemaService.createLayout( params ) );

        final String resource = readResource( "_layout.yaml" );

        final UpdateComponentParams updateParams = UpdateComponentParams.create()
            .descriptorKey( DescriptorKey.from( "myapp:mylayout" ) )
            .resource( resource )
            .build();

        final SchemaResult<LayoutDescriptor> result =
            createAdminContext().callWith( () -> schemaService.updateLayout( updateParams ) );

        final LayoutDescriptor layoutDescriptor = result.getSchema();

        createAdminContext().runWith( () -> assertThat( layoutDescriptor ).usingRecursiveComparison()
            .isEqualTo( schemaService.getLayout( DescriptorKey.from( "myapp:mylayout" ) ).getSchema() ) );

        assertEquals( "mylayout", layoutDescriptor.getName() );
        assertEquals( "myapp", layoutDescriptor.getApplicationKey().toString() );
        assertEquals( "Dynamic Layout", layoutDescriptor.getTitle() );
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

        final Node resourceNode = NamespaceContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/layouts/mylayout/mylayout.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void createPageComponent()
        throws Exception
    {
        final String resource = readResource( "_page.yaml" );

        CreateComponentParams params = CreateComponentParams.create()
            .descriptorKey( DescriptorKey.from( "myapp:mypage" ) )
            .resource( resource )
            .build();

        final SchemaResult<PageDescriptor> result =
            createAdminContext().callWith( () -> schemaService.createPage( params ) );

        final PageDescriptor pageDescriptor = result.getSchema();

        createAdminContext().runWith( () -> assertThat( pageDescriptor ).usingRecursiveComparison()
            .isEqualTo( schemaService.getPage( DescriptorKey.from( "myapp:mypage" ) ).getSchema() ) );

        assertEquals( "mypage", pageDescriptor.getName() );
        assertEquals( "myapp", pageDescriptor.getApplicationKey().toString() );
        assertEquals( "Dynamic Page", pageDescriptor.getTitle() );
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

        final Node resourceNode = NamespaceContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/pages/mypage/mypage.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void updatePageComponent()
        throws Exception
    {
        final CreateComponentParams createParams =
            CreateComponentParams.create()
                .descriptorKey( DescriptorKey.from( "myapp:mypage" ) )
                .resource( """
                               kind: "Page"
                               title: "MyPage"
                               regions:
                                 - "main"
                               """ )
                .build();

        createAdminContext().runWith( () -> schemaService.createPage( createParams ) );

        final String resource = readResource( "_page.yaml" );

        final UpdateComponentParams updateParams = UpdateComponentParams.create()
            .descriptorKey( DescriptorKey.from( "myapp:mypage" ) )
            .resource( resource )
            .build();

        final SchemaResult<PageDescriptor> result =
            createAdminContext().callWith( () -> schemaService.updatePage( updateParams ) );

        final PageDescriptor pageDescriptor = result.getSchema();

        createAdminContext().runWith( () -> assertThat( pageDescriptor ).usingRecursiveComparison()
            .isEqualTo( schemaService.getPage( DescriptorKey.from( "myapp:mypage" ) ).getSchema() ) );

        assertEquals( "mypage", pageDescriptor.getName() );
        assertEquals( "myapp", pageDescriptor.getApplicationKey().toString() );
        assertEquals( "Dynamic Page", pageDescriptor.getTitle() );
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

        final Node resourceNode = NamespaceContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/pages/mypage/mypage.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void createSite()
        throws Exception
    {
        final String resource = readResource( "_cms.yaml" );
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        assertThat( createAdminContext().callWith( () -> schemaService.getCmsDescriptor( applicationKey ) ) ).isNull();

        final SchemaResult<CmsDescriptor> result = createAdminContext().callWith(
            () -> schemaService.createCms( CreateCmsParams.create().key( applicationKey ).resource( resource ).build() ) );

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
            NamespaceContext.createAdminContext().callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/cms.yaml" ) ) );

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

        createDefaultCms( applicationKey );

        final SchemaResult<CmsDescriptor> result = createAdminContext().callWith(
            () -> schemaService.updateCms( UpdateCmsParams.create().key( applicationKey ).resource( resource ).build() ) );

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
            NamespaceContext.createAdminContext().callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/cms.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void updateNotCreatedSite()
        throws Exception
    {
        final String resource = readResource( "_cms.yaml" );
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        assertThrows( NodeNotFoundException.class, () -> createAdminContext().callWith(
            () -> schemaService.updateCms( UpdateCmsParams.create().key( applicationKey ).resource( resource ).build() ) ) );
    }

    @Test
    void createAlreadyCreatedSite()
        throws Exception
    {
        final String resource = readResource( "_cms.yaml" );
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        createAdminContext().runWith( () -> schemaService.createCms(
            CreateCmsParams.create().key( applicationKey ).resource( resource ).build() ) );

        assertThrows( NodeAlreadyExistAtPathException.class, () -> createAdminContext().callWith(
            () -> schemaService.createCms( CreateCmsParams.create().key( applicationKey ).resource( resource ).build() ) ) );
    }

    @Test
    void createCms_without_admin()
    {
        final CreateCmsParams params = CreateCmsParams.create().key( ApplicationKey.from( "myapp" ) ).resource( """
            kind: "CMS"
            mixins: [ ]
            form: [ ]
            """ ).build();

        assertThrows( ForbiddenAccessException.class,
                      () -> NamespaceContext.createContext().callWith( () -> schemaService.createCms( params ) ) );
    }

    @Test
    void deleteCms()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        assertThat( createAdminContext().callWith( () -> schemaService.getCmsDescriptor( applicationKey ) ) ).isNull();

        createAdminContext().callWith( () -> schemaService.createCms(
            CreateCmsParams.create().key( applicationKey ).resource( readResource( "_cms.yaml" ) ).build() ) );

        final SchemaResult<CmsDescriptor> cmsDescriptorResult =
            createAdminContext().callWith( () -> schemaService.getCmsDescriptor( applicationKey ) );

        assertThat( cmsDescriptorResult.getSchema().getForm() ).isNotEmpty();
        assertThat( cmsDescriptorResult.getSchema().getMixinMappings() ).isNotEmpty();

        assertThat( createAdminContext().callWith( () -> schemaService.deleteCms( applicationKey ) ) ).isTrue();

        assertThat( createAdminContext().callWith( () -> schemaService.getCmsDescriptor( applicationKey ) ) ).isNull();
    }

    @Test
    void deleteCms_without_admin()
    {
        assertThrows( ForbiddenAccessException.class, () -> NamespaceContext.createContext()
            .callWith( () -> schemaService.deleteCms( ApplicationKey.from( "myapp" ) ) ) );
    }

    @Test
    void mutation_writes_audit_log()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        createAdminContext().callWith( () -> schemaService.createCms(
            CreateCmsParams.create().key( applicationKey ).resource( readResource( "_cms.yaml" ) ).build() ) );

        verify( auditLogService ).log( argThat( params -> "system.schema.cms.create".equals( params.getType() ) ) );

        assertTrue( createAdminContext().callWith( () -> schemaService.deleteCms( applicationKey ) ) );

        verify( auditLogService ).log( argThat( params -> "system.schema.cms.delete".equals( params.getType() ) ) );
    }

    @Test
    void unsuccessful_delete_is_not_audited()
    {
        assertFalse( createAdminContext().callWith( () -> schemaService.deleteStyles( ApplicationKey.from( "myapp" ) ) ) );

        verify( auditLogService, never() ).log( argThat( params -> "system.schema.styles.delete".equals( params.getType() ) ) );
    }

    @Test
    void createStyles()
        throws Exception
    {
        final String resource = readResource( "_styles.yaml" );
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        assertThat( createAdminContext().callWith( () -> schemaService.getStyles( applicationKey ) ) ).isNull();

        final SchemaResult<StyleDescriptor> result = createAdminContext().callWith( () -> schemaService.createStyles(
            CreateStylesParams.create().key( applicationKey ).resource( resource ).build() ) );

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
            NamespaceContext.createAdminContext().callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/style/style.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void updateStyles()
        throws Exception
    {
        final String resource = readResource( "_styles.yaml" );
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        createAdminContext().callWith( () -> schemaService.createStyles(
            CreateStylesParams.create().key( applicationKey ).resource( "kind: \"Style\"\n" ).build() ) );

        final SchemaResult<StyleDescriptor> result = createAdminContext().callWith( () -> schemaService.updateStyles(
            UpdateStylesParams.create().key( applicationKey ).resource( resource ).build() ) );

        final StyleDescriptor styleDescriptor = result.getSchema();

        createAdminContext().runWith( () -> assertThat( styleDescriptor ).usingRecursiveComparison()
            .isEqualTo( schemaService.getStyles( applicationKey ).getSchema() ) );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/style/style.yaml", result.getResource().getKey().toString() );

        final Node resourceNode =
            NamespaceContext.createAdminContext().callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/style/style.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void deleteStyles()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        assertThat( createAdminContext().callWith( () -> schemaService.getStyles( applicationKey ) ) ).isNull();

        createAdminContext().callWith( () -> schemaService.createStyles(
            CreateStylesParams.create().key( applicationKey ).resource( readResource( "_styles.yaml" ) ).build() ) );

        assertThat( createAdminContext().callWith( () -> schemaService.getStyles( applicationKey ) ) ).isNotNull();

        assertThat( createAdminContext().callWith( () -> schemaService.deleteStyles( applicationKey ) ) ).isTrue();

        assertThat( createAdminContext().callWith( () -> schemaService.getStyles( applicationKey ) ) ).isNull();
    }

    @Test
    void createPhrases()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );
        final String resource = "action.save=Save\naction.delete=Delete\n";

        assertNull( createAdminContext().callWith( () -> schemaService.getPhrases(
            GetPhrasesParams.create().key( applicationKey ).name( "phrases_en" ).build() ) ) );

        final Resource result = createAdminContext().callWith( () -> schemaService.createPhrases(
            CreatePhrasesParams.create().key( applicationKey ).name( "phrases_en" ).resource( resource ).build() ) );

        assertEquals( "node", result.getResolverName() );
        assertTrue( result.exists() );
        assertEquals( resource, result.readString() );
        assertEquals( "myapp:/cms/i18n/phrases/phrases_en.properties", result.getKey().toString() );

        final Resource fetched = createAdminContext().callWith( () -> schemaService.getPhrases(
            GetPhrasesParams.create().key( applicationKey ).name( "phrases_en" ).build() ) );

        assertEquals( "node", fetched.getResolverName() );
        assertEquals( resource, fetched.readString() );

        final Node resourceNode = NamespaceContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/i18n/phrases/phrases_en.properties" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void createPhrases_without_admin()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        assertThrows( ForbiddenAccessException.class, () -> NamespaceContext.createContext()
            .callWith( () -> schemaService.createPhrases(
                CreatePhrasesParams.create().key( applicationKey ).name( "phrases_en" ).resource( "a=b\n" ).build() ) ) );
    }

    @Test
    void updatePhrases()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );
        final String resource = "action.save=Save changes\n";

        createAdminContext().callWith( () -> schemaService.createPhrases(
            CreatePhrasesParams.create().key( applicationKey ).name( "phrases_en" ).resource( "action.save=Save\n" ).build() ) );

        final Resource result = createAdminContext().callWith( () -> schemaService.updatePhrases(
            UpdatePhrasesParams.create().key( applicationKey ).name( "phrases_en" ).resource( resource ).build() ) );

        assertEquals( "node", result.getResolverName() );
        assertEquals( resource, result.readString() );
        assertEquals( "myapp:/cms/i18n/phrases/phrases_en.properties", result.getKey().toString() );

        final Node resourceNode = NamespaceContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/i18n/phrases/phrases_en.properties" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void listPhrases()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        assertTrue( createAdminContext().callWith( () -> schemaService.listPhrases( applicationKey ) ).isEmpty() );

        createAdminContext().callWith( () -> schemaService.createPhrases(
            CreatePhrasesParams.create().key( applicationKey ).name( "phrases" ).resource( "action.save=Save\n" ).build() ) );
        createAdminContext().callWith( () -> schemaService.createPhrases(
            CreatePhrasesParams.create().key( applicationKey ).name( "phrases_no" ).resource( "action.save=Lagre\n" ).build() ) );

        final List<Resource> result = createAdminContext().callWith( () -> schemaService.listPhrases( applicationKey ) );

        assertEquals( 2, result.size() );
        assertTrue( result.stream()
                        .anyMatch( resource -> "myapp:/cms/i18n/phrases/phrases.properties".equals( resource.getKey().toString() ) ) );
        assertTrue( result.stream()
                        .anyMatch( resource -> "myapp:/cms/i18n/phrases/phrases_no.properties".equals( resource.getKey().toString() ) ) );
    }

    @Test
    void deletePhrases()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        createAdminContext().callWith( () -> schemaService.createPhrases(
            CreatePhrasesParams.create().key( applicationKey ).name( "phrases_en" ).resource( "action.save=Save\n" ).build() ) );

        assertNotNull( createAdminContext().callWith( () -> schemaService.getPhrases(
            GetPhrasesParams.create().key( applicationKey ).name( "phrases_en" ).build() ) ) );

        assertTrue( createAdminContext().callWith( () -> schemaService.deletePhrases(
            DeletePhrasesParams.create().key( applicationKey ).name( "phrases_en" ).build() ) ) );

        assertNull( createAdminContext().callWith( () -> schemaService.getPhrases(
            GetPhrasesParams.create().key( applicationKey ).name( "phrases_en" ).build() ) ) );

        assertFalse( createAdminContext().callWith( () -> schemaService.deletePhrases(
            DeletePhrasesParams.create().key( applicationKey ).name( "phrases_en" ).build() ) ) );
    }


    @Test
    void listPartComponents()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        List<SchemaResult<PartDescriptor>> results =
            createAdminContext().callWith( () -> schemaService.listParts( applicationKey ) );

        assertTrue( results.isEmpty() );

        SchemaResult<PartDescriptor> part1 = createAdminContext().callWith( () -> schemaService.createPart(
            CreateComponentParams.create()
                .descriptorKey( DescriptorKey.from( "myapp:mypart1" ) )
                .resource( readResource( "_part.yaml" ) )
                .build() ) );
        SchemaResult<PartDescriptor> part2 = createAdminContext().callWith( () -> schemaService.createPart(
            CreateComponentParams.create()
                .descriptorKey( DescriptorKey.from( "myapp:mypart2" ) )
                .resource( readResource( "_part.yaml" ) )
                .build() ) );
        SchemaResult<PartDescriptor> part3 = createAdminContext().callWith( () -> schemaService.createPart(
            CreateComponentParams.create()
                .descriptorKey( DescriptorKey.from( "my_other_app:mypart" ) )
                .resource( readResource( "_part.yaml" ) )
                .build() ) );

        results = createAdminContext().callWith( () -> schemaService.listParts( applicationKey ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( part1, part2 ) );

        results = createAdminContext().callWith( () -> schemaService.listParts( ApplicationKey.from( "my_other_app" ) ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( part3 ) );

    }

    @Test
    void listContentTypes()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        List<SchemaResult<ContentType>> results =
            createAdminContext().callWith( () -> schemaService.listContentTypes( applicationKey ) );

        assertTrue( results.isEmpty() );

        SchemaResult<ContentType> contentType1 = createAdminContext().callWith( () -> schemaService.createContentType(
            CreateContentSchemaParams.create()
                .name( ContentTypeName.from( "myapp:mytype1" ) )
                .resource( readResource( "_contentType.yaml" ) )
                .build() ) );
        SchemaResult<ContentType> contentType2 = createAdminContext().callWith( () -> schemaService.createContentType(
            CreateContentSchemaParams.create()
                .name( ContentTypeName.from( "myapp:mytype2" ) )
                .resource( readResource( "_contentType.yaml" ) )
                .build() ) );
        SchemaResult<ContentType> contentType3 = createAdminContext().callWith( () -> schemaService.createContentType(
            CreateContentSchemaParams.create()
                .name( ContentTypeName.from( "my_other_app:mytype" ) )
                .resource( readResource( "_contentType.yaml" ) )
                .build() ) );

        results = createAdminContext().callWith( () -> schemaService.listContentTypes( applicationKey ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( contentType1, contentType2 ) );

        results = createAdminContext().callWith( () -> schemaService.listContentTypes( ApplicationKey.from( "my_other_app" ) ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( contentType3 ) );

    }

    @Test
    void listFormFragments()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        List<SchemaResult<FormFragmentDescriptor>> results =
            createAdminContext().callWith( () -> schemaService.listFormFragments( applicationKey ) );

        assertTrue( results.isEmpty() );

        SchemaResult<FormFragmentDescriptor> fragment1 = createAdminContext().callWith(
            () -> schemaService.createFormFragment( CreateContentSchemaParams.create()
                                                                .name( FormFragmentName.from( "myapp:mytype1" ) )
                                                                .resource( readResource( "_formFragment.yaml" ) )
                                                                .build() ) );
        SchemaResult<FormFragmentDescriptor> fragment2 = createAdminContext().callWith(
            () -> schemaService.createFormFragment( CreateContentSchemaParams.create()
                                                                .name( FormFragmentName.from( "myapp:mytype2" ) )
                                                                .resource( readResource( "_formFragment.yaml" ) )
                                                                .build() ) );
        SchemaResult<FormFragmentDescriptor> fragment3 = createAdminContext().callWith(
            () -> schemaService.createFormFragment( CreateContentSchemaParams.create()
                                                                .name( FormFragmentName.from( "my_other_app:mytype" ) )
                                                                .resource( readResource( "_formFragment.yaml" ) )
                                                                .build() ) );

        results = createAdminContext().callWith( () -> schemaService.listFormFragments( applicationKey ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( fragment1, fragment2 ) );

        results = createAdminContext().callWith( () -> schemaService.listFormFragments( ApplicationKey.from( "my_other_app" ) ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( fragment3 ) );

    }

    @Test
    void listFormFragmentsAsSchemaAdmin()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        List<SchemaResult<FormFragmentDescriptor>> results =
            createSchemaAdminContext().callWith( () -> schemaService.listFormFragments( applicationKey ) );

        assertTrue( results.isEmpty() );

        SchemaResult<FormFragmentDescriptor> fragment1 = createAdminContext().callWith(
            () -> schemaService.createFormFragment( CreateContentSchemaParams.create()
                                                                .name( FormFragmentName.from( "myapp:mytype1" ) )
                                                                .resource( readResource( "_formFragment.yaml" ) )
                                                                .build() ) );
        SchemaResult<FormFragmentDescriptor> fragment2 = createAdminContext().callWith(
            () -> schemaService.createFormFragment( CreateContentSchemaParams.create()
                                                                .name( FormFragmentName.from( "myapp:mytype2" ) )
                                                                .resource( readResource( "_formFragment.yaml" ) )
                                                                .build() ) );
        SchemaResult<FormFragmentDescriptor> fragment3 = createAdminContext().callWith(
            () -> schemaService.createFormFragment( CreateContentSchemaParams.create()
                                                                .name( FormFragmentName.from( "my_other_app:mytype" ) )
                                                                .resource( readResource( "_formFragment.yaml" ) )
                                                                .build() ) );

        results = createAdminContext().callWith( () -> schemaService.listFormFragments( applicationKey ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( fragment1, fragment2 ) );

        results = createAdminContext().callWith( () -> schemaService.listFormFragments( ApplicationKey.from( "my_other_app" ) ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( fragment3 ) );

    }

    @Test
    void listFormFragmentsAsNonSchemaAdmin()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        List<SchemaResult<FormFragmentDescriptor>> results =
            createSchemaAdminContext().callWith( () -> schemaService.listFormFragments( applicationKey ) );

        assertTrue( results.isEmpty() );

        createAdminContext().callWith( () -> schemaService.createFormFragment( CreateContentSchemaParams.create()
                                                                                           .name( FormFragmentName.from( "myapp:mytype1" ) )
                                                                                           .resource( readResource( "_formFragment.yaml" ) )
                                                                                           .build() ) );
        createAdminContext().callWith( () -> schemaService.createFormFragment( CreateContentSchemaParams.create()
                                                                                           .name( FormFragmentName.from( "myapp:mytype2" ) )
                                                                                           .resource( readResource( "_formFragment.yaml" ) )
                                                                                           .build() ) );
        createAdminContext().callWith( () -> schemaService.createFormFragment( CreateContentSchemaParams.create()
                                                                                           .name( FormFragmentName.from(
                                                                                               "my_other_app:mytype" ) )
                                                                                           .resource( readResource( "_formFragment.yaml" ) )
                                                                                           .build() ) );

        assertThrows( ForbiddenAccessException.class, () -> NamespaceContext.createContext()
            .callWith( () -> schemaService.listFormFragments( applicationKey ) ) );
    }

    @Test
    void listFormFragmentsWithSiteConfigAccess()
    {
        final SchemaResult<FormFragmentDescriptor> fragment = createAdminContext().callWith(
            () -> schemaService.createFormFragment( CreateContentSchemaParams.create()
                                                                .name( FormFragmentName.from( "myapp:mytype1" ) )
                                                                .resource( readResource( "_formFragment.yaml" ) )
                                                                .build() ) );

        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        List<SchemaResult<FormFragmentDescriptor>> results =
            createContentManagerAdminContext().callWith( () -> schemaService.listFormFragments( applicationKey ) );
        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( fragment ) );

        results = createProjectRoleContext( ProjectRole.OWNER ).callWith( () -> schemaService.listFormFragments( applicationKey ) );
        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( fragment ) );
    }

    @Test
    void listFormFragmentsAsNonOwnerProjectRole()
    {
        createAdminContext().callWith( () -> schemaService.createFormFragment( CreateContentSchemaParams.create()
                                                                                           .name( FormFragmentName.from( "myapp:mytype1" ) )
                                                                                           .resource( readResource( "_formFragment.yaml" ) )
                                                                                           .build() ) );

        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        assertThrows( ForbiddenAccessException.class, () -> createProjectRoleContext( ProjectRole.VIEWER ).callWith(
            () -> schemaService.listFormFragments( applicationKey ) ) );
        assertThrows( ForbiddenAccessException.class, () -> createProjectRoleContext( ProjectRole.EDITOR ).callWith(
            () -> schemaService.listFormFragments( applicationKey ) ) );
    }

    @Test
    void getFormFragmentWithProjectRole()
    {
        final SchemaResult<FormFragmentDescriptor> fragment = createAdminContext().callWith(
            () -> schemaService.createFormFragment( CreateContentSchemaParams.create()
                                                                .name( FormFragmentName.from( "myapp:mytype1" ) )
                                                                .resource( readResource( "_formFragment.yaml" ) )
                                                                .build() ) );

        final FormFragmentName name = FormFragmentName.from( "myapp:mytype1" );

        for ( final ProjectRole projectRole : ProjectRole.values() )
        {
            final SchemaResult<FormFragmentDescriptor> result =
                createProjectRoleContext( projectRole ).callWith( () -> schemaService.getFormFragment( name ) );
            assertThat( result ).usingRecursiveComparison().isEqualTo( fragment );
        }

        final SchemaResult<FormFragmentDescriptor> result =
            createContentManagerAdminContext().callWith( () -> schemaService.getFormFragment( name ) );
        assertThat( result ).usingRecursiveComparison().isEqualTo( fragment );

        assertThrows( ForbiddenAccessException.class, () -> NamespaceContext.createContext()
            .callWith( () -> schemaService.getFormFragment( name ) ) );
    }

    @Test
    void installGlobalApplicationPersistsSchema()
        throws Exception
    {
        final String contentTypeResource = readResource( "_contentType.yaml" );
        final String cmsResource = "kind: \"CMS\"\nform: [ ]\n";
        final String phrasesResource = "action.save=Save\naction.delete=Delete\n";

        final ByteSource app = createAppSource( "myglobalapp", "1.0.0",
                                                Map.of( "cms/cms.yaml", cmsResource, "cms/content-types/mytype/mytype.yml",
                                                        contentTypeResource, "cms/i18n/phrases/phrases_en.properties", phrasesResource,
                                                        "i18n/phrases/phrases_en.properties", "outside=schema" ) );

        createAdminContext().runWith( () -> applicationService.installGlobalApplication( app ) );

        assertTrue( createAdminContext().callWith( () -> schemaService.listNamespaces() )
                        .stream()
                        .anyMatch( namespace -> "myglobalapp".equals( namespace.getKey().toString() ) ) );

        final Node resourceNode = NamespaceContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myglobalapp/cms/content-types/mytype/mytype.yaml" ) ) );

        assertNotNull( resourceNode );
        assertEquals( contentTypeResource, resourceNode.data().getString( "resource" ) );

        final Node phrasesNode = NamespaceContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myglobalapp/cms/i18n/phrases/phrases_en.properties" ) ) );

        assertNotNull( phrasesNode );
        assertEquals( phrasesResource, phrasesNode.data().getString( "resource" ) );

        // app-root /i18n is not part of the namespace schema
        assertFalse( NamespaceContext.createAdminContext()
                         .callWith( () -> nodeService.nodeExists( new NodePath( "/myglobalapp/i18n" ) ) ) );

        final Resource phrasesFromNode = createAdminContext().callWith( () -> resourceService.getResource(
            ResourceKey.from( ApplicationKey.from( "myglobalapp" ), "cms/i18n/phrases/phrases_en.properties" ) ) );

        assertTrue( phrasesFromNode.exists() );
        assertEquals( "node", phrasesFromNode.getResolverName() );
        assertEquals( phrasesResource, phrasesFromNode.readString() );

        final SchemaResult<ContentType> schema = createAdminContext().callWith(
            () -> schemaService.getContentType( ContentTypeName.from( "myglobalapp:mytype" ) ) );

        assertNotNull( schema );
        assertEquals( "node", schema.getResource().getResolverName() );
        assertEquals( contentTypeResource, schema.getResource().readString() );

        final ByteSource updatedApp = createAppSource( "myglobalapp", "1.0.1",
                                                       Map.of( "cms/cms.yaml", cmsResource, "cms/content-types/newtype/newtype.yaml",
                                                               contentTypeResource ) );

        createAdminContext().runWith( () -> applicationService.installGlobalApplication( updatedApp ) );

        assertFalse( NamespaceContext.createAdminContext()
                         .callWith(
                             () -> nodeService.nodeExists( new NodePath( "/myglobalapp/cms/content-types/mytype/mytype.yaml" ) ) ) );

        // update wipes the cms subtree: phrases from the previous version are gone, the skeleton folder remains
        assertFalse( NamespaceContext.createAdminContext()
                         .callWith(
                             () -> nodeService.nodeExists( new NodePath( "/myglobalapp/cms/i18n/phrases/phrases_en.properties" ) ) ) );
        assertTrue( NamespaceContext.createAdminContext()
                        .callWith( () -> nodeService.nodeExists( new NodePath( "/myglobalapp/cms/i18n/phrases" ) ) ) );

        assertNull( createAdminContext().callWith(
            () -> schemaService.getContentType( ContentTypeName.from( "myglobalapp:mytype" ) ) ) );

        final SchemaResult<ContentType> newSchema = createAdminContext().callWith(
            () -> schemaService.getContentType( ContentTypeName.from( "myglobalapp:newtype" ) ) );

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
            NamespaceContext.createAdminContext().callWith( () -> nodeService.nodeExists( new NodePath( "/mynoncmsapp" ) ) ) );
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

        List<SchemaResult<MixinDescriptor>> results =
            createAdminContext().callWith( () -> schemaService.listMixins( applicationKey ) );

        assertTrue( results.isEmpty() );

        SchemaResult<MixinDescriptor> mixin1 = createAdminContext().callWith( () -> schemaService.createMixin(
            CreateContentSchemaParams.create()
                .name( MixinName.from( "myapp:mytype1" ) )
                .resource( readResource( "_mixin.yaml" ) )
                .build() ) );
        SchemaResult<MixinDescriptor> mixin2 = createAdminContext().callWith( () -> schemaService.createMixin(
            CreateContentSchemaParams.create()
                .name( MixinName.from( "myapp:mytype2" ) )
                .resource( readResource( "_mixin.yaml" ) )
                .build() ) );
        SchemaResult<MixinDescriptor> mixin3 = createAdminContext().callWith( () -> schemaService.createMixin(
            CreateContentSchemaParams.create()
                .name( MixinName.from( "my_other_app:mytype" ) )
                .resource( readResource( "_mixin.yaml" ) )
                .build() ) );

        results = createAdminContext().callWith( () -> schemaService.listMixins( applicationKey ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( mixin1, mixin2 ) );

        results = createAdminContext().callWith( () -> schemaService.listMixins( ApplicationKey.from( "my_other_app" ) ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( mixin3 ) );

    }

    @Test
    void deleteContentTypeComponent()
    {
        SchemaResult<ContentType> contentType = createAdminContext().callWith( () -> schemaService.createContentType(
            CreateContentSchemaParams.create()
                .name( ContentTypeName.from( "myapp:mytype" ) )
                .resource( readResource( "_contentType.yaml" ) )
                .build() ) );

        final boolean result =
            createAdminContext().callWith( () -> schemaService.deleteContentType( contentType.getSchema().getName() ) );

        assertThat( result ).isTrue();

        assertThat( createAdminContext().callWith(
            () -> schemaService.listContentTypes( ApplicationKey.from( "myapp" ) ) ) ).isEmpty();

        assertThat( createAdminContext().callWith(
            () -> schemaService.getContentType( contentType.getSchema().getName() ) ) ).isNull();
    }

    @Test
    void deleteContentTypeComponentAsSchemaAdmin()
    {
        SchemaResult<ContentType> contentType = createSchemaAdminContext().callWith( () -> schemaService.createContentType(
            CreateContentSchemaParams.create()
                .name( ContentTypeName.from( "myapp:mytype" ) )
                .resource( readResource( "_contentType.yaml" ) )
                .build() ) );

        final boolean result =
            createSchemaAdminContext().callWith( () -> schemaService.deleteContentType( contentType.getSchema().getName() ) );

        assertThat( result ).isTrue();
    }

    @Test
    void deleteContentTypeComponentAsNonSchemaAdmin()
    {
        SchemaResult<ContentType> contentType = createSchemaAdminContext().callWith( () -> schemaService.createContentType(
            CreateContentSchemaParams.create()
                .name( ContentTypeName.from( "myapp:mytype" ) )
                .resource( readResource( "_contentType.yaml" ) )
                .build() ) );

        assertThrows( ForbiddenAccessException.class, () -> NamespaceContext.createContext()
            .callWith( () -> schemaService.deleteContentType( contentType.getSchema().getName() ) ) );

    }


    @Test
    void deletePartComponent()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        SchemaResult<PartDescriptor> part = createAdminContext().callWith( () -> schemaService.createPart(
            CreateComponentParams.create()
                .descriptorKey( DescriptorKey.from( "myapp:mypart" ) )
                .resource( readResource( "_part.yaml" ) )
                .build() ) );

        final boolean result = createAdminContext().callWith( () -> schemaService.deletePart( part.getSchema().getKey() ) );

        assertTrue( result );

        assertThat( createAdminContext().callWith( () -> schemaService.listParts( applicationKey ) ) ).isEmpty();

        assertThat( createAdminContext().callWith(
            () -> schemaService.getPart( part.getSchema().getKey() ) ) ).usingRecursiveComparison().isNull();

    }

    @Test
    void createMacro()
        throws Exception
    {
        final String resource = readResource( "_macro.yaml" );

        final CreateMacroParams params =
            CreateMacroParams.create().key( MacroKey.from( "myapp:mymacro" ) ).resource( resource ).build();

        final SchemaResult<MacroDescriptor> result =
            createAdminContext().callWith( () -> schemaService.createMacro( params ) );

        final MacroDescriptor macroDescriptor = result.getSchema();

        createAdminContext().runWith( () -> assertThat( macroDescriptor ).usingRecursiveComparison()
            .isEqualTo( schemaService.getMacro(
                GetMacroParams.create().key( MacroKey.from( "myapp:mymacro" ) ).build() ).getSchema() ) );

        assertEquals( "mymacro", macroDescriptor.getName() );
        assertEquals( "myapp", macroDescriptor.getKey().getApplicationKey().toString() );
        assertEquals( "Dynamic Macro", macroDescriptor.getTitle() );
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

        final Node resourceNode = NamespaceContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/macros/mymacro/mymacro.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void updateMacro()
        throws Exception
    {
        final CreateMacroParams createParams = CreateMacroParams.create()
            .key( MacroKey.from( "myapp:mymacro" ) )
            .resource( """
                           kind: "Macro"
                           title: "MyMacro"
                           form: [ ]
                           """ )
            .build();

        createAdminContext().runWith( () -> schemaService.createMacro( createParams ) );

        final String resource = readResource( "_macro.yaml" );

        final UpdateMacroParams updateParams =
            UpdateMacroParams.create().key( MacroKey.from( "myapp:mymacro" ) ).resource( resource ).build();

        final SchemaResult<MacroDescriptor> result =
            createAdminContext().callWith( () -> schemaService.updateMacro( updateParams ) );

        assertEquals( "Dynamic Macro", result.getSchema().getTitle() );
        assertEquals( resource, result.getResource().readString() );
    }

    @Test
    void listMacros()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        assertThat( createAdminContext().callWith( () -> schemaService.listMacros(
            ListMacrosParams.create().applicationKey( applicationKey ).build() ) ) ).isEmpty();

        SchemaResult<MacroDescriptor> macro1 = createAdminContext().callWith( () -> schemaService.createMacro(
            CreateMacroParams.create().key( MacroKey.from( "myapp:mymacro1" ) ).resource( readResource( "_macro.yaml" ) ).build() ) );
        SchemaResult<MacroDescriptor> macro2 = createAdminContext().callWith( () -> schemaService.createMacro(
            CreateMacroParams.create().key( MacroKey.from( "myapp:mymacro2" ) ).resource( readResource( "_macro.yaml" ) ).build() ) );
        createAdminContext().callWith( () -> schemaService.createMacro(
            CreateMacroParams.create()
                .key( MacroKey.from( "my_other_app:mymacro" ) )
                .resource( readResource( "_macro.yaml" ) )
                .build() ) );

        final List<SchemaResult<MacroDescriptor>> results = createAdminContext().callWith(
            () -> schemaService.listMacros( ListMacrosParams.create().applicationKey( applicationKey ).build() ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( macro1, macro2 ) );
    }

    @Test
    void deleteMacro()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        SchemaResult<MacroDescriptor> macro = createAdminContext().callWith( () -> schemaService.createMacro(
            CreateMacroParams.create().key( MacroKey.from( "myapp:mymacro" ) ).resource( readResource( "_macro.yaml" ) ).build() ) );

        final boolean result = createAdminContext().callWith(
            () -> schemaService.deleteMacro( DeleteMacroParams.create().key( macro.getSchema().getKey() ).build() ) );

        assertTrue( result );

        assertThat( createAdminContext().callWith( () -> schemaService.listMacros(
            ListMacrosParams.create().applicationKey( applicationKey ).build() ) ) ).isEmpty();

        assertThat( createAdminContext().callWith( () -> schemaService.getMacro(
            GetMacroParams.create().key( macro.getSchema().getKey() ).build() ) ) ).usingRecursiveComparison().isNull();
    }

    @Test
    void createContentTypeSchemaInvalid()
    {
        final String resource = "unsupportedField: [ ]";

        CreateContentSchemaParams params = CreateContentSchemaParams.create()
            .name( ContentTypeName.from( "myapp:mytype" ) )
            .resource( resource )
            .build();

        final RuntimeException exception = assertThrows( RuntimeException.class, () -> createAdminContext().callWith(
            () -> schemaService.createContentType( params ) ) );

        assertEquals( "Could not parse dynamic content type [myapp:mytype]", exception.getMessage() );
    }

    @Test
    public void createFormFragmentSchemaInvalid()
    {
        final String resource = """
            kind: "FormFragment"
            unsupportedField: [ ]
            """;

        CreateContentSchemaParams params = CreateContentSchemaParams.create()
            .name( FormFragmentName.from( "myapp:mytype" ) )
            .resource( resource )
            .build();

        assertThrows( UncheckedIOException.class,
                      () -> createAdminContext().callWith( () -> schemaService.createFormFragment( params ) ) );
    }

    @Test
    void createMixinSchemaInvalid()
    {
        final String resource = """
            kind: "Mixin"
            unsupportedField: [ ]
            """;

        CreateContentSchemaParams params = CreateContentSchemaParams.create()
            .name( MixinName.from( "myapp:mytype" ) )
            .resource( resource )
            .build();

        assertThrows( UncheckedIOException.class,
                      () -> createAdminContext().callWith( () -> schemaService.createMixin( params ) ) );
    }

    @Test
    void createPartInvalid()
    {
        final String resource = """
            kind: "Part"        
            unsupportedField: [ ]
            """;

        final CreateComponentParams params = CreateComponentParams.create()
            .descriptorKey( DescriptorKey.from( "myapp:mytype" ) )
            .resource( resource )
            .build();

        assertThrows( UncheckedIOException.class,
                      () -> createAdminContext().callWith( () -> schemaService.createPart( params ) ) );
    }

    @Test
    void createLayoutInvalid()
    {
        final String resource = """
            kind: "Layout"
            unsupportedField: [ ]
            """;

        final CreateComponentParams params = CreateComponentParams.create()
            .descriptorKey( DescriptorKey.from( "myapp:mytype" ) )
            .resource( resource )
            .build();

        assertThrows( UncheckedIOException.class,
                      () -> createAdminContext().callWith( () -> schemaService.createLayout( params ) ) );
    }

    @Test
    void createPageInvalid()
    {
        final String resource = """
            kind: "Page"
            unsupportedField: [ ]
            """;

        final CreateComponentParams params = CreateComponentParams.create()
            .descriptorKey( DescriptorKey.from( "myapp:mytype" ) )
            .resource( resource )
            .build();

        assertThrows( UncheckedIOException.class,
                      () -> createAdminContext().callWith( () -> schemaService.createPage( params ) ) );
    }

    @Test
    void createStylesInvalid()
    {
        final String resource = """
            kind: "Style"
            unsupportedField: [ ]
            """;

        final CreateStylesParams params =
            CreateStylesParams.create().key( ApplicationKey.from( "myapp" ) ).resource( resource ).build();

        assertThrows( Exception.class, () -> createAdminContext().callWith( () -> schemaService.createStyles( params ) ) );
    }

    @Test
    void createSiteInvalid()
    {
        final String resource = """
            kind: "CMS"
            unsupportedField: [ ]
            """;

        final CreateCmsParams params =
            CreateCmsParams.create().key( ApplicationKey.from( "myapp" ) ).resource( resource ).build();

        assertThrows( Exception.class, () -> createAdminContext().callWith( () -> schemaService.createCms( params ) ) );
    }


    @Test
    void setContentTypeIcon()
        throws Exception
    {
        createAdminContext().callWith( () -> schemaService.createContentType( CreateContentSchemaParams.create()
                                                                                  .name( ContentTypeName.from( "myapp:mytype" ) )
                                                                                  .resource( readResource( "_contentType.yaml" ) )
                                                                                  .build() ) );

        final byte[] iconData = "<svg/>".getBytes( StandardCharsets.UTF_8 );

        final Icon icon = createAdminContext().callWith( () -> schemaService.setContentTypeIcon( SetSchemaIconParams.create()
                                                                                                     .name( ContentTypeName.from(
                                                                                                         "myapp:mytype" ) )
                                                                                                     .data( ByteSource.wrap( iconData ) )
                                                                                                     .mimeType( "image/svg+xml" )
                                                                                                     .build() ) );

        assertEquals( "image/svg+xml", icon.getMimeType() );
        assertArrayEquals( iconData, icon.toByteArray() );

        final Node iconNode = NamespaceContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/content-types/mytype/mytype.svg" ) ) );

        assertEquals( "image/svg+xml", iconNode.data().getString( "mimeType" ) );
        assertNull( iconNode.data().getString( "resource" ) );
        assertNotNull( iconNode.getAttachedBinaries().getByBinaryReference( BinaryReference.from( "icon" ) ) );
        assertArrayEquals( iconData, NamespaceContext.createAdminContext()
            .callWith( () -> nodeService.getBinary( iconNode.id(), BinaryReference.from( "icon" ) ) )
            .read() );

        final Node yamlNode = NamespaceContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/myapp/cms/content-types/mytype/mytype.yaml" ) ) );
        assertNotNull( yamlNode.data().getInstant( "iconModifiedTime" ) );

        final Icon fetched =
            createAdminContext().callWith( () -> schemaService.getContentTypeIcon( ContentTypeName.from( "myapp:mytype" ) ) );
        assertEquals( "image/svg+xml", fetched.getMimeType() );
        assertArrayEquals( iconData, fetched.toByteArray() );

        final ContentType contentType =
            createAdminContext().callWith( () -> schemaService.getContentType( ContentTypeName.from( "myapp:mytype" ) ) ).getSchema();
        assertNotNull( contentType.getIcon() );
        assertArrayEquals( iconData, contentType.getIcon().toByteArray() );

        final List<SchemaResult<ContentType>> listed =
            createAdminContext().callWith( () -> schemaService.listContentTypes( ApplicationKey.from( "myapp" ) ) );
        assertNotNull( listed.get( 0 ).getSchema().getIcon() );
    }

    @Test
    void setContentTypeIconReplacesPreviousFormat()
        throws Exception
    {
        createAdminContext().callWith( () -> schemaService.createContentType( CreateContentSchemaParams.create()
                                                                                  .name( ContentTypeName.from( "myapp:mytype" ) )
                                                                                  .resource( readResource( "_contentType.yaml" ) )
                                                                                  .build() ) );

        createAdminContext().callWith( () -> schemaService.setContentTypeIcon( SetSchemaIconParams.create()
                                                                                   .name( ContentTypeName.from( "myapp:mytype" ) )
                                                                                   .data( ByteSource.wrap(
                                                                                       "<svg/>".getBytes( StandardCharsets.UTF_8 ) ) )
                                                                                   .mimeType( "image/svg+xml" )
                                                                                   .build() ) );

        final byte[] pngData = {(byte) 0x89, 'P', 'N', 'G'};
        createAdminContext().callWith( () -> schemaService.setContentTypeIcon( SetSchemaIconParams.create()
                                                                                   .name( ContentTypeName.from( "myapp:mytype" ) )
                                                                                   .data( ByteSource.wrap( pngData ) )
                                                                                   .mimeType( "image/png" )
                                                                                   .build() ) );

        NamespaceContext.createAdminContext().runWith( () -> {
            assertNull( nodeService.getByPath( new NodePath( "/myapp/cms/content-types/mytype/mytype.svg" ) ) );
            assertNotNull( nodeService.getByPath( new NodePath( "/myapp/cms/content-types/mytype/mytype.png" ) ) );
        } );

        final Icon fetched =
            createAdminContext().callWith( () -> schemaService.getContentTypeIcon( ContentTypeName.from( "myapp:mytype" ) ) );
        assertEquals( "image/png", fetched.getMimeType() );
        assertArrayEquals( pngData, fetched.toByteArray() );
    }

    @Test
    void setContentTypeIconForMissingSchema()
    {
        final SetSchemaIconParams params = SetSchemaIconParams.create()
            .name( ContentTypeName.from( "myapp:mytype" ) )
            .data( ByteSource.wrap( "<svg/>".getBytes( StandardCharsets.UTF_8 ) ) )
            .mimeType( "image/svg+xml" )
            .build();

        assertThrows( SchemaNotFoundException.class,
                      () -> createAdminContext().callWith( () -> schemaService.setContentTypeIcon( params ) ) );
    }

    @Test
    void setContentTypeIconInvalidMimeType()
    {
        final SetSchemaIconParams params = SetSchemaIconParams.create()
            .name( ContentTypeName.from( "myapp:mytype" ) )
            .data( ByteSource.wrap( "<svg/>".getBytes( StandardCharsets.UTF_8 ) ) )
            .mimeType( "text/plain" )
            .build();

        final IllegalArgumentException exception = assertThrows( IllegalArgumentException.class, () -> createAdminContext().callWith(
            () -> schemaService.setContentTypeIcon( params ) ) );

        assertEquals( "unsupported icon mime type: text/plain", exception.getMessage() );
    }

    @Test
    void setContentTypeIconEmptyData()
    {
        final SetSchemaIconParams params = SetSchemaIconParams.create()
            .name( ContentTypeName.from( "myapp:mytype" ) )
            .data( ByteSource.empty() )
            .mimeType( "image/svg+xml" )
            .build();

        assertThrows( IllegalArgumentException.class,
                      () -> createAdminContext().callWith( () -> schemaService.setContentTypeIcon( params ) ) );
    }

    @Test
    void setContentTypeIconExceedingMaxSize()
    {
        final SetSchemaIconParams params = SetSchemaIconParams.create()
            .name( ContentTypeName.from( "myapp:mytype" ) )
            .data( ByteSource.wrap( new byte[100 * 1024 + 1] ) )
            .mimeType( "image/svg+xml" )
            .build();

        assertThrows( IllegalArgumentException.class,
                      () -> createAdminContext().callWith( () -> schemaService.setContentTypeIcon( params ) ) );
    }

    @Test
    void setContentTypeIconWithoutAdminRole()
    {
        final SetSchemaIconParams params = SetSchemaIconParams.create()
            .name( ContentTypeName.from( "myapp:mytype" ) )
            .data( ByteSource.wrap( "<svg/>".getBytes( StandardCharsets.UTF_8 ) ) )
            .mimeType( "image/svg+xml" )
            .build();

        assertThrows( ForbiddenAccessException.class,
                      () -> NamespaceContext.createContext().callWith( () -> schemaService.setContentTypeIcon( params ) ) );
    }

    @Test
    void deleteContentTypeIcon()
        throws Exception
    {
        createAdminContext().callWith( () -> schemaService.createContentType( CreateContentSchemaParams.create()
                                                                                  .name( ContentTypeName.from( "myapp:mytype" ) )
                                                                                  .resource( readResource( "_contentType.yaml" ) )
                                                                                  .build() ) );

        createAdminContext().callWith( () -> schemaService.setContentTypeIcon( SetSchemaIconParams.create()
                                                                                   .name( ContentTypeName.from( "myapp:mytype" ) )
                                                                                   .data( ByteSource.wrap(
                                                                                       "<svg/>".getBytes( StandardCharsets.UTF_8 ) ) )
                                                                                   .mimeType( "image/svg+xml" )
                                                                                   .build() ) );

        assertTrue(
            createAdminContext().callWith( () -> schemaService.deleteContentTypeIcon( ContentTypeName.from( "myapp:mytype" ) ) ) );

        assertNull( createAdminContext().callWith( () -> schemaService.getContentTypeIcon( ContentTypeName.from( "myapp:mytype" ) ) ) );
        assertNotNull( createAdminContext().callWith( () -> schemaService.getContentType( ContentTypeName.from( "myapp:mytype" ) ) ) );

        assertFalse(
            createAdminContext().callWith( () -> schemaService.deleteContentTypeIcon( ContentTypeName.from( "myapp:mytype" ) ) ) );
    }

    @Test
    void deleteContentTypeCascadesIcon()
        throws Exception
    {
        createAdminContext().callWith( () -> schemaService.createContentType( CreateContentSchemaParams.create()
                                                                                  .name( ContentTypeName.from( "myapp:mytype" ) )
                                                                                  .resource( readResource( "_contentType.yaml" ) )
                                                                                  .build() ) );

        createAdminContext().callWith( () -> schemaService.setContentTypeIcon( SetSchemaIconParams.create()
                                                                                   .name( ContentTypeName.from( "myapp:mytype" ) )
                                                                                   .data( ByteSource.wrap(
                                                                                       "<svg/>".getBytes( StandardCharsets.UTF_8 ) ) )
                                                                                   .mimeType( "image/svg+xml" )
                                                                                   .build() ) );

        assertTrue( createAdminContext().callWith( () -> schemaService.deleteContentType( ContentTypeName.from( "myapp:mytype" ) ) ) );

        NamespaceContext.createAdminContext().runWith( () -> {
            assertNull( nodeService.getByPath( new NodePath( "/myapp/cms/content-types/mytype/mytype.svg" ) ) );
            assertNull( nodeService.getByPath( new NodePath( "/myapp/cms/content-types/mytype/mytype.yaml" ) ) );
        } );
    }

    @Test
    void setPartIcon()
        throws Exception
    {
        createAdminContext().callWith( () -> schemaService.createPart( CreateComponentParams.create()
                                                                           .descriptorKey( DescriptorKey.from( "myapp:mypart" ) )
                                                                           .resource( readResource( "_part.yaml" ) )
                                                                           .build() ) );

        final byte[] iconData = "<svg/>".getBytes( StandardCharsets.UTF_8 );

        createAdminContext().callWith( () -> schemaService.setPartIcon( SetComponentIconParams.create()
                                                                            .descriptorKey( DescriptorKey.from( "myapp:mypart" ) )
                                                                            .data( ByteSource.wrap( iconData ) )
                                                                            .mimeType( "image/svg+xml" )
                                                                            .build() ) );

        final Icon fetched = createAdminContext().callWith( () -> schemaService.getPartIcon( DescriptorKey.from( "myapp:mypart" ) ) );
        assertArrayEquals( iconData, fetched.toByteArray() );

        final PartDescriptor partDescriptor =
            createAdminContext().callWith( () -> schemaService.getPart( DescriptorKey.from( "myapp:mypart" ) ) ).getSchema();
        assertNotNull( partDescriptor.getIcon() );
        assertArrayEquals( iconData, partDescriptor.getIcon().toByteArray() );

        assertTrue( createAdminContext().callWith( () -> schemaService.deletePartIcon( DescriptorKey.from( "myapp:mypart" ) ) ) );
        assertNull( createAdminContext().callWith( () -> schemaService.getPartIcon( DescriptorKey.from( "myapp:mypart" ) ) ) );
    }

    @Test
    void setMacroIcon()
        throws Exception
    {
        createAdminContext().callWith( () -> schemaService.createMacro(
            CreateMacroParams.create().key( MacroKey.from( "myapp:mymacro" ) ).resource( readResource( "_macro.yaml" ) ).build() ) );

        final byte[] iconData = "<svg/>".getBytes( StandardCharsets.UTF_8 );

        createAdminContext().callWith( () -> schemaService.setMacroIcon( SetMacroIconParams.create()
                                                                             .key( MacroKey.from( "myapp:mymacro" ) )
                                                                             .data( ByteSource.wrap( iconData ) )
                                                                             .mimeType( "image/svg+xml" )
                                                                             .build() ) );

        final Icon fetched = createAdminContext().callWith( () -> schemaService.getMacroIcon( MacroKey.from( "myapp:mymacro" ) ) );
        assertArrayEquals( iconData, fetched.toByteArray() );

        final MacroDescriptor macroDescriptor = createAdminContext().callWith(
            () -> schemaService.getMacro( GetMacroParams.create().key( MacroKey.from( "myapp:mymacro" ) ).build() ) ).getSchema();
        assertNotNull( macroDescriptor.getIcon() );
        assertArrayEquals( iconData, macroDescriptor.getIcon().toByteArray() );

        assertTrue( createAdminContext().callWith( () -> schemaService.deleteMacroIcon( MacroKey.from( "myapp:mymacro" ) ) ) );
        assertNull( createAdminContext().callWith( () -> schemaService.getMacroIcon( MacroKey.from( "myapp:mymacro" ) ) ) );
    }

    @Test
    void setMixinIcon()
        throws Exception
    {
        createAdminContext().callWith( () -> schemaService.createMixin( CreateContentSchemaParams.create()
                                                                            .name( MixinName.from( "myapp:mymixin" ) )
                                                                            .resource( readResource( "_mixin.yaml" ) )
                                                                            .build() ) );

        final byte[] iconData = "<svg/>".getBytes( StandardCharsets.UTF_8 );

        createAdminContext().callWith( () -> schemaService.setMixinIcon( SetSchemaIconParams.create()
                                                                             .name( MixinName.from( "myapp:mymixin" ) )
                                                                             .data( ByteSource.wrap( iconData ) )
                                                                             .mimeType( "image/svg+xml" )
                                                                             .build() ) );

        final Icon fetched = createAdminContext().callWith( () -> schemaService.getMixinIcon( MixinName.from( "myapp:mymixin" ) ) );
        assertArrayEquals( iconData, fetched.toByteArray() );

        final MixinDescriptor mixinDescriptor =
            createAdminContext().callWith( () -> schemaService.getMixin( MixinName.from( "myapp:mymixin" ) ) ).getSchema();
        assertNotNull( mixinDescriptor.getIcon() );

        assertTrue( createAdminContext().callWith( () -> schemaService.deleteMixinIcon( MixinName.from( "myapp:mymixin" ) ) ) );
        assertNull( createAdminContext().callWith( () -> schemaService.getMixinIcon( MixinName.from( "myapp:mymixin" ) ) ) );
    }

    @Test
    void setFormFragmentIcon()
        throws Exception
    {
        createAdminContext().callWith( () -> schemaService.createFormFragment( CreateContentSchemaParams.create()
                                                                                   .name( FormFragmentName.from( "myapp:myfragment" ) )
                                                                                   .resource( readResource( "_formFragment.yaml" ) )
                                                                                   .build() ) );

        final byte[] iconData = "<svg/>".getBytes( StandardCharsets.UTF_8 );

        createAdminContext().callWith( () -> schemaService.setFormFragmentIcon( SetSchemaIconParams.create()
                                                                                    .name( FormFragmentName.from( "myapp:myfragment" ) )
                                                                                    .data( ByteSource.wrap( iconData ) )
                                                                                    .mimeType( "image/svg+xml" )
                                                                                    .build() ) );

        final Icon fetched =
            createAdminContext().callWith( () -> schemaService.getFormFragmentIcon( FormFragmentName.from( "myapp:myfragment" ) ) );
        assertArrayEquals( iconData, fetched.toByteArray() );

        final FormFragmentDescriptor descriptor =
            createAdminContext().callWith( () -> schemaService.getFormFragment( FormFragmentName.from( "myapp:myfragment" ) ) )
                .getSchema();
        assertNotNull( descriptor.getIcon() );

        assertTrue(
            createAdminContext().callWith( () -> schemaService.deleteFormFragmentIcon( FormFragmentName.from( "myapp:myfragment" ) ) ) );
        assertNull(
            createAdminContext().callWith( () -> schemaService.getFormFragmentIcon( FormFragmentName.from( "myapp:myfragment" ) ) ) );
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

    private void createDefaultCms( final ApplicationKey applicationKey )
    {
        createAdminContext().runWith( () -> schemaService.createCms( CreateCmsParams.create().key( applicationKey ).resource( """
            kind: "CMS"
            mixins: [ ]
            form: [ ]
            """ ).build() ) );
    }
}
