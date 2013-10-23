package com.enonic.wem.admin.json.data;

import com.enonic.wem.api.data.Property;

public class PropertyJson
    extends DataJson
{
    private final Property property;

    public PropertyJson( final Property property )
    {
        super( property );
        this.property = property;
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
