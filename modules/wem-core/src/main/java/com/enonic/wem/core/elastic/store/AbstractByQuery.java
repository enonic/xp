package com.enonic.wem.core.elastic.store;

import com.enonic.wem.core.index.Index;
import com.enonic.wem.core.index.IndexType;

public abstract class AbstractByQuery
{
    public final static int DEFAULT_MAX_SIZE = 100000;

    private final Index index;

    private final IndexType indexType;

    protected AbstractByQuery( final Builder builder )
    {
        this.index = builder.index;
        this.indexType = builder.indexType;
    }

    public String index()
    {
        return index.getName();
    }

    public String indexType()
    {
        return this.indexType.getName();
    }

    public abstract int size();

    protected static class Builder<B extends Builder>
    {

        private Index index;

        private IndexType indexType;

        @SuppressWarnings("unchecked")
        public B index( final Index index )
        {
            this.index = index;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B indexType( final IndexType indexType )
        {
            this.indexType = indexType;
            return (B) this;
        }


    }


}
