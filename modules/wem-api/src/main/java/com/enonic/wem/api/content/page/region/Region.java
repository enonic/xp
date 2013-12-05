package com.enonic.wem.api.content.page.region;


import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;

public class Region
{
    private final String name;

    private final ImmutableList<RegionPlaceableComponent> components;

    public Region( final Builder builder )
    {
        this.name = builder.name;
        this.components = builder.components.build();
    }

    public String getName()
    {
        return name;
    }

    public ImmutableList<RegionPlaceableComponent> getComponents()
    {
        return components;
    }

    public RootDataSet toData()
    {
        final RootDataSet data = new RootDataSet();
        data.setProperty( "name", new Value.String( getName() ) );
        for ( final RegionPlaceableComponent component : getComponents() )
        {
            final DataSet componentAsDataSet = component.toDataSet();
            data.add( componentAsDataSet );
        }
        return data;
    }

    public static Builder newRegion()
    {
        return new Builder();
    }

    public static Builder newRegion( final Region source )
    {
        return new Builder( source );
    }

    public static Builder newRegion( final RootDataSet regionAsData )
    {
        final Builder builder = new Builder();
        builder.name( regionAsData.getProperty( "name" ).getString() );
        for ( final DataSet componentAsDataSet : regionAsData.getDataSets() )
        {
            builder.add( RegionPlaceableComponentFactory.create( componentAsDataSet ) );
        }
        return builder;
    }

    public static class Builder
    {
        private String name;

        private ImmutableList.Builder<RegionPlaceableComponent> components = new ImmutableList.Builder<>();

        public Builder()
        {

        }

        public Builder( final Region source )
        {
            this.name = source.name;
            for ( RegionPlaceableComponent component : source.components )
            {
                this.components.add( component );
            }
        }

        public Builder name( final String value )
        {
            this.name = value;
            return this;
        }

        public Builder add( final RegionPlaceableComponent component )
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
