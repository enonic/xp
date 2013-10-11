package com.enonic.wem.api.content.page;


import com.enonic.wem.api.data.RootDataSet;

public class PartTemplate
    extends Template<PartTemplate, PartTemplateId>
{
    /**
     * Template templateConfig.
     */
    RootDataSet templateConfig;

    /**
     * Default part templateConfig that can be overridden in part (content).
     */
    RootDataSet partConfig;


    public RootDataSet getTemplateConfig()
    {
        return templateConfig;
    }

    public RootDataSet getPartConfig()
    {
        return partConfig;
    }
}
