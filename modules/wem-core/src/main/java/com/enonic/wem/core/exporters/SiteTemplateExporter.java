package com.enonic.wem.core.exporters;

import java.io.IOException;
import java.nio.file.Path;

import com.enonic.wem.api.content.page.Template;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.xml.XmlSerializers;
import com.enonic.wem.xml.template.SiteTemplateXml;

@XMLFilename("site-template.xml")
public final class SiteTemplateExporter
    extends EntityExporter<SiteTemplate>
{
    protected String serializeToXMLString( final SiteTemplate siteTemplate )
    {
        final SiteTemplateXml siteTemplateXml = new SiteTemplateXml();
        siteTemplateXml.from( siteTemplate );
        return XmlSerializers.siteTemplate().serialize( siteTemplateXml );
    }

    public void writeXml( final SiteTemplate siteTemplate, final Path rootPath )
        throws IOException
    {
        super.writeXml( siteTemplate, rootPath );

        for ( final Template template : siteTemplate )
        {
            final EntityExporter<Template> exporter = getExporter( template );
            exporter.writeXml( template, createPath( rootPath.resolve( "components" ) ) );
        }
    }



}
