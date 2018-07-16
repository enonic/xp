package com.enonic.xp.admin.impl.rest.resource.content;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.ByteSource;
import com.google.common.net.HttpHeaders;

import com.enonic.xp.admin.impl.json.aggregation.BucketAggregationJson;
import com.enonic.xp.admin.impl.json.content.ActiveContentVersionEntryJson;
import com.enonic.xp.admin.impl.json.content.CompareContentResultJson;
import com.enonic.xp.admin.impl.json.content.CompareContentResultsJson;
import com.enonic.xp.admin.impl.json.content.ContentIdJson;
import com.enonic.xp.admin.impl.json.content.ContentJson;
import com.enonic.xp.admin.impl.json.content.ContentSummaryJson;
import com.enonic.xp.admin.impl.json.content.ContentSummaryListJson;
import com.enonic.xp.admin.impl.json.content.ContentTreeSelectorListJson;
import com.enonic.xp.admin.impl.json.content.ContentVersionJson;
import com.enonic.xp.admin.impl.json.content.ContentVersionViewJson;
import com.enonic.xp.admin.impl.json.content.ContentsExistByPathJson;
import com.enonic.xp.admin.impl.json.content.ContentsExistJson;
import com.enonic.xp.admin.impl.json.content.GetActiveContentVersionsResultJson;
import com.enonic.xp.admin.impl.json.content.GetContentVersionsForViewResultJson;
import com.enonic.xp.admin.impl.json.content.GetContentVersionsResultJson;
import com.enonic.xp.admin.impl.json.content.attachment.AttachmentJson;
import com.enonic.xp.admin.impl.rest.resource.AdminResourceTestSupport;
import com.enonic.xp.admin.impl.rest.resource.content.json.AbstractContentQueryResultJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.CompareContentsJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.ContentIdsJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.ContentPathsJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.ContentQueryJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.ContentTreeSelectorQueryJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.DeleteContentJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.DuplicateContentsJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.GetContentVersionsJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.GetDependenciesResultJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.GetDescendantsOfContents;
import com.enonic.xp.admin.impl.rest.resource.content.json.HasUnpublishedChildrenResultJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.LocaleJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.LocaleListJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.MoveContentJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.PublishContentJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.ReorderChildrenJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.SetActiveVersionJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.TaskResultJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.UndoPendingDeleteContentJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.UndoPendingDeleteContentResultJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.UnpublishContentJson;
import com.enonic.xp.admin.impl.rest.resource.content.task.DeleteRunnableTask;
import com.enonic.xp.admin.impl.rest.resource.content.task.DuplicateRunnableTask;
import com.enonic.xp.admin.impl.rest.resource.content.task.MoveRunnableTask;
import com.enonic.xp.admin.impl.rest.resource.content.task.PublishRunnableTask;
import com.enonic.xp.admin.impl.rest.resource.content.task.UnpublishRunnableTask;
import com.enonic.xp.aggregation.Aggregation;
import com.enonic.xp.aggregation.Aggregations;
import com.enonic.xp.aggregation.Bucket;
import com.enonic.xp.aggregation.BucketAggregation;
import com.enonic.xp.aggregation.Buckets;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ActiveContentVersionEntry;
import com.enonic.xp.content.ApplyContentPermissionsParams;
import com.enonic.xp.content.CompareContentResult;
import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.CompareContentsParams;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAlreadyExistsException;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentDependencies;
import com.enonic.xp.content.ContentDependenciesAggregation;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPaths;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.ContentVersion;
import com.enonic.xp.content.ContentVersionId;
import com.enonic.xp.content.ContentVersions;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.CreateMediaParams;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.content.FindContentByParentResult;
import com.enonic.xp.content.FindContentIdsByParentResult;
import com.enonic.xp.content.FindContentIdsByQueryResult;
import com.enonic.xp.content.FindContentVersionsParams;
import com.enonic.xp.content.FindContentVersionsResult;
import com.enonic.xp.content.GetActiveContentVersionsParams;
import com.enonic.xp.content.GetActiveContentVersionsResult;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.content.GetPublishStatusResult;
import com.enonic.xp.content.GetPublishStatusesParams;
import com.enonic.xp.content.GetPublishStatusesResult;
import com.enonic.xp.content.HasUnpublishedChildrenParams;
import com.enonic.xp.content.Media;
import com.enonic.xp.content.PublishStatus;
import com.enonic.xp.content.RenameContentParams;
import com.enonic.xp.content.ReorderChildContentsParams;
import com.enonic.xp.content.ReorderChildContentsResult;
import com.enonic.xp.content.ReorderChildParams;
import com.enonic.xp.content.ResolvePublishDependenciesParams;
import com.enonic.xp.content.ResolveRequiredDependenciesParams;
import com.enonic.xp.content.SetActiveContentVersionResult;
import com.enonic.xp.content.SetContentChildOrderParams;
import com.enonic.xp.content.UndoPendingDeleteContentParams;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.UpdateMediaParams;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.LocalScope;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.extractor.BinaryExtractor;
import com.enonic.xp.extractor.ExtractedData;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.icon.Thumbnail;
import com.enonic.xp.image.ImageService;
import com.enonic.xp.image.ReadImageParams;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.jaxrs.impl.MockRestResponse;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.media.MediaInfoService;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageRegions;
import com.enonic.xp.page.PageTemplateKey;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.Region;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.relationship.RelationshipTypeService;
import com.enonic.xp.security.Principal;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalQuery;
import com.enonic.xp.security.PrincipalQueryResult;
import com.enonic.xp.security.PrincipalRelationship;
import com.enonic.xp.security.PrincipalRelationships;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.session.SessionKey;
import com.enonic.xp.session.SimpleSession;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.util.BinaryReferences;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.multipart.MultipartForm;
import com.enonic.xp.web.multipart.MultipartItem;

