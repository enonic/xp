package com.enonic.wem.core.content;

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
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import com.enonic.wem.repo.internal.blob.BlobStore;
import com.enonic.wem.repo.internal.blob.file.FileBlobStore;
import com.enonic.wem.repo.internal.elasticsearch.AbstractElasticsearchIntegrationTest;
import com.enonic.wem.repo.internal.elasticsearch.ElasticsearchIndexServiceInternal;
import com.enonic.wem.repo.internal.elasticsearch.ElasticsearchQueryService;
import com.enonic.wem.repo.internal.elasticsearch.branch.ElasticsearchBranchService;
import com.enonic.wem.repo.internal.elasticsearch.version.ElasticsearchVersionService;
import com.enonic.wem.repo.internal.entity.NodeServiceImpl;
import com.enonic.wem.repo.internal.entity.dao.NodeDaoImpl;
import com.enonic.wem.repo.internal.repository.IndexNameResolver;
import com.enonic.wem.repo.internal.repository.RepositoryInitializer;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.impl.content.ContentInitializer;
import com.enonic.xp.core.impl.content.ContentNodeTranslator;
import com.enonic.xp.core.impl.content.ContentServiceImpl;
import com.enonic.xp.core.impl.event.EventPublisherImpl;
import com.enonic.xp.core.impl.media.MediaInfoServiceImpl;
import com.enonic.xp.core.impl.schema.content.ContentTypeRegistryImpl;
import com.enonic.xp.core.impl.schema.content.ContentTypeServiceImpl;
import com.enonic.xp.core.impl.site.SiteDescriptorRegistry;
import com.enonic.xp.core.impl.site.SiteServiceImpl;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.inputtype.ComboBoxConfig;
import com.enonic.xp.form.inputtype.ContentSelectorConfig;
import com.enonic.xp.form.inputtype.DateTimeConfig;
import com.enonic.xp.form.inputtype.InputTypes;
import com.enonic.xp.index.IndexType;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeRegistry;
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

    @Rule
    public TemporaryFolder WEM_HOME = new TemporaryFolder();

    protected ContentServiceImpl contentService;

    protected NodeServiceImpl nodeService;

    protected BlobStore binaryBlobStore;

    protected MixinService mixinService;

    protected ContentTypeRegistry contentTypeRegistry;

    protected ContentNodeTranslator contentNodeTranslator;

    private NodeDaoImpl nodeDao;

    private ElasticsearchVersionService versionService;

    private ElasticsearchBranchService branchService;

    private ElasticsearchIndexServiceInternal indexService;

    private ElasticsearchQueryService queryService;

    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();

        System.setProperty( "wem.home", WEM_HOME.getRoot().getPath() );

        ContextAccessor.INSTANCE.set( CTX_DEFAULT );

        this.binaryBlobStore = new FileBlobStore( "test" );

        this.queryService = new ElasticsearchQueryService();
        this.queryService.setElasticsearchDao( elasticsearchDao );

        this.branchService = new ElasticsearchBranchService();
        this.branchService.setElasticsearchDao( elasticsearchDao );

        this.versionService = new ElasticsearchVersionService();
        this.versionService.setElasticsearchDao( elasticsearchDao );

        this.indexService = new ElasticsearchIndexServiceInternal();
        this.indexService.setClient( client );
        this.indexService.setElasticsearchDao( elasticsearchDao );

        this.nodeDao = new NodeDaoImpl();
        this.nodeDao.setBranchService( this.branchService );

        this.contentService = new ContentServiceImpl();

        this.nodeService = new NodeServiceImpl();
        this.nodeService.setIndexServiceInternal( indexService );
        this.nodeService.setQueryService( queryService );
        this.nodeService.setNodeDao( nodeDao );
        this.nodeService.setVersionService( versionService );
        this.nodeService.setBranchService( branchService );

        this.mixinService = Mockito.mock( MixinService.class );
        this.contentTypeRegistry = Mockito.mock( ContentTypeRegistry.class );

        this.contentNodeTranslator = new ContentNodeTranslator();

        final EventPublisherImpl eventPublisher = new EventPublisherImpl();

        final MediaInfoServiceImpl mediaInfoService = new MediaInfoServiceImpl();
        mediaInfoService.setDetector( new DefaultDetector() );
        mediaInfoService.setParser( new DefaultParser() );

        final SiteDescriptorRegistry siteDescriptorRegistry = Mockito.mock( SiteDescriptorRegistry.class );
        final SiteServiceImpl siteService = new SiteServiceImpl();
        siteService.setSiteDescriptorRegistry( siteDescriptorRegistry );

        final ContentTypeServiceImpl contentTypeService = new ContentTypeServiceImpl();
        contentTypeService.setMixinService( mixinService );
        contentTypeService.setContentTypeRegistry( new ContentTypeRegistryImpl() );

        this.contentService.setNodeService( this.nodeService );
        this.contentService.setEventPublisher( eventPublisher );
        this.contentService.setMediaInfoService( mediaInfoService );
        this.contentService.setSiteService( siteService );
        this.contentService.setContentNodeTranslator( this.contentNodeTranslator );
        this.contentService.setContentTypeService( contentTypeService );
        this.contentService.setMixinService( mixinService );

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
        nodeService.setQueryService( queryService );
        nodeService.setNodeDao( nodeDao );
        nodeService.setVersionService( versionService );
        nodeService.setBranchService( branchService );

        RepositoryInitializer repositoryInitializer = new RepositoryInitializer( indexService );
        repositoryInitializer.initializeRepository( repository.getId() );

        refresh();
    }

    protected ByteSource loadImage( final String name )
        throws IOException
    {
        final InputStream imageStream = this.getClass().getResourceAsStream( name );

        return ByteSource.wrap( ByteStreams.toByteArray( imageStream ) );
    }


    protected CreateAttachments createAttachment( final String name, final String mimeType, final ByteSource image )
    {
        return CreateAttachments.from( CreateAttachment.create().
            name( name ).
            mimeType( mimeType ).
            byteSource( image ).
            build() );
    }

    protected void printContentRepoIndex()
    {
        printAllIndexContent( IndexNameResolver.resolveSearchIndexName( TEST_REPO.getId() ), WS_DEFAULT.getName() );
    }

    protected void printBranchIndex()
    {
        printAllIndexContent( IndexNameResolver.resolveStorageIndexName( CTX_DEFAULT.getRepositoryId() ), IndexType.BRANCH.getName() );
    }

    protected void printVersionIndex()
    {
        printAllIndexContent( IndexNameResolver.resolveStorageIndexName( CTX_DEFAULT.getRepositoryId() ), IndexType.VERSION.getName() );
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
        data.addString( "comboBox", "value3" );
        data.addBoolean( "checkbox", false );
        data.addHtmlPart( "tinyMce", "<p>paragraph</p>" );
        data.addString( "phone", "012345678" );
        data.addString( "tag", "tag" );
        data.addReference( "contentSelector", reference );
        data.addString( "contentTypeFilter", "stringValue" );
        data.addString( "siteConfigurator", "com.enonic.xp.modules.features" );
        data.addLocalDate( "date", LocalDate.of( 2015, 03, 13 ) );
        data.addLocalTime( "time", LocalTime.NOON );
        data.addGeoPoint( "geoPoint", GeoPoint.from( "59.9127300 ,10.7460900" ) );
        data.addHtmlPart( "htmlArea", "<p>paragraph</p>" );
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
                name( "setString" ).
                inputType( InputTypes.TEXT_LINE ).
                build() ).
            addFormItem( Input.create().
                name( "setDouble" ).
                inputType( InputTypes.DOUBLE ).
                build() ).
            build();

        return ContentType.create().
            superType( ContentTypeName.documentMedia() ).
            name( "myContentType" ).
            addFormItem( Input.create().
                name( "textLine" ).
                inputType( InputTypes.TEXT_LINE ).
                build() ).
            addFormItem( Input.create().
                name( "stringArray" ).
                inputType( InputTypes.TEXT_LINE ).
                build() ).
            addFormItem( Input.create().
                name( "double" ).
                inputType( InputTypes.DOUBLE ).
                build() ).
            addFormItem( Input.create().
                name( "long" ).
                inputType( InputTypes.LONG ).
                build() ).
            addFormItem( Input.create().
                name( "color" ).
                inputType( InputTypes.COLOR ).
                build() ).
            addFormItem( Input.create().
                name( "comboBox" ).
                inputType( InputTypes.COMBO_BOX ).
                inputTypeConfig( ComboBoxConfig.create().
                    addOption( "label1", "value1" ).
                    addOption( "label2", "value2" ).
                    addOption( "label3", "value3" ).
                    build() ).
                build() ).
            addFormItem( Input.create().
                name( "checkbox" ).
                inputType( InputTypes.CHECKBOX ).
                build() ).
            addFormItem( Input.create().
                name( "tinyMce" ).
                inputType( InputTypes.TINY_MCE ).
                build() ).
            addFormItem( Input.create().
                name( "phone" ).
                inputType( InputTypes.PHONE ).
                build() ).
            addFormItem( Input.create().
                name( "tag" ).
                inputType( InputTypes.TAG ).
                build() ).
            addFormItem( Input.create().
                name( "contentSelector" ).
                inputType( InputTypes.CONTENT_SELECTOR ).
                inputTypeConfig( ContentSelectorConfig.create().
                    addAllowedContentType( ContentTypeName.folder() ).
                    relationshipType( RelationshipTypeName.REFERENCE ).
                    build() ).
                build() ).
            addFormItem( Input.create().
                name( "contentTypeFilter" ).
                inputType( InputTypes.CONTENT_TYPE_FILTER ).
                build() ).
            addFormItem( Input.create().
                name( "siteConfigurator" ).
                inputType( InputTypes.SITE_CONFIGURATOR ).
                build() ).
            addFormItem( Input.create().
                name( "date" ).
                inputType( InputTypes.DATE ).
                build() ).
            addFormItem( Input.create().
                name( "time" ).
                inputType( InputTypes.TIME ).
                build() ).
            addFormItem( Input.create().
                name( "geoPoint" ).
                inputType( InputTypes.GEO_POINT ).
                build() ).
            addFormItem( Input.create().
                name( "htmlArea" ).
                inputType( InputTypes.HTML_AREA ).
                build() ).
            addFormItem( Input.create().
                name( "xml" ).
                inputType( InputTypes.XML ).
                build() ).
            addFormItem( Input.create().
                name( "localDateTime" ).
                inputType( InputTypes.DATE_TIME ).
                inputTypeConfig( DateTimeConfig.create().
                    withTimezone( false ).
                    build() ).
                build() ).
            addFormItem( Input.create().
                name( "dateTime" ).
                inputType( InputTypes.DATE_TIME ).
                inputTypeConfig( DateTimeConfig.create().
                    withTimezone( true ).
                    build() ).
                build() ).
            addFormItem( set ).
            build();
    }
}
