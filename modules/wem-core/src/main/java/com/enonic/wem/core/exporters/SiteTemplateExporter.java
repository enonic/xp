package com.enonic.wem.core.exporters;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.page.Template;
import com.enonic.wem.api.content.page.image.ImageTemplate;
import com.enonic.wem.api.content.page.image.ImageTemplateKey;
import com.enonic.wem.api.content.page.layout.LayoutTemplate;
import com.enonic.wem.api.content.page.layout.LayoutTemplateKey;
import com.enonic.wem.api.content.page.part.PartTemplate;
import com.enonic.wem.api.content.page.part.PartTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.exception.SystemException;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.xml.XmlSerializers;
import com.enonic.wem.xml.template.SiteTemplateXml;

import static org.apache.commons.lang.StringUtils.substringAfterLast;

@XMLFilename("site-template.xml")
public final class SiteTemplateExporter
    extends AbstractEntityExporter<SiteTemplate, SiteTemplate.Builder>
{
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
                    importTemplates( builder, moduleDir, siteTemplateKey );
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
            final ModuleKey templateModule = template.getKey().getModuleKey();
            exporter.exportObject( template, createPath( rootPath.resolve( templateModule.toString() ) ), template.getName().toString() );
        }
    }

    private void importTemplates( final SiteTemplate.Builder parentEntry, final Path parentDirectory,
                                  final SiteTemplateKey siteTemplateKey )
        throws IOException
    {
        final String pathSeparator = parentDirectory.getFileSystem().getSeparator();
        final String pathName = StringUtils.remove( parentDirectory.getFileName().toString(), pathSeparator );
        final ModuleKey moduleKey = ModuleKey.from( pathName );
        try (final DirectoryStream<Path> ds = Files.newDirectoryStream( parentDirectory ))
        {
            for ( final Path file : ds )
            {
                final String filename = getTemplateFilenameSuffix( file );
                final AbstractEntityExporter<Template, Template.BaseTemplateBuilder> entityExporter =
                    EntityExporters.getByFilename( filename );
                final Template.BaseTemplateBuilder template = entityExporter.importObject( parentDirectory, file );
                setTemplateKey( siteTemplateKey, moduleKey, template );
                parentEntry.addTemplate( template.build() );
            }
        }
    }

    private String getTemplateFilenameSuffix( final Path file )
    {
        final String filename = file.getFileName().toString();
        final String ext = FilenameUtils.getExtension( filename );
        final String nameWithouExt = FilenameUtils.removeExtension( filename );
        return substringAfterLast( nameWithouExt, NAME_SEPARATOR ) + "." + ext;
    }

    private void setTemplateKey( final SiteTemplateKey siteTemplateKey, final ModuleKey moduleKey,
                                 final Template.BaseTemplateBuilder template )
    {
        if ( template instanceof ImageTemplate.Builder )
        {
            final ImageTemplate.Builder templateBuilder = (ImageTemplate.Builder) template;
            templateBuilder.key( ImageTemplateKey.from( siteTemplateKey, moduleKey, templateBuilder.getName() ) );
        }
        else if ( template instanceof PartTemplate.Builder )
        {
            final PartTemplate.Builder templateBuilder = (PartTemplate.Builder) template;
            templateBuilder.key( PartTemplateKey.from( siteTemplateKey, moduleKey, templateBuilder.getName() ) );
        }
        else if ( template instanceof PageTemplate.Builder )
        {
            final PageTemplate.Builder templateBuilder = (PageTemplate.Builder) template;
            templateBuilder.key( PageTemplateKey.from( siteTemplateKey, moduleKey, templateBuilder.getName() ) );
        }
        else if ( template instanceof LayoutTemplate.Builder )
        {
            final LayoutTemplate.Builder templateBuilder = (LayoutTemplate.Builder) template;
            templateBuilder.key( LayoutTemplateKey.from( siteTemplateKey, moduleKey, templateBuilder.getName() ) );
        }
        else
        {
            throw new SystemException( "Template [{0}] not supported", template.getClass().getSimpleName() );
        }
    }
}
