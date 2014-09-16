package com.enonic.wem.api.content.page.part;

import com.enonic.wem.api.content.page.PageComponentType;

public final class PartComponentType
    extends PageComponentType
{
    private static final PartComponentDataSerializer dataSerializer = new PartComponentDataSerializer();

    public PartComponentType()
    {
        super( "part", PartComponent.class );
    }

    @Override
    public PartComponentDataSerializer getDataSerializer()
    {
        return dataSerializer;
    }
}
