package com.enonic.xp.script.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;

import com.enonic.xp.module.Module;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleNotFoundException;
import com.enonic.xp.module.ModuleService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.impl.executor.ScriptExecutor;
import com.enonic.xp.script.impl.executor.ScriptExecutorFactory;

public class ScriptServiceImplTest
{
    private ScriptServiceImpl service;

    private ScriptExecutorFactory factory;

    private ModuleService moduleService;

    private BundleContext bundleContext;

    private ResourceKey resourceKey;

    private ScriptExports scriptExports;

    private Module module;

    @Before
    public void setup()
    {
        this.factory = Mockito.mock( ScriptExecutorFactory.class );
        this.moduleService = Mockito.mock( ModuleService.class );
        this.bundleContext = Mockito.mock( BundleContext.class );

        this.service = new ScriptServiceImpl();
        this.service.setScriptExecutorFactory( this.factory );
        this.service.setModuleService( this.moduleService );
        this.service.setBundleContext( this.bundleContext );

        this.resourceKey = ResourceKey.from( "myapp:/lib/foo.js" );
        this.module = Mockito.mock( Module.class );
        final ScriptExecutor executor = Mockito.mock( ScriptExecutor.class );
        this.scriptExports = Mockito.mock( ScriptExports.class );

        Mockito.when( this.moduleService.getModule( this.resourceKey.getModule() ) ).thenReturn( this.module );
        Mockito.when( this.factory.newExecutor( this.module ) ).thenReturn( executor );
        Mockito.when( executor.execute( this.resourceKey ) ).thenReturn( this.scriptExports );

        this.service.start();
    }

    @Test
    public void execute_moduleFound()
    {
        Assert.assertSame( this.scriptExports, this.service.execute( this.resourceKey ) );
    }

    @Test(expected = ModuleNotFoundException.class)
    public void execute_moduleNotFound()
    {
        final ModuleKey key = this.resourceKey.getModule();
        Mockito.when( this.moduleService.getModule( key ) ).thenThrow( new ModuleNotFoundException( key ) );

        this.service.execute( this.resourceKey );
    }

    @Test
    public void execute_checkStart()
    {
        Mockito.verify( this.bundleContext, Mockito.times( 1 ) ).addBundleListener( this.service );
    }

    @Test
    public void execute_cachedExecutor()
    {
        Assert.assertSame( this.scriptExports, this.service.execute( this.resourceKey ) );
        Mockito.verify( this.factory, Mockito.times( 1 ) ).newExecutor( this.module );

        Assert.assertSame( this.scriptExports, this.service.execute( this.resourceKey ) );
        Mockito.verify( this.factory, Mockito.times( 1 ) ).newExecutor( this.module );

        final Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getSymbolicName() ).thenReturn( "myapp" );

        final BundleEvent event = new BundleEvent( BundleEvent.STOPPED, bundle );
        this.service.bundleChanged( event );

        Assert.assertSame( this.scriptExports, this.service.execute( this.resourceKey ) );
        Mockito.verify( this.factory, Mockito.times( 2 ) ).newExecutor( this.module );
    }
}
