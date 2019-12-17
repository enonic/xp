package com.enonic.xp.admin.impl.rest.resource.content;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Locale;

import javax.ws.rs.core.CacheControl;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.xp.admin.impl.rest.resource.AdminResourceTestSupport;
import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.Media;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.icon.Thumbnail;
import com.enonic.xp.image.ImageService;
import com.enonic.xp.image.ReadImageParams;
import com.enonic.xp.jaxrs.impl.MockRestResponse;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.media.MediaInfoService;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.web.HttpStatus;

import static com.enonic.xp.media.MediaInfo.IMAGE_INFO_IMAGE_HEIGHT;
import static com.enonic.xp.media.MediaInfo.IMAGE_INFO_IMAGE_WIDTH;
import static com.enonic.xp.media.MediaInfo.IMAGE_INFO_PIXEL_SIZE;
import static com.enonic.xp.media.MediaInfo.MEDIA_INFO_BYTE_SIZE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;

public class ContentIconResourceTest
    extends AdminResourceTestSupport
{
    private ContentService contentService;

    private ImageService imageService;

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

        final IOException ex = assertThrows(IOException.class, () -> {
            request().path("content/icon/content-id").
                    queryParam("contentId", "content-id").
                    queryParam("ts", "2").get();
        });
        assertEquals( "io error message", ex.getMessage());
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
            addExtraData( new ExtraData( XDataName.from( "myApplication:myField" ), metadata ) ).
            publishInfo( ContentPublishInfo.create().
                from( Instant.parse( "2016-11-02T10:36:00Z" ) ).
                to( Instant.parse( "2016-11-22T10:36:00Z" ) ).
                first( Instant.parse( "2016-11-02T10:36:00Z" ) ).
                build() ).
            thumbnail( thumbnail ).
            build();
    }

}
