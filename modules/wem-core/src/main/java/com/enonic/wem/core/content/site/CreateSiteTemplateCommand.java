package com.enonic.wem.core.content.site;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.site.CreateSiteTemplateParams;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.util.Exceptions;
import com.enonic.wem.core.config.SystemConfig;

final class CreateSiteTemplateCommand
{
    private CreateSiteTemplateParams param;

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
            throw Exceptions.newRutime( "Error creating site template" ).withCause( e );
        }
    }

    private SiteTemplate doExecute()
        throws IOException
    {
        final SiteTemplateKey siteTemplatekey = SiteTemplateKey.from( this.param.getName(), this.param.getVersion() );
        final SiteTemplate.Builder builder = SiteTemplate.newSiteTemplate().
            displayName( this.param.getDisplayName() ).
            url( this.param.getUrl() ).
            vendor( this.param.getVendor() ).
            key( siteTemplatekey ).
            modules( this.param.getModules() ).
            description( this.param.getDescription() ).
            contentTypeFilter( this.param.getContentTypeFilter() );

        for ( PageTemplate template : param.getTemplates() )
        {
            builder.addPageTemplate( template );
        }
        final SiteTemplate siteTemplate = builder.build();

        final Path templatesPath = this.systemConfig.getTemplatesDir();
        Files.createDirectories( templatesPath );

        this.siteTemplateExporter.exportToDirectory( siteTemplate, templatesPath );
        return siteTemplate;
    }

    public CreateSiteTemplateCommand systemConfig( final SystemConfig systemConfig )
    {
        this.systemConfig = systemConfig;
        return this;
    }

    public CreateSiteTemplateCommand siteTemplateExporter( final SiteTemplateExporter siteTemplateExporter )
    {
        this.siteTemplateExporter = siteTemplateExporter;
        return this;
    }

    public CreateSiteTemplateCommand param( final CreateSiteTemplateParams param )
    {
        this.param = param;
        return this;
    }
}
