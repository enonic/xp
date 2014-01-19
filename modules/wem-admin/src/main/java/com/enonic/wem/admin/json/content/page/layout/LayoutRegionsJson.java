package com.enonic.wem.admin.json.content.page.layout;

import java.util.ArrayList;
import java.util.List;

import com.enonic.wem.admin.json.content.page.region.RegionJson;
import com.enonic.wem.api.content.page.layout.LayoutRegions;
import com.enonic.wem.api.content.page.region.Region;

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

    public List<RegionJson> getRegions()
    {
        return regionsJson;
    }

}
