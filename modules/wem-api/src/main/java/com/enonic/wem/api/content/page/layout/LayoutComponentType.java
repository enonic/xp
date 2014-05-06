package com.enonic.wem.api.content.page.layout;


import com.enonic.wem.api.content.page.DescriptorBasedPageComponentType;

public final class LayoutComponentType
    extends DescriptorBasedPageComponentType<LayoutComponent>
{
    private static final LayoutComponentDataSerializer dataSerializer = new LayoutComponentDataSerializer();

    public LayoutComponentType()
    {
        super( "layout", LayoutComponent.class );
    }

    @Override
    public LayoutComponentXml toXml( final LayoutComponent component )
    {
        LayoutComponentXml componentXml = new LayoutComponentXml();
        componentXml.from( component );
        return componentXml;
    }

    @Override
    public LayoutComponentJson toJson( final LayoutComponent component )
    {
        return new LayoutComponentJson( component );
    }

    @Override
    public LayoutComponentDataSerializer getDataSerializer()
    {
        return dataSerializer;
    }
}
