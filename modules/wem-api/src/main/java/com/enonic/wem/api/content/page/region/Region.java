package com.enonic.wem.api.content.page.region;


import java.util.Objects;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.layout.LayoutComponent;

public final class Region
{
    private final String name;

    private final ImmutableList<PageComponent> pageComponents;

    private LayoutComponent parent;

    public Region( final Builder builder )
    {
        Preconditions.checkNotNull( builder.name, "name cannot be null" );
        this.name = builder.name;
        this.parent = builder.parent;
        this.pageComponents = builder.components.build();

        for ( final PageComponent pageComponent : this.pageComponents )
        {
            boolean layoutComponentWithinLayoutComponent = this.parent != null && pageComponent instanceof LayoutComponent;
            if ( layoutComponentWithinLayoutComponent )
            {
                throw new IllegalArgumentException( "Having a LayoutComponent within a LayoutComponent is not allowed" );
            }
            pageComponent.setRegion( this );
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
        for ( final PageComponent pageComponent : this.pageComponents )
        {
            boolean layoutComponentWithinLayoutComponent = this.parent != null && pageComponent instanceof LayoutComponent;
            if ( layoutComponentWithinLayoutComponent )
            {
                throw new IllegalArgumentException( "Having a LayoutComponent within a LayoutComponent is not allowed" );
            }
        }
    }

    public int getIndex( final PageComponent pageComponent )
    {
        for ( int i = 0; i < pageComponents.size(); i++ )
        {
            if ( pageComponent == pageComponents.get( i ) )
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

    public PageComponent getComponent( final int index )
    {
        if ( index >= this.pageComponents.size() )
        {
            return null;
        }
        return this.pageComponents.get( index );
    }

    public int numberOfComponents()
    {
        return this.pageComponents.size();
    }

    public ImmutableList<PageComponent> getComponents()
    {
        return pageComponents;
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

        return Objects.equals( name, region.name ) && Objects.equals( pageComponents, region.pageComponents );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name, pageComponents );
    }

    public static class Builder
    {
        private String name;

        private LayoutComponent parent;

        private ImmutableList.Builder<PageComponent> components = new ImmutableList.Builder<>();

        public Builder()
        {

        }

        public Builder( final Region source )
        {
            this.name = source.name;
            for ( final PageComponent component : source.pageComponents )
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

        public Builder add( final PageComponent component )
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
