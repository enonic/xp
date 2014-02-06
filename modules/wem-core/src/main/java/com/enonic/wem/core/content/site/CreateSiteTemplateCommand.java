package com.enonic.wem.core.content.site;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.enonic.wem.api.content.page.Template;
import com.enonic.wem.api.content.site.CreateSiteTemplateSpec;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.core.config.SystemConfig;
import com.enonic.wem.util.Exceptions;

final class CreateSiteTemplateCommand
{
    private CreateSiteTemplateSpec spec;

    private SystemConfig systemConfig;

    private SiteTemplateExporter siteTemplateExporter;

    public SiteTemplate execute()
    {
        this.spec.validate();

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
        final SiteTemplateKey siteTemplatekey = SiteTemplateKey.from( this.spec.getName(), this.spec.getVersion() );
        final SiteTemplate.Builder builder = SiteTemplate.newSiteTemplate().
            displayName( this.spec.getDisplayName() ).
            url( this.spec.getUrl() ).
            vendor( this.spec.getVendor() ).
            key( siteTemplatekey ).
            modules( this.spec.getModules() ).
            description( this.spec.getDescription() ).
            rootContentType( this.spec.getRootContentType() ).
            contentTypeFilter( this.spec.getContentTypeFilter() );

        for ( Template template : spec.getTemplates() )
        {
            builder.addTemplate( template );
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

    public CreateSiteTemplateCommand spec( final CreateSiteTemplateSpec spec )
    {
        this.spec = spec;
        return this;
    }
}
