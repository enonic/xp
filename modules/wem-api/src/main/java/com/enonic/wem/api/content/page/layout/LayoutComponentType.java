package com.enonic.wem.api.content.page.layout;

import com.enonic.wem.api.content.page.PageComponentType;

public final class LayoutComponentType
    extends PageComponentType
{
    public final static LayoutComponentType INSTANCE = new LayoutComponentType();

    private static final LayoutComponentDataSerializer dataSerializer = new LayoutComponentDataSerializer();

    private LayoutComponentType()
    {
        super( "layout", LayoutComponent.class );
    }

    @Override
    public LayoutComponentDataSerializer getDataSerializer()
    {
        return dataSerializer;
    }
}
