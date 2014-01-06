package com.enonic.wem.api.content.page.image;

import javax.xml.bind.annotation.XmlRootElement;

import com.enonic.wem.api.content.page.DescriptorXml;

@XmlRootElement(name = "image-component")
public final class ImageDescriptorXml
    extends DescriptorXml<ImageDescriptor, ImageDescriptor.Builder>
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
