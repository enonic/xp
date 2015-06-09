package com.enonic.xp.admin.impl.json.content.page.region;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.enonic.xp.page.region.LayoutRegions;
import com.enonic.xp.page.region.Region;

import static com.enonic.xp.page.region.LayoutRegions.newLayoutRegions;

@SuppressWarnings("UnusedDeclaration")
public class LayoutRegionsJson
{
    private final LayoutRegions regions;

    private final List<RegionJson> regionsJson;

    public LayoutRegionsJson( final LayoutRegions regions )
    {
        this.regions = regions;

        if ( regions != null )
        {
            regionsJson = new ArrayList<>();
            for ( Region region : regions )
            {
                regionsJson.add( new RegionJson( region ) );
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
        final LayoutRegions.Builder builder = newLayoutRegions();
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
