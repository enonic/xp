package com.enonic.wem.api.content.page.layout;


import com.enonic.wem.api.content.page.AbstractPageComponentXml;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.PageComponentDataSerializer;
import com.enonic.wem.api.content.page.PageComponentJson;
import com.enonic.wem.api.content.page.PageComponentType;

public final class LayoutComponentType
    extends PageComponentType<LayoutComponent>
{
    private static final LayoutComponentDataSerializer dataSerializer = new LayoutComponentDataSerializer();

    public LayoutComponentType()
    {
        super( "layout", LayoutComponent.class );
    }

    @Override
    public AbstractPageComponentXml toXml( final PageComponent component )
    {
        LayoutComponentXml componentXml = new LayoutComponentXml();
        componentXml.from( (LayoutComponent) component );
        return componentXml;
    }

    @Override
    public PageComponentJson toJson( final LayoutComponent component )
    {
        return new LayoutComponentJson( component );
    }

    @Override
    public PageComponentDataSerializer getDataSerializer()
    {
        return dataSerializer;
    }
}
