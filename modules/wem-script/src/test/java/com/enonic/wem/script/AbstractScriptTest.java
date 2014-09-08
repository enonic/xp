package com.enonic.wem.script;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceUrlRegistry;
import com.enonic.wem.api.resource.ResourceUrlTestHelper;
import com.enonic.wem.script.internal.RhinoScriptRunnerFactory;

public abstract class AbstractScriptTest
{
    private final static ModuleKey MYMODULE_KEY = ModuleKey.from( "mymodule-1.0.0" );

    private final SimpleScriptEnvironment environment;

    public AbstractScriptTest()
    {
        this.environment = new SimpleScriptEnvironment();

        final ResourceUrlRegistry urlRegistry = ResourceUrlTestHelper.mockModuleScheme();
        urlRegistry.modulesClassLoader( getClass().getClassLoader() );
    }

    protected final void addLibrary( final ScriptLibrary library )
    {
        this.environment.addLibrary( library );
    }

    protected final void runTestScript( final String name )
    {
        final RhinoScriptRunnerFactory runnerFactory = new RhinoScriptRunnerFactory();
        runnerFactory.setEnvironment( this.environment );
        final ScriptRunner runner = runnerFactory.newRunner();
        runner.variable( "test", new ScriptTestHelper() );

        runner.source( ResourceKey.from( MYMODULE_KEY, name ) );
        runner.execute();
    }
}
