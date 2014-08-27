package com.enonic.wem.script;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceUrlRegistry;
import com.enonic.wem.api.resource.ResourceUrlTestHelper;
import com.enonic.wem.script.internal.RhinoScriptRunnerFactory;

public abstract class AbstractScriptTest
{
    private final static ModuleKey MYLIBRARY_KEY = ModuleKey.from( "mylibrary-1.0.0" );

    private final static ModuleKey MYMODULE_KEY = ModuleKey.from( "mymodule-1.0.0" );

    private final SimpleScriptEnvironment environment;

    public AbstractScriptTest()
    {
        this.environment = new SimpleScriptEnvironment();

        final ResourceUrlRegistry urlRegistry = ResourceUrlTestHelper.mockModuleScheme();
        urlRegistry.modulesClassLoader( getClass().getClassLoader() );
    }

    protected final void addContributor( final ScriptContributor contributor )
    {
        this.environment.addContributor( MYLIBRARY_KEY, contributor );
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
