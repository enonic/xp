package com.enonic.wem.admin.json.content.page.region;

import java.util.ArrayList;
import java.util.List;

import com.enonic.wem.api.content.page.region.PageRegions;
import com.enonic.wem.api.content.page.region.Region;

@SuppressWarnings("UnusedDeclaration")
public class PageRegionsJson
{
    private final PageRegions regions;

    private final List<RegionJson> regionsJson;

    public PageRegionsJson( final PageRegions regions )
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

    public List<RegionJson> getRegions()
    {
        return regionsJson;
    }

}
