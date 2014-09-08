package com.enonic.wem.script.internal;

import com.enonic.wem.guice.GuiceActivator;
import com.enonic.wem.script.ScriptRunnerFactory;

public final class ScriptActivator
    extends GuiceActivator
{
    @Override
    protected void configure()
    {
        bind( ScriptEnvironment.class ).to( ScriptEnvironmentImpl.class );
        bind( ScriptRunnerFactory.class ).to( RhinoScriptRunnerFactory.class );

        service( ScriptRunnerFactory.class ).export();
    }
}
