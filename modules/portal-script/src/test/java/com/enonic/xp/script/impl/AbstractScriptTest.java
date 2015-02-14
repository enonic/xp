package com.enonic.xp.script.impl;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceUrlRegistry;
import com.enonic.wem.api.resource.ResourceUrlTestHelper;
import com.enonic.xp.portal.script.command.CommandHandler;
import com.enonic.xp.portal.script.ScriptExports;

public abstract class AbstractScriptTest
{
    private final static ModuleKey MYMODULE_KEY = ModuleKey.from( "mymodule" );

    private final ScriptServiceImpl scriptService;

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
