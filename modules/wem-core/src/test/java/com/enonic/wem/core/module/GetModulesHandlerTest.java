package com.enonic.wem.core.module;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.module.GetModules;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.ModuleVersion;
import com.enonic.wem.api.module.Modules;
import com.enonic.wem.core.config.SystemConfig;

import static junit.framework.Assert.assertEquals;

public class GetModulesHandlerTest
{

    private GetModulesHandler handler;

    private SystemConfig systemConfig;

    private ModuleImporter moduleImporter;

    private Path tempDir;

    @Before
    public void setUp()
        throws Exception
    {
        handler = new GetModulesHandler();
        systemConfig = Mockito.mock( SystemConfig.class );
        moduleImporter = Mockito.mock( ModuleImporter.class );
        handler.setSystemConfig( systemConfig );
        handler.setModuleImporter( moduleImporter );
        tempDir = Files.createTempDirectory( "wemce" );
    }

    @Test
    public void testGetModulesWithEmptyModuleKeys()
        throws Exception
    {
        File modulesDir = new File( tempDir.toFile(), "modules" );
        modulesDir.mkdir();
        Mockito.when( systemConfig.getModulesDir() ).thenReturn( modulesDir );

        GetModules command = Commands.module().list().modules( ModuleKeys.from( new ArrayList<ModuleKey>() ) );
        handler.setCommand( command );
        handler.handle();

        assertEquals( Modules.empty(), command.getResult() );
    }

    @Test
    public void testGetNonExistingModules()
        throws Exception
    {
        File modulesDir = new File( tempDir.toFile(), "modules" );
        modulesDir.mkdir();
        Mockito.when( systemConfig.getModulesDir() ).thenReturn( modulesDir );

        GetModules command = Commands.module().list().modules( ModuleKeys.from( "amodule-1.0.0", "bmodule-1.0.0", "cmodule-1.0.0" ) );
        handler.setCommand( command );
        handler.handle();

        assertEquals( Modules.empty(), command.getResult() );
    }

    @Test
    public void testGetExistingModules()
        throws Exception
    {
        File modulesDir = new File( tempDir.toFile(), "modules" );
        modulesDir.mkdir();
        File aModuleDir = new File( modulesDir, "amodule-1.0.0" );
        File bModuleDir = new File( modulesDir, "bmodule-1.0.0" );
        aModuleDir.mkdir();
        bModuleDir.mkdir();
        Mockito.when( systemConfig.getModulesDir() ).thenReturn( modulesDir );
        Module aModule = createModule( "amodule-1.0.0" );
        Module bModule = createModule( "bmodule-1.0.0" );
        Mockito.when( moduleImporter.importModuleFromDirectory( aModuleDir.toPath() ) ).thenReturn( aModule );
        Mockito.when( moduleImporter.importModuleFromDirectory( bModuleDir.toPath() ) ).thenReturn( bModule );

        GetModules command = Commands.module().list().modules( ModuleKeys.from( "amodule-1.0.0", "bmodule-1.0.0" ) );
        handler.setCommand( command );
        handler.handle();

        assertEquals( Modules.from( aModule, bModule ), command.getResult() );
    }

    @Test
    public void testGetExistingAndNonExistingModules()
        throws Exception
    {
        File modulesDir = new File( tempDir.toFile(), "modules" );
        modulesDir.mkdir();
        File aModuleDir = new File( modulesDir, "amodule-1.0.0" );
        File bModuleDir = new File( modulesDir, "bmodule-1.0.0" );
        aModuleDir.mkdir();
        bModuleDir.mkdir();
        Mockito.when( systemConfig.getModulesDir() ).thenReturn( modulesDir );
        Module aModule = createModule( "amodule-1.0.0" );
        Module bModule = createModule( "bmodule-1.0.0" );
        Mockito.when( moduleImporter.importModuleFromDirectory( aModuleDir.toPath() ) ).thenReturn( aModule );
        Mockito.when( moduleImporter.importModuleFromDirectory( bModuleDir.toPath() ) ).thenReturn( bModule );

        GetModules command =
            Commands.module().list().modules( ModuleKeys.from( "amodule-1.0.0", "bmodule-1.0.0", "cmodule-1.0.0", "dmodule-1.0.0" ) );
        handler.setCommand( command );
        handler.handle();

        assertEquals( Modules.from( aModule, bModule ), command.getResult() );
    }

    private Module createModule( String moduleKey )
    {

        final Module module = Module.newModule().
            moduleKey( ModuleKey.from( moduleKey ) ).
            displayName( "module display name" ).
            info( "module-info" ).
            url( "http://enonic.net" ).
            vendorName( "Enonic" ).
            vendorUrl( "https://www.enonic.com" ).
            minSystemVersion( ModuleVersion.from( 5, 0, 0 ) ).
            maxSystemVersion( ModuleVersion.from( 6, 0, 0 ) ).
            build();
        return module;
    }
}
