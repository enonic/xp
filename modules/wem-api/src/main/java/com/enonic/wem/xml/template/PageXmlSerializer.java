package com.enonic.wem.xml.template;

import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.xml.JaxbXmlSerializer;

public final class PageXmlSerializer
    extends JaxbXmlSerializer<PageTemplateXml, PageTemplate, PageTemplate.Builder>
{
    public PageXmlSerializer()
    {
        super( PageTemplateXml.class );
    }

    @Override
    public PageTemplateXml toJaxbObject( final PageTemplate value )
    {
        final PageTemplateXml xml = new PageTemplateXml();
        xml.from( value );
        return xml;
    }

    @Override
    public void fromJaxbObject( final PageTemplate.Builder value, final PageTemplateXml xml )
    {
        xml.to( value );
    }
}
