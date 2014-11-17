package com.enonic.wem.repo.internal.elasticsearch.document;

abstract class AbstractIndexDocument
{
    private final String indexTypeName;

    private final String indexName;

    private final boolean refreshAfterOperation;

    AbstractIndexDocument( final Builder builder )
    {
        indexTypeName = builder.indexTypeName;
        indexName = builder.indexName;
        refreshAfterOperation = builder.refreshAfterOperation;
    }

    public String getIndexTypeName()
    {
        return indexTypeName;
    }

    public String getIndexName()
    {
        return indexName;
    }

    public boolean isRefreshAfterOperation()
    {
        return refreshAfterOperation;
    }

    public static class Builder<T>
    {
        private String indexTypeName;

        private String indexName;

        private boolean refreshAfterOperation;

        @SuppressWarnings("unchecked")
        public T indexTypeName( final String indexTypeName )
        {
            this.indexTypeName = indexTypeName;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T indexName( final String indexName )
        {
            this.indexName = indexName;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T refreshAfterOperation( final boolean refreshAfterOperation )
        {
            this.refreshAfterOperation = refreshAfterOperation;
            return (T) this;
        }

    }
}
