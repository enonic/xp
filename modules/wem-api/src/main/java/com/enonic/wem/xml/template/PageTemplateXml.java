package com.enonic.wem.xml.template;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.enonic.wem.api.content.page.PageTemplate;

@XmlRootElement(name = "page-template")
public final class PageTemplateXml
    extends AbstractTemplateXml
{
    @XmlElement(name = "module")
    @XmlElementWrapper(name = "modules")
    private List<String> modules;

    public void from( final PageTemplate template )
    {
        this.displayName = template.getDisplayName();
    }

    public void to( final PageTemplate.Builder builder )
    {
        builder.displayName( this.displayName );
    }
}
