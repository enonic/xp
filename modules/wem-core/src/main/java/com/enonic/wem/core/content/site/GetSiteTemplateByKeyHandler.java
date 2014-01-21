package com.enonic.wem.core.content.site;

import java.nio.file.Files;
import java.nio.file.Path;

import javax.inject.Inject;

import com.enonic.wem.api.command.content.site.GetSiteTemplateByKey;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplateNotFoundException;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.config.SystemConfig;

public class GetSiteTemplateByKeyHandler
    extends CommandHandler<GetSiteTemplateByKey>
{
    private SystemConfig systemConfig;

    private SiteTemplateExporter siteTemplateExporter;

    @Override
    public void handle()
        throws Exception
    {
        final Path templatesDir = systemConfig.getTemplatesDir();
        final SiteTemplateKey templateKey = command.getKey();
        final Path templateDir = templatesDir.resolve( templateKey.toString() );

        if ( Files.isDirectory( templateDir ) )
        {
            final SiteTemplate.Builder builder = siteTemplateExporter.importFromDirectory( templateDir );

            if ( builder == null )
            {
                throw new SiteTemplateNotFoundException( templateKey );
            }

            final SiteTemplate siteTemplate = builder.build();
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
