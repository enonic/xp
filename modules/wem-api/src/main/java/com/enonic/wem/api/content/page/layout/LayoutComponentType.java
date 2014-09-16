package com.enonic.wem.api.content.page.layout;

import com.enonic.wem.api.content.page.PageComponentType;

public final class LayoutComponentType
    extends PageComponentType
{
    private static final LayoutComponentDataSerializer dataSerializer = new LayoutComponentDataSerializer();

    public LayoutComponentType()
    {
        super( "layout", LayoutComponent.class );
    }

    @Override
    public LayoutComponentDataSerializer getDataSerializer()
    {
        return dataSerializer;
    }
}
