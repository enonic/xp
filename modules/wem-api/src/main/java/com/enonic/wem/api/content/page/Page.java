package com.enonic.wem.api.content.page;


import com.enonic.wem.api.data.RootDataSet;

public class Page
{
    PageTemplateId pageTemplate;

    /**
     * Values will override any values in PageTemplate.pageConfig.
     */
    RootDataSet config;

    RootDataSet liveEditConfig;

    public PageTemplateId getTemplateId()
    {
        return pageTemplate;
    }

    public RootDataSet getConfig()
    {
        return config;
    }

    public RootDataSet getLiveEditConfig()
    {
        return liveEditConfig;
    }
}
