package com.enonic.xp.repo.impl.storage;

import java.util.Collection;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.StorageSource;
import com.enonic.xp.repo.impl.elasticsearch.executor.ExecutorProgressListener;
import com.enonic.xp.repository.RepositoryId;

public class CopyRequest
    implements ExecutorProgressListener
{
    private final StorageSource storageSource;

    private final Collection<NodeId> nodeIds;

    private final Branch targetBranch;

    private final RepositoryId targetRepo;

    private final ExecutorProgressListener progressListener;

    private CopyRequest( final Builder builder )
    {
        storageSource = builder.storageSource;
        nodeIds = builder.nodeIds;
        targetBranch = builder.targetBranch;
        targetRepo = builder.targetRepo;
        progressListener = builder.progressListener;
    }

    public StorageSource getStorageSource()
    {
        return storageSource;
    }

    public Collection<NodeId> getNodeIds()
    {
        return nodeIds;
    }

    public Branch getTargetBranch()
    {
        return targetBranch;
    }

    public RepositoryId getTargetRepo()
    {
        return targetRepo;
    }

    @Override
    public void progress( final int count )
    {
        if ( progressListener != null )
        {
            progressListener.progress( count );
        }
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private StorageSource storageSource;

        private Collection<NodeId> nodeIds;

        private Branch targetBranch;

        private RepositoryId targetRepo;

        private ExecutorProgressListener progressListener;

        private Builder()
        {
        }

        public Builder storageSettings( final StorageSource val )
        {
            storageSource = val;
            return this;
        }

        public Builder nodeIds( final Collection<NodeId> val )
        {
            nodeIds = val;
            return this;
        }

        public Builder targetBranch( final Branch val )
        {
            targetBranch = val;
            return this;
        }

        public Builder targetRepo( final RepositoryId val )
        {
            targetRepo = val;
            return this;
        }

        public Builder progressListener( final ExecutorProgressListener progressListener )
        {
            this.progressListener = progressListener;
            return this;
        }

        public CopyRequest build()
        {
            return new CopyRequest( this );
        }
    }
}
