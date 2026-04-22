package com.enonic.xp.repo.impl.dump.upgrade.model8to9;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;
import com.google.common.io.Resources;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
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

class ImageOrientationUpgraderTest
{
    private static final RepositoryId PROJECT_REPO = RepositoryId.from( ProjectConstants.PROJECT_REPO_ID_PREFIX + "default" );

    private static final BinaryReference BINARY_REF = BinaryReference.from( "exif-orientation-f2.jpg" );

    private static final String BLOB_KEY = "blob-key-1";

    private BlobStoreAccess blobStoreAccess;

    private ImageOrientationUpgrader upgrader;

    @BeforeEach
    void setUp()
        throws IOException
    {
        this.blobStoreAccess = mock( BlobStoreAccess.class );
        final ByteSource byteSource = Resources.asByteSource( ImageOrientationUpgraderTest.class.getResource( "exif-orientation-f2.jpg" ) );

        final BlobRecord record = mock( BlobRecord.class );
        when( record.getBytes() ).thenReturn( byteSource );
        when( blobStoreAccess.getRecord( any( Segment.class ), any( BlobKey.class ) ) ).thenReturn( record );

        this.upgrader = new ImageOrientationUpgrader( blobStoreAccess );
    }

    @Test
    void writes_orientation_from_exif_when_missing()
    {
        final NodeStoreVersion nodeVersion = imageNode();

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( PROJECT_REPO, nodeVersion );

        assertThat( result ).isNotNull();
        assertThat( result.data().getString( "media.orientation" ) ).isEqualTo( "2" );
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
        assertThat( mediaSet.getString( ContentPropertyNames.ORIENTATION ) ).isEqualTo( "2" );
    }

    @Test
    void skips_when_orientation_already_present()
    {
        final NodeStoreVersion nodeVersion = imageNode();
        nodeVersion.data().getSet( ContentPropertyNames.MEDIA ).addString( ContentPropertyNames.ORIENTATION, "6" );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( PROJECT_REPO, nodeVersion );

        assertThat( result ).isNull();
        assertThat( nodeVersion.data().getString( "media.orientation" ) ).isEqualTo( "6" );
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
        final var attachment = data.addSet( ContentPropertyNames.ATTACHMENT );
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

        final ImageOrientationUpgrader corruptUpgrader = new ImageOrientationUpgrader( corruptAccess );

        assertThat( corruptUpgrader.upgradeNodeVersion( PROJECT_REPO, imageNode() ) ).isNull();
    }

    private static NodeStoreVersion imageNode()
    {
        final PropertyTree data = new PropertyTree();
        data.setString( ContentPropertyNames.TYPE, "media:image" );
        data.addSet( ContentPropertyNames.MEDIA ).addString( ContentPropertyNames.MEDIA_ATTACHMENT, "exif-orientation-f2.jpg" );

        final var attachment = data.addSet( ContentPropertyNames.ATTACHMENT );
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
