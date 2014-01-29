package com.enonic.wem.api.content.page.image;

import javax.xml.bind.annotation.XmlRootElement;

import com.enonic.wem.xml.template.AbstractTemplateXml;

@XmlRootElement(name = "image-template")
public final class ImageTemplateXml
    extends AbstractTemplateXml<ImageTemplate, ImageTemplate.Builder>
{

    @Override
    public void from( final ImageTemplate template )
    {
        fromTemplate( template );
    }

    @Override
    public void to( final ImageTemplate.Builder builder )
    {
        toTemplate( builder );
    }
}
