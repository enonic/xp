package com.enonic.xp.core.content;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.google.common.net.HttpHeaders;

import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
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
import com.enonic.xp.content.FindContentByQueryResult;
import com.enonic.xp.content.FindContentVersionsParams;
import com.enonic.xp.content.FindContentVersionsResult;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.impl.content.ContentInitializer;
import com.enonic.xp.core.impl.content.ContentServiceImpl;
import com.enonic.xp.core.impl.event.EventPublisherImpl;
import com.enonic.xp.core.impl.media.MediaInfoServiceImpl;
import com.enonic.xp.core.impl.schema.content.ContentTypeServiceImpl;
import com.enonic.xp.core.impl.site.SiteServiceImpl;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.extractor.BinaryExtractor;
import com.enonic.xp.extractor.ExtractedData;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;
import com.enonic.xp.internal.blobstore.MemoryBlobStore;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartDescriptorService;
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
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.schema.relationship.RelationshipTypeName;
import com.enonic.xp.schema.xdata.XDataService;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.util.GeoPoint;
import com.enonic.xp.util.Reference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AbstractContentServiceTest
    extends AbstractElasticsearchIntegrationTest
{
    protected static final Repository TEST_REPO = Repository.create().
            id( RepositoryId.from( "com.enonic.cms.default" ) ).
            branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) ).
            build();

    public static final User TEST_DEFAULT_USER =
        User.create().key( PrincipalKey.ofUser( IdProviderKey.system(), "test-user" ) ).login( "test-user" ).build();

    public static final AuthenticationInfo TEST_DEFAULT_USER_AUTHINFO = AuthenticationInfo.create().
        principals( RoleKeys.AUTHENTICATED ).
        principals( RoleKeys.CONTENT_MANAGER_ADMIN ).
        user( TEST_DEFAULT_USER ).
        build();

    public static final Context MASTER_CONTEXT = ContextBuilder.create().
        branch( ContentConstants.BRANCH_MASTER ).
        repositoryId( ContentConstants.CONTENT_REPO.getId() ).
        build();

    public static final Context AUTHORIZED_MASTER_CONTEXT = ContextBuilder.create().
        branch( ContentConstants.BRANCH_MASTER ).
        repositoryId( ContentConstants.CONTENT_REPO.getId() ).
        authInfo( AuthenticationInfo.create().
            principals( RoleKeys.ADMIN ).
            user( ContentInitializer.SUPER_USER ).
            build() ).
        build();

    protected static final Branch WS_DEFAULT = Branch.create().
        value( "draft" ).
        build();

    protected static final Branch WS_OTHER = Branch.create().
        value( "master" ).
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

    protected BinaryServiceImpl binaryService;

    protected MixinService mixinService;

    protected XDataService xDataService;

    protected ContentTypeServiceImpl contentTypeService;

    protected AuditLogService auditLogService;

    @BeforeEach
    public void setUpAbstractContentServiceTest()
        throws Exception
    {
        deleteAllIndices();

        ContextAccessor.INSTANCE.set( CTX_DEFAULT );

        final MemoryBlobStore blobStore = new MemoryBlobStore();

        binaryService = new BinaryServiceImpl();
        binaryService.setBlobStore( blobStore );

        final StorageDaoImpl storageDao = new StorageDaoImpl();
        storageDao.setClient( client );

        final EventPublisherImpl eventPublisher = new EventPublisherImpl();

        SearchDaoImpl searchDao = new SearchDaoImpl();
        searchDao.setClient( client );

        BranchServiceImpl branchService = new BranchServiceImpl();
        branchService.setStorageDao( storageDao );
        branchService.setSearchDao(searchDao);

        VersionServiceImpl versionService = new VersionServiceImpl();
        versionService.setStorageDao( storageDao );

        CommitServiceImpl commitService = new CommitServiceImpl();
        commitService.setStorageDao( storageDao );

        IndexServiceInternalImpl indexServiceInternal = new IndexServiceInternalImpl();
        indexServiceInternal.setClient( client );

        NodeVersionServiceImpl nodeDao = new NodeVersionServiceImpl();
        nodeDao.setBlobStore( blobStore );

        contentService = new ContentServiceImpl();

        IndexDataServiceImpl indexedDataService = new IndexDataServiceImpl();
        indexedDataService.setStorageDao( storageDao );

        NodeStorageServiceImpl storageService = new NodeStorageServiceImpl();
        storageService.setBranchService(branchService);
        storageService.setVersionService(versionService);
        storageService.setCommitService(commitService);
        storageService.setNodeVersionService(nodeDao);
        storageService.setIndexDataService(indexedDataService);

        NodeSearchServiceImpl searchService = new NodeSearchServiceImpl();
        searchService.setSearchDao(searchDao);

        final NodeRepositoryServiceImpl nodeRepositoryService = new NodeRepositoryServiceImpl();
        nodeRepositoryService.setIndexServiceInternal(indexServiceInternal);

        final IndexServiceInternalImpl elasticsearchIndexService = new IndexServiceInternalImpl();
        elasticsearchIndexService.setClient( client );

        final RepositoryEntryServiceImpl repositoryEntryService = new RepositoryEntryServiceImpl();
        repositoryEntryService.setIndexServiceInternal( elasticsearchIndexService );
        repositoryEntryService.setNodeRepositoryService( nodeRepositoryService );
        repositoryEntryService.setNodeStorageService(storageService);
        repositoryEntryService.setNodeSearchService(searchService);
        repositoryEntryService.setEventPublisher( eventPublisher );
        repositoryEntryService.setBinaryService( binaryService );

        IndexServiceImpl indexService = new IndexServiceImpl();
        indexService.setIndexServiceInternal( indexServiceInternal );
        indexService.setRepositoryEntryService( repositoryEntryService );

        RepositoryServiceImpl repositoryService = new RepositoryServiceImpl();
        repositoryService.setRepositoryEntryService( repositoryEntryService );
        repositoryService.setIndexServiceInternal( elasticsearchIndexService );
        repositoryService.setNodeRepositoryService( nodeRepositoryService );
        repositoryService.setNodeStorageService(storageService);
        repositoryService.setNodeSearchService(searchService);
        repositoryService.setIndexService( indexService );
        repositoryService.initialize();

        nodeService = new NodeServiceImpl();
        nodeService.setIndexServiceInternal(indexServiceInternal);
        nodeService.setNodeStorageService(storageService);
        nodeService.setNodeSearchService(searchService);
        nodeService.setEventPublisher( eventPublisher );
        nodeService.setBinaryService( binaryService );
        nodeService.setRepositoryService(repositoryService);
        nodeService.initialize();

        mixinService = Mockito.mock( MixinService.class );
        Mockito.when( mixinService.inlineFormItems( Mockito.isA( Form.class ) ) ).then( AdditionalAnswers.returnsFirstArg() );

        xDataService = Mockito.mock( XDataService.class );

        Map<String, List<String>> metadata = new HashMap<>();
        metadata.put( HttpHeaders.CONTENT_TYPE, Lists.newArrayList( "image/jpg" ) );

        final ExtractedData extractedData = ExtractedData.create().
            metadata( metadata ).
            build();

        final BinaryExtractor extractor = Mockito.mock( BinaryExtractor.class );
        Mockito.when( extractor.extract( Mockito.isA( ByteSource.class ) ) ).
            thenReturn( extractedData );

        final MediaInfoServiceImpl mediaInfoService = new MediaInfoServiceImpl();
        mediaInfoService.setBinaryExtractor( extractor );

        final ResourceService resourceService = Mockito.mock( ResourceService.class );
        final SiteServiceImpl siteService = new SiteServiceImpl();
        siteService.setResourceService( resourceService );
        siteService.setMixinService( mixinService );

        contentTypeService = new ContentTypeServiceImpl();
        contentTypeService.setMixinService( mixinService );

        PageDescriptorService pageDescriptorService = Mockito.mock(PageDescriptorService.class);
        PartDescriptorService partDescriptorService = Mockito.mock(PartDescriptorService.class);
        LayoutDescriptorService layoutDescriptorService = Mockito.mock(LayoutDescriptorService.class);
        auditLogService = Mockito.mock( AuditLogService.class );

        contentService.setNodeService( nodeService );
        contentService.setEventPublisher( eventPublisher );
        contentService.setMediaInfoService( mediaInfoService );
        contentService.setSiteService( siteService );
        contentService.setContentTypeService( contentTypeService );
        contentService.setxDataService( xDataService );
        contentService.setPageDescriptorService(pageDescriptorService);
        contentService.setPartDescriptorService(partDescriptorService);
        contentService.setLayoutDescriptorService(layoutDescriptorService);
        contentService.setAuditLogService( auditLogService );
        contentService.setFormDefaultValuesProcessor( ( form, data ) -> {
        } );
        contentService.setIndexService(indexService);
        contentService.setNodeService( nodeService );
        contentService.setRepositoryService(repositoryService);
        contentService.initialize();

        waitForClusterHealth();
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
        return doCreateContent( parentPath, "This is my test content #" + UUID.randomUUID().toString(), new PropertyTree(),
                                ExtraDatas.empty(), ContentTypeName.folder() );
    }

    protected Content createContent( final ContentPath parentPath, final String displayName )
    {
        return doCreateContent( parentPath, displayName, new PropertyTree(), ExtraDatas.empty(), ContentTypeName.folder() );
    }

    protected Content createContent( ContentPath parentPath, final ContentPublishInfo publishInfo )
        throws Exception
    {
        final CreateContentParams.Builder builder =
            createContentBuilder( parentPath, "This is my test content #" + UUID.randomUUID().toString(), new PropertyTree(),
                                  ExtraDatas.empty(), ContentTypeName.folder() ).
                contentPublishInfo( publishInfo );

        return doCreateContent( builder );
    }

    protected Content createContent( final ContentPath parentPath, final String displayName, final PropertyTree data )
        throws Exception
    {

        return doCreateContent( parentPath, displayName, data, ExtraDatas.empty(), ContentTypeName.folder() );
    }

    protected Content createContent( final ContentPath parentPath, final String displayName, final PropertyTree data, ContentTypeName type )
        throws Exception
    {
        return doCreateContent( parentPath, displayName, data, ExtraDatas.empty(), type );
    }

    protected Content createContent( final ContentPath parentPath, final String displayName, final PropertyTree data,
                                     final ExtraDatas extraDatas )
        throws Exception
    {

        return doCreateContent( parentPath, displayName, data, extraDatas, ContentTypeName.folder() );
    }

    private Content doCreateContent( final ContentPath parentPath, final String displayName, final PropertyTree data,
                                     final ExtraDatas extraDatas, ContentTypeName type )
    {
        final CreateContentParams.Builder builder = createContentBuilder( parentPath, displayName, data, extraDatas, type );
        return doCreateContent( builder );
    }

    private Content doCreateContent( final CreateContentParams.Builder builder )
    {
        return this.contentService.create( builder.build() );
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

        //Creates a property set
        PropertySet propertySet = new PropertySet();
        propertySet.addString( "setString", "stringValue" );
        propertySet.addDouble( "setDouble", 1.5d );

        //Creates the property tree with value assigned for each attribute
        PropertyTree data = new PropertyTree();
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

    protected void assertOrder( final FindContentByQueryResult result, final Content... expectedOrder )
    {
        final ContentIds contentIds = result.getContents().getIds();

        doAssertOrder( contentIds, expectedOrder );
    }

    protected void assertOrder( final ContentIds contentIds, final Content... expectedOrder )
    {
        doAssertOrder( contentIds, expectedOrder );
    }

    private void doAssertOrder( final ContentIds contentIds, final Content[] expectedOrder )
    {
        assertEquals( expectedOrder.length, contentIds.getSize(), "Expected [" + expectedOrder.length + "] number of hits in result" );

        final Iterator<ContentId> iterator = contentIds.iterator();

        for ( final Content content : expectedOrder )
        {
            assertTrue( iterator.hasNext(), "Expected more content, iterator empty" );
            final ContentId next = iterator.next();
            assertEquals( content.getId(), next,
                    "Expected content with path [" + content.getPath() + "] in this position, found [" +
                            this.contentService.getById( next ).getPath() + "]");
        }
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
        builder.append( "'" );
        builder.append( "--" );
        builder.append( content.getName() );
        // builder.append( " (" + compareStatus.getCompareStatus().toString().toLowerCase() + ")" );

        return builder.toString();
    }

}
