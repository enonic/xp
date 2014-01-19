package com.enonic.wem.api.content.page.region;


import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.content.page.PageComponent;

public final class Region
{
    private final String name;

    private final ImmutableList<PageComponent> components;

    public Region( final Builder builder )
    {
        Preconditions.checkNotNull( builder.name, "name cannot be null" );
        this.name = builder.name;
        this.components = builder.components.build();
    }

    public String getName()
    {
        return name;
    }

    public int numberOfComponents()
    {
        return this.components.size();
    }

    public ImmutableList<PageComponent> getComponents()
    {
        return components;
    }

    public static Builder newRegion()
    {
        return new Builder();
    }

    public static Builder newRegion( final Region source )
    {
        return new Builder( source );
    }

    public static class Builder
    {
        private String name;

        private ImmutableList.Builder<PageComponent> components = new ImmutableList.Builder<>();

        public Builder()
        {

        }

        public Builder( final Region source )
        {
            this.name = source.name;
            for ( PageComponent component : source.components )
            {
                this.components.add( component );
            }
        }

        public Builder name( final String value )
        {
            this.name = value;
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
