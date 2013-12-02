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
import com.enonic.wem.api.command.module.ModuleEditor;
import com.enonic.wem.api.command.module.UpdateModule;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleFileEntry;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleNotFoundException;
import com.enonic.wem.api.module.ModuleVersion;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.config.SystemConfig;
import com.enonic.wem.core.exporters.ModuleExporter;

import static com.enonic.wem.api.module.ModuleFileEntry.newModuleDirectory;
import static com.google.common.io.ByteStreams.asByteSource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class UpdateModuleHandlerTest
    extends AbstractCommandHandlerTest
{
    private UpdateModuleHandler handler;

    private SystemConfig systemConfig;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        systemConfig = Mockito.mock( SystemConfig.class );

        when( systemConfig.getModulesDir() ).thenReturn( Files.createTempDirectory( "module" ).toFile() );

        handler = new UpdateModuleHandler();
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
    public void updateNonExistingModule()
        throws Exception
    {
        ModuleKey moduleKey = ModuleKey.from( "foomodule-1.0.0" );
        UpdateModule updateCommand = Commands.module().update().module( moduleKey );

        try
        {
            handler.setCommand( updateCommand );
            handler.handle();
        }
        catch ( ModuleNotFoundException e )
        {
            assertEquals( "Module [foomodule-1.0.0] was not found", e.getMessage() );
        }
    }

    @Test
    public void updateModuleEdited()
        throws Exception
    {
        // setup
        ModuleKey moduleKey = ModuleKey.from( "foomodule-1.0.0" );
        Module module = createModule( moduleKey );
        Path moduleDir = new ModuleExporter().exportToDirectory( module, systemConfig.getModulesDir().toPath() );

        final UpdateModule command = Commands.module().update().
            module( moduleKey ).
            editor( new ModuleEditor()
            {
                @Override
                public Module edit( final Module module )
                {
                    return Module.newModule( module ).displayName( module.getDisplayName() + " (edited)" ).build();
                }
            } );

        // exercise
        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        final Boolean edited = command.getResult();

        assertTrue( edited );
        assertTrue( "Module directory not found: " + moduleDir, Files.isDirectory( moduleDir ) );

        Module updatedModule = new ModuleExporter().importFromDirectory( moduleDir );

        assertEquals( "Display name should have been edited", module.getDisplayName() + " (edited)", updatedModule.getDisplayName() );
        assertEquals( module.getInfo(), updatedModule.getInfo() );
        assertEquals( module.getConfig().toString(), updatedModule.getConfig().toString() );
        assertEquals( module.getName(), updatedModule.getName() );
        assertEquals( module.getUrl(), updatedModule.getUrl() );
        assertEquals( module.getVendorName(), updatedModule.getVendorName() );
        assertEquals( module.getVendorUrl(), updatedModule.getVendorUrl() );
        assertEquals( module.getVersion(), updatedModule.getVersion() );
        assertEquals( module.getMaxSystemVersion(), updatedModule.getMaxSystemVersion() );
        assertEquals( module.getMinSystemVersion(), updatedModule.getMinSystemVersion() );
        assertEquals( module.getModuleDependencies(), updatedModule.getModuleDependencies() );
        assertEquals( module.getContentTypeDependencies(), updatedModule.getContentTypeDependencies() );

        assertDirectoryExists( moduleDir.resolve( "public" ) );
        assertFileExists( moduleDir.resolve( "public" ).resolve( "resource1.txt" ) );
        assertFileExists( moduleDir.resolve( "public" ).resolve( "resource2.txt" ) );
        assertFileExists( moduleDir.resolve( "public" ).resolve( "resource3.txt" ) );
        assertDirectoryExists( moduleDir.resolve( "public" ).resolve( "javascript" ) );
        assertFileExists( moduleDir.resolve( "public" ).resolve( "javascript" ).resolve( "controller.js" ) );
        assertFileExists( moduleDir.resolve( "public" ).resolve( "javascript" ).resolve( "helper.js" ) );
    }

    @Test
    public void updateModuleNotEdited()
        throws Exception
    {
        // setup
        ModuleKey moduleKey = ModuleKey.from( "foomodule-1.0.0" );
        Module module = createModule( moduleKey );
        Path moduleDir = new ModuleExporter().exportToDirectory( module, systemConfig.getModulesDir().toPath() );

        final UpdateModule command = Commands.module().update().
            module( moduleKey ).
            editor( new ModuleEditor()
            {
                @Override
                public Module edit( final Module module )
                {
                    return module;
                }
            } );

        // exercise
        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        final Boolean edited = command.getResult();

        assertFalse( edited );
        assertTrue( "Module directory not found: " + moduleDir, Files.isDirectory( moduleDir ) );

        Module updatedModule = new ModuleExporter().importFromDirectory( moduleDir );

        assertEquals( module.getDisplayName(), updatedModule.getDisplayName() );
        assertEquals( module.getInfo(), updatedModule.getInfo() );
        assertEquals( module.getConfig().toString(), updatedModule.getConfig().toString() );
        assertEquals( module.getName(), updatedModule.getName() );
        assertEquals( module.getUrl(), updatedModule.getUrl() );
        assertEquals( module.getVendorName(), updatedModule.getVendorName() );
        assertEquals( module.getVendorUrl(), updatedModule.getVendorUrl() );
        assertEquals( module.getVersion(), updatedModule.getVersion() );
        assertEquals( module.getMaxSystemVersion(), updatedModule.getMaxSystemVersion() );
        assertEquals( module.getMinSystemVersion(), updatedModule.getMinSystemVersion() );
        assertEquals( module.getModuleDependencies(), updatedModule.getModuleDependencies() );
        assertEquals( module.getContentTypeDependencies(), updatedModule.getContentTypeDependencies() );
    }

    private void assertDirectoryExists( final Path path )
    {
        assertTrue( "Expected directory not found: " + path.toString(), Files.isDirectory( path ) );
    }

    private void assertFileExists( final Path path )
    {
        assertTrue( "Expected file not found: " + path.toString(), Files.isRegularFile( path ) );
    }

    private Module createModule( final ModuleKey moduleKey )
    {
        final Form config = Form.newForm().
            addFormItem( Input.newInput().name( "some-name" ).inputType( InputTypes.TEXT_LINE ).build() ).
            build();

        final ModuleFileEntry.Builder publicDir = newModuleDirectory( "public" ).
            addFile( "resource1.txt", asByteSource( "data1".getBytes() ) ).
            addFile( "resource2.txt", asByteSource( "data2".getBytes() ) ).
            addFile( "resource3.txt", asByteSource( "data3".getBytes() ) );

        final ModuleFileEntry.Builder jsDir = newModuleDirectory( "javascript" ).
            addFile( "controller.js", asByteSource( "some data".getBytes() ) ).
            addFile( "helper.js", asByteSource( "more data".getBytes() ) );

        publicDir.addEntry( jsDir );

        return Module.newModule().
            moduleKey( moduleKey ).
            config( config ).
            displayName( "module display name" ).
            info( "module-info" ).
            url( "http://enonic.net" ).
            vendorName( "Enonic" ).
            vendorUrl( "https://www.enonic.com" ).
            minSystemVersion( ModuleVersion.from( 5, 0, 0 ) ).
            maxSystemVersion( ModuleVersion.from( 6, 0, 0 ) ).
            addFileEntry( publicDir.build() ).build();
    }

}
