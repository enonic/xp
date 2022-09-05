package com.enonic.xp.core.dynamic;

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
import com.enonic.xp.core.impl.app.ApplicationRepoServiceImpl;
import com.enonic.xp.core.impl.app.ApplicationServiceImpl;
import com.enonic.xp.core.impl.app.CreateDynamicSiteParams;
import com.enonic.xp.core.impl.app.DynamicSchemaServiceImpl;
import com.enonic.xp.core.impl.app.VirtualAppContext;
import com.enonic.xp.core.impl.app.VirtualAppService;
import com.enonic.xp.core.impl.app.resource.ResourceServiceImpl;
import com.enonic.xp.core.impl.event.EventPublisherImpl;
import com.enonic.xp.core.impl.project.init.ContentInitializer;
import com.enonic.xp.internal.blobstore.MemoryBlobStore;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.region.ComponentDescriptor;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.repo.impl.binary.BinaryServiceImpl;
import com.enonic.xp.repo.impl.branch.storage.BranchServiceImpl;
import com.enonic.xp.repo.impl.commit.CommitServiceImpl;
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
import com.enonic.xp.resource.UpdateDynamicComponentParams;
import com.enonic.xp.resource.UpdateDynamicContentSchemaParams;
import com.enonic.xp.resource.UpdateDynamicSiteParams;
import com.enonic.xp.resource.UpdateDynamicStylesParams;
import com.enonic.xp.schema.BaseSchema;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.xdata.XData;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.style.StyleDescriptor;
import com.enonic.xp.xml.XmlException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class DynamicSchemaServiceImplTest
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

    @BeforeEach
    public void initService()
        throws Exception
    {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        deleteAllIndices();

        ContextAccessor.INSTANCE.set( ctxDefault() );

        final MemoryBlobStore blobStore = new MemoryBlobStore();

        BinaryServiceImpl binaryService = new BinaryServiceImpl();
        binaryService.setBlobStore( blobStore );

        final StorageDaoImpl storageDao = new StorageDaoImpl();
        storageDao.setClient( client );

        final EventPublisherImpl eventPublisher = new EventPublisherImpl( executorService );

        SearchDaoImpl searchDao = new SearchDaoImpl();
        searchDao.setClient( client );

        BranchServiceImpl branchService = new BranchServiceImpl();
        branchService.setStorageDao( storageDao );
        branchService.setSearchDao( searchDao );

        VersionServiceImpl versionService = new VersionServiceImpl();
        versionService.setStorageDao( storageDao );

        CommitServiceImpl commitService = new CommitServiceImpl();
        commitService.setStorageDao( storageDao );

        IndexServiceInternalImpl indexServiceInternal = new IndexServiceInternalImpl();
        indexServiceInternal.setClient( client );

        IndexServiceImpl indexService = new IndexServiceImpl();
        indexService.setIndexServiceInternal( indexServiceInternal );

        NodeVersionServiceImpl nodeDao = new NodeVersionServiceImpl();
        nodeDao.setBlobStore( blobStore );

        IndexDataServiceImpl indexedDataService = new IndexDataServiceImpl();
        indexedDataService.setStorageDao( storageDao );

        NodeStorageServiceImpl storageService = new NodeStorageServiceImpl();
        storageService.setBranchService( branchService );
        storageService.setVersionService( versionService );
        storageService.setCommitService( commitService );
        storageService.setNodeVersionService( nodeDao );
        storageService.setIndexDataService( indexedDataService );

        NodeSearchServiceImpl searchService = new NodeSearchServiceImpl();
        searchService.setSearchDao( searchDao );

        final NodeRepositoryServiceImpl nodeRepositoryService = new NodeRepositoryServiceImpl();
        nodeRepositoryService.setIndexServiceInternal( indexServiceInternal );

        final IndexServiceInternalImpl elasticsearchIndexService = new IndexServiceInternalImpl();
        elasticsearchIndexService.setClient( client );

        final RepositoryEntryServiceImpl repositoryEntryService = new RepositoryEntryServiceImpl();
        repositoryEntryService.setIndexServiceInternal( elasticsearchIndexService );
        repositoryEntryService.setNodeStorageService( storageService );
        repositoryEntryService.setNodeSearchService( searchService );
        repositoryEntryService.setEventPublisher( eventPublisher );
        repositoryEntryService.setBinaryService( binaryService );

        RepositoryServiceImpl repositoryService =
            new RepositoryServiceImpl( repositoryEntryService, elasticsearchIndexService, nodeRepositoryService, storageService,
                                       searchService );
        repositoryService.initialize();

        nodeService = new NodeServiceImpl();
        nodeService.setIndexServiceInternal( indexServiceInternal );
        nodeService.setNodeStorageService( storageService );
        nodeService.setNodeSearchService( searchService );
        nodeService.setEventPublisher( eventPublisher );
        nodeService.setBinaryService( binaryService );
        nodeService.setRepositoryService( repositoryService );
        nodeService.initialize();

        Path cacheDir = Files.createDirectory( this.felixTempFolder.resolve( "cache" ) ).toAbsolutePath();

        Felix felix = createFelixInstance( cacheDir );
        felix.start();

        ApplicationRepoServiceImpl repoService = new ApplicationRepoServiceImpl( nodeService, indexService );
        repoService.initialize();

        BundleContext bundleContext = felix.getBundleContext();

        AppConfig appConfig = mock( AppConfig.class, invocation -> invocation.getMethod().getDefaultValue() );

        ApplicationFactoryServiceImpl applicationFactoryService = new ApplicationFactoryServiceImpl( bundleContext, nodeService, appConfig );
        applicationFactoryService.activate();

        ResourceServiceImpl resourceService = new ResourceServiceImpl( applicationFactoryService );

        this.dynamicSchemaService = new DynamicSchemaServiceImpl( nodeService, resourceService );

        AppFilterService appFilterService = new AppFilterServiceImpl( appConfig );

        ApplicationRegistry applicationRegistry =
            new ApplicationRegistryImpl( bundleContext, new ApplicationListenerHub(), applicationFactoryService );

        VirtualAppService virtualAppService = new VirtualAppService( indexService, repositoryService, nodeService );
        virtualAppService.initialize();

        ApplicationService applicationService =
            new ApplicationServiceImpl( bundleContext, applicationRegistry, repoService, eventPublisher, appFilterService,
                                        virtualAppService, new ApplicationAuditLogSupportImpl( mock( AuditLogService.class ) ) );

        createAdminContext().runWith( () -> applicationService.createVirtualApplication(
            CreateVirtualApplicationParams.create().key( ApplicationKey.from( "myapp" ) ).build() ) );

        createAdminContext().runWith( () -> applicationService.createVirtualApplication(
            CreateVirtualApplicationParams.create().key( ApplicationKey.from( "my-other-app" ) ).build() ) );

        ContentInitializer.create()
            .setIndexService( indexService )
            .setNodeService( nodeService )
            .setRepositoryService( repositoryService )
            .build()
            .initialize();

        VirtualAppContext.createAdminContext().runWith( () -> nodeService.refresh( RefreshMode.ALL ) );
    }

    @Test
    public void createContentTypeSchema()
        throws Exception
    {
        final String resource = readResource( "_contentType.xml" );

        CreateDynamicContentSchemaParams params = CreateDynamicContentSchemaParams.create()
            .name( ContentTypeName.from( "myapp:mytype" ) )
            .resource( resource )
            .type( DynamicContentSchemaType.CONTENT_TYPE )
            .build();

        final DynamicSchemaResult<BaseSchema<?>> result =
            createAdminContext().callWith( () -> dynamicSchemaService.createContentSchema( params ) );

        VirtualAppContext.createAdminContext().runWith( () -> nodeService.refresh( RefreshMode.ALL ) );

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
        assertTrue( contentType.getXData().contains( XDataName.from( "myapplication:metadata" ) ) );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/site/content-types/mytype/mytype.xml", result.getResource().getKey().toString() );
        assertEquals( 737, result.getResource().getSize() );

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( NodePath.create( "/myapp/site/content-types/mytype/mytype.xml" ).build() ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    public void updateContentTypeSchema()
        throws Exception
    {
        final CreateDynamicContentSchemaParams createParams = CreateDynamicContentSchemaParams.create()
            .name( ContentTypeName.from( "myapp:mytype" ) )
            .resource( "<content-type><super-type>base:unstructured</super-type></content-type>" )
            .type( DynamicContentSchemaType.CONTENT_TYPE )
            .build();

        createAdminContext().runWith( () -> dynamicSchemaService.createContentSchema( createParams ) );

        VirtualAppContext.createAdminContext().runWith( () -> nodeService.refresh( RefreshMode.ALL ) );

        final String resource = readResource( "_contentType.xml" );

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
        assertTrue( contentType.getXData().contains( XDataName.from( "myapplication:metadata" ) ) );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/site/content-types/mytype/mytype.xml", result.getResource().getKey().toString() );
        assertEquals( 737, result.getResource().getSize() );

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( NodePath.create( "/myapp/site/content-types/mytype/mytype.xml" ).build() ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    public void createMixinSchema()
        throws Exception
    {
        final String resource = readResource( "_mixin.xml" );

        CreateDynamicContentSchemaParams params = CreateDynamicContentSchemaParams.create()
            .name( MixinName.from( "myapp:mymixin" ) )
            .resource( resource )
            .type( DynamicContentSchemaType.MIXIN )
            .build();

        final DynamicSchemaResult<BaseSchema<?>> result =
            createAdminContext().callWith( () -> dynamicSchemaService.createContentSchema( params ) );

        VirtualAppContext.createAdminContext().runWith( () -> nodeService.refresh( RefreshMode.ALL ) );

        final Mixin mixin = (Mixin) result.getSchema();

        createAdminContext().runWith( () -> assertThat( mixin ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getContentSchema( GetDynamicContentSchemaParams.create()
                                                                   .name( MixinName.from( "myapp:mymixin" ) )
                                                                   .type( DynamicContentSchemaType.MIXIN )
                                                                   .build() ).getSchema() ) );

        assertEquals( "myapp:mymixin", mixin.getName().toString() );
        assertEquals( "Virtual Mixin", mixin.getDisplayName() );
        assertEquals( "Mixin description", mixin.getDescription() );
        assertEquals( 2, mixin.getForm().size() );
        assertEquals( "myapplication:inline", mixin.getForm().getInlineMixin( "inline" ).getMixinName().toString() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/site/mixins/mymixin/mymixin.xml", result.getResource().getKey().toString() );
        assertEquals( 323, result.getResource().getSize() );

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( NodePath.create( "/myapp/site/mixins/mymixin/mymixin.xml" ).build() ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    public void updateMixinSchema()
        throws Exception
    {

        final CreateDynamicContentSchemaParams createParams = CreateDynamicContentSchemaParams.create()
            .name( MixinName.from( "myapp:mymixin" ) )
            .resource( "<mixin></mixin>" )
            .type( DynamicContentSchemaType.MIXIN )
            .build();

        createAdminContext().runWith( () -> dynamicSchemaService.createContentSchema( createParams ) );

        VirtualAppContext.createAdminContext().runWith( () -> nodeService.refresh( RefreshMode.ALL ) );

        final String resource = readResource( "_mixin.xml" );

        final UpdateDynamicContentSchemaParams updateParams = UpdateDynamicContentSchemaParams.create()
            .name( MixinName.from( "myapp:mymixin" ) )
            .resource( resource )
            .type( DynamicContentSchemaType.MIXIN )
            .build();

        final DynamicSchemaResult<BaseSchema<?>> result =
            createAdminContext().callWith( () -> dynamicSchemaService.updateContentSchema( updateParams ) );

        final Mixin mixin = (Mixin) result.getSchema();

        createAdminContext().runWith( () -> assertThat( mixin ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getContentSchema( GetDynamicContentSchemaParams.create()
                                                                   .name( MixinName.from( "myapp:mymixin" ) )
                                                                   .type( DynamicContentSchemaType.MIXIN )
                                                                   .build() ).getSchema() ) );

        assertEquals( "myapp:mymixin", mixin.getName().toString() );
        assertEquals( "Virtual Mixin", mixin.getDisplayName() );
        assertEquals( "Mixin description", mixin.getDescription() );
        assertEquals( 2, mixin.getForm().size() );
        assertEquals( "myapplication:inline", mixin.getForm().getInlineMixin( "inline" ).getMixinName().toString() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/site/mixins/mymixin/mymixin.xml", result.getResource().getKey().toString() );
        assertEquals( 323, result.getResource().getSize() );

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( NodePath.create( "/myapp/site/mixins/mymixin/mymixin.xml" ).build() ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    public void createXDataSchema()
        throws Exception
    {
        final String resource = readResource( "_xdata.xml" );

        CreateDynamicContentSchemaParams params = CreateDynamicContentSchemaParams.create()
            .name( XDataName.from( "myapp:myxdata" ) )
            .resource( resource )
            .type( DynamicContentSchemaType.XDATA )
            .build();

        final DynamicSchemaResult<BaseSchema<?>> result =
            createAdminContext().callWith( () -> dynamicSchemaService.createContentSchema( params ) );

        VirtualAppContext.createAdminContext().runWith( () -> nodeService.refresh( RefreshMode.ALL ) );

        final XData xdata = (XData) result.getSchema();

        createAdminContext().runWith( () -> assertThat( xdata ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getContentSchema( GetDynamicContentSchemaParams.create()
                                                                   .name( XDataName.from( "myapp:myxdata" ) )
                                                                   .type( DynamicContentSchemaType.XDATA )
                                                                   .build() ).getSchema() ) );

        assertEquals( "myapp:myxdata", xdata.getName().toString() );
        assertEquals( "Virtual X-data", xdata.getDisplayName() );
        assertEquals( "X-data description", xdata.getDescription() );
        assertEquals( 1, xdata.getForm().size() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/site/x-data/myxdata/myxdata.xml", result.getResource().getKey().toString() );
        assertEquals( 434, result.getResource().getSize() );

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( NodePath.create( "/myapp/site/x-data/myxdata/myxdata.xml" ).build() ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    public void updateXDataSchema()
        throws Exception
    {
        final CreateDynamicContentSchemaParams createParams = CreateDynamicContentSchemaParams.create()
            .name( XDataName.from( "myapp:myxdata" ) )
            .resource( "<x-data></x-data>" )
            .type( DynamicContentSchemaType.XDATA )
            .build();

        createAdminContext().runWith( () -> dynamicSchemaService.createContentSchema( createParams ) );

        VirtualAppContext.createAdminContext().runWith( () -> nodeService.refresh( RefreshMode.ALL ) );

        final String resource = readResource( "_xdata.xml" );

        final UpdateDynamicContentSchemaParams updateParams = UpdateDynamicContentSchemaParams.create()
            .name( XDataName.from( "myapp:myxdata" ) )
            .resource( resource )
            .type( DynamicContentSchemaType.XDATA )
            .build();

        final DynamicSchemaResult<BaseSchema<?>> result =
            createAdminContext().callWith( () -> dynamicSchemaService.updateContentSchema( updateParams ) );

        final XData xdata = (XData) result.getSchema();

        createAdminContext().runWith( () -> assertThat( xdata ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getContentSchema( GetDynamicContentSchemaParams.create()
                                                                   .name( XDataName.from( "myapp:myxdata" ) )
                                                                   .type( DynamicContentSchemaType.XDATA )
                                                                   .build() ).getSchema() ) );

        assertEquals( "myapp:myxdata", xdata.getName().toString() );
        assertEquals( "Virtual X-data", xdata.getDisplayName() );
        assertEquals( "X-data description", xdata.getDescription() );
        assertEquals( 1, xdata.getForm().size() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/site/x-data/myxdata/myxdata.xml", result.getResource().getKey().toString() );
        assertEquals( 434, result.getResource().getSize() );

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( NodePath.create( "/myapp/site/x-data/myxdata/myxdata.xml" ).build() ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    public void createPartComponent()
        throws Exception
    {
        final String resource = readResource( "_part.xml" );

        CreateDynamicComponentParams params = CreateDynamicComponentParams.create()
            .descriptorKey( DescriptorKey.from( "myapp:mypart" ) )
            .resource( resource )
            .type( DynamicComponentType.PART )
            .build();

        final DynamicSchemaResult<ComponentDescriptor> result =
            createAdminContext().callWith( () -> dynamicSchemaService.createComponent( params ) );

        VirtualAppContext.createAdminContext().runWith( () -> nodeService.refresh( RefreshMode.ALL ) );

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

        assertEquals( 1, partDescriptor.getSchemaConfig().getSize() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/site/parts/mypart/mypart.xml", result.getResource().getKey().toString() );
        assertEquals( 822, result.getResource().getSize() );

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( NodePath.create( "/myapp/site/parts/mypart/mypart.xml" ).build() ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    public void updatePartComponent()
        throws Exception
    {

        final CreateDynamicComponentParams createParams = CreateDynamicComponentParams.create()
            .descriptorKey( DescriptorKey.from( "myapp:mypart" ) )
            .resource( "<part></part>" )
            .type( DynamicComponentType.PART )
            .build();

        createAdminContext().runWith( () -> dynamicSchemaService.createComponent( createParams ) );

        VirtualAppContext.createAdminContext().runWith( () -> nodeService.refresh( RefreshMode.ALL ) );

        final String resource = readResource( "_part.xml" );

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

        assertEquals( 1, partDescriptor.getSchemaConfig().getSize() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/site/parts/mypart/mypart.xml", result.getResource().getKey().toString() );
        assertEquals( 822, result.getResource().getSize() );

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( NodePath.create( "/myapp/site/parts/mypart/mypart.xml" ).build() ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    public void createLayoutComponent()
        throws Exception
    {
        final String resource = readResource( "_layout.xml" );

        CreateDynamicComponentParams params = CreateDynamicComponentParams.create()
            .descriptorKey( DescriptorKey.from( "myapp:mylayout" ) )
            .resource( resource )
            .type( DynamicComponentType.LAYOUT )
            .build();

        final DynamicSchemaResult<ComponentDescriptor> result =
            createAdminContext().callWith( () -> dynamicSchemaService.createComponent( params ) );

        VirtualAppContext.createAdminContext().runWith( () -> nodeService.refresh( RefreshMode.ALL ) );

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
        assertEquals( "myapp:/site/layouts/mylayout/mylayout.xml", result.getResource().getKey().toString() );
        assertEquals( 1719, result.getResource().getSize() );

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( NodePath.create( "/myapp/site/layouts/mylayout/mylayout.xml" ).build() ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    public void updateLayoutComponent()
        throws Exception
    {
        final CreateDynamicComponentParams params = CreateDynamicComponentParams.create()
            .descriptorKey( DescriptorKey.from( "myapp:mylayout" ) )
            .resource( "<layout></layout>" )
            .type( DynamicComponentType.LAYOUT )
            .build();

        createAdminContext().runWith( () -> dynamicSchemaService.createComponent( params ) );

        VirtualAppContext.createAdminContext().runWith( () -> nodeService.refresh( RefreshMode.ALL ) );

        final String resource = readResource( "_layout.xml" );

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
        assertEquals( "myapp:/site/layouts/mylayout/mylayout.xml", result.getResource().getKey().toString() );
        assertEquals( 1719, result.getResource().getSize() );

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( NodePath.create( "/myapp/site/layouts/mylayout/mylayout.xml" ).build() ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    public void createPageComponent()
        throws Exception
    {
        final String resource = readResource( "_page.xml" );

        CreateDynamicComponentParams params = CreateDynamicComponentParams.create()
            .descriptorKey( DescriptorKey.from( "myapp:mypage" ) )
            .resource( resource )
            .type( DynamicComponentType.PAGE )
            .build();

        final DynamicSchemaResult<ComponentDescriptor> result =
            createAdminContext().callWith( () -> dynamicSchemaService.createComponent( params ) );

        VirtualAppContext.createAdminContext().runWith( () -> nodeService.refresh( RefreshMode.ALL ) );

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
        assertEquals( "myapp:/site/pages/mypage/mypage.xml", result.getResource().getKey().toString() );
        assertEquals( 611, result.getResource().getSize() );

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( NodePath.create( "/myapp/site/pages/mypage/mypage.xml" ).build() ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    public void updatePageComponent()
        throws Exception
    {
        final CreateDynamicComponentParams createParams = CreateDynamicComponentParams.create()
            .descriptorKey( DescriptorKey.from( "myapp:mypage" ) )
            .resource( "<page></page>" )
            .type( DynamicComponentType.PAGE )
            .build();

        createAdminContext().runWith( () -> dynamicSchemaService.createComponent( createParams ) );

        VirtualAppContext.createAdminContext().runWith( () -> nodeService.refresh( RefreshMode.ALL ) );

        final String resource = readResource( "_page.xml" );

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
        assertEquals( "myapp:/site/pages/mypage/mypage.xml", result.getResource().getKey().toString() );
        assertEquals( 611, result.getResource().getSize() );

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( NodePath.create( "/myapp/site/pages/mypage/mypage.xml" ).build() ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    public void createSite()
        throws Exception
    {
        final String resource = readResource( "_site.xml" );
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.getSite( applicationKey ) ) ).isNotNull();

        final DynamicSchemaResult<SiteDescriptor> result = createAdminContext().callWith(
            () -> dynamicSchemaService.createSite( CreateDynamicSiteParams.create().key( applicationKey ).resource( resource ).build() ) );

        VirtualAppContext.createAdminContext().runWith( () -> nodeService.refresh( RefreshMode.ALL ) );

        final SiteDescriptor siteDescriptor = result.getSchema();

        createAdminContext().runWith( () -> assertThat( siteDescriptor ).usingRecursiveComparison(
                RecursiveComparisonConfiguration.builder().withIgnoredFields( "mappingDescriptors" ).build() )
            .isEqualTo( dynamicSchemaService.getSite( applicationKey ).getSchema() ) );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/site/site.xml", result.getResource().getKey().toString() );
        assertEquals( 946, result.getResource().getSize() );

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( NodePath.create( "/myapp/site/site.xml" ).build() ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    public void getSiteNonExistedApp()
        throws Exception
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "non-app" );

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.getSite( applicationKey ) ) ).isNull();
    }

    @Test
    public void updateSite()
        throws Exception
    {
        final String resource = readResource( "_site.xml" );
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        createAdminContext().runWith( () -> dynamicSchemaService.createSite(
            CreateDynamicSiteParams.create().key( applicationKey ).resource( "<site></site>" ).build() ) );

        VirtualAppContext.createAdminContext().runWith( () -> nodeService.refresh( RefreshMode.ALL ) );

        final DynamicSchemaResult<SiteDescriptor> result = createAdminContext().callWith(
            () -> dynamicSchemaService.updateSite( UpdateDynamicSiteParams.create().key( applicationKey ).resource( resource ).build() ) );

        final SiteDescriptor siteDescriptor = result.getSchema();

        createAdminContext().runWith( () -> assertThat( siteDescriptor ).usingRecursiveComparison(
                RecursiveComparisonConfiguration.builder().withIgnoredFields( "mappingDescriptors" ).build() )
            .isEqualTo( dynamicSchemaService.getSite( applicationKey ).getSchema() ) );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/site/site.xml", result.getResource().getKey().toString() );
        assertEquals( 946, result.getResource().getSize() );

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( NodePath.create( "/myapp/site/site.xml" ).build() ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    public void updateNotCreatedSite()
        throws Exception
    {
        final String resource = readResource( "_site.xml" );
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        VirtualAppContext.createAdminContext().runWith( () -> nodeService.refresh( RefreshMode.ALL ) );

        final DynamicSchemaResult<SiteDescriptor> result = createAdminContext().callWith(
            () -> dynamicSchemaService.updateSite( UpdateDynamicSiteParams.create().key( applicationKey ).resource( resource ).build() ) );

        final SiteDescriptor siteDescriptor = result.getSchema();

        createAdminContext().runWith( () -> assertThat( siteDescriptor ).usingRecursiveComparison(
                RecursiveComparisonConfiguration.builder().withIgnoredFields( "mappingDescriptors" ).build() )
            .isEqualTo( dynamicSchemaService.getSite( applicationKey ).getSchema() ) );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/site/site.xml", result.getResource().getKey().toString() );
        assertEquals( 946, result.getResource().getSize() );

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( NodePath.create( "/myapp/site/site.xml" ).build() ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    public void deleteSite()
        throws Exception
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        createAdminContext().callWith( () -> dynamicSchemaService.getSite( applicationKey ) );

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.getSite( applicationKey ) ) ).isNotNull();

        createAdminContext().callWith( () -> dynamicSchemaService.createSite(
            CreateDynamicSiteParams.create().key( applicationKey ).resource( readResource( "_site.xml" ) ).build() ) );

        VirtualAppContext.createAdminContext().runWith( () -> nodeService.refresh( RefreshMode.ALL ) );

        DynamicSchemaResult<SiteDescriptor> site = createAdminContext().callWith( () -> dynamicSchemaService.getSite( applicationKey ) );

        assertThat( site.getSchema().getForm() ).isNotEmpty();
        assertThat( site.getSchema().getXDataMappings() ).isNotEmpty();
        assertThat( site.getSchema().getMappingDescriptors() ).isNotEmpty();
        assertThat( site.getSchema().getResponseProcessors() ).isNotEmpty();

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.deleteSite( applicationKey ) ) ).isTrue();

        VirtualAppContext.createAdminContext().runWith( () -> nodeService.refresh( RefreshMode.ALL ) );

        site = createAdminContext().callWith( () -> dynamicSchemaService.getSite( applicationKey ) );

        assertThat( site.getSchema().getForm() ).isEmpty();
        assertThat( site.getSchema().getXDataMappings() ).isEmpty();
        assertThat( site.getSchema().getMappingDescriptors() ).isEmpty();
        assertThat( site.getSchema().getResponseProcessors() ).isEmpty();
    }

    @Test
    public void createStyles()
        throws Exception
    {
        final String resource = readResource( "_styles.xml" );
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.getStyles( applicationKey ) ) ).isNull();

        final DynamicSchemaResult<StyleDescriptor> result = createAdminContext().callWith( () -> dynamicSchemaService.createStyles(
            CreateDynamicStylesParams.create().key( applicationKey ).resource( resource ).build() ) );

        VirtualAppContext.createAdminContext().runWith( () -> nodeService.refresh( RefreshMode.ALL ) );

        final StyleDescriptor styleDescriptor = result.getSchema();

        createAdminContext().runWith( () -> assertThat( styleDescriptor ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getStyles( applicationKey ).getSchema() ) );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/site/styles.xml", result.getResource().getKey().toString() );
        assertEquals( 537, result.getResource().getSize() );

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( NodePath.create( "/myapp/site/styles.xml" ).build() ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    public void updateStyles()
        throws Exception
    {
        final String resource = readResource( "_styles.xml" );
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        createAdminContext().callWith( () -> dynamicSchemaService.createStyles(
            CreateDynamicStylesParams.create().key( applicationKey ).resource( "<styles></styles>" ).build() ) );

        VirtualAppContext.createAdminContext().runWith( () -> nodeService.refresh( RefreshMode.ALL ) );

        final DynamicSchemaResult<StyleDescriptor> result = createAdminContext().callWith( () -> dynamicSchemaService.updateStyles(
            UpdateDynamicStylesParams.create().key( applicationKey ).resource( resource ).build() ) );

        final StyleDescriptor styleDescriptor = result.getSchema();

        createAdminContext().runWith( () -> assertThat( styleDescriptor ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getStyles( applicationKey ).getSchema() ) );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/site/styles.xml", result.getResource().getKey().toString() );
        assertEquals( 537, result.getResource().getSize() );

        final Node resourceNode = VirtualAppContext.createAdminContext()
            .callWith( () -> nodeService.getByPath( NodePath.create( "/myapp/site/styles.xml" ).build() ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    public void deleteStyles()
        throws Exception
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.getStyles( applicationKey ) ) ).isNull();

        createAdminContext().callWith( () -> dynamicSchemaService.createStyles(
            CreateDynamicStylesParams.create().key( applicationKey ).resource( readResource( "_styles.xml" ) ).build() ) );

        VirtualAppContext.createAdminContext().runWith( () -> nodeService.refresh( RefreshMode.ALL ) );

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.getStyles( applicationKey ) ) ).isNotNull();

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.deleteStyles( applicationKey ) ) ).isTrue();

        VirtualAppContext.createAdminContext().runWith( () -> nodeService.refresh( RefreshMode.ALL ) );

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.getStyles( applicationKey ) ) ).isNull();
    }


    @Test
    public void listPartComponents()
        throws Exception
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        List<DynamicSchemaResult<ComponentDescriptor>> results = createAdminContext().callWith( () -> dynamicSchemaService.listComponents(
            ListDynamicComponentsParams.create().applicationKey( applicationKey ).type( DynamicComponentType.PART ).build() ) );

        assertTrue( results.isEmpty() );

        DynamicSchemaResult<PartDescriptor> part1 = createAdminContext().callWith( () -> dynamicSchemaService.createComponent(
            CreateDynamicComponentParams.create()
                .descriptorKey( DescriptorKey.from( "myapp:mypart1" ) )
                .resource( readResource( "_part.xml" ) )
                .type( DynamicComponentType.PART )
                .build() ) );
        DynamicSchemaResult<PartDescriptor> part2 = createAdminContext().callWith( () -> dynamicSchemaService.createComponent(
            CreateDynamicComponentParams.create()
                .descriptorKey( DescriptorKey.from( "myapp:mypart2" ) )
                .resource( readResource( "_part.xml" ) )
                .type( DynamicComponentType.PART )
                .build() ) );
        DynamicSchemaResult<PartDescriptor> part3 = createAdminContext().callWith( () -> dynamicSchemaService.createComponent(
            CreateDynamicComponentParams.create()
                .descriptorKey( DescriptorKey.from( "my-other-app:mypart" ) )
                .resource( readResource( "_part.xml" ) )
                .type( DynamicComponentType.PART )
                .build() ) );

        VirtualAppContext.createAdminContext().runWith( () -> nodeService.refresh( RefreshMode.ALL ) );

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
    public void listContentTypes()
        throws Exception
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
                .resource( readResource( "_contentType.xml" ) )
                .type( DynamicContentSchemaType.CONTENT_TYPE )
                .build() ) );
        DynamicSchemaResult<ContentType> contentType2 = createAdminContext().callWith( () -> dynamicSchemaService.createContentSchema(
            CreateDynamicContentSchemaParams.create()
                .name( ContentTypeName.from( "myapp:mytype2" ) )
                .resource( readResource( "_contentType.xml" ) )
                .type( DynamicContentSchemaType.CONTENT_TYPE )
                .build() ) );
        DynamicSchemaResult<ContentType> contentType3 = createAdminContext().callWith( () -> dynamicSchemaService.createContentSchema(
            CreateDynamicContentSchemaParams.create()
                .name( ContentTypeName.from( "my-other-app:mytype" ) )
                .resource( readResource( "_contentType.xml" ) )
                .type( DynamicContentSchemaType.CONTENT_TYPE )
                .build() ) );

        VirtualAppContext.createAdminContext().runWith( () -> nodeService.refresh( RefreshMode.ALL ) );

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
    public void listMixinTypes()
        throws Exception
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        List<DynamicSchemaResult<BaseSchema<?>>> results = createAdminContext().callWith( () -> dynamicSchemaService.listContentSchemas(
            ListDynamicContentSchemasParams.create().applicationKey( applicationKey ).type( DynamicContentSchemaType.MIXIN ).build() ) );

        assertTrue( results.isEmpty() );

        DynamicSchemaResult<Mixin> mixin1 = createAdminContext().callWith( () -> dynamicSchemaService.createContentSchema(
            CreateDynamicContentSchemaParams.create()
                .name( MixinName.from( "myapp:mytype1" ) )
                .resource( readResource( "_mixin.xml" ) )
                .type( DynamicContentSchemaType.MIXIN )
                .build() ) );
        DynamicSchemaResult<Mixin> mixin2 = createAdminContext().callWith( () -> dynamicSchemaService.createContentSchema(
            CreateDynamicContentSchemaParams.create()
                .name( MixinName.from( "myapp:mytype2" ) )
                .resource( readResource( "_mixin.xml" ) )
                .type( DynamicContentSchemaType.MIXIN )
                .build() ) );
        DynamicSchemaResult<Mixin> mixin3 = createAdminContext().callWith( () -> dynamicSchemaService.createContentSchema(
            CreateDynamicContentSchemaParams.create()
                .name( MixinName.from( "my-other-app:mytype" ) )
                .resource( readResource( "_mixin.xml" ) )
                .type( DynamicContentSchemaType.MIXIN )
                .build() ) );

        VirtualAppContext.createAdminContext().runWith( () -> nodeService.refresh( RefreshMode.ALL ) );

        results = createAdminContext().callWith( () -> dynamicSchemaService.listContentSchemas(
            ListDynamicContentSchemasParams.create().applicationKey( applicationKey ).type( DynamicContentSchemaType.MIXIN ).build() ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( mixin2, mixin1 ) );

        results = createAdminContext().callWith( () -> dynamicSchemaService.listContentSchemas( ListDynamicContentSchemasParams.create()
                                                                                                    .applicationKey( ApplicationKey.from(
                                                                                                        "my-other-app" ) )
                                                                                                    .type( DynamicContentSchemaType.MIXIN )
                                                                                                    .build() ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( mixin3 ) );

    }

    @Test
    public void listXDataTypes()
        throws Exception
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        List<DynamicSchemaResult<BaseSchema<?>>> results = createAdminContext().callWith( () -> dynamicSchemaService.listContentSchemas(
            ListDynamicContentSchemasParams.create().applicationKey( applicationKey ).type( DynamicContentSchemaType.XDATA ).build() ) );

        assertTrue( results.isEmpty() );

        DynamicSchemaResult<XData> xdata1 = createAdminContext().callWith( () -> dynamicSchemaService.createContentSchema(
            CreateDynamicContentSchemaParams.create()
                .name( XDataName.from( "myapp:mytype1" ) )
                .resource( readResource( "_xdata.xml" ) )
                .type( DynamicContentSchemaType.XDATA )
                .build() ) );
        DynamicSchemaResult<XData> xdata2 = createAdminContext().callWith( () -> dynamicSchemaService.createContentSchema(
            CreateDynamicContentSchemaParams.create()
                .name( XDataName.from( "myapp:mytype2" ) )
                .resource( readResource( "_xdata.xml" ) )
                .type( DynamicContentSchemaType.XDATA )
                .build() ) );
        DynamicSchemaResult<XData> xdata3 = createAdminContext().callWith( () -> dynamicSchemaService.createContentSchema(
            CreateDynamicContentSchemaParams.create()
                .name( XDataName.from( "my-other-app:mytype" ) )
                .resource( readResource( "_xdata.xml" ) )
                .type( DynamicContentSchemaType.XDATA )
                .build() ) );

        VirtualAppContext.createAdminContext().runWith( () -> nodeService.refresh( RefreshMode.ALL ) );

        results = createAdminContext().callWith( () -> dynamicSchemaService.listContentSchemas(
            ListDynamicContentSchemasParams.create().applicationKey( applicationKey ).type( DynamicContentSchemaType.XDATA ).build() ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( xdata1, xdata2 ) );

        results = createAdminContext().callWith( () -> dynamicSchemaService.listContentSchemas( ListDynamicContentSchemasParams.create()
                                                                                                    .applicationKey( ApplicationKey.from(
                                                                                                        "my-other-app" ) )
                                                                                                    .type( DynamicContentSchemaType.XDATA )
                                                                                                    .build() ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( xdata3 ) );

    }

    @Test
    public void deleteContentTypeComponent()
        throws Exception
    {
        DynamicSchemaResult<ContentType> contentType = createAdminContext().callWith( () -> dynamicSchemaService.createContentSchema(
            CreateDynamicContentSchemaParams.create()
                .name( ContentTypeName.from( "myapp:mytype" ) )
                .resource( readResource( "_contentType.xml" ) )
                .type( DynamicContentSchemaType.CONTENT_TYPE )
                .build() ) );

        VirtualAppContext.createAdminContext().runWith( () -> nodeService.refresh( RefreshMode.ALL ) );

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
    public void deletePartComponent()
        throws Exception
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        DynamicSchemaResult<PartDescriptor> part = createAdminContext().callWith( () -> dynamicSchemaService.createComponent(
            CreateDynamicComponentParams.create()
                .descriptorKey( DescriptorKey.from( "myapp:mypart" ) )
                .resource( readResource( "_part.xml" ) )
                .type( DynamicComponentType.PART )
                .build() ) );

        VirtualAppContext.createAdminContext().runWith( () -> nodeService.refresh( RefreshMode.ALL ) );

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
    public void createContentTypeSchemaInvalid()
        throws Exception
    {
        final String resource = "<content-type xmlns=\"urn:enonic:xp:model:1.0\"><invalid-tag/></content-type>";

        CreateDynamicContentSchemaParams params = CreateDynamicContentSchemaParams.create()
            .name( ContentTypeName.from( "myapp:mytype" ) )
            .resource( resource )
            .type( DynamicContentSchemaType.CONTENT_TYPE )
            .build();

        final XmlException exception = assertThrows( XmlException.class, () -> createAdminContext().callWith(
            () -> dynamicSchemaService.createContentSchema( params ) ) );

        assertEquals( "Could not parse dynamic content type [myapp:mytype]", exception.getMessage() );
    }

    @Test
    public void createMixinSchemaInvalid()
        throws Exception
    {
        final String resource = "<mixin xmlns=\"urn:enonic:xp:model:1.0\"><invalid-tag/></mixin>";

        CreateDynamicContentSchemaParams params = CreateDynamicContentSchemaParams.create()
            .name( MixinName.from( "myapp:mytype" ) )
            .resource( resource )
            .type( DynamicContentSchemaType.MIXIN )
            .build();

        final XmlException exception = assertThrows( XmlException.class, () -> createAdminContext().callWith(
            () -> dynamicSchemaService.createContentSchema( params ) ) );

        assertEquals( "Could not parse dynamic mixin [myapp:mytype]", exception.getMessage() );
    }

    @Test
    public void createXDataSchemaInvalid()
        throws Exception
    {
        final String resource = "<x-data xmlns=\"urn:enonic:xp:model:1.0\"><invalid-tag/></x-data>";

        CreateDynamicContentSchemaParams params = CreateDynamicContentSchemaParams.create()
            .name( XDataName.from( "myapp:mytype" ) )
            .resource( resource )
            .type( DynamicContentSchemaType.XDATA )
            .build();

        final XmlException exception = assertThrows( XmlException.class, () -> createAdminContext().callWith(
            () -> dynamicSchemaService.createContentSchema( params ) ) );

        assertEquals( "Could not parse dynamic xdata [myapp:mytype]", exception.getMessage() );
    }

    @Test
    public void createPartInvalid()
        throws Exception
    {
        final String resource = "<part xmlns=\"urn:enonic:xp:model:1.0\"><invalid-tag/></part>";

        final CreateDynamicComponentParams params = CreateDynamicComponentParams.create()
            .descriptorKey( DescriptorKey.from( "myapp:mytype" ) )
            .resource( resource )
            .type( DynamicComponentType.PART )
            .build();

        final XmlException exception =
            assertThrows( XmlException.class, () -> createAdminContext().callWith( () -> dynamicSchemaService.createComponent( params ) ) );

        assertEquals( "Could not parse dynamic part descriptor [myapp:mytype]", exception.getMessage() );
    }

    @Test
    public void createLayoutInvalid()
        throws Exception
    {
        final String resource = "<layout xmlns=\"urn:enonic:xp:model:1.0\"><invalid-tag/></layout>";

        final CreateDynamicComponentParams params = CreateDynamicComponentParams.create()
            .descriptorKey( DescriptorKey.from( "myapp:mytype" ) )
            .resource( resource )
            .type( DynamicComponentType.LAYOUT )
            .build();

        final XmlException exception =
            assertThrows( XmlException.class, () -> createAdminContext().callWith( () -> dynamicSchemaService.createComponent( params ) ) );

        assertEquals( "Could not parse dynamic layout descriptor [myapp:mytype]", exception.getMessage() );
    }

    @Test
    public void createPageInvalid()
        throws Exception
    {
        final String resource = "<page xmlns=\"urn:enonic:xp:model:1.0\"><invalid-tag/></page>";

        final CreateDynamicComponentParams params = CreateDynamicComponentParams.create()
            .descriptorKey( DescriptorKey.from( "myapp:mytype" ) )
            .resource( resource )
            .type( DynamicComponentType.PAGE )
            .build();

        final XmlException exception =
            assertThrows( XmlException.class, () -> createAdminContext().callWith( () -> dynamicSchemaService.createComponent( params ) ) );

        assertEquals( "Could not parse dynamic page descriptor [myapp:mytype]", exception.getMessage() );
    }

    @Test
    public void createStylesInvalid()
        throws Exception
    {
        final String resource = "<styles xmlns=\"urn:enonic:xp:model:1.0\"><invalid-tag/></styles>";

        final CreateDynamicStylesParams params =
            CreateDynamicStylesParams.create().key( ApplicationKey.from( "myapp" ) ).resource( resource ).build();

        final XmlException exception =
            assertThrows( XmlException.class, () -> createAdminContext().callWith( () -> dynamicSchemaService.createStyles( params ) ) );

        assertEquals( "Could not parse dynamic style descriptor, application key: [myapp]", exception.getMessage() );
    }

    @Test
    public void createSiteInvalid()
        throws Exception
    {
        final String resource = "<site xmlns=\"urn:enonic:xp:model:1.0\"><invalid-tag/></site>";

        final CreateDynamicSiteParams params =
            CreateDynamicSiteParams.create().key( ApplicationKey.from( "myapp" ) ).resource( resource ).build();

        final XmlException exception =
            assertThrows( XmlException.class, () -> createAdminContext().callWith( () -> dynamicSchemaService.createSite( params ) ) );

        assertEquals( "Could not parse dynamic site descriptor, application key: [myapp]", exception.getMessage() );
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
