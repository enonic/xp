package com.enonic.wem.admin.json.content.page.layout;


import java.util.List;

import com.enonic.wem.admin.json.content.page.PageComponentJson;
import com.enonic.wem.admin.json.content.page.region.RegionJson;
import com.enonic.wem.api.content.page.layout.LayoutComponent;

@SuppressWarnings("UnusedDeclaration")
public class LayoutComponentJson
    extends PageComponentJson
{
    private final LayoutComponent layout;

    private final LayoutRegionsJson regionsJson;

    public LayoutComponentJson( final LayoutComponent component )
    {
        super( component );
        this.layout = component;
        this.regionsJson = new LayoutRegionsJson( component.getRegions() );
    }

    public String getName()
    {
        return layout.getName().toString();
    }

    public List<RegionJson> getRegions()
    {
        return regionsJson.getRegions();
    }
}
