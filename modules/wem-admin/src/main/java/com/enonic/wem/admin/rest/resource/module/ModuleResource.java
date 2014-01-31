package com.enonic.wem.admin.rest.resource.module;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.inject.Inject;
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

import com.enonic.wem.admin.json.module.ModuleJson;
import com.enonic.wem.admin.json.module.ModuleSummaryJson;
import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.admin.rest.resource.module.json.ListModuleJson;
import com.enonic.wem.admin.rest.resource.module.json.ModuleDeleteParams;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.module.CreateModule;
import com.enonic.wem.api.command.module.GetModule;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleNotFoundException;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.api.module.Modules;
import com.enonic.wem.core.module.ModuleExporter;

import static com.enonic.wem.api.command.Commands.module;

@javax.ws.rs.Path("module")
@Produces(MediaType.APPLICATION_JSON)
public class ModuleResource
    extends AbstractResource
{
    private static final String ZIP_MIME_TYPE = "application/zip";

    @Inject
    protected ModuleService moduleService;

    @GET
    @javax.ws.rs.Path("list")
    public ListModuleJson list()
    {
        final Modules modules = client.execute( Commands.module().list() );
        return new ListModuleJson( modules );
    }

    @POST
    @javax.ws.rs.Path("delete")
    public ModuleJson delete( ModuleDeleteParams params )
    {
        final Module deleted = this.moduleService.delete( params.getModuleKey() );
        return new ModuleJson( deleted );
    }

    @POST
    @javax.ws.rs.Path("install")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public ModuleSummaryJson install( @FormDataParam("file") InputStream uploadedInputStream,
                                      @FormDataParam("file") FormDataContentDisposition fileDetail )
        throws IOException
    {
        Path tempDirectory = null;
        try
        {
            final String fileName = fileDetail.getFileName();

            tempDirectory = Files.createTempDirectory( "modules" );

            final Path tempZipFile = tempDirectory.resolve( fileName );
            Files.copy( uploadedInputStream, tempZipFile );
            final ModuleExporter moduleExporter = new ModuleExporter();
            final Module importedModule;

            importedModule = moduleExporter.importFromZip( tempZipFile ).build();

            final CreateModule createModuleCommand = CreateModule.fromModule( importedModule );
            final Module createdModule = client.execute( createModuleCommand );

            return new ModuleSummaryJson( createdModule );
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
    public javax.ws.rs.core.Response export( @QueryParam("moduleKey") String moduleKeyParam )
        throws IOException
    {
        final ModuleKey moduleKey;
        try
        {
            moduleKey = ModuleKey.from( moduleKeyParam );
        }
        catch ( Exception e )
        {
            return Response.status( Response.Status.BAD_REQUEST ).build();
        }

        final GetModule getModuleCommand = module().get().module( moduleKey );
        final Module module;
        try
        {
            module = client.execute( getModuleCommand );
        }
        catch ( ModuleNotFoundException e )
        {
            return Response.status( Response.Status.NOT_FOUND ).build();
        }

        final Path tempDirectory = Files.createTempDirectory( "modules" );
        try
        {
            final ModuleExporter moduleExporter = new ModuleExporter();
            final Path moduleFilePath = moduleExporter.exportToZip( module, tempDirectory );
            final byte[] zipContents = Files.readAllBytes( moduleFilePath );

            final String fileName = moduleFilePath.getFileName().toString();
            return Response.ok( zipContents, ZIP_MIME_TYPE ).header( "Content-Disposition", "attachment; filename=" + fileName ).build();
        }
        finally
        {
            FileUtils.deleteDirectory( tempDirectory.toFile() );
        }
    }

    @GET
    public ModuleJson getByKey( @QueryParam("moduleKey") String moduleKey )
    {
        final GetModule getModuleCommand = Commands.module().get().module( ModuleKey.from( moduleKey ) );
        final Module module = client.execute( getModuleCommand );

        return new ModuleJson( module );
    }
}
