package com.enonic.wem.api.content.page;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.enonic.wem.api.data2.Property;
import com.enonic.wem.api.data2.PropertySet;
import com.enonic.wem.api.support.serializer.AbstractDataListSerializer;

public class PageComponentsDataSerializer
    extends AbstractDataListSerializer<Collection<PageComponent>, List<PageComponent>>
{
    public void toData( final Collection<PageComponent> components, final PropertySet parent )
    {
        for ( final PageComponent component : components )
        {
            final PropertySet componentAsSet = parent.addSet( "component" );
            final PageComponentType type = component.getType();
            componentAsSet.setString( "type", type.getComponentClass().getSimpleName() );
            type.getDataSerializer().toData( component, componentAsSet );
        }
    }

    public List<PageComponent> fromData( final Iterable<Property> componentProperties )
    {
        final List<PageComponent> componentList = new ArrayList<>();
        for ( final Property componentAsProperty : componentProperties )
        {
            final PropertySet componentWrapper = componentAsProperty.getSet();
            final PageComponentType type = PageComponentTypes.bySimpleClassName( componentWrapper.getString( "type" ) );
            final PropertySet componentSet = componentWrapper.getSet( type.getComponentClass().getSimpleName() );
            componentList.add( type.getDataSerializer().fromData( componentSet ) );
        }
        return componentList;
    }
}
