package com.enonic.xp.core.impl.content.serializer;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.data.PropertySet;

public final class SerializedData
{
    private final PropertySet asData;

    private final List<PropertySet> componentsAsData;

    public SerializedData( final PropertySet asData )
    {
        this.asData = asData;
        this.componentsAsData = new ArrayList<>();
    }

    public SerializedData( final PropertySet asData, final List<PropertySet> componentsAsData )
    {
        this.asData = asData;
        this.componentsAsData = componentsAsData;
    }

    public PropertySet getAsData()
    {
        return asData;
    }

    public List<PropertySet> getComponentsAsData()
    {
        return componentsAsData;
    }
}
