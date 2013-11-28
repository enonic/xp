package com.enonic.wem.admin.json.content.site;


import java.util.ArrayList;
import java.util.List;

import com.enonic.wem.api.content.site.ModuleConfig;
import com.enonic.wem.api.content.site.Site;

public class SiteJson
{
    private final Site site;

    private final List<ModuleConfigJson> moduleConfigJsonList;

    public SiteJson( final Site site )
    {
        this.site = site;
        this.moduleConfigJsonList = new ArrayList<>();

        for ( final ModuleConfig moduleConfig : this.site.getModuleConfigs().getList() )
        {
            this.moduleConfigJsonList.add( new ModuleConfigJson( moduleConfig ) );
        }
    }

    String getTemplateName()
    {
        return site.getTemplate().toString();
    }

    List<ModuleConfigJson> getModuleConfigs()
    {
        return moduleConfigJsonList;
    }
}
