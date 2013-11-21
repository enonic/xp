package com.enonic.wem.core.exporters;

import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.xml.XmlSerializers;
import com.enonic.wem.xml.template.PageTemplateXml;

@XMLFilename("PageTemplate.xml")
public final class PageTemplateExporter
    extends EntityExporter<PageTemplate>
{
    protected String serializeToXMLString( final PageTemplate pageTemplate )
    {
        final PageTemplateXml pageTemplateXml = new PageTemplateXml();
        pageTemplateXml.from( pageTemplate );
        return XmlSerializers.pageTemplate().serialize( pageTemplateXml );
    }
}
