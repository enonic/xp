package com.enonic.xp.repo.impl.commit;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.commit.storage.NodeCommitEntryFactory;
import com.enonic.xp.repo.impl.storage.SearchPreferences;
import com.enonic.xp.storage.spi.CommitRecord;
import com.enonic.xp.storage.spi.NodeStore;

@Component
public class CommitServiceImpl
    implements CommitService
{
    private final NodeStore nodeStore;

    @Activate
    public CommitServiceImpl( @Reference final NodeStore nodeStore )
    {
        this.nodeStore = nodeStore;
    }

    @Override
    public String store( final NodeCommitEntry nodeBranchEntry, final InternalContext context )
    {
        this.nodeStore.storeCommit( context.getRepositoryId(), NodeCommitEntryFactory.toRecord( nodeBranchEntry ) );
        return nodeBranchEntry.getNodeCommitId().toString();
    }

    @Override
    public NodeCommitEntry get( final NodeCommitId nodeCommitId, final InternalContext context )
    {
        final CommitRecord record = this.nodeStore.getCommit( context.getRepositoryId(), nodeCommitId.toString(),
                                                               SearchPreferences.toSpi( context.getSearchPreference() ) );

        return record == null ? null : NodeCommitEntryFactory.fromRecord( record );
    }
}
