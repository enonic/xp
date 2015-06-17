package com.enonic.xp.core.impl.app;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.Applications;
import com.enonic.xp.module.Module;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleService;
import com.enonic.xp.module.Modules;

import static org.junit.Assert.*;

public class ApplicationServiceImplTest
{

    private ApplicationServiceImpl applicationService;

    @Before
    public void setup()
    {
        final ModuleKey appModuleKey = ModuleKey.from( "myapp" );
        final ModuleKey app2ModuleKey = ModuleKey.from( "otherapp" );
        final ModuleKey module1Key = ModuleKey.from( "somemodule" );
        final ModuleKey module2Key = ModuleKey.from( "othermodule" );

        final Module appModule = Mockito.mock( Module.class );
        Mockito.when( appModule.getKey() ).thenReturn( appModuleKey );
        Mockito.when( appModule.isApplication() ).thenReturn( true );

        final Module app2Module = Mockito.mock( Module.class );
        Mockito.when( app2Module.getKey() ).thenReturn( app2ModuleKey );
        Mockito.when( app2Module.isApplication() ).thenReturn( true );

        final Module module1 = Mockito.mock( Module.class );
        Mockito.when( module1.getKey() ).thenReturn( module1Key );
        Mockito.when( module1.isApplication() ).thenReturn( false );

        final Module module2 = Mockito.mock( Module.class );
        Mockito.when( module2.getKey() ).thenReturn( module2Key );
        Mockito.when( module2.isApplication() ).thenReturn( false );

        final ModuleService moduleService = Mockito.mock( ModuleService.class );
        Mockito.when( moduleService.getModule( appModuleKey ) ).thenReturn( appModule );
        Mockito.when( moduleService.getModule( app2ModuleKey ) ).thenReturn( app2Module );
        final Modules allModules = Modules.from( appModule, app2Module, module1, module2 );
        Mockito.when( moduleService.getAllModules() ).thenReturn( allModules );

        applicationService = new ApplicationServiceImpl();
        applicationService.setModuleService( moduleService );
    }

    @Test
    public void getByKey_existing()
    {
        final ApplicationKey appKey = ApplicationKey.from( "myapp" );
        final Application app = applicationService.getByKey( appKey );
        assertNotNull( app );
        assertNotNull( app.getModule() );
        assertEquals( appKey, app.getKey() );
    }

    @Test
    public void getByKey_not_found()
    {
        final ApplicationKey appKey = ApplicationKey.from( "mynewapp" );
        final Application app = applicationService.getByKey( appKey );
        assertNull( app );
    }

    @Test
    public void getAll()
    {
        final Applications apps = applicationService.getAll();
        assertNotNull( apps );
        assertEquals( 2, apps.getSize() );
        assertNotNull( apps.getApplication( ApplicationKey.from( "myapp" ) ) );
        assertNotNull( apps.getApplication( ApplicationKey.from( "otherapp" ) ) );
    }

}