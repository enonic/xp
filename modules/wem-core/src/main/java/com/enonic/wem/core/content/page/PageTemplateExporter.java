package com.enonic.wem.core.content.page;

import java.io.IOException;
import java.nio.file.Path;

import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateXml;
import com.enonic.wem.core.support.export.AbstractEntityExporter;
import com.enonic.wem.core.support.export.XMLFilename;
import com.enonic.wem.xml.XmlSerializers;

@XMLFilename("page-template.xml")
public final class PageTemplateExporter
    extends AbstractEntityExporter<PageTemplate, PageTemplate.Builder>
{
    @Override
    protected String toXMLString( final PageTemplate pageTemplate )
    {
        final PageTemplateXml pageTemplateXml = new PageTemplateXml();
        pageTemplateXml.from( pageTemplate );
        return XmlSerializers.pageTemplate().serialize( pageTemplateXml );
    }

    @Override
    protected PageTemplate.Builder fromXMLString( final String xml, final Path directoryPath )
        throws IOException
    {
        final PageTemplate.Builder builder = PageTemplate.newPageTemplate();
        XmlSerializers.pageTemplate().parse( xml ).to( builder );
        return builder;
    }
}
