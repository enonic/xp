package com.enonic.xp.perftest.content;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;

import com.google.common.net.HttpHeaders;

import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessorSupport;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.impl.content.ContentAuditLogFilterService;
import com.enonic.xp.core.impl.content.ContentAuditLogSupportImpl;
import com.enonic.xp.core.impl.content.ContentConfig;
import com.enonic.xp.core.impl.content.ContentServiceImpl;
import com.enonic.xp.core.impl.content.MixinMappingServiceImpl;
import com.enonic.xp.core.impl.content.SiteConfigServiceImpl;
import com.enonic.xp.core.impl.content.schema.ContentTypeServiceImpl;
import com.enonic.xp.core.impl.content.validate.CmsConfigsValidator;
import com.enonic.xp.core.impl.content.validate.ContentNameValidator;
import com.enonic.xp.core.impl.content.validate.MixinValidator;
import com.enonic.xp.core.impl.content.validate.OccurrenceValidator;
import com.enonic.xp.core.impl.event.EventPublisherImpl;
import com.enonic.xp.core.impl.media.MediaInfoServiceImpl;
import com.enonic.xp.core.impl.project.ProjectConfig;
import com.enonic.xp.core.impl.project.ProjectServiceImpl;
import com.enonic.xp.core.impl.security.PasswordSecurityService;
import com.enonic.xp.core.impl.security.SecurityAuditLogSupportImpl;
import com.enonic.xp.core.impl.security.SecurityConfig;
import com.enonic.xp.core.impl.security.SecurityInitializer;
import com.enonic.xp.core.impl.security.SecurityServiceImpl;
import com.enonic.xp.core.impl.site.CmsServiceImpl;
import com.enonic.xp.extractor.BinaryExtractor;
import com.enonic.xp.extractor.ExtractedData;
import com.enonic.xp.form.Form;
import com.enonic.xp.internal.blobstore.MemoryBlobStore;
import com.enonic.xp.itest.EmbeddedElasticsearchServer;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.project.CreateProjectParams;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartDescriptorService;
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
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.content.CmsFormFragmentService;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

/**
 * Wires the minimal XP service graph needed to call ContentService.create.
 * Mirrors AbstractContentServiceTest.setUp without JUnit or Mockito.
 */
public final class Bootstrap
{
    private static final AtomicInteger PROJECT_COUNTER = new AtomicInteger();

    private final User testUser = User.create()
        .key( PrincipalKey.ofUser( IdProviderKey.system(), "perftest-user" ) )
        .login( "perftest-user" )
        .build();

    private final AuthenticationInfo testAuth = AuthenticationInfo.create()
        .principals( RoleKeys.AUTHENTICATED )
        .principals( RoleKeys.CONTENT_MANAGER_ADMIN )
        .user( testUser )
        .build();

    private Path rootDir;

    private EmbeddedElasticsearchServer esServer;

    private ExecutorService executorService;

    public ContentServiceImpl contentService;

    public ProjectName projectName;

    public Context draftContext()
    {
        return ContextBuilder.create()
            .branch( ContentConstants.BRANCH_DRAFT )
            .repositoryId( projectName.getRepoId() )
            .authInfo( testAuth )
            .build();
    }

