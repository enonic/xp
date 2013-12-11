package com.enonic.wem.portal.controller;

import java.util.Set;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.Response;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.Sets;

import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ResourcePath;
import com.enonic.wem.portal.exception.PortalWebException;
import com.enonic.wem.portal.script.loader.ScriptLoader;
import com.enonic.wem.portal.script.loader.ScriptSource;
import com.enonic.wem.portal.script.runner.ScriptRunner;
import com.enonic.wem.portal.script.runner.ScriptRunnerFactory;

final class JsControllerImpl
    implements JsController
{
    private final static String[] ALL_METHODS = {HttpMethod.GET, HttpMethod.POST};

    private final ScriptRunnerFactory factory;

    private final ScriptLoader loader;

    private ModuleResourceKey scriptDir;

    private JsContext context;

    public JsControllerImpl( final ScriptRunnerFactory factory, final ScriptLoader loader )
    {
        this.factory = factory;
        this.loader = loader;
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

    private Optional<ScriptSource> findScript( final String method )
    {
        final ResourcePath path = this.scriptDir.getPath().resolve( method.toLowerCase() + ".js" );
        final ModuleResourceKey key = new ModuleResourceKey( this.scriptDir.getModuleKey(), path );
        return this.loader.loadFromModule( key );
    }

    private boolean hasScript( final String method )
    {
        return findScript( method ) != null;
    }

    @Override
    public Response execute()
    {
        final String method = this.context.getRequest().getMethod();
        final Optional<ScriptSource> script = findScript( method );
        if ( script.isPresent() )
        {
            return doExecute( script.get() );
        }

        if ( method.equals( HttpMethod.OPTIONS ) )
        {
            return executeOptions();
        }

        throw PortalWebException.methodNotAllowed().build();
    }

    private Response doExecute( final ScriptSource script )
    {
        final JsHttpResponse response = new JsHttpResponse();
        this.context.setResponse( response );

        final ScriptRunner runner = this.factory.newRunner();
        runner.source( script );
        runner.property( "context", this.context );
        runner.property( "request", this.context.getRequest() );
        runner.property( "response", this.context.getResponse() );

        runner.execute();

        return new JsHttpResponseSerializer( response ).serialize();
    }

    private Response executeOptions()
    {
        final String allow = Joiner.on( ", " ).join( findMethods() );
        return Response.noContent().header( "Allow", allow ).build();
    }
}
