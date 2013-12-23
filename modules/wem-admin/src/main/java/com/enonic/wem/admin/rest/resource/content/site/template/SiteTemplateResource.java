package com.enonic.wem.admin.rest.resource.content.site.template;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import com.enonic.wem.admin.json.content.site.SiteTemplateJson;
import com.enonic.wem.admin.json.content.site.SiteTemplateSummaryJson;
import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.admin.rest.resource.content.site.template.json.DeleteSiteTemplateJson;
import com.enonic.wem.admin.rest.resource.content.site.template.json.ListSiteTemplateJson;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.site.CreateSiteTemplate;
import com.enonic.wem.api.command.content.site.DeleteSiteTemplate;
import com.enonic.wem.api.command.content.site.GetSiteTemplateByKey;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplateNotFoundException;
import com.enonic.wem.api.content.site.SiteTemplates;
import com.enonic.wem.core.content.site.SiteTemplateExporter;

@javax.ws.rs.Path("content/site/template")
@Produces(MediaType.APPLICATION_JSON)
public final class SiteTemplateResource
    extends AbstractResource
{
    private static final String ZIP_MIME_TYPE = "application/zip";

    @GET
    @javax.ws.rs.Path("list")
    public ListSiteTemplateJson listSiteTemplate()
    {
        SiteTemplates siteTemplates = client.execute( Commands.site().template().get().all() );
        return new ListSiteTemplateJson( siteTemplates );
    }

    @POST
    @javax.ws.rs.Path("delete")
    @Consumes(MediaType.APPLICATION_JSON)
    public HashMap<String, String> deleteSiteTemplate( final DeleteSiteTemplateJson params )
    {
        final DeleteSiteTemplate command = Commands.site().template().delete( params.getKey() );

        final SiteTemplateKey siteTemplateKey = client.execute( command );

        final HashMap<String, String> map = new HashMap<>();
        map.put( "result", siteTemplateKey.toString() );
        return map;
    }

    @GET
    public SiteTemplateJson getSiteTemplate( @QueryParam("siteTemplateKey") final String siteTemplateKeyParam )
    {
        final SiteTemplateKey siteTemplateKey = SiteTemplateKey.from( siteTemplateKeyParam );

        final SiteTemplate siteTemplate = client.execute( Commands.site().template().get().byKey( siteTemplateKey ) );
        return new SiteTemplateJson( siteTemplate );
    }

    @POST
    @javax.ws.rs.Path("import")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public SiteTemplateSummaryJson importSiteTemplate( @FormDataParam("file") InputStream uploadedInputStream,
                                                       @FormDataParam("file") FormDataContentDisposition formDataContentDisposition )
        throws IOException
    {

        Path tempDirectory = null;
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

            return new SiteTemplateSummaryJson( createdSiteTemplate );
        }
        finally
        {
            if ( tempDirectory != null )
            {
                FileUtils.deleteDirectory( tempDirectory.toFile() );
            }
        }
    }

    @GET
    @javax.ws.rs.Path("export")
    public javax.ws.rs.core.Response export( @QueryParam("siteTemplateKey") final String siteTemplateKeyParam )
        throws IOException
    {
        final SiteTemplateKey siteTemplateKey;
        try
        {
            siteTemplateKey = SiteTemplateKey.from( siteTemplateKeyParam );
        }
        catch ( Exception e )
        {
            return Response.status( Response.Status.BAD_REQUEST ).build();
        }

        final GetSiteTemplateByKey getSiteTemplate = Commands.site().template().get().byKey( siteTemplateKey );
        final SiteTemplate siteTemplate;
        try
        {
            siteTemplate = client.execute( getSiteTemplate );
        }
        catch ( SiteTemplateNotFoundException e )
        {
            return Response.status( Response.Status.NOT_FOUND ).build();
        }

        final Path tempDirectory = Files.createTempDirectory( "templates" );
        try
        {
            final SiteTemplateExporter exporter = new SiteTemplateExporter();
            final Path path = exporter.exportToZip( siteTemplate, tempDirectory );
            final byte[] zipContents = Files.readAllBytes( path );

            final String fileName = path.getFileName().toString();
            return Response.ok( zipContents, ZIP_MIME_TYPE ).header( "Content-Disposition", "attachment; filename=" + fileName ).build();
        }
        finally
        {
            FileUtils.deleteDirectory( tempDirectory.toFile() );
        }
    }
}