import static com.enonic.xp.media.MediaInfo.IMAGE_INFO_IMAGE_HEIGHT;
import static com.enonic.xp.media.MediaInfo.IMAGE_INFO_IMAGE_WIDTH;
import static com.enonic.xp.media.MediaInfo.IMAGE_INFO_PIXEL_SIZE;
import static com.enonic.xp.media.MediaInfo.MEDIA_INFO_BYTE_SIZE;
import static com.enonic.xp.security.acl.Permission.CREATE;
import static com.enonic.xp.security.acl.Permission.DELETE;
import static com.enonic.xp.security.acl.Permission.MODIFY;
import static com.enonic.xp.security.acl.Permission.READ;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;

public class ContentIconResourceTest
    extends AdminResourceTestSupport
{
    private ContentService contentService;

    private ImageService imageService;

    @Rule
    public ExpectedException ex = ExpectedException.none();

    @Override
    protected ContentIconResource getResourceInstance()
    {
        ContentIconResource resource = new ContentIconResource();

        contentService = Mockito.mock( ContentService.class );
        imageService = Mockito.mock( ImageService.class );

        MediaInfoService mediaInfoService = Mockito.mock( MediaInfoService.class );

        resource.setContentService( contentService );
        resource.setImageService( imageService );
        resource.setMediaInfoService( mediaInfoService );

        return resource;
    }

    @Test
    public void get_from_thumbnail()
        throws Exception
    {
        Thumbnail thumbnail = Thumbnail.from( BinaryReference.from( "thumbnail.png" ), "image/png", 128 );

        Content content = createContent( "content-id", thumbnail, "my-content-type" );

        Mockito.when( contentService.getById( content.getId() ) ).thenReturn( content );
        Mockito.when( imageService.getFormatByMimeType( eq( "image/png" ) ) ).thenReturn( "format" );

        ByteSource byteSource = ByteSource.wrap( new byte[]{1, 2, 3} );
        Mockito.when( imageService.readImage( Mockito.isA( ReadImageParams.class ) ) ).thenReturn( byteSource );


        MockRestResponse result = request().path( "content/icon/content-id" ).
            queryParam( "contentId", "content-id" ).
            queryParam( "ts", "2" ).get();

        final CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge( Integer.MAX_VALUE );

        assertTrue( Arrays.equals( byteSource.read(), result.getData() ) );
        assertEquals( cacheControl.toString(), result.getHeader( "Cache-Control" ) );

        result = request().path( "content/icon/content-id" ).
            queryParam( "contentId", "content-id" ).get();

        assertEquals( null, result.getHeader( "Cache-Control" ) );
    }

    @Test
    public void get_empty_thumbnail_for_a_media()
        throws Exception
    {
        Content content = createContent( "content-id", ContentTypeName.imageMedia().toString() );

        Mockito.when( contentService.getById( content.getId() ) ).thenReturn( content );
        Mockito.when( imageService.getFormatByMimeType( eq( "image/png" ) ) ).thenReturn( "format" );

        ByteSource byteSource = ByteSource.wrap( new byte[]{1, 2, 3} );
        Mockito.when( imageService.readImage( Mockito.isA( ReadImageParams.class ) ) ).thenReturn( byteSource );

        MockRestResponse result = request().path( "content/icon/content-id" ).
            queryParam( "contentId", "content-id" ).
            queryParam( "ts", "2020327" ).get();

        assertTrue( Arrays.equals( byteSource.read(), result.getData() ) );
        assertEquals( "image/png", result.getHeader( "Content-Type" ) );
    }

    @Test
    public void get_empty_thumbnail_for_a_svg()
        throws Exception
    {
        Content content = createContent( "content-id", null, ContentTypeName.vectorMedia(), "image/svg+xml" );

        Mockito.when( contentService.getById( content.getId() ) ).thenReturn( content );

        ByteSource byteSource = ByteSource.wrap( new byte[]{1, 2, 3} );
        Mockito.when( contentService.getBinary( content.getId(), content.getAttachments().get( 0 ).getBinaryReference() ) ).thenReturn(
            byteSource );

        MockRestResponse result = request().path( "content/icon/content-id" ).
            queryParam( "contentId", "content-id" ).
            queryParam( "ts", "2020327" ).get();

        assertTrue( Arrays.equals( byteSource.read(), result.getData() ) );
        assertEquals( "image/svg+xml", result.getHeader( "Content-Type" ) );
    }

    @Test
    public void get_empty_thumbnail_for_not_a_media()
        throws Exception
    {
        Content content = createContent( "content-id" );

        Mockito.when( contentService.getById( content.getId() ) ).thenReturn( content );

        MockRestResponse result = request().path( "content/icon/content-id" ).
            queryParam( "contentId", "content-id" ).
            queryParam( "ts", "2020327" ).get();

        assertEquals( HttpStatus.NOT_FOUND.value(), result.getStatus() );
    }

    @Test
    public void get_content_not_found()
        throws Exception
    {
        MockRestResponse result = request().path( "content/icon/content-id" ).
            queryParam( "contentId", "content-id" ).
            queryParam( "ts", "2020327" ).get();

        assertEquals( HttpStatus.NOT_FOUND.value(), result.getStatus() );

    }

    @Test
    public void read_image_error()
        throws Exception
    {
        Thumbnail thumbnail = Thumbnail.from( BinaryReference.from( "thumbnail.png" ), "image/png", 128 );

        Content content = createContent( "content-id", thumbnail, "my-content-type" );

        Mockito.when( contentService.getById( content.getId() ) ).thenReturn( content );
        Mockito.when( imageService.getFormatByMimeType( eq( "image/png" ) ) ).thenReturn( "format" );

        Mockito.when( imageService.readImage( Mockito.isA( ReadImageParams.class ) ) ).thenThrow( new IOException( "io error message" ) );

        ex.expect( IOException.class );
        ex.expectMessage( "io error message" );

        request().path( "content/icon/content-id" ).
            queryParam( "contentId", "content-id" ).
            queryParam( "ts", "2" ).get();
    }

    @Test
    public void get_empty_image_media()
        throws Exception
    {
        Content content = createContent( "content-id", ContentTypeName.imageMedia().toString() );
        content = Content.create( content ).attachments( Attachments.create().build() ).build();

        Mockito.when( contentService.getById( content.getId() ) ).thenReturn( content );

        MockRestResponse result = request().path( "content/icon/content-id" ).
            queryParam( "contentId", "content-id" ).
            queryParam( "ts", "2020327" ).get();

        assertEquals( HttpStatus.NOT_FOUND.value(), result.getStatus() );
    }

    private Content createContent( final String id, final Thumbnail thumbnail, final String contentTypeName )
    {
        return this.createContent( id, thumbnail, ContentTypeName.from( contentTypeName ), "image/png" );
    }

    private Content createContent( final String id, final String contentTypeName )
    {
        return this.createContent( id, null, contentTypeName );
    }


    private Content createContent( final String id )
    {
        return this.createContent( id, "my-content-type" );
    }

    private Media.Builder createMediaBuilder( final String attachmentType )
    {
        Media.Builder result = Media.create();

        final Attachment attachment = Attachment.create().
            name( "logo.png" ).
            mimeType( attachmentType ).
            label( "small" ).
            build();
        final PropertyTree data = new PropertyTree();
        data.addString( "media", attachment.getName() );

        final PropertyTree mediaData = new PropertyTree();
        mediaData.setLong( IMAGE_INFO_PIXEL_SIZE, 300L * 200L );
        mediaData.setLong( IMAGE_INFO_IMAGE_HEIGHT, 200L );
        mediaData.setLong( IMAGE_INFO_IMAGE_WIDTH, 300L );
        mediaData.setLong( MEDIA_INFO_BYTE_SIZE, 100000L );

        final ExtraData mediaExtraData = new ExtraData( MediaInfo.IMAGE_INFO_METADATA_NAME, mediaData );

        return result.attachments( Attachments.from( attachment ) ).
            data( data ).
            addExtraData( mediaExtraData );
    }

    private Content createContent( final String id, final Thumbnail thumbnail, final ContentTypeName contentType,
                                   final String attachmentType )
    {
        final PropertyTree metadata = new PropertyTree();
        metadata.setLong( "myProperty", 1L );

        Content.Builder builder =
            contentType.isMedia() || contentType.isDescendantOfMedia() ? createMediaBuilder( attachmentType ) : Content.create();

        return builder.
            id( ContentId.from( id ) ).
            parentPath( ContentPath.ROOT ).
            name( "content-name" ).
            valid( true ).
            createdTime( Instant.now() ).
            creator( PrincipalKey.from( "user:system:admin" ) ).
            owner( PrincipalKey.from( "user:myStore:me" ) ).
            language( Locale.ENGLISH ).
            displayName( "My Content" ).
            modifiedTime( Instant.now() ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            type( contentType ).
            addExtraData( new ExtraData( MixinName.from( "myApplication:myField" ), metadata ) ).
            publishInfo( ContentPublishInfo.create().
                from( Instant.parse( "2016-11-02T10:36:00Z" ) ).
                to( Instant.parse( "2016-11-22T10:36:00Z" ) ).
                first( Instant.parse( "2016-11-02T10:36:00Z" ) ).
                build() ).
            thumbnail( thumbnail ).
            build();
    }

}