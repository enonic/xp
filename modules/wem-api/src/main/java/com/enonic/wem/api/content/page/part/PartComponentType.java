package com.enonic.wem.api.content.page.part;


import com.enonic.wem.api.content.page.AbstractPageComponentXml;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.PageComponentDataSerializer;
import com.enonic.wem.api.content.page.PageComponentJson;
import com.enonic.wem.api.content.page.PageComponentType;

public final class PartComponentType
    extends PageComponentType<PartComponent>
{
    private static final PartComponentDataSerializer dataSerializer = new PartComponentDataSerializer();

    public PartComponentType()
    {
        super( "part", PartComponent.class );
    }

    @Override
    public AbstractPageComponentXml toXml( final PageComponent component )
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

    @Override
    public PageComponentDataSerializer getDataSerializer()
    {
        return dataSerializer;
    }
}
