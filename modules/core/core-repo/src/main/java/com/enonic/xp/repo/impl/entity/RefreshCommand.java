package com.enonic.xp.repo.impl.entity;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repo.impl.repository.IndexNameResolver;

public class RefreshCommand
{
    private RefreshMode refreshMode;

    private IndexServiceInternal indexServiceInternal;

    private RefreshCommand( Builder builder )
    {
        refreshMode = builder.refreshMode;
        indexServiceInternal = builder.indexServiceInternal;
    }

    public void execute()
    {
        final Context context = ContextAccessor.current();

        final List<String> indices = Lists.newArrayList();

        if ( refreshMode.equals( RefreshMode.ALL ) )
        {
            indices.add( IndexNameResolver.resolveSearchIndexName( context.getRepositoryId() ) );
            indices.add( IndexNameResolver.resolveStorageIndexName( context.getRepositoryId() ) );
        }
        else if ( refreshMode.equals( RefreshMode.SEARCH ) )
        {
            indices.add( IndexNameResolver.resolveSearchIndexName( context.getRepositoryId() ) );
        }
        else
        {
            indices.add( IndexNameResolver.resolveStorageIndexName( context.getRepositoryId() ) );
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
