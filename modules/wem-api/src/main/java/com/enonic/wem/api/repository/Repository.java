package com.enonic.wem.api.repository;

public class Repository
{
    private final RepositoryId id;

    private Repository( Builder builder )
    {
        id = builder.id;
    }

    public RepositoryId getId()
    {
        return id;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final Repository that = (Repository) o;

        if ( id != null ? !id.equals( that.id ) : that.id != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return id != null ? id.hashCode() : 0;
    }

    public static final class Builder
    {
        private RepositoryId id;

        private Builder()
        {
        }

        public Builder id( RepositoryId id )
        {
            this.id = id;
            return this;
        }

        public Repository build()
        {
            return new Repository( this );
        }
    }
}
