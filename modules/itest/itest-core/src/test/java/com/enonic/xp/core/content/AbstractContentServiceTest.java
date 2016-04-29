package com.enonic.xp.core.content;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.parser.DefaultParser;
import org.junit.Before;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.impl.content.ContentInitializer;
import com.enonic.xp.core.impl.content.ContentNodeTranslatorImpl;
import com.enonic.xp.core.impl.content.ContentServiceImpl;
import com.enonic.xp.core.impl.event.EventPublisherImpl;
import com.enonic.xp.core.impl.media.MediaInfoServiceImpl;
import com.enonic.xp.core.impl.schema.content.ContentTypeServiceImpl;
import com.enonic.xp.core.impl.site.SiteDescriptorRegistry;
import com.enonic.xp.core.impl.site.SiteServiceImpl;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;
import com.enonic.xp.repo.impl.branch.storage.BranchServiceImpl;
import com.enonic.xp.repo.impl.config.RepoConfiguration;
import com.enonic.xp.repo.impl.elasticsearch.AbstractElasticsearchIntegrationTest;
import com.enonic.xp.repo.impl.elasticsearch.ElasticsearchIndexServiceInternal;
import com.enonic.xp.repo.impl.elasticsearch.search.ElasticsearchSearchDao;
import com.enonic.xp.repo.impl.elasticsearch.storage.ElasticsearchStorageDao;
import com.enonic.xp.repo.impl.node.MemoryBlobStore;
import com.enonic.xp.repo.impl.node.NodeServiceImpl;
import com.enonic.xp.repo.impl.node.dao.NodeVersionDaoImpl;
import com.enonic.xp.repo.impl.repository.RepositoryInitializer;
import com.enonic.xp.repo.impl.search.SearchServiceImpl;
import com.enonic.xp.repo.impl.storage.IndexedDataServiceImpl;
import com.enonic.xp.repo.impl.storage.StorageServiceImpl;
import com.enonic.xp.repo.impl.version.VersionServiceImpl;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.schema.relationship.RelationshipTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.util.GeoPoint;
import com.enonic.xp.util.Reference;

