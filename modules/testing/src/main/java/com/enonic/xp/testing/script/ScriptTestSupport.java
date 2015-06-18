package com.enonic.xp.testing.script;

import org.mockito.Mockito;

import com.enonic.xp.module.Module;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleService;
import com.enonic.xp.portal.impl.script.ScriptServiceImpl;
import com.enonic.xp.portal.script.ScriptExports;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.testing.bean.SimpleBeanManager;
import com.enonic.xp.testing.resource.ResourceUrlRegistry;
import com.enonic.xp.testing.resource.ResourceUrlTestHelper;

public abstract class ScriptTestSupport
{
    private final static ModuleKey DEFAULT_MODULE_KEY = ModuleKey.from( "mymodule" );

    protected final ScriptServiceImpl scriptService;

    private final SimpleBeanManager beanManager;

    public ScriptTestSupport()
    {
        this.beanManager = new SimpleBeanManager();
        this.scriptService = new ScriptServiceImpl();
        this.scriptService.setBeanManager( this.beanManager );

        final ResourceUrlRegistry urlRegistry = ResourceUrlTestHelper.mockModuleScheme();
        urlRegistry.modulesClassLoader( getClass().getClassLoader() );

        final Module module = Mockito.mock( Module.class );
        Mockito.when( module.getClassLoader() ).thenReturn( getClass().getClassLoader() );

        final ModuleService moduleService = Mockito.mock( ModuleService.class );
        Mockito.when( moduleService.getModule( getModuleKey() ) ).thenReturn( module );

        this.scriptService.setModuleService( moduleService );
    }

    protected final void addBean( final String name, final Object bean )
    {
        this.beanManager.register( getModuleKey(), name, bean );
    }

    protected final ScriptExports runTestScript( final String name )
    {
        return runTestScript( ResourceKey.from( getModuleKey(), name ) );
    }

    protected final ScriptExports runTestScript( final ResourceKey key )
    {
        return this.scriptService.execute( key );
    }

    protected ModuleKey getModuleKey()
    {
        return DEFAULT_MODULE_KEY;
    }
}
