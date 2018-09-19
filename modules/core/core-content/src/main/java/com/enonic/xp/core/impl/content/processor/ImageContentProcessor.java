package com.enonic.xp.core.impl.content.processor;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.ContentEditor;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.EditableContent;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.Media;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.image.Cropping;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.xdata.XDataService;
import com.enonic.xp.util.Exceptions;

import static com.enonic.xp.media.MediaInfo.IMAGE_INFO_IMAGE_HEIGHT;
import static com.enonic.xp.media.MediaInfo.IMAGE_INFO_IMAGE_WIDTH;
import static com.enonic.xp.media.MediaInfo.IMAGE_INFO_PIXEL_SIZE;
import static com.enonic.xp.media.MediaInfo.MEDIA_INFO_BYTE_SIZE;

@Component
public final class ImageContentProcessor
    implements ContentProcessor
{
    private ContentService contentService;

    protected ContentTypeService contentTypeService;

    protected XDataService xDataService;

    @Override
    public boolean supports( final ContentType contentType )
    {
        return contentType.getName().isImageMedia();
    }

    @Override
    public ProcessCreateResult processCreate( final ProcessCreateParams params )
    {
        final CreateContentParams createContentParams = params.getCreateContentParams();

        final CreateAttachments originalAttachments = createContentParams.getCreateAttachments();
        Preconditions.checkArgument( originalAttachments.getSize() == 1, "Expected only one attachment" );

        final CreateAttachment sourceAttachment = originalAttachments.first();

        final CreateAttachments.Builder builder = CreateAttachments.create();
        builder.add( sourceAttachment );

        return new ProcessCreateResult( CreateContentParams.create( createContentParams ).
            createAttachments( builder.build() ).
            build() );
    }

    @Override
    public ProcessUpdateResult processUpdate( final ProcessUpdateParams params )
    {
        final CreateAttachments createAttachments = params.getCreateAttachments();
        final MediaInfo mediaInfo = params.getMediaInfo();

        final ContentEditor editor;
        if ( mediaInfo != null )
        {
            editor = editable -> {

            };
        }
        else
        {
            editor = editable -> {

                if ( !params.getContentType().getName().isDescendantOfMedia() )
                {
                    return;
                }

                editable.extraDatas = updateImageMetadata( editable );

            };
        }
        return new ProcessUpdateResult( createAttachments, editor );

    }

    private ExtraDatas updateImageMetadata( final EditableContent editable )
    {
        final Media media = (Media) editable.source;
        final Attachment mediaAttachment = media.getMediaAttachment();
        if ( mediaAttachment == null )
        {
            return editable.extraDatas;
        }

        final ByteSource binary = contentService.getBinary( editable.source.getId(), mediaAttachment.getBinaryReference() );
        if ( binary == null )
        {
            return editable.extraDatas;
        }

        final BufferedImage image = toBufferedImage( binary );
        final Cropping cropping = media.getCropping();
        final long byteSize = mediaAttachment.getSize();

        final long imageWidth;
        final long imageHeight;
        final long imageSize;
        if ( cropping == null || cropping.isUnmodified() )
        {
            imageWidth = image.getWidth();
            imageHeight = image.getHeight();
            imageSize = imageWidth * imageHeight;
        }
        else
        {
            final BufferedImage croppedImage = cropImage( image, cropping );
            imageWidth = croppedImage.getWidth();
            imageHeight = croppedImage.getHeight();
            imageSize = imageWidth * imageHeight;
        }

        ExtraData extraData = editable.extraDatas.getMetadata( MediaInfo.IMAGE_INFO_METADATA_NAME );
        if ( extraData != null )
        {
            final PropertyTree xData = extraData.getData();
            setLongProperty( xData, IMAGE_INFO_PIXEL_SIZE, imageSize );
            setLongProperty( xData, IMAGE_INFO_IMAGE_HEIGHT, imageHeight );
            setLongProperty( xData, IMAGE_INFO_IMAGE_WIDTH, imageWidth );
            setLongProperty( xData, MEDIA_INFO_BYTE_SIZE, byteSize );
        }

        return editable.extraDatas;
    }

    private void setLongProperty( final PropertyTree propertyTree, final String path, final Long value )
    {
        final Property existingProperty = propertyTree.getProperty( path );
        if ( existingProperty != null && !existingProperty.getType().equals( ValueTypes.LONG ) )
        {
            propertyTree.removeProperty( path );
        }
        propertyTree.setLong( path, value );
    }

    private BufferedImage toBufferedImage( final ByteSource source )
    {
        try (InputStream stream = source.openStream())
        {
            return ImageIO.read( stream );
        }
        catch ( IOException e )
        {
            throw Exceptions.newRuntime( "Failed to read BufferedImage from InputStream" ).withCause( e );
        }
    }

    private BufferedImage cropImage( final BufferedImage image, final Cropping cropping )
    {
        final double width = image.getWidth();
        final double height = image.getHeight();
        return image.getSubimage( (int) ( width * cropping.left() ), (int) ( height * cropping.top() ), (int) ( width * cropping.width() ),
                                  (int) ( height * cropping.height() ) );
    }

    @Reference
    public void setXDataService( final XDataService xDataService )
    {
        this.xDataService = xDataService;
    }

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    @Reference
    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }

}
