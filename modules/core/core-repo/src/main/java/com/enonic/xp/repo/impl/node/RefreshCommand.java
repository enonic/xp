package com.enonic.xp.repo.impl.node;

import java.util.Objects;
import java.util.Set;

import org.elasticsearch.index.IndexNotFoundException;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repo.impl.repository.IndexNameResolver;
import com.enonic.xp.repository.IndexException;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.SystemConstants;

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
        final RepositoryId repositoryId =
            Objects.requireNonNullElse( ContextAccessor.current().getRepositoryId(), SystemConstants.SYSTEM_REPO_ID );

        final Set<String> indices = switch ( refreshMode )
        {
            case ALL -> IndexNameResolver.resolveIndexNames( repositoryId );
            case SEARCH -> Set.of( IndexNameResolver.resolveSearchIndexName( repositoryId ) );
            case STORAGE -> Set.of( IndexNameResolver.resolveStorageIndexName( repositoryId ) );
        };

        try
        {
            this.indexServiceInternal.refresh( indices.toArray( String[]::new ) );
        }
        catch ( IndexNotFoundException e )
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
