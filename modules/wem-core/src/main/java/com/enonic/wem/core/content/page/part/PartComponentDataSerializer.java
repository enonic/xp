package com.enonic.wem.core.content.page.part;


import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.part.PartComponent;
import com.enonic.wem.api.content.page.part.PartDescriptorKey;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.core.content.page.PageComponentDataSerializer;

public class PartComponentDataSerializer
    extends PageComponentDataSerializer<PartComponent, PartComponent>
{

    public DataSet toData( final PartComponent component )
    {
        final DataSet asData = new DataSet( PartComponent.class.getSimpleName() );
        applyPageComponentToData( component, asData );
        return asData;
    }

    public PartComponent fromData( final DataSet asData )
    {
        PartComponent.Builder component = PartComponent.newPartComponent();
        applyPageComponentFromData( component, asData );
        return component.build();
    }

    @Override
    protected DescriptorKey toDescriptorkey( final String s )
    {
        return PartDescriptorKey.from( s );
    }

}
