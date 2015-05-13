package com.enonic.xp.portal.impl.script;

import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceUrlRegistry;
import com.enonic.xp.resource.ResourceUrlTestHelper;
import com.enonic.xp.portal.script.command.CommandHandler;
import com.enonic.xp.portal.script.ScriptExports;

public abstract class AbstractScriptTest
{
    private final static ModuleKey MYMODULE_KEY = ModuleKey.from( "mymodule" );

    protected final ScriptServiceImpl scriptService;

    public AbstractScriptTest()
    {
        this.scriptService = new ScriptServiceImpl();
        this.scriptService.addGlobalVariable( "assert", new AssertHelper() );

        final ResourceUrlRegistry urlRegistry = ResourceUrlTestHelper.mockModuleScheme();
        urlRegistry.modulesClassLoader( getClass().getClassLoader() );
    }

    protected final void addHandler( final CommandHandler handler )
    {
        this.scriptService.addHandler( handler );
    }

    protected final void removeHandler( final CommandHandler handler )
    {
        this.scriptService.removeHandler( handler );
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
