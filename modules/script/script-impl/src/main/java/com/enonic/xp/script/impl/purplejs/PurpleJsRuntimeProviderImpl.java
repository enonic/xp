package com.enonic.xp.script.impl.purplejs;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.script.impl.ScriptRuntimeProvider;
import com.enonic.xp.script.runtime.ScriptRuntime;
import com.enonic.xp.script.runtime.ScriptRuntimeFactory;
import com.enonic.xp.script.runtime.ScriptSettings;

@Component(immediate = true, property = "provider=purpleJs", service = {ScriptRuntimeProvider.class})
public final class PurpleJsRuntimeProviderImpl
    implements ScriptRuntimeProvider, ScriptRuntimeFactory
{
    @Override
    public ScriptRuntime create( final ScriptSettings settings )
    {
        throw new IllegalArgumentException( "Not implemented" );
    }

    @Override
    public void dispose( final ScriptRuntime runtime )
    {
    }

    @Override
    public void invalidate( final ApplicationKey key )
    {
    }
}
