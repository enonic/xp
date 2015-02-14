package com.enonic.xp.core.impl.content.page.region;


import com.enonic.xp.core.content.page.DescriptorKey;
import com.enonic.xp.core.content.page.region.PartComponent;
import com.enonic.xp.core.data.PropertySet;

public class PartComponentDataSerializer
    extends DescriptorBasedComponentDataSerializer<PartComponent, PartComponent>
{

    public void toData( final PartComponent component, final PropertySet parent )
    {
        final PropertySet asData = parent.addSet( PartComponent.class.getSimpleName() );
        applyComponentToData( component, asData );
    }

    public PartComponent fromData( final PropertySet asData )
    {
        PartComponent.Builder component = PartComponent.newPartComponent();
        applyComponentFromData( component, asData );
        return component.build();
    }

    @Override
    protected DescriptorKey toDescriptorKey( final String s )
    {
        return DescriptorKey.from( s );
    }

}
