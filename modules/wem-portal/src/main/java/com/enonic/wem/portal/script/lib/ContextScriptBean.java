package com.enonic.wem.portal.script.lib;

import java.net.URL;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceNotFoundException;
import com.enonic.wem.api.resource.ResourceUrlResolver;
import com.enonic.wem.portal.controller.JsContext;

public final class ContextScriptBean
{
    private final static ThreadLocal<ContextScriptBean> CURRENT = new ThreadLocal<>();

    private ModuleKey module;

    private JsContext jsContext;

    public ModuleKey getModule()
    {
        return module;
    }

    public void setModule( final ModuleKey module )
    {
        this.module = module;
    }

    public JsContext getJsContext()
    {
        return jsContext;
    }

    public void setJsContext( final JsContext jsContext )
    {
        this.jsContext = jsContext;
    }

    public URL resolveFile( final String name )
    {
        final ResourceKey key = ResourceKey.from( this.module, name );
        final URL resourceUrl = ResourceUrlResolver.resolve( key );

        if ( exists( resourceUrl ) )
        {
            return resourceUrl;
        }

        throw new ResourceNotFoundException( key );
    }

    private boolean exists( final URL url )
    {
        try
        {
            url.openStream();
            return true;
        }
        catch ( final Exception e )
        {
            return false;
        }
    }

    public void install()
    {
        CURRENT.set( this );
    }

    public static void remove()
    {
        CURRENT.remove();
    }

    public static ContextScriptBean get()
    {
        return CURRENT.get();
    }
}
