package com.enonic.wem.admin.json.content.page;


import java.util.List;

import com.enonic.wem.admin.json.data.DataJson;
import com.enonic.wem.admin.json.data.RootDataSetJson;
import com.enonic.wem.api.content.page.Page;

public final class PageJson
{
    private final Page page;

    private final RootDataSetJson configJson;

    public PageJson( final Page page )
    {
        this.page = page;
        this.configJson = new RootDataSetJson( page.getConfig() );
    }

    public String getTemplate()
    {
        return page.getTemplate().toString();
    }

    public List<DataJson> getConfig()
    {
        return configJson.getValue();
    }

}
