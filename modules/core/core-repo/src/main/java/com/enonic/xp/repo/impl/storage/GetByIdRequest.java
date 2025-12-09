package com.enonic.xp.repo.impl.storage;

import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.SearchPreference;
import com.enonic.xp.repo.impl.StorageSource;

public class GetByIdRequest
{
    private final String id;

    private final StorageSource storageSource;

    private final SearchPreference searchPreference;

    private final ReturnFields returnFields;

    private final String routing;

    private final int timeout;

    GetByIdRequest( final Builder builder )
    {
        this.id = builder.id;
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

    public int getTimeout()
    {
        return timeout;
    }

    public String getId()
    {
        return id;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private SearchPreference searchPreference;

        private StorageSource storageSource;

        private ReturnFields returnFields;

        private String routing;

        private final int timeout = 5;

        private String id;

        public Builder searchPreference( SearchPreference searchPreference )
        {
            this.searchPreference = searchPreference;
            return this;
        }

        public Builder returnFields( ReturnFields returnFields )
        {
            this.returnFields = returnFields;
            return this;
        }

        public Builder storageSettings( StorageSource storageSource )
        {
            this.storageSource = storageSource;
            return this;
        }

        public Builder routing( String routing )
        {
            this.routing = routing;
            return this;
        }

        private Builder()
        {
        }

        public Builder id( String id )
        {
            this.id = id;
            return this;
        }

        public GetByIdRequest build()
        {
            return new GetByIdRequest( this );
        }
    }
}
