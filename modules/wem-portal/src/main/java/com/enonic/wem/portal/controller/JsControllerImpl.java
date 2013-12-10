package com.enonic.wem.portal.controller;

import java.nio.file.Path;
import java.util.Set;

import javax.ws.rs.HttpMethod;

import com.google.common.collect.Sets;

import com.enonic.wem.portal.script.runner.ScriptRunner;
import com.enonic.wem.portal.script.runner.ScriptRunnerFactory;

final class JsControllerImpl
    implements JsController
{
    private final static String[] ALL_METHODS = {HttpMethod.GET, HttpMethod.POST};

    private final ScriptRunnerFactory factory;

    private final Path path;

    public JsControllerImpl( final ScriptRunnerFactory factory, final Path path )
    {
        this.factory = factory;
        this.path = path;
    }

    @Override
    public Set<String> getMethods()
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

    private Path findScript( final String method )
    {
        return this.path.resolve( method.toLowerCase() + ".js" );
    }

    private boolean hasScript( final String method )
    {
        return fileExists( findScript( method ) );
    }

    private boolean fileExists( final Path path )
    {
        return path.toFile().isFile();
    }

    @Override
    public boolean execute( final JsContext context )
    {
        final Path script = findScript( context.getRequest().getMethod() );
        if ( fileExists( script ) )
        {
            doExecute( context, script );
            return true;
        }

        return false;
    }

    private void doExecute( final JsContext context, final Path script )
    {
        context.setResponse( new JsHttpResponse() );

        final ScriptRunner runner = this.factory.newRunner();
        runner.file( script );
        runner.object( "context", context );
        runner.object( "request", context.getRequest() );
        runner.object( "response", context.getResponse() );

        runner.execute();
    }
}
