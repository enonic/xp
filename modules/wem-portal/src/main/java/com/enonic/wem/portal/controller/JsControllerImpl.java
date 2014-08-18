package com.enonic.wem.portal.controller;

import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.portal.postprocess.PostProcessor;
import com.enonic.wem.portal.script.lib.ContextScriptBean;
import com.enonic.wem.script.ScriptRunner;

final class JsControllerImpl
    implements JsController
{
    private final ScriptRunner runner;

    private ResourceKey scriptDir;

    private JsContext context;

    private PostProcessor postProcessor;

    public JsControllerImpl( final ScriptRunner runner )
    {
        this.runner = runner;
    }

    @Override
    public JsController scriptDir( final ResourceKey dir )
    {
        this.scriptDir = dir;
        return this;
    }

    @Override
    public JsController context( final JsContext context )
    {
        this.context = context;
        this.runner.property( "portal", this.context );
        return this;
    }

    public JsController postProcessor( final PostProcessor postProcessor )
    {
        this.postProcessor = postProcessor;
        return this;
    }

    private ResourceKey findScript( final String method )
    {
        final ResourceKey key = this.scriptDir.resolve( method.toLowerCase() + ".js" );
        final Resource resource = Resource.from( key );

        if ( resource.exists() )
        {
            return key;
        }

        return null;
    }

    @Override
    public void execute()
    {
        final String method = this.context.getRequest().getMethod();
        final ResourceKey script = findScript( method );

        if ( script == null )
        {
            methodNotAllowed();
            return;
        }

        doExecute( script );
    }

    private void doExecute( final ResourceKey script )
    {
        final ContextScriptBean scriptBean = new ContextScriptBean();
        scriptBean.setModule( script.getModule() );
        scriptBean.setJsContext( this.context );
        scriptBean.install();

        try
        {
            this.runner.source( script );
            this.runner.execute();

            this.postProcessor.processResponse( this.context );
        }
        finally
        {
            ContextScriptBean.remove();
        }
    }

    private void methodNotAllowed()
    {
        this.context.getResponse().setStatus( JsHttpResponse.STATUS_METHOD_NOT_ALLOWED );
    }
}
