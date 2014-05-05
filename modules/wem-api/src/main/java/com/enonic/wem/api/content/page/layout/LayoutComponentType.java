package com.enonic.wem.api.content.page.layout;


import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.PageComponentJson;
import com.enonic.wem.api.content.page.PageComponentType;
import com.enonic.wem.api.content.page.PageComponentXml;

public final class LayoutComponentType
    extends PageComponentType<LayoutComponent>
{
    public LayoutComponentType()
    {
        super( "layout" );
    }

    @Override
    public PageComponentXml toXml( final PageComponent component )
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
}
