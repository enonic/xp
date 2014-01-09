package com.enonic.wem.core.content.site;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

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
import com.enonic.wem.api.content.site.SiteTemplateXml;
import com.enonic.wem.api.exception.SystemException;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeys;
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
            final Path templatePath = rootPath.resolve( templateModule.getName().toString() ).resolve( template.getName().toString() );
            exporter.exportObject( template, createPath( templatePath ), "" );
        }
    }

    private void importTemplates( final SiteTemplate.Builder siteTemplate, final Path parentDirectory,
                                  final SiteTemplateKey siteTemplateKey )
        throws IOException
    {
        final String pathSeparator = parentDirectory.getFileSystem().getSeparator();
        final String pathName = StringUtils.remove( parentDirectory.getFileName().toString(), pathSeparator );
        final ModuleName moduleName = ModuleName.from( pathName );
        final ModuleKey moduleKey = resolveModuleVersion( moduleName, siteTemplate );

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
                            setTemplateKey( siteTemplateKey, moduleKey, template );
                            siteTemplate.addTemplate( template.build() );
                            break;
                        }
                    }
                }
            }
        }
    }

    private ModuleKey resolveModuleVersion( final ModuleName moduleName, final SiteTemplate.Builder siteTemplate )
    {
        final SiteTemplate st = siteTemplate.build();
        final ModuleKeys siteTemplateModules = st.getModules();
        for ( ModuleKey moduleKey : siteTemplateModules )
        {
            if ( moduleKey.getName().equals( moduleName ) )
            {
                return moduleKey;
            }
        }
        throw new SystemException( "Could not resolve version for module [{0}] in site template [{1}]", moduleName, st.getKey() );
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
            throw new UnsupportedOperationException( "Template [" + template.getClass().getName() + "] not supported" );
        }
    }
}
