package com.enonic.wem.core.content.site;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;

import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateEditor;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplateNotFoundException;
import com.enonic.wem.api.content.site.UpdateSiteTemplateParams;
import com.enonic.wem.core.config.SystemConfig;
import com.enonic.wem.api.util.Exceptions;

final class UpdateSiteTemplateCommand
{
    private UpdateSiteTemplateParams param;

    private SystemConfig systemConfig;

    private SiteTemplateExporter siteTemplateExporter;

    public SiteTemplate execute()
    {
        this.param.validate();

        try
        {
            return doExecute();
        }
        catch ( final IOException e )
        {
            throw Exceptions.newRutime( "Error updating site template" ).withCause( e );
        }
    }

    private SiteTemplate doExecute()
        throws IOException
    {
        final SiteTemplateKey key = this.param.getKey();
        final SiteTemplateEditor editor = this.param.getEditor();

        final SiteTemplate persistedSiteTemplate = getSiteTemplate( key );
        if ( persistedSiteTemplate == null )
        {
            throw new SiteTemplateNotFoundException( key );
        }

        final SiteTemplate editedSiteTemplate = editor.edit( persistedSiteTemplate );
        if ( ( editedSiteTemplate == null ) || ( editedSiteTemplate == persistedSiteTemplate ) )
        {
            return persistedSiteTemplate;
        }
        persistSiteTemplate( editedSiteTemplate );

        return editedSiteTemplate;
    }

    private void persistSiteTemplate( final SiteTemplate siteTemplate )
        throws IOException
    {
        final Path templatesPath = this.systemConfig.getTemplatesDir();
        Files.createDirectories( templatesPath );

        // delete current site template directory contents
        final Path siteTemplateDirectory = templatesPath.resolve( siteTemplate.getKey().toString() );
        FileUtils.deleteDirectory( siteTemplateDirectory.toFile() );

        // persist updated site template
        this.siteTemplateExporter.exportToDirectory( siteTemplate, templatesPath );
    }

    private SiteTemplate getSiteTemplate( final SiteTemplateKey key )
        throws IOException
    {
        final Path templatesDir = systemConfig.getTemplatesDir();
        final Path templateDir = templatesDir.resolve( key.toString() );

        if ( Files.isDirectory( templateDir ) )
        {
            final SiteTemplate.Builder builder = siteTemplateExporter.importFromDirectory( templateDir );
            return builder == null ? null : builder.build();
        }
        return null;
    }

    public UpdateSiteTemplateCommand systemConfig( final SystemConfig systemConfig )
    {
        this.systemConfig = systemConfig;
        return this;
    }

    public UpdateSiteTemplateCommand siteTemplateExporter( final SiteTemplateExporter siteTemplateExporter )
    {
        this.siteTemplateExporter = siteTemplateExporter;
        return this;
    }

    public UpdateSiteTemplateCommand param( final UpdateSiteTemplateParams param )
    {
        this.param = param;
        return this;
    }
}
