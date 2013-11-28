package com.enonic.wem.admin.rest.resource.module;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.io.ByteStreams;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.module.CreateModule;
import com.enonic.wem.api.command.module.GetModule;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleFileEntry;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.ModuleNotFoundException;
import com.enonic.wem.api.module.ModuleVersion;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.core.exporters.ModuleExporter;

import static com.enonic.wem.api.module.ModuleFileEntry.directoryBuilder;
import static com.enonic.wem.api.module.ModuleFileEntry.newFileEntry;
import static org.junit.Assert.*;

public class ModuleResourceTest
    extends AbstractResourceTest
{
    private Client client;

    private Path tempDir;

    @Before
    public void setup()
        throws IOException
    {
        mockCurrentContextHttpRequest();
        tempDir = Files.createTempDirectory( "wemtest" );
    }

    @After
    public void after()
    {
        try
        {
            FileUtils.deleteDirectory( tempDir.toFile() );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    @Test
    public void install_module_exception()
        throws Exception
    {
        final WebResource webResource = resource().path( "module/install" );
        final FormDataMultiPart mp = new FormDataMultiPart();
        final FormDataContentDisposition file = FormDataContentDisposition.name( "file" ).fileName( "testmodule-1.0.0.zip" ).build();
        mp.bodyPart( new FormDataBodyPart( file, "INVALID_ZIP_CONTENT" ) );

        final String jsonString = webResource.type( MediaType.MULTIPART_FORM_DATA_TYPE ).post( String.class, mp );

        assertJson( "install_module_exception.json", jsonString );
    }

    @Test
    public void install_module_success()
        throws Exception
    {
        final Module module = createModule();
        Mockito.when( client.execute( Mockito.isA( CreateModule.class ) ) ).thenReturn( module );

        final ModuleExporter moduleExporter = new ModuleExporter();
        final Path exportedModuleFile = moduleExporter.exportToZip( module, tempDir );

        final WebResource webResource = resource().path( "module/install" );
        final FormDataMultiPart mp = new FormDataMultiPart();
        final FormDataContentDisposition file = FormDataContentDisposition.name( "file" ).fileName( "mymodule-1.0.0.zip" ).build();
        final byte[] fileData = Files.readAllBytes( exportedModuleFile );
        final FormDataBodyPart p = new FormDataBodyPart( file, fileData, MediaType.APPLICATION_OCTET_STREAM_TYPE );
        mp.bodyPart( p );

        final String jsonString = webResource.type( MediaType.MULTIPART_FORM_DATA_TYPE ).post( String.class, mp );

        assertJson( "install_module_success.json", jsonString );
    }

    @Test
    public void export_module_success()
        throws Exception
    {
        final Module module = createModule();
        Mockito.when( client.execute( Mockito.isA( GetModule.class ) ) ).thenReturn( module );

        final WebResource webResource = resource().
            path( "module/export" ).
            queryParam( "moduleKey", "testmodule-1.0.0" );
        final byte[] response = webResource.get( byte[].class );

        final ModuleExporter moduleExporter = new ModuleExporter();
        final Path zipFilePath = Files.write( tempDir.resolve( "testmodule-1.0.0.zip" ), response );
        final Module exportedModule = moduleExporter.importFromZip( zipFilePath );

        assertEquals( "module display name", exportedModule.getDisplayName() );
        assertEquals( "testmodule-1.0.0", exportedModule.getModuleKey().toString() );
        assertEquals( 3, exportedModule.getModuleDirectoryEntry().size() );
    }

    @Test
    public void export_module_not_found()
        throws Exception
    {
        final Module module = createModule();
        Mockito.when( client.execute( Mockito.isA( GetModule.class ) ) ).thenThrow( new ModuleNotFoundException( module.getModuleKey() ) );

        final WebResource webResource = resource().
            path( "module/export" ).
            queryParam( "moduleKey", "testmodule-1.0.0" );

        try
        {
            webResource.get( byte[].class );
            fail( "Expected exception" );
        }
        catch ( UniformInterfaceException e )
        {
            assertEquals( 404, e.getResponse().getStatus() );
        }
    }

    @Test
    public void export_module_invalid_module_key()
        throws Exception
    {
        final Module module = createModule();
        Mockito.when( client.execute( Mockito.isA( GetModule.class ) ) ).thenThrow( new ModuleNotFoundException( module.getModuleKey() ) );

        final WebResource webResource = resource().
            path( "module/export" ).
            queryParam( "moduleKey", "testmodule-1-2-3" );

        try
        {
            webResource.get( byte[].class );
            fail( "Expected exception" );
        }
        catch ( UniformInterfaceException e )
        {
            assertEquals( 400, e.getResponse().getStatus() );
        }
    }

    private Module createModule()
    {
        final ModuleFileEntry publicDir = directoryBuilder( "public" ).
            addEntry( newFileEntry( "file1.txt", ByteStreams.asByteSource( "some data".getBytes() ) ) ).
            build();
        final ModuleFileEntry templatesDir = directoryBuilder( "templates" ).
            addEntry( newFileEntry( "template1.txt", ByteStreams.asByteSource( "some more data".getBytes() ) ) ).
            build();

        final Form config = Form.newForm().
            addFormItem( Input.newInput().name( "some-name" ).inputType( InputTypes.TEXT_LINE ).build() ).
            build();

        final ContentTypeNames requiredCtypes = ContentTypeNames.from( "ctype1", "ctype2", "ctype3" );
        final ModuleKeys requiredModules = ModuleKeys.from( ModuleKey.from( "modA-1.0.0" ), ModuleKey.from( "modB-1.0.0" ) );

        return Module.newModule().
            moduleKey( ModuleKey.from( "testmodule-1.0.0" ) ).
            displayName( "module display name" ).
            info( "module-info" ).
            url( "http://enonic.net" ).
            vendorName( "Enonic" ).
            vendorUrl( "https://www.enonic.com" ).
            minSystemVersion( ModuleVersion.from( 5, 0, 0 ) ).
            maxSystemVersion( ModuleVersion.from( 6, 0, 0 ) ).
            addModuleDependency( ModuleKey.from( "modulefoo-1.0.0" ) ).
            addContentTypeDependency( ContentTypeName.from( "article" ) ).
            addModuleDependencies( requiredModules ).
            addContentTypeDependencies( requiredCtypes ).
            config( config ).
            addFileEntry( publicDir ).
            addFileEntry( templatesDir ).
            addFileEntry( directoryBuilder( "emptydir" ).build() ).
            build();
    }

    @Override
    protected Object getResourceInstance()
    {
        client = Mockito.mock( Client.class );
        final ModuleResource resource = new ModuleResource();
        resource.setClient( client );

        return resource;
    }

}
