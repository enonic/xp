package com.enonic.xp.core.impl.content.serializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.enonic.xp.core.impl.content.page.region.ComponentTypes;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.ComponentType;

public final class ComponentsDataSerializer
{
    private final static ComponentDataSerializerProvider COMPONENT_DATA_SERIALIZER_FACTORY = new ComponentDataSerializerProvider();

    public static final String TYPE = "type";

    public void toData( final Collection<Component> components, final PropertySet parent )
    {
        for ( final Component component : components )
        {
            COMPONENT_DATA_SERIALIZER_FACTORY.getDataSerializer( component.getType() ).toData( component, parent );
        }
    }

    public List<Component> fromData( final SerializedData data )
    {
        final List<Component> componentList = new ArrayList<>();

        for ( final PropertySet childComponentData : ComponentDataSerializer.getChildren( data ) )
        {
            final ComponentType type = ComponentTypes.bySimpleClassName( childComponentData.getString( TYPE ) );
            componentList.add( COMPONENT_DATA_SERIALIZER_FACTORY.getDataSerializer( type ).fromData(
                new SerializedData( childComponentData, data.getComponentsAsData() ) ) );
        }

        return componentList;
    }

}
