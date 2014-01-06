package com.enonic.wem.api.content.page;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "page-component")
public final class PageDescriptorXml
    extends DescriptorXml<PageDescriptor, PageDescriptor.Builder>
{
    @Override
    public void from( final PageDescriptor pageDescriptor )
    {
        fromDescriptor( pageDescriptor );
    }

    @Override
    public void to( final PageDescriptor.Builder builder )
    {
        toDescriptor( builder );
    }
}
