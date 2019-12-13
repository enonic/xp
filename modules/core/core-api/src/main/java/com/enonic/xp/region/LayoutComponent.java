package com.enonic.xp.region;

import java.util.Objects;

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;

@Beta
@SuppressWarnings("UnusedDeclaration")
public final class LayoutComponent
    extends DescriptorBasedComponent
{
    private static final ComponentName NAME = ComponentName.from( "Fragment" );

    private LayoutRegions regions;

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

    public static <T extends Builder<T>> Builder<T> create()
    {
        return new Builder();
    }

    public static <T extends Builder<T>> Builder<T> create( final LayoutComponent source )
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

    @Override
    public ComponentName getName()
    {
        return NAME;
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

    public static class Builder<T extends Builder<T>>
        extends DescriptorBasedComponent.Builder<T>
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

        public T regions( final LayoutRegions value )
        {
            this.regions = value;
            return (T) this;
        }

        public LayoutComponent build()
        {
            return new LayoutComponent( this );
        }
    }
}
