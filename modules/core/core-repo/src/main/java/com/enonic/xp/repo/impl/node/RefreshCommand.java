package com.enonic.xp.repo.impl.node;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.repository.IndexException;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.storage.spi.NodeSearchIndex;
import com.enonic.xp.storage.spi.RepositoryStorageAdmin;
import com.enonic.xp.storage.spi.StorageIndexNotFoundException;

import static java.util.Objects.requireNonNullElse;

public class RefreshCommand
{
    private final RefreshMode refreshMode;

    private final RepositoryStorageAdmin repositoryStorageAdmin;

    private final NodeSearchIndex nodeSearchIndex;

    private RefreshCommand( Builder builder )
    {
        refreshMode = builder.refreshMode;
        repositoryStorageAdmin = builder.repositoryStorageAdmin;
        nodeSearchIndex = builder.nodeSearchIndex;
    }

    public void execute()
    {
        final RepositoryId repositoryId =
            requireNonNullElse( ContextAccessor.current().getRepositoryId(), SystemConstants.SYSTEM_REPO_ID );

        try
        {
            switch ( refreshMode )
            {
                case ALL:
                    repositoryStorageAdmin.refresh( repositoryId );
                    nodeSearchIndex.refresh( repositoryId );
                    break;
                case SEARCH:
                    nodeSearchIndex.refresh( repositoryId );
                    break;
                case STORAGE:
                    repositoryStorageAdmin.refresh( repositoryId );
                    break;
            }
        }
        catch ( StorageIndexNotFoundException e )
        {
            throw new IndexException( "Cannot refresh index, index for repository [" + repositoryId + "] does not exist", e );
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private RefreshMode refreshMode;

        private RepositoryStorageAdmin repositoryStorageAdmin;

        private NodeSearchIndex nodeSearchIndex;

        private Builder()
        {
        }

        public Builder refreshMode( RefreshMode refreshMode )
        {
            this.refreshMode = refreshMode;
            return this;
        }

        public Builder repositoryStorageAdmin( RepositoryStorageAdmin repositoryStorageAdmin )
        {
            this.repositoryStorageAdmin = repositoryStorageAdmin;
            return this;
        }

        public Builder nodeSearchIndex( NodeSearchIndex nodeSearchIndex )
        {
            this.nodeSearchIndex = nodeSearchIndex;
            return this;
        }

        public RefreshCommand build()
        {
            return new RefreshCommand( this );
        }
    }
}
