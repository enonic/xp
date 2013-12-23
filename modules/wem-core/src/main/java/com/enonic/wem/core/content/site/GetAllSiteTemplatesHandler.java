package com.enonic.wem.core.content.site;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;

import javax.inject.Inject;

import com.enonic.wem.api.command.content.site.GetAllSiteTemplates;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplates;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.config.SystemConfig;

public class GetAllSiteTemplatesHandler
    extends CommandHandler<GetAllSiteTemplates>
{
    private SystemConfig systemConfig;

    private SiteTemplateExporter siteTemplateExporter;

    @Override
    public void handle()
        throws Exception
    {
        final File templatesDir = systemConfig.getTemplatesDir();
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

        command.setResult( templatesBuilder.build() );
    }

    @Inject
    public void setSystemConfig( final SystemConfig systemConfig )
    {
        this.systemConfig = systemConfig;
    }

    @Inject
    public void setSiteTemplateExporter( final SiteTemplateExporter siteTemplateExporter )
    {
        this.siteTemplateExporter = siteTemplateExporter;
    }
}
