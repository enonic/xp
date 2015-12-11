package com.enonic.xp.repo.impl.elasticsearch.document;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.index.document.IndexItems;

public class IndexDocument2
    extends AbstractIndexDocument
{
    private final NodeId id;

    private final IndexItems indexItems;

    private final String analyzer;

    private IndexDocument2( Builder builder )
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
        private NodeId id;

        private IndexItems indexItems;

        private String analyzer;

        private Builder()
        {
        }

        public Builder id( NodeId id )
        {
            this.id = id;
            return this;
        }

        public Builder indexItems( IndexItems indexItems )
        {
            this.indexItems = indexItems;
            return this;
        }

        public Builder analyzer( String analyzer )
        {
            this.analyzer = analyzer;
            return this;
        }

        public IndexDocument2 build()
        {
            return new IndexDocument2( this );
        }
    }
}
