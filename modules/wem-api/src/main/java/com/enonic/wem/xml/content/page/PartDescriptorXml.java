package com.enonic.wem.xml.content.page;

import javax.xml.bind.annotation.XmlRootElement;

import com.enonic.wem.api.content.page.part.PartDescriptor;

@XmlRootElement(name = "part-component")
public final class PartDescriptorXml
    extends AbstractDescriptorXml<PartDescriptor, PartDescriptor.Builder>
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
