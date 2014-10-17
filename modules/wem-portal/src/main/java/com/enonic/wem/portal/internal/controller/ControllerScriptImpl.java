package com.enonic.wem.portal.internal.controller;

import com.enonic.wem.portal.PortalContext;
import com.enonic.wem.portal.PortalContextAccessor;
import com.enonic.wem.portal.internal.postprocess.PostProcessor;
import com.enonic.wem.script.ScriptExports;

final class ControllerScriptImpl
    implements ControllerScript
{
    private final ScriptExports scriptExports;

    private final PostProcessor postProcessor;

    public ControllerScriptImpl( final ScriptExports scriptExports, final PostProcessor postProcessor )
    {
        this.scriptExports = scriptExports;
        this.postProcessor = postProcessor;
    }

    @Override
    public void execute( final PortalContext context )
    {
        PortalContextAccessor.set( context );

        try
        {
            doExecute( context );
            this.postProcessor.processResponse( context );
        }
        finally
        {
            PortalContextAccessor.remove();
        }
    }

    private void doExecute( final PortalContext context )
    {
        final String method = context.getRequest().getMethod();
        final String methodName = method.toLowerCase();

        if ( !this.scriptExports.hasProperty( methodName ) )
        {
            methodNotAllowed( context );
            return;
        }

        this.scriptExports.executeMethod( methodName, context );
    }

    private void methodNotAllowed( final PortalContext context )
    {
        context.getResponse().setStatus( PortalResponseImpl.STATUS_METHOD_NOT_ALLOWED );
    }
}
