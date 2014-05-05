package com.enonic.wem.api.content.page.part;


import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.PageComponentJson;
import com.enonic.wem.api.content.page.PageComponentType;
import com.enonic.wem.api.content.page.PageComponentXml;

public final class PartComponentType
    extends PageComponentType<PartComponent>
{
    public PartComponentType()
    {
        super( "part" );
    }

    @Override
    public PageComponentXml toXml( final PageComponent component )
    {
        PartComponentXml componentXml = new PartComponentXml();
        componentXml.from( (PartComponent) component );
        return componentXml;
    }

    @Override
    public PageComponentJson toJson( final PartComponent component )
    {
        return new PartComponentJson( component );
    }
}
