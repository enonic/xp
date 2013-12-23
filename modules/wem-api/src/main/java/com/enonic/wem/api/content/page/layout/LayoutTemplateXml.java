package com.enonic.wem.api.content.page.layout;

import javax.xml.bind.annotation.XmlRootElement;

import com.enonic.wem.xml.template.AbstractTemplateXml;

@XmlRootElement(name = "layout-template")
public final class LayoutTemplateXml
    extends AbstractTemplateXml<LayoutTemplate, LayoutTemplate.Builder>
{

    @Override
    public void from( final LayoutTemplate template )
    {
        fromTemplate( template );
    }

    @Override
    public void to( final LayoutTemplate.Builder builder )
    {
        toTemplate( builder );
        builder.name( new LayoutTemplateName( getName() ) );
    }
}
