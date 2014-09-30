package com.enonic.wem.admin.rest.resource.content.site.template;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FileUtils;

import com.google.common.base.Strings;

import com.enonic.wem.admin.json.content.site.SiteTemplateJson;
import com.enonic.wem.admin.json.content.site.SiteTemplateSummaryJson;
import com.enonic.wem.admin.rest.multipart.MultipartForm;
import com.enonic.wem.admin.rest.resource.content.site.template.json.CreateSiteTemplateJson;
import com.enonic.wem.admin.rest.resource.content.site.template.json.DeleteSiteTemplateJson;
import com.enonic.wem.admin.rest.resource.content.site.template.json.ListSiteTemplateJson;
import com.enonic.wem.admin.rest.resource.content.site.template.json.ListTemplateItemJson;
import com.enonic.wem.admin.rest.resource.content.site.template.json.UpdateSiteTemplateJson;
import com.enonic.wem.admin.rest.resource.schema.content.ContentTypeIconResolver;
import com.enonic.wem.admin.rest.resource.schema.content.ContentTypeIconUrlResolver;
import com.enonic.wem.api.content.site.CreateSiteTemplateParams;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplateNotFoundException;
import com.enonic.wem.api.content.site.SiteTemplateService;
import com.enonic.wem.api.content.site.SiteTemplates;
import com.enonic.wem.api.content.site.UpdateSiteTemplateParams;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.core.content.site.SiteTemplateExporter;

@javax.ws.rs.Path("content/site/template")
@Produces(MediaType.APPLICATION_JSON)
public final class SiteTemplateResource
{
    private static final String ZIP_MIME_TYPE = "application/zip";

    private SiteTemplateService siteTemplateService;

    private SiteTemplateIconUrlResolver siteTemplateIconUrlResolver;

    @GET
    @javax.ws.rs.Path("list")
    public ListSiteTemplateJson listSiteTemplate()
    {
        SiteTemplates siteTemplates = this.siteTemplateService.getSiteTemplates();
        return new ListSiteTemplateJson( siteTemplates, this.siteTemplateIconUrlResolver );
    }

    @GET
    @javax.ws.rs.Path("tree")
    public ListTemplateItemJson listTemplates( @QueryParam("parentId") final String parentIdParam )
    {
        if ( Strings.isNullOrEmpty( parentIdParam ) )
        {
            SiteTemplates siteTemplates = this.siteTemplateService.getSiteTemplates();
            return new ListTemplateItemJson( siteTemplates, this.siteTemplateIconUrlResolver );
        }
        else
        {
            final SiteTemplateKey siteTemplateKey = SiteTemplateKey.from( parentIdParam );
            final SiteTemplate siteTemplate = siteTemplateService.getSiteTemplate( siteTemplateKey );
            return new ListTemplateItemJson( siteTemplateKey, siteTemplate.getPageTemplates() );
        }
    }

    @POST
    @javax.ws.rs.Path("delete")
    @Consumes(MediaType.APPLICATION_JSON)
    public HashMap<String, String> deleteSiteTemplate( final DeleteSiteTemplateJson params )
    {
        final SiteTemplateKey key = params.getKey();
        this.siteTemplateService.deleteSiteTemplate( key );

        final HashMap<String, String> map = new HashMap<>();
        map.put( "result", key.toString() );
        return map;
    }

    @POST
    @javax.ws.rs.Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    public SiteTemplateJson create( final CreateSiteTemplateJson params )
    {
        final CreateSiteTemplateParams command = params.getCommand();
        final SiteTemplate siteTemplate = this.siteTemplateService.createSiteTemplate( command );

        return new SiteTemplateJson( siteTemplate, this.siteTemplateIconUrlResolver );
    }

    @POST
    @javax.ws.rs.Path("update")
    @Consumes(MediaType.APPLICATION_JSON)
    public SiteTemplateJson update( final UpdateSiteTemplateJson params )
    {
        final UpdateSiteTemplateParams command = params.getCommand();
        final SiteTemplate siteTemplate = this.siteTemplateService.updateSiteTemplate( command );

        return new SiteTemplateJson( siteTemplate, this.siteTemplateIconUrlResolver );
    }

    @GET
    public SiteTemplateJson getSiteTemplate( @QueryParam("siteTemplateKey") final String siteTemplateKeyParam )
    {
        final SiteTemplateKey siteTemplateKey = SiteTemplateKey.from( siteTemplateKeyParam );

        final SiteTemplate siteTemplate = this.siteTemplateService.getSiteTemplate( siteTemplateKey );
        return new SiteTemplateJson( siteTemplate, this.siteTemplateIconUrlResolver );
    }

    @POST
    @javax.ws.rs.Path("import")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public SiteTemplateSummaryJson importSiteTemplate( final MultipartForm form )
        throws IOException
    {
        Path tempDirectory = null;
        final FileItem file = form.get( "file" );

        try
        {
            tempDirectory = Files.createTempDirectory( "modules" );

            final String fileName = file.getName();
            final Path tempZipFile = tempDirectory.resolve( fileName );
            Files.copy( file.getInputStream(), tempZipFile );

            final SiteTemplateExporter siteTemplateImporter = new SiteTemplateExporter();
            final SiteTemplate importedSiteTemplate;

            importedSiteTemplate = siteTemplateImporter.importFromZip( tempZipFile ).build();

            final CreateSiteTemplateParams createSiteTemplate = CreateSiteTemplateParams.fromSiteTemplate( importedSiteTemplate );
            final SiteTemplate createdSiteTemplate = siteTemplateService.createSiteTemplate( createSiteTemplate );

            return new SiteTemplateSummaryJson( createdSiteTemplate, siteTemplateIconUrlResolver );
        }
        finally
        {
            form.delete();

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

        final SiteTemplate siteTemplate;

        try
        {
            siteTemplate = this.siteTemplateService.getSiteTemplate( siteTemplateKey );
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

    @Inject
    public void setSiteTemplateService( final SiteTemplateService siteTemplateService )
    {
        this.siteTemplateService = siteTemplateService;
    }

    @Inject
    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.siteTemplateIconUrlResolver =
            new SiteTemplateIconUrlResolver( new ContentTypeIconUrlResolver( new ContentTypeIconResolver( contentTypeService ) ) );
    }
}
