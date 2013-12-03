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
import com.enonic.wem.admin.rest.resource.content.site.template.json.DeleteSiteTemplateJson;
import com.enonic.wem.admin.rest.resource.content.site.template.json.DeleteSiteTemplateResultJson;
import com.enonic.wem.admin.rest.resource.content.site.template.json.GetSiteTemplateResultJson;
import com.enonic.wem.admin.rest.resource.content.site.template.json.ImportSiteTemplateResultJson;
import com.enonic.wem.admin.rest.resource.content.site.template.json.ListSiteTemplateJson;
import com.enonic.wem.admin.rest.resource.content.site.template.json.ListSiteTemplateResultJson;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.site.CreateSiteTemplate;
import com.enonic.wem.api.command.content.site.DeleteSiteTemplate;
import com.enonic.wem.api.command.content.site.GetAllSiteTemplates;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplateNotFoundException;
import com.enonic.wem.api.content.site.SiteTemplates;
import com.enonic.wem.core.exporters.SiteTemplateExporter;

@javax.ws.rs.Path("content/site/template")
@Produces(MediaType.APPLICATION_JSON)
public final class SiteTemplateResource
    extends AbstractResource
{
    @GET
    @javax.ws.rs.Path("list")
    public ListSiteTemplateResultJson listSiteTemplate()
    {
        GetAllSiteTemplates listCommand = Commands.site().template().get().all();
        try
        {
            SiteTemplates siteTemplates = client.execute( listCommand );
            return ListSiteTemplateResultJson.result( new ListSiteTemplateJson( siteTemplates ) );
        }
        catch ( Exception e )
        {
            return ListSiteTemplateResultJson.error( e.getMessage() );
        }
    }

    @POST
    @javax.ws.rs.Path("delete")
    @Consumes(MediaType.APPLICATION_JSON)
    public DeleteSiteTemplateResultJson deleteSiteTemplate( final DeleteSiteTemplateJson params )
    {
        final DeleteSiteTemplate command = Commands.site().template().delete( params.getKey() );
        try
        {
            final SiteTemplateKey key = client.execute( command );

            return DeleteSiteTemplateResultJson.result( key );
        }
        catch ( Exception e )
        {
            return DeleteSiteTemplateResultJson.error( e.getMessage() );
        }
    }

    @GET
    public GetSiteTemplateResultJson getSiteTemplate( @QueryParam("key") final String siteTemplateKeyParam )
    {
        final SiteTemplateKey siteTemplateKey = SiteTemplateKey.from( siteTemplateKeyParam );
        try
        {
            final SiteTemplate siteTemplate = client.execute( Commands.site().template().get().byKey( siteTemplateKey ) );
            return GetSiteTemplateResultJson.result( new SiteTemplateJson( siteTemplate ) );
        }
        catch ( SiteTemplateNotFoundException e )
        {
            return GetSiteTemplateResultJson.error( String.format( "Site Template [%s] not found", siteTemplateKeyParam ) );
        }
    }

    @POST
    @javax.ws.rs.Path("import")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public ImportSiteTemplateResultJson importSiteTemplate( @FormDataParam("file") InputStream uploadedInputStream,
                                                            @FormDataParam("file") FormDataContentDisposition fileDetail )
        throws IOException
    {
        final String fileName = fileDetail.getFileName();

        final Path tempDirectory = Files.createTempDirectory( "modules" );
        try
        {
            final Path tempZipFile = tempDirectory.resolve( fileName );
            Files.copy( uploadedInputStream, tempZipFile );
            final SiteTemplateExporter siteTemplateImporter = new SiteTemplateExporter();
            final SiteTemplate importedSiteTemplate;
            try
            {
                importedSiteTemplate = siteTemplateImporter.importFromZip( tempZipFile );
            }
            catch ( Exception e )
            {
                return ImportSiteTemplateResultJson.error( e.getMessage() );
            }

            final CreateSiteTemplate createSiteTemplateCommand = CreateSiteTemplate.fromSiteTemplate( importedSiteTemplate );
            final SiteTemplate createdSiteTemplate = client.execute( createSiteTemplateCommand );

            return ImportSiteTemplateResultJson.result( new SiteTemplateSummaryJson( createdSiteTemplate ) );
        }
        finally
        {
            FileUtils.deleteDirectory( tempDirectory.toFile() );
        }
    }

}
