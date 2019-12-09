package com.enonic.xp.script.impl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationInvalidationLevel;
import com.enonic.xp.app.ApplicationInvalidator;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.script.runtime.ScriptRuntime;
import com.enonic.xp.script.runtime.ScriptRuntimeFactory;
import com.enonic.xp.script.runtime.ScriptSettings;

@Component(immediate = true, service = {ScriptRuntimeFactory.class, ApplicationInvalidator.class})
public final class ScriptRuntimeFactoryDelegate
    implements ScriptRuntimeFactory, ApplicationInvalidator
{
    private final static String SWITCH_PROP = "xp.usePurpleJs";

    private ScriptRuntimeProvider standardProvider;

    private ScriptRuntimeProvider purpleJsProvider;

    private ScriptRuntimeProvider provider;

    @Activate
    public void activate()
    {
        final boolean usePurpleJs = "true".equalsIgnoreCase( System.getProperty( SWITCH_PROP, "false" ) );
        this.provider = usePurpleJs ? this.purpleJsProvider : this.standardProvider;
    }

    @Override
    public ScriptRuntime create( final ScriptSettings settings )
    {
        return this.provider.create( settings );
    }

    @Override
    public void dispose( final ScriptRuntime runtime )
    {
        this.provider.dispose( runtime );
    }

    @Override
    @Deprecated
    public void invalidate( final ApplicationKey key )
    {
        invalidate( key, ApplicationInvalidationLevel.FULL );
    }

    @Override
    public void invalidate( final ApplicationKey key, final ApplicationInvalidationLevel level )
    {
        this.provider.invalidate( key );
    }

    @Reference(target = "(provider=standard)")
    public void setStandardProvider( final ScriptRuntimeProvider provider )
    {
        this.standardProvider = provider;
    }

    @Reference(target = "(provider=purpleJs)")
    public void setPurpleJsProvider( final ScriptRuntimeProvider provider )
    {
        this.purpleJsProvider = provider;
    }
}
