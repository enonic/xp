package com.enonic.xp.core.impl.content.serializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.enonic.xp.core.impl.content.page.region.ComponentTypes;
import com.enonic.xp.data.Property;
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
            final PropertySet componentAsSet = parent.addSet( "component" );
            final ComponentType type = component.getType();
            componentAsSet.setString( TYPE, type.getComponentClass().getSimpleName() );
            COMPONENT_DATA_SERIALIZER_FACTORY.getDataSerializer( type ).toData( component, componentAsSet );
        }
    }

    public List<Component> fromData( final Iterable<Property> componentProperties )
    {
        final List<Component> componentList = new ArrayList<>();
        for ( final Property componentAsProperty : componentProperties )
        {
            final PropertySet componentWrapper = componentAsProperty.getSet();
            final ComponentType type = ComponentTypes.bySimpleClassName( componentWrapper.getString( TYPE ) );
            final PropertySet componentSet = componentWrapper.getSet( type.getComponentClass().getSimpleName() );
            componentList.add( COMPONENT_DATA_SERIALIZER_FACTORY.getDataSerializer( type ).fromData( componentSet ) );
        }
        return componentList;
    }
}
