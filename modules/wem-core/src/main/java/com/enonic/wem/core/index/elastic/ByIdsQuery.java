package com.enonic.wem.core.index.elastic;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class ByIdsQuery
    extends AbstractByQuery
{

    private final ImmutableSet<IndexDocumentId> indexDocumentIds;

    @Override
    public int size()
    {
        return indexDocumentIds.size();
    }

    private ByIdsQuery( final Builder builder )
    {
        super( builder );
        this.indexDocumentIds = ImmutableSet.copyOf( builder.indexDocumentIds );
    }

    public ImmutableSet<IndexDocumentId> getIndexDocumentIds()
    {
        return indexDocumentIds;
    }

    public static Builder byIds()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractByQuery.Builder<Builder>
    {
        private Set<IndexDocumentId> indexDocumentIds = Sets.newHashSet();

        public Builder add( final IndexDocumentId indexDocumentId )
        {
            this.indexDocumentIds.add( indexDocumentId );
            return this;
        }

        public ByIdsQuery build()
        {
            return new ByIdsQuery( this );
        }

    }

}
