package com.enonic.xp.region;

import java.util.Objects;

import com.google.common.base.MoreObjects;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.descriptor.DescriptorKey;

@PublicApi
@SuppressWarnings("UnusedDeclaration")
public final class LayoutComponent
    extends DescriptorBasedComponent
{
    private final LayoutRegions regions;

    public LayoutComponent( final Builder builder )
    {
        super( builder );
        if ( builder.regions == null )
        {
            this.regions = LayoutRegions.create().build();
        }
        else
        {
            this.regions = builder.regions;
        }

        for ( final Region region : this.regions )
        {
            region.setParent( this );
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final LayoutComponent source )
    {
        return new Builder( source );
    }

    @Override
    public LayoutComponent copy()
    {
        return create( this ).build();
    }

    @Override
    public ComponentType getType()
    {
        return LayoutComponentType.INSTANCE;
    }

    public boolean hasRegions()
    {
        return regions != null;
    }

    public Region getRegion( final String name )
    {
        return this.regions.getRegion( name );
    }

    public LayoutRegions getRegions()
    {
        return regions;
    }

    public Component getComponent( final ComponentPath path )
    {
        return regions.getComponent( path );
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof LayoutComponent ) )
        {
            return false;
        }
        if ( !super.equals( o ) )
        {
            return false;
        }

        final LayoutComponent that = (LayoutComponent) o;

        return regions.equals( that.regions );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), regions );
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).
            add( "type", getType() ).
            add( "path", getPath() ).
            add( "regions", getRegions() ).
            toString();
    }

    public static class Builder
        extends DescriptorBasedComponent.Builder
    {
        private LayoutRegions regions;

        private Builder()
        {

        }

        private Builder( final LayoutComponent source )
        {
            super( source );
            regions = source.regions.copy();
        }

        public Builder descriptor( String value )
        {
            this.descriptor = DescriptorKey.from( value );
            return this;
        }

        @Override
        public Builder descriptor( DescriptorKey value )
        {
            super.descriptor( value );
            return this;
        }

        @Override
        public Builder config( final PropertyTree config )
        {
            this.config = config;
            return this;
        }

        public Builder regions( final LayoutRegions value )
        {
            this.regions = value;
            return this;
        }

        public LayoutComponent build()
        {
            return new LayoutComponent( this );
        }
    }
}
