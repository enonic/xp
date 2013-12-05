package com.enonic.wem.api.content.page.region;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableMap;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.PropertyVisitor;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.type.ValueTypes;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.FormItemPath;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.InputTypes;

public class PageRegions
    implements Iterable<Region>
{
    private ImmutableMap<String, Region> regionByName;

    private PageRegions( final Builder builder )
    {
        this.regionByName = builder.regions.build();
    }

    @Override
    public Iterator<Region> iterator()
    {
        return this.regionByName.values().iterator();
    }

    public static class Builder
    {
        private ImmutableMap.Builder<String, Region> regions = new ImmutableMap.Builder<>();

        public Builder add( final Region region )
        {
            regions.put( region.getName(), region );
            return this;
        }

        public PageRegions build()
        {
            return new PageRegions( this );
        }
    }

    public static PageRegions resolve( final RootDataSet data, final Form form )
    {
        final List<Property> regionPropertyList = new ArrayList<>();
        new PropertyVisitor()
        {
            @Override
            public void visit( final Property property )
            {
                final FormItemPath formItemPath = FormItemPath.from( property.getPath() );
                final Input input = form.getInput( formItemPath );
                if ( input == null )
                {
                    return;
                }

                if ( input.getInputType().equals( InputTypes.REGION ) )
                {
                    regionPropertyList.add( property );
                }

            }
        }.restrictType( ValueTypes.DATA ).traverse( data );

        Builder builder = new Builder();
        for ( Property regionProperty : regionPropertyList )
        {
            builder.add( Region.newRegion( regionProperty.getData() ).build() );
        }
        return builder.build();
    }
}
