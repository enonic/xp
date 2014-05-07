package com.enonic.wem.core.elastic.store;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.core.elastic.DocumentId;

public class ByIdsQuery
    extends AbstractByQuery
{

    private final ImmutableSet<DocumentId> documentIds;

    @Override
    public int size()
    {
        return documentIds.size();
    }

    private ByIdsQuery( final Builder builder )
    {
        super( builder );
        this.documentIds = ImmutableSet.copyOf( builder.documentIds );
    }

    public ImmutableSet<DocumentId> getDocumentIds()
    {
        return documentIds;
    }

    public static Builder byIds()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractByQuery.Builder<Builder>
    {
        private Set<DocumentId> documentIds = Sets.newHashSet();

        public Builder add( final DocumentId documentId )
        {
            this.documentIds.add( documentId );
            return this;
        }

        public ByIdsQuery build()
        {
            return new ByIdsQuery( this );
        }

    }

}
