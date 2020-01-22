package com.enonic.xp.repo.impl.node;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repo.impl.repository.IndexNameResolver;
import com.enonic.xp.repository.IndexException;
import com.enonic.xp.repository.RepositoryId;

public class RefreshCommand
{
    private final RefreshMode refreshMode;

    private final IndexServiceInternal indexServiceInternal;

    private RefreshCommand( Builder builder )
    {
        refreshMode = builder.refreshMode;
        indexServiceInternal = builder.indexServiceInternal;
    }

    public void execute()
    {
        final RepositoryId repositoryId = ContextAccessor.current().getRepositoryId();

        if ( !indexServiceInternal.indicesExists( IndexNameResolver.resolveStorageIndexName( repositoryId ),
                                                  IndexNameResolver.resolveCommitIndexName( repositoryId ) ) )
        {
            throw new IndexException( "Cannot refresh index, index for repository [" + repositoryId + "] does not exist" );
        }

        final List<String> indices = new ArrayList<>();

        if ( RefreshMode.ALL == refreshMode || RefreshMode.STORAGE == refreshMode )
        {
            if ( RefreshMode.ALL == refreshMode )
            {
                indices.add( IndexNameResolver.resolveSearchIndexName( repositoryId, ContextAccessor.current().getBranch() ) );
            }

            indices.add( IndexNameResolver.resolveStorageIndexName( repositoryId ) );
            indices.add( IndexNameResolver.resolveCommitIndexName( repositoryId ) );
        }
        else if ( refreshMode.equals( RefreshMode.SEARCH ) )
        {
            indices.add( IndexNameResolver.resolveSearchIndexName( repositoryId, ContextAccessor.current().getBranch() ) );
        }

        this.indexServiceInternal.refresh( indices.toArray( new String[indices.size()] ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private RefreshMode refreshMode;

        private IndexServiceInternal indexServiceInternal;

        private Builder()
        {
        }

        public Builder refreshMode( RefreshMode refreshMode )
        {
            this.refreshMode = refreshMode;
            return this;
        }

        public Builder indexServiceInternal( IndexServiceInternal indexServiceInternal )
        {
            this.indexServiceInternal = indexServiceInternal;
            return this;
        }

        public RefreshCommand build()
        {
            return new RefreshCommand( this );
        }
    }
}
