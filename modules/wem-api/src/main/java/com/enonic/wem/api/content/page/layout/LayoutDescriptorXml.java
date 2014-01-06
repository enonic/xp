package com.enonic.wem.api.content.page.layout;

import javax.xml.bind.annotation.XmlRootElement;

import com.enonic.wem.api.content.page.DescriptorXml;

@XmlRootElement(name = "layout-component")
public final class LayoutDescriptorXml
    extends DescriptorXml<LayoutDescriptor, LayoutDescriptor.Builder>
{
    @Override
    public void from( final LayoutDescriptor template )
    {
        fromDescriptor( template );
    }

    @Override
    public void to( final LayoutDescriptor.Builder builder )
    {
        toDescriptor( builder );
    }
}