public class AbstractContentServiceTest
    extends AbstractElasticsearchIntegrationTest
{
    public static final User TEST_DEFAULT_USER =
        User.create().key( PrincipalKey.ofUser( UserStoreKey.system(), "test-user" ) ).login( "test-user" ).build();

    public static final AuthenticationInfo TEST_DEFAULT_USER_AUTHINFO = AuthenticationInfo.create().
        principals( RoleKeys.AUTHENTICATED ).
        principals( RoleKeys.CONTENT_MANAGER_ADMIN ).
        user( TEST_DEFAULT_USER ).
        build();

    protected static final Branch WS_DEFAULT = Branch.create().
        name( "draft" ).
        build();

    protected static final Branch WS_OTHER = Branch.create().
        name( "master" ).
        build();

    protected static final Context CTX_DEFAULT = ContextBuilder.create().
        branch( WS_DEFAULT ).
        repositoryId( TEST_REPO.getId() ).
        authInfo( TEST_DEFAULT_USER_AUTHINFO ).
        build();

    protected static final Context CTX_OTHER = ContextBuilder.create().
        branch( WS_OTHER ).
        repositoryId( TEST_REPO.getId() ).
        authInfo( TEST_DEFAULT_USER_AUTHINFO ).
        build();

    protected ContentServiceImpl contentService;

    protected NodeServiceImpl nodeService;

    protected BlobStore blobStore;

    protected MixinService mixinService;

    protected ContentNodeTranslatorImpl translator;

    private NodeVersionDaoImpl nodeDao;

    private VersionServiceImpl versionService;

    private BranchServiceImpl branchService;

    private ElasticsearchIndexServiceInternal indexService;

    private StorageServiceImpl storageService;

    private SearchServiceImpl searchService;

    private IndexedDataServiceImpl indexedDataService;

    private ElasticsearchSearchDao searchDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();

        final RepoConfiguration repoConfig = Mockito.mock( RepoConfiguration.class );
        Mockito.when( repoConfig.getBlobStoreDir() ).thenReturn( new File( this.xpHome.getRoot(), "repo/blob" ) );

        ContextAccessor.INSTANCE.set( CTX_DEFAULT );

        this.blobStore = new MemoryBlobStore();

        final ElasticsearchStorageDao storageDao = new ElasticsearchStorageDao();
        storageDao.setClient( this.client );
        storageDao.setElasticsearchDao( this.elasticsearchDao );

        final EventPublisherImpl eventPublisher = new EventPublisherImpl();

        this.branchService = new BranchServiceImpl();
        this.branchService.setStorageDao( storageDao );

        this.versionService = new VersionServiceImpl();
        this.versionService.setStorageDao( storageDao );

        this.indexService = new ElasticsearchIndexServiceInternal();
        this.indexService.setClient( client );
        this.indexService.setElasticsearchDao( elasticsearchDao );

        this.nodeDao = new NodeVersionDaoImpl();
        this.nodeDao.setConfiguration( repoConfig );
        this.nodeDao.setBlobStore( blobStore );

        this.contentService = new ContentServiceImpl();

        this.indexedDataService = new IndexedDataServiceImpl();
        this.indexedDataService.setStorageDao( storageDao );

        this.storageService = new StorageServiceImpl();
        this.storageService.setBranchService( this.branchService );
        this.storageService.setVersionService( this.versionService );
        this.storageService.setNodeVersionDao( this.nodeDao );
        this.storageService.setIndexServiceInternal( this.indexService );
        this.storageService.setIndexedDataService( this.indexedDataService );

        this.searchDao = new ElasticsearchSearchDao();
        this.searchDao.setElasticsearchDao( this.elasticsearchDao );

        this.searchService = new SearchServiceImpl();
        this.searchService.setSearchDao( this.searchDao );

        this.nodeService = new NodeServiceImpl();
        this.nodeService.setIndexServiceInternal( indexService );
        this.nodeService.setStorageService( storageService );
        this.nodeService.setSearchService( searchService );
        this.nodeService.setEventPublisher( eventPublisher );
        this.nodeService.setConfiguration( repoConfig );
        this.nodeService.setBlobStore( blobStore );
        this.nodeService.initialize();

        this.mixinService = Mockito.mock( MixinService.class );

        final MediaInfoServiceImpl mediaInfoService = new MediaInfoServiceImpl();
        mediaInfoService.setDetector( new DefaultDetector() );
        mediaInfoService.setParser( new DefaultParser() );
        mediaInfoService.activate();

        final SiteDescriptorRegistry siteDescriptorRegistry = Mockito.mock( SiteDescriptorRegistry.class );
        final SiteServiceImpl siteService = new SiteServiceImpl();
        siteService.setSiteDescriptorRegistry( siteDescriptorRegistry );

        final ContentTypeServiceImpl contentTypeService = new ContentTypeServiceImpl();
        contentTypeService.setMixinService( mixinService );

        this.translator = new ContentNodeTranslatorImpl();
        this.translator.setNodeService( this.nodeService );

        this.contentService.setNodeService( this.nodeService );
        this.contentService.setEventPublisher( eventPublisher );
        this.contentService.setMediaInfoService( mediaInfoService );
        this.contentService.setSiteService( siteService );
        this.contentService.setContentTypeService( contentTypeService );
        this.contentService.setMixinService( mixinService );
        this.contentService.setTranslator( this.translator );

        createContentRepository();
        waitForClusterHealth();

        final ContentInitializer contentInitializer = new ContentInitializer( this.nodeService );
        contentInitializer.initialize();
    }

    void createContentRepository()
    {
        createRepository( TEST_REPO );
    }

    void createRepository( final Repository repository )
    {
        NodeServiceImpl nodeService = new NodeServiceImpl();
        nodeService.setIndexServiceInternal( indexService );
        nodeService.setSearchService( searchService );
        nodeService.setStorageService( storageService );

        RepositoryInitializer repositoryInitializer = new RepositoryInitializer( indexService );
        repositoryInitializer.initializeRepositories( repository.getId() );

        refresh();
    }

    protected ByteSource loadImage( final String name )
        throws IOException
    {
        final InputStream imageStream = this.getClass().getResourceAsStream( name );

        return ByteSource.wrap( ByteStreams.toByteArray( imageStream ) );
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
        throws Exception
    {

        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my test content #" + UUID.randomUUID().toString() ).
            parent( parentPath ).
            type( ContentTypeName.folder() ).
            build();

        return this.contentService.create( createContentParams );
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

        //Creates a property set
        PropertySet propertySet = new PropertySet();
        propertySet.addString( "setString", "stringValue" );
        propertySet.addDouble( "setDouble", 1.5d );

        //Creates the property tree with value assigned for each attribute
        PropertyTree data = new PropertyTree();
        data.addString( "textLine", "textLine" );
        data.addDouble( "double", 1.4d );
        data.addLong( "long", 2l );
        data.addString( "color", "FFFFFF" );
        data.addString( "comboBox", "value2" );
        data.addBoolean( "checkbox", false );
        data.addString( "phone", "012345678" );
        data.addString( "tag", "tag" );
        data.addReference( "contentSelector", reference );
        data.addString( "contentTypeFilter", "stringValue" );
        data.addString( "siteConfigurator", "com.enonic.app.features" );
        data.addLocalDate( "date", LocalDate.of( 2015, 03, 13 ) );
        data.addLocalTime( "time", LocalTime.NOON );
        data.addGeoPoint( "geoPoint", GeoPoint.from( "59.9127300 ,10.7460900" ) );
        data.addString( "htmlArea", "<p>paragraph</p>" );
        data.addString( "xml", "<elem>paragraph</elem>" );
        data.addLocalDateTime( "localDateTime", LocalDateTime.of( 2015, 03, 13, 10, 00, 0 ) );
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
                inputTypeProperty( InputTypeProperty.create( "allowedContentType", ContentTypeName.folder().toString() ).build() ).
                inputTypeProperty( InputTypeProperty.create( "relationshipType", RelationshipTypeName.REFERENCE.toString() ).build() ).
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
}
