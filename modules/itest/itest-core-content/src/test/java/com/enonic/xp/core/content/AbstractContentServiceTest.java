package com.enonic.xp.core.content;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;
import com.google.common.net.HttpHeaders;

import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.content.FindContentByParentResult;
import com.enonic.xp.content.GetContentVersionsParams;
import com.enonic.xp.content.GetContentVersionsResult;
import com.enonic.xp.content.Mixins;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextAccessorSupport;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.impl.content.ContentAuditLogFilterService;
import com.enonic.xp.core.impl.content.ContentAuditLogSupportImpl;
import com.enonic.xp.core.impl.content.ContentConfig;
import com.enonic.xp.core.impl.content.ContentServiceImpl;
import com.enonic.xp.core.impl.content.LayersContentService;
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
import com.enonic.xp.core.impl.project.init.ContentInitializer;
import com.enonic.xp.core.impl.security.PasswordSecurityService;
import com.enonic.xp.core.impl.security.SecurityAuditLogSupportImpl;
import com.enonic.xp.core.impl.security.SecurityConfig;
import com.enonic.xp.core.impl.security.SecurityInitializer;
import com.enonic.xp.core.impl.security.SecurityServiceImpl;
import com.enonic.xp.core.impl.site.CmsServiceImpl;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.extractor.BinaryExtractor;
import com.enonic.xp.extractor.ExtractedData;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.internal.blobstore.MemoryBlobStore;
import com.enonic.xp.itest.AbstractElasticsearchIntegrationTest;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.page.PageTemplateService;
import com.enonic.xp.project.CreateProjectParams;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.repo.impl.binary.BinaryServiceImpl;
import com.enonic.xp.repo.impl.branch.storage.BranchServiceImpl;
import com.enonic.xp.repo.impl.commit.CommitServiceImpl;
import com.enonic.xp.repo.impl.config.RepoConfiguration;
import com.enonic.xp.repo.impl.index.IndexServiceImpl;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
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
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.site.CmsService;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.core.nodb.NodbItestWiring;
import com.enonic.xp.core.nodb.NodbTenant;
import com.enonic.xp.core.nodb.NodbTestCluster;
import com.enonic.xp.storage.spi.NodeSearchIndex;
import com.enonic.xp.storage.spi.NodeStore;
import com.enonic.xp.storage.spi.RepositoryStorageAdmin;
import com.enonic.xp.util.GenericValue;
import com.enonic.xp.util.GeoPoint;
import com.enonic.xp.util.Reference;

import static java.util.Objects.requireNonNullElse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

