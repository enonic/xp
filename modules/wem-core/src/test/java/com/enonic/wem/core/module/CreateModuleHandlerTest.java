package com.enonic.wem.core.module;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.module.CreateModule;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleFileEntry;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.ModuleVersion;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.config.SystemConfig;
import com.enonic.wem.core.exporters.ModuleExporter;

import static com.enonic.wem.api.module.ModuleFileEntry.directoryBuilder;
import static com.google.common.io.ByteStreams.asByteSource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class CreateModuleHandlerTest
    extends AbstractCommandHandlerTest
{
    private CreateModuleHandler handler;

    private SystemConfig systemConfig;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        systemConfig = Mockito.mock( SystemConfig.class );
        when( systemConfig.getModulesDir() ).thenReturn( java.nio.file.Files.createTempDirectory( "module" ).toFile() );
        handler = new CreateModuleHandler();
        handler.setContext( this.context );
        handler.setSystemConfig( systemConfig );
        handler.setModuleExporter( new ModuleExporter() );
    }

    @After
    public void deleteTempDir()
    {
        try
        {
            FileUtils.deleteDirectory( systemConfig.getModulesDir() );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    @Test
    public void createModuleWithoutFiles()
        throws Exception
    {
        // setup
        final Form config = Form.newForm().
            addFormItem( Input.newInput().name( "some-name" ).inputType( InputTypes.TEXT_LINE ).build() ).
            build();

        final CreateModule command = Commands.module().create().
            name( "modulename" ).
            version( ModuleVersion.from( 1, 0, 0 ) ).
            displayName( "module display name" ).
            info( "module-info" ).
            url( "http://enonic.net" ).
            vendorName( "Enonic" ).
            vendorUrl( "https://www.enonic.com" ).
            minSystemVersion( ModuleVersion.from( 5, 0, 0 ) ).
            maxSystemVersion( ModuleVersion.from( 6, 0, 0 ) ).
            moduleDependencies( ModuleKeys.from( "modulefoo-1.0.0", "modulebar-1.2.3" ) ).
            contentTypeDependencies( ContentTypeNames.from( "article" ) ).
            config( config );

        // exercise
        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        final Module moduleCreated = command.getResult();
        assertNotNull( command.getResult() );
        assertEquals( "modulename", moduleCreated.getName().toString() );
        assertEquals( "1.0.0", moduleCreated.getVersion().toString() );
        assertEquals( "module display name", moduleCreated.getDisplayName() );
        assertEquals( "module-info", moduleCreated.getInfo() );
        assertEquals( "http://enonic.net", moduleCreated.getUrl() );
        assertEquals( "Enonic", moduleCreated.getVendorName() );
        assertEquals( "https://www.enonic.com", moduleCreated.getVendorUrl() );
        assertEquals( "5.0.0", moduleCreated.getMinSystemVersion().toString() );
        assertEquals( "6.0.0", moduleCreated.getMaxSystemVersion().toString() );
        assertEquals( "[modulefoo-1.0.0, modulebar-1.2.3]", moduleCreated.getModuleDependencies().toString() );
        assertEquals( ContentTypeNames.from( "article" ), moduleCreated.getContentTypeDependencies() );

        final Path expectedModuleDir = systemConfig.getModulesDir().toPath().resolve( "modulename-1.0.0" );
        assertTrue( "Module directory not found: " + expectedModuleDir, Files.isDirectory( expectedModuleDir ) );
        final Path moduleXmlPath = expectedModuleDir.resolve( ModuleExporter.MODULE_XML );
        assertTrue( "Module xml not found: " + moduleXmlPath, Files.isRegularFile( moduleXmlPath ) );
    }

    @Test
    public void createModuleWithFiles()
        throws Exception
    {
        // setup
        final Form config = Form.newForm().
            addFormItem( Input.newInput().name( "some-name" ).inputType( InputTypes.TEXT_LINE ).build() ).
            build();

        final ModuleFileEntry.Builder directoryBuilder = directoryBuilder( "public" ).
            addFile( "resource1.txt", asByteSource( "data1".getBytes() ) ).
            addFile( "resource2.txt", asByteSource( "data2".getBytes() ) ).
            addFile( "resource3.txt", asByteSource( "data3".getBytes() ) );
        final ModuleFileEntry.Builder subDirectory = directoryBuilder( "javascript" ).
            addFile( "controller.js", asByteSource( "some data".getBytes() ) ).
            addFile( "helper.js", asByteSource( "more data".getBytes() ) );
        final ModuleFileEntry moduleDirectoryEntry = ModuleFileEntry.directoryBuilder( "" ).
            addEntry( directoryBuilder.addEntry( subDirectory ) ).
            build();

        final CreateModule command = Commands.module().create().
            name( "modulename" ).
            version( ModuleVersion.from( 1, 0, 0 ) ).
            displayName( "module display name" ).
            info( "module-info" ).
            url( "http://enonic.net" ).
            vendorName( "Enonic" ).
            vendorUrl( "https://www.enonic.com" ).
            minSystemVersion( ModuleVersion.from( 5, 0, 0 ) ).
            maxSystemVersion( ModuleVersion.from( 6, 0, 0 ) ).
            moduleDependencies( ModuleKeys.from( "modulefoo-1.0.0", "modulebar-1.2.3" ) ).
            contentTypeDependencies( ContentTypeNames.from( "article" ) ).
            config( config ).
            moduleDirectoryEntry( moduleDirectoryEntry );

        // exercise
        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        final Module moduleCreated = command.getResult();
        assertNotNull( command.getResult() );
        assertEquals( "modulename", moduleCreated.getName().toString() );
        assertEquals( "1.0.0", moduleCreated.getVersion().toString() );
        assertEquals( "module display name", moduleCreated.getDisplayName() );
        assertEquals( "module-info", moduleCreated.getInfo() );
        assertEquals( "http://enonic.net", moduleCreated.getUrl() );
        assertEquals( "Enonic", moduleCreated.getVendorName() );
        assertEquals( "https://www.enonic.com", moduleCreated.getVendorUrl() );
        assertEquals( "5.0.0", moduleCreated.getMinSystemVersion().toString() );
        assertEquals( "6.0.0", moduleCreated.getMaxSystemVersion().toString() );
        assertEquals( "[modulefoo-1.0.0, modulebar-1.2.3]", moduleCreated.getModuleDependencies().toString() );
        assertEquals( ContentTypeNames.from( "article" ), moduleCreated.getContentTypeDependencies() );

        final Path expectedModuleDir = systemConfig.getModulesDir().toPath().resolve( "modulename-1.0.0" );
        assertTrue( "Module directory not found: " + expectedModuleDir, Files.isDirectory( expectedModuleDir ) );
        final Path moduleXmlPath = expectedModuleDir.resolve( ModuleExporter.MODULE_XML );
        assertTrue( "Module xml not found: " + moduleXmlPath, Files.isRegularFile( moduleXmlPath ) );

        assertDirectoryExists( expectedModuleDir.resolve( "public" ) );
        assertFileExists( expectedModuleDir.resolve( "public" ).resolve( "resource1.txt" ) );
        assertFileExists( expectedModuleDir.resolve( "public" ).resolve( "resource2.txt" ) );
        assertFileExists( expectedModuleDir.resolve( "public" ).resolve( "resource3.txt" ) );
        assertDirectoryExists( expectedModuleDir.resolve( "public" ).resolve( "javascript" ) );
        assertFileExists( expectedModuleDir.resolve( "public" ).resolve( "javascript" ).resolve( "controller.js" ) );
        assertFileExists( expectedModuleDir.resolve( "public" ).resolve( "javascript" ).resolve( "helper.js" ) );
    }

    private void assertDirectoryExists( final Path path )
    {
        assertTrue( "Expected directory not found: " + path.toString(), Files.isDirectory( path ) );
    }

    private void assertFileExists( final Path path )
    {
        assertTrue( "Expected file not found: " + path.toString(), Files.isRegularFile( path ) );
    }
}
