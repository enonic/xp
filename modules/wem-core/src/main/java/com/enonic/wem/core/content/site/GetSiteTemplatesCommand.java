package com.enonic.wem.core.content.site;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;

import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplates;
import com.enonic.wem.core.config.SystemConfig;
import com.enonic.wem.api.util.Exceptions;

final class GetSiteTemplatesCommand
{
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
        File[] allTemplateDirs = templatesDir.listFiles( new FileFilter()
        {
            @Override
            public boolean accept( final File pathname )
            {
                return Files.isDirectory( pathname.toPath() );
            }
        } );

        SiteTemplates.Builder templatesBuilder = new SiteTemplates.Builder();
        for ( File templateDir : allTemplateDirs )
        {
            final SiteTemplate siteTemplate = siteTemplateExporter.importFromDirectory( templateDir.toPath() ).build();
            templatesBuilder.add( siteTemplate );
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
