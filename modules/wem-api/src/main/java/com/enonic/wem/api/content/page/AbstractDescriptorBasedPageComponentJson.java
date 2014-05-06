package com.enonic.wem.api.content.page;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.enonic.wem.api.content.page.image.ImageComponentJson;
import com.enonic.wem.api.content.page.layout.LayoutComponentJson;
import com.enonic.wem.api.content.page.part.PartComponentJson;
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

    public static AbstractDescriptorBasedPageComponentJson fromPageComponent( final DescriptorBasedPageComponent component )
    {
        return (AbstractDescriptorBasedPageComponentJson) component.getType().toJson( component );
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
