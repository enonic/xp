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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.ContentVersion;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.content.FindContentByParentResult;
import com.enonic.xp.content.FindContentVersionsParams;
import com.enonic.xp.content.FindContentVersionsResult;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextAccessorSupport;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.impl.content.ContentAuditLogFilterService;
import com.enonic.xp.core.impl.content.ContentAuditLogSupportImpl;
import com.enonic.xp.core.impl.content.ContentConfig;
import com.enonic.xp.core.impl.content.ContentServiceImpl;
import com.enonic.xp.core.impl.content.validate.ContentNameValidator;
import com.enonic.xp.core.impl.content.validate.ExtraDataValidator;
import com.enonic.xp.core.impl.content.validate.OccurrenceValidator;
import com.enonic.xp.core.impl.content.validate.SiteConfigsValidator;
import com.enonic.xp.core.impl.event.EventPublisherImpl;
import com.enonic.xp.core.impl.media.MediaInfoServiceImpl;
import com.enonic.xp.core.impl.project.ProjectConfig;
import com.enonic.xp.core.impl.project.ProjectServiceImpl;
import com.enonic.xp.core.impl.project.init.ContentInitializer;
import com.enonic.xp.core.impl.schema.content.ContentTypeServiceImpl;
import com.enonic.xp.core.impl.security.SecurityAuditLogSupportImpl;
import com.enonic.xp.core.impl.security.SecurityConfig;
import com.enonic.xp.core.impl.security.SecurityInitializer;
import com.enonic.xp.core.impl.security.SecurityServiceImpl;
import com.enonic.xp.core.impl.site.SiteServiceImpl;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.extractor.BinaryExtractor;
import com.enonic.xp.extractor.ExtractedData;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;
import com.enonic.xp.internal.blobstore.MemoryBlobStore;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.project.CreateProjectParams;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartDescriptorService;
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
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.schema.xdata.XDataService;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.util.GeoPoint;
import com.enonic.xp.util.Reference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class AbstractContentServiceTest
    extends AbstractElasticsearchIntegrationTest
{
    private static final AtomicInteger PROJECT_COUNTER = new AtomicInteger();

    private static final MemoryBlobStore BLOB_STORE = new MemoryBlobStore();

    private final ProjectName testprojectName = ProjectName.from( "test" + PROJECT_COUNTER.incrementAndGet() );

    public static final User TEST_DEFAULT_USER =
        User.create().key( PrincipalKey.ofUser( IdProviderKey.system(), "test-user" ) ).login( "test-user" ).build();

    public static final AuthenticationInfo TEST_DEFAULT_USER_AUTHINFO = AuthenticationInfo.create().
        principals( RoleKeys.AUTHENTICATED ).
        principals( RoleKeys.CONTENT_MANAGER_ADMIN ).
        user( TEST_DEFAULT_USER ).
        build();

    protected ProjectServiceImpl projectService;

    protected ContentServiceImpl contentService;

    protected ContentConfig config;

    protected NodeServiceImpl nodeService;

    protected MixinService mixinService;

    protected XDataService xDataService;

    protected AuditLogService auditLogService;

    protected IndexServiceImpl indexService;

    protected ContentAuditLogFilterService contentAuditLogFilterService;

    protected PageDescriptorService pageDescriptorService;

    protected EventPublisher eventPublisher;

    private ExecutorService executorService;

    private Context initialContext;

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
        return ContextBuilder.create().
            branch( ContentConstants.BRANCH_MASTER ).
            repositoryId( testprojectName.getRepoId() ).
            authInfo( TEST_DEFAULT_USER_AUTHINFO ).
            build();
    }

    public Context ctxMasterAnonymous()
    {
        return ContextBuilder.create().
            branch( ContentConstants.BRANCH_MASTER ).
            repositoryId( testprojectName.getRepoId() ).
            build();
    }

    public Context ctxMasterSu()
    {
        return ContextBuilder.create().
            branch( ContentConstants.BRANCH_MASTER ).
            repositoryId( testprojectName.getRepoId() ).
            authInfo( AuthenticationInfo.create().
                principals( RoleKeys.ADMIN ).
                user( ContentInitializer.SUPER_USER ).
                build() ).
            build();
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

        final BinaryServiceImpl binaryService = new BinaryServiceImpl( BLOB_STORE );

        final StorageDaoImpl storageDao = new StorageDaoImpl( client );

        this.eventPublisher = new EventPublisherImpl( executorService );

        final SearchDaoImpl searchDao = new SearchDaoImpl( client );

        BranchServiceImpl branchService = new BranchServiceImpl( storageDao, searchDao );

        VersionServiceImpl versionService = new VersionServiceImpl( storageDao );

        CommitServiceImpl commitService = new CommitServiceImpl( storageDao );

        IndexServiceInternalImpl indexServiceInternal = new IndexServiceInternalImpl( client );

        NodeVersionServiceImpl nodeDao = new NodeVersionServiceImpl( BLOB_STORE, new RepoConfiguration( Map.of() ) );

        IndexDataServiceImpl indexedDataService = new IndexDataServiceImpl( storageDao );

        final NodeStorageServiceImpl storageService =
            new NodeStorageServiceImpl( versionService, branchService, commitService, nodeDao, indexedDataService );

        NodeSearchServiceImpl searchService = new NodeSearchServiceImpl( searchDao );
        final RepositoryEntryServiceImpl repositoryEntryService =
            new RepositoryEntryServiceImpl( indexServiceInternal, storageService, searchService, eventPublisher, binaryService );

        indexService = new IndexServiceImpl( indexServiceInternal, indexedDataService, searchService, nodeDao, repositoryEntryService );

        final NodeRepositoryServiceImpl nodeRepositoryService = new NodeRepositoryServiceImpl( indexServiceInternal );

        RepositoryServiceImpl repositoryService =
            new RepositoryServiceImpl( repositoryEntryService, indexServiceInternal, nodeRepositoryService, storageService,
                                       searchService );
        SystemRepoInitializer.create().
            setIndexServiceInternal( indexServiceInternal ).
            setRepositoryService( repositoryService ).
            setNodeStorageService( storageService ).
            build().
            initialize();

        nodeService = new NodeServiceImpl( indexServiceInternal, storageService, searchService, eventPublisher, binaryService, repositoryService );

        mixinService = mock( MixinService.class );
        when( mixinService.inlineFormItems( Mockito.isA( Form.class ) ) ).then( AdditionalAnswers.returnsFirstArg() );

        xDataService = mock( XDataService.class );

        Map<String, List<String>> metadata = new HashMap<>();
        metadata.put( HttpHeaders.CONTENT_TYPE, List.of( "image/jpeg" ) );

        final ExtractedData extractedData = ExtractedData.create().
            metadata( metadata ).
            build();

        final BinaryExtractor extractor = mock( BinaryExtractor.class );
        when( extractor.extract( Mockito.isA( ByteSource.class ) ) ).
            thenReturn( extractedData );

        MediaInfoServiceImpl mediaInfoService = new MediaInfoServiceImpl( extractor );

        final ResourceService resourceService = mock( ResourceService.class );
        final SiteServiceImpl siteService = new SiteServiceImpl();
        siteService.setResourceService( resourceService );
        siteService.setMixinService( mixinService );

        ContentTypeServiceImpl contentTypeService = new ContentTypeServiceImpl( resourceService, null, mixinService );

        this.pageDescriptorService = mock( PageDescriptorService.class );
        PartDescriptorService partDescriptorService = mock( PartDescriptorService.class );
        LayoutDescriptorService layoutDescriptorService = mock( LayoutDescriptorService.class );
        auditLogService = mock( AuditLogService.class );

        contentAuditLogFilterService = mock( ContentAuditLogFilterService.class, invocation -> true );

        final ContentConfig contentConfig = mock( ContentConfig.class );
        when( contentConfig.auditlog_enabled() ).thenReturn( Boolean.TRUE );

        final ContentAuditLogSupportImpl contentAuditLogSupport =
            new ContentAuditLogSupportImpl( contentConfig, Runnable::run, auditLogService, contentAuditLogFilterService );

        final SecurityConfig securityConfig = mock( SecurityConfig.class );
        when( securityConfig.auditlog_enabled() ).thenReturn( Boolean.TRUE );

        final ProjectConfig projectConfig = mock( ProjectConfig.class );

        final SecurityAuditLogSupportImpl securityAuditLogSupport = new SecurityAuditLogSupportImpl( auditLogService );
        securityAuditLogSupport.activate( securityConfig );

        final SecurityServiceImpl securityService = new SecurityServiceImpl( nodeService, securityAuditLogSupport );
        SecurityInitializer.create()
            .setIndexService( indexService )
            .setSecurityService( securityService )
            .setNodeService( nodeService )
            .build()
            .initialize();

        projectService = new ProjectServiceImpl( repositoryService, indexService, nodeService, securityService, eventPublisher, projectConfig );
        projectService.initialize();

        projectService.create( CreateProjectParams.create().name( testprojectName ).displayName( "test" ).build() );

        this.config = mock( ContentConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        contentService =
            new ContentServiceImpl( nodeService, pageDescriptorService, partDescriptorService, layoutDescriptorService, ( form, data ) -> {
            }, ( page ) -> {
            }, config );
        contentService.setEventPublisher( eventPublisher );
        contentService.setMediaInfoService( mediaInfoService );
        contentService.setSiteService( siteService );
        contentService.setContentTypeService( contentTypeService );
        contentService.setxDataService( xDataService );
        contentService.setContentAuditLogSupport( contentAuditLogSupport );

        contentService.addContentValidator( new ContentNameValidator() );
        contentService.addContentValidator( new SiteConfigsValidator( siteService ) );
        contentService.addContentValidator( new OccurrenceValidator() );
        contentService.addContentValidator( new ExtraDataValidator( xDataService ) );
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
        } catch ( IOException e ) {
            throw new UncheckedIOException( e );
        }
    }

    protected CreateAttachments createAttachment( final String name, final String mimeType, final ByteSource byteSource )
    {
        return CreateAttachments.from( CreateAttachment.create().
            name( name ).
            mimeType( mimeType ).
            byteSource( byteSource ).
            build() );
    }

    protected Content createContent( ContentPath parentPath )
    {
        return doCreateContent( parentPath, "This is my test content #" + UUID.randomUUID(), new PropertyTree(), ExtraDatas.empty(),
                                ContentTypeName.folder() );
    }

    protected Content createContent( final ContentPath parentPath, final String displayName )
    {
        return doCreateContent( parentPath, displayName, new PropertyTree(), ExtraDatas.empty(), ContentTypeName.folder() );
    }

    protected Content createContent( ContentPath parentPath, final ContentPublishInfo publishInfo )
    {
        final CreateContentParams.Builder builder =
            createContentBuilder( parentPath, "This is my test content #" + UUID.randomUUID(), new PropertyTree(), ExtraDatas.empty(),
                                  ContentTypeName.folder() ).
                contentPublishInfo( publishInfo );

        return doCreateContent( builder );
    }

    protected Content createContent( final ContentPath parentPath, final String displayName, final PropertyTree data )
    {
        return doCreateContent( parentPath, displayName, data, ExtraDatas.empty(), ContentTypeName.folder() );
    }

    protected Content createContent( final ContentPath parentPath, final String displayName, final PropertyTree data, ContentTypeName type )
    {
        return doCreateContent( parentPath, displayName, data, ExtraDatas.empty(), type );
    }

    protected Content createContent( final ContentPath parentPath, final String displayName, final PropertyTree data,
                                     final ExtraDatas extraDatas )
    {
        return doCreateContent( parentPath, displayName, data, extraDatas, ContentTypeName.folder() );
    }

    protected Content createContent( final ContentPath parentPath, final String displayName, final AccessControlList permissions )
    {
        final CreateContentParams.Builder builder =
            createContentBuilder( parentPath, displayName, new PropertyTree(), ExtraDatas.empty(), ContentTypeName.folder() );

        builder.permissions( permissions );
        builder.inheritPermissions( false );

        return doCreateContent( builder );
    }

    private Content doCreateContent( final ContentPath parentPath, final String displayName, final PropertyTree data,
                                     final ExtraDatas extraDatas, ContentTypeName type )
    {
        final CreateContentParams.Builder builder = createContentBuilder( parentPath, displayName, data, extraDatas, type );
        return doCreateContent( builder );
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
                                                              final PropertyTree data, final ExtraDatas extraDatas, ContentTypeName type )
    {
        return CreateContentParams.create().
            displayName( displayName ).
            parent( parentPath ).
            contentData( data ).
            extraDatas( extraDatas ).
            type( type );
    }

    protected PropertyTree createPropertyTreeForAllInputTypes()
    {

        //Creates a content and a reference to this object
        final Content referredContent = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "Referred content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build() );
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
        final FormItemSet set = FormItemSet.create().
            name( "set" ).
            addFormItem( Input.create().
                label( "String" ).
                name( "setString" ).
                inputType( InputTypeName.TEXT_LINE ).
                build() ).
            addFormItem( Input.create().
                label( "Double" ).
                name( "setDouble" ).
                inputType( InputTypeName.DOUBLE ).
                build() ).
            build();

        return ContentType.create().
            superType( ContentTypeName.documentMedia() ).
            name( "myContentType" ).
            addFormItem( Input.create().
                label( "Textline" ).
                name( "textLine" ).
                inputType( InputTypeName.TEXT_LINE ).
                build() ).
            addFormItem( Input.create().
                name( "stringArray" ).
                label( "String array" ).
                inputType( InputTypeName.TEXT_LINE ).
                build() ).
            addFormItem( Input.create().
                name( "double" ).
                label( "Double" ).
                inputType( InputTypeName.DOUBLE ).
                build() ).
            addFormItem( Input.create().
                name( "long" ).
                label( "Long" ).
                inputType( InputTypeName.LONG ).
                build() ).
            addFormItem( Input.create().
                name( "comboBox" ).
                label( "Combobox" ).
                inputType( InputTypeName.COMBO_BOX ).
                inputTypeProperty( InputTypeProperty.create( "option", "label1" ).attribute( "value", "value1" ).build() ).
                inputTypeProperty( InputTypeProperty.create( "option", "label2" ).attribute( "value", "value2" ).build() ).
                build() ).
            addFormItem( Input.create().
                name( "checkbox" ).
                label( "Checkbox" ).
                inputType( InputTypeName.CHECK_BOX ).
                build() ).
            addFormItem( Input.create().
                name( "tag" ).
                label( "Tag" ).
                inputType( InputTypeName.TAG ).
                build() ).
            addFormItem( Input.create().
                name( "contentSelector" ).
                label( "Content selector" ).
                inputType( InputTypeName.CONTENT_SELECTOR ).
                inputTypeProperty( InputTypeProperty.create( "allowContentType", ContentTypeName.folder().toString() ).build() ).
                build() ).
            addFormItem( Input.create().
                name( "contentTypeFilter" ).
                label( "Content type filter" ).
                inputType( InputTypeName.CONTENT_TYPE_FILTER ).
                build() ).
            addFormItem( Input.create().
                name( "siteConfigurator" ).
                inputType( InputTypeName.SITE_CONFIGURATOR ).
                label( "Site configurator" ).
                build() ).
            addFormItem( Input.create().
                name( "date" ).
                label( "Date" ).
                inputType( InputTypeName.DATE ).
                build() ).
            addFormItem( Input.create().
                name( "time" ).
                label( "Time" ).
                inputType( InputTypeName.TIME ).
                build() ).
            addFormItem( Input.create().
                name( "geoPoint" ).
                label( "Geopoint" ).
                inputType( InputTypeName.GEO_POINT ).
                build() ).
            addFormItem( Input.create().
                name( "htmlArea" ).
                label( "Htmlarea" ).
                inputType( InputTypeName.HTML_AREA ).
                build() ).
            addFormItem( Input.create().
                name( "localDateTime" ).
                label( "Local datetime" ).
                inputType( InputTypeName.DATE_TIME ).
                inputTypeProperty( InputTypeProperty.create( "timezone", "false" ).build() ).
                build() ).
            addFormItem( Input.create().
                name( "dateTime" ).
                label( "Datetime" ).
                inputType( InputTypeName.DATE_TIME ).
                inputTypeProperty( InputTypeProperty.create( "timezone", "true" ).build() ).
                build() ).
            addFormItem( set ).
            build();
    }

    protected void assertOrder( final Iterable<ContentId> contentIds, final Content... expectedOrder )
    {
        assertThat( contentIds ).containsExactly( Arrays.stream( expectedOrder ).map( Content::getId ).toArray( ContentId[]::new ) );
    }

    protected void assertVersions( final ContentId contentId, final int expected )
    {
        FindContentVersionsResult versions = this.contentService.getVersions( FindContentVersionsParams.create().
            contentId( contentId ).
            build() );

        assertEquals( expected, versions.getHits() );

        final Iterator<ContentVersion> iterator = versions.getContentVersions().iterator();

        Instant lastModified = null;

        while ( iterator.hasNext() )
        {
            final ContentVersion next = iterator.next();

            if ( lastModified != null )
            {
                assertFalse( next.getModified().isAfter( lastModified ) );
            }

            lastModified = next.getModified();
        }
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

        final FindContentByParentResult result = this.contentService.findByParent( FindContentByParentParams.create().
            parentId( root.getId() ).
            size( -1 ).
            build() );

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
        builder.append( Objects.requireNonNullElse( content.getName(), "" ) );
        builder.append( " (" );
        builder.append( content.getId().toString(), 0, 8 );
        builder.append( ")" );
        // builder.append( " (" + compareStatus.getCompareStatus().toString().toLowerCase() + ")" );

        return builder.toString();
    }

}
