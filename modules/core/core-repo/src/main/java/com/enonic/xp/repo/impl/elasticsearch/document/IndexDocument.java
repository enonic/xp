package com.enonic.xp.repo.impl.elasticsearch.document;

import com.enonic.xp.repo.impl.elasticsearch.document.indexitem.IndexItems;

public class IndexDocument
    extends AbstractIndexDocument
{
    private final String id;

    private final IndexItems indexItems;

    private final String analyzer;

    public String getId()
    {
        return id;
    }

    public IndexItems getIndexItems()
    {
        return indexItems;
    }

    public String getAnalyzer()
    {
        return analyzer;
    }

    private IndexDocument( Builder builder )
    {
        super( builder );
        id = builder.id;
        indexItems = builder.indexItems;
        analyzer = builder.analyzer;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static class Builder
        extends AbstractIndexDocument.Builder<Builder>
    {
        private String id;

        private IndexItems indexItems;

        private String analyzer;

        private Builder()
        {
        }

        public Builder id( final String id )
        {
            this.id = id;
            return this;
        }

        public Builder indexItems( final IndexItems indexItems )
        {
            this.indexItems = indexItems;
            return this;
        }

        public Builder analyzer( final String analyzer )
        {
            this.analyzer = analyzer;
            return this;
        }

        public IndexDocument build()
        {
            return new IndexDocument( this );
        }
    }
}
