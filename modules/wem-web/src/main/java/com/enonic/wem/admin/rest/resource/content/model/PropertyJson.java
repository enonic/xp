package com.enonic.wem.admin.rest.resource.content.model;

import com.enonic.wem.api.data.data.Property;

public class PropertyJson
    extends AbstractDataJson
{
    private final Property property;

    public PropertyJson( final Property property )
    {
        this.property = property;
    }

    public String getName()
    {
        return property.getName();
    }

    public String getPath()
    {
        return property.getPath().toString();
    }

    public String getType()
    {
        return property.getValueType().getName();
    }

    public String getValue()
    {
        return property.getString();
    }
}
