package com.enonic.wem.core.content.site;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplates;
import com.enonic.wem.api.util.Exceptions;
import com.enonic.wem.core.config.SystemConfig;

final class GetSiteTemplatesCommand
{
    private final static Logger LOG = LoggerFactory.getLogger( GetSiteTemplatesCommand.class );

    private SystemConfig systemConfig;

    private SiteTemplateExporter siteTemplateExporter;

    public SiteTemplates execute()
    {
        try
        {
            return doExecute();
        }
        catch ( IOException e )
        {
            throw Exceptions.newRutime( "Error retrieving site templates" ).withCause( e );
        }
    }

    private SiteTemplates doExecute()
        throws IOException
    {
        final File templatesDir = systemConfig.getTemplatesDir().toFile();
        File[] allTemplateDirs = templatesDir.listFiles( pathname -> Files.isDirectory( pathname.toPath() ) );

        SiteTemplates.Builder templatesBuilder = new SiteTemplates.Builder();
        for ( File templateDir : allTemplateDirs )
        {
            try
            {
                final SiteTemplate siteTemplate = siteTemplateExporter.importFromDirectory( templateDir.toPath() ).build();
                templatesBuilder.add( siteTemplate );
            }
            catch ( Exception e )
            {
                LOG.error( "Could not load site template from " + templateDir.toPath(), e );
            }
        }

        return templatesBuilder.build();
    }

    public GetSiteTemplatesCommand systemConfig( final SystemConfig systemConfig )
    {
        this.systemConfig = systemConfig;
        return this;
    }

    public GetSiteTemplatesCommand siteTemplateExporter( final SiteTemplateExporter siteTemplateExporter )
    {
        this.siteTemplateExporter = siteTemplateExporter;
        return this;
    }
}
