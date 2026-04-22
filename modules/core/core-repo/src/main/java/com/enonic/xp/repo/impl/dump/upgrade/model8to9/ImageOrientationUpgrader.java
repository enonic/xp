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

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.media.ImageOrientation;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.dump.reader.BlobStoreAccess;
import com.enonic.xp.repo.impl.dump.upgrade.NodeVersionUpgrader;
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositorySegmentUtils;
import com.enonic.xp.util.BinaryReference;

public class ImageOrientationUpgrader
    implements NodeVersionUpgrader
{
    private static final Logger LOG = LoggerFactory.getLogger( ImageOrientationUpgrader.class );

    private static final String IMAGE_CONTENT_TYPE = "media:image";

    private final BlobStoreAccess blobStoreAccess;

    public ImageOrientationUpgrader( final BlobStoreAccess blobStoreAccess )
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
        if ( hasExistingOrientation( data ) )
        {
            return null;
        }

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

        final String orientation = extractOrientation( record, nodeVersion.id().toString() );
        if ( orientation == null || !ImageOrientation.isValid( orientation ) )
        {
            return null;
        }

        if ( !writeOrientation( data, orientation ) )
        {
            return null;
        }
        LOG.info( "Extracted camera orientation [{}] for node [{}] in repository [{}]", orientation, nodeVersion.id(), repositoryId );
        return nodeVersion;
    }

    private static boolean hasExistingOrientation( final PropertyTree data )
    {
        final Property mediaProperty = data.getProperty( ContentPropertyNames.MEDIA );
        if ( mediaProperty == null || !ValueTypes.PROPERTY_SET.equals( mediaProperty.getType() ) )
        {
            return false;
        }
        final PropertySet mediaSet = mediaProperty.getSet();
        return mediaSet != null && mediaSet.getString( ContentPropertyNames.ORIENTATION ) != null;
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

    private static String extractOrientation( final BlobRecord record, final String nodeId )
    {
        final Metadata metadata = new Metadata();
        try (InputStream in = record.getBytes().openStream())
        {
            new AutoDetectParser().parse( in, new DefaultHandler(), metadata, new ParseContext() );
        }
        catch ( IOException | SAXException | TikaException e )
        {
            LOG.warn( "Failed to parse EXIF for node [{}]", nodeId, e );
            return null;
        }
        return metadata.get( TIFF.ORIENTATION );
    }

    private static boolean writeOrientation( final PropertyTree data, final String orientation )
    {
        final Property mediaProperty = data.getProperty( ContentPropertyNames.MEDIA );
        if ( mediaProperty == null )
        {
            return false;
        }
        if ( ValueTypes.STRING.equals( mediaProperty.getType() ) )
        {
            final String attachmentName = mediaProperty.getString();
            data.removeProperties( ContentPropertyNames.MEDIA );
            final PropertySet mediaSet = data.addSet( ContentPropertyNames.MEDIA );
            mediaSet.addString( ContentPropertyNames.MEDIA_ATTACHMENT, attachmentName );
            mediaSet.addString( ContentPropertyNames.ORIENTATION, orientation );
            return true;
        }
        if ( ValueTypes.PROPERTY_SET.equals( mediaProperty.getType() ) )
        {
            final PropertySet mediaSet = mediaProperty.getSet();
            if ( mediaSet == null )
            {
                return false;
            }
            mediaSet.addString( ContentPropertyNames.ORIENTATION, orientation );
            return true;
        }
        return false;
    }
}
