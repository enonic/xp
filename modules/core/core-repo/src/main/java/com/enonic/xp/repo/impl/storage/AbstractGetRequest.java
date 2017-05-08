package com.enonic.xp.repo.impl.storage;

import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.SearchPreference;
import com.enonic.xp.repo.impl.StorageSource;

public abstract class AbstractGetRequest
{
    private final StorageSource storageSource;

    private final SearchPreference searchPreference;

    private final ReturnFields returnFields;

    private final String routing;

    private final int timeout;

    AbstractGetRequest( final Builder builder )
    {
        this.searchPreference = builder.searchPreference;
        this.storageSource = builder.storageSource;
        this.returnFields = builder.returnFields;
        this.routing = builder.routing;
        this.timeout = builder.timeout;
    }

    public StorageSource getStorageSource()
    {
        return storageSource;
    }

    public SearchPreference getSearchPreference()
    {
        return searchPreference;
    }

    public ReturnFields getReturnFields()
    {
        return returnFields;
    }

    public String getRouting()
    {
        return routing;
    }

    public String getTimeout()
    {
        return timeout + "s";
    }

    public static class Builder<B extends Builder>
    {
        private SearchPreference searchPreference = SearchPreference.LOCAL;

        private StorageSource storageSource;

        private ReturnFields returnFields;

        private String routing;

        private int timeout = 5;

        @SuppressWarnings("unchecked")
        public B searchPreference( SearchPreference searchPreference )
        {
            this.searchPreference = searchPreference;
            return (B) this;
        }


        @SuppressWarnings("unchecked")
        public B returnFields( ReturnFields returnFields )
        {
            this.returnFields = returnFields;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B storageSettings( StorageSource storageSource )
        {
            this.storageSource = storageSource;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B routing( String routing )
        {
            this.routing = routing;
            return (B) this;
        }
    }
}
