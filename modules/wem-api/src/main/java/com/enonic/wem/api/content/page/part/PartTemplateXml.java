package com.enonic.wem.api.content.page.part;

import javax.xml.bind.annotation.XmlRootElement;

import com.enonic.wem.xml.template.AbstractTemplateXml;

@XmlRootElement(name = "part-template")
public final class PartTemplateXml
    extends AbstractTemplateXml<PartTemplate, PartTemplate.Builder>
{

    @Override
    public void from( final PartTemplate template )
    {
        fromTemplate( template );
    }

    @Override
    public void to( final PartTemplate.Builder builder )
    {
        toTemplate( builder );
    }
}
