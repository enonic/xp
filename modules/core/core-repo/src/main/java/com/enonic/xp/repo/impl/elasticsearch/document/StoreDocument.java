package com.enonic.xp.repo.impl.elasticsearch.document;


import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.xp.node.NodeId;

public class StoreDocument
    extends AbstractIndexDocument
{
    private final NodeId id;

    private final ImmutableSet<AbstractStoreDocumentItem> indexDocumentItems;

    private final String analyzer;

    private StoreDocument( final Builder builder )
    {
        super( builder );
        this.id = builder.id;
        this.indexDocumentItems = ImmutableSet.copyOf( builder.indexDocumentEntries );
        this.analyzer = builder.analyzer;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public String getId()
    {
        return id.toString();
    }

    public Set<AbstractStoreDocumentItem> getStoreDocumentItems()
    {
        return indexDocumentItems;
    }

    public String getAnalyzer()
    {
        return analyzer;
    }

    public static class Builder
        extends AbstractIndexDocument.Builder<Builder>
    {
        private NodeId id;

        private String analyzer;

        private final Set<AbstractStoreDocumentItem> indexDocumentEntries;

        public Builder()
        {
            indexDocumentEntries = Sets.newHashSet();
        }

        public Builder id( final NodeId id )
        {
            this.id = id;
            return this;
        }

        public Builder analyzer( final String analyzer )
        {
            this.analyzer = analyzer;
            return this;
        }

        public Builder addEntry( final AbstractStoreDocumentItem entry )
        {
            this.indexDocumentEntries.add( entry );

            return this;
        }

        public Builder addEntries( final Set<AbstractStoreDocumentItem> entries )
        {
            this.indexDocumentEntries.addAll( entries );
            return this;
        }

        public StoreDocument build()
        {
            return new StoreDocument( this );
        }
    }

}

