package com.enonic.xp.core.impl.content.processor;

import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.Media;
import com.enonic.xp.core.impl.schema.xdata.BuiltinXDataTypesAccessor;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.xdata.XDataService;
import com.enonic.xp.util.GeoPoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ImageContentProcessorTest
{
    private ImageContentProcessor imageContentProcessor;

    private ContentService contentService;

    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.contentService = Mockito.mock( ContentService.class );
        final XDataService xDataService = Mockito.mock( XDataService.class );
        when( xDataService.getByNames( any() ) ).thenReturn( BuiltinXDataTypesAccessor.getAll() );
        this.imageContentProcessor = new ImageContentProcessor( contentService, xDataService );
    }

    @Test
    public void testSupports()
    {
        assertTrue( imageContentProcessor.supports( ContentTypeName.imageMedia() ) );
        assertFalse( imageContentProcessor.supports( ContentTypeName.media() ) );
    }

    @Test
    public void testProcessCreate()
    {
        final CreateAttachments createAttachments = createAttachments();
        final CreateContentParams params = createContentParams( createAttachments );

        final ProcessCreateParams processCreateParams = new ProcessCreateParams( params, MediaInfo.create().
            build(), ContentIds.empty() );

        final ProcessCreateResult result = this.imageContentProcessor.processCreate( processCreateParams );

        assertEquals( result.getCreateContentParams().getCreateAttachments().first(), createAttachments.first() );
    }

    @Test
    public void testProcessCreateWithGeoData()
    {
        final CreateContentParams params = createContentParams( createAttachments() );
        final ProcessCreateParams processCreateParams = new ProcessCreateParams( params, MediaInfo.create().
            addMetadata( "geo lat", "1" ).addMetadata( "geo long", "2" ).build(), ContentIds.empty() );
        final GeoPoint geoPoint = new GeoPoint( 1.0, 2.0 );
        final ProcessCreateResult result = this.imageContentProcessor.processCreate( processCreateParams );
        final ExtraData geoExtraData = result.getCreateContentParams().getExtraDatas().getMetadata( MediaInfo.GPS_INFO_METADATA_NAME );
        assertEquals( geoExtraData.getData().getGeoPoint( MediaInfo.GPS_INFO_GEO_POINT, 0 ), geoPoint );
    }

    @Test
    public void testProcessCreateWithExtraData()
    {
        final CreateContentParams params = createContentParams( createAttachments() );
        final ProcessCreateParams processCreateParams = new ProcessCreateParams( params, MediaInfo.create().
            addMetadata( "exposure time", "1" ).addMetadata( "gps altitude ", "2" ).addMetadata( "bytesize", "13" ).build(), ContentIds.empty() );
        final ProcessCreateResult result = this.imageContentProcessor.processCreate( processCreateParams );
        final ExtraDatas extraDatas = result.getCreateContentParams().getExtraDatas();
        assertEquals( "1", extraDatas.getMetadata( MediaInfo.CAMERA_INFO_METADATA_NAME ).getData().getString( "shutterTime", 0 ) );
        assertEquals( "2", extraDatas.getMetadata( MediaInfo.GPS_INFO_METADATA_NAME ).getData().getString( "altitude", 0 ) );
        assertEquals( 13, extraDatas.getMetadata( MediaInfo.IMAGE_INFO_METADATA_NAME ).getData().getLong( MediaInfo.MEDIA_INFO_BYTE_SIZE, 0 ) );
    }

    @Test
    public void testProcessUpdate()
        throws IOException
    {
        when( contentService.getBinary( Mockito.any(), Mockito.any() ) ).thenReturn( this.loadImage( "cat-small.jpg" ) );

        final PropertyTree data = new PropertyTree();
        data.addProperty( ContentPropertyNames.MEDIA, ValueFactory.newString( "MyImage.jpg" ) );

        final Media content = Media.create()
            .name( "myContentName" )
            .type( ContentTypeName.imageMedia() )
            .parentPath( ContentPath.ROOT )
            .data( data )
            .extraDatas( ExtraDatas.create().add( new ExtraData( MediaInfo.IMAGE_INFO_METADATA_NAME, new PropertyTree() ) ).build() )
            .attachments( Attachments.from( Attachment.create().mimeType( "image/jpeg" ).name( "MyImage.jpg" ).build() ) )
            .build();

        final ProcessUpdateParams processUpdateParams = ProcessUpdateParams.create().content( content ).build();

        final ProcessUpdateResult result = this.imageContentProcessor.processUpdate( processUpdateParams );

        final ExtraData extraData = result.getContent().getAllExtraData().getMetadata( MediaInfo.IMAGE_INFO_METADATA_NAME );
        assertNotNull( extraData.getData().getLong( "pixelSize", 0 ) );
        assertNotNull( extraData.getData().getLong( "imageHeight", 0 ) );
        assertNotNull( extraData.getData().getLong( "imageWidth", 0 ) );
        assertNotNull( extraData.getData().getLong( "byteSize", 0 ) );
    }

    @Test
    public void testProcessUpdateWithCorruptedImage()
        throws IOException
    {
        ByteSource byteSource = Mockito.mock( ByteSource.class );
        when( byteSource.openStream() ).thenThrow( new IOException() );
        when( contentService.getBinary( Mockito.any(), Mockito.any() ) ).thenReturn( byteSource );

        final PropertyTree data = new PropertyTree();
        data.addProperty( ContentPropertyNames.MEDIA, ValueFactory.newString( "CorruptedImage.jpg" ) );
        final Media content = Media.create()
            .name( "myContentName" )
            .type( ContentTypeName.imageMedia() )
            .parentPath( ContentPath.ROOT )
            .data( data )
            .extraDatas( ExtraDatas.create().add( new ExtraData( MediaInfo.IMAGE_INFO_METADATA_NAME, new PropertyTree() ) ).build() )
            .attachments( Attachments.from( Attachment.create().mimeType( "image/jpeg" ).name( "CorruptedImage.jpg" ).build() ) )
            .build();

        final ProcessUpdateParams processUpdateParams = ProcessUpdateParams.create().content( content ).build();

        final ProcessUpdateResult result = this.imageContentProcessor.processUpdate( processUpdateParams );

        assertThat(result.getContent().getAllExtraData()).map( ExtraData::getName ).containsExactly( MediaInfo.IMAGE_INFO_METADATA_NAME );
    }

    @Test
    public void testProcessUpdateWithMediaInfo()
    {
        final Content content = Content.create().name( "myContentName" ).parentPath( ContentPath.ROOT ).build();
        final ProcessUpdateParams processUpdateParams = ProcessUpdateParams.create().content( content ).
            mediaInfo( MediaInfo.create().
                addMetadata( "exposure time", "1" ).addMetadata( "gps altitude ", "2" ).addMetadata( "bytesize", "13" ).build() ).
            build();

        final ProcessUpdateResult result = this.imageContentProcessor.processUpdate( processUpdateParams );

        final ExtraDatas extraDatas = result.getContent().getAllExtraData();
        assertEquals( "1", extraDatas.getMetadata( MediaInfo.CAMERA_INFO_METADATA_NAME ).getData().getString( "shutterTime", 0 ) );
        assertEquals( "2", extraDatas.getMetadata( MediaInfo.GPS_INFO_METADATA_NAME ).getData().getString( "altitude", 0 ) );
        assertEquals( 13, extraDatas.getMetadata( MediaInfo.IMAGE_INFO_METADATA_NAME ).getData().getLong( MediaInfo.MEDIA_INFO_BYTE_SIZE, 0 ) );
    }

    @Test
    public void testProcessUpdateWithMediaInfoOverwritten()
    {
        final Content content = Content.create().name( "myContentName" ).parentPath( ContentPath.ROOT ).data( new PropertyTree() ).build();
        final ProcessUpdateParams processUpdateParams = ProcessUpdateParams.create().content( content )
            .mediaInfo( MediaInfo.create()
                            .addMetadata( "exposure time", "1" )
                            .addMetadata( "exif Subifd Exposure Time", "2" )
                            .addMetadata( "shutter Time", "3" )
                            .build() )
            .build();

        final ProcessUpdateResult result = this.imageContentProcessor.processUpdate( processUpdateParams );

        final ExtraDatas extraDatas = result.getContent().getAllExtraData();
        assertEquals( "2", extraDatas.getMetadata( MediaInfo.CAMERA_INFO_METADATA_NAME ).getData().getString( "shutterTime", 0 ) );
    }

    private CreateAttachments createAttachments()
    {
        return CreateAttachments.create().
            add( CreateAttachment.create().
                name( "imageAttach" ).
                byteSource( ByteSource.wrap( "this is image".getBytes() ) ).
                text( "This is the image" ).
                build() ).
            build();
    }

    private CreateContentParams createContentParams( final CreateAttachments createAttachments )
    {
        return CreateContentParams.create().
            parent( ContentPath.ROOT ).
            name( "myContent" ).
            contentData( new PropertyTree() ).
            type( ContentTypeName.imageMedia() ).
            createAttachments( createAttachments ).
            build();
    }

    protected ByteSource loadImage( final String name )
        throws IOException
    {
        final InputStream imageStream = this.getClass().getResourceAsStream( name );

        return ByteSource.wrap( ByteStreams.toByteArray( imageStream ) );
    }
}
