package com.enonic.wem.api.content.page.region;


import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;

import com.enonic.wem.api.content.page.ComponentName;
import com.enonic.wem.api.content.page.ComponentPath;
import com.enonic.wem.api.content.page.PageComponent;

public class Region
{
    private final String name;

    private final ImmutableMap<ComponentName, PageComponent> componentByName;

    private RegionPath regionPath;

    public Region( final Builder builder )
    {
        Preconditions.checkNotNull( builder.name, "name cannot be null" );
        this.name = builder.name;
        this.componentByName = builder.components.build();
        this.regionPath = builder.regionPath;
    }

    public String getName()
    {
        return name;
    }

    public RegionPath getRegionPath()
    {
        return regionPath;
    }

    public PageComponent getComponent( final ComponentName componentName )
    {
        return this.componentByName.get( componentName );
    }

    public int numberOfComponents()
    {
        return this.componentByName.size();
    }

    public ImmutableCollection<PageComponent> getComponents()
    {
        return componentByName.values();
    }

    public static Builder newRegion()
    {
        return new Builder();
    }

    public static Builder newRegion( final Region source )
    {
        return new Builder( source );
    }

    public void applyComponentPaths( final ComponentPath parent )
    {
        this.regionPath = RegionPath.from( parent, this.name );
        for ( final PageComponent component : this.getComponents() )
        {
            final ComponentPath.RegionAndComponent regionAndComponent =
                ComponentPath.RegionAndComponent.from( this.getName(), component.getName() );

            final ComponentPath componentPath =
                parent != null ? ComponentPath.from( parent, regionAndComponent ) : ComponentPath.from( regionAndComponent );
            component.setPath( componentPath );
        }
    }

    public static class Builder
    {
        private String name;

        private ImmutableMap.Builder<ComponentName, PageComponent> components = new ImmutableMap.Builder<>();

        private RegionPath regionPath;

        public Builder()
        {

        }

        public Builder( final Region source )
        {
            this.name = source.name;
            for ( final PageComponent component : source.componentByName.values() )
            {
                this.components.put( component.getName(), component );
            }
            this.regionPath = source.regionPath;
        }

        public Builder name( final String value )
        {
            this.name = value;
            return this;
        }

        public Builder regionPath( final RegionPath regionPath )
        {
            this.regionPath = regionPath;
            return this;
        }

        public Builder add( final PageComponent component )
        {
            this.components.put( component.getName(), component );
            return this;
        }

        public Region build()
        {
            return new Region( this );
        }

    }
}
