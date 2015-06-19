package com.enonic.xp.portal.impl.script;

import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.enonic.xp.module.Module;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleService;
import com.enonic.xp.portal.impl.script.invoker.CommandInvokerImpl;
import com.enonic.xp.portal.script.ScriptExports;
import com.enonic.xp.portal.script.command.CommandHandler;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceUrlRegistry;
import com.enonic.xp.resource.ResourceUrlTestHelper;

public abstract class AbstractScriptTest
{
    private final static ModuleKey MYMODULE_KEY = ModuleKey.from( "mymodule" );

    protected final ScriptServiceImpl scriptService;

    protected final CommandInvokerImpl invoker;

    protected final SimpleBeanManager beanManager;

    public AbstractScriptTest()
    {
        this.invoker = new CommandInvokerImpl();
        this.beanManager = new SimpleBeanManager();

        this.scriptService = new ScriptServiceImpl();
        this.scriptService.addGlobalVariable( "assert", new AssertHelper() );
        this.scriptService.setInvoker( this.invoker );
        this.scriptService.setBeanManager( this.beanManager );

        final ResourceUrlRegistry urlRegistry = ResourceUrlTestHelper.mockModuleScheme();
        urlRegistry.modulesClassLoader( getClass().getClassLoader() );

        final BundleContext bundleContext = Mockito.mock( BundleContext.class );

        final Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getBundleContext() ).thenReturn( bundleContext );

        final Module module = Mockito.mock( Module.class );
        Mockito.when( module.getClassLoader() ).thenReturn( getClass().getClassLoader() );
        Mockito.when( module.getBundle() ).thenReturn( bundle );

        final ModuleService moduleService = Mockito.mock( ModuleService.class );
        Mockito.when( moduleService.getModule( MYMODULE_KEY ) ).thenReturn( module );

        this.scriptService.setModuleService( moduleService );
    }

    protected final void addHandler( final CommandHandler handler )
    {
        this.invoker.addHandler( handler );
    }

    protected final void removeHandler( final CommandHandler handler )
    {
        this.invoker.removeHandler( handler );
    }

    protected final ScriptExports runTestScript( final String name )
    {
        return runTestScript( ResourceKey.from( MYMODULE_KEY, name ) );
    }

    protected final ScriptExports runTestScript( final ResourceKey key )
    {
        return this.scriptService.execute( key );
    }
}
