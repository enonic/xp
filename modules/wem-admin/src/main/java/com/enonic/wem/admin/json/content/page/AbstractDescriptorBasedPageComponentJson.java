package com.enonic.wem.admin.json.content.page;


import java.util.List;

import com.enonic.wem.api.content.page.DescriptorBasedPageComponent;
import com.enonic.wem.api.data.DataJson;
import com.enonic.wem.api.data.RootDataSetJson;

@SuppressWarnings("UnusedDeclaration")
public abstract class AbstractDescriptorBasedPageComponentJson<COMPONENT extends DescriptorBasedPageComponent>
    extends AbstractPageComponentJson<COMPONENT>
{
    private final List<DataJson> config;

    protected AbstractDescriptorBasedPageComponentJson( final COMPONENT component )
    {
        super( component );
        this.config = component.getConfig() != null ? new RootDataSetJson( component.getConfig() ).getSet() : null;
    }

    public String getDescriptor()
    {
        return getComponent().getDescriptor() != null ? getComponent().getDescriptor().toString() : null;
    }

    public List<DataJson> getConfig()
    {
        return config;
    }
}