public abstract class AbstractContentServiceTest
    extends AbstractElasticsearchIntegrationTest
{
    private static final AtomicInteger PROJECT_COUNTER = new AtomicInteger();

    protected static final MemoryBlobStore BLOB_STORE = new MemoryBlobStore();

    /**
     * Phase 4 Gate B: the nodb harness, replicating {@code AbstractNodeTest}'s single mode
     * branch for the content fixture. Non-null only in nodb mode; memoized per concrete test
     * CLASS by {@link NodbTestCluster#tenantForClass}, deliberately NOT closed in teardown
     * (closing it after the first method would break every later method in the same class).
     * <p>
     * Class-scoped is the granularity this fixture needs, and it is not arbitrary: the ES side
     * is wiped in {@code @BeforeAll} while a fresh project (repository) is created per method,
     * so storage and search agree about the SYSTEM repository for the whole class. Making the
     * tenant per-method would make {@code SystemRepoInitializer}'s
     * {@code isInitialized} check see "search says it exists, storage says it does not" on the
     * second method and fail with {@code RepositoryAlreadyExistsException}.
     */
    private NodbTenant nodbTenant;

    /**
     * Phase 4 Gate F: the mode-correct storage AND search wiring (see {@link NodbItestWiring}).
     * Class-scoped, like {@link #nodbTenant}, so it is deliberately not closed in teardown.
     */
    private NodbItestWiring wiring;

    /** {@code ElasticsearchNodeStore} in default mode, the nodb gRPC client in nodb mode. */
    protected NodeStore nodeStore;

    /** The swappable {@code RepositoryStorageAdmin} slot. */
    protected RepositoryStorageAdmin repositoryStorageAdmin;

    /** {@link #BLOB_STORE}, wrapped by a {@code NodbBinaryBlobStore} in nodb mode. */
    protected BlobStore blobStore;

    final ProjectName testprojectName = ProjectName.from( "test" + PROJECT_COUNTER.incrementAndGet() );

    public static final User TEST_DEFAULT_USER =
        User.create().key( PrincipalKey.ofUser( IdProviderKey.system(), "test-user" ) ).login( "test-user" ).build();

    public static final AuthenticationInfo TEST_DEFAULT_USER_AUTHINFO = AuthenticationInfo.create()
        .principals( RoleKeys.AUTHENTICATED )
        .principals( RoleKeys.CONTENT_MANAGER_ADMIN )
        .user( TEST_DEFAULT_USER )
        .build();

    protected ProjectServiceImpl projectService;

    protected ContentServiceImpl contentService;

    protected LayersContentService layersContentService;

    protected ContentConfig config;

    protected NodeServiceImpl nodeService;

    protected ContentTypeServiceImpl contentTypeService;

    protected CmsFormFragmentService formFragmentService;

    protected MixinService mixinService;

    protected MixinMappingServiceImpl mixinMappingService;

    protected SiteConfigServiceImpl siteConfigService;

    protected AuditLogService auditLogService;

    protected IndexServiceImpl indexService;

    protected ResourceService resourceService;

    protected ContentAuditLogFilterService contentAuditLogFilterService;

    protected PageDescriptorService pageDescriptorService;

    protected PageTemplateService pageTemplateService;

    protected EventPublisher eventPublisher;

    private ExecutorService executorService;

    private Context initialContext;

    protected CmsService cmsService;

    protected Context ctxDraft()
    {
        return ContextBuilder.create()
            .branch( ContentConstants.BRANCH_DRAFT )
            .repositoryId( testprojectName.getRepoId() )
            .authInfo( TEST_DEFAULT_USER_AUTHINFO )
            .build();
    }

    protected Context ctxMaster()
    {
        return ContextBuilder.create()
            .branch( ContentConstants.BRANCH_MASTER )
            .repositoryId( testprojectName.getRepoId() )
            .authInfo( TEST_DEFAULT_USER_AUTHINFO )
            .build();
    }

    public Context ctxMasterAnonymous()
    {
        return ContextBuilder.create().branch( ContentConstants.BRANCH_MASTER ).repositoryId( testprojectName.getRepoId() ).build();
    }

    public Context ctxMasterSu()
    {
        return ContextBuilder.create()
            .branch( ContentConstants.BRANCH_MASTER )
            .repositoryId( testprojectName.getRepoId() )
            .authInfo( ContentInitializer.SUPER_USER_AUTH )
            .build();
    }

    @BeforeAll
    static void cleanupAbstractContentServiceTest()
    {
        BLOB_STORE.clear();
        deleteAllIndices();
    }

    @BeforeEach
    void setUpAbstractContentServiceTest()
    {
        executorService = Executors.newSingleThreadExecutor();

        initialContext = ContextAccessor.current();
        ContextAccessorSupport.getInstance().set( ctxDraft() );

        this.eventPublisher = new EventPublisherImpl( executorService );

        // Phase 4 Gate F (nodb/BUILD-PHASE-4.md): the one mode branch, now including the SEARCH
        // half. Gate B branched storage here but built `new NodeSearchIndexImpl( client, ... )`
        // unconditionally, so every content-level query, sort and aggregation this suite called
        // "green in nodb mode" through Gates B-E was in fact answered by embedded Elasticsearch --
        // the same wrong-engine harness error as Gate C's corpus and Gate E's aggregation classes
        // (nodb/FINDINGS.md #6). NodbItestWiring owns the decision for all four hand-rolled
        // graphs so it cannot be made differently in four places again.
        this.wiring = NodbItestWiring.perClass( this.getClass(), client, BLOB_STORE );

        final NodeSearchIndex nodeSearchIndex = wiring.nodeSearchIndex();

        final IndexServiceInternal indexServiceInternal = wiring.indexServiceInternal();

        this.nodbTenant = wiring.tenant();
        this.nodeStore = wiring.nodeStore();
        this.repositoryStorageAdmin = wiring.repositoryStorageAdmin();
        this.blobStore = wiring.blobStore();

        final BinaryServiceImpl binaryService = new BinaryServiceImpl( blobStore );

        BranchServiceImpl branchService = new BranchServiceImpl( nodeStore );

        VersionServiceImpl versionService = new VersionServiceImpl( nodeStore );

        CommitServiceImpl commitService = new CommitServiceImpl( nodeStore );

        NodeVersionServiceImpl nodeDao = new NodeVersionServiceImpl( blobStore, new RepoConfiguration( Map.of() ) );

        IndexDataServiceImpl indexedDataService = new IndexDataServiceImpl( nodeSearchIndex );

        final NodeStorageServiceImpl storageService =
            new NodeStorageServiceImpl( versionService, branchService, commitService, nodeDao, indexedDataService );

        NodeSearchServiceImpl searchService = new NodeSearchServiceImpl( nodeSearchIndex );
        final RepositoryEntryServiceImpl repositoryEntryService =
            new RepositoryEntryServiceImpl( this.repositoryStorageAdmin, nodeSearchIndex, storageService, searchService, nodeStore, eventPublisher, binaryService );

        indexService = new IndexServiceImpl( indexServiceInternal, this.repositoryStorageAdmin, nodeSearchIndex, indexedDataService, searchService, nodeStore, nodeDao, repositoryEntryService );

        final NodeRepositoryServiceImpl nodeRepositoryService = new NodeRepositoryServiceImpl( indexServiceInternal, this.repositoryStorageAdmin, nodeSearchIndex );

        RepositoryServiceImpl repositoryService =
            new RepositoryServiceImpl( repositoryEntryService, nodeRepositoryService, storageService, searchService, nodeStore, branchService,
                                       () -> null );
        SystemRepoInitializer.create()
            .setIndexServiceInternal( indexServiceInternal )
            .setNodeStorageService( storageService )
            .setNodeRepositoryService( nodeRepositoryService )
            .setRepositoryEntryService( repositoryEntryService )
            .build()
            .initialize();

        nodeService = new NodeServiceImpl( this.repositoryStorageAdmin, nodeSearchIndex, storageService, searchService, nodeStore, eventPublisher, binaryService );

        formFragmentService = mock( CmsFormFragmentService.class );
        when( formFragmentService.inlineFormItems( Mockito.isA( Form.class ) ) ).then( AdditionalAnswers.returnsFirstArg() );

        mixinService = mock( MixinService.class );

        Map<String, List<String>> metadata = new HashMap<>();
        metadata.put( HttpHeaders.CONTENT_TYPE, List.of( "image/jpeg" ) );

        final ExtractedData extractedData = ExtractedData.create().metadata( metadata ).build();

        final BinaryExtractor extractor = mock( BinaryExtractor.class );
        when( extractor.extract( Mockito.isA( ByteSource.class ) ) ).thenReturn( extractedData );

        MediaInfoServiceImpl mediaInfoService = new MediaInfoServiceImpl( extractor );

        resourceService = mock( ResourceService.class );

        cmsService = new CmsServiceImpl( resourceService, formFragmentService );

        contentTypeService = new ContentTypeServiceImpl( resourceService, null, formFragmentService );

        this.pageDescriptorService = mock( PageDescriptorService.class );
        this.pageTemplateService = mock( PageTemplateService.class );
        PartDescriptorService partDescriptorService = mock( PartDescriptorService.class );
        LayoutDescriptorService layoutDescriptorService = mock( LayoutDescriptorService.class );
        auditLogService = mock( AuditLogService.class );

        contentAuditLogFilterService = mock( ContentAuditLogFilterService.class, _ -> true );

        final ContentConfig contentConfig = mock( ContentConfig.class );
        when( contentConfig.auditlog_enabled() ).thenReturn( Boolean.TRUE );

        final ContentAuditLogSupportImpl contentAuditLogSupport =
            new ContentAuditLogSupportImpl( contentConfig, Runnable::run, auditLogService, contentAuditLogFilterService );

        final SecurityConfig securityConfig = mock( SecurityConfig.class, withSettings().stubOnly()
            .defaultAnswer( invocationOnMock -> invocationOnMock.getMethod().getDefaultValue() ) );

        final ProjectConfig projectConfig = mock( ProjectConfig.class );

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

        projectService =
            new ProjectServiceImpl( repositoryService, repositoryService, indexService, nodeService, securityService, eventPublisher,
                                    projectConfig );
        projectService.initialize();

        projectService.create( CreateProjectParams.create().name( testprojectName ).displayName( "test" ).build() );

        mixinMappingService = new MixinMappingServiceImpl( cmsService, mixinService );
        siteConfigService = new SiteConfigServiceImpl( nodeService, projectService, contentTypeService, eventPublisher );

        this.config = mock( ContentConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        contentService =
            new ContentServiceImpl( nodeService, pageDescriptorService, partDescriptorService, layoutDescriptorService, siteConfigService,
                                    config );
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

        layersContentService =
            new LayersContentService( nodeService, contentTypeService, eventPublisher, mixinService, cmsService, pageDescriptorService,
                                      partDescriptorService, layoutDescriptorService, config );
    }

    @AfterEach
    void tearDownAbstractContentServiceTest()
    {
        projectService.delete( testprojectName );

        ContextAccessorSupport.getInstance().set( initialContext );

        executorService.shutdownNow();
    }

    protected ByteSource loadImage( final String name )
    {
        try (InputStream stream = this.getClass().getResourceAsStream( name ))
        {
            return ByteSource.wrap( stream.readAllBytes() );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    protected CreateAttachments createAttachment( final String name, final String mimeType, final ByteSource byteSource )
    {
        return CreateAttachments.from( CreateAttachment.create().name( name ).mimeType( mimeType ).byteSource( byteSource ).build() );
    }

    protected Content createContent( ContentPath parentPath )
    {
        return doCreateContent( parentPath, "This is my test content #" + UUID.randomUUID(), new PropertyTree(), Mixins.empty(),
                                ContentTypeName.folder() );
    }

    protected Content createContent( final ContentPath parentPath, final String displayName )
    {
        return doCreateContent( parentPath, displayName, new PropertyTree(), Mixins.empty(), ContentTypeName.folder() );
    }

    protected Content createAndPublishContent( final ContentPath parentPath, final Instant publishFrom )
    {
        return createAndPublishContent( parentPath, publishFrom, null );
    }

    protected Content createAndPublishContent( final ContentPath parentPath, final Instant publishFrom, final Instant publishTo )
    {
        final CreateContentParams params =
            createContentBuilder( parentPath, "This is my test content #" + UUID.randomUUID(), new PropertyTree(), Mixins.empty(),
                                  ContentTypeName.folder() ).build();

        return doCreateAndPublishContent( params, publishFrom, publishTo );
    }

    protected Content createContent( final ContentPath parentPath, final String displayName, final PropertyTree data )
    {
        return doCreateContent( parentPath, displayName, data, Mixins.empty(), ContentTypeName.folder() );
    }

    protected Content createContent( final ContentPath parentPath, final String displayName, final PropertyTree data, ContentTypeName type )
    {
        return doCreateContent( parentPath, displayName, data, Mixins.empty(), type );
    }

    protected Content createContent( final ContentPath parentPath, final String displayName, final PropertyTree data, final Mixins mixins )
    {
        return doCreateContent( parentPath, displayName, data, mixins, ContentTypeName.folder() );
    }

    protected Content createContent( final ContentPath parentPath, final String displayName, final AccessControlList permissions )
    {
        final CreateContentParams.Builder builder =
            createContentBuilder( parentPath, displayName, new PropertyTree(), Mixins.empty(), ContentTypeName.folder() );

        builder.permissions( permissions );
        builder.inheritPermissions( false );

        return doCreateContent( builder );
    }

    private Content doCreateContent( final ContentPath parentPath, final String displayName, final PropertyTree data, final Mixins mixins,
                                     ContentTypeName type )
    {
        final CreateContentParams.Builder builder = createContentBuilder( parentPath, displayName, data, mixins, type );
        return doCreateContent( builder );
    }

    private Content doCreateAndPublishContent( final CreateContentParams params, final Instant publishFrom, final Instant publishTo )
    {
        final Context context = ContextAccessor.current();

        return ContextBuilder.from( context ).branch( ContentConstants.BRANCH_DRAFT ).build().callWith( () -> {
            final Content content = this.contentService.create( params );
            this.contentService.publish( PushContentParams.create()
                                             .publishFrom( publishFrom )
                                             .publishTo( publishTo )
                                             .contentIds( ContentIds.from( content.getId() ) )
                                             .build() );
            return content;
        } );
    }

    private Content doCreateContent( final CreateContentParams.Builder builder )
    {
        final Context context = ContextAccessor.current();

        return ContextBuilder.from( context ).branch( ContentConstants.BRANCH_DRAFT ).build().callWith( () -> {
            final Content content = this.contentService.create( builder.build() );
            if ( context.getBranch().equals( ContentConstants.BRANCH_MASTER ) )
            {
                this.contentService.publish( PushContentParams.create().contentIds( ContentIds.from( content.getId() ) ).build() );
            }
            return content;
        } );
    }

    private CreateContentParams.Builder createContentBuilder( final ContentPath parentPath, final String displayName,
                                                              final PropertyTree data, final Mixins mixins, ContentTypeName type )
    {
        return CreateContentParams.create()
            .displayName( displayName )
            .parent( parentPath )
            .contentData( data )
            .mixins( mixins )
            .type( type );
    }

    protected PropertyTree createPropertyTreeForAllInputTypes()
    {

        //Creates a content and a reference to this object
        final Content referredContent = this.contentService.create( CreateContentParams.create()
                                                                        .contentData( new PropertyTree() )
                                                                        .displayName( "Referred content" )
                                                                        .parent( ContentPath.ROOT )
                                                                        .type( ContentTypeName.folder() )
                                                                        .build() );
        final Reference reference = Reference.from( referredContent.getId().toString() );

        //Creates the property tree with value assigned for each attribute
        PropertyTree data = new PropertyTree();

        //Creates a property set
        PropertySet propertySet = data.newSet();
        propertySet.addString( "setString", "stringValue" );
        propertySet.addDouble( "setDouble", 1.5d );

        data.addString( "textLine", "textLine" );
        data.addDouble( "double", 1.4d );
        data.addLong( "long", 2L );
        data.addString( "color", "FFFFFF" );
        data.addString( "comboBox", "value2" );
        data.addBoolean( "checkbox", false );
        data.addString( "phone", "012345678" );
        data.addString( "tag", "tag" );
        data.addReference( "contentSelector", reference );
        data.addString( "contentTypeFilter", "stringValue" );
        data.addString( "siteConfigurator", "com.enonic.app.features" );
        data.addLocalDate( "date", LocalDate.of( 2015, 3, 13 ) );
        data.addLocalTime( "time", LocalTime.NOON );
        data.addGeoPoint( "geoPoint", GeoPoint.from( "59.9127300 ,10.7460900" ) );
        data.addString( "htmlArea", "<p>paragraph</p>" );
        data.addString( "xml", "<elem>paragraph</elem>" );
        data.addLocalDateTime( "localDateTime", LocalDateTime.of( 2015, 3, 13, 10, 0, 0 ) );
        data.addInstant( "dateTime", Instant.now() );
        data.addSet( "set", propertySet );

        return data;
    }


    protected ContentType createContentTypeForAllInputTypes()
    {
        final FormItemSet set = FormItemSet.create()
            .name( "set" )
            .addFormItem( Input.create().label( "String" ).name( "setString" ).inputType( InputTypeName.TEXT_LINE ).build() )
            .addFormItem( Input.create().label( "Double" ).name( "setDouble" ).inputType( InputTypeName.DOUBLE ).build() )
            .build();

        return ContentType.create()
            .superType( ContentTypeName.documentMedia() )
            .name( "myapp:myContentType" )
            .addFormItem( Input.create().label( "Textline" ).name( "textLine" ).inputType( InputTypeName.TEXT_LINE ).build() )
            .addFormItem( Input.create().name( "stringArray" ).label( "String array" ).inputType( InputTypeName.TEXT_LINE ).build() )
            .addFormItem( Input.create().name( "double" ).label( "Double" ).inputType( InputTypeName.DOUBLE ).build() )
            .addFormItem( Input.create().name( "long" ).label( "Long" ).inputType( InputTypeName.LONG ).build() )
            .addFormItem( Input.create()
                              .name( "comboBox" )
                              .label( "Combobox" )
                              .inputType( InputTypeName.COMBO_BOX )
                              .inputTypeConfig( GenericValue.newObject()
                                                    .put( "options", GenericValue.newList()
                                                        .add( GenericValue.newObject()
                                                                  .put( "value", "value1" )
                                                                  .put( "label", GenericValue.newObject().put( "text", "label1" ).build() )
                                                                  .build() )
                                                        .add( GenericValue.newObject()
                                                                  .put( "value", "value2" )
                                                                  .put( "label", GenericValue.newObject().put( "text", "label2" ).build() )
                                                                  .build() )
                                                        .build() )
                                                    .build() )
                              .build() )
            .addFormItem( Input.create().name( "checkbox" ).label( "Checkbox" ).inputType( InputTypeName.CHECK_BOX ).build() )
            .addFormItem( Input.create().name( "tag" ).label( "Tag" ).inputType( InputTypeName.TAG ).build() )
            .addFormItem( Input.create()
                              .name( "contentSelector" )
                              .label( "Content selector" )
                              .inputType( InputTypeName.CONTENT_SELECTOR )
                              .inputTypeProperty( "allowContentType", ContentTypeName.folder().toString() )
                              .build() )
            .addFormItem( Input.create()
                              .name( "contentTypeFilter" )
                              .label( "Content type filter" )
                              .inputType( InputTypeName.CONTENT_TYPE_FILTER )
                              .build() )
            .addFormItem( Input.create()
                              .name( "siteConfigurator" )
                              .inputType( InputTypeName.SITE_CONFIGURATOR )
                              .label( "Site configurator" )
                              .build() )
            .addFormItem( Input.create().name( "date" ).label( "Date" ).inputType( InputTypeName.DATE ).build() )
            .addFormItem( Input.create().name( "time" ).label( "Time" ).inputType( InputTypeName.TIME ).build() )
            .addFormItem( Input.create().name( "geoPoint" ).label( "Geopoint" ).inputType( InputTypeName.GEO_POINT ).build() )
            .addFormItem( Input.create().name( "htmlArea" ).label( "Htmlarea" ).inputType( InputTypeName.HTML_AREA ).build() )
            .addFormItem( Input.create().name( "localDateTime" ).label( "Local datetime" ).inputType( InputTypeName.DATE_TIME ).build() )
            .addFormItem( Input.create().name( "dateTime" ).label( "Datetime" ).inputType( InputTypeName.INSTANT ).build() )
            .addFormItem( set )
            .build();
    }

    protected void assertOrder( final Iterable<ContentId> contentIds, final Content... expectedOrder )
    {
        assertThat( contentIds ).containsExactly( Arrays.stream( expectedOrder ).map( Content::getId ).toArray( ContentId[]::new ) );
    }

    protected void assertVersions( final ContentId contentId, final int expected )
    {
        GetContentVersionsResult versions =
            this.contentService.getVersions( GetContentVersionsParams.create().contentId( contentId ).build() );

        assertThat( versions.getContentVersions() ).hasSize( expected );
    }

    protected void printContentTree( final ContentId rootId )
    {
        doPrintContentTree( rootId );
    }

    protected void printContentTree( final ContentId rootId, final Context context )
    {
        context.runWith( () -> doPrintContentTree( rootId ) );
    }

    private void doPrintContentTree( final ContentId rootId )
    {

        final Content root = this.contentService.getById( rootId );

        final Branch branch = ContextAccessor.current().getBranch();
        System.out.println( "** Content-tree in branch [" + branch.getValue() + "], starting with path [" + root.getPath() + "]" );

        doPrintChildren( 0, root );
    }

    private void doPrintChildren( int ident, final Content root )
    {
        System.out.println( createString( root, ident ) );

        ident += 3;

        final FindContentByParentResult result =
            this.contentService.findByParent( FindContentByParentParams.create().parentId( root.getId() ).size( -1 ).build() );

        for ( final Content content : result.getContents() )
        {
            doPrintChildren( ident, content );
        }
    }

    private String createString( final Content content, final int indent )
    {
        final Branch currentBranch = ContextAccessor.current().getBranch();

      /*  final CompareContentResult compareStatus = this.contentService.compare(
            new CompareContentParams( content.getId(), currentBranch.equals( WS_DEFAULT ) ? WS_OTHER : WS_DEFAULT ) );
*/
        StringBuilder builder = new StringBuilder();
        builder.append( new String( new char[indent] ).replace( '\0', ' ' ) );
        builder.append( "'--" );
        builder.append( requireNonNullElse( content.getName(), "" ) );
        builder.append( " (" );
        builder.append( content.getId().toString(), 0, 8 );
        builder.append( ")" );
        // builder.append( " (" + compareStatus.getCompareStatus().toString().toLowerCase() + ")" );

        return builder.toString();
    }

}
