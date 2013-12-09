package com.enonic.wem.core.module;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.module.DeleteModule;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleNotFoundException;
import com.enonic.wem.api.module.ModuleVersion;
import com.enonic.wem.core.config.SystemConfig;
import com.enonic.wem.core.exporters.ModuleExporter;

import static org.junit.Assert.*;

public class DeleteModuleHandlerTest
{

    private DeleteModuleHandler handler;

    private SystemConfig systemConfig;

    private ModuleExporter moduleExporter;

    private Path tempDir;

    @Before
    public void setUp()
        throws Exception
    {
        handler = new DeleteModuleHandler();
        systemConfig = Mockito.mock( SystemConfig.class );
        moduleExporter = Mockito.mock( ModuleExporter.class );
        handler.setModuleResourcePathResolver( new ModuleResourcePathResolver( systemConfig ) );
        handler.setModuleImporter( moduleExporter );
        tempDir = Files.createTempDirectory( "wemce" );
    }

    @Test
    public void testDeleteExistingModule()
        throws Exception
    {
        final Path modulesDir = tempDir.resolve( "modules" );
        Files.createDirectory( modulesDir );
        final Path moduleDir = modulesDir.resolve( "foomodule-1.0.0" );
        Files.createDirectory( moduleDir );
        final Path subModuleDir = moduleDir.resolve( "config" );
        Files.createDirectory( subModuleDir );

        assertTrue( Files.exists( moduleDir ) );

        Module.Builder fooModule = createModule();

        Mockito.when( systemConfig.getModulesDir() ).thenReturn( modulesDir );
        Mockito.when( moduleExporter.importFromDirectory( moduleDir ) ).thenReturn( fooModule );

        DeleteModule command = Commands.module().delete().module( ModuleKey.from( "foomodule-1.0.0" ) );
        handler.setCommand( command );
        handler.handle();

        assertNotNull( command.getResult() );
        assertFalse( Files.exists( moduleDir ) );
        assertFalse( Files.exists( subModuleDir ) );
    }

    @Test(expected = ModuleNotFoundException.class)
    public void testDeleteNonExistingModule()
        throws Exception
    {
        final Path modulesDir = tempDir.resolve( "modules" );
        Files.createDirectory( modulesDir );
        final Path moduleDir = modulesDir.resolve( "foomodule-1.2.0" );
        Files.createDirectory( moduleDir );

        Mockito.when( systemConfig.getModulesDir() ).thenReturn( modulesDir );

        DeleteModule command = Commands.module().delete().module( ModuleKey.from( "foomodule-1.0.0" ) );
        handler.setCommand( command );
        handler.handle();
    }

    private Module.Builder createModule()
    {

        final Module.Builder module = Module.newModule().
            moduleKey( ModuleKey.from( "foomodule-1.0.0" ) ).
            displayName( "module display name" ).
            info( "module-info" ).
            url( "http://enonic.net" ).
            vendorName( "Enonic" ).
            vendorUrl( "https://www.enonic.com" ).
            minSystemVersion( ModuleVersion.from( 5, 0, 0 ) ).
            maxSystemVersion( ModuleVersion.from( 6, 0, 0 ) );
        return module;
    }
}
