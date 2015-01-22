package com.enonic.wem.api.content.page.region;

public final class PartComponentType
    extends ComponentType
{
    public final static PartComponentType INSTANCE = new PartComponentType();

    private PartComponentType()
    {
        super( "part", PartComponent.class );
    }

}
