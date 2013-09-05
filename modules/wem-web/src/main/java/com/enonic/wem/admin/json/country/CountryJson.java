package com.enonic.wem.admin.json.country;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.admin.rest.resource.model.Item;
import com.enonic.wem.core.country.Country;
import com.enonic.wem.core.country.Region;

public class CountryJson
    extends Item
{
    private final Country model;

    private final ImmutableList<CallingCodeJson> callingCodes;

    private final ImmutableList<RegionJson> regions;

    public CountryJson( final Country model )
    {
        this.model = model;

        final ImmutableList.Builder<CallingCodeJson> builderCode = ImmutableList.builder();
        builderCode.add( new CallingCodeJson( model ) );
        this.callingCodes = builderCode.build();

        final ImmutableList.Builder<RegionJson> builderRegion = ImmutableList.builder();
        for ( final Region region : model.getRegions() )
        {
            builderRegion.add( new RegionJson( region ) );
        }
        this.regions = builderRegion.build();
    }

    public String getCode()
    {
        return this.model.getCode().toString();
    }

    public String getEnglishName()
    {
        return this.model.getEnglishName();
    }

    public String getLocalName()
    {
        return this.model.getLocalName();
    }

    public String getRegionsEnglishName()
    {
        return this.model.getRegionsEnglishName();
    }

    public String getRegionsLocalName()
    {
        return this.model.getRegionsLocalName();
    }

    public List<CallingCodeJson> getCallingCodes()
    {
        return this.callingCodes;
    }

    public List<RegionJson> getRegions()
    {
        return this.regions;
    }

    @Override
    public boolean getEditable()
    {
        return false;
    }

    @Override
    public boolean getDeletable()
    {
        return false;
    }
}
