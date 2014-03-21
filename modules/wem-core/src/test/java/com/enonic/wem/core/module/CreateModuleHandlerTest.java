package com.enonic.wem.core.module;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.module.CreateModuleParams;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleFileEntry;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.ModuleVersion;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.config.SystemConfig;

import static com.enonic.wem.api.module.ModuleFileEntry.newModuleDirectory;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class CreateModuleHandlerTest
    extends AbstractCommandHandlerTest
{
    private SystemConfig systemConfig;

    private ModuleServiceImpl moduleService;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        systemConfig = Mockito.mock( SystemConfig.class );
        when( systemConfig.getModulesDir() ).thenReturn( java.nio.file.Files.createTempDirectory( "module" ) );

        this.moduleService = new ModuleServiceImpl();
        this.moduleService.systemConfig = this.systemConfig;
        this.moduleService.moduleExporter = new ModuleExporter();
    }

    @After
    public void deleteTempDir()
    {
        try
        {
            FileUtils.deleteDirectory( systemConfig.getModulesDir().toFile() );
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

        final CreateModuleParams params = new CreateModuleParams().
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
        final Module moduleCreated = this.moduleService.createModule( params );

        // verify
        assertNotNull( moduleCreated );
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

        final Path expectedModuleDir = systemConfig.getModulesDir().resolve( "modulename-1.0.0" );
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

        final ModuleFileEntry.Builder directoryBuilder = newModuleDirectory( "public" ).
            addFile( "resource1.txt", ByteSource.wrap( "data1".getBytes() ) ).
            addFile( "resource2.txt", ByteSource.wrap( "data2".getBytes() ) ).
            addFile( "resource3.txt", ByteSource.wrap( "data3".getBytes() ) );
        final ModuleFileEntry.Builder subDirectory = newModuleDirectory( "javascript" ).
            addFile( "controller.js", ByteSource.wrap( "some data".getBytes() ) ).
            addFile( "helper.js", ByteSource.wrap( "more data".getBytes() ) );
        final ModuleFileEntry moduleDirectoryEntry = ModuleFileEntry.newModuleDirectory( "" ).
            addEntry( directoryBuilder.addEntry( subDirectory ) ).
            build();

        final CreateModuleParams params = new CreateModuleParams().
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
        final Module moduleCreated = this.moduleService.createModule( params );

        // verify
        assertNotNull( moduleCreated );
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

        final Path expectedModuleDir = systemConfig.getModulesDir().resolve( "modulename-1.0.0" );
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
