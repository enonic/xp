package com.enonic.wem.api.content.page;


import com.enonic.wem.api.data.RootDataSet;

public class Part
    extends Component<PartTemplateId>
{
    RootDataSet config;

    RootDataSet liveEditConfig;

    public RootDataSet getConfig()
    {
        return config;
    }
}
