package com.enonic.xp.repo.impl.dump.upgrade.model8to9;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;
import com.google.common.io.Resources;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.media.ImageOrientation;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.dump.reader.BlobStoreAccess;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.util.BinaryReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ImageUpgraderTest
{
    private static final RepositoryId PROJECT_REPO = RepositoryId.from( ProjectConstants.PROJECT_REPO_ID_PREFIX + "default" );

    private static final BinaryReference BINARY_REF = BinaryReference.from( "exif-orientation-f2.jpg" );

    private static final String BLOB_KEY = "blob-key-1";

    private BlobStoreAccess blobStoreAccess;

    private ImageUpgrader upgrader;

    @BeforeEach
    void setUp()
        throws IOException
    {
        this.blobStoreAccess = mock( BlobStoreAccess.class );
        final ByteSource byteSource = Resources.asByteSource( ImageUpgraderTest.class.getResource( "exif-orientation-f2.jpg" ) );

        final BlobRecord record = mock( BlobRecord.class );
        when( record.getBytes() ).thenReturn( byteSource );
        when( blobStoreAccess.getRecord( any( Segment.class ), any( BlobKey.class ) ) ).thenReturn( record );

        this.upgrader = new ImageUpgrader( blobStoreAccess );
    }

    @Test
    void writes_orientation_as_long_from_exif()
    {
        final NodeStoreVersion nodeVersion = imageNode();

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( PROJECT_REPO, nodeVersion );

        assertThat( result ).isNotNull();
        final PropertySet mediaSet = result.data().getSet( ContentPropertyNames.MEDIA );
        assertThat( mediaSet.getLong( ContentPropertyNames.ORIENTATION ) ).isEqualTo( 2L );
        assertThat( mediaSet.getProperty( ContentPropertyNames.ORIENTATION ).getType() ).isEqualTo( ValueTypes.LONG );
    }

    @Test
    void writes_original_dimensions_to_image_info_mixin()
    {
        final NodeStoreVersion nodeVersion = imageNode();

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( PROJECT_REPO, nodeVersion );

        assertThat( result ).isNotNull();
        final PropertySet imageInfo = imageInfoSet( result.data() );
        assertThat( imageInfo ).isNotNull();
        assertThat( imageInfo.getLong( MediaInfo.IMAGE_INFO_IMAGE_WIDTH ) ).isNotNull();
        assertThat( imageInfo.getLong( MediaInfo.IMAGE_INFO_IMAGE_HEIGHT ) ).isNotNull();
        final Long width = imageInfo.getLong( MediaInfo.IMAGE_INFO_IMAGE_WIDTH );
        final Long height = imageInfo.getLong( MediaInfo.IMAGE_INFO_IMAGE_HEIGHT );
        assertThat( imageInfo.getLong( MediaInfo.IMAGE_INFO_PIXEL_SIZE ) ).isEqualTo( width * height );
    }

    @Test
    void writes_original_orientation_to_image_info_mixin()
    {
        final NodeStoreVersion nodeVersion = imageNode();

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( PROJECT_REPO, nodeVersion );

        assertThat( result ).isNotNull();
        final PropertySet imageInfo = imageInfoSet( result.data() );
        assertThat( imageInfo.getLong( MediaInfo.IMAGE_INFO_ORIENTATION ) ).isEqualTo( 2L );
        assertThat( imageInfo.getProperty( MediaInfo.IMAGE_INFO_ORIENTATION ).getType() ).isEqualTo( ValueTypes.LONG );
    }

    @Test
    void overwrites_existing_cropped_dimensions_in_image_info()
    {
        final NodeStoreVersion nodeVersion = imageNode();
        final PropertySet existingImageInfo = existingImageInfo( nodeVersion.data() );
        existingImageInfo.addLong( MediaInfo.IMAGE_INFO_IMAGE_WIDTH, 1L );
        existingImageInfo.addLong( MediaInfo.IMAGE_INFO_IMAGE_HEIGHT, 1L );
        existingImageInfo.addLong( MediaInfo.IMAGE_INFO_PIXEL_SIZE, 1L );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( PROJECT_REPO, nodeVersion );

        assertThat( result ).isNotNull();
        final PropertySet imageInfo = imageInfoSet( result.data() );
        assertThat( imageInfo.getLong( MediaInfo.IMAGE_INFO_IMAGE_WIDTH ) ).isGreaterThan( 1L );
        assertThat( imageInfo.getLong( MediaInfo.IMAGE_INFO_IMAGE_HEIGHT ) ).isGreaterThan( 1L );
    }

    @Test
    void writes_effective_size_from_original_and_orientation()
    {
        final NodeStoreVersion nodeVersion = imageNode();

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( PROJECT_REPO, nodeVersion );

        assertThat( result ).isNotNull();
        final PropertySet mediaSet = result.data().getSet( ContentPropertyNames.MEDIA );
        final PropertySet imageInfo = imageInfoSet( result.data() );
        final Long origWidth = imageInfo.getLong( MediaInfo.IMAGE_INFO_IMAGE_WIDTH );
        final Long origHeight = imageInfo.getLong( MediaInfo.IMAGE_INFO_IMAGE_HEIGHT );
        // Orientation 2 (TopRight) is a horizontal flip, no dimension swap
        assertThat( mediaSet.getLong( ContentPropertyNames.MEDIA_IMAGE_WIDTH ) ).isEqualTo( origWidth );
        assertThat( mediaSet.getLong( ContentPropertyNames.MEDIA_IMAGE_HEIGHT ) ).isEqualTo( origHeight );
    }

    @Test
    void writes_effective_size_applying_cropping()
    {
        final NodeStoreVersion nodeVersion = imageNode();
        final PropertySet mediaSet = nodeVersion.data().getSet( ContentPropertyNames.MEDIA );
        final PropertySet cropping = mediaSet.addSet( ContentPropertyNames.MEDIA_CROPPING );
        cropping.addDouble( ContentPropertyNames.MEDIA_CROPPING_TOP, 0.0 );
        cropping.addDouble( ContentPropertyNames.MEDIA_CROPPING_LEFT, 0.0 );
        cropping.addDouble( ContentPropertyNames.MEDIA_CROPPING_BOTTOM, 0.5 );
        cropping.addDouble( ContentPropertyNames.MEDIA_CROPPING_RIGHT, 0.5 );
        cropping.addDouble( "zoom", 1.0 );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( PROJECT_REPO, nodeVersion );

        assertThat( result ).isNotNull();
        final PropertySet imageInfo = imageInfoSet( result.data() );
        final Long origWidth = imageInfo.getLong( MediaInfo.IMAGE_INFO_IMAGE_WIDTH );
        final Long origHeight = imageInfo.getLong( MediaInfo.IMAGE_INFO_IMAGE_HEIGHT );
        final PropertySet resultMedia = result.data().getSet( ContentPropertyNames.MEDIA );
        assertThat( resultMedia.getLong( ContentPropertyNames.MEDIA_IMAGE_WIDTH ) ).isEqualTo( origWidth / 2 );
        assertThat( resultMedia.getLong( ContentPropertyNames.MEDIA_IMAGE_HEIGHT ) ).isEqualTo( origHeight / 2 );
    }

    @Test
    void normalizes_legacy_string_orientation_to_long()
    {
        final NodeStoreVersion nodeVersion = imageNode();
        nodeVersion.data().getSet( ContentPropertyNames.MEDIA ).addString( ContentPropertyNames.ORIENTATION, "6" );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( PROJECT_REPO, nodeVersion );

        assertThat( result ).isNotNull();
        final PropertySet mediaSet = result.data().getSet( ContentPropertyNames.MEDIA );
        assertThat( mediaSet.getLong( ContentPropertyNames.ORIENTATION ) ).isEqualTo( 6L );
        assertThat( mediaSet.getProperty( ContentPropertyNames.ORIENTATION ).getType() ).isEqualTo( ValueTypes.LONG );
    }

    @Test
    void swaps_effective_size_when_orientation_rotates_90()
    {
        final NodeStoreVersion nodeVersion = imageNode();
        nodeVersion.data()
            .getSet( ContentPropertyNames.MEDIA )
            .addLong( ContentPropertyNames.ORIENTATION, (long) ImageOrientation.RightTop.getValue() );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( PROJECT_REPO, nodeVersion );

        assertThat( result ).isNotNull();
        final PropertySet imageInfo = imageInfoSet( result.data() );
        final Long origWidth = imageInfo.getLong( MediaInfo.IMAGE_INFO_IMAGE_WIDTH );
        final Long origHeight = imageInfo.getLong( MediaInfo.IMAGE_INFO_IMAGE_HEIGHT );
        final PropertySet resultMedia = result.data().getSet( ContentPropertyNames.MEDIA );
        assertThat( resultMedia.getLong( ContentPropertyNames.MEDIA_IMAGE_WIDTH ) ).isEqualTo( origHeight );
        assertThat( resultMedia.getLong( ContentPropertyNames.MEDIA_IMAGE_HEIGHT ) ).isEqualTo( origWidth );
    }

    @Test
    void converts_legacy_string_media_to_property_set()
    {
        final PropertyTree data = new PropertyTree();
        data.setString( ContentPropertyNames.TYPE, "media:image" );
        data.setString( ContentPropertyNames.MEDIA, "exif-orientation-f2.jpg" );
        final PropertySet attachment = data.addSet( ContentPropertyNames.ATTACHMENT );
        attachment.addString( ContentPropertyNames.ATTACHMENT_NAME, "exif-orientation-f2.jpg" );
        attachment.addBinaryReference( ContentPropertyNames.ATTACHMENT_BINARY_REF, BINARY_REF );

        final AttachedBinaries attachedBinaries = AttachedBinaries.create().add( new AttachedBinary( BINARY_REF, BLOB_KEY ) ).build();

        final NodeStoreVersion nodeVersion = NodeStoreVersion.create()
            .id( NodeId.from( "image-legacy" ) )
            .nodeType( ContentConstants.CONTENT_NODE_COLLECTION )
            .data( data )
            .attachedBinaries( attachedBinaries )
            .build();

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( PROJECT_REPO, nodeVersion );

        assertThat( result ).isNotNull();
        final PropertySet mediaSet = result.data().getSet( ContentPropertyNames.MEDIA );
        assertThat( mediaSet ).isNotNull();
        assertThat( mediaSet.getString( ContentPropertyNames.MEDIA_ATTACHMENT ) ).isEqualTo( "exif-orientation-f2.jpg" );
        assertThat( mediaSet.getLong( ContentPropertyNames.ORIENTATION ) ).isEqualTo( 2L );
    }

    @Test
    void skips_non_image_content()
    {
        final PropertyTree data = new PropertyTree();
        data.setString( ContentPropertyNames.TYPE, "media:document" );
        final NodeStoreVersion nodeVersion = NodeStoreVersion.create()
            .id( NodeId.from( "doc-1" ) )
            .nodeType( ContentConstants.CONTENT_NODE_COLLECTION )
            .data( data )
            .build();

        assertThat( upgrader.upgradeNodeVersion( PROJECT_REPO, nodeVersion ) ).isNull();
    }

    @Test
    void skips_non_project_repository()
    {
        final RepositoryId systemRepo = RepositoryId.from( "system-repo" );

        assertThat( upgrader.upgradeNodeVersion( systemRepo, imageNode() ) ).isNull();
    }

    @Test
    void skips_when_media_property_missing()
    {
        final PropertyTree data = new PropertyTree();
        data.setString( ContentPropertyNames.TYPE, "media:image" );
        final NodeStoreVersion nodeVersion = NodeStoreVersion.create()
            .id( NodeId.from( "image-2" ) )
            .nodeType( ContentConstants.CONTENT_NODE_COLLECTION )
            .data( data )
            .build();

        assertThat( upgrader.upgradeNodeVersion( PROJECT_REPO, nodeVersion ) ).isNull();
    }

    @Test
    void skips_when_attached_binary_missing()
    {
        final PropertyTree data = new PropertyTree();
        data.setString( ContentPropertyNames.TYPE, "media:image" );
        data.addSet( ContentPropertyNames.MEDIA ).addString( ContentPropertyNames.MEDIA_ATTACHMENT, "ghost.jpg" );
        final PropertySet attachment = data.addSet( ContentPropertyNames.ATTACHMENT );
        attachment.addString( ContentPropertyNames.ATTACHMENT_NAME, "ghost.jpg" );
        attachment.addBinaryReference( ContentPropertyNames.ATTACHMENT_BINARY_REF, BinaryReference.from( "ghost.jpg" ) );

        final NodeStoreVersion nodeVersion = NodeStoreVersion.create()
            .id( NodeId.from( "image-3" ) )
            .nodeType( ContentConstants.CONTENT_NODE_COLLECTION )
            .data( data )
            .build();

        assertThat( upgrader.upgradeNodeVersion( PROJECT_REPO, nodeVersion ) ).isNull();
    }

    @Test
    void returns_null_on_corrupt_binary()
        throws IOException
    {
        final BlobStoreAccess corruptAccess = mock( BlobStoreAccess.class );
        final BlobRecord corruptRecord = mock( BlobRecord.class );
        when( corruptRecord.getBytes() ).thenReturn( ByteSource.wrap( new byte[]{0x00, 0x01, 0x02} ) );
        when( corruptAccess.getRecord( any( Segment.class ), any( BlobKey.class ) ) ).thenReturn( corruptRecord );

        final ImageUpgrader corruptUpgrader = new ImageUpgrader( corruptAccess );

        assertThat( corruptUpgrader.upgradeNodeVersion( PROJECT_REPO, imageNode() ) ).isNull();
    }

    @Test
    void normalizes_crop_position_zoom_greater_than_one()
    {
        final NodeStoreVersion nodeVersion = imageNode();
        final PropertySet cropping = nodeVersion.data().getSet( ContentPropertyNames.MEDIA ).addSet( ContentPropertyNames.MEDIA_CROPPING );
        cropping.addDouble( ContentPropertyNames.MEDIA_CROPPING_TOP, 0.20 );
        cropping.addDouble( ContentPropertyNames.MEDIA_CROPPING_LEFT, 0.10 );
        cropping.addDouble( ContentPropertyNames.MEDIA_CROPPING_BOTTOM, 0.80 );
        cropping.addDouble( ContentPropertyNames.MEDIA_CROPPING_RIGHT, 0.90 );
        cropping.addDouble( "zoom", 2.0 );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( PROJECT_REPO, nodeVersion );

        assertThat( result ).isNotNull();
        final PropertySet upgraded = result.data().getSet( ContentPropertyNames.MEDIA ).getSet( ContentPropertyNames.MEDIA_CROPPING );
        assertThat( upgraded.getDouble( ContentPropertyNames.MEDIA_CROPPING_TOP ) ).isEqualTo( 0.10 );
        assertThat( upgraded.getDouble( ContentPropertyNames.MEDIA_CROPPING_LEFT ) ).isEqualTo( 0.05 );
        assertThat( upgraded.getDouble( ContentPropertyNames.MEDIA_CROPPING_BOTTOM ) ).isEqualTo( 0.40 );
        assertThat( upgraded.getDouble( ContentPropertyNames.MEDIA_CROPPING_RIGHT ) ).isEqualTo( 0.45 );
        assertThat( upgraded.getDouble( "zoom" ) ).isNull();
    }

    @Test
    void removes_crop_position_zoom_equal_to_one()
    {
        final NodeStoreVersion nodeVersion = imageNode();
        final PropertySet cropping = nodeVersion.data().getSet( ContentPropertyNames.MEDIA ).addSet( ContentPropertyNames.MEDIA_CROPPING );
        cropping.addDouble( ContentPropertyNames.MEDIA_CROPPING_TOP, 0.10 );
        cropping.addDouble( ContentPropertyNames.MEDIA_CROPPING_LEFT, 0.20 );
        cropping.addDouble( ContentPropertyNames.MEDIA_CROPPING_BOTTOM, 0.80 );
        cropping.addDouble( ContentPropertyNames.MEDIA_CROPPING_RIGHT, 0.90 );
        cropping.addDouble( "zoom", 1.0 );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( PROJECT_REPO, nodeVersion );

        assertThat( result ).isNotNull();
        final PropertySet upgraded = result.data().getSet( ContentPropertyNames.MEDIA ).getSet( ContentPropertyNames.MEDIA_CROPPING );
        assertThat( upgraded.getDouble( ContentPropertyNames.MEDIA_CROPPING_TOP ) ).isEqualTo( 0.10 );
        assertThat( upgraded.getDouble( ContentPropertyNames.MEDIA_CROPPING_RIGHT ) ).isEqualTo( 0.90 );
        assertThat( upgraded.getDouble( "zoom" ) ).isNull();
    }

    @Test
    void crop_position_without_zoom_is_unchanged()
    {
        final NodeStoreVersion nodeVersion = imageNode();
        final PropertySet cropping = nodeVersion.data().getSet( ContentPropertyNames.MEDIA ).addSet( ContentPropertyNames.MEDIA_CROPPING );
        cropping.addDouble( ContentPropertyNames.MEDIA_CROPPING_TOP, 0.10 );
        cropping.addDouble( ContentPropertyNames.MEDIA_CROPPING_LEFT, 0.20 );
        cropping.addDouble( ContentPropertyNames.MEDIA_CROPPING_BOTTOM, 0.80 );
        cropping.addDouble( ContentPropertyNames.MEDIA_CROPPING_RIGHT, 0.90 );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( PROJECT_REPO, nodeVersion );

        assertThat( result ).isNotNull();
        final PropertySet upgraded = result.data().getSet( ContentPropertyNames.MEDIA ).getSet( ContentPropertyNames.MEDIA_CROPPING );
        assertThat( upgraded.getDouble( ContentPropertyNames.MEDIA_CROPPING_TOP ) ).isEqualTo( 0.10 );
        assertThat( upgraded.getDouble( ContentPropertyNames.MEDIA_CROPPING_RIGHT ) ).isEqualTo( 0.90 );
        assertThat( upgraded.getDouble( "zoom" ) ).isNull();
    }

    @Test
    void crop_position_zoom_less_than_one_keeps_edges_removes_zoom()
    {
        final NodeStoreVersion nodeVersion = imageNode();
        final PropertySet cropping = nodeVersion.data().getSet( ContentPropertyNames.MEDIA ).addSet( ContentPropertyNames.MEDIA_CROPPING );
        cropping.addDouble( ContentPropertyNames.MEDIA_CROPPING_TOP, 0.10 );
        cropping.addDouble( ContentPropertyNames.MEDIA_CROPPING_LEFT, 0.20 );
        cropping.addDouble( ContentPropertyNames.MEDIA_CROPPING_BOTTOM, 0.80 );
        cropping.addDouble( ContentPropertyNames.MEDIA_CROPPING_RIGHT, 0.90 );
        cropping.addDouble( "zoom", 0.5 );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( PROJECT_REPO, nodeVersion );

        assertThat( result ).isNotNull();
        final PropertySet upgraded = result.data().getSet( ContentPropertyNames.MEDIA ).getSet( ContentPropertyNames.MEDIA_CROPPING );
        assertThat( upgraded.getDouble( ContentPropertyNames.MEDIA_CROPPING_TOP ) ).isEqualTo( 0.10 );
        assertThat( upgraded.getDouble( ContentPropertyNames.MEDIA_CROPPING_RIGHT ) ).isEqualTo( 0.90 );
        assertThat( upgraded.getDouble( "zoom" ) ).isNull();
    }

    @Test
    void crop_position_with_missing_edge_keeps_edges_removes_zoom_even_when_zoom_greater_than_one()
    {
        final NodeStoreVersion nodeVersion = imageNode();
        final PropertySet cropping = nodeVersion.data().getSet( ContentPropertyNames.MEDIA ).addSet( ContentPropertyNames.MEDIA_CROPPING );
        cropping.addDouble( ContentPropertyNames.MEDIA_CROPPING_TOP, 0.20 );
        cropping.addDouble( ContentPropertyNames.MEDIA_CROPPING_LEFT, 0.10 );
        // bottom intentionally missing
        cropping.addDouble( ContentPropertyNames.MEDIA_CROPPING_RIGHT, 0.90 );
        cropping.addDouble( "zoom", 2.0 );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( PROJECT_REPO, nodeVersion );

        assertThat( result ).isNotNull();
        final PropertySet upgraded = result.data().getSet( ContentPropertyNames.MEDIA ).getSet( ContentPropertyNames.MEDIA_CROPPING );
        // Edges left as-is — NOT divided by zoom
        assertThat( upgraded.getDouble( ContentPropertyNames.MEDIA_CROPPING_TOP ) ).isEqualTo( 0.20 );
        assertThat( upgraded.getDouble( ContentPropertyNames.MEDIA_CROPPING_LEFT ) ).isEqualTo( 0.10 );
        assertThat( upgraded.getDouble( ContentPropertyNames.MEDIA_CROPPING_RIGHT ) ).isEqualTo( 0.90 );
        assertThat( upgraded.getDouble( ContentPropertyNames.MEDIA_CROPPING_BOTTOM ) ).isNull();
        // Zoom is removed
        assertThat( upgraded.getDouble( "zoom" ) ).isNull();
    }

    @Test
    void crop_position_zoom_normalization_is_idempotent()
    {
        final NodeStoreVersion nodeVersion = imageNode();
        final PropertySet cropping = nodeVersion.data().getSet( ContentPropertyNames.MEDIA ).addSet( ContentPropertyNames.MEDIA_CROPPING );
        cropping.addDouble( ContentPropertyNames.MEDIA_CROPPING_TOP, 0.20 );
        cropping.addDouble( ContentPropertyNames.MEDIA_CROPPING_LEFT, 0.10 );
        cropping.addDouble( ContentPropertyNames.MEDIA_CROPPING_BOTTOM, 0.80 );
        cropping.addDouble( ContentPropertyNames.MEDIA_CROPPING_RIGHT, 0.90 );
        cropping.addDouble( "zoom", 2.0 );

        final NodeStoreVersion firstPass = upgrader.upgradeNodeVersion( PROJECT_REPO, nodeVersion );
        final NodeStoreVersion secondPass = upgrader.upgradeNodeVersion( PROJECT_REPO, firstPass );

        // After the first pass, edges are halved and zoom is removed.
        // Second pass should re-run safely without further mutation of edges or re-introducing zoom.
        final PropertySet upgraded = ( secondPass != null ? secondPass : firstPass ).data().getSet( ContentPropertyNames.MEDIA ).getSet( ContentPropertyNames.MEDIA_CROPPING );
        assertThat( upgraded.getDouble( ContentPropertyNames.MEDIA_CROPPING_TOP ) ).isEqualTo( 0.10 );
        assertThat( upgraded.getDouble( ContentPropertyNames.MEDIA_CROPPING_LEFT ) ).isEqualTo( 0.05 );
        assertThat( upgraded.getDouble( ContentPropertyNames.MEDIA_CROPPING_BOTTOM ) ).isEqualTo( 0.40 );
        assertThat( upgraded.getDouble( ContentPropertyNames.MEDIA_CROPPING_RIGHT ) ).isEqualTo( 0.45 );
        assertThat( upgraded.getDouble( "zoom" ) ).isNull();
    }

    private static PropertySet imageInfoSet( final PropertyTree data )
    {
        final PropertySet mixins = data.getSet( ContentPropertyNames.MIXINS );
        if ( mixins == null )
        {
            return null;
        }
        final PropertySet mediaApp = mixins.getSet( ApplicationKey.MEDIA_MOD.toString() );
        if ( mediaApp == null )
        {
            return null;
        }
        return mediaApp.getSet( MediaInfo.IMAGE_INFO );
    }

    private static PropertySet existingImageInfo( final PropertyTree data )
    {
        final PropertySet mixins = data.addSet( ContentPropertyNames.MIXINS );
        final PropertySet mediaApp = mixins.addSet( ApplicationKey.MEDIA_MOD.toString() );
        return mediaApp.addSet( MediaInfo.IMAGE_INFO );
    }

    private static NodeStoreVersion imageNode()
    {
        final PropertyTree data = new PropertyTree();
        data.setString( ContentPropertyNames.TYPE, "media:image" );
        data.addSet( ContentPropertyNames.MEDIA ).addString( ContentPropertyNames.MEDIA_ATTACHMENT, "exif-orientation-f2.jpg" );

        final PropertySet attachment = data.addSet( ContentPropertyNames.ATTACHMENT );
        attachment.addString( ContentPropertyNames.ATTACHMENT_NAME, "exif-orientation-f2.jpg" );
        attachment.addBinaryReference( ContentPropertyNames.ATTACHMENT_BINARY_REF, BINARY_REF );

        final AttachedBinaries attachedBinaries = AttachedBinaries.create().add( new AttachedBinary( BINARY_REF, BLOB_KEY ) ).build();

        return NodeStoreVersion.create()
            .id( NodeId.from( "image-1" ) )
            .nodeType( ContentConstants.CONTENT_NODE_COLLECTION )
            .data( data )
            .attachedBinaries( attachedBinaries )
            .build();
    }
}
