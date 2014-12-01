package com.enonic.wem.admin.json.content.page;


import java.util.List;

import com.enonic.wem.api.content.page.DescriptorBasedPageComponent;
import com.enonic.wem.api.data2.PropertyArrayJson;
import com.enonic.wem.api.data2.PropertyTreeJson;

@SuppressWarnings("UnusedDeclaration")
public abstract class AbstractDescriptorBasedPageComponentJson<COMPONENT extends DescriptorBasedPageComponent>
    extends AbstractPageComponentJson<COMPONENT>
{
    private final List<PropertyArrayJson> config;

    protected AbstractDescriptorBasedPageComponentJson( final COMPONENT component )
    {
        super( component );
        this.config = component.getConfig() != null ? PropertyTreeJson.toJson( component.getConfig() ) : null;
    }

    public String getDescriptor()
    {
        return getComponent().getDescriptor() != null ? getComponent().getDescriptor().toString() : null;
    }

    public List<PropertyArrayJson> getConfig()
    {
        return config;
    }
}
