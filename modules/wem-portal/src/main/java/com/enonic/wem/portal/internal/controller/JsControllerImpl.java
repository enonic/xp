package com.enonic.wem.portal.internal.controller;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.portal.PortalContextAccessor;
import com.enonic.wem.portal.internal.postprocess.PostProcessor;
import com.enonic.wem.script.ScriptExports;
import com.enonic.wem.script.ScriptService;

final class JsControllerImpl
    implements JsController
{
    private final ScriptService scriptService;

    private ResourceKey scriptDir;

    private JsContext context;

    private PostProcessor postProcessor;

    public JsControllerImpl( final ScriptService scriptService )
    {
        this.scriptService = scriptService;
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
        return this;
    }

    public JsController postProcessor( final PostProcessor postProcessor )
    {
        this.postProcessor = postProcessor;
        return this;
    }

    @Override
    public void execute()
    {
        PortalContextAccessor.set( this.context );

        try
        {
            doExecute();
            this.postProcessor.processResponse( this.context );
        }
        finally
        {
            PortalContextAccessor.remove();
        }
    }

    private void doExecute()
    {
        final ResourceKey script = this.scriptDir.resolve( "controller.js" );
        final ScriptExports exports = this.scriptService.execute( script );

        final String method = this.context.getRequest().getMethod();
        final String methodName = method.toLowerCase();

        if ( !exports.hasProperty( methodName ) )
        {
            methodNotAllowed();
            return;
        }

        exports.executeMethod( methodName, this.context );
    }

    private void methodNotAllowed()
    {
        this.context.getResponse().setStatus( JsHttpResponse.STATUS_METHOD_NOT_ALLOWED );
    }
}
