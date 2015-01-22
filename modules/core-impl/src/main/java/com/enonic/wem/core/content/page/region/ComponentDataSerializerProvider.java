package com.enonic.wem.core.content.page.region;

import com.enonic.wem.api.content.page.ComponentDataSerializer;
import com.enonic.wem.api.content.page.region.ComponentType;
import com.enonic.wem.api.content.page.region.ImageComponentType;
import com.enonic.wem.api.content.page.region.LayoutComponentType;
import com.enonic.wem.api.content.page.region.PartComponentType;
import com.enonic.wem.api.content.page.region.TextComponentType;

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
