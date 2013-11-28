package com.enonic.wem.core.module;

import java.io.File;
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
        handler.setSystemConfig( systemConfig );
        handler.setModuleImporter( moduleExporter );
        tempDir = Files.createTempDirectory( "wemce" );

    }

    @Test
    public void testGetExistingModule()
        throws Exception
    {
        File modulesDir = new File( tempDir.toFile(), "modules" );
        modulesDir.mkdir();

        File fooModuleDir = new File( modulesDir, "foomodule-1.0.0" );
        fooModuleDir.mkdir();

        Module fooModule = createModule();

        Mockito.when( systemConfig.getModulesDir() ).thenReturn( modulesDir );
        Mockito.when( moduleExporter.importFromDirectory( fooModuleDir.toPath() ) ).thenReturn( fooModule );

        GetModule getModuleCommand = Commands.module().get().module( fooModule.getModuleKey() );
        handler.setCommand( getModuleCommand );
        handler.handle();

        assertEquals( fooModule, getModuleCommand.getResult() );

    }

    @Test(expected = ModuleNotFoundException.class)
    public void testGetNonExistingModule()
        throws Exception
    {
        File modulesDir = new File( tempDir.toFile(), "modules" );
        modulesDir.mkdir();

        File fooModuleDir = new File( modulesDir, "module-1.2.3" );
        fooModuleDir.mkdir();

        Module fooModule = createModule();

        Mockito.when( systemConfig.getModulesDir() ).thenReturn( modulesDir );

        GetModule getModuleCommand = Commands.module().get().module( fooModule.getModuleKey() );
        handler.setCommand( getModuleCommand );
        handler.handle();
    }

    private Module createModule()
    {

        final Module module = Module.newModule().
            moduleKey( ModuleKey.from( "foomodule-1.0.0" ) ).
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
