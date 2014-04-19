package com.enonic.wem.core.content.site;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplateNotFoundException;
import com.enonic.wem.core.config.SystemConfig;
import com.enonic.wem.api.util.Exceptions;

final class GetSiteTemplateCommand
{
    private SiteTemplateKey key;

    private SystemConfig systemConfig;

    private SiteTemplateExporter siteTemplateExporter;

    public SiteTemplate execute()
    {
        try
        {
            return doExecute();
        }
        catch ( IOException e )
        {
            throw Exceptions.newRutime( "Error retrieving site template [{0}]", this.key ).withCause( e );
        }
    }

    private SiteTemplate doExecute()
        throws IOException
    {
        final Path templatesDir = systemConfig.getTemplatesDir();
        final Path templateDir = templatesDir.resolve( key.toString() );

        if ( Files.isDirectory( templateDir ) )
        {
            final SiteTemplate.Builder siteTemplate = siteTemplateExporter.importFromDirectory( templateDir );

            if ( siteTemplate == null )
            {
                throw new SiteTemplateNotFoundException( key );
            }

            return siteTemplate.build();
        }

        throw new SiteTemplateNotFoundException( key );
    }

    public GetSiteTemplateCommand systemConfig( final SystemConfig systemConfig )
    {
        this.systemConfig = systemConfig;
        return this;
    }

    public GetSiteTemplateCommand siteTemplateExporter( final SiteTemplateExporter siteTemplateExporter )
    {
        this.siteTemplateExporter = siteTemplateExporter;
        return this;
    }

    public GetSiteTemplateCommand key( final SiteTemplateKey key )
    {
        this.key = key;
        return this;
    }
}
