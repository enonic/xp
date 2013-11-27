package com.enonic.wem.xml.content.page;

import javax.xml.bind.annotation.XmlRootElement;

import com.enonic.wem.api.content.page.ImageDescriptor;

@XmlRootElement(name = "image-component")
public final class ImageDescriptorXml
    extends AbstractDescriptorXml<ImageDescriptor, ImageDescriptor.Builder>
{
    @Override
    public void from( final ImageDescriptor template )
    {
        fromDescriptor( template );
    }

    @Override
    public void to( final ImageDescriptor.Builder builder )
    {
        toDescriptor( builder );
    }
}
