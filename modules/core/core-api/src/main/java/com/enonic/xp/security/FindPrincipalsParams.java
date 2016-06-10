package com.enonic.xp.security;

import java.util.List;

import com.google.common.annotations.Beta;

@Beta
public final class FindPrincipalsParams
{
    private UserStoreKey userStoreKey;

    private List<PrincipalType> types;

    private String query;

    private Integer from;

    private Integer size;


    private FindPrincipalsParams( final Builder builder )
    {
        this.userStoreKey = builder.userStoreKey;
        this.types = builder.types;
        this.query = builder.query;
        this.from = builder.from;
        this.size = builder.size;
    }

    public UserStoreKey getUserStoreKey()
    {
        return userStoreKey;
    }

    public List<PrincipalType> getTypes()
    {
        return types;
    }

    public String getQuery()
    {
        return query;
    }

    public Integer getFrom()
    {
        return from;
    }

    public Integer getSize()
    {
        return size;
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

        final FindPrincipalsParams that = (FindPrincipalsParams) o;

        if ( userStoreKey != null ? !userStoreKey.equals( that.userStoreKey ) : that.userStoreKey != null )
        {
            return false;
        }
        if ( types != null ? !types.equals( that.types ) : that.types != null )
        {
            return false;
        }
        if ( query != null ? !query.equals( that.query ) : that.query != null )
        {
            return false;
        }
        if ( from != null ? !from.equals( that.from ) : that.from != null )
        {
            return false;
        }
        return !( size != null ? !size.equals( that.size ) : that.size != null );

    }

    @Override
    public int hashCode()
    {
        int result = userStoreKey != null ? userStoreKey.hashCode() : 0;
        result = 31 * result + ( types != null ? types.hashCode() : 0 );
        result = 31 * result + ( query != null ? query.hashCode() : 0 );
        result = 31 * result + ( from != null ? from.hashCode() : 0 );
        result = 31 * result + ( size != null ? size.hashCode() : 0 );
        return result;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private UserStoreKey userStoreKey;

        private List<PrincipalType> types;

        private String query;

        private Integer from;

        private Integer size;

        private Builder()
        {
        }

        public Builder userStoreKey( final UserStoreKey value )
        {
            this.userStoreKey = value;
            return this;
        }

        public Builder types( final List<PrincipalType> value )
        {
            this.types = value;
            return this;
        }

        public Builder query( final String value )
        {
            this.query = value;
            return this;
        }

        public Builder from( final Integer value )
        {
            this.from = value;
            return this;
        }

        public Builder size( final Integer value )
        {
            this.size = value;
            return this;
        }

        public FindPrincipalsParams build()
        {
            return new FindPrincipalsParams( this );
        }
    }
}
