package com.enonic.xp.region;


import com.google.common.annotations.Beta;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.DescriptorKey;

@Beta
public final class PartComponent
    extends DescriptorBasedComponent
{
    private static final ComponentName NAME = ComponentName.from( "Part" );

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

    @Deprecated
    @Override
    public ComponentName getName()
    {
        return NAME;
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
        }

        private Builder( final PartComponent source )
        {
            super( source );
        }

        @Deprecated
        @Override
        public Builder name( ComponentName value )
        {
            return this;
        }

        @Deprecated
        public Builder name( String value )
        {
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
