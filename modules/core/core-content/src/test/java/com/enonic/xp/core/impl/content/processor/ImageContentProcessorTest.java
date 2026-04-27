package com.enonic.xp.core.impl.content.processor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

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
import com.enonic.xp.content.Media;
import com.enonic.xp.content.Mixin;
import com.enonic.xp.content.Mixins;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.media.ImageOrientation;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.util.GeoPoint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class ImageContentProcessorTest
{
    private ImageContentProcessor imageContentProcessor;

    private ContentService contentService;

    @BeforeEach
    void setUp()
    {
        this.contentService = Mockito.mock( ContentService.class );
        this.imageContentProcessor = new ImageContentProcessor();
    }

    @Test
    void testSupports()
    {
        assertTrue( imageContentProcessor.supports( ContentTypeName.imageMedia() ) );
        assertFalse( imageContentProcessor.supports( ContentTypeName.media() ) );
    }

    @Test
    void testProcessCreate()
    {
        final CreateAttachments createAttachments = createAttachments();
        final CreateContentParams params = createContentParams( createAttachments );

        final ProcessCreateParams processCreateParams = new ProcessCreateParams( params, MediaInfo.create().build(), ContentIds.empty() );

        final ProcessCreateResult result = this.imageContentProcessor.processCreate( processCreateParams );

        assertEquals( result.getCreateContentParams().getCreateAttachments().first(), createAttachments.first() );
    }

    @Test
    void testProcessCreateWithGeoData()
    {
        final CreateContentParams params = createContentParams( createAttachments() );
        final ProcessCreateParams processCreateParams =
            new ProcessCreateParams( params, MediaInfo.create().addMetadata( "geo:lat", "1" ).addMetadata( "geo:long", "2" ).build(),
                                     ContentIds.empty() );
        final GeoPoint geoPoint = new GeoPoint( 1.0, 2.0 );
        final ProcessCreateResult result = this.imageContentProcessor.processCreate( processCreateParams );
        final Mixin geoMixin = result.getCreateContentParams().getMixins().getByName( MediaInfo.GPS_INFO_METADATA_NAME );
        assertEquals( geoMixin.getData().getGeoPoint( MediaInfo.GPS_INFO_GEO_POINT, 0 ), geoPoint );
    }

    @Test
    void testProcessCreateWithMixins()
    {
        final CreateContentParams params = createContentParams( createAttachments() );
        final ProcessCreateParams processCreateParams = new ProcessCreateParams( params, MediaInfo.create()
            .addMetadata( "exif:ExposureTime", "1" )
            .addMetadata( "geo:alt", "2" )
            .addMetadata( MediaInfo.MEDIA_INFO_BYTE_SIZE, "13" )
            .build(), ContentIds.empty() );
        final ProcessCreateResult result = this.imageContentProcessor.processCreate( processCreateParams );
        final Mixins mixins = result.getCreateContentParams().getMixins();
        assertEquals( "1", mixins.getByName( MediaInfo.CAMERA_INFO_METADATA_NAME ).getData().getString( "shutterTime", 0 ) );
        assertEquals( "2", mixins.getByName( MediaInfo.GPS_INFO_METADATA_NAME ).getData().getString( "altitude", 0 ) );
        assertEquals( 13, mixins.getByName( MediaInfo.IMAGE_INFO_METADATA_NAME ).getData().getLong( MediaInfo.MEDIA_INFO_BYTE_SIZE, 0 ) );
    }

    @Test
    void testProcessCreateWritesEffectiveSize()
    {
        final PropertyTree data = new PropertyTree();
        data.addSet( ContentPropertyNames.MEDIA ).addString( ContentPropertyNames.MEDIA_ATTACHMENT, "MyImage.jpg" );

        final CreateContentParams params = CreateContentParams.create()
            .parent( ContentPath.ROOT )
            .name( "myContent" )
            .contentData( data )
            .type( ContentTypeName.imageMedia() )
            .createAttachments( createAttachments() )
            .build();

        final ProcessCreateParams processCreateParams = new ProcessCreateParams( params,
                                                                                 MediaInfo.create()
                                                                                     .addMetadata( "tiff:ImageWidth", "400" )
                                                                                     .addMetadata( "tiff:ImageLength", "300" )
                                                                                     .build(), ContentIds.empty() );

        final ProcessCreateResult result = this.imageContentProcessor.processCreate( processCreateParams );

        final PropertySet mediaSet = result.getCreateContentParams().getData().getSet( ContentPropertyNames.MEDIA );
        assertEquals( 400L, mediaSet.getLong( ContentPropertyNames.MEDIA_IMAGE_WIDTH ) );
        assertEquals( 300L, mediaSet.getLong( ContentPropertyNames.MEDIA_IMAGE_HEIGHT ) );
    }

    @Test
    void testProcessUpdateNoMediaInfoDoesNotReadBinary()
    {
        final PropertyTree data = new PropertyTree();
        data.addSet( ContentPropertyNames.MEDIA ).addString( ContentPropertyNames.MEDIA_ATTACHMENT, "MyImage.jpg" );

        final PropertyTree imageInfoData = new PropertyTree();
        imageInfoData.setLong( MediaInfo.IMAGE_INFO_IMAGE_WIDTH, 1000L );
        imageInfoData.setLong( MediaInfo.IMAGE_INFO_IMAGE_HEIGHT, 500L );

        final Media content = Media.create()
            .name( "myContentName" )
            .type( ContentTypeName.imageMedia() )
            .parentPath( ContentPath.ROOT )
            .data( data )
            .mixins( Mixins.create().add( new Mixin( MediaInfo.IMAGE_INFO_METADATA_NAME, imageInfoData ) ).build() )
            .attachments( Attachments.from( Attachment.create().mimeType( "image/jpeg" ).name( "MyImage.jpg" ).build() ) )
            .build();

        final ProcessUpdateParams processUpdateParams = ProcessUpdateParams.create().content( content ).build();

        final ProcessUpdateResult result = this.imageContentProcessor.processUpdate( processUpdateParams );

        verify( contentService, never() ).getBinary( any(), any() );

        final PropertySet mediaSet = result.getContent().getData().getSet( ContentPropertyNames.MEDIA );
        assertEquals( 1000L, mediaSet.getLong( ContentPropertyNames.MEDIA_IMAGE_WIDTH ) );
        assertEquals( 500L, mediaSet.getLong( ContentPropertyNames.MEDIA_IMAGE_HEIGHT ) );
    }

    @Test
    void testProcessUpdateCropOnlyRecomputesEffectiveSize()
    {
        final PropertyTree data = new PropertyTree();
        final PropertySet mediaSet = data.addSet( ContentPropertyNames.MEDIA );
        mediaSet.addString( ContentPropertyNames.MEDIA_ATTACHMENT, "MyImage.jpg" );
        final PropertySet cropping = mediaSet.addSet( ContentPropertyNames.MEDIA_CROPPING );
        cropping.addDouble( ContentPropertyNames.MEDIA_CROPPING_TOP, 0.0 );
        cropping.addDouble( ContentPropertyNames.MEDIA_CROPPING_LEFT, 0.0 );
        cropping.addDouble( ContentPropertyNames.MEDIA_CROPPING_BOTTOM, 0.5 );
        cropping.addDouble( ContentPropertyNames.MEDIA_CROPPING_RIGHT, 0.5 );
        cropping.addDouble( "zoom", 1.0 );

        final PropertyTree imageInfoData = new PropertyTree();
        imageInfoData.setLong( MediaInfo.IMAGE_INFO_IMAGE_WIDTH, 1000L );
        imageInfoData.setLong( MediaInfo.IMAGE_INFO_IMAGE_HEIGHT, 800L );

        final Media content = Media.create()
            .name( "myContentName" )
            .type( ContentTypeName.imageMedia() )
            .parentPath( ContentPath.ROOT )
            .data( data )
            .mixins( Mixins.create().add( new Mixin( MediaInfo.IMAGE_INFO_METADATA_NAME, imageInfoData ) ).build() )
            .attachments( Attachments.from( Attachment.create().mimeType( "image/jpeg" ).name( "MyImage.jpg" ).build() ) )
            .build();

        final ProcessUpdateParams processUpdateParams = ProcessUpdateParams.create().content( content ).build();
        final ProcessUpdateResult result = this.imageContentProcessor.processUpdate( processUpdateParams );

        verify( contentService, never() ).getBinary( any(), any() );

        final PropertySet resultMedia = result.getContent().getData().getSet( ContentPropertyNames.MEDIA );
        assertEquals( 500L, resultMedia.getLong( ContentPropertyNames.MEDIA_IMAGE_WIDTH ) );
        assertEquals( 400L, resultMedia.getLong( ContentPropertyNames.MEDIA_IMAGE_HEIGHT ) );

        final Mixin imageInfoMixin = result.getContent().getMixins().getByName( MediaInfo.IMAGE_INFO_METADATA_NAME );
        assertEquals( 1000L, imageInfoMixin.getData().getLong( MediaInfo.IMAGE_INFO_IMAGE_WIDTH ) );
        assertEquals( 800L, imageInfoMixin.getData().getLong( MediaInfo.IMAGE_INFO_IMAGE_HEIGHT ) );
        assertNull( imageInfoMixin.getData().getLong( MediaInfo.IMAGE_INFO_PIXEL_SIZE ) );
    }

    @Test
    void testProcessUpdateOrientationSwapsDimensions()
    {
        final PropertyTree data = new PropertyTree();
        final PropertySet mediaSet = data.addSet( ContentPropertyNames.MEDIA );
        mediaSet.addString( ContentPropertyNames.MEDIA_ATTACHMENT, "MyImage.jpg" );
        mediaSet.addLong( ContentPropertyNames.ORIENTATION, (long) ImageOrientation.RightTop.getValue() );

        final PropertyTree imageInfoData = new PropertyTree();
        imageInfoData.setLong( MediaInfo.IMAGE_INFO_IMAGE_WIDTH, 1000L );
        imageInfoData.setLong( MediaInfo.IMAGE_INFO_IMAGE_HEIGHT, 500L );

        final Media content = Media.create()
            .name( "myContentName" )
            .type( ContentTypeName.imageMedia() )
            .parentPath( ContentPath.ROOT )
            .data( data )
            .mixins( Mixins.create().add( new Mixin( MediaInfo.IMAGE_INFO_METADATA_NAME, imageInfoData ) ).build() )
            .attachments( Attachments.from( Attachment.create().mimeType( "image/jpeg" ).name( "MyImage.jpg" ).build() ) )
            .build();

        final ProcessUpdateParams processUpdateParams = ProcessUpdateParams.create().content( content ).build();
        final ProcessUpdateResult result = this.imageContentProcessor.processUpdate( processUpdateParams );

        verify( contentService, never() ).getBinary( any(), any() );

        final PropertySet resultMedia = result.getContent().getData().getSet( ContentPropertyNames.MEDIA );
        assertEquals( 500L, resultMedia.getLong( ContentPropertyNames.MEDIA_IMAGE_WIDTH ) );
        assertEquals( 1000L, resultMedia.getLong( ContentPropertyNames.MEDIA_IMAGE_HEIGHT ) );
    }

    @Test
    void testProcessUpdateWithMediaInfo()
    {
        final Content content = Content.create().name( "myContentName" ).parentPath( ContentPath.ROOT ).build();
        final ProcessUpdateParams processUpdateParams = ProcessUpdateParams.create()
            .content( content )
            .mediaInfo( MediaInfo.create()
                            .addMetadata( "exif:ExposureTime", "1" )
                            .addMetadata( "geo:alt", "2" )
                            .addMetadata( MediaInfo.MEDIA_INFO_BYTE_SIZE, "13" )
                            .build() )
            .build();

        final ProcessUpdateResult result = this.imageContentProcessor.processUpdate( processUpdateParams );

        final Mixins mixins = result.getContent().getMixins();
        assertEquals( "1", mixins.getByName( MediaInfo.CAMERA_INFO_METADATA_NAME ).getData().getString( "shutterTime", 0 ) );
        assertEquals( "2", mixins.getByName( MediaInfo.GPS_INFO_METADATA_NAME ).getData().getString( "altitude", 0 ) );
        assertEquals( 13, mixins.getByName( MediaInfo.IMAGE_INFO_METADATA_NAME ).getData().getLong( MediaInfo.MEDIA_INFO_BYTE_SIZE, 0 ) );
    }

    @Test
    void testProcessUpdateWithMediaInfoOverwritten()
    {
        final Content content = Content.create().name( "myContentName" ).parentPath( ContentPath.ROOT ).data( new PropertyTree() ).build();
        final ProcessUpdateParams processUpdateParams = ProcessUpdateParams.create()
            .content( content )
            .mediaInfo( MediaInfo.create()
                            .addMetadata( "exif:ExposureTime", "2" )
                            .addMetadata( "shutterTime", "3" )
                            .build() )
            .build();

        final ProcessUpdateResult result = this.imageContentProcessor.processUpdate( processUpdateParams );

        final Mixins mixins = result.getContent().getMixins();
        assertEquals( "2", mixins.getByName( MediaInfo.CAMERA_INFO_METADATA_NAME ).getData().getString( "shutterTime", 0 ) );
    }

    private CreateAttachments createAttachments()
    {
        return CreateAttachments.create()
            .add( CreateAttachment.create()
                      .name( "imageAttach" )
                      .byteSource( ByteSource.wrap( "this is image".getBytes() ) )
                      .text( "This is the image" )
                      .build() )
            .build();
    }

    private CreateContentParams createContentParams( final CreateAttachments createAttachments )
    {
        return CreateContentParams.create()
            .parent( ContentPath.ROOT )
            .name( "myContent" )
            .contentData( new PropertyTree() )
            .type( ContentTypeName.imageMedia() )
            .createAttachments( createAttachments )
            .build();
    }

}
