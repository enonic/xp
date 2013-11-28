package com.enonic.wem.xml.template;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateName;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;

@XmlRootElement(name = "page-template")
public final class PageTemplateXml
    extends AbstractTemplateXml<PageTemplate, PageTemplate.Builder>
{
    @XmlElement(name = "content-type", required = false)
    @XmlElementWrapper(name = "can-render")
    private List<String> canRender = new ArrayList<>();

    @Override
    public void from( final PageTemplate template )
    {
        fromTemplate( template );
        for ( ContentTypeName contentType : template.getCanRender() )
        {
            this.canRender.add( contentType.toString() );
        }
    }

    @Override
    public void to( final PageTemplate.Builder builder )
    {
        toTemplate( builder );
        builder.canRender( ContentTypeNames.from( this.canRender ) );
        builder.name( new PageTemplateName( getName() ) );
    }
}
