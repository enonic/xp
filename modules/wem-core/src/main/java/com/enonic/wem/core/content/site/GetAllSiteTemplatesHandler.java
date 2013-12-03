package com.enonic.wem.core.content.site;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.enonic.wem.api.command.content.site.GetAllSiteTemplates;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplates;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.config.SystemConfig;
import com.enonic.wem.core.exporters.SiteTemplateExporter;

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

        List<SiteTemplate> templates = new ArrayList<>();
        for ( File templateDir : allTemplateDirs )
        {
            final SiteTemplate siteTemplate = siteTemplateExporter.importFromDirectory( templateDir.toPath() );
            templates.add( siteTemplate );
        }

        command.setResult( SiteTemplates.from( templates ) );
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
