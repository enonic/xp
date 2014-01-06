package com.enonic.wem.api.content.page.part;

import javax.xml.bind.annotation.XmlRootElement;

import com.enonic.wem.api.content.page.DescriptorXml;

@XmlRootElement(name = "part-component")
public final class PartDescriptorXml
    extends DescriptorXml<PartDescriptor, PartDescriptor.Builder>
{
    @Override
    public void from( final PartDescriptor template )
    {
        fromDescriptor( template );
    }

    @Override
    public void to( final PartDescriptor.Builder builder )
    {
        toDescriptor( builder );
    }
}
