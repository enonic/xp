package com.enonic.xp.repo.impl.dump.upgrade.model8to9;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TIFF;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.image.Cropping;
import com.enonic.xp.media.ImageOrientation;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.dump.reader.BlobStoreAccess;
import com.enonic.xp.repo.impl.dump.upgrade.NodeVersionUpgrader;
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositorySegmentUtils;
import com.enonic.xp.util.BinaryReference;

public class ImageUpgrader
    implements NodeVersionUpgrader
{
    private static final Logger LOG = LoggerFactory.getLogger( ImageUpgrader.class );

    private static final String IMAGE_CONTENT_TYPE = "media:image";

    private static final String MEDIA_APPLICATION_PREFIX = ApplicationKey.MEDIA_MOD.toString();

    private final BlobStoreAccess blobStoreAccess;

    public ImageUpgrader( final BlobStoreAccess blobStoreAccess )
    {
        this.blobStoreAccess = blobStoreAccess;
    }

    @Override
    public NodeStoreVersion upgradeNodeVersion( final RepositoryId repositoryId, final NodeStoreVersion nodeVersion )
    {
        if ( !repositoryId.toString().startsWith( ProjectConstants.PROJECT_REPO_ID_PREFIX ) )
        {
            return null;
        }
        if ( !ContentConstants.CONTENT_NODE_COLLECTION.equals( nodeVersion.nodeType() ) )
        {
            return null;
        }

        final PropertyTree data = nodeVersion.data();
        if ( !IMAGE_CONTENT_TYPE.equals( data.getString( ContentPropertyNames.TYPE ) ) )
        {
            return null;
        }

        final PropertySet mediaSet = ensureMediaSet( data );
        if ( mediaSet == null )
        {
            return null;
        }

        boolean changed = normalizeCropPositionZoom( mediaSet, nodeVersion.id().toString() );

        final ImageMetadata metadata = loadImageMetadata( repositoryId, nodeVersion, data );

        if ( metadata != null && metadata.width != null && metadata.height != null )
        {
            writeOriginalDimensions( data, metadata.width, metadata.height );
            changed = true;
        }

        if ( metadata != null && metadata.orientation != null )
        {
            try
            {
                final long exifOrientation = (long) ImageOrientation.valueOf( Integer.parseInt( metadata.orientation ) ).getValue();
                writeOriginalOrientation( data, exifOrientation );
                changed = true;
            }
            catch ( NumberFormatException e )
            {
                LOG.debug( "Failed to parse image orientation value: {}", metadata.orientation );
            }
        }

        changed |= normalizeExistingStringOrientation( mediaSet );

        if ( metadata != null && mediaSet.getProperty( ContentPropertyNames.ORIENTATION ) == null && metadata.orientation != null )
        {
            try
            {
                mediaSet.setLong( ContentPropertyNames.ORIENTATION,
                                  (long) ImageOrientation.valueOf( Integer.parseInt( metadata.orientation ) ).getValue() );
                changed = true;
            }
            catch ( NumberFormatException e )
            {
                LOG.debug( "Failed to parse image orientation value: {}", metadata.orientation );
            }
        }

        final PropertySet imageInfo = getImageInfoSet( data );
        final Long origWidth = readOriginal( imageInfo, MediaInfo.IMAGE_INFO_IMAGE_WIDTH );
        final Long origHeight = readOriginal( imageInfo, MediaInfo.IMAGE_INFO_IMAGE_HEIGHT );
        if ( origWidth != null && origHeight != null )
        {
            final Integer orientationValue = mediaSet.getInteger( ContentPropertyNames.ORIENTATION );
            final ImageOrientation orientation = orientationValue == null ? null : ImageOrientation.valueOf( orientationValue );
            final Cropping cropping = readCropping( mediaSet );
            long width = origWidth;
            long height = origHeight;
            if ( swapsDimensions( orientation ) )
            {
                final long tmp = width;
                width = height;
                height = tmp;
            }
            if ( cropping != null && !cropping.isUnmodified() )
            {
                width = (long) ( width * ( cropping.right() - cropping.left() ) );
                height = (long) ( height * ( cropping.bottom() - cropping.top() ) );
            }
            mediaSet.setLong( ContentPropertyNames.MEDIA_IMAGE_WIDTH, width );
            mediaSet.setLong( ContentPropertyNames.MEDIA_IMAGE_HEIGHT, height );
            changed = true;
        }

        if ( !changed )
        {
            return null;
        }

        return finishUpgrade( repositoryId, nodeVersion );
    }

    private NodeStoreVersion finishUpgrade( final RepositoryId repositoryId, final NodeStoreVersion nodeVersion )
    {
        LOG.info( "Upgraded image metadata for node [{}] in repository [{}]", nodeVersion.id(), repositoryId );
        return nodeVersion;
    }

    private ImageMetadata loadImageMetadata( final RepositoryId repositoryId, final NodeStoreVersion nodeVersion, final PropertyTree data )
    {
        final String attachmentName = resolveAttachmentName( data );
        if ( attachmentName == null )
        {
            return null;
        }
        final BinaryReference binaryRef = findAttachmentBinaryRef( data, attachmentName );
        if ( binaryRef == null )
        {
            return null;
        }
        final AttachedBinary attachedBinary = nodeVersion.attachedBinaries().getByBinaryReference( binaryRef );
        if ( attachedBinary == null )
        {
            return null;
        }
        final Segment binarySegment = RepositorySegmentUtils.toSegment( repositoryId, NodeConstants.BINARY_SEGMENT_LEVEL );
        final BlobRecord record = blobStoreAccess.getRecord( binarySegment, BlobKey.from( attachedBinary.getBlobKey() ) );
        if ( record == null )
        {
            return null;
        }
        return extractMetadata( record, nodeVersion.id().toString() );
    }

    private static boolean swapsDimensions( final ImageOrientation orientation )
    {
        if ( orientation == null )
        {
            return false;
        }
        return switch ( orientation )
        {
            case LeftTop, RightTop, RightBottom, LeftBottom -> true;
            default -> false;
        };
    }

    private static boolean normalizeExistingStringOrientation( final PropertySet mediaSet )
    {
        final Property existing = mediaSet.getProperty( ContentPropertyNames.ORIENTATION );
        if ( existing == null || !ValueTypes.STRING.equals( existing.getType() ) )
        {
            return false;
        }
        final long orientationValue;
        try
        {
            orientationValue = (long) ImageOrientation.valueOf( Integer.parseInt( existing.getString() ) ).getValue();
        }
        catch ( Exception e )
        {
            LOG.warn( "Failed to parse process orientation value: {}", existing, e );
            return false;
        }
        mediaSet.removeProperties( ContentPropertyNames.ORIENTATION );
        mediaSet.setLong( ContentPropertyNames.ORIENTATION, orientationValue );
        return true;
    }

    private static PropertySet ensureMediaSet( final PropertyTree data )
    {
        final Property mediaProperty = data.getProperty( ContentPropertyNames.MEDIA );
        if ( mediaProperty == null )
        {
            return null;
        }
        if ( ValueTypes.STRING.equals( mediaProperty.getType() ) )
        {
            final String attachmentName = mediaProperty.getString();
            data.removeProperties( ContentPropertyNames.MEDIA );
            final PropertySet mediaSet = data.addSet( ContentPropertyNames.MEDIA );
            mediaSet.addString( ContentPropertyNames.MEDIA_ATTACHMENT, attachmentName );
            return mediaSet;
        }
        if ( ValueTypes.PROPERTY_SET.equals( mediaProperty.getType() ) )
        {
            return mediaProperty.getSet();
        }
        return null;
    }

    private static void writeOriginalDimensions( final PropertyTree data, final long width, final long height )
    {
        final PropertySet imageInfoSet = ensureImageInfoSet( data );
        imageInfoSet.removeProperties( MediaInfo.IMAGE_INFO_IMAGE_WIDTH );
        imageInfoSet.removeProperties( MediaInfo.IMAGE_INFO_IMAGE_HEIGHT );
        imageInfoSet.removeProperties( MediaInfo.IMAGE_INFO_PIXEL_SIZE );
        imageInfoSet.setLong( MediaInfo.IMAGE_INFO_IMAGE_WIDTH, width );
        imageInfoSet.setLong( MediaInfo.IMAGE_INFO_IMAGE_HEIGHT, height );
        imageInfoSet.setLong( MediaInfo.IMAGE_INFO_PIXEL_SIZE, width * height );
    }

    private static void writeOriginalOrientation( final PropertyTree data, final long orientation )
    {
        final PropertySet imageInfoSet = ensureImageInfoSet( data );
        imageInfoSet.removeProperties( MediaInfo.IMAGE_INFO_ORIENTATION );
        imageInfoSet.setLong( MediaInfo.IMAGE_INFO_ORIENTATION, orientation );
    }

    private static PropertySet ensureImageInfoSet( final PropertyTree data )
    {
        PropertySet mixins = data.getSet( ContentPropertyNames.MIXINS );
        if ( mixins == null )
        {
            mixins = data.addSet( ContentPropertyNames.MIXINS );
        }
        PropertySet mediaApp = mixins.getSet( MEDIA_APPLICATION_PREFIX );
        if ( mediaApp == null )
        {
            mediaApp = mixins.addSet( MEDIA_APPLICATION_PREFIX );
        }
        PropertySet imageInfo = mediaApp.getSet( MediaInfo.IMAGE_INFO );
        if ( imageInfo == null )
        {
            imageInfo = mediaApp.addSet( MediaInfo.IMAGE_INFO );
        }
        return imageInfo;
    }

    private static Long readOriginal( final PropertySet set, final String propertyName )
    {
        return set == null ? null : set.getLong( propertyName );
    }

    private static PropertySet getImageInfoSet( final PropertyTree data )
    {
        final PropertySet mixins = data.getSet( ContentPropertyNames.MIXINS );
        if ( mixins == null )
        {
            return null;
        }
        final PropertySet mediaApp = mixins.getSet( MEDIA_APPLICATION_PREFIX );
        if ( mediaApp == null )
        {
            return null;
        }
        return mediaApp.getSet( MediaInfo.IMAGE_INFO );
    }

    private static Cropping readCropping( final PropertySet mediaSet )
    {
        final PropertySet croppingSet = mediaSet.getSet( ContentPropertyNames.MEDIA_CROPPING );
        if ( croppingSet == null )
        {
            return null;
        }
        final Double top = croppingSet.getDouble( ContentPropertyNames.MEDIA_CROPPING_TOP );
        final Double left = croppingSet.getDouble( ContentPropertyNames.MEDIA_CROPPING_LEFT );
        final Double bottom = croppingSet.getDouble( ContentPropertyNames.MEDIA_CROPPING_BOTTOM );
        final Double right = croppingSet.getDouble( ContentPropertyNames.MEDIA_CROPPING_RIGHT );
        if ( top == null || left == null || bottom == null || right == null )
        {
            return null;
        }
        return Cropping.create().top( top ).left( left ).bottom( bottom ).right( right ).build();
    }

    private static String resolveAttachmentName( final PropertyTree data )
    {
        final Property mediaProperty = data.getProperty( ContentPropertyNames.MEDIA );
        if ( mediaProperty == null )
        {
            return null;
        }
        if ( ValueTypes.STRING.equals( mediaProperty.getType() ) )
        {
            return mediaProperty.getString();
        }
        if ( ValueTypes.PROPERTY_SET.equals( mediaProperty.getType() ) )
        {
            final PropertySet mediaSet = mediaProperty.getSet();
            return mediaSet == null ? null : mediaSet.getString( ContentPropertyNames.MEDIA_ATTACHMENT );
        }
        return null;
    }

    private static BinaryReference findAttachmentBinaryRef( final PropertyTree data, final String attachmentName )
    {
        final Iterable<PropertySet> attachments = data.getSets( ContentPropertyNames.ATTACHMENT );
        if ( attachments == null )
        {
            return null;
        }
        for ( PropertySet attachmentSet : attachments )
        {
            if ( attachmentName.equals( attachmentSet.getString( ContentPropertyNames.ATTACHMENT_NAME ) ) )
            {
                return attachmentSet.getBinaryReference( ContentPropertyNames.ATTACHMENT_BINARY_REF );
            }
        }
        return null;
    }

    private static ImageMetadata extractMetadata( final BlobRecord record, final String nodeId )
    {
        final Metadata metadata = new Metadata();
        try (InputStream in = record.getBytes().openStream())
        {
            new AutoDetectParser().parse( in, new DefaultHandler(), metadata, new ParseContext() );
        }
        catch ( IOException | SAXException | TikaException e )
        {
            LOG.warn( "Failed to parse image metadata for node [{}]", nodeId, e );
            return new ImageMetadata( null, null, null );
        }
        return new ImageMetadata( metadata.get( TIFF.ORIENTATION ), parseLong( metadata.get( TIFF.IMAGE_WIDTH ) ),
                                  parseLong( metadata.get( TIFF.IMAGE_LENGTH ) ) );
    }

    private static Long parseLong( final String value )
    {
        if ( value == null )
        {
            return null;
        }
        try
        {
            return Long.parseLong( value );
        }
        catch ( NumberFormatException e )
        {
            return null;
        }
    }

    private static boolean normalizeCropPositionZoom( final PropertySet mediaSet, final String nodeId )
    {
        final PropertySet cropSet = mediaSet.getSet( ContentPropertyNames.MEDIA_CROPPING );
        if ( cropSet == null )
        {
            return false;
        }
        final Double zoom = cropSet.getDouble( "zoom" );
        final Double top = cropSet.getDouble( ContentPropertyNames.MEDIA_CROPPING_TOP );
        final Double left = cropSet.getDouble( ContentPropertyNames.MEDIA_CROPPING_LEFT );
        final Double bottom = cropSet.getDouble( ContentPropertyNames.MEDIA_CROPPING_BOTTOM );
        final Double right = cropSet.getDouble( ContentPropertyNames.MEDIA_CROPPING_RIGHT );
        if ( top == null || left == null || bottom == null || right == null )
        {
            LOG.warn( "cropPosition has missing edge(s) on node [{}]; leaving edges as-is, removing zoom if present", nodeId );
            if ( zoom == null )
            {
                return false;
            }
            cropSet.removeProperties( "zoom" );
            return true;
        }
        boolean changed = false;
        if ( zoom != null && zoom > 1.0 )
        {
            cropSet.setDouble( ContentPropertyNames.MEDIA_CROPPING_TOP, clamp01( top / zoom ) );
            cropSet.setDouble( ContentPropertyNames.MEDIA_CROPPING_LEFT, clamp01( left / zoom ) );
            cropSet.setDouble( ContentPropertyNames.MEDIA_CROPPING_BOTTOM, clamp01( bottom / zoom ) );
            cropSet.setDouble( ContentPropertyNames.MEDIA_CROPPING_RIGHT, clamp01( right / zoom ) );
            changed = true;
        }
        else if ( zoom != null && zoom < 1.0 )
        {
            LOG.warn( "cropPosition.zoom < 1.0 ({}) on node [{}]; leaving edges unchanged", zoom, nodeId );
        }
        if ( zoom != null )
        {
            cropSet.removeProperties( "zoom" );
            changed = true;
        }
        return changed;
    }

    private static double clamp01( final double v )
    {
        return Math.max( 0.0, Math.min( 1.0, v ) );
    }

    private record ImageMetadata(String orientation, Long width, Long height)
    {
    }
}
