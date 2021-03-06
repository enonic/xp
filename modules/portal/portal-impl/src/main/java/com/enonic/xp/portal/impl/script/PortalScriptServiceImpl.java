package com.enonic.xp.portal.impl.script;

import java.util.concurrent.CompletableFuture;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.runtime.ScriptRuntime;
import com.enonic.xp.script.runtime.ScriptRuntimeFactory;
import com.enonic.xp.script.runtime.ScriptSettings;

@Component(immediate = true)
public final class PortalScriptServiceImpl
    implements PortalScriptService
{
    private final ScriptRuntimeFactory scriptRuntimeFactory;

    private ScriptRuntime scriptRuntime;

    @Activate
    public PortalScriptServiceImpl( @Reference final ScriptRuntimeFactory scriptRuntimeFactory )
    {
        this.scriptRuntimeFactory = scriptRuntimeFactory;
    }

    @Activate
    public void initialize()
    {
        final ScriptSettings settings = ScriptSettings.create().
            binding( Context.class, ContextAccessor::current ).
            binding( PortalRequest.class, PortalRequestAccessor::get ).
            build();

        this.scriptRuntime = this.scriptRuntimeFactory.create( settings );
    }

    @Deactivate
    public void destroy()
    {
        this.scriptRuntimeFactory.dispose( this.scriptRuntime );
    }

    @Override
    public boolean hasScript( final ResourceKey script )
    {
        return this.scriptRuntime.hasScript( script );
    }

    @Override
    public ScriptExports execute( final ResourceKey script )
    {
        return this.scriptRuntime.execute( script );
    }

    @Override
    public CompletableFuture<ScriptExports> executeAsync( final ResourceKey script )
    {
        return this.scriptRuntime.executeAsync( script );
    }

    @Override
    public ScriptValue toScriptValue( final ResourceKey script, final Object value )
    {
        return this.scriptRuntime.toScriptValue( script, value );
    }

    @Override
    public Object toNativeObject( final ResourceKey script, final Object value )
    {
        return this.scriptRuntime.toNativeObject( script, value );
    }
}
