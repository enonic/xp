package com.enonic.wem.xml.content.page;

import javax.xml.bind.annotation.XmlRootElement;

import com.enonic.wem.api.content.page.PageDescriptor;

@XmlRootElement(name = "page-component")
public final class PageDescriptorXml
    extends AbstractDescriptorXml<PageDescriptor, PageDescriptor.Builder>
{
    @Override
    public void from( final PageDescriptor template )
    {
        fromDescriptor( template );
    }

    @Override
    public void to( final PageDescriptor.Builder builder )
    {
        toDescriptor( builder );
    }
}
