package com.enonic.wem.core.content.site;

import java.nio.file.Files;
import java.nio.file.Path;

import javax.inject.Inject;

import com.enonic.wem.api.command.content.site.CreateSiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.config.SystemConfig;
import com.enonic.wem.core.exporters.SiteTemplateExporter;

public class CreateSiteTemplateHandler
    extends CommandHandler<CreateSiteTemplate>
{
    private SystemConfig systemConfig;

    private SiteTemplateExporter siteTemplateExporter;

    @Override
    public void handle()
        throws Exception
    {
        final SiteTemplate.Builder builder = SiteTemplate.newSiteTemplate().
            displayName( command.getDisplayName() ).
            vendor( command.getVendor() ).
            key( command.getSiteTemplateKey() ).
            modules( command.getModules() ).
            templates( command.getTemplates() ).
            info( command.getDescription() ).
            rootContentType( command.getRootContentType() ).
            contentTypeFilter( command.getSupportedContentTypes() );

        final SiteTemplate siteTemplate = builder.build();

        final Path templatesPath = systemConfig.getTemplatesDir().toPath();
        Files.createDirectories( templatesPath );

        siteTemplateExporter.exportToDirectory( siteTemplate, templatesPath );
        command.setResult( siteTemplate );
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
