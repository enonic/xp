package com.enonic.wem.xml.content.page;

import javax.xml.bind.annotation.XmlRootElement;

import com.enonic.wem.api.content.page.LayoutDescriptor;

@XmlRootElement(name = "layout-component")
public final class LayoutDescriptorXml
    extends AbstractDescriptorXml<LayoutDescriptor, LayoutDescriptor.Builder>
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
