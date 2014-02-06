package com.enonic.wem.core.content.site;

import java.nio.file.Files;
import java.nio.file.Path;

import javax.inject.Inject;

import com.enonic.wem.api.command.content.site.CreateSiteTemplate;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.config.SystemConfig;

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
            url( command.getUrl() ).
            vendor( command.getVendor() ).
            key( command.getSiteTemplateKey() ).
            modules( command.getModules() ).
            description( command.getDescription() ).
            rootContentType( command.getRootContentType() ).
            contentTypeFilter( command.getContentTypeFilter() );

        for ( PageTemplate template : command.getPageTemplates() )
        {
            builder.addPageTemplate( template );
        }
        final SiteTemplate siteTemplate = builder.build();

        final Path templatesPath = systemConfig.getTemplatesDir();
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
