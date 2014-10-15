package com.enonic.wem.core.elasticsearch.document;


import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.core.entity.EntityId;
import com.enonic.wem.core.index.IndexType;

public class StoreDocument
{
    private final EntityId id;

    private final String indexTypeName;

    private final String indexName;

    private final ImmutableSet<AbstractStoreDocumentItem> indexDocumentItems;

    private final boolean refreshOnStore;

    private final String analyzer;

    private StoreDocument( final Builder builder )
    {
        this.id = builder.id;
        this.indexTypeName = builder.indexTypeName;
        this.indexName = builder.indexName;
        this.indexDocumentItems = ImmutableSet.copyOf( builder.indexDocumentEntries );
        this.analyzer = builder.analyzer;
        this.refreshOnStore = builder.refreshOnStore;
    }

    public static Builder newIndexDocument()
    {
        return new Builder();
    }

    public String getId()
    {
        return id.toString();
    }

    public String getIndexTypeName()
    {
        return indexTypeName;
    }

    public String getIndexName()
    {
        return indexName;
    }

    public Set<AbstractStoreDocumentItem> getIndexDocumentItems()
    {
        return indexDocumentItems;
    }

    public boolean doRefreshOnStore()
    {
        return refreshOnStore;
    }

    public String getAnalyzer()
    {
        return analyzer;
    }

    public static class Builder
    {
        private EntityId id;

        private String indexTypeName;

        private String indexName;

        private String analyzer;

        private boolean refreshOnStore = true;

        private final Set<AbstractStoreDocumentItem> indexDocumentEntries;

        public Builder()
        {
            indexDocumentEntries = Sets.newHashSet();
        }

        public Builder id( final EntityId id )
        {
            this.id = id;
            return this;
        }

        public Builder refreshOnStore( final boolean refreshOnStore )
        {
            this.refreshOnStore = refreshOnStore;
            return this;
        }

        public Builder indexType( final IndexType indexType )
        {
            this.indexTypeName = indexType.getName();
            return this;
        }

        public Builder indexType( final String indexTypeName )
        {
            this.indexTypeName = indexTypeName;
            return this;
        }

        public Builder index( final String indexName )
        {
            this.indexName = indexName;
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

