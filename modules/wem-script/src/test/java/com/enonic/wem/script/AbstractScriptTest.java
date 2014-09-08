package com.enonic.wem.script;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceUrlRegistry;
import com.enonic.wem.api.resource.ResourceUrlTestHelper;
import com.enonic.wem.script.internal.v2.ScriptServiceImpl;

public abstract class AbstractScriptTest
{
    private final static ModuleKey MYMODULE_KEY = ModuleKey.from( "mymodule-1.0.0" );

    private final SimpleScriptEnvironment environment;

    private final ScriptService scriptService;

    public AbstractScriptTest()
    {
        this.environment = new SimpleScriptEnvironment();
        this.environment.addLibrary( new AssertScriptLibrary() );

        this.scriptService = new ScriptServiceImpl( this.environment );

        final ResourceUrlRegistry urlRegistry = ResourceUrlTestHelper.mockModuleScheme();
        urlRegistry.modulesClassLoader( getClass().getClassLoader() );
    }

    protected final void addLibrary( final ScriptLibrary library )
    {
        this.environment.addLibrary( library );
    }

    protected final void runTestScript( final String name )
    {
        this.scriptService.execute( ResourceKey.from( MYMODULE_KEY, name ) );
    }
}
