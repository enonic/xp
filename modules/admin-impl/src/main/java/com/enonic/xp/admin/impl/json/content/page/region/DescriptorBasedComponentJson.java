package com.enonic.xp.admin.impl.json.content.page.region;


import java.util.List;

import com.enonic.xp.core.content.page.region.DescriptorBasedComponent;
import com.enonic.xp.core.data.PropertyArrayJson;
import com.enonic.xp.core.data.PropertyTreeJson;

@SuppressWarnings("UnusedDeclaration")
public abstract class DescriptorBasedComponentJson<COMPONENT extends DescriptorBasedComponent>
    extends ComponentJson<COMPONENT>
{
    private final List<PropertyArrayJson> config;

    protected DescriptorBasedComponentJson( final COMPONENT component )
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
