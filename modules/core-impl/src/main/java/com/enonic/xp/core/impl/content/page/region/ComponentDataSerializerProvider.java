package com.enonic.xp.core.impl.content.page.region;

import com.enonic.xp.page.ComponentDataSerializer;
import com.enonic.xp.page.region.ComponentType;
import com.enonic.xp.page.region.ImageComponentType;
import com.enonic.xp.page.region.LayoutComponentType;
import com.enonic.xp.page.region.PartComponentType;
import com.enonic.xp.page.region.TextComponentType;

class ComponentDataSerializerProvider
{
    private final PartComponentDataSerializer partDataSerializer = new PartComponentDataSerializer();

    private final TextComponentDataSerializer textDataSerializer = new TextComponentDataSerializer();

    private final LayoutComponentDataSerializer layoutDataSerializer = new LayoutComponentDataSerializer();

    private final ImageComponentDataSerializer imageDataSerializer = new ImageComponentDataSerializer();

    public ComponentDataSerializerProvider()
    {
    }

    public ComponentDataSerializer getDataSerializer( final ComponentType componentType )
    {
        if ( componentType instanceof PartComponentType )
        {
            return partDataSerializer;
        }
        if ( componentType instanceof LayoutComponentType )
        {
            return layoutDataSerializer;
        }
        if ( componentType instanceof TextComponentType )
        {
            return textDataSerializer;
        }
        if ( componentType instanceof ImageComponentType )
        {
            return imageDataSerializer;
        }
        else
        {
            return null;
        }
    }

}
