package com.enonic.wem.admin.rest.resource.module;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import com.enonic.wem.admin.json.module.ModuleSummaryJson;
import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.admin.rest.resource.module.json.InstallModuleResultJson;
import com.enonic.wem.api.command.module.CreateModule;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.core.module.ModuleImporter;

@javax.ws.rs.Path("module")
@Produces(MediaType.APPLICATION_JSON)
public class ModuleResource
    extends AbstractResource
{

    @POST
    @javax.ws.rs.Path("install")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public InstallModuleResultJson install( @FormDataParam("file") InputStream uploadedInputStream,
                                            @FormDataParam("file") FormDataContentDisposition fileDetail )
        throws IOException
    {
        final String fileName = fileDetail.getFileName();

        final Path tempDirectory = Files.createTempDirectory( "modules" );
        try
        {
            final Path tempZipFile = tempDirectory.resolve( fileName );
            Files.copy( uploadedInputStream, tempZipFile );
            final ModuleImporter moduleImporter = new ModuleImporter();
            final Module importedModule;
            try
            {
                importedModule = moduleImporter.importModuleFromZip( tempZipFile );
            }
            catch ( Exception e )
            {
                return InstallModuleResultJson.error( e.getMessage() );
            }

            final CreateModule createModuleCommand = CreateModule.fromModule( importedModule );
            final Module createdModule = client.execute( createModuleCommand );

            return InstallModuleResultJson.result( new ModuleSummaryJson( createdModule ) );
        }
        finally
        {
            FileUtils.deleteDirectory( tempDirectory.toFile() );
        }
    }

}
