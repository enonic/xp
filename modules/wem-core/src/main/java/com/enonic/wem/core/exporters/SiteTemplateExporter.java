package com.enonic.wem.core.exporters;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import com.enonic.wem.api.content.page.Template;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.xml.XmlSerializers;
import com.enonic.wem.xml.template.SiteTemplateXml;

@XMLFilename("site-template.xml")
public final class SiteTemplateExporter
    extends AbstractEntityExporter<SiteTemplate>
{
    public static final String COMPONENTS_DIR = "components";

    protected String toXMLString( final SiteTemplate siteTemplate )
    {
        final SiteTemplateXml siteTemplateXml = new SiteTemplateXml();
        siteTemplateXml.from( siteTemplate );
        return XmlSerializers.siteTemplate().serialize( siteTemplateXml );
    }

    @Override
    protected SiteTemplate fromXMLString( final String xml, final Path directoryPath )
        throws IOException
    {
        final SiteTemplate.Builder builder = SiteTemplate.newSiteTemplate();
        XmlSerializers.siteTemplate().parse( xml ).to( builder );

        final String id = resolveId( directoryPath );
        final SiteTemplateKey siteTemplateKey = SiteTemplateKey.from( id );
        builder.key( siteTemplateKey );

        importTemplates( builder, directoryPath.resolve( COMPONENTS_DIR ) );

        return builder.build();
    }

    public void exportObject( final SiteTemplate siteTemplate, final Path rootPath )
        throws IOException
    {
        super.exportObject( siteTemplate, rootPath );

        for ( final Template template : siteTemplate )
        {
            final AbstractEntityExporter<Template> exporter = EntityExporters.getForObject( template );
            exporter.exportObject( template, createPath( rootPath.resolve( COMPONENTS_DIR ) ) );
        }
    }

    private void importTemplates( final SiteTemplate.Builder parentEntry, final Path parentDirectory )
        throws IOException
    {
        try (final DirectoryStream<Path> ds = Files.newDirectoryStream( parentDirectory ))
        {
            for ( final Path file : ds )
            {
                final String filename = file.getFileName().toString();
                final AbstractEntityExporter<Template> entityExporter = EntityExporters.getByFilename( filename );
                final Template template = entityExporter.importObject( parentDirectory, file );
                parentEntry.addTemplate( template );
            }
        }
    }
}
