package com.enonic.wem.api.content.page.part;


import com.enonic.wem.api.content.page.DescriptorBasedPageComponentType;

public final class PartComponentType
    extends DescriptorBasedPageComponentType<PartComponent>
{
    private static final PartComponentDataSerializer dataSerializer = new PartComponentDataSerializer();

    public PartComponentType()
    {
        super( "part", PartComponent.class );
    }

    @Override
    public PartComponentXml toXml( final PartComponent component )
    {
        PartComponentXml componentXml = new PartComponentXml();
        componentXml.from( component );
        return componentXml;
    }

    @Override
    public PartComponentJson toJson( final PartComponent component )
    {
        return new PartComponentJson( component );
    }

    @Override
    public PartComponentDataSerializer getDataSerializer()
    {
        return dataSerializer;
    }
}
