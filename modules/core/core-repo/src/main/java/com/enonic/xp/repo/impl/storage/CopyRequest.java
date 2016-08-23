package com.enonic.xp.repo.impl.storage;

import com.enonic.xp.branch.BranchId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.repo.impl.StorageSettings;
import com.enonic.xp.repository.RepositoryId;

public class CopyRequest
{
    private final StorageSettings storageSettings;

    private final NodeIds nodeIds;

    private final BranchId targetBranchId;

    private final RepositoryId targetRepo;

    private CopyRequest( final Builder builder )
    {
        storageSettings = builder.storageSettings;
        nodeIds = builder.nodeIds;
        targetBranchId = builder.targetBranchId;
        targetRepo = builder.targetRepo;
    }

    public StorageSettings getStorageSettings()
    {
        return storageSettings;
    }

    public NodeIds getNodeIds()
    {
        return nodeIds;
    }

    public BranchId getTargetBranchId()
    {
        return targetBranchId;
    }

    public RepositoryId getTargetRepo()
    {
        return targetRepo;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private StorageSettings storageSettings;

        private NodeIds nodeIds;

        private BranchId targetBranchId;

        private RepositoryId targetRepo;

        private Builder()
        {
        }

        public Builder storageSettings( final StorageSettings val )
        {
            storageSettings = val;
            return this;
        }

        public Builder nodeIds( final NodeIds val )
        {
            nodeIds = val;
            return this;
        }

        public Builder targetBranch( final BranchId val )
        {
            targetBranchId = val;
            return this;
        }

        public Builder targetRepo( final RepositoryId val )
        {
            targetRepo = val;
            return this;
        }

        public CopyRequest build()
        {
            return new CopyRequest( this );
        }
    }
}
