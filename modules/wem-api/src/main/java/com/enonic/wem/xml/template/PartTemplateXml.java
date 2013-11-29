package com.enonic.wem.xml.template;

import javax.xml.bind.annotation.XmlRootElement;

import com.enonic.wem.api.content.page.part.PartTemplate;
import com.enonic.wem.api.content.page.part.PartTemplateName;

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
        builder.name( new PartTemplateName( getName() ) );
    }
}