    public void start()
        throws Exception
    {
        rootDir = Files.createTempDirectory( "ptest-content-" );
        esServer = new EmbeddedElasticsearchServer( rootDir );
        final Client client = esServer.getClient();

        executorService = Executors.newSingleThreadExecutor();
        projectName = ProjectName.from( "ptest" + PROJECT_COUNTER.incrementAndGet() );

        ContextAccessorSupport.getInstance().set( draftContext() );

        final MemoryBlobStore blobStore = new MemoryBlobStore();
        final BinaryServiceImpl binaryService = new BinaryServiceImpl( blobStore );

        final StorageDaoImpl storageDao = new StorageDaoImpl( client );
        final SearchDaoImpl searchDao = new SearchDaoImpl( client );

        final EventPublisherImpl eventPublisher = new EventPublisherImpl( executorService );

        final BranchServiceImpl branchService = new BranchServiceImpl( storageDao, searchDao );
        final VersionServiceImpl versionService = new VersionServiceImpl( storageDao );
        final CommitServiceImpl commitService = new CommitServiceImpl( storageDao );

        final IndexServiceInternalImpl indexServiceInternal = new IndexServiceInternalImpl( client );
        final NodeVersionServiceImpl nodeDao = new NodeVersionServiceImpl( blobStore, new RepoConfiguration( Map.of() ) );
        final IndexDataServiceImpl indexedDataService = new IndexDataServiceImpl( storageDao );

        final NodeStorageServiceImpl storageService =
            new NodeStorageServiceImpl( versionService, branchService, commitService, nodeDao, indexedDataService );

        final NodeSearchServiceImpl searchService = new NodeSearchServiceImpl( searchDao );

        final RepositoryEntryServiceImpl repositoryEntryService =
            new RepositoryEntryServiceImpl( indexServiceInternal, storageService, searchService, eventPublisher, binaryService );

        final IndexServiceImpl indexService =
            new IndexServiceImpl( indexServiceInternal, indexedDataService, searchService, nodeDao, repositoryEntryService );

        final NodeRepositoryServiceImpl nodeRepositoryService = new NodeRepositoryServiceImpl( indexServiceInternal );

        final RepositoryServiceImpl repositoryService =
            new RepositoryServiceImpl( repositoryEntryService, nodeRepositoryService, storageService, searchService, branchService,
                                       () -> null );

        SystemRepoInitializer.create()
            .setIndexServiceInternal( indexServiceInternal )
            .setNodeStorageService( storageService )
            .setNodeRepositoryService( nodeRepositoryService )
            .setRepositoryEntryService( repositoryEntryService )
            .build()
            .initialize();

        final NodeServiceImpl nodeService =
            new NodeServiceImpl( indexServiceInternal, storageService, searchService, eventPublisher, binaryService );

        final CmsFormFragmentService formFragmentService = noOp( CmsFormFragmentService.class,
            ( method, args ) -> "inlineFormItems".equals( method.getName() ) && args.length == 1 && args[0] instanceof Form
                ? args[0]
                : UNSET );

        final MixinService mixinService = noOp( MixinService.class, NONE );

        final Map<String, List<String>> metadata = new HashMap<>();
        metadata.put( HttpHeaders.CONTENT_TYPE, List.of( "image/jpeg" ) );
        final ExtractedData extractedData = ExtractedData.create().metadata( metadata ).build();

        final BinaryExtractor extractor = noOp( BinaryExtractor.class,
            ( method, args ) -> "extract".equals( method.getName() ) ? extractedData : UNSET );

        final MediaInfoServiceImpl mediaInfoService = new MediaInfoServiceImpl( extractor );

        final ResourceService resourceService = noOp( ResourceService.class, NONE );

        final CmsServiceImpl cmsService = new CmsServiceImpl( resourceService, formFragmentService );

        final ContentTypeServiceImpl contentTypeService = new ContentTypeServiceImpl( resourceService, null, formFragmentService );

        final PageDescriptorService pageDescriptorService = noOp( PageDescriptorService.class, NONE );
        final PartDescriptorService partDescriptorService = noOp( PartDescriptorService.class, NONE );
        final LayoutDescriptorService layoutDescriptorService = noOp( LayoutDescriptorService.class, NONE );
        final AuditLogService auditLogService = noOp( AuditLogService.class, NONE );

        final ContentAuditLogFilterService contentAuditLogFilterService =
            noOp( ContentAuditLogFilterService.class, ( method, args ) -> Boolean.TRUE );

        // Disable auditlog for the benchmark to avoid polluting the timing with audit writes.
        final ContentConfig contentConfig = annotationDefaults( ContentConfig.class,
            ( method, args ) -> "auditlog_enabled".equals( method.getName() ) ? Boolean.FALSE : UNSET );

        final ContentAuditLogSupportImpl contentAuditLogSupport =
            new ContentAuditLogSupportImpl( contentConfig, Runnable::run, auditLogService, contentAuditLogFilterService );

        final SecurityConfig securityConfig = annotationDefaults( SecurityConfig.class, NONE );
        final ProjectConfig projectConfig = annotationDefaults( ProjectConfig.class, NONE );

        final SecurityAuditLogSupportImpl securityAuditLogSupport = new SecurityAuditLogSupportImpl( auditLogService );
        securityAuditLogSupport.activate( securityConfig );

        final PasswordSecurityService passwordSecurityService = new PasswordSecurityService();
        passwordSecurityService.activate( securityConfig );

        final SecurityServiceImpl securityService =
            new SecurityServiceImpl( nodeService, securityAuditLogSupport, passwordSecurityService );
        SecurityInitializer.create()
            .setIndexService( indexService )
            .setSecurityService( securityService )
            .setNodeService( nodeService )
            .build()
            .initialize();

        final ProjectServiceImpl projectService =
            new ProjectServiceImpl( repositoryService, repositoryService, indexService, nodeService, securityService, eventPublisher,
                                    projectConfig );
        projectService.initialize();
        projectService.create( CreateProjectParams.create().name( projectName ).displayName( "ptest" ).build() );

        final MixinMappingServiceImpl mixinMappingService = new MixinMappingServiceImpl( cmsService, mixinService );
        final SiteConfigServiceImpl siteConfigService =
            new SiteConfigServiceImpl( nodeService, projectService, contentTypeService, eventPublisher );

        contentService = new ContentServiceImpl( nodeService, pageDescriptorService, partDescriptorService, layoutDescriptorService,
                                                 siteConfigService, contentConfig );
        contentService.setEventPublisher( eventPublisher );
        contentService.setMediaInfoService( mediaInfoService );
        contentService.setCmsService( cmsService );
        contentService.setContentTypeService( contentTypeService );
        contentService.setMixinService( mixinService );
        contentService.setMixinMappingService( mixinMappingService );
        contentService.setContentAuditLogSupport( contentAuditLogSupport );

        contentService.addContentValidator( new ContentNameValidator() );
        contentService.addContentValidator( new CmsConfigsValidator( cmsService ) );
        contentService.addContentValidator( new OccurrenceValidator() );
        contentService.addContentValidator( new MixinValidator( mixinService ) );
    }

