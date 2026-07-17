package com.enonic.xp.repo.impl.version;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.storage.SearchPreferences;
import com.enonic.xp.storage.spi.NodeStore;
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
        this.nodeStore.storeVersion( context.getRepositoryId(), NodeVersionFactory.toRecord( nodeVersion ) );
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
