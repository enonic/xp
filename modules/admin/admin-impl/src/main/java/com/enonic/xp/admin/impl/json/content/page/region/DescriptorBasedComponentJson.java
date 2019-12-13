package com.enonic.xp.admin.impl.json.content.page.region;


import java.util.List;

import com.enonic.xp.admin.impl.rest.resource.content.ComponentNameResolver;
import com.enonic.xp.data.PropertyArrayJson;
import com.enonic.xp.data.PropertyTreeJson;
import com.enonic.xp.region.DescriptorBasedComponent;

@SuppressWarnings("UnusedDeclaration")
public abstract class DescriptorBasedComponentJson<COMPONENT extends DescriptorBasedComponent>
    extends ComponentJson<COMPONENT>
{
    private final List<PropertyArrayJson> config;

    protected DescriptorBasedComponentJson( final COMPONENT component, final ComponentNameResolver componentNameResolver )
    {
        super( component, componentNameResolver );
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
