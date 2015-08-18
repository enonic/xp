package com.enonic.xp.script.impl;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.script.runtime.ScriptRuntime;
import com.enonic.xp.script.runtime.ScriptRuntimeFactory;
import com.enonic.xp.script.runtime.ScriptSettings;

@Component(immediate = true, service = ScriptRuntimeFactory.class)
public final class ScriptRuntimeFactoryImpl
    implements ScriptRuntimeFactory
{
    @Override
    public ScriptRuntime create( final ScriptSettings settings )
    {
        return null;
    }

    @Override
    public void dispose( final ScriptRuntime runtime )
    {

    }
}
