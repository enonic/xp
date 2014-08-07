package com.enonic.wem.portal.script.lib;

import java.net.URL;

import org.mozilla.javascript.Context;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.resource.ModuleResourceKey;
import com.enonic.wem.api.resource.ResourceUrlResolver;
import com.enonic.wem.portal.controller.JsContext;
import com.enonic.wem.portal.script.helper.ScriptHelper;

public final class ContextScriptBean
{
    private final static String NAME = ContextScriptBean.class.getName();

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
        final ModuleResourceKey key = ModuleResourceKey.from( this.module, name );
        final URL resourceUrl = ResourceUrlResolver.resolve( key );

        final boolean isFile = !resourceUrl.getPath().endsWith( "/" );
        if ( isFile )
        {
            return resourceUrl;
        }

        throw ScriptHelper.error( "Failed find file [{0}] from module.", key.toString() );
    }

    public void install( final Context context )
    {
        context.putThreadLocal( NAME, this );
    }

    public static void remove( final Context context )
    {
        context.removeThreadLocal( NAME );
    }

    public static ContextScriptBean get()
    {
        return (ContextScriptBean) Context.getCurrentContext().getThreadLocal( NAME );
    }
}
