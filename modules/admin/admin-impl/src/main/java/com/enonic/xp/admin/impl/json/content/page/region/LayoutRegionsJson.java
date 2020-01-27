package com.enonic.xp.admin.impl.json.content.page.region;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.enonic.xp.admin.impl.rest.resource.content.ComponentNameResolver;
import com.enonic.xp.region.LayoutRegions;
import com.enonic.xp.region.Region;

@SuppressWarnings("UnusedDeclaration")
public class LayoutRegionsJson
{
    private final LayoutRegions regions;

    private final List<RegionJson> regionsJson;

    public LayoutRegionsJson( final LayoutRegions regions, final ComponentNameResolver componentNameResolver )
    {
        this.regions = regions;

        if ( regions != null )
        {
            regionsJson = new ArrayList<>();
            for ( Region region : regions )
            {
                regionsJson.add( new RegionJson( region, componentNameResolver ) );
            }
        }
        else
        {
            regionsJson = null;
        }
    }

    @JsonCreator
    public LayoutRegionsJson( final List<RegionJson> regionJsons )
    {
        this.regionsJson = regionJsons;
        final LayoutRegions.Builder builder = LayoutRegions.create();
        for ( RegionJson region : regionJsons )
        {
            builder.add( region.getRegion() );
        }
        this.regions = builder.build();
    }

    public List<RegionJson> getRegions()
    {
        return regionsJson;
    }

    @JsonIgnore
    public LayoutRegions getLayoutRegions()
    {
        return regions;
    }
}
