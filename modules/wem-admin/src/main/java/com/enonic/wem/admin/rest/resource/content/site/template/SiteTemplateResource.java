package com.enonic.wem.admin.rest.resource.content.site.template;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import com.enonic.wem.admin.json.content.site.SiteTemplateJson;
import com.enonic.wem.admin.json.content.site.SiteTemplateSummaryJson;
import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.admin.rest.resource.Result;
import com.enonic.wem.admin.rest.resource.content.site.template.json.DeleteSiteTemplateJson;
import com.enonic.wem.admin.rest.resource.content.site.template.json.ListSiteTemplateJson;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.site.CreateSiteTemplate;
import com.enonic.wem.api.command.content.site.DeleteSiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplates;
import com.enonic.wem.core.exporters.SiteTemplateExporter;

@javax.ws.rs.Path("content/site/template")
@Produces(MediaType.APPLICATION_JSON)
public final class SiteTemplateResource
    extends AbstractResource
{
    @GET
    @javax.ws.rs.Path("list")
    public Result listSiteTemplate()
    {
        try
        {
            SiteTemplates siteTemplates = client.execute( Commands.site().template().get().all() );
            return Result.result( new ListSiteTemplateJson( siteTemplates ) );
        }
        catch ( Exception e )
        {
            return Result.exception( e );
        }
    }

    @POST
    @javax.ws.rs.Path("delete")
    @Consumes(MediaType.APPLICATION_JSON)
    public Result deleteSiteTemplate( final DeleteSiteTemplateJson params )
    {
        try
        {
            final DeleteSiteTemplate command = Commands.site().template().delete( params.getKey() );

            final SiteTemplateKey keyOfDeletedSiteTemplate = client.execute( command );

            return Result.result( keyOfDeletedSiteTemplate.toString() );
        }
        catch ( Exception e )
        {
            return Result.exception( e );
        }
    }

    @GET
    public Result getSiteTemplate( @QueryParam("key") final String siteTemplateKeyParam )
    {
        try
        {
            final SiteTemplateKey siteTemplateKey = SiteTemplateKey.from( siteTemplateKeyParam );

            final SiteTemplate siteTemplate = client.execute( Commands.site().template().get().byKey( siteTemplateKey ) );
            return Result.result( new SiteTemplateJson( siteTemplate ) );
        }
        catch ( Exception e )
        {
            return Result.exception( e );
        }
    }

    @POST
    @javax.ws.rs.Path("import")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Result importSiteTemplate( @FormDataParam("file") InputStream uploadedInputStream,
                                      @FormDataParam("file") FormDataContentDisposition formDataContentDisposition )
        throws IOException
    {

        Path tempDirectory = null;
        try
        {
            try
            {
                tempDirectory = Files.createTempDirectory( "modules" );
                final String fileName = formDataContentDisposition.getFileName();
                final Path tempZipFile = tempDirectory.resolve( fileName );
                Files.copy( uploadedInputStream, tempZipFile );
                final SiteTemplateExporter siteTemplateImporter = new SiteTemplateExporter();
                final SiteTemplate importedSiteTemplate;

                importedSiteTemplate = siteTemplateImporter.importFromZip( tempZipFile ).build();

                final CreateSiteTemplate createSiteTemplateCommand = CreateSiteTemplate.fromSiteTemplate( importedSiteTemplate );
                final SiteTemplate createdSiteTemplate = client.execute( createSiteTemplateCommand );

                return Result.result( new SiteTemplateSummaryJson( createdSiteTemplate ) );
            }
            catch ( Exception e )
            {
                return Result.error( e.getMessage() );
            }
        }
        finally
        {
            if ( tempDirectory != null )
            {
                FileUtils.deleteDirectory( tempDirectory.toFile() );
            }
        }
    }

}
