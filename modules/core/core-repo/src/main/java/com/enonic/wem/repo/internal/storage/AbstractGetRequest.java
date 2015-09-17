package com.enonic.wem.repo.internal.storage;

public abstract class AbstractGetRequest
{
    protected final StorageSettings storageSettings;

    protected final SearchPreference searchPreference;

    protected final ReturnFields returnFields;

    protected final String routing;

    protected final int timeout;

    protected AbstractGetRequest( final Builder builder )
    {
        this.searchPreference = builder.searchPreference;
        this.storageSettings = builder.storageSettings;
        this.returnFields = builder.returnFields;
        this.routing = builder.routing;
        this.timeout = builder.timeout;
    }

    public StorageSettings getStorageSettings()
    {
        return storageSettings;
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

        private StorageSettings storageSettings;

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
        public B storageSettings( StorageSettings storageSettings )
        {
            this.storageSettings = storageSettings;
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
