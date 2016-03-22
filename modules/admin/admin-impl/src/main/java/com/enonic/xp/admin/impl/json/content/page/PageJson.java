package com.enonic.xp.admin.impl.json.content.page;


import java.util.List;

import com.enonic.xp.admin.impl.json.content.page.region.ComponentJson;
import com.enonic.xp.admin.impl.json.content.page.region.ComponentJsonSerializer;
import com.enonic.xp.admin.impl.json.content.page.region.PageRegionsJson;
import com.enonic.xp.admin.impl.json.content.page.region.RegionJson;
import com.enonic.xp.data.PropertyArrayJson;
import com.enonic.xp.data.PropertyTreeJson;
import com.enonic.xp.page.Page;

@SuppressWarnings("UnusedDeclaration")
public final class PageJson
{
    private final Page page;

    private final PageRegionsJson regionsJson;

    private final List<PropertyArrayJson> configJson;

    public PageJson( final Page page )
    {
        this.page = page;
        this.regionsJson = page.hasRegions() ? new PageRegionsJson( page.getRegions() ) : null;
        this.configJson = page.hasConfig() ? PropertyTreeJson.toJson( page.getConfig() ) : null;
    }

    public String getController()
    {
        return page.hasController() ? page.getController().toString() : null;
    }

    public String getTemplate()
    {
        return page.hasTemplate() ? page.getTemplate().toString() : null;
    }

    public List<RegionJson> getRegions()
    {
        return regionsJson != null ? regionsJson.getRegions() : null;
    }

    public List<PropertyArrayJson> getConfig()
    {
        return configJson;
    }

    public boolean isCustomized()
    {
        return page.isCustomized();
    }

    public ComponentJson getFragment()
    {
        return page.isFragment() ? ComponentJsonSerializer.toJson( page.getFragment() ) : null;
    }
}
