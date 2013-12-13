package com.enonic.wem.portal.controller;

import java.util.Set;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.Response;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ResourcePath;
import com.enonic.wem.portal.exception.PortalWebException;
import com.enonic.wem.portal.script.loader.ScriptSource;
import com.enonic.wem.portal.script.runner.ScriptRunner;

final class JsControllerImpl
    implements JsController
{
    private final static String[] ALL_METHODS = {HttpMethod.GET, HttpMethod.POST};

    private final ScriptRunner runner;

    private ModuleResourceKey scriptDir;

    private JsContext context;

    public JsControllerImpl( final ScriptRunner runner )
    {
        this.runner = runner;
    }

    @Override
    public JsController scriptDir( final ModuleResourceKey dir )
    {
        this.scriptDir = dir;
        return this;
    }

    @Override
    public JsController context( final JsContext context )
    {
        this.context = context;
        this.runner.property( "__context", this.context );
        return this;
    }

    private Set<String> findMethods()
    {
        final Set<String> set = Sets.newHashSet();
        for ( final String method : ALL_METHODS )
        {
            if ( hasScript( method ) )
            {
                set.add( method );
            }
        }

        set.add( HttpMethod.OPTIONS );
        return set;
    }

    private ScriptSource findScript( final String method )
    {
        final ResourcePath path = this.scriptDir.getPath().resolve( method.toLowerCase() + ".js" );
        final ModuleResourceKey key = new ModuleResourceKey( this.scriptDir.getModuleKey(), path );
        return this.runner.getLoader().loadFromModule( key );
    }

    private boolean hasScript( final String method )
    {
        return findScript( method ) != null;
    }

    @Override
    public Response execute()
    {
        final String method = this.context.getRequest().getMethod();
        final ScriptSource script = findScript( method );
        if ( script != null )
        {
            return doExecute( script );
        }

        if ( method.equals( HttpMethod.OPTIONS ) )
        {
            return executeOptions();
        }

        throw PortalWebException.methodNotAllowed().build();
    }

    private Response doExecute( final ScriptSource script )
    {
        this.runner.source( script );
        this.runner.execute();
        return new JsHttpResponseSerializer( this.context.getResponse() ).serialize();
    }

    private Response executeOptions()
    {
        final String allow = Joiner.on( ", " ).join( findMethods() );
        return Response.noContent().header( "Allow", allow ).build();
    }
}
