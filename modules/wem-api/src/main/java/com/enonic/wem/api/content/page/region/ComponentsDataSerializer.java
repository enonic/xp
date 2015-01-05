package com.enonic.wem.api.content.page.region;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.PropertySet;
import com.enonic.wem.api.support.serializer.AbstractDataListSerializer;

public class ComponentsDataSerializer
    extends AbstractDataListSerializer<Collection<Component>, List<Component>>
{
    public void toData( final Collection<Component> components, final PropertySet parent )
    {
        for ( final Component component : components )
        {
            final PropertySet componentAsSet = parent.addSet( "component" );
            final ComponentType type = component.getType();
            componentAsSet.setString( "type", type.getComponentClass().getSimpleName() );
            type.getDataSerializer().toData( component, componentAsSet );
        }
    }

    public List<Component> fromData( final Iterable<Property> componentProperties )
    {
        final List<Component> componentList = new ArrayList<>();
        for ( final Property componentAsProperty : componentProperties )
        {
            final PropertySet componentWrapper = componentAsProperty.getSet();
            final ComponentType type = ComponentTypes.bySimpleClassName( componentWrapper.getString( "type" ) );
            final PropertySet componentSet = componentWrapper.getSet( type.getComponentClass().getSimpleName() );
            componentList.add( type.getDataSerializer().fromData( componentSet ) );
        }
        return componentList;
    }
}
