package com.enonic.wem.core.content.site;

import java.io.File;

import javax.inject.Inject;

import com.enonic.wem.api.command.content.site.GetSiteTemplateByKey;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplateNotFoundException;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.config.SystemConfig;
import com.enonic.wem.core.exporters.SiteTemplateExporter;

public class GetSiteTemplateByKeyHandler
    extends CommandHandler<GetSiteTemplateByKey>
{
    private SystemConfig systemConfig;

    private SiteTemplateExporter siteTemplateExporter;

    @Override
    public void handle()
        throws Exception
    {
        final File templatesDir = systemConfig.getTemplatesDir();
        final SiteTemplateKey templateKey = command.getKey();
        final File templateDir = new File( templatesDir, templateKey.toString() );

        if ( templateDir.exists() )
        {
            final SiteTemplate siteTemplate = siteTemplateExporter.importFromDirectory( templateDir.toPath() );
            command.setResult( siteTemplate );
            return;
        }

        throw new SiteTemplateNotFoundException( templateKey );
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
