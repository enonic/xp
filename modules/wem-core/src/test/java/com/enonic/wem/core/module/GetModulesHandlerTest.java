package com.enonic.wem.core.module;

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

    private ModuleExporter moduleExporter;

    private Path tempDir;

    @Before
    public void setUp()
        throws Exception
    {
        handler = new GetModulesHandler();
        systemConfig = Mockito.mock( SystemConfig.class );
        moduleExporter = Mockito.mock( ModuleExporter.class );
        handler.setSystemConfig( systemConfig );
        handler.setModuleResourcePathResolver( new ModuleResourcePathResolverImpl( systemConfig ) );
        handler.setModuleExporter( moduleExporter );
        tempDir = Files.createTempDirectory( "wemce" );
    }

    @Test
    public void testGetModulesWithEmptyModuleKeys()
        throws Exception
    {
        Path modulesDir = tempDir.resolve( "modules" );
        Files.createDirectory( modulesDir );
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
        Path modulesDir = tempDir.resolve( "modules" );
        Files.createDirectory( modulesDir );
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
        Path modulesDir = tempDir.resolve( "modules" );
        Files.createDirectory( modulesDir );
        Path aModuleDir = modulesDir.resolve( "amodule-1.0.0" );
        Path bModuleDir = modulesDir.resolve( "bmodule-1.0.0" );
        Files.createDirectory( aModuleDir );
        Files.createDirectory( bModuleDir );
        Mockito.when( systemConfig.getModulesDir() ).thenReturn( modulesDir );
        Module.Builder aModule = createModule( "amodule-1.0.0" );
        Module.Builder bModule = createModule( "bmodule-1.0.0" );
        Mockito.when( moduleExporter.importFromDirectory( aModuleDir ) ).thenReturn( aModule );
        Mockito.when( moduleExporter.importFromDirectory( bModuleDir ) ).thenReturn( bModule );

        GetModules command = Commands.module().list().modules( ModuleKeys.from( "amodule-1.0.0", "bmodule-1.0.0" ) );
        handler.setCommand( command );
        handler.handle();

        assertEquals( Modules.from( aModule.build(), bModule.build() ).toString(), command.getResult().toString() );
    }

    @Test
    public void testGetExistingAndNonExistingModules()
        throws Exception
    {
        Path modulesDir = tempDir.resolve( "modules" );
        Files.createDirectory( modulesDir );
        Path aModuleDir = modulesDir.resolve( "amodule-1.0.0" );
        Path bModuleDir = modulesDir.resolve( "bmodule-1.0.0" );
        Files.createDirectory( aModuleDir );
        Files.createDirectory( bModuleDir );
        Mockito.when( systemConfig.getModulesDir() ).thenReturn( modulesDir );
        Module.Builder aModule = createModule( "amodule-1.0.0" );
        Module.Builder bModule = createModule( "bmodule-1.0.0" );
        Mockito.when( moduleExporter.importFromDirectory( aModuleDir ) ).thenReturn( aModule );
        Mockito.when( moduleExporter.importFromDirectory( bModuleDir ) ).thenReturn( bModule );

        GetModules command =
            Commands.module().list().modules( ModuleKeys.from( "amodule-1.0.0", "bmodule-1.0.0", "cmodule-1.0.0", "dmodule-1.0.0" ) );
        handler.setCommand( command );
        handler.handle();

        assertEquals( Modules.from( aModule.build(), bModule.build() ).toString(), command.getResult().toString() );
    }

    private Module.Builder createModule( String moduleKey )
    {
        return Module.newModule().
            moduleKey( ModuleKey.from( moduleKey ) ).
            displayName( "module display name" ).
            info( "module-info" ).
            url( "http://enonic.net" ).
            vendorName( "Enonic" ).
            vendorUrl( "https://www.enonic.com" ).
            minSystemVersion( ModuleVersion.from( 5, 0, 0 ) ).
            maxSystemVersion( ModuleVersion.from( 6, 0, 0 ) );
    }
}
