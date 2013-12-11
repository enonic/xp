package com.enonic.wem.core.module;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.module.GetModule;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleNotFoundException;
import com.enonic.wem.api.module.ModuleVersion;
import com.enonic.wem.core.config.SystemConfig;
import com.enonic.wem.core.exporters.ModuleExporter;

import static junit.framework.Assert.assertEquals;


public class GetModuleHandlerTest
{

    private GetModuleHandler handler;

    private SystemConfig systemConfig;

    private ModuleExporter moduleExporter;

    private Path tempDir;

    @Before
    public void setUp()
        throws Exception
    {
        handler = new GetModuleHandler();
        systemConfig = Mockito.mock( SystemConfig.class );
        moduleExporter = Mockito.mock( ModuleExporter.class );
        handler.setModuleResourcePathResolver( new ModuleResourcePathResolverImpl( systemConfig ) );
        handler.setModuleImporter( moduleExporter );
        tempDir = Files.createTempDirectory( "wemce" );

    }

    @Test
    public void testGetExistingModule()
        throws Exception
    {
        Path modulesDir = tempDir.resolve( "modules" );
        Files.createDirectory( modulesDir );

        Path fooModuleDir = modulesDir.resolve( "foomodule-1.0.0" );
        Files.createDirectory( fooModuleDir );

        Module.Builder fooModule = createModule();

        Mockito.when( systemConfig.getModulesDir() ).thenReturn( modulesDir );
        Mockito.when( moduleExporter.importFromDirectory( fooModuleDir ) ).thenReturn( fooModule );

        GetModule getModuleCommand = Commands.module().get().module( fooModule.build().getModuleKey() );
        handler.setCommand( getModuleCommand );
        handler.handle();

        assertEquals( fooModule.build().toString(), getModuleCommand.getResult().toString() );

    }

    @Test(expected = ModuleNotFoundException.class)
    public void testGetNonExistingModule()
        throws Exception
    {
        Path modulesDir = tempDir.resolve( "modules" );
        Files.createDirectory( modulesDir );

        Path fooModuleDir = modulesDir.resolve( "module-1.2.3" );
        Files.createDirectory( fooModuleDir );

        Module fooModule = createModule().build();

        Mockito.when( systemConfig.getModulesDir() ).thenReturn( modulesDir );

        GetModule getModuleCommand = Commands.module().get().module( fooModule.getModuleKey() );
        handler.setCommand( getModuleCommand );
        handler.handle();
    }

    private Module.Builder createModule()
    {
        return Module.newModule().
            moduleKey( ModuleKey.from( "foomodule-1.0.0" ) ).
            displayName( "module display name" ).
            info( "module-info" ).
            url( "http://enonic.net" ).
            vendorName( "Enonic" ).
            vendorUrl( "https://www.enonic.com" ).
            minSystemVersion( ModuleVersion.from( 5, 0, 0 ) ).
            maxSystemVersion( ModuleVersion.from( 6, 0, 0 ) );
    }
}
