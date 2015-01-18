package com.enonic.wem.api.content.page.region;

public final class LayoutComponentType
    extends ComponentType
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
