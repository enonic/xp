package com.enonic.wem.core.module;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.module.DeleteModule;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.core.config.SystemConfig;

import static org.junit.Assert.*;

public class DeleteModuleHandlerTest
{

    private DeleteModuleHandler handler;

    private SystemConfig systemConfig;

    private Path tempDir;

    @Before
    public void setUp()
        throws Exception
    {
        handler = new DeleteModuleHandler();
        systemConfig = Mockito.mock( SystemConfig.class );
        handler.setSystemConfig( systemConfig );
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

        Mockito.when( systemConfig.getModuleDir() ).thenReturn( modulesDir );

        DeleteModule command = Commands.module().delete().module( ModuleKey.from( "foomodule-1.0.0" ) );
        handler.setCommand( command );
        handler.handle();

        assertTrue( command.getResult() );
        assertFalse( moduleDir.exists() );
        assertFalse( subModuleDir.exists() );
    }

    @Test
    public void testDeleteNonExistingModule()
        throws Exception
    {
        final File modulesDir = new File( tempDir.toFile(), "modules" );
        modulesDir.mkdir();
        final File moduleDir = new File( modulesDir, "foomodule-1.2.0" );
        moduleDir.mkdir();

        Mockito.when( systemConfig.getModuleDir() ).thenReturn( modulesDir );

        DeleteModule command = Commands.module().delete().module( ModuleKey.from( "foomodule-1.0.0" ) );
        handler.setCommand( command );
        handler.handle();

        assertFalse( command.getResult() );
    }
}
