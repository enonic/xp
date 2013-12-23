package com.enonic.wem.core.content.page.layout;

import java.io.IOException;
import java.nio.file.Path;

import com.enonic.wem.api.content.page.layout.LayoutTemplate;
import com.enonic.wem.api.content.page.layout.LayoutTemplateXml;
import com.enonic.wem.core.support.export.AbstractEntityExporter;
import com.enonic.wem.core.support.export.XMLFilename;
import com.enonic.wem.xml.XmlSerializers;

@XMLFilename("layout-template.xml")
public final class LayoutTemplateExporter
    extends AbstractEntityExporter<LayoutTemplate, LayoutTemplate.Builder>
{
    @Override
    protected String toXMLString( final LayoutTemplate layoutTemplate )
    {
        final LayoutTemplateXml layoutTemplateXml = new LayoutTemplateXml();
        layoutTemplateXml.from( layoutTemplate );
        return XmlSerializers.layoutTemplate().serialize( layoutTemplateXml );
    }

    @Override
    protected LayoutTemplate.Builder fromXMLString( final String xml, final Path directoryPath )
        throws IOException
    {
        final LayoutTemplate.Builder builder = LayoutTemplate.newLayoutTemplate();
        XmlSerializers.layoutTemplate().parse( xml ).to( builder );
        return builder;
    }
}
