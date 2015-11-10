package com.enonic.xp.repo.impl.node.dao;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.osgi.service.component.annotations.Component;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;

import com.enonic.xp.home.HomeDir;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionIds;
import com.enonic.xp.node.NodeVersions;
import com.enonic.xp.repo.impl.blob.BlobKey;
import com.enonic.xp.repo.impl.blob.BlobRecord;
import com.enonic.xp.repo.impl.blob.BlobStore;
import com.enonic.xp.repo.impl.blob.file.FileBlobStore;
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.repo.impl.node.json.NodeVersionJsonSerializer;

@Component
public class NodeVersionDaoImpl
    implements NodeVersionDao
{
    private final NodeVersionJsonSerializer nodeVersionJsonSerializer = NodeVersionJsonSerializer.create( false );

    private final BlobStore nodeVersionBlobStore;

    public NodeVersionDaoImpl()
    {
        final File blobStoreDir = new File( HomeDir.get().toFile(), "repo/blob/" + NodeConstants.NODE_VERSION_BLOB_STORE_DIR );
        this.nodeVersionBlobStore = new FileBlobStore( blobStoreDir );
    }

    @Override
    public NodeVersionId store( final Node node )
    {
        final BlobRecord blob = doStoreNodeAsBlob( node );

        return NodeVersionId.from( blob.getKey().toString() );
    }

    private BlobRecord doStoreNodeAsBlob( final Node node )
    {
        final String serializedNode = this.nodeVersionJsonSerializer.toString( NodeVersion.from( node ) );
        final ByteSource source = ByteSource.wrap( serializedNode.getBytes( StandardCharsets.UTF_8 ) );
        return nodeVersionBlobStore.addRecord( source );
    }

    @Override
    public NodeVersions get( final NodeVersionIds nodeVersionIds )
    {
        return doGetFromVersionIds( nodeVersionIds );
    }

    @Override
    public NodeVersion get( final NodeVersionId nodeVersionId )
    {
        return doGetByVersionId( nodeVersionId );
    }

    private NodeVersion doGetByVersionId( final NodeVersionId nodeVersionId )
    {
        final BlobKey blobKey = new BlobKey( nodeVersionId.toString() );

        final NodeVersion nodeVersionFromBlob = getNodeVersionFromBlob( nodeVersionBlobStore.getRecord( blobKey ) );
        return NodeVersion.create( nodeVersionFromBlob ).
            versionId( nodeVersionId ).
            build();
    }

    private NodeVersions doGetFromVersionIds( final NodeVersionIds nodeVersionIds )
    {
        NodeVersions.Builder builder = NodeVersions.create();

        for ( final NodeVersionId nodeVersionId : nodeVersionIds )
        {
            final BlobRecord blob = nodeVersionBlobStore.getRecord( new BlobKey( nodeVersionId.toString() ) );

            if ( blob == null )
            {
                throw new NodeNotFoundException( "Blob for node with BlobKey " + nodeVersionId + " not found" );
            }

            final NodeVersion nodeVersionFromBlob = getNodeVersionFromBlob( blob );
            final NodeVersion nodeVersion = NodeVersion.create( nodeVersionFromBlob ).
                versionId( nodeVersionId ).
                build();

            builder.add( nodeVersion );
        }

        return builder.build();
    }

    private NodeVersion getNodeVersionFromBlob( final BlobRecord blob )
    {
        if ( blob == null )
        {
            throw new IllegalArgumentException( "Trying to load blob when blob is null" );
        }

        try
        {
            final String str = blob.getBytes().asCharSource( Charsets.UTF_8 ).read();
            return this.nodeVersionJsonSerializer.toNodeVersion( str );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to load blob with key: " + blob.getKey(), e );
        }
    }
}
