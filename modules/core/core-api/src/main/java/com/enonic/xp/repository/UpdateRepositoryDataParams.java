package com.enonic.xp.repository;

import java.util.Objects;
import java.util.Optional;

import com.google.common.base.Preconditions;

public final class UpdateRepositoryDataParams
{
    private final RepositoryId repositoryId;

    private final RepositoryData data;

    private UpdateRepositoryDataParams( final Builder builder )
    {
        repositoryId = builder.repositoryId;
        this.data = Optional.ofNullable( builder.data ).orElse( RepositoryData.empty() );
    }

    public RepositoryData getData()
    {
        return data;
    }


    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }

        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        UpdateRepositoryDataParams that = (UpdateRepositoryDataParams) o;
        return Objects.equals( repositoryId, that.repositoryId ) &&
                Objects.equals( data, that.data );
    }

    @Override
    public int hashCode() {
        return Objects.hash( repositoryId, data );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private RepositoryId repositoryId;
        private RepositoryData data;

        private Builder()
        {
        }

        public Builder repositoryId( final RepositoryId repositoryId )
        {
            this.repositoryId = repositoryId;
            return this;
        }

        public Builder data( final RepositoryData data )
        {
            this.data = data;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( repositoryId, "repositoryId cannot be null" );
        }


        public UpdateRepositoryDataParams build()
        {
            validate();
            return new UpdateRepositoryDataParams( this );
        }
    }
}
