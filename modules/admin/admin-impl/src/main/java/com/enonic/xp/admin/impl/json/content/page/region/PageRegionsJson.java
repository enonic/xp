package com.enonic.xp.admin.impl.json.content.page.region;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.enonic.xp.admin.impl.rest.resource.content.ComponentNameResolver;
import com.enonic.xp.page.PageRegions;
import com.enonic.xp.region.Region;

@SuppressWarnings("UnusedDeclaration")
public class PageRegionsJson
{
    private final PageRegions regions;

    private final List<RegionJson> regionsJson;

    public PageRegionsJson( final PageRegions regions, final ComponentNameResolver componentNameResolver )
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
    public PageRegionsJson( final List<RegionJson> regionJsons )
    {
        this.regionsJson = regionJsons;
        final PageRegions.Builder builder = PageRegions.create();
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
    public PageRegions getPageRegions()
    {
        return regions;
    }
}