    /** Sets index.refresh_interval on all indices. Use "1s" for realistic measurement, "-1" to disable. */
    public void setRefreshInterval( final String value )
    {
        esServer.getClient().admin().indices().prepareUpdateSettings( "*" )
            .setSettings( Settings.builder().put( "index.refresh_interval", value ) ).get();
    }

    /** Force one refresh across all indices. Call this at the end of corpus prep so the data is searchable. */
    public void refresh()
    {
        esServer.getClient().admin().indices().prepareRefresh( "*" ).get();
    }

    /**
     * Sets indices.store.throttle.type cluster-wide. Use "merge" (ES default) for
     * realistic measurement; the embedded ES helper boots with "none" (no merge
     * throttling), which speeds up itest setup but inflates write benchmarks.
     */
    public void setStoreThrottleType( final String value )
    {
        esServer.getClient().admin().cluster().prepareUpdateSettings()
            .setTransientSettings( Settings.builder().put( "indices.store.throttle.type", value ) ).get();
    }

    public void stop()
    {
        if ( esServer != null )
        {
            esServer.shutdown();
        }
        if ( executorService != null )
        {
            executorService.shutdownNow();
        }
        if ( rootDir != null )
        {
            try
            {
                deleteRecursively( rootDir );
            }
            catch ( Exception ignore )
            {
            }
        }
    }

    private static void deleteRecursively( final Path path )
        throws java.io.IOException
    {
        if ( !Files.exists( path ) )
        {
            return;
        }
        Files.walk( path ).sorted( ( a, b ) -> b.compareTo( a ) ).forEach( p -> {
            try
            {
                Files.deleteIfExists( p );
            }
            catch ( java.io.IOException ignore )
            {
            }
        } );
    }

    // --- proxy-based no-op mocks (replaces Mockito) ---------------------------

    /** Sentinel meaning "the override has no opinion about this method - fall back to default". */
    private static final Object UNSET = new Object();

    /** No override at all - every method returns its default value (null / 0 / false). */
    @SuppressWarnings( "unchecked" )
    private static final java.util.function.BiFunction<Method, Object[], Object> NONE = ( m, a ) -> UNSET;

    private static <T> T noOp( final Class<T> iface, final java.util.function.BiFunction<Method, Object[], Object> override )
    {
        return iface.cast( Proxy.newProxyInstance( iface.getClassLoader(), new Class<?>[]{ iface }, new MockHandler( iface, override ) ) );
    }

    private static <T> T annotationDefaults( final Class<T> annotation, final java.util.function.BiFunction<Method, Object[], Object> override )
    {
        return annotation.cast( Proxy.newProxyInstance( annotation.getClassLoader(), new Class<?>[]{ annotation },
                                                        new MockHandler( annotation, override ) ) );
    }

    private static final class MockHandler
        implements InvocationHandler
    {
        private final Class<?> iface;

        private final java.util.function.BiFunction<Method, Object[], Object> override;

        MockHandler( final Class<?> iface, final java.util.function.BiFunction<Method, Object[], Object> override )
        {
            this.iface = iface;
            this.override = override;
        }

        @Override
        public Object invoke( final Object proxy, final Method method, final Object[] args )
        {
            if ( override != null )
            {
                final Object o = override.apply( method, args );
                if ( o != UNSET )
                {
                    return o;
                }
            }
            // Annotation methods - use the annotation default if declared.
            if ( method.getDeclaringClass().isAnnotation() )
            {
                final Object def = method.getDefaultValue();
                if ( def != null )
                {
                    return def;
                }
            }
            final String name = method.getName();
            if ( "toString".equals( name ) )
            {
                return iface.getSimpleName() + "Mock";
            }
            if ( "equals".equals( name ) )
            {
                return proxy == args[0];
            }
            if ( "hashCode".equals( name ) )
            {
                return System.identityHashCode( proxy );
            }
            return defaultValue( method.getReturnType() );
        }
    }

    private static Object defaultValue( final Class<?> type )
    {
        if ( !type.isPrimitive() || type == void.class )
        {
            return null;
        }
        if ( type == boolean.class )
        {
            return Boolean.FALSE;
        }
        if ( type == int.class )
        {
            return 0;
        }
        if ( type == long.class )
        {
            return 0L;
        }
        if ( type == short.class )
        {
            return (short) 0;
        }
        if ( type == byte.class )
        {
            return (byte) 0;
        }
        if ( type == char.class )
        {
            return (char) 0;
        }
        if ( type == float.class )
        {
            return 0f;
        }
        if ( type == double.class )
        {
            return 0d;
        }
        return null;
    }
}
