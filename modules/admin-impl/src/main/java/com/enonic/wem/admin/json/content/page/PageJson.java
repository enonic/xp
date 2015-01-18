package com.enonic.wem.admin.json.content.page;


import java.util.List;

import com.enonic.wem.admin.json.content.page.region.PageRegionsJson;
import com.enonic.wem.admin.json.content.page.region.RegionJson;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.data.PropertyArrayJson;
import com.enonic.wem.api.data.PropertyTreeJson;

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

}
