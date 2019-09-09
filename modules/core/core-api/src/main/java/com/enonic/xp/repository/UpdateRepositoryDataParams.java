package com.enonic.xp.repository;

import java.util.Optional;

public final class UpdateRepositoryDataParams
{
    private final RepositoryData data;

    private UpdateRepositoryDataParams( final Builder builder )
    {
        this.data = Optional.ofNullable( builder.data ).orElse( RepositoryData.empty() );
    }

    public RepositoryData getData()
    {
        return data;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private RepositoryData data;

        private Builder()
        {
        }


        public Builder data( final RepositoryData data )
        {
            this.data = data;
            return this;
        }

        public UpdateRepositoryDataParams build()
        {
            return new UpdateRepositoryDataParams( this );
        }
    }
}
