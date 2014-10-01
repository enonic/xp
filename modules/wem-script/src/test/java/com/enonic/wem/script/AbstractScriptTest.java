package com.enonic.wem.script;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceUrlRegistry;
import com.enonic.wem.api.resource.ResourceUrlTestHelper;
import com.enonic.wem.script.command.CommandHandler;
import com.enonic.wem.script.internal.ScriptEnvironment;
import com.enonic.wem.script.internal.ScriptServiceImpl;

public abstract class AbstractScriptTest
{
    private final static ModuleKey MYMODULE_KEY = ModuleKey.from( "mymodule" );

    private final ScriptEnvironment environment;

    private final ScriptService scriptService;

    public AbstractScriptTest()
    {
        this.environment = new ScriptEnvironment();
        this.scriptService = new ScriptServiceImpl( this.environment );

        final ResourceUrlRegistry urlRegistry = ResourceUrlTestHelper.mockModuleScheme();
        urlRegistry.modulesClassLoader( getClass().getClassLoader() );
    }

    protected final void addLibrary( final ScriptLibrary library )
    {
        this.environment.addLibrary( library );
    }

    protected final void addHandler( final CommandHandler handler )
    {
        this.environment.addHandler( handler );
    }

    protected final void runTestScript( final String name )
    {
        this.scriptService.execute( ResourceKey.from( MYMODULE_KEY, name ) );
    }
}
