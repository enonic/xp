package com.enonic.wem.api.content.page.layout;

import java.util.Objects;

import com.enonic.wem.api.content.page.Component;
import com.enonic.wem.api.content.page.ComponentName;
import com.enonic.wem.api.content.page.ComponentPath;
import com.enonic.wem.api.content.page.ComponentType;
import com.enonic.wem.api.content.page.DescriptorBasedComponent;
import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.api.content.page.region.RegionPlaceableComponent;
import com.enonic.wem.api.data.PropertyTree;

@SuppressWarnings("UnusedDeclaration")
public final class LayoutComponent
    extends DescriptorBasedComponent<LayoutDescriptorKey>
    implements RegionPlaceableComponent
{
    private LayoutRegions regions;

    public LayoutComponent( final Builder builder )
    {
        super( builder );
        if ( builder.regions == null )
        {
            this.regions = LayoutRegions.newLayoutRegions().build();
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

    public static Builder newLayoutComponent()
    {
        return new Builder();
    }

    public static Builder newLayoutComponent( final LayoutComponent source )
    {
        return new Builder( source );
    }

    public Component copy()
    {
        return newLayoutComponent( this ).build();
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

        if ( !regions.equals( that.regions ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), regions );
    }

    public static class Builder
        extends DescriptorBasedComponent.Builder<LayoutDescriptorKey>
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

        public Builder name( ComponentName value )
        {
            this.name = value;
            return this;
        }

        public Builder name( String value )
        {
            this.name = new ComponentName( value );
            return this;
        }

        public Builder descriptor( String value )
        {
            this.descrpitor = LayoutDescriptorKey.from( value );
            return this;
        }

        public Builder descriptor( LayoutDescriptorKey value )
        {
            this.descrpitor = value;
            return this;
        }

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
