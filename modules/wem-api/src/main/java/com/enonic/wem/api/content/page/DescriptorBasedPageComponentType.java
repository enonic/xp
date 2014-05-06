package com.enonic.wem.api.content.page;

public abstract class DescriptorBasedPageComponentType<DESCRIPTOR_BASED_PAGE_COMPONENT extends DescriptorBasedPageComponent>
    extends PageComponentType<DESCRIPTOR_BASED_PAGE_COMPONENT>
{
    protected DescriptorBasedPageComponentType( final String shortName, final Class clazz )
    {
        super( shortName, clazz );
    }

    public abstract AbstractDescriptorBasedPageComponentXml toXml( final DESCRIPTOR_BASED_PAGE_COMPONENT component );

    public abstract AbstractDescriptorBasedPageComponentJson toJson( final DESCRIPTOR_BASED_PAGE_COMPONENT component );

    public abstract AbstractDescriptorBasedPageComponentDataSerializer getDataSerializer();

}
