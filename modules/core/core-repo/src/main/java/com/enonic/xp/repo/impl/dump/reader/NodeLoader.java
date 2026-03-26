package com.enonic.xp.repo.impl.dump.reader;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.ImportNodeCommitParams;
import com.enonic.xp.node.ImportNodeVersionParams;
import com.enonic.xp.node.LoadNodeParams;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.storage.NodeStorageService;
import com.enonic.xp.repo.impl.storage.StoreNodeCommitParams;
import com.enonic.xp.repo.impl.storage.StoreNodeParams;
import com.enonic.xp.repo.impl.storage.StoreNodeVersionParams;

public class NodeLoader
{
    private final NodeStorageService nodeStorageService;

    public NodeLoader( final NodeStorageService nodeStorageService )
    {
        this.nodeStorageService = nodeStorageService;
    }

    public void loadNode( final LoadNodeParams params )
    {
        this.nodeStorageService.store(
            StoreNodeParams.overrideVersion( params.getNode(), params.getNodeCommitId(), params.getAttributes() ),
            InternalContext.from( ContextAccessor.current() ) );
    }

    public void importNodeCommit( final ImportNodeCommitParams params )
    {
        this.nodeStorageService.storeCommit( StoreNodeCommitParams.create()
                                                 .nodeCommitId( params.getNodeCommitId() )
                                                 .message( params.getMessage() )
                                                 .committer( params.getCommitter() )
                                                 .timestamp( params.getTimestamp() )
                                                 .build(), InternalContext.from( ContextAccessor.current() ) );
    }

    public void importNodeVersion( final ImportNodeVersionParams params )
    {
        this.nodeStorageService.storeVersion( StoreNodeVersionParams.create()
                                                  .nodeId( params.getNode().id() )
                                                  .nodePath( params.getNode().path() )
                                                  .nodeVersion( NodeStoreVersion.from( params.getNode() ) )
                                                  .nodeVersionId( params.getNode().getNodeVersionId() )
                                                  .nodeCommitId( params.getNodeCommitId() )
                                                  .timestamp( params.getNode().getTimestamp() )
                                                  .attributes( params.getAttributes() )
                                                  .build(), InternalContext.from( ContextAccessor.current() ) );
    }
}
