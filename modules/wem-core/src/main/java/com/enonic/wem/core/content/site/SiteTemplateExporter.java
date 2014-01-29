package com.enonic.wem.core.content.site;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.Template;
import com.enonic.wem.api.content.page.image.ImageTemplate;
import com.enonic.wem.api.content.page.layout.LayoutTemplate;
import com.enonic.wem.api.content.page.part.PartTemplate;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplateXml;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.core.support.export.AbstractEntityExporter;
import com.enonic.wem.core.support.export.EntityExporters;
import com.enonic.wem.core.support.export.XMLFilename;
import com.enonic.wem.xml.XmlSerializers;

@XMLFilename("site.xml")
public final class SiteTemplateExporter
    extends AbstractEntityExporter<SiteTemplate, SiteTemplate.Builder>
{
    private static final String TEMPLATE_FILE_PATTERN = "*-template.xml";

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
            for ( Path moduleDir : directoryStream )
            {
                if ( Files.isDirectory( moduleDir ) )
                {
                    importTemplates( builder, moduleDir );
                }
            }
        }

        return builder;
    }

    @Override
    public void exportObject( final SiteTemplate siteTemplate, final Path rootPath, final String objectName )
        throws IOException
    {
        super.exportObject( siteTemplate, rootPath, "" );

        for ( final Template template : siteTemplate )
        {
            final AbstractEntityExporter<Template, Template.BaseTemplateBuilder> exporter = EntityExporters.getForObject( template );
            final ModuleName module = template.getKey().getModuleName();
            final Path templatePath = rootPath.resolve( module.toString() ).resolve( template.getName().toString() );
            exporter.exportObject( template, createPath( templatePath ), "" );
        }
    }

    private void importTemplates( final SiteTemplate.Builder siteTemplate, final Path parentDirectory )
        throws IOException
    {
        final String pathSeparator = parentDirectory.getFileSystem().getSeparator();
        final String pathName = StringUtils.remove( parentDirectory.getFileName().toString(), pathSeparator );
        final ModuleName moduleName = ModuleName.from( pathName );

        try (final DirectoryStream<Path> ds = Files.newDirectoryStream( parentDirectory ))
        {
            for ( final Path templateDir : ds )
            {
                if ( !Files.isDirectory( templateDir ) || IGNORE_FILES.contains( getFileName( templateDir ) ) )
                {
                    continue;
                }
                try (final DirectoryStream<Path> templateFileDs = Files.newDirectoryStream( templateDir, TEMPLATE_FILE_PATTERN ))
                {
                    for ( final Path templateFile : templateFileDs )
                    {
                        final String filename = templateFile.getFileName().toString();
                        final AbstractEntityExporter<Template, Template.BaseTemplateBuilder> entityExporter =
                            EntityExporters.getByFilename( filename );
                        if ( entityExporter != null )
                        {
                            final Template.BaseTemplateBuilder template = entityExporter.importObject( parentDirectory, templateFile );
                            setTemplateKey( moduleName, template );
                            siteTemplate.addTemplate( template.build() );
                            break;
                        }
                    }
                }
            }
        }
    }

    private void setTemplateKey( final ModuleName moduleName, final Template.BaseTemplateBuilder template )
    {
        if ( template instanceof ImageTemplate.Builder )
        {
            final ImageTemplate.Builder templateBuilder = (ImageTemplate.Builder) template;
            templateBuilder.module( moduleName );
        }
        else if ( template instanceof PartTemplate.Builder )
        {
            final PartTemplate.Builder templateBuilder = (PartTemplate.Builder) template;
            templateBuilder.module( moduleName );
        }
        else if ( template instanceof PageTemplate.Builder )
        {
            final PageTemplate.Builder templateBuilder = (PageTemplate.Builder) template;
            templateBuilder.module( moduleName );
        }
        else if ( template instanceof LayoutTemplate.Builder )
        {
            final LayoutTemplate.Builder templateBuilder = (LayoutTemplate.Builder) template;
            templateBuilder.module( moduleName );
        }
        else
        {
            throw new UnsupportedOperationException( "Template [" + template.getClass().getName() + "] not supported" );
        }
    }
}
