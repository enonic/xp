package com.enonic.wem.api.content.page.region;


import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.layout.LayoutComponent;

public class Region
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
            pageComponent.setParent( this );
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

    public String getName()
    {
        return name;
    }

    public void setParent( LayoutComponent parent )
    {
        this.parent = parent;
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
                this.components.add( component );
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
