package com.enonic.xp.security;

import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.PropertyTree;

@PublicApi
public class ServiceAccount
    extends Principal
{

    private final PropertyTree data;

    private ServiceAccount( final Builder builder )
    {
        super( builder );

        this.data = builder.data;
    }

    public PropertyTree getData()
    {
        return data;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof ServiceAccount ) )
        {
            return false;
        }
        if ( !super.equals( o ) )
        {
            return false;
        }

        final ServiceAccount that = (ServiceAccount) o;

        return Objects.equals( data, that.data );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), data );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final ServiceAccount serviceAccount )
    {
        return new Builder( serviceAccount );
    }

    public static class Builder
        extends Principal.Builder<Builder>
    {
        private PropertyTree data;

        private Builder()
        {
            super();
            this.data = new PropertyTree();
        }

        private Builder( final ServiceAccount serviceAccount )
        {
            super( serviceAccount );
            this.data = serviceAccount.getData();
        }

        public Builder data( final PropertyTree data )
        {
            this.data = data;
            return this;
        }

        public ServiceAccount build()
        {
            validate();

            return new ServiceAccount( this );
        }
    }
}
