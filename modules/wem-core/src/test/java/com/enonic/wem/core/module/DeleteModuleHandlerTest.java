package com.enonic.wem.core.module;

import java.io.File;
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
        handler.setSystemConfig( systemConfig );
        handler.setModuleImporter( moduleExporter );
        tempDir = Files.createTempDirectory( "wemce" );
    }

    @Test
    public void testDeleteExistingModule()
        throws Exception
    {
        final File modulesDir = new File( tempDir.toFile(), "modules" );
        modulesDir.mkdir();
        final File moduleDir = new File( modulesDir, "foomodule-1.0.0" );
        moduleDir.mkdir();
        final File subModuleDir = new File( moduleDir, "config" );
        subModuleDir.mkdir();

        assertTrue( moduleDir.exists() );

        Module.Builder fooModule = createModule();

        Mockito.when( systemConfig.getModulesDir() ).thenReturn( modulesDir );
        Mockito.when( moduleExporter.importFromDirectory( moduleDir.toPath() ) ).thenReturn( fooModule );

        DeleteModule command = Commands.module().delete().module( ModuleKey.from( "foomodule-1.0.0" ) );
        handler.setCommand( command );
        handler.handle();

        assertNotNull( command.getResult() );
        assertFalse( moduleDir.exists() );
        assertFalse( subModuleDir.exists() );
    }

    @Test(expected = ModuleNotFoundException.class)
    public void testDeleteNonExistingModule()
        throws Exception
    {
        final File modulesDir = new File( tempDir.toFile(), "modules" );
        modulesDir.mkdir();
        final File moduleDir = new File( modulesDir, "foomodule-1.2.0" );
        moduleDir.mkdir();

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
