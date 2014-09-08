package com.enonic.wem.script.internal;

import com.enonic.wem.guice.GuiceActivator;
import com.enonic.wem.script.ScriptRunnerFactory;
import com.enonic.wem.script.ScriptService;
import com.enonic.wem.script.internal.v2.ScriptServiceImpl;

public final class ScriptActivator
    extends GuiceActivator
{
    @Override
    protected void configure()
    {
        bind( ScriptEnvironment.class ).to( ScriptEnvironmentImpl.class );
        bind( ScriptRunnerFactory.class ).to( RhinoScriptRunnerFactory.class );
        bind( ScriptService.class ).to( ScriptServiceImpl.class );

        service( ScriptRunnerFactory.class ).export();
        service( ScriptService.class ).export();
    }
}
