package com.enonic.wem.api.content.page;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.support.serializer.AbstractDataListSerializer;

public class PageComponentsDataSerializer
    extends AbstractDataListSerializer<Collection<PageComponent>, List<PageComponent>>
{
    public List<Data> toData( final Collection<PageComponent> components )
    {
        final List<Data> dataList = new ArrayList<>( components.size() );
        for ( final PageComponent component : components )
        {
            final AbstractPageComponentDataSerializer pageComponentDataSerializer = component.getType().getDataSerializer();
            dataList.add( pageComponentDataSerializer.toData( component ) );
        }
        return dataList;
    }

    public List<PageComponent> fromData( final List<Data> asData )
    {
        final List<PageComponent> componentList = new ArrayList<>( asData.size() );
        for ( final Data componentAsData : asData )
        {
            final DataSet componentAsDataSet = componentAsData.toDataSet();
            final AbstractPageComponentDataSerializer pageComponentDataSerializer =
                PageComponentTypes.bySimpleClassName( componentAsDataSet.getName() ).getDataSerializer();
            final PageComponent component = pageComponentDataSerializer.fromData( componentAsDataSet );
            componentList.add( component );
        }
        return componentList;
    }
}
