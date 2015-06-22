package com.enonic.xp.region;


import java.util.Objects;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

@Beta
public final class Region
{
    private final String name;

    private final ImmutableList<Component> components;

    private LayoutComponent parent;

    public Region( final Builder builder )
    {
        Preconditions.checkNotNull( builder.name, "name cannot be null" );
        this.name = builder.name;
        this.parent = builder.parent;
        this.components = builder.components.build();

        for ( final Component component : this.components )
        {
            boolean layoutComponentWithinLayoutComponent = this.parent != null && component instanceof LayoutComponent;
            if ( layoutComponentWithinLayoutComponent )
            {
                throw new IllegalArgumentException( "Having a LayoutComponent within a LayoutComponent is not allowed" );
            }
            component.setRegion( this );
        }
    }

    public static Builder newRegion()
    {
        return new Builder();
    }

    public static Builder newRegion( final Region source )
    {
        return new Builder( source );
    }

    public Region copy()
    {
        return Region.newRegion( this ).build();
    }

    public String getName()
    {
        return name;
    }

    public void setParent( LayoutComponent parent )
    {
        this.parent = parent;
        for ( final Component component : this.components )
        {
            boolean layoutComponentWithinLayoutComponent = this.parent != null && component instanceof LayoutComponent;
            if ( layoutComponentWithinLayoutComponent )
            {
                throw new IllegalArgumentException( "Having a LayoutComponent within a LayoutComponent is not allowed" );
            }
        }
    }

    public int getIndex( final Component component )
    {
        for ( int i = 0; i < components.size(); i++ )
        {
            if ( component == components.get( i ) )
            {
                return i;
            }
        }
        return -1;
    }

    public RegionPath getRegionPath()
    {
        return RegionPath.from( parent != null ? parent.getPath() : null, name );
    }

    public Component getComponent( final int index )
    {
        if ( index >= this.components.size() )
        {
            return null;
        }
        return this.components.get( index );
    }

    public int numberOfComponents()
    {
        return this.components.size();
    }

    public ImmutableList<Component> getComponents()
    {
        return components;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof Region ) )
        {
            return false;
        }

        final Region region = (Region) o;

        return Objects.equals( name, region.name ) && Objects.equals( components, region.components );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name, components );
    }

    public static class Builder
    {
        private String name;

        private LayoutComponent parent;

        private ImmutableList.Builder<Component> components = new ImmutableList.Builder<>();

        public Builder()
        {

        }

        public Builder( final Region source )
        {
            this.name = source.name;
            for ( final Component component : source.components )
            {
                this.components.add( component.copy() );
            }
            this.parent = source.parent;
        }

        public Builder name( final String value )
        {
            this.name = value;
            return this;
        }

        public Builder parent( final LayoutComponent value )
        {
            this.parent = value;
            return this;
        }

        public Builder add( final Component component )
        {
            this.components.add( component );
            return this;
        }

        public Region build()
        {
            return new Region( this );
        }

    }
}
