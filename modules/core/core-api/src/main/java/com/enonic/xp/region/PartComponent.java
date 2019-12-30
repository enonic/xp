package com.enonic.xp.region;


import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.DescriptorKey;

@PublicApi
public final class PartComponent
    extends DescriptorBasedComponent
{
    public PartComponent( final Builder builder )
    {
        super( builder );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final PartComponent source )
    {
        return new Builder( source );
    }

    @Override
    public Component copy()
    {
        return create( this ).build();
    }

    @Override
    public ComponentType getType()
    {
        return PartComponentType.INSTANCE;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof PartComponent ) )
        {
            return false;
        }

        return super.equals( o );
    }

    public static class Builder
        extends DescriptorBasedComponent.Builder
    {
        private Builder()
        {
            // Default
        }

        private Builder( final PartComponent source )
        {
            super( source );
        }

        @Override
        public Builder name( ComponentName value )
        {
            this.name = value;
            return this;
        }

        public Builder name( String value )
        {
            this.name = value != null ? new ComponentName( value ) : null;
            return this;
        }

        @Override
        public Builder descriptor( DescriptorKey value )
        {
            this.descriptor = value;
            return this;
        }

        public Builder descriptor( String value )
        {
            this.descriptor = DescriptorKey.from( value );
            return this;
        }

        @Override
        public Builder config( final PropertyTree config )
        {
            this.config = config;
            return this;
        }

        public PartComponent build()
        {
            return new PartComponent( this );
        }
    }
}
