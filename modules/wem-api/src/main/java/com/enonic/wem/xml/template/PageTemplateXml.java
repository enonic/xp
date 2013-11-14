package com.enonic.wem.xml.template;

import javax.xml.bind.annotation.XmlRootElement;

import com.enonic.wem.api.content.page.PageTemplate;

@XmlRootElement(name = "page-template")
public final class PageTemplateXml
    extends AbstractTemplateXml<PageTemplate, PageTemplate.Builder>
{

    @Override
    public void from( final PageTemplate template )
    {
        fromTemplate( template );
    }

    @Override
    public void to( final PageTemplate.Builder builder )
    {
        toTemplate( builder );
    }
}
