package com.enonic.wem.core.workspace.compare.query;

import com.enonic.wem.api.repository.Repository;

public class AbstractCompareQuery
{
    private Repository repository;

    protected AbstractCompareQuery( Builder builder )
    {
        repository = builder.repository;
    }

    public Repository getRepository()
    {
        return repository;
    }

    public static class Builder<B extends Builder>
    {
        private Repository repository;

        @SuppressWarnings("unchecked")
        public B repository( Repository repository )
        {
            this.repository = repository;
            return (B) this;
        }

        public AbstractCompareQuery build()
        {
            return new AbstractCompareQuery( this );
        }
    }
}
