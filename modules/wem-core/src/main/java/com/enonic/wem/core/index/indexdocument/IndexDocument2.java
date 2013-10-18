package com.enonic.wem.core.index.indexdocument;


import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.item.ItemId;
import com.enonic.wem.core.index.IndexType;

public class IndexDocument2
{

    private final ItemId id;

    private final IndexType indexType;

    private final String index;

    private final Set<AbstractIndexDocumentItem> indexDocumentEntries;

    private boolean refreshOnStore = false;

    private String analyzerName;

    private IndexDocument2( final Builder builder )
    {
        this.id = builder.id;
        this.indexType = builder.indexType;
        this.index = builder.index;
        this.indexDocumentEntries = builder.indexDocumentEntries;
    }

    public static Builder newIndexDocument()
    {
        return new Builder();
    }

    public String getId()
    {
        return id.toString();
    }

    public IndexType getIndexType()
    {
        return indexType;
    }

    public String getIndex()
    {
        return index;
    }

    public Set<AbstractIndexDocumentItem> getIndexDocumentEntries()
    {
        return indexDocumentEntries;
    }

    public boolean doRefreshOnStore()
    {
        return refreshOnStore;
    }

    public void setRefreshOnStore( final boolean refreshOnStore )
    {
        this.refreshOnStore = refreshOnStore;
    }

    public void setAnalyzerName( final String analyzerName )
    {
        this.analyzerName = analyzerName;
    }

    public static class Builder
    {
        private ItemId id;

        private IndexType indexType;

        private String index;

        private String analyzer;

        private Set<AbstractIndexDocumentItem> indexDocumentEntries = Sets.newHashSet();

        public Builder()
        {
        }


        public Builder setId( final ItemId id )
        {
            this.id = id;
            return this;
        }

        public Builder setIndexType( final IndexType indexType )
        {
            this.indexType = indexType;
            return this;
        }

        public Builder setIndex( final String index )
        {
            this.index = index;
            return this;
        }

        public Builder setAnalyzer( final String analyzer )
        {
            this.analyzer = analyzer;
            return this;
        }

        public Builder addEntry( final AbstractIndexDocumentItem entry )
        {
            this.indexDocumentEntries.add( entry );

            return this;
        }

        public Builder addEntries( final Set<AbstractIndexDocumentItem> entries )
        {
            this.indexDocumentEntries.addAll( entries );
            return this;
        }

        public IndexDocument2 build()
        {
            return new IndexDocument2( this );
        }
    }

}

