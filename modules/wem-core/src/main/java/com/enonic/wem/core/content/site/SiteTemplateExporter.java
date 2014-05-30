package com.enonic.wem.core.content.site;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateName;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplateXml;
import com.enonic.wem.api.schema.SchemaIcon;
import com.enonic.wem.core.schema.SchemaIconDao;
import com.enonic.wem.core.support.export.AbstractEntityExporter;
import com.enonic.wem.core.support.export.EntityExporters;
import com.enonic.wem.core.support.export.XMLFilename;
import com.enonic.wem.api.xml.XmlSerializers;

@XMLFilename("site.xml")
public class SiteTemplateExporter
    extends AbstractEntityExporter<SiteTemplate, SiteTemplate.Builder>
{
    private static final String PAGE_TEMPLATE_FILE = "page-template.xml";

    protected String toXMLString( final SiteTemplate siteTemplate )
    {
        final SiteTemplateXml siteTemplateXml = new SiteTemplateXml();
        siteTemplateXml.from( siteTemplate );
        return XmlSerializers.siteTemplate().serialize( siteTemplateXml );
    }

    @Override
    protected SiteTemplate.Builder fromXMLString( final String xml, final Path directoryPath )
        throws IOException
    {
        final SiteTemplate.Builder builder = SiteTemplate.newSiteTemplate();
        XmlSerializers.siteTemplate().parse( xml ).to( builder );

        final String id = resolveId( directoryPath );
        final SiteTemplateKey siteTemplateKey = SiteTemplateKey.from( id );
        builder.key( siteTemplateKey );

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream( directoryPath ))
        {
            for ( Path templateDir : directoryStream )
            {
                if ( Files.isDirectory( templateDir ) && !IGNORE_FILES.contains( getFileName( templateDir ) ) )
                {
                    importPageTemplate( builder, templateDir );
                }
            }
        }
        final SchemaIcon icon = new SchemaIconDao().readSchemaIcon( directoryPath );
        builder.icon( icon );

        return builder;
    }

    @Override
    public void exportObject( final SiteTemplate siteTemplate, final Path rootPath, final String objectName )
        throws IOException
    {
        super.exportObject( siteTemplate, rootPath, "" );

        for ( final PageTemplate template : siteTemplate.getPageTemplates() )
        {
            final AbstractEntityExporter<PageTemplate, PageTemplate.Builder> exporter = EntityExporters.getForObject( template );
            final Path templatePath = rootPath.resolve( template.getName().toString() );
            exporter.exportObject( template, createPath( templatePath ), "" );
        }

        new SchemaIconDao().writeSchemaIcon( siteTemplate.getIcon(), rootPath );
    }

    private void importPageTemplate( final SiteTemplate.Builder siteTemplate, final Path templateDir )
        throws IOException
    {
        final Path templateFile = templateDir.resolve( PAGE_TEMPLATE_FILE );
        if ( !Files.isRegularFile( templateFile ) )
        {
            return;
        }
        final String pathSeparator = templateDir.getFileSystem().getSeparator();
        final String pathName = StringUtils.remove( templateDir.getFileName().toString(), pathSeparator );
        final PageTemplateName templateName = PageTemplateName.from( pathName );

        final AbstractEntityExporter<PageTemplate, PageTemplate.Builder> entityExporter = EntityExporters.getForClass( PageTemplate.class );

        final PageTemplate.Builder pageTemplate = entityExporter.importObject( templateDir, templateFile );
        pageTemplate.name( templateName );
        siteTemplate.addPageTemplate( pageTemplate.build() );
    }
}