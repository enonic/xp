package com.enonic.xp.repo.impl.version;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionKey;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.storage.SearchPreferences;
import com.enonic.xp.storage.spi.NodeSegments;
import com.enonic.xp.storage.spi.NodeStore;
import com.enonic.xp.storage.spi.PayloadSegment;
import com.enonic.xp.storage.spi.VersionRecord;

@Component
public class VersionServiceImpl
    implements VersionService
{
    private final NodeStore nodeStore;

    @Activate
    public VersionServiceImpl( @Reference final NodeStore nodeStore )
    {
        this.nodeStore = nodeStore;
    }

    @Override
    public void store( final NodeVersion nodeVersion, final InternalContext context )
    {
        store( nodeVersion, hashOnlySegments( nodeVersion.getNodeVersionKey() ), context );
    }

    @Override
    public void store( final NodeVersion nodeVersion, final NodeSegments segments, final InternalContext context )
    {
        this.nodeStore.storeVersion( context.getRepositoryId(), NodeVersionFactory.toRecord( nodeVersion ), segments );
    }

    /** No new bytes: the version's key is unchanged, so every segment is a hash-only reference to already-stored content. */
    private static NodeSegments hashOnlySegments( final NodeVersionKey key )
    {
        return new NodeSegments( new PayloadSegment( key.getNodeBlobKey().toString(), null ),
                                  new PayloadSegment( key.getIndexConfigBlobKey().toString(), null ),
                                  new PayloadSegment( key.getAccessControlBlobKey().toString(), null ) );
    }

    @Override
    public void delete( final NodeVersionId nodeVersionId, final InternalContext context )
    {
        this.nodeStore.deleteVersion( context.getRepositoryId(), nodeVersionId.toString() );
    }

    @Override
    public NodeVersion getVersion( final NodeVersionId nodeVersionId, final InternalContext context )
    {
        final VersionRecord record = this.nodeStore.getVersion( context.getRepositoryId(), nodeVersionId.toString(),
                                                                 SearchPreferences.toSpi( context.getSearchPreference() ) );

        return record == null ? null : NodeVersionFactory.fromRecord( record );
    }
}
